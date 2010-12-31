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

package com.kreative.prc;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import com.kreative.ksfl.KSFLUtilities;

/**
 * The <code>PalmResourceFile</code> class represents a Palm OS resource database in a <code>RandomAccessFile</code>.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class PalmResourceFile extends PalmResourceProvider {
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
	
	private int[] locate(int type, short id) {
		// 0 - number of resources
		// 1 - offset to header
		// 2 - length of header
		// 3 - offset to data
		// 4 - length of data
		try {
			raf.seek(0x4C);
			int n = raf.readShort();
			for (int i=0, h=0x4E; i<n; i++, h+=10) {
				int t = raf.readInt();
				short d = raf.readShort();
				int o = raf.readInt();
				if (t == type && d == id) {
					int o2;
					if (i == n-1) {
						o2 = (int)raf.length();
					} else {
						raf.skipBytes(6);
						o2 = raf.readInt();
					}
					return new int[]{ n, h, 10, o, o2-o };
				}
			}
		} catch (IOException e) {}
		return null;
	}
	
	private int[] locateend() {
		try {
			raf.seek(0x4C);
			int n = raf.readShort();
			return new int[] { n, 0x4E+10*n, 10, (int)raf.length(), 0 };
		} catch (IOException e) {}
		return null;
	}
	
	private static final int INSERTED_OBJECT_RECORD = 2;
	private static final int INSERTED_DATA = 4;
	private static final int REMOVED_OBJECT_RECORD = 2;
	private static final int REMOVED_DATA = 4;
	
	private void cut(int[] loc, int offset, int length, int what, int type, short id) throws IOException {
		KSFLUtilities.cut(raf, offset, length);
		//update location
		if (loc != null) {
			if (what == REMOVED_OBJECT_RECORD) loc[0]--;
			if (loc[1] > offset) loc[1] -= length;
			if (loc[3] > offset) loc[3] -= length;
		}
		//update header
		raf.seek(0x4C);
		short n = raf.readShort();
		if (what == REMOVED_OBJECT_RECORD) {
			raf.seek(0x4C);
			raf.writeShort(--n);
		}
		//update records
		for (int i=0, h=0x4E; i<n; i++, h+=10) {
			raf.seek(h+6);
			int o = raf.readInt();
			if (o > offset) {
				raf.seek(h+6);
				raf.writeInt(o-length);
			}
		}
	}
	
	private void paste(int[] loc, int offset, byte[] stuff, int what, int type, short id) throws IOException {
		boolean found = false;
		KSFLUtilities.paste(raf, offset, stuff);
		//update location
		if (loc != null) {
			if (what == INSERTED_OBJECT_RECORD) loc[0]++;
			if ((loc[1] > offset) || (loc[1] == offset && what != INSERTED_OBJECT_RECORD)) loc[1] += stuff.length;
			if ((loc[3] > offset) || (loc[3] == offset && what != INSERTED_DATA)) loc[3] += stuff.length;
		}
		//update header
		raf.seek(0x4C);
		short n = raf.readShort();
		if (what == INSERTED_OBJECT_RECORD) {
			raf.seek(0x4C);
			raf.writeShort(++n);
		}
		//update records
		for (int i=0, h=0x4E; i<n; i++, h+=10) {
			int t = raf.readInt();
			short d = raf.readShort();
			int o = raf.readInt();
			if ((o > offset) || (o == offset && !(what == INSERTED_DATA && !found))) {
				raf.seek(h+6);
				raf.writeInt(o+stuff.length);
			}
			found = found || (type == t && id == d);
		}
	}
	
	/**
	 * Creates a <code>PalmResourceFile</code> around a file.
	 * The file will not be modified until the resource structure is modified.
	 * If the file does not exist or is empty, a new resource structure is created.
	 * @param f the file object.
	 * @param mode the access mode, as described by <code>RandomAccessFile(File, String)</code>.
	 * @param create <code>CREATE_ALWAYS</code> if a new resource structure should be created, <code>CREATE_IF_EMPTY</code> if a new resource should be created if the file is empty, <code>CREATE_NEVER</code> if the file should not be modified.
	 * @throws IOException if an I/O error occurs.
	 */
	public PalmResourceFile(File f, String mode, int create) throws IOException {
		raf = new RandomAccessFile(f, mode);
		readOnly = (mode.equalsIgnoreCase("r"));
		if ((create == CREATE_ALWAYS) || ((create == CREATE_IF_EMPTY) && (raf.length() == 0))) {
			raf.setLength(0);
			raf.seek(0);
			raf.write(new byte[32]);
			raf.writeShort(1);
			raf.writeShort(1);
			raf.writeInt(0);
			raf.writeInt(0);
			raf.writeInt(0);
			raf.writeInt(0);
			raf.writeInt(0);
			raf.writeInt(0);
			raf.writeInt(0x6170706C);
			raf.writeInt(0);
			raf.writeInt(0);
			raf.writeInt(0);
			raf.writeShort(0);
			raf.writeShort(0);
		}
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
	public synchronized byte[] getPRCHeader() {
		byte[] h = new byte[0x4E];
		try {
			raf.seek(0);
			raf.read(h);
		} catch (IOException ioe) {}
		return h;
	}
	
	@Override
	public synchronized void setPRCHeader(byte[] header) {
		if (header.length > 0x4C) header = KSFLUtilities.copy(header, 0x00, 0x4C);
		try {
			raf.seek(0);
			raf.write(header);
		} catch (IOException ioe) {}
	}
	
	@Override
	public synchronized boolean add(PalmResource r) throws PalmResourceAlreadyExistsException {
		try {
			if (locate(r.type,r.id) != null) throw new PalmResourceAlreadyExistsException();
			int[] loc = locateend();
			byte[] rcd = new byte[10];
			KSFLUtilities.putInt(rcd, 0, r.type);
			KSFLUtilities.putShort(rcd, 4, r.id);
			KSFLUtilities.putInt(rcd, 6, loc[3]);
			paste(loc, loc[1], rcd, INSERTED_OBJECT_RECORD, r.type, r.id);
			paste(loc, loc[3], r.data, INSERTED_DATA, r.type, r.id);
			return true;
		} catch (IOException ioe) {}
		return false;
	}
	
	@Override
	public synchronized boolean contains(int type, short id) {
		return (locate(type,id) != null);
	}
	
	@Override
	public synchronized PalmResource get(int type, short id) {
		try {
			int[] l = locate(type,id);
			if (l != null) {
				raf.seek(l[3]); byte[] stuff = new byte[l[4]]; raf.read(stuff);
				return new PalmResource(type, id, stuff);
			}
		} catch (IOException ioe) {}
		return null;
	}
	
	@Override
	public synchronized PalmResource getAttributes(int type, short id) {
		int[] l = locate(type,id);
		if (l != null) {
			return new PalmResource(type, id, new byte[0]);
		}
		return null;
	}
	
	@Override
	public synchronized byte[] getData(int type, short id) {
		try {
			int[] l = locate(type,id);
			if (l != null) {
				raf.seek(l[3]); byte[] stuff = new byte[l[4]]; raf.read(stuff);
				return stuff;
			}
		} catch (IOException ioe) {}
		return null;
	}
	
	@Override
	public synchronized boolean remove(int type, short id) {
		try {
			int[] loc = locate(type,id);
			if (loc != null) {
				cut(loc, loc[3], loc[4], REMOVED_DATA, type, id);
				cut(loc, loc[1], loc[2], REMOVED_OBJECT_RECORD, type, id);
				return true;
			}
		} catch (IOException ioe) {}
		return false;
	}
	
	@Override
	public synchronized boolean set(int type, short id, PalmResource r) throws PalmResourceAlreadyExistsException {
		if (!contains(type, id)) return false;
		if (r.data == null) r.data = new byte[0];
		return setAttributes(type, id, r) && setData(r.type, r.id, r.data);
	}

	@Override
	public synchronized boolean setAttributes(int type, short id, PalmResource r) throws PalmResourceAlreadyExistsException {
		try {
			int[] loc = locate(type,id);
			if (loc != null) {
				int[] loce = locate(r.type,r.id);
				if ((loce != null) && (
						(loce[0] != loc[0]) ||
						(loce[1] != loc[1]) ||
						(loce[2] != loc[2]) ||
						(loce[3] != loc[3]) ||
						(loce[4] != loc[4])
				)) {
					throw new PalmResourceAlreadyExistsException();
				} else {
					raf.seek(loc[1]);
					raf.writeInt(r.type);
					raf.writeShort(r.id);
					return true;
				}
			}
		} catch (IOException ioe) {}
		return false;
	}
	
	@Override
	public synchronized boolean setData(int type, short id, byte[] data) {
		try {
			int[] loc = locate(type,id);
			if (loc != null) {
				cut(loc, loc[3], loc[4], REMOVED_DATA, type, id);
				paste(loc, loc[3], data, INSERTED_DATA, type, id);
				return true;
			}
		} catch (IOException ioe) {}
		return false;
	}
	
	@Override
	public synchronized int getTypeCount() {
		int cnt = 0;
		try {
			ArrayList<Integer> a = new ArrayList<Integer>();
			raf.seek(0x4C);
			int n = raf.readShort();
			for (int i=0, h=0x4E; i<n; i++, h+=10) {
				int t = raf.readInt();
				raf.skipBytes(6);
				if (!a.contains(t)) {
					cnt++;
					a.add(t);
				}
			}
		} catch (IOException ioe) {}
		return cnt;
	}
	@Override
	public synchronized int getType(int index) {
		try {
			int idx = 0;
			ArrayList<Integer> a = new ArrayList<Integer>();
			raf.seek(0x4C);
			int n = raf.readShort();
			for (int i=0, h=0x4E; i<n; i++, h+=10) {
				int t = raf.readInt();
				raf.skipBytes(6);
				if (!a.contains(t)) {
					if (idx == index) return t;
					else {
						idx++;
						a.add(t);
					}
				}
			}
		} catch (IOException ioe) {}
		return 0;
	}
	@Override
	public synchronized int[] getTypes() {
		ArrayList<Integer> a = new ArrayList<Integer>();
		try {
			raf.seek(0x4C);
			int n = raf.readShort();
			for (int i=0, h=0x4E; i<n; i++, h+=10) {
				int t = raf.readInt();
				raf.skipBytes(6);
				if (!a.contains(t)) a.add(t);
			}
		} catch (IOException ioe) {}
		int[] s = new int[a.size()];
		Iterator<Integer> i = a.iterator();
		for (int j=0; j<s.length && i.hasNext(); j++) s[j] = i.next();
		return s;
	}
	
	@Override
	public synchronized int getResourceCount(int type) {
		int cnt = 0;
		try {
			raf.seek(0x4C);
			int n = raf.readShort();
			for (int i=0, h=0x4E; i<n; i++, h+=10) {
				int t = raf.readInt();
				raf.skipBytes(6);
				if (t == type) cnt++;
			}
		} catch (IOException ioe) {}
		return cnt;
	}
	@Override
	public synchronized short getID(int type, int index) {
		try {
			int idx = 0;
			raf.seek(0x4C);
			int n = raf.readShort();
			for (int i=0, h=0x4E; i<n; i++, h+=10) {
				int t = raf.readInt();
				short d = raf.readShort();
				raf.skipBytes(4);
				if (t == type) {
					if (idx == index) return d;
					else idx++;
				}
			}
		} catch (IOException ioe) {}
		return 0;
	}
	@Override
	public synchronized short[] getIDs(int type) {
		ArrayList<Short> a = new ArrayList<Short>();
		try {
			raf.seek(0x4C);
			int n = raf.readShort();
			for (int i=0, h=0x4E; i<n; i++, h+=10) {
				int t = raf.readInt();
				short d = raf.readShort();
				raf.skipBytes(4);
				if (t == type) a.add(d);
			}
		} catch (IOException ioe) {}
		short[] s = new short[a.size()];
		Iterator<Short> i = a.iterator();
		for (int j=0; j<s.length && i.hasNext(); j++) s[j] = i.next();
		return s;
	}
	
	@Override
	public synchronized short getNextAvailableID(int type, short start) {
		try {
			ArrayList<Short> a = new ArrayList<Short>();
			raf.seek(0x4C);
			int n = raf.readShort();
			for (int i=0, h=0x4E; i<n; i++, h+=10) {
				int t = raf.readInt();
				short d = raf.readShort();
				raf.skipBytes(4);
				if (t == type) a.add(d);
			}
			while (a.contains(start)) start++;
		} catch (IOException ioe) {}
		return start;
	}
}
