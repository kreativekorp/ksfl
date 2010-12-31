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

package com.kreative.pe;

import java.io.UnsupportedEncodingException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class PEResourceDirectory extends PEResourceEntry {
	private static final long serialVersionUID = 1L;
	public int characteristics;
	public int timeStamp;
	public int resourceVersion;
	public List<PEResourceEntry> entries = new ArrayList<PEResourceEntry>();
	
	public PEResourceDirectory() {
		super();
		this.characteristics = 0;
		this.timeStamp = 0;
		this.resourceVersion = 0;
	}
	
	public PEResourceDirectory(int id, int chars, int time, int vers) {
		super(id);
		this.characteristics = chars;
		this.timeStamp = time;
		this.resourceVersion = vers;
	}
	
	public PEResourceDirectory(String name, int chars, int time, int vers) {
		super(name);
		this.characteristics = chars;
		this.timeStamp = time;
		this.resourceVersion = vers;
	}
	
	public PEResourceDirectory(int id, String name, int chars, int time, int vers) {
		super(id, name);
		this.characteristics = chars;
		this.timeStamp = time;
		this.resourceVersion = vers;
	}
	
	public PEResourceDirectory clone() {
		PEResourceDirectory rd = new PEResourceDirectory(id, name, characteristics, timeStamp, resourceVersion);
		for (PEResourceEntry re : entries) {
			rd.entries.add(re.clone());
		}
		return rd;
	}
	
	private static int alignPart(int a) {
		int m = a % 16;
		if (m == 0) return a;
		else return a - m + 16;
	}
	
	private static int alignResource(int a) {
		int m = a % 8;
		if (m == 0) return a;
		else return a - m + 8;
	}
	
	/*package*/ static void subtractVA(byte[] arr, int l, int va) {
		int namedEntries = getShort(arr, l+12);
		int numbdEntries = getShort(arr, l+14);
		for (int i=0, ll=l+16; i<namedEntries+numbdEntries; i++, ll+=8) {
			int of = getInt(arr, ll+4);
			if (of < 0) {
				subtractVA(arr, (of & 0x7FFFFFFF), va);
			} else {
				putInt(arr, of, getInt(arr, of)-va);
			}
		}
	}
	
	/*package*/ static void addVA(byte[] arr, int l, int va) {
		int namedEntries = getShort(arr, l+12);
		int numbdEntries = getShort(arr, l+14);
		for (int i=0, ll=l+16; i<namedEntries+numbdEntries; i++, ll+=8) {
			int of = getInt(arr, ll+4);
			if (of < 0) {
				addVA(arr, (of & 0x7FFFFFFF), va);
			} else {
				putInt(arr, of, getInt(arr, of)+va);
			}
		}
	}
	
	public void decompile(byte[] arr, int l) {
//		System.out.println(l+" "+Long.toHexString(l).toUpperCase()+" DIRENTRY");
		characteristics = getInt(arr, l+0);
		timeStamp = getInt(arr, l+4);
		resourceVersion = ((getShort(arr, l+8) & 0xFFFF) << 16) | (getShort(arr, l+10) & 0xFFFF);
		int namedEntries = getShort(arr, l+12);
		int numbdEntries = getShort(arr, l+14);
		entries.clear();
		for (int i=0, ll=l+16; i<namedEntries+numbdEntries; i++, ll+=8) {
			int id = getInt(arr, ll+0);
			int of = getInt(arr, ll+4);
			PEResourceEntry e;
			if (of < 0) {
				e = new PEResourceDirectory();
			} else {
				e = new PEResourceData();
			}
			e.id = id;
			if (id < 0) {
//				System.out.println((id & 0x7FFFFFFF)+" "+Long.toHexString(id & 0x7FFFFFFF).toUpperCase()+" STRING");
				int nl = (getShort(arr, (id & 0x7FFFFFFF)) & 0xFFFF) << 1;
				try {
					e.name = new String(arr, (id & 0x7FFFFFFF)+2, nl, "UTF-16LE");
				} catch (UnsupportedEncodingException ee) {
					e.name = new String(arr, (id & 0x7FFFFFFF)+2, nl);
				}
			} else {
				e.name = null;
			}
			if (of < 0) {
				((PEResourceDirectory)e).decompile(arr, (of & 0x7FFFFFFF));
			} else {
//				System.out.println(of+" "+Long.toHexString(of).toUpperCase()+" DATAENTRY");
				PEResourceData de = (PEResourceData)e;
				int dof = getInt(arr, of+0);
				int lof = getInt(arr, of+4);
//				System.out.println(dof+" "+Long.toHexString(dof).toUpperCase()+" RESDATA");
				de.data = copy(arr, dof, lof);
				de.codePage = getInt(arr, of+8);
				de.reserved = getInt(arr, of+12);
			}
			entries.add(e);
		}
	}
	
	private int calculateResDirLength() {
		int l = 16+8*entries.size();
		Iterator<PEResourceEntry> i = entries.iterator();
		while (i.hasNext()) {
			PEResourceEntry e = i.next();
			if (e instanceof PEResourceDirectory) {
				l += ((PEResourceDirectory)e).calculateResDirLength();
			}
		}
		return l;
	}
	
	private int calculateResDataLength() {
		int l = 0;
		Iterator<PEResourceEntry> i = entries.iterator();
		while (i.hasNext()) {
			PEResourceEntry e = i.next();
			if (e instanceof PEResourceDirectory) {
				l += ((PEResourceDirectory)e).calculateResDataLength();
			} else if (e instanceof PEResourceData) {
				l += 16;
			}
		}
		return l;
	}
	
	private int calculateStringLength() {
		int l = 0;
		Iterator<PEResourceEntry> i = entries.iterator();
		while (i.hasNext()) {
			PEResourceEntry e = i.next();
			if (e.name != null) l += alignResource(e.name.length()*2 + 2);
			if (e instanceof PEResourceDirectory) {
				l += ((PEResourceDirectory)e).calculateStringLength();
			}
		}
		return l;
	}
	
	private int calculateDataLength() {
		int l = 0;
		Iterator<PEResourceEntry> i = entries.iterator();
		while (i.hasNext()) {
			PEResourceEntry e = i.next();
			if (e instanceof PEResourceDirectory) {
				l += ((PEResourceDirectory)e).calculateDataLength();
			} else if (e instanceof PEResourceData) {
				l += alignResource(((PEResourceData)e).data.length);
			}
		}
		return l;
	}
	
	public int calculateTotalLength() {
		return alignPart(calculateResDirLength()+calculateResDataLength())
			 + alignPart(calculateStringLength())
			 + alignPart(calculateDataLength());
	}
	
	public void recompile(byte[] arr, int l) {
		int dl = calculateResDirLength();
		int ddl = calculateResDataLength();
		int sl = calculateStringLength();
		int[] loc = new int[4];
		loc[0] = l;
		loc[1] = l+dl;
		loc[2] = l+alignPart(dl+ddl);
		loc[3] = l+alignPart(dl+ddl)+alignPart(sl);
		recompile(arr, loc);
	}
	
	private void recompile(byte[] arr, int[] l) {
		// l[0] is where directories go
		// l[1] is where data descriptions go
		// l[2] is where strings go
		// l[3] is where raw data goes
		List<PEResourceEntry> namedEntries = new ArrayList<PEResourceEntry>();
		List<PEResourceEntry> numbdEntries = new ArrayList<PEResourceEntry>();
		Iterator<PEResourceEntry> it = entries.iterator();
		while (it.hasNext()) {
			PEResourceEntry en = it.next();
			if (en.name != null) namedEntries.add(en);
			else numbdEntries.add(en);
		}
		putInt(arr, l[0]+0, characteristics);
		putInt(arr, l[0]+4, timeStamp);
		putShort(arr, l[0]+8, (short)((resourceVersion >>> 16) & 0xFFFF));
		putShort(arr, l[0]+10, (short)(resourceVersion & 0xFFFF));
		putShort(arr, l[0]+12, (short)namedEntries.size());
		putShort(arr, l[0]+14, (short)numbdEntries.size());
		int es = l[0]+16;
		l[0] += 16+8*entries.size();
		it = namedEntries.iterator();
		while (it.hasNext()) {
			PEResourceEntry en = it.next();
			if (en.name != null) {
				putInt(arr, es+0, l[2] | 0x80000000);
				putShort(arr, l[2], (short)en.name.length()); l[2] += 2;
				CharacterIterator ci = new StringCharacterIterator(en.name);
				for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next()) {
					putShort(arr, l[2], (short)ch); l[2] += 2;
				}
				l[2] = alignResource(l[2]);
			} else {
				putInt(arr, es+0, en.id);
			}
			if (en instanceof PEResourceDirectory) {
				putInt(arr, es+4, l[0] | 0x80000000);
				((PEResourceDirectory)en).recompile(arr, l);
			} else {
				PEResourceData ed = (PEResourceData)en;
				putInt(arr, es+4, l[1]);
				putInt(arr, l[1]+0, l[3]);
				putInt(arr, l[1]+4, ed.data.length);
				putInt(arr, l[1]+8, ed.codePage);
				putInt(arr, l[1]+12, ed.reserved);
				for (int s=0, d=l[3]; s < ed.data.length && d < arr.length; s++, d++) arr[d] = ed.data[s];
				l[1] += 16;
				l[3] += alignResource(ed.data.length);
			}
			es += 8;
		}
		it = numbdEntries.iterator();
		while (it.hasNext()) {
			PEResourceEntry en = it.next();
			if (en.name != null) {
				putInt(arr, es+0, l[2] | 0x80000000);
				putShort(arr, l[2], (short)en.name.length()); l[2] += 2;
				CharacterIterator ci = new StringCharacterIterator(en.name);
				for (char ch = ci.first(); ch != CharacterIterator.DONE; ch = ci.next(), l[2] += 2) {
					putShort(arr, l[2], (short)ch);
				}
				l[2] = alignResource(l[2]);
			} else {
				putInt(arr, es+0, en.id);
			}
			if (en instanceof PEResourceDirectory) {
				putInt(arr, es+4, l[0] | 0x80000000);
				((PEResourceDirectory)en).recompile(arr, l);
			} else {
				PEResourceData ed = (PEResourceData)en;
				putInt(arr, es+4, l[1]);
				putInt(arr, l[1]+0, l[3]);
				putInt(arr, l[1]+4, ed.data.length);
				putInt(arr, l[1]+8, ed.codePage);
				putInt(arr, l[1]+12, ed.reserved);
				for (int s=0, d=l[3]; s < ed.data.length && d < arr.length; s++, d++) arr[d] = ed.data[s];
				l[1] += 16;
				l[3] += alignResource(ed.data.length);
			}
			es += 8;
		}
	}
	
	private static short getShort(byte[] b, int i) {
		return (short)(((b[i+1] & 0xFF) << 8) | (b[i] & 0xFF));
	}
	
	private static int getInt(byte[] b, int i) {
		return (int)(((b[i+3] & 0xFF) << 24) | ((b[i+2] & 0xFF) << 16) | ((b[i+1] & 0xFF) << 8) | (b[i] & 0xFF));
	}
	
	private static void putShort(byte[] b, int i, short s) {
		b[i+1] = (byte)((s >> 8) & 0xFF);
		b[i] = (byte)(s & 0xFF);
	}
	
	private static void putInt(byte[] b, int i, int s) {
		b[i+3] = (byte)((s >> 24) & 0xFF);
		b[i+2] = (byte)((s >> 16) & 0xFF);
		b[i+1] = (byte)((s >> 8) & 0xFF);
		b[i] = (byte)(s & 0xFF);
	}
	
	private static byte[] copy(byte[] arr, int offset, int bytesToCopy) {
		byte[] stuff = new byte[bytesToCopy];
		for (int s=offset, d=0; d<bytesToCopy && s<arr.length; s++, d++) {
			stuff[d] = arr[s];
		}
		return stuff;
	}
}
