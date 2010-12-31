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

import java.util.ArrayList;
import com.kreative.ksfl.KSFLUtilities;

/**
 * The <code>MacResourceArray</code> class represents a resource fork as defined by
 * the Mac OS Resource Manager in an array of bytes. The Resource Manager was originally
 * designed and implemented by Bruce Horn for use in the original
 * Macintosh Operating System. It remains today as a feature unique
 * to the Mac OS. Other operating systems have the concept of
 * resources, but the Mac OS implementation remains unique.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class MacResourceArray extends MacResourceProvider {
	private byte[] arr;
	private int resMap, typeList, nameList, resData;
	private int resMapLen, resDataLen;
	private String textEncoding = "MACROMAN";
	
	private String gps(byte[] a, int b) {
		try {
			return KSFLUtilities.getPString(a,b,textEncoding);
		} catch (java.io.UnsupportedEncodingException uue) {
			return KSFLUtilities.getPString(a,b);
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
		int m = KSFLUtilities.getShort(arr, typeList)+1;
		for (int i=0; i<m; i++) {
			int t = KSFLUtilities.getInt(arr, typeList+2+8*i);
			if (t == type) {
				int cnt = KSFLUtilities.getShort(arr, typeList+2+8*i+4)+1;
				int lst = typeList + KSFLUtilities.getShort(arr, typeList+2+8*i+6);
				return new int[]{typeList+2+8*i, cnt, lst};
			}
		}
		return null;
	}
	
	private int[] locate(int type, short id) {
		// 0 - offset to type record
		// 1 - number of items of that type
		// 2 - offset to reference list
		// 3 - offset to reference record
		// 4 - offset to name
		// 5 - offset to data
		int[] t = locateType(type);
		if (t != null) {
			for (int i=0; i<t[1]; i++) {
				short thisid = KSFLUtilities.getShort(arr, t[2]+12*i);
				if (thisid == id) {
					int n = KSFLUtilities.getShort(arr, t[2]+12*i+2);
					int d = KSFLUtilities.getInt(arr, t[2]+12*i+4) & 0xFFFFFF;
					return new int[]{
							t[0], t[1], t[2], t[2]+12*i,
							((n<0)?0:(nameList+n)),
							((d<0)?0:(resData+d))
					};
				}
			}
		}
		return null;
	}
	
	private int[] locate(int type, String name) {
		// 0 - offset to type record
		// 1 - number of items of that type
		// 2 - offset to reference list
		// 3 - offset to reference record
		// 4 - offset to name
		// 5 - offset to data
		int[] t = locateType(type);
		if (t != null) {
			for (int i=0; i<t[1]; i++) {
				int n = KSFLUtilities.getShort(arr, t[2]+12*i+2);
				if (n >= 0 && gps(arr, nameList+n).equals(name)) {
					int d = KSFLUtilities.getInt(arr, t[2]+12*i+4) & 0xFFFFFF;
					return new int[]{
							t[0], t[1], t[2], t[2]+12*i,
							((n<0)?0:(nameList+n)),
							((d<0)?0:(resData+d))
					};
				}
			}
		}
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
	
	private void cut(int[] loc, int offset, int length, int what, int type, short id) {
		boolean typeListAdjusted=false, nameListAdjusted=false, resDataAdjusted=false;
		arr = KSFLUtilities.cut(arr, offset, length);
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
		KSFLUtilities.putInt(arr, 0, resData);
		KSFLUtilities.putInt(arr, 4, resMap);
		KSFLUtilities.putInt(arr, 8, resDataLen);
		KSFLUtilities.putInt(arr, 12, resMapLen);
		// update resource map
		KSFLUtilities.putInt(arr, resMap+0, resData);
		KSFLUtilities.putInt(arr, resMap+4, resMap);
		KSFLUtilities.putInt(arr, resMap+8, resDataLen);
		KSFLUtilities.putInt(arr, resMap+12, resMapLen);
		KSFLUtilities.putShort(arr, resMap+24, (short)(typeList - resMap));
		KSFLUtilities.putShort(arr, resMap+26, (short)(nameList - resMap));
		// update type list
		int numtypes = KSFLUtilities.getShort(arr, typeList)+1;
		if (what == REMOVED_TYPE_RECORD) {
			numtypes--;
			KSFLUtilities.putShort(arr, typeList, (short)(numtypes-1));
		}
		for (int i=0; i<numtypes; i++) {
			int thistype = KSFLUtilities.getInt(arr, typeList+2+8*i);
			int numrefs = KSFLUtilities.getShort(arr, typeList+2+8*i+4)+1;
			if (what == REMOVED_OBJECT_RECORD && thistype == type) {
				numrefs--;
				KSFLUtilities.putShort(arr, typeList+2+8*i+4, (short)(numrefs-1));
			}
			int reflist = typeList + KSFLUtilities.getShort(arr, typeList+2+8*i+6);
			if ((!typeListAdjusted) && (reflist > offset)) {
				reflist -= length;
				KSFLUtilities.putShort(arr, typeList+2+8*i+6, (short)(reflist - typeList));
			}
			// update references
			for (int j=0; j<numrefs; j++) {
				//short thisid = KSFLUtilities.getShort(arr, reflist+12*j);
				short name = KSFLUtilities.getShort(arr, reflist+12*j+2);
				int data = KSFLUtilities.getInt(arr, reflist+12*j+4) & 0xFFFFFF;
				if (name >= 0) {
					if ((!nameListAdjusted) && (nameList+name > offset))
						KSFLUtilities.putShort(arr, reflist+12*j+2, (short)(name-length));
				}
				if (data >= 0) {
					if ((!resDataAdjusted) && (resData+data > offset))
						KSFLUtilities.putInt(arr, reflist+12*j+4, ((data-length) & 0xFFFFFF) | (arr[reflist+12*j+4] << 24));
				}
			}
		}
	}
	
	private void paste(int[] loc, int offset, byte[] stuff, int what, int type, short id) {
		boolean typeListAdjusted=false, nameListAdjusted=false, resDataAdjusted=false;
		arr = KSFLUtilities.paste(arr, offset, stuff);
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
		KSFLUtilities.putInt(arr, 0, resData);
		KSFLUtilities.putInt(arr, 4, resMap);
		KSFLUtilities.putInt(arr, 8, resDataLen);
		KSFLUtilities.putInt(arr, 12, resMapLen);
		// update resource map
		KSFLUtilities.putInt(arr, resMap+0, resData);
		KSFLUtilities.putInt(arr, resMap+4, resMap);
		KSFLUtilities.putInt(arr, resMap+8, resDataLen);
		KSFLUtilities.putInt(arr, resMap+12, resMapLen);
		KSFLUtilities.putShort(arr, resMap+24, (short)(typeList - resMap));
		KSFLUtilities.putShort(arr, resMap+26, (short)(nameList - resMap));
		// update type list
		int numtypes = KSFLUtilities.getShort(arr, typeList)+1;
		if (what == INSERTED_TYPE_RECORD) {
			numtypes++;
			KSFLUtilities.putShort(arr, typeList, (short)(numtypes-1));
		}
		for (int i=0; i<numtypes; i++) {
			int thistype = KSFLUtilities.getInt(arr, typeList+2+8*i);
			int numrefs = KSFLUtilities.getShort(arr, typeList+2+8*i+4)+1;
			if (what == INSERTED_OBJECT_RECORD && thistype == type) {
				numrefs++;
				KSFLUtilities.putShort(arr, typeList+2+8*i+4, (short)(numrefs-1));
			}
			int reflist = typeList + KSFLUtilities.getShort(arr, typeList+2+8*i+6);
			if ((!typeListAdjusted) && (reflist > offset || (reflist == offset && !(what == INSERTED_OBJECT_RECORD && thistype == type)))) {
				reflist += stuff.length;
				KSFLUtilities.putShort(arr, typeList+2+8*i+6, (short)(reflist - typeList));
			}
			// update references
			for (int j=0; j<numrefs; j++) {
				short thisid = KSFLUtilities.getShort(arr, reflist+12*j);
				short name = KSFLUtilities.getShort(arr, reflist+12*j+2);
				int data = KSFLUtilities.getInt(arr, reflist+12*j+4) & 0xFFFFFF;
				if (name >= 0) {
					if ((!nameListAdjusted) && (nameList+name > offset || (nameList+name == offset && !(what == INSERTED_NAME && thistype == type && thisid == id))))
						KSFLUtilities.putShort(arr, reflist+12*j+2, (short)(name+stuff.length));
				}
				if (data >= 0) {
					if ((!resDataAdjusted) && (resData+data > offset || (resData+data == offset && !(what == INSERTED_DATA && thistype == type && thisid == id))))
						KSFLUtilities.putInt(arr, reflist+12*j+4, ((data+stuff.length) & 0xFFFFFF) | (arr[reflist+12*j+4] << 24));
				}
			}
		}
	}
	
	/**
	 * Creates a new <code>MacResourceArray</code> containing no resources.
	 */
	public MacResourceArray() {
		arr = new byte[286];
		KSFLUtilities.putInt(arr, 0, 256);
		KSFLUtilities.putInt(arr, 4, 256);
		KSFLUtilities.putInt(arr, 8, 0);
		KSFLUtilities.putInt(arr, 12, 30);
		KSFLUtilities.putInt(arr, 256, 256);
		KSFLUtilities.putInt(arr, 260, 256);
		KSFLUtilities.putInt(arr, 264, 0);
		KSFLUtilities.putInt(arr, 268, 30);
		KSFLUtilities.putInt(arr, 272, 0);
		KSFLUtilities.putShort(arr, 276, (short)0);
		KSFLUtilities.putShort(arr, 278, (short)0);
		KSFLUtilities.putShort(arr, 280, (short)28);
		KSFLUtilities.putShort(arr, 282, (short)30);
		KSFLUtilities.putShort(arr, 284, (short)-1);
		resData = 256;
		resMap = 256;
		resDataLen = 0;
		resMapLen = 30;
		typeList = 284;
		nameList = 286;
	}
	
	/**
	 * Creates a <code>MacResourceArray</code> wrapped around the specified byte array.
	 * The array should not be modified or read after this call.
	 * <p>
	 * No verification of the integrity of the data is performed.
	 * If the data does not contain a resource structure, calls to the
	 * constructed <code>MacResourceArray</code> will throw <code>ArrayIndexOutOfBoundsException</code>s.
	 * @param stuff a byte array containing a resource fork.
	 */
	public MacResourceArray(byte[] stuff) {
		arr = stuff;
		resData = KSFLUtilities.getInt(stuff, 0);
		resMap = KSFLUtilities.getInt(stuff, 4);
		resDataLen = KSFLUtilities.getInt(stuff, 8);
		resMapLen = KSFLUtilities.getInt(stuff, 12);
		typeList = resMap + KSFLUtilities.getShort(stuff, resMap+24);
		nameList = resMap + KSFLUtilities.getShort(stuff, resMap+26);
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
	 * Returns the byte array this <code>MacResourceArray</code> is wrapped around.
	 * The array should not be modified after this call.
	 * Subsequent calls to this method may not return the same array.
	 * @return a byte array containing this resource structure.
	 */
	public byte[] getBytes() {
		return arr;
	}
	
	@Override
	public boolean isReadOnly() {
		return false;
	}
	
	@Override
	public synchronized void flush() {
		// nothing
	}
	
	@Override
	public synchronized void close() {
		// nothing
	}
	
	@Override
	public synchronized short getResourceMapAttributes() {
		return KSFLUtilities.getShort(arr, resMap+22);
	}
	
	@Override
	public synchronized void setResourceMapAttributes(short attr) {
		KSFLUtilities.putShort(arr, resMap+22, attr);
	}
	
	@Override
	public synchronized boolean add(MacResource r) throws MacResourceAlreadyExistsException {
		if (locate(r.type,r.id) != null) throw new MacResourceAlreadyExistsException();
		//type record
		if (locateType(r.type) == null) {
			byte[] th = new byte[8];
			KSFLUtilities.putInt(th, 0, r.type);
			KSFLUtilities.putShort(th, 4, (short)-1);
			KSFLUtilities.putShort(th, 6, (short)(nameList - typeList));
			int lasttype = typeList+2+8*(KSFLUtilities.getShort(arr, typeList)+1);
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
		int[] l = locate(type,id);
		if (l != null) {
			return new MacResource(
					KSFLUtilities.getInt(arr, l[0]),
					KSFLUtilities.getShort(arr, l[3]),
					arr[l[3]+4],
					(l[4]>0)?gps(arr, l[4]):"",
					(l[5]>0)?KSFLUtilities.copy(arr, l[5]+4, KSFLUtilities.getInt(arr, l[5])):(new byte[0])
			);
		}
		return null;
	}
	@Override
	public synchronized MacResource get(int type, String name) {
		int[] l = locate(type,name);
		if (l != null) {
			return new MacResource(
					KSFLUtilities.getInt(arr, l[0]),
					KSFLUtilities.getShort(arr, l[3]),
					arr[l[3]+4],
					(l[4]>0)?gps(arr, l[4]):"",
					(l[5]>0)?KSFLUtilities.copy(arr, l[5]+4, KSFLUtilities.getInt(arr, l[5])):(new byte[0])
			);
		}
		return null;
	}
	
	@Override
	public synchronized MacResource getAttributes(int type, short id) {
		int[] l = locate(type,id);
		if (l != null) {
			return new MacResource(
					KSFLUtilities.getInt(arr, l[0]),
					KSFLUtilities.getShort(arr, l[3]),
					arr[l[3]+4],
					(l[4]>0)?gps(arr, l[4]):"",
					new byte[0]
			);
		}
		return null;
	}
	@Override
	public synchronized MacResource getAttributes(int type, String name) {
		int[] l = locate(type,name);
		if (l != null) {
			return new MacResource(
					KSFLUtilities.getInt(arr, l[0]),
					KSFLUtilities.getShort(arr, l[3]),
					arr[l[3]+4],
					(l[4]>0)?gps(arr, l[4]):"",
					new byte[0]
			);
		}
		return null;
	}
	
	@Override
	public synchronized byte[] getData(int type, short id) {
		int[] l = locate(type,id);
		if (l != null) {
			return (l[5]>0)?KSFLUtilities.copy(arr, l[5]+4, KSFLUtilities.getInt(arr, l[5])):(new byte[0]);
		}
		return null;
	}
	@Override
	public synchronized byte[] getData(int type, String name) {
		int[] l = locate(type,name);
		if (l != null) {
			return (l[5]>0)?KSFLUtilities.copy(arr, l[5]+4, KSFLUtilities.getInt(arr, l[5])):(new byte[0]);
		}
		return null;
	}
	
	@Override
	public synchronized boolean remove(int type, short id) {
		int[] loc = locate(type,id);
		if (loc != null) {
			//delete data
			if (loc[5] > 0) {
				int dlen = KSFLUtilities.getInt(arr, loc[5])+4;
				cut(loc, loc[5], dlen, REMOVED_DATA, type, id);
			}
			//delete name
			if (loc[4] > 0) {
				int nlen = (arr[loc[4]] & 0xFF)+1;
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
		return false;
	}
	@Override
	public synchronized boolean remove(int type, String name) {
		int[] loc = locate(type,name);
		if (loc != null) {
			short id = KSFLUtilities.getShort(arr, loc[3]);
			//delete data
			if (loc[5] > 0) {
				int dlen = KSFLUtilities.getInt(arr, loc[5])+4;
				cut(loc, loc[5], dlen, REMOVED_DATA, type, id);
			}
			//delete name
			if (loc[4] > 0) {
				int nlen = (arr[loc[4]] & 0xFF)+1;
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
						int lasttype = typeList+2+8*(KSFLUtilities.getShort(arr, typeList)+1);
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
					if (KSFLUtilities.getShort(arr, loc[0]+4) < 0) {
						cut(loc, loc[0], 8, REMOVED_TYPE_RECORD, type, id);
					}
					//MacResourceArray's auto-handling of counts and offsets makes this
					//MUCH easier than DFFArray's setObjectAttributes
				} else {
					//the easy part
					KSFLUtilities.putShort(arr, loc[3], (short)r.id);
					arr[loc[3]+4] = r.getAttributes();
				}
				//the other part
				if (r.name != null && r.name.length() > 0) {
					byte[] n = gb(r.name);
					if (n.length > 255) n = KSFLUtilities.copy(n, 0, 255);
					n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
					if (loc[4] > 0) {
						paste(loc, loc[4], n, INSERTED_NAME, r.type, r.id);
						cut(loc, loc[4]+n.length, (arr[loc[4]+n.length]&0xFF)+1, REMOVED_NAME, r.type, r.id);
					} else {
						KSFLUtilities.putShort(arr, loc[3]+2, (short)(resMap+resMapLen-nameList));
						paste(loc, resMap+resMapLen, n, INSERTED_NAME, r.type, r.id);
					}
				} else if (loc[4] > 0) {
					cut(loc, loc[4], (arr[loc[4]]&0xFF)+1, REMOVED_NAME, r.type, r.id);
					KSFLUtilities.putShort(arr, loc[3]+2, (short)-1);
					loc[4] = 0;
				}
				return true;
			}
		}
		return false;
	}
	@Override
	public synchronized boolean setAttributes(int type, String name, MacResource r) throws MacResourceAlreadyExistsException {
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
				short id = KSFLUtilities.getShort(arr, loc[3]);
				if (type != r.type) {
					//the hard part
					//type record
					if (locateType(r.type) == null) {
						byte[] th = new byte[8];
						KSFLUtilities.putInt(th, 0, r.type);
						KSFLUtilities.putShort(th, 4, (short)-1);
						KSFLUtilities.putShort(th, 6, (short)(nameList - typeList));
						int lasttype = typeList+2+8*(KSFLUtilities.getShort(arr, typeList)+1);
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
					if (KSFLUtilities.getShort(arr, loc[0]+4) < 0) {
						cut(loc, loc[0], 8, REMOVED_TYPE_RECORD, type, id);
					}
					//MacResourceArray's auto-handling of counts and offsets makes this
					//MUCH easier than DFFArray's setObjectAttributes
				} else {
					//the easy part
					KSFLUtilities.putShort(arr, loc[3], (short)r.id);
					arr[loc[3]+4] = r.getAttributes();
				}
				//the other part
				if (r.name != null && r.name.length() > 0) {
					byte[] n = gb(r.name);
					if (n.length > 255) n = KSFLUtilities.copy(n, 0, 255);
					n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
					if (loc[4] > 0) {
						paste(loc, loc[4], n, INSERTED_NAME, r.type, r.id);
						cut(loc, loc[4]+n.length, (arr[loc[4]+n.length]&0xFF)+1, REMOVED_NAME, r.type, r.id);
					} else {
						KSFLUtilities.putShort(arr, loc[3]+2, (short)(resMap+resMapLen-nameList));
						paste(loc, resMap+resMapLen, n, INSERTED_NAME, r.type, r.id);
					}
				} else if (loc[4] > 0) {
					cut(loc, loc[4], (arr[loc[4]]&0xFF)+1, REMOVED_NAME, r.type, r.id);
					KSFLUtilities.putShort(arr, loc[3]+2, (short)-1);
					loc[4] = 0;
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public synchronized boolean setData(int type, short id, byte[] data) {
		int[] loc = locate(type,id);
		if (loc != null) {
			//delete data
			if (loc[5] > 0) {
				int dlen = KSFLUtilities.getInt(arr, loc[5])+4;
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
		return false;
	}
	@Override
	public synchronized boolean setData(int type, String name, byte[] data) {
		int[] loc = locate(type,name);
		if (loc != null) {
			short id = KSFLUtilities.getShort(arr, loc[3]);
			//delete data
			if (loc[5] > 0) {
				int dlen = KSFLUtilities.getInt(arr, loc[5])+4;
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
		return false;
	}
	
	@Override
	public synchronized int getTypeCount() {
		return KSFLUtilities.getShort(arr, typeList)+1;
	}
	@Override
	public synchronized int getType(int index) {
		return KSFLUtilities.getInt(arr, typeList+2+8*index);
	}
	@Override
	public synchronized int[] getTypes() {
		int m = KSFLUtilities.getShort(arr, typeList)+1;
		int[] a = new int[m];
		for (int i=0; i<m; i++) a[i]=KSFLUtilities.getInt(arr, typeList+2+8*i);
		return a;
	}
	
	@Override
	public synchronized int getResourceCount(int type) {
		int[] t = locateType(type);
		if (t != null) return t[1];
		else return 0;
	}
	@Override
	public synchronized short getID(int type, int index) {
		int[] t = locateType(type);
		if (t != null) return KSFLUtilities.getShort(arr, t[2]+12*index);
		else return 0;
	}
	@Override
	public synchronized short[] getIDs(int type) {
		int[] t = locateType(type);
		if (t != null) {
			short[] a = new short[t[1]];
			for (int i=0; i<t[1]; i++) a[i] = KSFLUtilities.getShort(arr, t[2]+12*i);
			return a;
		}
		return new short[0];
	}
	@Override
	public String getName(int type, int index) {
		int[] t = locateType(type);
		if (t != null) {
			int n = KSFLUtilities.getShort(arr, t[2]+12*index+2);
			if (n < 0) return "";
			else return gps(arr, nameList + n);
		}
		return "";
	}
	@Override
	public String[] getNames(int type) {
		ArrayList<String> a = new ArrayList<String>();
		int[] t = locateType(type);
		if (t != null) for (int i=0; i<t[1]; i++) {
			int n = KSFLUtilities.getShort(arr, t[2]+12*i+2);
			if (n < 0) a.add("");
			else a.add(gps(arr, nameList + n));
		}
		return a.toArray(new String[0]);
	}
	
	@Override
	public synchronized short getNextAvailableID(int type, short start) {
		ArrayList<Short> a = new ArrayList<Short>();
		int[] t = locateType(type);
		if (t != null) for (int i=0; i<t[1]; i++) a.add(KSFLUtilities.getShort(arr, t[2]+12*i));
		short next = start;
		while (a.contains(next)) next++;
		return next;
	}
	
	@Override
	public synchronized String getNameFromID(int type, short id) {
		int[] l = locate(type,id);
		if (l != null) {
			return (l[4]>0)?gps(arr, l[4]):"";
		}
		return "";
	}
	
	@Override
	public synchronized short getIDFromName(int type, String name) {
		int[] l = locate(type,name);
		if (l != null) {
			return KSFLUtilities.getShort(arr, l[3]);
		}
		return 0;
	}
}
