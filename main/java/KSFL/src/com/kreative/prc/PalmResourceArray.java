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

import java.util.ArrayList;
import java.util.Iterator;
import com.kreative.ksfl.KSFLUtilities;

/**
 * The <code>PalmResourceArray</code> class represents a Palm OS resource database in an array of bytes.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class PalmResourceArray extends PalmResourceProvider {
	private byte[] arr;
	
	private int[] locate(int type, short id) {
		// 0 - number of resources
		// 1 - offset to header
		// 2 - length of header
		// 3 - offset to data
		// 4 - length of data
		int n = KSFLUtilities.getShort(arr, 0x4C);
		for (int i=0, h=0x4E; i<n; i++, h+=10) {
			int t = KSFLUtilities.getInt(arr, h);
			short d = KSFLUtilities.getShort(arr, h+4);
			int o = KSFLUtilities.getInt(arr, h+6);
			if (t == type && d == id) {
				int o2 = (i == n-1)?(arr.length):KSFLUtilities.getInt(arr, h+16);
				return new int[]{ n, h, 10, o, o2-o };
			}
		}
		return null;
	}
	
	private int[] locateend() {
		int n = KSFLUtilities.getShort(arr, 0x4C);
		return new int[] { n, 0x4E+10*n, 10, arr.length, 0 };
	}
	
	private static final int INSERTED_OBJECT_RECORD = 2;
	private static final int INSERTED_DATA = 4;
	private static final int REMOVED_OBJECT_RECORD = 2;
	private static final int REMOVED_DATA = 4;
	
	private void cut(int[] loc, int offset, int length, int what, int type, short id) {
		arr = KSFLUtilities.cut(arr, offset, length);
		//update location
		if (loc != null) {
			if (what == REMOVED_OBJECT_RECORD) loc[0]--;
			if (loc[1] > offset) loc[1] -= length;
			if (loc[3] > offset) loc[3] -= length;
		}
		//update header
		short n = KSFLUtilities.getShort(arr, 0x4C);
		if (what == REMOVED_OBJECT_RECORD) KSFLUtilities.putShort(arr, 0x4C, --n);
		//update records
		for (int i=0, h=0x4E; i<n; i++, h+=10) {
			int o = KSFLUtilities.getInt(arr, h+6);
			if (o > offset) KSFLUtilities.putInt(arr, h+6, o - length);
		}
	}
	
	private void paste(int[] loc, int offset, byte[] stuff, int what, int type, short id) {
		boolean found = false;
		arr = KSFLUtilities.paste(arr, offset, stuff);
		//update location
		if (loc != null) {
			if (what == INSERTED_OBJECT_RECORD) loc[0]++;
			if ((loc[1] > offset) || (loc[1] == offset && what != INSERTED_OBJECT_RECORD)) loc[1] += stuff.length;
			if ((loc[3] > offset) || (loc[3] == offset && what != INSERTED_DATA)) loc[3] += stuff.length;
		}
		//update header
		short n = KSFLUtilities.getShort(arr, 0x4C);
		if (what == INSERTED_OBJECT_RECORD) KSFLUtilities.putShort(arr, 0x4C, ++n);
		//update records
		for (int i=0, h=0x4E; i<n; i++, h+=10) {
			int t = KSFLUtilities.getInt(arr, h);
			short d = KSFLUtilities.getShort(arr, h+4);
			int o = KSFLUtilities.getInt(arr, h+6);
			if ((o > offset) || (o == offset && !(what == INSERTED_DATA && !found))) KSFLUtilities.putInt(arr, h+6, o + stuff.length);
			found = found || (type == t && id == d);
		}
	}
	
	/**
	 * Creates a new <code>PalmResourceArray</code> containing no resources.
	 */
	public PalmResourceArray() {
		arr = new byte[0x50];
		KSFLUtilities.putShort(arr, 0x20, (short)0x01);
		KSFLUtilities.putShort(arr, 0x22, (short)0x01);
		KSFLUtilities.putInt(arr, 0x3C, 0x6170706C);
	}
	
	/**
	 * Creates a <code>PalmResourceArray</code> wrapped around the specified byte array.
	 * The array should not be modified or read after this call.
	 * <p>
	 * No verification of the integrity of the data is performed.
	 * If the data does not contain a PRC structure, calls to the
	 * constructed <code>PalmResourceArray</code> will throw <code>ArrayIndexOutOfBoundsException</code>s.
	 * @param stuff a byte array containing a PRC file.
	 */
	public PalmResourceArray(byte[] stuff) {
		arr = stuff;
	}
	
	/**
	 * Returns the byte array this <code>PalmResourceArray</code> is wrapped around.
	 * The array should not be modified after this call.
	 * Subsequent calls to this method may not return the same array.
	 * @return a byte array containing this PRC structure.
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
	public synchronized byte[] getPRCHeader() {
		byte[] h = new byte[0x4E];
		for (int i=0; i<h.length && i<arr.length; i++) h[i] = arr[i];
		return h;
	}
	
	@Override
	public synchronized void setPRCHeader(byte[] header) {
		for (int i=0; i<0x4C && i<header.length && i<arr.length; i++) arr[i] = header[i];
	}
	
	@Override
	public synchronized boolean add(PalmResource r) throws PalmResourceAlreadyExistsException {
		if (locate(r.type,r.id) != null) throw new PalmResourceAlreadyExistsException();
		int[] loc = locateend();
		byte[] rcd = new byte[10];
		KSFLUtilities.putInt(rcd, 0, r.type);
		KSFLUtilities.putShort(rcd, 4, r.id);
		KSFLUtilities.putInt(rcd, 6, loc[3]);
		paste(loc, loc[1], rcd, INSERTED_OBJECT_RECORD, r.type, r.id);
		paste(loc, loc[3], r.data, INSERTED_DATA, r.type, r.id);
		return true;
	}
	
	@Override
	public synchronized boolean contains(int type, short id) {
		return (locate(type,id) != null);
	}
	
	@Override
	public synchronized PalmResource get(int type, short id) {
		int[] l = locate(type,id);
		if (l != null) {
			return new PalmResource(type, id, KSFLUtilities.copy(arr, l[3], l[4]));
		}
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
		int[] l = locate(type,id);
		if (l != null) {
			return KSFLUtilities.copy(arr, l[3], l[4]);
		}
		return null;
	}
	
	@Override
	public synchronized boolean remove(int type, short id) {
		int[] loc = locate(type,id);
		if (loc != null) {
			cut(loc, loc[3], loc[4], REMOVED_DATA, type, id);
			cut(loc, loc[1], loc[2], REMOVED_OBJECT_RECORD, type, id);
			return true;
		}
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
				KSFLUtilities.putInt(arr, loc[1], r.type);
				KSFLUtilities.putShort(arr, loc[1]+4, r.id);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public synchronized boolean setData(int type, short id, byte[] data) {
		int[] loc = locate(type,id);
		if (loc != null) {
			cut(loc, loc[3], loc[4], REMOVED_DATA, type, id);
			paste(loc, loc[3], data, INSERTED_DATA, type, id);
			return true;
		}
		return false;
	}
	
	@Override
	public synchronized int getTypeCount() {
		int cnt = 0;
		ArrayList<Integer> a = new ArrayList<Integer>();
		int n = KSFLUtilities.getShort(arr, 0x4C);
		for (int i=0, h=0x4E; i<n; i++, h+=10) {
			int t = KSFLUtilities.getInt(arr, h);
			if (!a.contains(t)) {
				cnt++;
				a.add(t);
			}
		}
		return cnt;
	}
	@Override
	public synchronized int getType(int index) {
		int idx = 0;
		ArrayList<Integer> a = new ArrayList<Integer>();
		int n = KSFLUtilities.getShort(arr, 0x4C);
		for (int i=0, h=0x4E; i<n; i++, h+=10) {
			int t = KSFLUtilities.getInt(arr, h);
			if (!a.contains(t)) {
				if (idx == index) return t;
				else {
					idx++;
					a.add(t);
				}
			}
		}
		return 0;
	}
	@Override
	public synchronized int[] getTypes() {
		ArrayList<Integer> a = new ArrayList<Integer>();
		int n = KSFLUtilities.getShort(arr, 0x4C);
		for (int i=0, h=0x4E; i<n; i++, h+=10) {
			int t = KSFLUtilities.getInt(arr, h);
			if (!a.contains(t)) a.add(t);
		}
		int[] s = new int[a.size()];
		Iterator<Integer> i = a.iterator();
		for (int j=0; j<s.length && i.hasNext(); j++) s[j] = i.next();
		return s;
	}
	
	@Override
	public synchronized int getResourceCount(int type) {
		int cnt = 0;
		int n = KSFLUtilities.getShort(arr, 0x4C);
		for (int i=0, h=0x4E; i<n; i++, h+=10) {
			int t = KSFLUtilities.getInt(arr, h);
			if (t == type) cnt++;
		}
		return cnt;
	}
	@Override
	public synchronized short getID(int type, int index) {
		int idx = 0;
		int n = KSFLUtilities.getShort(arr, 0x4C);
		for (int i=0, h=0x4E; i<n; i++, h+=10) {
			int t = KSFLUtilities.getInt(arr, h);
			short d = KSFLUtilities.getShort(arr, h+4);
			if (t == type) {
				if (idx == index) return d;
				else idx++;
			}
		}
		return 0;
	}
	@Override
	public synchronized short[] getIDs(int type) {
		ArrayList<Short> a = new ArrayList<Short>();
		int n = KSFLUtilities.getShort(arr, 0x4C);
		for (int i=0, h=0x4E; i<n; i++, h+=10) {
			int t = KSFLUtilities.getInt(arr, h);
			short d = KSFLUtilities.getShort(arr, h+4);
			if (t == type) a.add(d);
		}
		short[] s = new short[a.size()];
		Iterator<Short> i = a.iterator();
		for (int j=0; j<s.length && i.hasNext(); j++) s[j] = i.next();
		return s;
	}
	
	@Override
	public synchronized short getNextAvailableID(int type, short start) {
		ArrayList<Short> a = new ArrayList<Short>();
		int n = KSFLUtilities.getShort(arr, 0x4C);
		for (int i=0, h=0x4E; i<n; i++, h+=10) {
			int t = KSFLUtilities.getInt(arr, h);
			short d = KSFLUtilities.getShort(arr, h+4);
			if (t == type) a.add(d);
		}
		while (a.contains(start)) start++;
		return start;
	}
}
