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
import com.kreative.ksfl.KSFLUtilities;

/**
 * The <code>MacResourceFile</code> class represents a resource fork as defined
 * by the Mac OS Resource Manager in a <code>RandomAccessFile</code>.
 * The Resource Manager was originally designed and implemented by Bruce Horn
 * for use in the original Macintosh Operating System. It remains today as a
 * feature unique to the Mac OS. Other operating systems have the concept of
 * resources, but the Mac OS implementation remains unique.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class MacResourceFile extends MacResourceProvider {
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
	private int resMap, typeList, nameList, resData;
	private int resMapLen, resDataLen;
	private String textEncoding = "MACROMAN";
	
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
	
	private byte[] gb(String a) {
		try {
			return a.getBytes(textEncoding);
		} catch (java.io.UnsupportedEncodingException uue) {
			return a.getBytes();
		}
	}
	
	private int[] locateType(int type) {
		// 0 - offset to type record
		// 1 - number of items of that type
		// 2 - offset to reference list
		try {
			raf.seek(typeList);
			int m = raf.readShort()+1;
			for (int i=0; i<m; i++) {
				int t = raf.readInt();
				if (t == type) {
					int cnt = raf.readShort()+1;
					int lst = typeList + raf.readShort();
					return new int[]{typeList+2+8*i, cnt, lst};
				} else {
					raf.readInt();
				}
			}
		} catch (IOException e) {}
		return null;
	}
	
	private int[] locate(int type, short id) {
		// 0 - offset to type record
		// 1 - number of items of that type
		// 2 - offset to reference list
		// 3 - offset to reference record
		// 4 - offset to name
		// 5 - offset to data
		try {
			int[] t = locateType(type);
			if (t != null) {
				raf.seek(t[2]);
				for (int i=0; i<t[1]; i++) {
					short thisid = raf.readShort();
					if (thisid == id) {
						int n = raf.readShort();
						int d = raf.readInt() & 0xFFFFFF;
						return new int[]{
								t[0], t[1], t[2], t[2]+12*i,
								((n<0)?0:(nameList+n)),
								((d<0)?0:(resData+d))
						};
					} else {
						raf.skipBytes(10);
					}
				}
			}
		} catch (IOException e) {}
		return null;
	}
	
	private int[] locate(int type, String name) {
		// 0 - offset to type record
		// 1 - number of items of that type
		// 2 - offset to reference list
		// 3 - offset to reference record
		// 4 - offset to name
		// 5 - offset to data
		try {
			int[] t = locateType(type);
			if (t != null) {
				for (int i=0; i<t[1]; i++) {
					raf.seek(t[2]+12*i+2);
					int n = raf.readShort();
					if (n >= 0 && gps(nameList+n).equals(name)) {
						raf.seek(t[2]+12*i+4);
						int d = raf.readInt() & 0xFFFFFF;
						return new int[]{
								t[0], t[1], t[2], t[2]+12*i,
								((n<0)?0:(nameList+n)),
								((d<0)?0:(resData+d))
						};
					}
				}
			}
		} catch (IOException e) {}
		return null;
	}
	
	private static final int INSERTED_TYPE_RECORD = 1;
	private static final int INSERTED_OBJECT_RECORD = 2;
	private static final int INSERTED_NAME = 3;
	private static final int INSERTED_DATA = 4;
	private static final int REMOVED_TYPE_RECORD = 1;
	private static final int REMOVED_OBJECT_RECORD = 2;
	private static final int REMOVED_NAME = 3;
	private static final int REMOVED_DATA = 4;
	
	private void cut(int[] loc, int offset, int length, int what, int type, short id) throws IOException {
		boolean typeListAdjusted=false, nameListAdjusted=false, resDataAdjusted=false;
		KSFLUtilities.cut(raf, offset, length);
		// update offsets
		if (resMap > offset) resMap -= length;
		if (typeList > offset) { typeList -= length; typeListAdjusted = true; }
		if (nameList > offset) { nameList -= length; nameListAdjusted = true; }
		if (resData > offset) { resData -= length; resDataAdjusted = true; }
		if (what == REMOVED_TYPE_RECORD || what == REMOVED_OBJECT_RECORD || what == REMOVED_NAME) resMapLen -= length;
		if (what == REMOVED_DATA) resDataLen -= length;
		// update location
		if (loc != null) {
			if (loc.length > 0 && loc[0] > offset) loc[0] -= length;
			if (loc.length > 1 && what == REMOVED_OBJECT_RECORD) loc[1]--;
			if (loc.length > 2 && loc[2] > offset) loc[2] -= length;
			if (loc.length > 3 && loc[3] > offset) loc[3] -= length;
			if (loc.length > 4 && loc[4] > offset) loc[4] -= length;
			if (loc.length > 5 && loc[5] > offset) loc[5] -= length;
		}
		// update header
		raf.seek(0);
		raf.writeInt(resData);
		raf.writeInt(resMap);
		raf.writeInt(resDataLen);
		raf.writeInt(resMapLen);
		// update resource map
		raf.seek(resMap);
		raf.writeInt(resData);
		raf.writeInt(resMap);
		raf.writeInt(resDataLen);
		raf.writeInt(resMapLen);
		raf.seek(resMap+24);
		raf.writeShort(typeList - resMap);
		raf.writeShort(nameList - resMap);
		// update type list
		raf.seek(typeList);
		int numtypes = raf.readShort()+1;
		if (what == REMOVED_TYPE_RECORD) {
			numtypes--;
			raf.seek(typeList);
			raf.writeShort(numtypes-1);
		}
		for (int i=0; i<numtypes; i++) {
			raf.seek(typeList+2+8*i);
			int thistype = raf.readInt();
			int numrefs = raf.readShort()+1;
			if (what == REMOVED_OBJECT_RECORD && thistype == type) {
				numrefs--;
				raf.seek(typeList+2+8*i+4);
				raf.writeShort(numrefs-1);
			}
			int reflist = typeList + raf.readShort();
			if ((!typeListAdjusted) && (reflist > offset)) {
				reflist -= length;
				raf.seek(typeList+2+8*i+6);
				raf.writeShort(reflist - typeList);
			}
			// update references
			for (int j=0; j<numrefs; j++) {
				raf.seek(reflist+12*j);
				/*short thisid = */raf.readShort();
				short name = raf.readShort();
				int dattr = raf.readInt();
				int data = dattr & 0x00FFFFFF;
				int attr = dattr & 0xFF000000;
				if (name >= 0) {
					if ((!nameListAdjusted) && (nameList+name > offset)) {
						raf.seek(reflist+12*j+2);
						raf.writeShort(name-length);
					}
				}
				if (data >= 0) {
					if ((!resDataAdjusted) && (resData+data > offset)) {
						raf.seek(reflist+12*j+4);
						raf.writeInt(((data-length) & 0xFFFFFF) | attr);
					}
				}
			}
		}
	}

	private void paste(int[] loc, int offset, byte[] stuff, int what, int type, short id) throws IOException {
		boolean typeListAdjusted=false, nameListAdjusted=false, resDataAdjusted=false;
		KSFLUtilities.paste(raf, offset, stuff);
		// update offsets
		if (resMap >= offset) resMap += stuff.length;
		if (typeList >= offset) { typeList += stuff.length; typeListAdjusted = true; }
		if (nameList > offset || (nameList == offset && what != INSERTED_NAME)) { nameList += stuff.length; nameListAdjusted = true; }
		if (resData > offset || (resData == offset && what != INSERTED_DATA)) { resData += stuff.length; resDataAdjusted = true; }
		if (what == INSERTED_TYPE_RECORD || what == INSERTED_OBJECT_RECORD || what == INSERTED_NAME) resMapLen += stuff.length;
		if (what == INSERTED_DATA) resDataLen += stuff.length;
		// update location
		if (loc != null) {
			if (loc.length > 0 && (loc[0] > offset || (loc[0] == offset && what != INSERTED_TYPE_RECORD))) loc[0] += stuff.length;
			if (loc.length > 1 && what == INSERTED_OBJECT_RECORD) loc[1]++;
			if (loc.length > 2 && (loc[2] > offset || (loc[2] == offset && what != INSERTED_OBJECT_RECORD))) loc[2] += stuff.length;
			if (loc.length > 3 && (loc[3] > offset || (loc[3] == offset && what != INSERTED_OBJECT_RECORD))) loc[3] += stuff.length;
			if (loc.length > 4 && (loc[4] > offset || (loc[4] == offset && what != INSERTED_NAME))) loc[4] += stuff.length;
			if (loc.length > 5 && (loc[5] > offset || (loc[5] == offset && what != INSERTED_DATA))) loc[5] += stuff.length;
		}
		// update header
		raf.seek(0);
		raf.writeInt(resData);
		raf.writeInt(resMap);
		raf.writeInt(resDataLen);
		raf.writeInt(resMapLen);
		// update resource map
		raf.seek(resMap);
		raf.writeInt(resData);
		raf.writeInt(resMap);
		raf.writeInt(resDataLen);
		raf.writeInt(resMapLen);
		raf.seek(resMap+24);
		raf.writeShort(typeList - resMap);
		raf.writeShort(nameList - resMap);
		// update type list
		raf.seek(typeList);
		int numtypes = raf.readShort()+1;
		if (what == INSERTED_TYPE_RECORD) {
			numtypes++;
			raf.seek(typeList);
			raf.writeShort(numtypes-1);
		}
		for (int i=0; i<numtypes; i++) {
			raf.seek(typeList+2+8*i);
			int thistype = raf.readInt();
			int numrefs = raf.readShort()+1;
			if (what == INSERTED_OBJECT_RECORD && thistype == type) {
				numrefs++;
				raf.seek(typeList+2+8*i+4);
				raf.writeShort(numrefs-1);
			}
			int reflist = typeList + raf.readShort();
			if ((!typeListAdjusted) && (reflist > offset || (reflist == offset && !(what == INSERTED_OBJECT_RECORD && thistype == type)))) {
				reflist += stuff.length;
				raf.seek(typeList+2+8*i+6);
				raf.writeShort(reflist - typeList);
			}
			// update references
			for (int j=0; j<numrefs; j++) {
				raf.seek(reflist+12*j);
				short thisid = raf.readShort();
				short name = raf.readShort();
				int dattr = raf.readInt();
				int data = dattr & 0x00FFFFFF;
				int attr = dattr & 0xFF000000;
				if (name >= 0) {
					if ((!nameListAdjusted) && (nameList+name > offset || (nameList+name == offset && !(what == INSERTED_NAME && thistype == type && thisid == id)))) {
						raf.seek(reflist+12*j+2);
						raf.writeShort(name+stuff.length);
					}
				}
				if (data >= 0) {
					if ((!resDataAdjusted) && (resData+data > offset || (resData+data == offset && !(what == INSERTED_DATA && thistype == type && thisid == id)))) {
						raf.seek(reflist+12*j+4);
						raf.writeInt(((data+stuff.length) & 0xFFFFFF) | attr);
					}
				}
			}
		}
	}
	
	/**
	 * Creates a <code>MacResourceFile</code> around a file.
	 * The file will not be modified until the resource structure is modified.
	 * If the file does not exist or is empty, a new resource structure is created.
	 * @param f the file object.
	 * @param mode the access mode, as described by <code>RandomAccessFile(File, String)</code>.
	 * @param create <code>CREATE_ALWAYS</code> if a new resource structure should be created, <code>CREATE_IF_EMPTY</code> if a new resource should be created if the file is empty, <code>CREATE_NEVER</code> if the file should not be modified.
	 * @throws IOException if an I/O error occurs.
	 */
	public MacResourceFile(File f, String mode, int create) throws IOException {
		raf = new RandomAccessFile(f, mode);
		readOnly = (mode.equalsIgnoreCase("r"));
		if ((create == CREATE_ALWAYS) || ((create == CREATE_IF_EMPTY) && (raf.length() == 0))) {
			raf.setLength(286);
			raf.seek(0);
			raf.writeInt(256);
			raf.writeInt(256);
			raf.writeInt(0);
			raf.writeInt(30);
			raf.seek(256);
			raf.writeInt(256);
			raf.writeInt(256);
			raf.writeInt(0);
			raf.writeInt(30);
			raf.writeInt(0);
			raf.writeShort(0);
			raf.writeShort(0);
			raf.writeShort(28);
			raf.writeShort(30);
			raf.writeShort(-1);
			resData = 256;
			resMap = 256;
			resDataLen = 0;
			resMapLen = 30;
			typeList = 284;
			nameList = 286;
		} else {
			raf.seek(0);
			resData = raf.readInt();
			resMap = raf.readInt();
			resDataLen = raf.readInt();
			resMapLen = raf.readInt();
			raf.seek(resMap+24);
			typeList = resMap + raf.readShort();
			nameList = resMap + raf.readShort();
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
	public synchronized short getResourceMapAttributes() {
		try {
			raf.seek(resMap+22);
			return raf.readShort();
		} catch (IOException e) {
			return 0;
		}
	}
	
	@Override
	public synchronized void setResourceMapAttributes(short attr) {
		try {
			raf.seek(resMap+22);
			raf.writeShort(attr);
		} catch (IOException e) {}
	}
	
	@Override
	public synchronized boolean add(MacResource r) throws MacResourceAlreadyExistsException {
		try {
			if (locate(r.type,r.id) != null) throw new MacResourceAlreadyExistsException();
			//type record
			if (locateType(r.type) == null) {
				byte[] th = new byte[8];
				KSFLUtilities.putInt(th, 0, r.type);
				KSFLUtilities.putShort(th, 4, (short)-1);
				KSFLUtilities.putShort(th, 6, (short)(nameList - typeList));
				raf.seek(typeList);
				int lasttype = typeList+2+8*(raf.readShort()+1);
				paste(null, lasttype, th, INSERTED_TYPE_RECORD, r.type, r.id);
			}
			//object record
			int[] t = locateType(r.type);
			byte[] ref = new byte[12];
			KSFLUtilities.putShort(ref, 0, r.id);
			KSFLUtilities.putShort(ref, 2, (r.name != null && r.name.length()>0)?(short)(resMap+resMapLen-nameList):(short)-1);
			KSFLUtilities.putInt(ref, 4, resDataLen);
			ref[4] = r.getAttributes();
			KSFLUtilities.putInt(ref, 8, 0);
			paste(null, t[2]+12*t[1], ref, INSERTED_OBJECT_RECORD, r.type, r.id);
			//name
			if (r.name != null && r.name.length()>0) {
				byte[] n = gb(r.name);
				if (n.length > 255) n = KSFLUtilities.copy(n, 0, 255);
				n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
				paste(null, resMap+resMapLen, n, INSERTED_NAME, r.type, r.id);
			}
			//data
			byte[] d = KSFLUtilities.paste(r.data, 0, 4);
			KSFLUtilities.putInt(d, 0, r.data.length);
			paste(null, resData+resDataLen, d, INSERTED_DATA, r.type, r.id);
			//done
			return true;
		} catch (IOException e) {}
		return false;
	}
	
	@Override
	public synchronized boolean contains(int type, short id) {
		return (locate(type,id) != null);
	}
	@Override
	public synchronized boolean contains(int type, String name) {
		return (locate(type,name) != null);
	}
	
	@Override
	public synchronized MacResource get(int type, short id) {
		try {
			int[] l = locate(type,id);
			if (l != null) {
				raf.seek(l[0]);
				int t = raf.readInt();
				raf.seek(l[3]);
				short i = raf.readShort();
				raf.seek(l[3]+4);
				byte a = raf.readByte();
				raf.seek(l[5]);
				int dl = raf.readInt();
				return new MacResource(
						t, i, a,
						(l[4]>0)?gps(l[4]):"",
						(l[5]>0)?KSFLUtilities.copy(raf, l[5]+4, dl):(new byte[0])
				);
			}
		} catch (IOException e) {}
		return null;
	}
	@Override
	public synchronized MacResource get(int type, String name) {
		try {
			int[] l = locate(type,name);
			if (l != null) {
				raf.seek(l[0]);
				int t = raf.readInt();
				raf.seek(l[3]);
				short i = raf.readShort();
				raf.seek(l[3]+4);
				byte a = raf.readByte();
				raf.seek(l[5]);
				int dl = raf.readInt();
				return new MacResource(
						t, i, a,
						(l[4]>0)?gps(l[4]):"",
						(l[5]>0)?KSFLUtilities.copy(raf, l[5]+4, dl):(new byte[0])
				);
			}
		} catch (IOException e) {}
		return null;
	}
	
	@Override
	public synchronized MacResource getAttributes(int type, short id) {
		try {
			int[] l = locate(type,id);
			if (l != null) {
				raf.seek(l[0]);
				int t = raf.readInt();
				raf.seek(l[3]);
				short i = raf.readShort();
				raf.seek(l[3]+4);
				byte a = raf.readByte();
				return new MacResource(
						t, i, a,
						(l[4]>0)?gps(l[4]):"",
						new byte[0]
				);
			}
		} catch (IOException e) {}
		return null;
	}
	@Override
	public synchronized MacResource getAttributes(int type, String name) {
		try {
			int[] l = locate(type,name);
			if (l != null) {
				raf.seek(l[0]);
				int t = raf.readInt();
				raf.seek(l[3]);
				short i = raf.readShort();
				raf.seek(l[3]+4);
				byte a = raf.readByte();
				return new MacResource(
						t, i, a,
						(l[4]>0)?gps(l[4]):"",
						new byte[0]
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
	public synchronized byte[] getData(int type, String name) {
		try {
			int[] l = locate(type,name);
			if (l != null) {
				raf.seek(l[5]);
				int dl = raf.readInt();
				return (l[5]>0)?KSFLUtilities.copy(raf, l[5]+4, dl):(new byte[0]);
			}
		} catch (IOException e) {}
		return null;
	}
	
	@Override
	public synchronized boolean remove(int type, short id) {
		try {
			int[] loc = locate(type,id);
			if (loc != null) {
				//delete data
				if (loc[5] > 0) {
					raf.seek(loc[5]);
					int dlen = raf.readInt()+4;
					cut(loc, loc[5], dlen, REMOVED_DATA, type, id);
				}
				//delete name
				if (loc[4] > 0) {
					raf.seek(loc[4]);
					int nlen = (raf.readByte() & 0xFF)+1;
					cut(loc, loc[4], nlen, REMOVED_NAME, type, id);
				}
				//delete object record
				cut(loc, loc[3], 12, REMOVED_OBJECT_RECORD, type, id);
				//delete type record
				if (loc[1] <= 0) {
					cut(loc, loc[0], 8, REMOVED_TYPE_RECORD, type, id);
				}
				return true;
			}
		} catch (IOException e) {}
		return false;
	}
	@Override
	public synchronized boolean remove(int type, String name) {
		try {
			int[] loc = locate(type,name);
			if (loc != null) {
				raf.seek(loc[3]);
				short id = raf.readShort();
				//delete data
				if (loc[5] > 0) {
					raf.seek(loc[5]);
					int dlen = raf.readInt()+4;
					cut(loc, loc[5], dlen, REMOVED_DATA, type, id);
				}
				//delete name
				if (loc[4] > 0) {
					raf.seek(loc[4]);
					int nlen = (raf.readByte() & 0xFF)+1;
					cut(loc, loc[4], nlen, REMOVED_NAME, type, id);
				}
				//delete object record
				cut(loc, loc[3], 12, REMOVED_OBJECT_RECORD, type, id);
				//delete type record
				if (loc[1] <= 0) {
					cut(loc, loc[0], 8, REMOVED_TYPE_RECORD, type, id);
				}
				return true;
			}
		} catch (IOException e) {}
		return false;
	}
	
	@Override
	public synchronized boolean set(int type, short id, MacResource r) throws MacResourceAlreadyExistsException {
		if (!contains(type, id)) return false;
		if (r.name == null) r.name = "";
		if (r.data == null) r.data = new byte[0];
		return setAttributes(type, id, r) && setData(r.type, r.id, r.data);
	}
	@Override
	public synchronized boolean set(int type, String name, MacResource r) throws MacResourceAlreadyExistsException {
		short id = getIDFromName(type, name);
		if (!contains(type, id)) return false;
		if (r.name == null) r.name = "";
		if (r.data == null) r.data = new byte[0];
		return setAttributes(type, id, r) && setData(r.type, r.id, r.data);
	}
	
	@Override
	public synchronized boolean setAttributes(int type, short id, MacResource r) throws MacResourceAlreadyExistsException {
		try {
			int[] loc = locate(type,id);
			if (loc != null) {
				int[] loce = locate(r.type,r.id);
				if ((loce != null) && (
						(loce[0] != loc[0]) ||
						(loce[1] != loc[1]) ||
						(loce[2] != loc[2]) ||
						(loce[3] != loc[3]) ||
						(loce[4] != loc[4]) ||
						(loce[5] != loc[5])
				)) {
					throw new MacResourceAlreadyExistsException();
				} else {
					if (type != r.type) {
						//the hard part
						//type record
						if (locateType(r.type) == null) {
							byte[] th = new byte[8];
							KSFLUtilities.putInt(th, 0, r.type);
							KSFLUtilities.putShort(th, 4, (short)-1);
							KSFLUtilities.putShort(th, 6, (short)(nameList - typeList));
							raf.seek(typeList);
							int lasttype = typeList+2+8*(raf.readShort()+1);
							paste(loc, lasttype, th, INSERTED_TYPE_RECORD, r.type, r.id);
						}
						//object record
						int[] t = locateType(r.type);
						byte[] ref = new byte[12];
						KSFLUtilities.putShort(ref, 0, r.id);
						KSFLUtilities.putShort(ref, 2, (loc[4]>0)?(short)(loc[4]-nameList):(short)-1);
						KSFLUtilities.putInt(ref, 4, (loc[5]>0)?(loc[5]-resData):-1);
						ref[4] = r.getAttributes();
						KSFLUtilities.putInt(ref, 8, 0);
						paste(loc, t[2]+12*t[1], ref, INSERTED_OBJECT_RECORD, r.type, r.id);
						//delete object record
						cut(loc, loc[3], 12, REMOVED_OBJECT_RECORD, type, id);
						//delete type record
						raf.seek(loc[0]+4);
						if (raf.readShort() < 0) {
							cut(loc, loc[0], 8, REMOVED_TYPE_RECORD, type, id);
						}
						//ResourceArray's auto-handling of counts and offsets makes this
						//MUCH easier than DFFArray's setObjectAttributes
					} else {
						//the easy part
						raf.seek(loc[3]);
						raf.writeShort(r.id);
						raf.skipBytes(2);
						raf.writeByte(r.getAttributes());
					}
					//the other part
					if (r.name != null && r.name.length() > 0) {
						byte[] n = gb(r.name);
						if (n.length > 255) n = KSFLUtilities.copy(n, 0, 255);
						n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
						if (loc[4] > 0) {
							paste(loc, loc[4], n, INSERTED_NAME, r.type, r.id);
							raf.seek(loc[4]+n.length);
							int nl = (raf.readByte()&0xFF)+1;
							cut(loc, loc[4]+n.length, nl, REMOVED_NAME, r.type, r.id);
						} else {
							raf.seek(loc[3]+2);
							raf.writeShort(resMap+resMapLen-nameList);
							paste(loc, resMap+resMapLen, n, INSERTED_NAME, r.type, r.id);
						}
					} else if (loc[4] > 0) {
						raf.seek(loc[4]);
						int nl = (raf.readByte()&0xFF)+1;
						cut(loc, loc[4], nl, REMOVED_NAME, r.type, r.id);
						raf.seek(loc[3]+2);
						raf.writeShort(-1);
						loc[4] = 0;
					}
					return true;
				}
			}
		} catch (IOException e) {}
		return false;
	}
	@Override
	public synchronized boolean setAttributes(int type, String name, MacResource r) throws MacResourceAlreadyExistsException {
		try {
			int[] loc = locate(type,name);
			if (loc != null) {
				int[] loce = locate(r.type,r.id);
				if ((loce != null) && (
						(loce[0] != loc[0]) ||
						(loce[1] != loc[1]) ||
						(loce[2] != loc[2]) ||
						(loce[3] != loc[3]) ||
						(loce[4] != loc[4]) ||
						(loce[5] != loc[5])
				)) {
					throw new MacResourceAlreadyExistsException();
				} else {
					raf.seek(loc[3]);
					short id = raf.readShort();
					if (type != r.type) {
						//the hard part
						//type record
						if (locateType(r.type) == null) {
							byte[] th = new byte[8];
							KSFLUtilities.putInt(th, 0, r.type);
							KSFLUtilities.putShort(th, 4, (short)-1);
							KSFLUtilities.putShort(th, 6, (short)(nameList - typeList));
							raf.seek(typeList);
							int lasttype = typeList+2+8*(raf.readShort()+1);
							paste(loc, lasttype, th, INSERTED_TYPE_RECORD, r.type, r.id);
						}
						//object record
						int[] t = locateType(r.type);
						byte[] ref = new byte[12];
						KSFLUtilities.putShort(ref, 0, r.id);
						KSFLUtilities.putShort(ref, 2, (loc[4]>0)?(short)(loc[4]-nameList):(short)-1);
						KSFLUtilities.putInt(ref, 4, (loc[5]>0)?(loc[5]-resData):-1);
						ref[4] = r.getAttributes();
						KSFLUtilities.putInt(ref, 8, 0);
						paste(loc, t[2]+12*t[1], ref, INSERTED_OBJECT_RECORD, r.type, r.id);
						//delete object record
						cut(loc, loc[3], 12, REMOVED_OBJECT_RECORD, type, id);
						//delete type record
						raf.seek(loc[0]+4);
						if (raf.readShort() < 0) {
							cut(loc, loc[0], 8, REMOVED_TYPE_RECORD, type, id);
						}
						//ResourceArray's auto-handling of counts and offsets makes this
						//MUCH easier than DFFArray's setObjectAttributes
					} else {
						//the easy part
						raf.seek(loc[3]);
						raf.writeShort(r.id);
						raf.skipBytes(2);
						raf.writeByte(r.getAttributes());
					}
					//the other part
					if (r.name != null && r.name.length() > 0) {
						byte[] n = gb(r.name);
						if (n.length > 255) n = KSFLUtilities.copy(n, 0, 255);
						n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
						if (loc[4] > 0) {
							paste(loc, loc[4], n, INSERTED_NAME, r.type, r.id);
							raf.seek(loc[4]+n.length);
							int nl = (raf.readByte()&0xFF)+1;
							cut(loc, loc[4]+n.length, nl, REMOVED_NAME, r.type, r.id);
						} else {
							raf.seek(loc[3]+2);
							raf.writeShort(resMap+resMapLen-nameList);
							paste(loc, resMap+resMapLen, n, INSERTED_NAME, r.type, r.id);
						}
					} else if (loc[4] > 0) {
						raf.seek(loc[4]);
						int nl = (raf.readByte()&0xFF)+1;
						cut(loc, loc[4], nl, REMOVED_NAME, r.type, r.id);
						raf.seek(loc[3]+2);
						raf.writeShort(-1);
						loc[4] = 0;
					}
					return true;
				}
			}
		} catch (IOException e) {}
		return false;
	}
	
	@Override
	public synchronized boolean setData(int type, short id, byte[] data) {
		try {
			int[] loc = locate(type,id);
			if (loc != null) {
				//delete data
				if (loc[5] > 0) {
					raf.seek(loc[5]);
					int dlen = raf.readInt()+4;
					cut(loc, loc[5], dlen, REMOVED_DATA, type, id);
				} else {
					loc[5] = resData+resDataLen;
				}
				//insert data
				byte[] d = KSFLUtilities.paste(data, 0, 4);
				KSFLUtilities.putInt(d, 0, data.length);
				paste(loc, loc[5], d, INSERTED_DATA, type, id);
				return true;
			}
		} catch (IOException e) {}
		return false;
	}
	@Override
	public synchronized boolean setData(int type, String name, byte[] data) {
		try {
			int[] loc = locate(type,name);
			if (loc != null) {
				raf.seek(loc[3]);
				short id = raf.readShort();
				//delete data
				if (loc[5] > 0) {
					raf.seek(loc[5]);
					int dlen = raf.readInt()+4;
					cut(loc, loc[5], dlen, REMOVED_DATA, type, id);
				} else {
					loc[5] = resData+resDataLen;
				}
				//insert data
				byte[] d = KSFLUtilities.paste(data, 0, 4);
				KSFLUtilities.putInt(d, 0, data.length);
				paste(loc, loc[5], d, INSERTED_DATA, type, id);
				return true;
			}
		} catch (IOException e) {}
		return false;
	}
	
	@Override
	public synchronized int getTypeCount() {
		try {
			raf.seek(typeList);
			return raf.readShort()+1;
		} catch (IOException e) {}
		return 0;
	}
	@Override
	public synchronized int getType(int index) {
		try {
			raf.seek(typeList+2+8*index);
			return raf.readInt();
		} catch (IOException e) {}
		return 0;
	}
	@Override
	public synchronized int[] getTypes() {
		try {
			raf.seek(typeList);
			int m = raf.readShort()+1;
			int[] a = new int[m];
			for (int i=0; i<m; i++) {
				a[i]=raf.readInt();
				raf.readInt();
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
				raf.seek(t[2]+12*index);
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
					raf.seek(t[2]+12*i);
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
				int n = raf.readShort();
				if (n < 0) return "";
				else return gps(nameList + n);
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
				int n = raf.readShort();
				if (n < 0) a.add("");
				else a.add(gps(nameList + n));
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
				raf.seek(t[2]+12*i);
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
	public synchronized short getIDFromName(int type, String name) {
		try {
			int[] l = locate(type,name);
			if (l != null) {
				raf.seek(l[3]);
				return raf.readShort();
			}
		} catch (IOException e) {}
		return 0;
	}
}
