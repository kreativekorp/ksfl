/*
 * Copyright &copy; 2007-2011 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.rsrc;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.kreative.ksfl.KSFLUtilities;

/**
 * The <code>BerkeleyResourceFile</code> class represents a resource fork as defined
 * by the Berkeley Systems modification of the Mac OS Resource Manager in a <code>RandomAccessFile</code>.
 * The Resource Manager was originally designed and implemented by Bruce Horn
 * for use in the original Macintosh Operating System. It remains today as a
 * feature unique to the Mac OS. Other operating systems have the concept of
 * resources, but the Mac OS implementation remains unique.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software, modified James Wallace
 */
public class BerkeleyResourceFile extends MacResourceProvider {
	/**
	 * Never modify the file upon opening. Throw an exception if the file is empty. 
	 */
	public static final int CREATE_NEVER = 0;
	/**
	 * Only create a new resource structure if the file is empty.
	 */
	public static final int CREATE_IF_EMPTY = 1;
	/**
	 * Create a new resource structure, overwriting the existing file contents.
	 */
	public static final int CREATE_ALWAYS = 2;
	
	private RandomAccessFile raf;
	private boolean readOnly = false;
	private int resMap, resData;
	private int resMapLen, resDataLen;
	private String textEncoding = "MACROMAN";
	private Hashtable<Integer, Long> typeoffs = new Hashtable<Integer,Long>();
	private String gps(int b) {
		try {
			raf.seek(b);
			int l = raf.readByte() & 0xFF;
			byte[] s = new byte[l];
			raf.read(s);
			try {
				return new String(s,textEncoding);
			} catch (java.io.UnsupportedEncodingException uue) {
				return new String(s);
			}
		} catch (IOException e) {
			return "";
		}
	}
	
	private int[] locateType(int type) {
		// 0 - offset to type record
		// 1 - number of items of that type
		// 2 - offset to first data
		try {
			Long seekval = typeoffs.get(type);
			int offset = seekval.intValue();
			raf.seek(seekval);
			int items = raf.readInt();
			return new int[]{offset, items, offset+8};
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	}
	
	private int[] locate(int type, int id) {
		// 0 - offset to type record
		// 1 - number of items of that type
		// 2 - offset to reference list
		// 3 - offset to reference record
		// 4 - offset to name
		// 5 - offset to data
		try {
			int[] t = locateType(type);
			if (t != null) {
				raf.seek(t[0]+4);
				for (int i=0; i<t[1]; i++) {
					int thisid = raf.readInt();
					if (thisid == id) {
						Long nameloc = raf.getFilePointer();
						int n = nameloc.intValue();
						int d = raf.readInt();
						return new int[]{
								t[0]-4, t[1], t[0], t[0],
								(n),
								(d)
						};
					} else {
						raf.skipBytes(8);
					}
				}
			}
		} catch (IOException e) {}
		return null;
	}
	
	/**
	 * Creates a <code>BerkeleyResourceFile</code> around a file.
	 * The file will not be modified until the resource structure is modified.
	 * If the file does not exist or is empty, a new resource structure is created.
	 * @param f the file object.
	 * @param mode the access mode, as described by <code>RandomAccessFile(File, String)</code>.
	 * @param create <code>CREATE_ALWAYS</code> if a new resource structure should be created, <code>CREATE_IF_EMPTY</code> if a new resource should be created if the file is empty, <code>CREATE_NEVER</code> if the file should not be modified.
	 * @throws IOException if an I/O error occurs.
	 */
	public BerkeleyResourceFile(File f, String mode, int create) throws IOException {
		raf = new RandomAccessFile(f, mode);
		readOnly = (mode.equalsIgnoreCase("r"));
		{
			raf.seek(4);//seek past srf1 magic.
			int totalfile = raf.readInt();
			resMapLen = raf.readInt();
			resData = resMapLen +11;
			resMap = 12;
			resDataLen = totalfile - resData;
			raf.seek(resMap);
			getTypes();
		}
	}
	
	/**
	 * Returns the name of the text encoding used for resource names.
	 * Defaults to MACROMAN. If your system does not support this encoding,
	 * you will want to change it, since things will slow to a crawl.
	 * @return the name of the text encoding used for resource names.
	 */
	public synchronized String getTextEncoding() {
		return textEncoding;
	}
	
	/**
	 * Sets the text encoding used for resource names.
	 * Defaults to MACROMAN. If your system does not support this encoding,
	 * you will want to change it, since things will slow to a crawl.
	 * @param encoding the name of the text encoding used for resource names.
	 */
	public synchronized void setTextEncoding(String encoding) {
		textEncoding = encoding;
	}
	
	/**
	 * Returns the <code>RandomAccessFile</code> this is wrapped around.
	 * This should be used for debugging purposes only.
	 * @return the <code>RandomAccessFile</code> this is wrapped around.
	 */
	public RandomAccessFile getRandomAccessFile() {
		return raf;
	}
	
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}
	
	@Override
	public synchronized void flush() {
		// nothing
	}
	
	@Override
	public synchronized void close() {
		try { raf.close(); } catch (Exception e) {}
	}
	
	
	@Override
	public synchronized boolean contains(int type, short id) {
		return (locate(type,id) != null);
	}
	
	@Override
	public synchronized MacResource get(int type, short id) {
		try {
			int[] l = locate(type,id);
			if (l != null) {
				raf.seek(l[0]);
				int t = raf.readInt();
				short i = id;
				raf.seek(l[3]+4);
				byte a = raf.readByte();
				raf.seek(l[4]+4);
				int dl = raf.readInt();
				return new MacResource(
						t, i, a,
						String.valueOf(i),
						(l[5]>0)?KSFLUtilities.copy(raf, l[5], dl):(new byte[0])
				);
			}
		} catch (IOException e) {}
		return null;
	}
	
	@Override
	public synchronized byte[] getData(int type, short id) {
		try {
			int[] l = locate(type,id);
			if (l != null) {
				raf.seek(l[5]);
				int dl = raf.readInt();
				return (l[5]>0)?KSFLUtilities.copy(raf, l[5]+4, dl):(new byte[0]);
			}
		} catch (IOException e) {}
		return null;
	}
	
	@Override
	public synchronized int getTypeCount() {
		return getTypes().length;
	}
	@Override
	public synchronized int getType(int index) {
		int[] types = getTypes();
		return types[index];
	}
	@Override
	public synchronized int[] getTypes() {
		try {
			ArrayList<Integer> types = new ArrayList<Integer>();
			typeoffs = new Hashtable<Integer,Long>();
			raf.seek(resMap);
			int typeval = raf.readInt();
			types.add(typeval);
			typeoffs.put(typeval, raf.getFilePointer());
			
			int nooffirsts = raf.readInt();//we have to know how many to skip to the next name.
			long skip = nooffirsts *12;
			raf.seek(raf.getFilePointer()+skip);
			while (raf.getFilePointer() < resData)
			{
				typeval = raf.readInt();
				types.add(typeval);
				typeoffs.put(typeval, raf.getFilePointer());

				nooffirsts = raf.readInt();//we have to know how many to skip to the next name.
				skip = nooffirsts *12;
				raf.seek(raf.getFilePointer()+skip);
			}
			int[] a = new int[types.size()];
			for (int i =0; i < types.size(); i++)
			{
				a[i]= types.get(i);
			}
			return a;
		} catch (IOException e) {}
		return new int[0];
	}
	
	@Override
	public synchronized int getResourceCount(int type) {
		int[] t = locateType(type);
		if (t != null) return t[1];
		else return 0;
	}
	@Override
	public synchronized short getID(int type, int index) {
		try {
			int[] t = locateType(type);
			if (t != null) {
				raf.seek(t[2]+2+(12*index));
				return raf.readShort();
			}
			else return 0;
		} catch (IOException e) {}
		return 0;
	}
	@Override
	public synchronized short[] getIDs(int type) {
		try {
			int[] t = locateType(type);
			if (t != null) {
				short[] a = new short[t[1]];
				for (int i=0; i<t[1]; i++) {
					raf.seek(t[0]+6+(12*i));
					a[i] = raf.readShort();
				}
				return a;
			}
		} catch (IOException e) {}
		return new short[0];
	}
	@Override
	public synchronized String getName(int type, int index) {
		try {
			int[] t = locateType(type);
			if (t != null) {
				raf.seek(t[2]+12*index+2);
				int n = raf.readInt();
				if (n < 0) return "";
				else return gps(n);
			}
		} catch (IOException e) {}
		return "";
	}
	@Override
	public synchronized String[] getNames(int type) {
		ArrayList<String> a = new ArrayList<String>();
		try {
			int[] t = locateType(type);
			if (t != null) for (int i=0; i<t[1]; i++) {
				raf.seek(t[2]+12*i+2);
				int n = raf.readInt();
				if (n < 0) a.add("");
				else a.add(gps(n));
			}
		} catch (IOException e) {}
		return a.toArray(new String[0]);
	}
	
	@Override
	public synchronized short getNextAvailableID(int type, short start) {
		try {
			ArrayList<Short> a = new ArrayList<Short>();
			int[] t = locateType(type);
			if (t != null) for (int i=0; i<t[1]; i++) {
				raf.seek(t[2]+2+(12*i));
				a.add(raf.readShort());
			}
			short next = start;
			while (a.contains(next)) next++;
			return next;
		} catch (IOException e) {}
		return 0;
	}
	
	@Override
	public synchronized String getNameFromID(int type, short id) {
		int[] l = locate(type,id);
		if (l != null) {
			return (l[4]>0)?gps(l[4]):"";
		}
		return "";
	}

	@Override
	public boolean contains(int type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MacResource get(int type, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MacResource getAttributes(int type, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getData(int type, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(int type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setAttributes(int type, String name, MacResource r)
			throws MacResourceAlreadyExistsException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setData(int type, String name, byte[] data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public short getIDFromName(int type, String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int readInt(int offset) {
		try {
			raf.seek(offset);
			return raf.readInt();
		} catch (IOException e) {
			return 0;
		}
	}

	@Override
	public short getResourceMapAttributes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setResourceMapAttributes(short attr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean add(MacResource r) throws MacResourceAlreadyExistsException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MacResource getAttributes(int type, short id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(int type, short id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean set(int type, short id, MacResource r)
			throws MacResourceAlreadyExistsException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean set(int type, String name, MacResource r)
			throws MacResourceAlreadyExistsException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setAttributes(int type, short id, MacResource r)
			throws MacResourceAlreadyExistsException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setData(int type, short id, byte[] data) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
