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

package com.kreative.dff;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import com.kreative.ksfl.KSFLUtilities;

/**
 * The <code>DFFResourceFile</code> class provides a DFF interface
 * to a <code>RandomAccessFile</code> in DFF format.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class DFFResourceFile extends DFFResourceProvider {
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
	private int version;
	private boolean sb;
	private int d2objectCount;
	private long d2headerSpace, d2nameSpace, d2dataSpace;
	private String textEncoding = "UTF-8";
	
	private String ns(byte[] a) {
		try {
			return new String(a,textEncoding);
		} catch (java.io.UnsupportedEncodingException uue) {
			return new String(a);
		}
	}
	
	private byte[] gb(String a) {
		try {
			return a.getBytes(textEncoding);
		} catch (java.io.UnsupportedEncodingException uue) {
			return a.getBytes();
		}
	}
	
	private long d1locate(long type, int id) {
		try {
			raf.seek(4L);
			while (raf.getFilePointer()+16L <= raf.length()) {
				long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
				short i = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
				raf.readShort();
				int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
				if (t == type && i == (short)id) return raf.getFilePointer()-16L;
				else raf.skipBytes(l);
			}
		} catch (IOException ioe) {}
		return 0L;
	}
	
	private long[] d2locate(long type, int id) {
		int i = 0;
		long hi = d2headerSpace;
		long ni = d2nameSpace;
		long di = d2dataSpace;
		try {
			while (i < d2objectCount && hi < d2nameSpace && ni < d2dataSpace && di < raf.length()) {
				raf.seek(hi);
				long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
				short id1 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
				raf.readShort();
				raf.readShort();
				short id2 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
				int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
				raf.seek(ni);
				int nl = (raf.readByte() & 0xFF);
				if (t == type && (id1 & 0xFFFF) == (id & 0xFFFF) && ((id2 << 16) & 0xFFFF0000) == (id & 0xFFFF0000)) {
					return new long[]{hi,ni,di,20L,(long)nl+1L,(long)l};
				} else {
					di += l;
					ni += 1L+(long)nl;
					hi += 20;
					i++;
				}
			}
		} catch (IOException ioe) {}
		return null;
	}
	
	private long[] d2locate(long type, String name) {
		int i = 0;
		long hi = d2headerSpace;
		long ni = d2nameSpace;
		long di = d2dataSpace;
		try {
			while (i < d2objectCount && hi < d2nameSpace && ni < d2dataSpace && di < raf.length()) {
				raf.seek(hi);
				long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
				raf.readShort();
				raf.readShort();
				raf.readShort();
				raf.readShort();
				int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
				raf.seek(ni);
				int nl = (raf.readByte() & 0xFF);
				byte[] nb = new byte[nl];
				raf.read(nb);
				String n = ns(nb);
				if (t == type && n.equals(name)) {
					return new long[]{hi,ni,di,20L,(long)nl+1L,(long)l};
				} else {
					di += l;
					ni += 1L+(long)nl;
					hi += 20;
					i++;
				}
			}
		} catch (IOException ioe) {}
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
	
	private void d3adjustTypeMap(long offset, long length, int whatInserted, long typeInserted, int idInserted) throws IOException {
		// adjust the type map
		raf.seek(4);
		int typeCnt = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
		for (int ti=0; ti<typeCnt; ti++) {
			long thisType = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
			raf.readInt();
			int objTblOfst = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
			if ((long)objTblOfst > offset || ((long)objTblOfst == offset && length > 0L && !(whatInserted == INSERTED_OBJECT_RECORD && typeInserted == thisType))) {
				if ((long)objTblOfst+length > (long)Integer.MAX_VALUE)
					throw new DFFResourceTooBigException("Map too big. Map now corrupted. You're screwed.");
				else {
					raf.seek(raf.getFilePointer()-4);
					raf.writeInt(sb?Integer.reverseBytes((int)(objTblOfst+length)):(int)(objTblOfst+length));
				}
			}
		}
	}
	
	private void d3adjustObjectMaps(long offset, long length, int whatInserted, long typeInserted, int idInserted) throws IOException {
		// adjust the object maps
		raf.seek(4);
		int typeCnt = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
		for (int ti=0; ti<typeCnt; ti++) {
			raf.seek(8L+16L*ti);
			long thisType = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
			int objCnt = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
			int objTblOfst = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
			raf.seek((long)objTblOfst);
			for (int oi=0; oi<objCnt; oi++) {
				int thisID = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
				raf.readInt();
				int nameOfst = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
				if ((nameOfst > 0) && (nameOfst > offset || (nameOfst == offset && length > 0L && !(whatInserted == INSERTED_NAME && typeInserted == thisType && idInserted == thisID)))) {
					if ((long)nameOfst+length > (long)Integer.MAX_VALUE)
						throw new DFFResourceTooBigException("Map too big. Map now corrupted. You're screwed.");
					else {
						raf.seek(raf.getFilePointer()-4);
						raf.writeInt(sb?Integer.reverseBytes((int)(nameOfst+length)):(int)(nameOfst+length));
					}
				}
				long dataOfst = sb?KSFLUtilities.reverseUInt48(KSFLUtilities.readUInt48(raf)):KSFLUtilities.readUInt48(raf);
				if ((dataOfst > 0) && (dataOfst > offset || (dataOfst == offset && length > 0L && !(whatInserted == INSERTED_DATA && typeInserted == thisType && idInserted == thisID)))) {
					if (dataOfst+length > KSFLUtilities.UINT48_MAX_VALUE)
						throw new DFFResourceTooBigException("Map too big. Map now corrupted. You're screwed.");
					else {
						raf.seek(raf.getFilePointer()-6);
						KSFLUtilities.writeUInt48(raf, sb?KSFLUtilities.reverseUInt48(dataOfst+length):(dataOfst+length));
					}
				}
				KSFLUtilities.readUInt48(raf);
			}
		}
	}
	
	private void d3adjustMetrics(long[] a, long offset, long length, int whatInserted) {
		if (a[1] > offset || (a[1] == offset && length > 0L && whatInserted != INSERTED_TYPE_RECORD)) a[1] += length;
		if (a[3] > offset || (a[3] == offset && length > 0L && whatInserted != INSERTED_OBJECT_RECORD)) a[3] += length;
		if (a[4] > offset || (a[4] == offset && length > 0L && whatInserted != INSERTED_OBJECT_RECORD)) a[4] += length;
		if (a[5] > offset || (a[5] == offset && length > 0L && whatInserted != INSERTED_NAME)) a[5] += length;
		if (a[6] > offset || (a[6] == offset && length > 0L && whatInserted != INSERTED_NAME)) a[6] += length;
		if (a[7] > offset || (a[7] == offset && length > 0L && whatInserted != INSERTED_DATA)) a[7] += length;
		if (a[8] > offset || (a[8] == offset && length > 0L && whatInserted != INSERTED_DATA)) a[8] += length;
	}
	
	private void d3adjustLocation(long[] a, long offset, long length, int whatInserted) {
		if (a[0] > offset || (a[0] == offset && length > 0L && whatInserted != INSERTED_TYPE_RECORD)) a[0] += length;
		if (a[3] > offset || (a[3] == offset && length > 0L && whatInserted != INSERTED_OBJECT_RECORD)) a[3] += length;
		if (a[5] > offset || (a[5] == offset && length > 0L && whatInserted != INSERTED_OBJECT_RECORD)) a[5] += length;
		if (a[7] > offset || (a[7] == offset && length > 0L && whatInserted != INSERTED_NAME)) a[7] += length;
		if (a[9] > offset || (a[9] == offset && length > 0L && whatInserted != INSERTED_DATA)) a[9] += length;
	}
	
	private long[] d3metrics() throws IOException {
		// a[0] = number of types in type table
		// a[1] = offset of type table
		// a[2] = length of type table
		// a[3] = lowest offset of any object table
		// a[4] = highest offset of any object table + that object table's length
		// a[5] = lowest offset of any name
		// a[6] = highest offset of any name + that name's length + 1
		// a[7] = lowest offset of any data
		// a[8] = highest offset of any data + that data's length
		long objTblMin=Long.MAX_VALUE, objTblMax=0;
		long nameMin=Long.MAX_VALUE, nameMax=0;
		long dataMin=Long.MAX_VALUE, dataMax=0;
		raf.seek(4);
		int typeCnt = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
		for (int ti=0; ti<typeCnt; ti++) {
			raf.seek(8L+16L*ti+8);
			int objCnt = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
			int objTblOfst = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
			if (objTblOfst > 0) {
				if (objTblOfst < objTblMin) objTblMin = objTblOfst;
				if (objTblOfst+24*objCnt > objTblMax) objTblMax = objTblOfst+24*objCnt;
			}
			for (int oi=0; oi<objCnt; oi++) {
				raf.seek((long)objTblOfst+24L*oi+8);
				int nameOfst = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
				long dataOfst = sb?KSFLUtilities.reverseUInt48(KSFLUtilities.readUInt48(raf)):KSFLUtilities.readUInt48(raf);
				long dataLen = sb?KSFLUtilities.reverseUInt48(KSFLUtilities.readUInt48(raf)):KSFLUtilities.readUInt48(raf);
				if (nameOfst > 0) {
					raf.seek((long)nameOfst);
					int nameLen = (raf.readByte()&0xFF)+1;
					if (nameOfst < nameMin) nameMin = nameOfst;
					if (nameOfst+nameLen > nameMax) nameMax = nameOfst+nameLen;
				}
				if (dataOfst > 0) {
					if (dataOfst < dataMin) dataMin = dataOfst;
					if (dataOfst+dataLen > dataMax) dataMax = dataOfst+dataLen;
				}
			}
		}
		if (objTblMin > objTblMax) {
			objTblMin = objTblMax = 8L + typeCnt*16L;
		}
		if (nameMin > nameMax) {
			nameMin = nameMax = objTblMax;
		}
		if (dataMin > dataMax) {
			dataMin = dataMax = nameMax;
		}
		return new long[]{(long)typeCnt,8L,(long)typeCnt*16L,objTblMin,objTblMax,nameMin,nameMax,dataMin,dataMax};
	}
	
	private long[] d3getType(long type) throws IOException {
		// a[0] = offset of type record
		// a[1] = length of type record (16)
		// a[2] = number of objects in object table
		// a[3] = offset of object table
		// a[4] = length of object table
		raf.seek(4);
		int typeCnt = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
		for (int ti=0; ti<typeCnt; ti++) {
			long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
			int objCnt = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
			int objTblOfst = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
			if (t == type) return new long[]{raf.getFilePointer()-16L,16L,(long)objCnt,objTblOfst,24L*objCnt};
		}
		return null;
	}
	
	private long[] d3locate(long type, int id) throws IOException {
		//  a[0] = offset of type record
		//  a[1] = length of type record (16)
		//  a[2] = number of objects in object table
		//  a[3] = offset of object table
		//  a[4] = length of object table
		//  a[5] = offset of object record
		//  a[6] = length of object record (24)
		//  a[7] = offset of name
		//  a[8] = length of name
		//  a[9] = offset of data
		// a[10] = length of data
		long[] ty = d3getType(type);
		if (ty != null) {
			for (int oi=0; oi<ty[2]; oi++) {
				raf.seek(ty[3]+24L*oi);
				int i = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
				if (i == id) {
					raf.readInt();
					int nameOfst = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					long dataOfst = sb?KSFLUtilities.reverseUInt48(KSFLUtilities.readUInt48(raf)):KSFLUtilities.readUInt48(raf);
					long dataLen = sb?KSFLUtilities.reverseUInt48(KSFLUtilities.readUInt48(raf)):KSFLUtilities.readUInt48(raf);
					int nameLen = 0;
					if (nameOfst>0) {
						raf.seek(nameOfst);
						nameLen = ((raf.readByte()&0xFF)+1);
					}
					return new long[]{ty[0],ty[1],ty[2],ty[3],ty[4],ty[3]+24L*oi,24L,(long)nameOfst,(long)nameLen,dataOfst,dataLen};
				}
			}
		}
		return null;
	}
	
	private long[] d3locate(long type, String name) throws IOException {
		//  a[0] = offset of type record
		//  a[1] = length of type record (16)
		//  a[2] = number of objects in object table
		//  a[3] = offset of object table
		//  a[4] = length of object table
		//  a[5] = offset of object record
		//  a[6] = length of object record (24)
		//  a[7] = offset of name
		//  a[8] = length of name
		//  a[9] = offset of data
		// a[10] = length of data
		long[] ty = d3getType(type);
		if (ty != null) {
			for (int oi=0; oi<ty[2]; oi++) {
				raf.seek(ty[3]+24L*oi+8L);
				int nameOfst = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
				if (nameOfst>0) {
					raf.seek(nameOfst);
					int nameLen = ((raf.readByte()&0xFF)+1);
					byte[] b = new byte[nameLen-1];
					raf.read(b);
					String n = ns(b);
					if (n.equals(name)) {
						raf.seek(ty[3]+24L*oi+12);
						long dataOfst = sb?KSFLUtilities.reverseUInt48(KSFLUtilities.readUInt48(raf)):KSFLUtilities.readUInt48(raf);
						long dataLen = sb?KSFLUtilities.reverseUInt48(KSFLUtilities.readUInt48(raf)):KSFLUtilities.readUInt48(raf);
						return new long[]{ty[0],ty[1],ty[2],ty[3],ty[4],ty[3]+24L*oi,24L,(long)nameOfst,(long)nameLen,dataOfst,dataLen};
					}
				}
			}
		}
		return null;
	}
	
	private void d3cut(long[] metrics, long[] loc, long offset, long length, int what, long type, int id) throws IOException {
		KSFLUtilities.cut(raf, offset, length);
		d3adjustTypeMap(offset, -length, what, type, id);
		d3adjustObjectMaps(offset, -length, what, type, id);
		if (loc != null) d3adjustLocation(loc, offset, -length, what);
		if (metrics != null) d3adjustMetrics(metrics, offset, -length, what);
	}
	
	private void d3paste(long[] metrics, long[] loc, long offset, byte[] stuff, int what, long type, int id) throws IOException {
		KSFLUtilities.paste(raf, offset, stuff);
		d3adjustTypeMap(offset, stuff.length, what, type, id);
		d3adjustObjectMaps(offset, stuff.length, what, type, id);
		if (loc != null) d3adjustLocation(loc, offset, stuff.length, what);
		if (metrics != null) d3adjustMetrics(metrics, offset, stuff.length, what);
	}
	
	private void d3paste(long[] metrics, long[] loc, long offset, long length, int what, long type, int id) throws IOException {
		KSFLUtilities.paste(raf, offset, length);
		d3adjustTypeMap(offset, length, what, type, id);
		d3adjustObjectMaps(offset, length, what, type, id);
		if (loc != null) d3adjustLocation(loc, offset, length, what);
		if (metrics != null) d3adjustMetrics(metrics, offset, length, what);
	}
	
	/**
	 * Creates a <code>DFFResourceFile</code> around a file.
	 * The file will not be modified until the DFF structure is modified.
	 * @param f the file object.
	 * @param mode the access mode, as described by <code>RandomAccessFile(File, String)</code>.
	 * @param create <code>CREATE_ALWAYS</code> if a new resource structure should be created, <code>CREATE_IF_EMPTY</code> if a new resource should be created if the file is empty, <code>CREATE_NEVER</code> if the file should not be modified.
	 * @param version the DFF version to use; one of 1, 2, or 3.
	 * @param le true if the DFF structure should use little-endian fields, false for big-endian.
	 * @throws NotADFFFileException if the file does not start with any of the magic numbers for DFF files.
	 * @throws IOException if an I/O error occurs.
	 */
	public DFFResourceFile(File f, String mode, int create, int version, boolean le) throws IOException {
		raf = new RandomAccessFile(f, mode);
		readOnly = (mode.equalsIgnoreCase("r"));
		if ((create == CREATE_ALWAYS) || ((create == CREATE_IF_EMPTY) && (raf.length() == 0))) {
			this.version = version;
			this.sb = le;
			switch (version) {
			case 1:
				raf.seek(0l);
				raf.setLength(0l);
				raf.writeInt(MAGIC_NUMBER_DFF1);
				break;
			case 2:
				raf.seek(0l);
				raf.setLength(0l);
				raf.writeInt(MAGIC_NUMBER_DFF2);
				raf.writeInt(0);
				d2objectCount = 0;
				d2headerSpace = d2nameSpace = d2dataSpace = 8l;
				break;
			case 3:
				raf.seek(0l);
				raf.setLength(0l);
				raf.writeInt(MAGIC_NUMBER_DFF3);
				raf.writeInt(0);
				break;
			default:
				throw new NotADFFFileException("Invalid version number: "+version);
			}
		} else {
			if (raf.length() < 4) throw new NotADFFFileException("File is too small");
			raf.seek(0l);
			int v = raf.readInt();
			switch (v) {
			case MAGIC_NUMBER_DFF1:
				this.version = 1;
				this.sb = false;
				break;
			case MAGIC_NUMBER_DFF1R:
				this.version = 1;
				this.sb = true;
				break;
			case MAGIC_NUMBER_DFF2:
				if (raf.length() < 8) throw new NotADFFFileException("File is too small");
				this.version = 2;
				this.sb = false;
				raf.seek(4l);
				d2objectCount = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
				d2headerSpace = 8l;
				d2nameSpace = d2headerSpace+20l*d2objectCount;
				d2dataSpace = d2nameSpace;
				raf.seek(d2nameSpace);
				for (int nh2 = d2objectCount; nh2 > 0; nh2--) {
					int sl = ((int)raf.readByte() & (int)0xFF);
					d2dataSpace += sl+1;
					raf.skipBytes(sl);
				}
				break;
			case MAGIC_NUMBER_DFF2R:
				if (raf.length() < 8) throw new NotADFFFileException("File is too small");
				this.version = 2;
				this.sb = true;
				raf.seek(4l);
				d2objectCount = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
				d2headerSpace = 8l;
				d2nameSpace = d2headerSpace+20l*d2objectCount;
				d2dataSpace = d2nameSpace;
				raf.seek(d2nameSpace);
				for (int nh2 = d2objectCount; nh2 > 0; nh2--) {
					int sl = ((int)raf.readByte() & (int)0xFF);
					d2dataSpace += sl+1;
					raf.skipBytes(sl);
				}
				break;
			case MAGIC_NUMBER_DFF3:
				if (raf.length() < 8) throw new NotADFFFileException("File is too small");
				this.version = 3;
				this.sb = false;
				break;
			case MAGIC_NUMBER_DFF3R:
				if (raf.length() < 8) throw new NotADFFFileException("File is too small");
				this.version = 3;
				this.sb = true;
				break;
			default:
				throw new NotADFFFileException("Invalid magic number: "+Integer.toHexString(v));
			}
		}
	}
	
	/**
	 * Returns the name of the text encoding used for the names of DFF objects.
	 * Defaults to UTF-8.
	 * @return the name of the text encoding used for the names of DFF objects.
	 */
	public synchronized String getTextEncoding() {
		return textEncoding;
	}
	
	/**
	 * Sets the text encoding used for the names of DFF objects.
	 * Defaults to UTF-8.
	 * @param encoding the name of the text encoding used for the names of DFF objects.
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
	public synchronized boolean add(DFFResource r) throws DFFResourceAlreadyExistsException {
		if (r.name == null) r.name = "";
		if (r.data == null) r.data = new byte[0];
		switch (version) {
		case 1:
			if (d1locate(r.type,r.id) > 0L) {
				throw new DFFResourceAlreadyExistsException();
			} else {
				try {
					long l = raf.length();
					try {
						raf.seek(l);
						raf.writeLong(sb?Long.reverseBytes(r.type):r.type);
						raf.writeShort(sb?Short.reverseBytes((short)r.id):(short)r.id);
						raf.writeShort(0);
						raf.writeInt(sb?Integer.reverseBytes(r.data.length):r.data.length);
						raf.write(r.data);
						return true;
					} catch (IOException ioe2) {
						raf.setLength(l);
					}
				} catch (IOException ioe) {}
			}
			break;
		case 2:
			if (d2locate(r.type,r.id) != null) {
				throw new DFFResourceAlreadyExistsException();
			} else {
				byte[] n = gb(r.name);
				if (n.length > 255) n = KSFLUtilities.cut(n, 255, n.length-255);
				n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
				try {
					KSFLUtilities.paste(raf, d2nameSpace, 20L);
					raf.seek(d2nameSpace);
					raf.writeLong(sb?Long.reverseBytes(r.type):r.type);
					raf.writeShort(sb?Short.reverseBytes((short)(r.id & 0xFFFF)):(short)(r.id & 0xFFFF));
					raf.writeShort(sb?Short.reverseBytes(r.datatype):r.datatype);
					raf.writeShort(sb?Short.reverseBytes(r.getAttributes()):r.getAttributes());
					raf.writeShort(sb?Short.reverseBytes((short)((r.id >> 16) & 0xFFFF)):(short)((r.id >> 16) & 0xFFFF));
					raf.writeInt(sb?Integer.reverseBytes(r.data.length):r.data.length);
					d2nameSpace += 20L;
					d2dataSpace += 20L;
					KSFLUtilities.paste(raf, d2dataSpace, n);
					d2dataSpace += n.length;
					raf.seek(raf.length());
					raf.write(r.data);
					d2objectCount++;
					raf.seek(4L);
					raf.writeInt(sb?Integer.reverseBytes(d2objectCount):d2objectCount);
					return true;
				} catch (IOException ioe) {}
			}
			break;
		case 3:
			try {
				if ((raf.length() + 24L + ((r.name.length() > 0)?((long)gb(r.name).length+1L):0) + ((d3getType(r.type) == null)?(16L):0) + r.data.length) > KSFLUtilities.UINT48_MAX_VALUE) {
					throw new DFFResourceTooBigException();
				} else if (d3locate(r.type,r.id) != null) {
					throw new DFFResourceAlreadyExistsException();
				} else {
					long[] t = d3getType(r.type);
					long[] m = d3metrics();
					if (t == null) {
						if (m[0] == Integer.MAX_VALUE) {
							throw new DFFResourceTooBigException("Too many types.");
						} else {
							byte[] th = new byte[16];
							KSFLUtilities.putLong(th, 0, sb, r.type);
							KSFLUtilities.putInt(th, 8, sb, 0);
							KSFLUtilities.putInt(th, 12, sb, (int)m[4]); //offset to object table

							raf.seek(4);
							raf.writeInt(sb?Integer.reverseBytes((int)m[0]+1):((int)m[0]+1));
							d3paste(m, null, m[1]+m[2], th, INSERTED_TYPE_RECORD, r.type, r.id);
							t = d3getType(r.type);
						}
					} else if (t[2] == Integer.MAX_VALUE) {
						throw new DFFResourceTooBigException("Too many objects of this type.");
					}

					byte[] h = new byte[24];
					KSFLUtilities.putInt(h, 0, sb, r.id);
					KSFLUtilities.putShort(h, 4, sb, r.datatype);
					KSFLUtilities.putShort(h, 6, sb, r.getAttributes());
					KSFLUtilities.putInt(h, 8, sb, (r.name.length() > 0)?(int)m[6]:0); //offset to name
					KSFLUtilities.putUInt48(h, 12, sb, (r.data.length > 0)?m[8]:0); //offset to data
					KSFLUtilities.putUInt48(h, 18, sb, (long)r.data.length);

					raf.seek(t[0]+8);
					raf.writeInt(sb?Integer.reverseBytes((int)t[2]+1):((int)t[2]+1));
					d3paste(m, null, t[3]+t[4], h, INSERTED_OBJECT_RECORD, r.type, r.id);

					if (r.name.length() > 0) {
						byte[] n = gb(r.name);
						if (n.length > 255) n = KSFLUtilities.cut(n, 255, n.length-255);
						n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
						d3paste(m, null, m[6], n, INSERTED_NAME, r.type, r.id);
					}
					if (r.data.length > 0) {
						d3paste(m, null, m[8], r.data, INSERTED_DATA, r.type, r.id);
					}
					return true;
				}
			}
			catch (DFFResourceTooBigException e) { throw e; }
			catch (DFFResourceAlreadyExistsException e) { throw e; }
			catch (IOException ioe) {}
			return false;
		}
		return false;
	}
	
	@Override
	public synchronized boolean contains(long type, int id) {
		switch (version) {
		case 1:
			long d1i = d1locate(type,id);
			return (d1i > 0L);
		case 2:
			long[] d2i = d2locate(type,id);
			return (d2i != null);
		case 3:
			try {
				long[] d3i = d3locate(type,id);
				return (d3i != null);
			} catch (IOException ioe) {}
			break;
		}
		return false;
	}
	@Override
	public synchronized boolean contains(long type, String name) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			long[] d2i = d2locate(type,name);
			return (d2i != null);
		case 3:
			try {
				long[] d3i = d3locate(type,name);
				return (d3i != null);
			} catch (IOException ioe) {}
			break;
		}
		return false;
	}
	
	@Override
	public synchronized DFFResource get(long type, int id) {
		switch (version) {
		case 1:
			long d1i = d1locate(type,id);
			if (d1i > 0L) {
				try {
					raf.seek(d1i);
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					short i = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					raf.readShort();
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					byte[] b = new byte[l];
					raf.read(b);
					return new DFFResource(t,i,b);
				} catch (IOException ioe1) {}
			}
			break;
		case 2:
			long[] d2i = d2locate(type,id);
			if (d2i != null) {
				try {
					raf.seek(d2i[0]);
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					short id1 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short dt = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short f = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short id2 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					int i = (id1 & 0xFFFF) | ((id2 << 16) & 0xFFFF0000);
					raf.seek(d2i[1]);
					int nl = (raf.readByte() & 0xFF);
					byte[] nb = new byte[nl];
					raf.read(nb);
					String n = ns(nb);
					raf.seek(d2i[2]);
					byte[] d = new byte[l];
					raf.read(d);
					return new DFFResource(t,i,dt,f,n,d);
				} catch (IOException ioe1) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,id);
				if (d3i != null) {
					raf.seek(d3i[0]);
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					raf.seek(d3i[5]);
					int i = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					short dt = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short f = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					String n = "";
					if (d3i[7] > 0) {
						byte[] nb = new byte[(int)d3i[8]-1];
						raf.seek(d3i[7]+1);
						raf.read(nb);
						n = ns(nb);
					}
					if (d3i[10] > (long)Integer.MAX_VALUE) {
						throw new DFFResourceTooBigException();
					}
					byte[] d = new byte[(int)d3i[10]];
					if (d3i[9] > 0) {
						raf.seek(d3i[9]);
						raf.read(d);
					}
					return new DFFResource(t,i,dt,f,n,d);
				}
			} catch (IOException ioe) {}
			break;
		}
		return null;
	}
	@Override
	public synchronized DFFResource get(long type, String name) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			long[] d2i = d2locate(type,name);
			if (d2i != null) {
				try {
					raf.seek(d2i[0]);
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					short id1 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short dt = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short f = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short id2 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					int i = (id1 & 0xFFFF) | ((id2 << 16) & 0xFFFF0000);
					raf.seek(d2i[1]);
					int nl = (raf.readByte() & 0xFF);
					byte[] nb = new byte[nl];
					raf.read(nb);
					String n = ns(nb);
					raf.seek(d2i[2]);
					byte[] d = new byte[l];
					raf.read(d);
					return new DFFResource(t,i,dt,f,n,d);
				} catch (IOException ioe1) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,name);
				if (d3i != null) {
					raf.seek(d3i[0]);
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					raf.seek(d3i[5]);
					int i = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					short dt = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short f = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					String n = "";
					if (d3i[7] > 0) {
						byte[] nb = new byte[(int)d3i[8]-1];
						raf.seek(d3i[7]+1);
						raf.read(nb);
						n = ns(nb);
					}
					if (d3i[10] > (long)Integer.MAX_VALUE) {
						throw new DFFResourceTooBigException();
					}
					byte[] d = new byte[(int)d3i[10]];
					if (d3i[9] > 0) {
						raf.seek(d3i[9]);
						raf.read(d);
					}
					return new DFFResource(t,i,dt,f,n,d);
				}
			} catch (IOException ioe) {}
			break;
		}
		return null;
	}
	
	@Override
	public synchronized DFFResource getAttributes(long type, int id) {
		switch (version) {
		case 1:
			long d1i = d1locate(type,id);
			if (d1i > 0L) {
				try {
					raf.seek(d1i);
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					short i = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					return new DFFResource(t,i,new byte[0]);
				} catch (IOException ioe1) {}
			}
			break;
		case 2:
			long[] d2i = d2locate(type,id);
			if (d2i != null) {
				try {
					raf.seek(d2i[0]);
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					short id1 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short dt = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short f = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short id2 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					int i = (id1 & 0xFFFF) | ((id2 << 16) & 0xFFFF0000);
					raf.seek(d2i[1]);
					int nl = (raf.readByte() & 0xFF);
					byte[] nb = new byte[nl];
					raf.read(nb);
					String n = ns(nb);
					return new DFFResource(t,i,dt,f,n,new byte[0]);
				} catch (IOException ioe1) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,id);
				if (d3i != null) {
					raf.seek(d3i[0]);
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					raf.seek(d3i[5]);
					int i = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					short dt = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short f = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					String n = "";
					if (d3i[7] > 0) {
						byte[] nb = new byte[(int)d3i[8]-1];
						raf.seek(d3i[7]+1);
						raf.read(nb);
						n = ns(nb);
					}
					return new DFFResource(t,i,dt,f,n,new byte[0]);
				}
			} catch (IOException ioe) {}
			break;
		}
		return null;
	}
	@Override
	public synchronized DFFResource getAttributes(long type, String name) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			long[] d2i = d2locate(type,name);
			if (d2i != null) {
				try {
					raf.seek(d2i[0]);
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					short id1 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short dt = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short f = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short id2 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					int i = (id1 & 0xFFFF) | ((id2 << 16) & 0xFFFF0000);
					raf.seek(d2i[1]);
					int nl = (raf.readByte() & 0xFF);
					byte[] nb = new byte[nl];
					raf.read(nb);
					String n = ns(nb);
					return new DFFResource(t,i,dt,f,n,new byte[0]);
				} catch (IOException ioe1) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,name);
				if (d3i != null) {
					raf.seek(d3i[0]);
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					raf.seek(d3i[5]);
					int i = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					short dt = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					short f = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					String n = "";
					if (d3i[7] > 0) {
						byte[] nb = new byte[(int)d3i[8]-1];
						raf.seek(d3i[7]+1);
						raf.read(nb);
						n = ns(nb);
					}
					return new DFFResource(t,i,dt,f,n,new byte[0]);
				}
			} catch (IOException ioe) {}
			break;
		}
		return null;
	}
	
	@Override
	public synchronized long getLength(long type, int id) {
		switch (version) {
		case 1:
			long d1i = d1locate(type,id);
			if (d1i > 0L) {
				try {
					raf.seek(d1i+12L);
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					return l;
				} catch (IOException ioe1) {}
			}
			break;
		case 2:
			long[] d2i = d2locate(type,id);
			if (d2i != null) {
				return d2i[5];
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,id);
				if (d3i != null) {
					return d3i[10];
				}
			} catch (IOException ioe) {}
			break;
		}
		return 0;
	}
	@Override
	public synchronized long getLength(long type, String name) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			long[] d2i = d2locate(type,name);
			if (d2i != null) {
				return d2i[5];
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,name);
				if (d3i != null) {
					return d3i[10];
				}
			} catch (IOException ioe) {}
			break;
		}
		return 0;
	}
	
	@Override
	public synchronized byte[] getData(long type, int id) {
		switch (version) {
		case 1:
			long d1i = d1locate(type,id);
			if (d1i > 0L) {
				try {
					raf.seek(d1i+12);
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					byte[] b = new byte[l];
					raf.read(b);
					return b;
				} catch (IOException ioe1) {}
			}
			break;
		case 2:
			long[] d2i = d2locate(type,id);
			if (d2i != null) {
				try {
					raf.seek(d2i[0]+16);
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					raf.seek(d2i[2]);
					byte[] d = new byte[l];
					raf.read(d);
					return d;
				} catch (IOException ioe1) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,id);
				if (d3i != null) {
					if (d3i[10] > (long)Integer.MAX_VALUE) {
						throw new DFFResourceTooBigException();
					}
					byte[] d = new byte[(int)d3i[10]];
					if (d3i[9] > 0) {
						raf.seek(d3i[9]);
						raf.read(d);
					}
					return d;
				}
			} catch (IOException ioe) {}
			break;
		}
		return null;
	}
	@Override
	public synchronized byte[] getData(long type, String name) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			long[] d2i = d2locate(type,name);
			if (d2i != null) {
				try {
					raf.seek(d2i[0]+16);
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					raf.seek(d2i[2]);
					byte[] d = new byte[l];
					raf.read(d);
					return d;
				} catch (IOException ioe1) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,name);
				if (d3i != null) {
					if (d3i[10] > (long)Integer.MAX_VALUE) {
						throw new DFFResourceTooBigException();
					}
					byte[] d = new byte[(int)d3i[10]];
					if (d3i[9] > 0) {
						raf.seek(d3i[9]);
						raf.read(d);
					}
					return d;
				}
			} catch (IOException ioe) {}
			break;
		}
		return null;
	}
	
	@Override
	public synchronized int read(long type, int id, long doffset, byte[] data, int off, int len) {
		switch (version) {
		case 1:
			long d1i = d1locate(type,id);
			if (d1i > 0) {
				try {
					raf.seek(d1i+12L);
					long d1l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					long begin = d1i+16L+doffset;
					long end = d1i+16L+Math.min(d1l,doffset+len);
					if (begin < end) {
						raf.seek(begin);
						int n = raf.read(data, off, len);
						return n;
					}
				} catch (IOException ioe1) {}
			}
			break;
		case 2:
			long[] d2i = d2locate(type,id);
			if (d2i != null) {
				try {
					long begin = d2i[2]+doffset;
					long end = Math.min(d2i[2]+d2i[5],d2i[2]+doffset+len);
					if (begin < end) {
						raf.seek(begin);
						int n = raf.read(data, off, (int)(end-begin));
						return n;
					}
				} catch (IOException ioe) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,id);
				if (d3i != null) {
					long begin = (d3i[9]+doffset);
					long end = Math.min(d3i[9]+d3i[10], d3i[9]+doffset+len);
					if (begin < end) {
						raf.seek(begin);
						int n = raf.read(data, off, (int)(end-begin));
						return n;
					}
				}
			} catch (IOException ioe) {}
			break;
		}
		return 0;
	}
	@Override
	public synchronized int read(long type, String name, long doffset, byte[] data, int off, int len) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			long[] d2i = d2locate(type,name);
			if (d2i != null) {
				try {
					long begin = d2i[2]+doffset;
					long end = Math.min(d2i[2]+d2i[5],d2i[2]+doffset+len);
					if (begin < end) {
						raf.seek(begin);
						int n = raf.read(data, off, (int)(end-begin));
						return n;
					}
				} catch (IOException ioe) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,name);
				if (d3i != null) {
					long begin = (d3i[9]+doffset);
					long end = Math.min(d3i[9]+d3i[10], d3i[9]+doffset+len);
					if (begin < end) {
						raf.seek(begin);
						int n = raf.read(data, off, (int)(end-begin));
						return n;
					}
				}
			} catch (IOException ioe) {}
			break;
		}
		return 0;
	}
	
	@Override
	public synchronized boolean remove(long type, int id) {
		switch (version) {
		case 1:
			long d1i = d1locate(type,id);
			if (d1i > 0L) {
				try {
					raf.seek(d1i+12L);
					int d1l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					KSFLUtilities.cut(raf, d1i, (long)d1l+16L);
					return true;
				} catch (IOException ioe1) {}
			}
			break;
		case 2:
			long[] d2i = d2locate(type,id);
			if (d2i != null) {
				try {
					KSFLUtilities.cut(raf, d2i[2], d2i[5]);
					KSFLUtilities.cut(raf, d2i[1], d2i[4]);
					KSFLUtilities.cut(raf, d2i[0], d2i[3]);
					d2dataSpace -= (d2i[3]+d2i[4]);
					d2nameSpace -= d2i[3];
					d2objectCount--;
					raf.seek(4L);
					raf.writeInt(sb?Integer.reverseBytes(d2objectCount):d2objectCount);
					return true;
				} catch (IOException ioe1) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,id);
				if (d3i != null) {
					if (d3i[9] > 0) d3cut(null, d3i, d3i[9], d3i[10], REMOVED_DATA, type, id);
					if (d3i[7] > 0) d3cut(null, d3i, d3i[7], d3i[8], REMOVED_NAME, type, id);
					if (d3i[2] < 2) {
						raf.seek(4);
						int tc = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
						raf.seek(4);
						raf.writeInt(sb?Integer.reverseBytes(tc-1):(tc-1));
						d3cut(null, d3i, d3i[0], d3i[1], REMOVED_TYPE_RECORD, type, id);
					} else {
						raf.seek(d3i[0]+8);
						raf.writeInt(sb?Integer.reverseBytes((int)d3i[2]-1):((int)d3i[2]-1));
					}
					if (d3i[5] > 0) d3cut(null, d3i, d3i[5], d3i[6], REMOVED_OBJECT_RECORD, type, id);
					return true;
				} 
			} catch (IOException ioe) {}
			break;
		}
		return false;
	}
	@Override
	public synchronized boolean remove(long type, String name) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			long[] d2i = d2locate(type,name);
			if (d2i != null) {
				try {
					KSFLUtilities.cut(raf, d2i[2], d2i[5]);
					KSFLUtilities.cut(raf, d2i[1], d2i[4]);
					KSFLUtilities.cut(raf, d2i[0], d2i[3]);
					d2dataSpace -= (d2i[3]+d2i[4]);
					d2nameSpace -= d2i[3];
					d2objectCount--;
					raf.seek(4L);
					raf.writeInt(sb?Integer.reverseBytes(d2objectCount):d2objectCount);
					return true;
				} catch (IOException ioe1) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,name);
				if (d3i != null) {
					raf.seek(d3i[5]);
					int id = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					if (d3i[9] > 0) d3cut(null, d3i, d3i[9], d3i[10], REMOVED_DATA, type, id);
					if (d3i[7] > 0) d3cut(null, d3i, d3i[7], d3i[8], REMOVED_NAME, type, id);
					if (d3i[2] < 2) {
						raf.seek(4);
						int tc = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
						raf.seek(4);
						raf.writeInt(sb?Integer.reverseBytes(tc-1):(tc-1));
						d3cut(null, d3i, d3i[0], d3i[1], REMOVED_TYPE_RECORD, type, id);
					} else {
						raf.seek(d3i[0]+8);
						raf.writeInt(sb?Integer.reverseBytes((int)d3i[2]-1):((int)d3i[2]-1));
					}
					if (d3i[5] > 0) d3cut(null, d3i, d3i[5], d3i[6], REMOVED_OBJECT_RECORD, type, id);
					return true;
				} 
			} catch (IOException ioe) {}
			break;
		}
		return false;
	}
	
	@Override
	public synchronized boolean set(long type, int id, DFFResource r) throws DFFResourceAlreadyExistsException {
		if (!contains(type, id)) return false;
		if (r.name == null) r.name = "";
		if (r.data == null) r.data = new byte[0];
		return setAttributes(type, id, r) && setData(r.type, r.id, r.data);
	}
	@Override
	public synchronized boolean set(long type, String name, DFFResource r) throws DFFResourceAlreadyExistsException {
		int id = getIDFromName(type, name);
		if (!contains(type, id)) return false;
		if (r.name == null) r.name = "";
		if (r.data == null) r.data = new byte[0];
		return setAttributes(type, id, r) && setData(r.type, r.id, r.data);
	}
	
	@Override
	public synchronized boolean setAttributes(long type, int id, DFFResource r) throws DFFResourceAlreadyExistsException {
		if (r.name == null) r.name = "";
		switch (version) {
		case 1:
			long d1i = d1locate(type,id);
			if (d1i > 0L) {
				long d1e = d1locate(r.type,r.id);
				if ((d1e > 0L) && (d1e != d1i)) {
					throw new DFFResourceAlreadyExistsException();
				} else {
					try {
						raf.seek(d1i);
						raf.writeLong(sb?Long.reverseBytes(r.type):r.type);
						raf.writeShort(sb?Short.reverseBytes((short)r.id):(short)r.id);
						return true;
					} catch (IOException ioe1) {}
				}
			}
			break;
		case 2:
			long[] d2i = d2locate(type,id);
			if (d2i != null) {
				long[] d2e = d2locate(r.type,r.id);
				if ((d2e != null) && ((d2e[0] != d2i[0]) || (d2e[1] != d2i[1]) || (d2e[2] != d2i[2]))) {
					throw new DFFResourceAlreadyExistsException();
				} else {
					try {
						raf.seek(d2i[0]);
						raf.writeLong(sb?Long.reverseBytes(r.type):r.type);
						raf.writeShort(sb?Short.reverseBytes((short)(r.id & 0xFFFF)):(short)(r.id & 0xFFFF));
						raf.writeShort(sb?Short.reverseBytes(r.datatype):r.datatype);
						raf.writeShort(sb?Short.reverseBytes(r.getAttributes()):r.getAttributes());
						raf.writeShort(sb?Short.reverseBytes((short)((r.id >> 16) & 0xFFFF)):(short)((r.id >> 16) & 0xFFFF));
						byte[] n = gb(r.name);
						if (n.length > 255) n = KSFLUtilities.cut(n, 255, n.length-255);
						n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
						KSFLUtilities.cut(raf, d2i[1], d2i[4]);
						KSFLUtilities.paste(raf, d2i[1], n);
						d2dataSpace = d2dataSpace - d2i[4] + n.length;
						return true;
					} catch (IOException ioe) {}
				}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,id);
				if (d3i != null) {
					long[] d3e = d3locate(r.type,r.id);
					if ((d3e != null) && (
							(d3e[0] != d3i[0]) ||
							(d3e[1] != d3i[1]) ||
							(d3e[2] != d3i[2]) ||
							(d3e[3] != d3i[3]) ||
							(d3e[4] != d3i[4]) ||
							(d3e[5] != d3i[5]) ||
							(d3e[6] != d3i[6]) ||
							(d3e[7] != d3i[7]) ||
							(d3e[8] != d3i[8]) ||
							(d3e[9] != d3i[9]) ||
							(d3e[10] != d3i[10])
					)) {
						throw new DFFResourceAlreadyExistsException();
					} else if ((raf.length() - d3i[8] + 1L + (long)gb(r.name).length) > KSFLUtilities.UINT48_MAX_VALUE) {
						throw new DFFResourceTooBigException();
					} else {
						if (type != r.type) {
							long[] m = d3metrics();
							// add type entry for new type if necessary
							long[] t = d3getType(r.type);
							if (t == null) {
								if (m[0] == Integer.MAX_VALUE) {
									throw new DFFResourceTooBigException("Too many types.");
								} else {
									byte[] th = new byte[16];
									KSFLUtilities.putLong(th, 0, sb, r.type);
									KSFLUtilities.putInt(th, 8, sb, 0);
									KSFLUtilities.putInt(th, 12, sb, (int)m[4]);
									raf.seek(4);
									raf.writeInt(sb?Integer.reverseBytes((int)++m[0]):(int)++m[0]);
									d3paste(m, d3i, m[1]+m[2], th, INSERTED_TYPE_RECORD, r.type, r.id);
									t = d3getType(r.type);
								}
							} else if (t[2] == Integer.MAX_VALUE) {
								throw new DFFResourceTooBigException("Too many objects of this type.");
							}
							// increment new object count
							// add new object record
							byte[] h = new byte[24];
							KSFLUtilities.putInt(h, 0, sb, r.id);
							KSFLUtilities.putShort(h, 4, sb, r.datatype);
							KSFLUtilities.putShort(h, 6, sb, r.getAttributes());
							KSFLUtilities.putInt(h, 8, sb, (int)d3i[7]);
							KSFLUtilities.putUInt48(h, 12, sb, d3i[9]);
							KSFLUtilities.putUInt48(h, 18, sb, d3i[10]);
							raf.seek(t[0]+8);
							raf.writeInt(sb?Integer.reverseBytes((int)t[2]+1):(int)t[2]+1);
							d3paste(m, d3i, t[3]+t[4], h, INSERTED_OBJECT_RECORD, r.type, r.id);
							// decrement old object count
							// delete type entry for old type if necessary
							// remove old object record
							if (d3i[2] < 2) {
								raf.seek(4);
								raf.writeInt(sb?Integer.reverseBytes((int)--m[0]):(int)--m[0]);
								d3cut(m, d3i, d3i[0], d3i[1], REMOVED_TYPE_RECORD, type, id);
							} else {
								raf.seek(d3i[0]+8);
								raf.writeInt(sb?Integer.reverseBytes((int)d3i[2]-1):((int)d3i[2]-1));
							}
							if (d3i[5] > 0) d3cut(m, d3i, d3i[5], d3i[6], REMOVED_OBJECT_RECORD, type, id);
						} else {
							long[] t = d3getType(r.type);
							raf.seek(t[0]+8);
							raf.writeInt(sb?Integer.reverseBytes((int)t[2]-1):(int)t[2]-1);
							d3cut(null, d3i, d3i[5], d3i[6], REMOVED_OBJECT_RECORD, type, id);
							byte[] h = new byte[24];
							KSFLUtilities.putInt(h, 0, sb, r.id);
							KSFLUtilities.putShort(h, 4, sb, r.datatype);
							KSFLUtilities.putShort(h, 6, sb, r.getAttributes());
							KSFLUtilities.putInt(h, 8, sb, (int)d3i[7]);
							KSFLUtilities.putUInt48(h, 12, sb, d3i[9]);
							KSFLUtilities.putUInt48(h, 18, sb, d3i[10]);
							raf.seek(t[0]+8);
							raf.writeInt(sb?Integer.reverseBytes((int)t[2]):(int)t[2]);
							d3paste(null, d3i, d3i[5], h, INSERTED_OBJECT_RECORD, r.type, r.id);
						}
						if (r.name.length() > 0) {
							byte[] n = gb(r.name);
							if (n.length > 255) n = KSFLUtilities.cut(n, 255, n.length-255);
							n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
							if (d3i[7] > 0) {
								d3paste(null, d3i, d3i[7], n, INSERTED_NAME, r.type, r.id);
								d3cut(null, d3i, d3i[7]+n.length, d3i[8], REMOVED_NAME, r.type, r.id);
							} else {
								d3i[7] = d3metrics()[6];
								raf.seek(d3i[5]+8);
								raf.writeInt(sb?Integer.reverseBytes((int)d3i[7]):(int)d3i[7]);
								d3paste(null, d3i, d3i[7], n, INSERTED_NAME, r.type, r.id);
							}
							d3i[8] = n.length;
						} else if (d3i[7] > 0) {
							d3cut(null, d3i, d3i[7], d3i[8], REMOVED_NAME, r.type, r.id);
							raf.seek(d3i[5]+8);
							raf.writeInt((int)(d3i[7] = 0));
							d3i[8] = 0;
						}
						return true;
					}
				}
			} catch (IOException ioe) {}
			break;
		}
		return false;
	}
	@Override
	public synchronized boolean setAttributes(long type, String name, DFFResource r) throws DFFResourceAlreadyExistsException {
		if (r.name == null) r.name = "";
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			long[] d2i = d2locate(type,name);
			if (d2i != null) {
				long[] d2e = d2locate(r.type,r.id);
				if ((d2e != null) && ((d2e[0] != d2i[0]) || (d2e[1] != d2i[1]) || (d2e[2] != d2i[2]))) {
					throw new DFFResourceAlreadyExistsException();
				} else {
					try {
						raf.seek(d2i[0]);
						raf.writeLong(sb?Long.reverseBytes(r.type):r.type);
						raf.writeShort(sb?Short.reverseBytes((short)(r.id & 0xFFFF)):(short)(r.id & 0xFFFF));
						raf.writeShort(sb?Short.reverseBytes(r.datatype):r.datatype);
						raf.writeShort(sb?Short.reverseBytes(r.getAttributes()):r.getAttributes());
						raf.writeShort(sb?Short.reverseBytes((short)((r.id >> 16) & 0xFFFF)):(short)((r.id >> 16) & 0xFFFF));
						byte[] n = gb(r.name);
						if (n.length > 255) n = KSFLUtilities.cut(n, 255, n.length-255);
						n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
						KSFLUtilities.cut(raf, d2i[1], d2i[4]);
						KSFLUtilities.paste(raf, d2i[1], n);
						d2dataSpace = d2dataSpace - d2i[4] + n.length;
						return true;
					} catch (IOException ioe) {}
				}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,name);
				if (d3i != null) {
					long[] d3e = d3locate(r.type,r.id);
					if ((d3e != null) && (
							(d3e[0] != d3i[0]) ||
							(d3e[1] != d3i[1]) ||
							(d3e[2] != d3i[2]) ||
							(d3e[3] != d3i[3]) ||
							(d3e[4] != d3i[4]) ||
							(d3e[5] != d3i[5]) ||
							(d3e[6] != d3i[6]) ||
							(d3e[7] != d3i[7]) ||
							(d3e[8] != d3i[8]) ||
							(d3e[9] != d3i[9]) ||
							(d3e[10] != d3i[10])
					)) {
						throw new DFFResourceAlreadyExistsException();
					} else if ((raf.length() - d3i[8] + 1L + (long)gb(r.name).length) > KSFLUtilities.UINT48_MAX_VALUE) {
						throw new DFFResourceTooBigException();
					} else {
						raf.seek(d3i[5]);
						int id = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
						if (type != r.type) {
							long[] m = d3metrics();
							// add type entry for new type if necessary
							long[] t = d3getType(r.type);
							if (t == null) {
								if (m[0] == Integer.MAX_VALUE) {
									throw new DFFResourceTooBigException("Too many types.");
								} else {
									byte[] th = new byte[16];
									KSFLUtilities.putLong(th, 0, sb, r.type);
									KSFLUtilities.putInt(th, 8, sb, 0);
									KSFLUtilities.putInt(th, 12, sb, (int)m[4]);
									raf.seek(4);
									raf.writeInt(sb?Integer.reverseBytes((int)++m[0]):(int)++m[0]);
									d3paste(m, d3i, m[1]+m[2], th, INSERTED_TYPE_RECORD, r.type, r.id);
									t = d3getType(r.type);
								}
							} else if (t[2] == Integer.MAX_VALUE) {
								throw new DFFResourceTooBigException("Too many objects of this type.");
							}
							// increment new object count
							// add new object record
							byte[] h = new byte[24];
							KSFLUtilities.putInt(h, 0, sb, r.id);
							KSFLUtilities.putShort(h, 4, sb, r.datatype);
							KSFLUtilities.putShort(h, 6, sb, r.getAttributes());
							KSFLUtilities.putInt(h, 8, sb, (int)d3i[7]);
							KSFLUtilities.putUInt48(h, 12, sb, d3i[9]);
							KSFLUtilities.putUInt48(h, 18, sb, d3i[10]);
							raf.seek(t[0]+8);
							raf.writeInt(sb?Integer.reverseBytes((int)t[2]+1):(int)t[2]+1);
							d3paste(m, d3i, t[3]+t[4], h, INSERTED_OBJECT_RECORD, r.type, r.id);
							// decrement old object count
							// delete type entry for old type if necessary
							// remove old object record
							if (d3i[2] < 2) {
								raf.seek(4);
								raf.writeInt(sb?Integer.reverseBytes((int)--m[0]):(int)--m[0]);
								d3cut(m, d3i, d3i[0], d3i[1], REMOVED_TYPE_RECORD, type, id);
							} else {
								raf.seek(d3i[0]+8);
								raf.writeInt(sb?Integer.reverseBytes((int)d3i[2]-1):((int)d3i[2]-1));
							}
							if (d3i[5] > 0) d3cut(m, d3i, d3i[5], d3i[6], REMOVED_OBJECT_RECORD, type, id);
						} else {
							long[] t = d3getType(r.type);
							raf.seek(t[0]+8);
							raf.writeInt(sb?Integer.reverseBytes((int)t[2]-1):(int)t[2]-1);
							d3cut(null, d3i, d3i[5], d3i[6], REMOVED_OBJECT_RECORD, type, id);
							byte[] h = new byte[24];
							KSFLUtilities.putInt(h, 0, sb, r.id);
							KSFLUtilities.putShort(h, 4, sb, r.datatype);
							KSFLUtilities.putShort(h, 6, sb, r.getAttributes());
							KSFLUtilities.putInt(h, 8, sb, (int)d3i[7]);
							KSFLUtilities.putUInt48(h, 12, sb, d3i[9]);
							KSFLUtilities.putUInt48(h, 18, sb, d3i[10]);
							raf.seek(t[0]+8);
							raf.writeInt(sb?Integer.reverseBytes((int)t[2]):(int)t[2]);
							d3paste(null, d3i, d3i[5], h, INSERTED_OBJECT_RECORD, r.type, r.id);
						}
						if (r.name.length() > 0) {
							byte[] n = gb(r.name);
							if (n.length > 255) n = KSFLUtilities.cut(n, 255, n.length-255);
							n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
							if (d3i[7] > 0) {
								d3paste(null, d3i, d3i[7], n, INSERTED_NAME, r.type, r.id);
								d3cut(null, d3i, d3i[7]+n.length, d3i[8], REMOVED_NAME, r.type, r.id);
							} else {
								d3i[7] = d3metrics()[6];
								raf.seek(d3i[5]+8);
								raf.writeInt(sb?Integer.reverseBytes((int)d3i[7]):(int)d3i[7]);
								d3paste(null, d3i, d3i[7], n, INSERTED_NAME, r.type, r.id);
							}
							d3i[8] = n.length;
						} else if (d3i[7] > 0) {
							d3cut(null, d3i, d3i[7], d3i[8], REMOVED_NAME, r.type, r.id);
							raf.seek(d3i[5]+8);
							raf.writeInt((int)(d3i[7] = 0));
							d3i[8] = 0;
						}
						return true;
					}
				}
			} catch (IOException ioe) {}
			break;
		}
		return false;
	}
	
	@Override
	public synchronized boolean setLength(long type, int id, long len) {
		switch (version) {
		case 1:
			if (len > (long)Integer.MAX_VALUE) {
				throw new DFFResourceTooBigException();
			}
			long d1i = d1locate(type,id);
			if (d1i > 0L) {
				try {
					raf.seek(d1i+12L);
					int d1l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					if (len < d1l) {
						KSFLUtilities.cut(raf, d1i+16L+len, d1l-len);
					} else {
						KSFLUtilities.paste(raf, d1i+16L+d1l, len-d1l);
					}
					raf.seek(d1i+12L);
					raf.writeInt(sb?Integer.reverseBytes((int)len):(int)len);
					return true;
				} catch (IOException ioe1) {}
			}
			break;
		case 2:
			if (len > (long)Integer.MAX_VALUE) {
				throw new DFFResourceTooBigException();
			}
			long[] d2i = d2locate(type,id);
			if (d2i != null) {
				try {
					if (len < d2i[5]) {
						KSFLUtilities.cut(raf, d2i[2]+(long)len, d2i[5]-(long)len);
					} else {
						KSFLUtilities.paste(raf, d2i[2]+d2i[5], (long)len-d2i[5]);
					}
					raf.seek(d2i[0]+16L);
					raf.writeInt(sb?Integer.reverseBytes((int)len):(int)len);
					return true;
				} catch (IOException ioe) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,id);
				if (d3i != null) {
					raf.seek(d3i[5]);
					if ((raf.length() - (long)d3i[10] + len) > KSFLUtilities.UINT48_MAX_VALUE) {
						throw new DFFResourceTooBigException();
					}
					raf.seek(d3i[5]+18);
					KSFLUtilities.writeUInt48(raf, sb?KSFLUtilities.reverseUInt48(len):len);
					if (len < d3i[10]) {
						d3cut(null, d3i, (d3i[9]+len), (d3i[10]-len), REMOVED_DATA, type, id);
					} else {
						d3paste(null, d3i, (d3i[9]+d3i[10]), (len-d3i[10]), INSERTED_DATA, type, id);
					}
					return true;
				}
			} catch (IOException ioe) {}
			break;
		}
		return false;
	}
	@Override
	public synchronized boolean setLength(long type, String name, long len) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			if (len > (long)Integer.MAX_VALUE) {
				throw new DFFResourceTooBigException();
			}
			long[] d2i = d2locate(type,name);
			if (d2i != null) {
				try {
					if (len < d2i[5]) {
						KSFLUtilities.cut(raf, d2i[2]+(long)len, d2i[5]-(long)len);
					} else {
						KSFLUtilities.paste(raf, d2i[2]+d2i[5], (long)len-d2i[5]);
					}
					raf.seek(d2i[0]+16L);
					raf.writeInt(sb?Integer.reverseBytes((int)len):(int)len);
					return true;
				} catch (IOException ioe) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,name);
				if (d3i != null) {
					raf.seek(d3i[5]);
					int id = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					if ((raf.length() - (long)d3i[10] + len) > KSFLUtilities.UINT48_MAX_VALUE) {
						throw new DFFResourceTooBigException();
					}
					raf.seek(d3i[5]+18);
					KSFLUtilities.writeUInt48(raf, sb?KSFLUtilities.reverseUInt48(len):len);
					if (len < d3i[10]) {
						d3cut(null, d3i, (d3i[9]+len), (d3i[10]-len), REMOVED_DATA, type, id);
					} else {
						d3paste(null, d3i, (d3i[9]+d3i[10]), (len-d3i[10]), INSERTED_DATA, type, id);
					}
					return true;
				}
			} catch (IOException ioe) {}
			break;
		}
		return false;
	}
	
	@Override
	public synchronized boolean setData(long type, int id, byte[] data) {
		switch (version) {
		case 1:
			long d1i = d1locate(type,id);
			if (d1i > 0L) {
				try {
					raf.seek(d1i+12L);
					int d1l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					KSFLUtilities.cut(raf, d1i+16L, d1l);
					raf.seek(d1i+12L);
					raf.writeInt(0);
					KSFLUtilities.paste(raf, d1i+16L, data);
					raf.seek(d1i+12L);
					raf.writeInt(sb?Integer.reverseBytes(data.length):data.length);
					return true;
				} catch (IOException ioe1) {}
			}
			break;
		case 2:
			long[] d2i = d2locate(type,id);
			if (d2i != null) {
				try {
					KSFLUtilities.cut(raf, d2i[2], d2i[5]);
					KSFLUtilities.paste(raf, d2i[2], data);
					raf.seek(d2i[0]+16L);
					raf.writeInt(sb?Integer.reverseBytes(data.length):data.length);
					return true;
				} catch (IOException ioe) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,id);
				if (d3i != null) {
					raf.seek(d3i[5]);
					if (d3i[9] > 0) d3cut(null, d3i, d3i[9], d3i[10], REMOVED_DATA, type, id);
					if (data.length > 0) {
						if (d3i[9] <= 0) {
							d3i[9] = d3metrics()[8];
							raf.seek(d3i[5]+12);
							KSFLUtilities.writeUInt48(raf, sb?KSFLUtilities.reverseUInt48(d3i[9]):d3i[9]);
						}
						raf.seek(d3i[5]+18);
						KSFLUtilities.writeUInt48(raf, sb?KSFLUtilities.reverseUInt48(data.length):data.length);
						d3paste(null, d3i, d3i[9], data, INSERTED_DATA, type, id);
					} else if (d3i[9] > 0) {
						raf.seek(d3i[5]+18);
						KSFLUtilities.writeUInt48(raf, 0);
					}
					return true;
				}
			} catch (IOException ioe) {}
			break;
		}
		return false;
	}
	@Override
	public synchronized boolean setData(long type, String name, byte[] data) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			long[] d2i = d2locate(type,name);
			if (d2i != null) {
				try {
					KSFLUtilities.cut(raf, d2i[2], d2i[5]);
					KSFLUtilities.paste(raf, d2i[2], data);
					raf.seek(d2i[0]+16L);
					raf.writeInt(sb?Integer.reverseBytes(data.length):data.length);
					return true;
				} catch (IOException ioe) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,name);
				if (d3i != null) {
					raf.seek(d3i[5]);
					int id = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					if (d3i[9] > 0) d3cut(null, d3i, d3i[9], d3i[10], REMOVED_DATA, type, id);
					if (data.length > 0) {
						if (d3i[9] <= 0) {
							d3i[9] = d3metrics()[8];
							raf.seek(d3i[5]+12);
							KSFLUtilities.writeUInt48(raf, sb?KSFLUtilities.reverseUInt48(d3i[9]):d3i[9]);
						}
						raf.seek(d3i[5]+18);
						KSFLUtilities.writeUInt48(raf, sb?KSFLUtilities.reverseUInt48(data.length):data.length);
						d3paste(null, d3i, d3i[9], data, INSERTED_DATA, type, id);
					} else if (d3i[9] > 0) {
						raf.seek(d3i[5]+18);
						KSFLUtilities.writeUInt48(raf, 0);
					}
					return true;
				}
			} catch (IOException ioe) {}
			break;
		}
		return false;
	}
	
	@Override
	public synchronized int write(long type, int id, long doffset, byte[] data, int off, int len) {
		switch (version) {
		case 1:
			long d1i = d1locate(type,id);
			if (d1i > 0) {
				try {
					raf.seek(d1i+12L);
					long d1l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					if (doffset+len > d1l) {
						if (doffset+len > (long)Integer.MAX_VALUE) {
							throw new DFFResourceTooBigException();
						}
						if (doffset > d1l) {
							KSFLUtilities.paste(raf, d1i+16L+d1l, doffset-d1l+len);
						} else {
							KSFLUtilities.paste(raf, d1i+16L+d1l, len-(d1l-doffset));
						}
						raf.seek(d1i+12L);
						raf.writeInt(sb?Integer.reverseBytes((int)(doffset+len)):(int)(doffset+len));
					}
					raf.seek(d1i+16L+doffset);
					raf.write(data, off, len);
					return len;
				} catch (IOException ioe1) {}
			}
			break;
		case 2:
			long[] d2i = d2locate(type,id);
			if (d2i != null) {
				try {
					if (doffset+len > d2i[5]) {
						if (doffset+len > (long)Integer.MAX_VALUE) {
							throw new DFFResourceTooBigException();
						}
						if (doffset > d2i[5]) {
							KSFLUtilities.paste(raf, d2i[2]+d2i[5], doffset-d2i[5]+len);
						} else {
							KSFLUtilities.paste(raf, d2i[2]+d2i[5], len-(d2i[5]-doffset));
						}
						raf.seek(d2i[0]+16L);
						raf.writeInt(sb?Integer.reverseBytes((int)(doffset+len)):(int)(doffset+len));
					}
					raf.seek(d2i[2]+doffset);
					raf.write(data, off, len);
					return len;
				} catch (IOException ioe) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,id);
				if (d3i != null) {
					if (doffset+len > d3i[10]) {
						if ((raf.length() - d3i[10] + doffset + (long)len) > (long)KSFLUtilities.UINT48_MAX_VALUE) {
							throw new DFFResourceTooBigException();
						}
						raf.seek(d3i[5]+18);
						KSFLUtilities.writeUInt48(raf, sb?KSFLUtilities.reverseUInt48((long)(doffset+len)):(long)(doffset+len));
						if (doffset > d3i[10]) {
							d3paste(null, d3i, d3i[9]+d3i[10], (doffset-d3i[10]), INSERTED_DATA, type, id);
						} else {
							d3cut(null, d3i, (d3i[9]+doffset), (d3i[10]-doffset), INSERTED_DATA, type, id);
						}
						d3paste(null, d3i, (d3i[9]+doffset), KSFLUtilities.copy(data, off, len), INSERTED_DATA, type, id);
					} else {
						KSFLUtilities.cut(raf, (d3i[9]+doffset), len);
						KSFLUtilities.paste(raf, (d3i[9]+doffset), KSFLUtilities.copy(data, off, len));
					}
					return len;
				}
			} catch (IOException ioe) {}
			break;
		}
		return 0;
	}
	@Override
	public synchronized int write(long type, String name, long doffset, byte[] data, int off, int len) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			long[] d2i = d2locate(type,name);
			if (d2i != null) {
				try {
					if (doffset+len > d2i[5]) {
						if (doffset+len > (long)Integer.MAX_VALUE) {
							throw new DFFResourceTooBigException();
						}
						if (doffset > d2i[5]) {
							KSFLUtilities.paste(raf, d2i[2]+d2i[5], doffset-d2i[5]+len);
						} else {
							KSFLUtilities.paste(raf, d2i[2]+d2i[5], len-(d2i[5]-doffset));
						}
						raf.seek(d2i[0]+16L);
						raf.writeInt(sb?Integer.reverseBytes((int)(doffset+len)):(int)(doffset+len));
					}
					raf.seek(d2i[2]+doffset);
					raf.write(data, off, len);
					return len;
				} catch (IOException ioe) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,name);
				if (d3i != null) {
					raf.seek(d3i[5]);
					int id = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					if (doffset+len > d3i[10]) {
						if ((raf.length() - d3i[10] + doffset + (long)len) > (long)KSFLUtilities.UINT48_MAX_VALUE) {
							throw new DFFResourceTooBigException();
						}
						raf.seek(d3i[5]+18);
						KSFLUtilities.writeUInt48(raf, sb?KSFLUtilities.reverseUInt48((long)(doffset+len)):(long)(doffset+len));
						if (doffset > d3i[10]) {
							d3paste(null, d3i, d3i[9]+d3i[10], (doffset-d3i[10]), INSERTED_DATA, type, id);
						} else {
							d3cut(null, d3i, (d3i[9]+doffset), (d3i[10]-doffset), INSERTED_DATA, type, id);
						}
						d3paste(null, d3i, (d3i[9]+doffset), KSFLUtilities.copy(data, off, len), INSERTED_DATA, type, id);
					} else {
						KSFLUtilities.cut(raf, (d3i[9]+doffset), len);
						KSFLUtilities.paste(raf, (d3i[9]+doffset), KSFLUtilities.copy(data, off, len));
					}
					return len;
				}
			} catch (IOException ioe) {}
			break;
		}
		return 0;
	}
	
	@Override
	public synchronized int getTypeCount() {
		switch (version) {
		case 1:
			List<Long> d1s = new ArrayList<Long>();
			try {
				raf.seek(4L);
				while (raf.getFilePointer()+16L <= raf.length()) {
					long ty = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					raf.readShort();
					raf.readShort();
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					if (!d1s.contains(ty)) d1s.add(ty);
					raf.skipBytes(l);
				}
			} catch (IOException ioe) {}
			return d1s.size();
		case 2:
			List<Long> d2s = new ArrayList<Long>();
			int d2i = 0;
			long d2h = d2headerSpace;
			try {
				raf.seek(d2headerSpace);
				while (d2i < d2objectCount && d2h < d2nameSpace) {
					long ty = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					if (!d2s.contains(ty)) d2s.add(ty);
					raf.skipBytes(12);
					d2h += 20;
					d2i++;
				}
			} catch (IOException ioe) {}
			return d2s.size();
		case 3:
			try {
				raf.seek(4);
				int d3typeCnt = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
				return d3typeCnt;
			} catch (IOException ioe) {}
			break;
		}
		return 0;
	}
	@Override
	public synchronized long getType(int index) {
		switch (version) {
		case 1:
			List<Long> d1s = new ArrayList<Long>();
			try {
				raf.seek(4L);
				while (raf.getFilePointer()+16L <= raf.length()) {
					long ty = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					raf.readShort();
					raf.readShort();
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					if (!d1s.contains(ty)) {
						if (d1s.size() == index) return ty;
						else d1s.add(ty);
					}
					raf.skipBytes(l);
				}
			} catch (IOException ioe) {}
			break;
		case 2:
			List<Long> d2s = new ArrayList<Long>();
			int d2i = 0;
			long d2h = d2headerSpace;
			try {
				raf.seek(d2headerSpace);
				while (d2i < d2objectCount && d2h < d2nameSpace) {
					long ty = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					if (!d2s.contains(ty)) {
						if (d2s.size() == index) return ty;
						else d2s.add(ty);
					}
					raf.skipBytes(12);
					d2h += 20;
					d2i++;
				}
			} catch (IOException ioe) {}
			break;
		case 3:
			try {
				raf.seek(4);
				int d3typeCnt = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
				if (index >= 0 && index < d3typeCnt) {
					raf.seek(8L+16L*index);
					return sb?Long.reverseBytes(raf.readLong()):raf.readLong();
				}
			} catch (IOException ioe) {}
			break;
		}
		return 0;
	}
	@Override
	public synchronized long[] getTypes() {
		switch (version) {
		case 1:
			List<Long> d1s = new ArrayList<Long>();
			try {
				raf.seek(4L);
				while (raf.getFilePointer()+16L <= raf.length()) {
					long ty = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					raf.readShort();
					raf.readShort();
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					if (!d1s.contains(ty)) d1s.add(ty);
					raf.skipBytes(l);
				}
			} catch (IOException ioe) {}
			long[] d1a = new long[d1s.size()];
			Iterator<Long> d1si = d1s.iterator();
			int d1ai = 0;
			while (d1si.hasNext()) d1a[d1ai++] = d1si.next();
			return d1a;
		case 2:
			List<Long> d2s = new ArrayList<Long>();
			int d2i = 0;
			long d2h = d2headerSpace;
			try {
				raf.seek(d2headerSpace);
				while (d2i < d2objectCount && d2h < d2nameSpace) {
					long ty = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					if (!d2s.contains(ty)) d2s.add(ty);
					raf.skipBytes(12);
					d2h += 20;
					d2i++;
				}
			} catch (IOException ioe) {}
			long[] d2a = new long[d2s.size()];
			Iterator<Long> d2si = d2s.iterator();
			int d2ai = 0;
			while (d2si.hasNext()) d2a[d2ai++] = d2si.next();
			return d2a;
		case 3:
			try {
				raf.seek(4);
				int d3typeCnt = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
				long[] d3a = new long[d3typeCnt];
				for (int d3i = 0; d3i < d3typeCnt; d3i++) {
					d3a[d3i] = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					raf.readLong();
				}
				return d3a;
			} catch (IOException ioe) {}
			break;
		}
		return null;
	}
	
	@Override
	public synchronized int getResourceCount(long type) {
		switch (version) {
		case 1:
			List<Integer> d1s = new ArrayList<Integer>();
			try {
				raf.seek(4L);
				while (raf.getFilePointer()+16L <= raf.length()) {
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					short i = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					raf.readShort();
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					if (t == type) {
						if (!d1s.contains((int)i)) d1s.add((int)i);
					}
					raf.skipBytes(l);
				}
			} catch (IOException ioe) {}
			return d1s.size();
		case 2:
			List<Integer> d2s = new ArrayList<Integer>();
			int d2i = 0;
			long d2h = d2headerSpace;
			try {
				raf.seek(d2headerSpace);
				while (d2i < d2objectCount && d2h < d2nameSpace) {
					long ty = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					if (ty == type) {
						short id1 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
						raf.readShort();
						raf.readShort();
						short id2 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
						int id = (id1 & 0xFFFF) | ((id2 << 16) & 0xFFFF0000);
						if (!d2s.contains(id)) d2s.add(id);
						raf.skipBytes(4);
					} else raf.skipBytes(12);
					d2h += 20;
					d2i++;
				}
			} catch (IOException ioe) {}
			return d2s.size();
		case 3:
			try {
				long[] d3t = d3getType(type);
				if (d3t != null) return (int)d3t[2];
			} catch (IOException ioe) {}
			break;
		}
		return 0;
	}
	@Override
	public synchronized int getID(long type, int index) {
		switch (version) {
		case 1:
			List<Integer> d1s = new ArrayList<Integer>();
			try {
				raf.seek(4L);
				while (raf.getFilePointer()+16L <= raf.length()) {
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					short i = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					raf.readShort();
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					if (t == type) {
						if (!d1s.contains((int)i)) {
							if (d1s.size() == index) return (int)i;
							else d1s.add((int)i);
						}
					}
					raf.skipBytes(l);
				}
			} catch (IOException ioe) {}
			break;
		case 2:
			List<Integer> d2s = new ArrayList<Integer>();
			int d2i = 0;
			long d2h = d2headerSpace;
			try {
				raf.seek(d2headerSpace);
				while (d2i < d2objectCount && d2h < d2nameSpace) {
					long ty = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					if (ty == type) {
						short id1 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
						raf.readShort();
						raf.readShort();
						short id2 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
						int id = (id1 & 0xFFFF) | ((id2 << 16) & 0xFFFF0000);
						if (!d2s.contains(id)) {
							if (d2s.size() == index) return id;
							else d2s.add(id);
						}
						raf.skipBytes(4);
					} else raf.skipBytes(12);
					d2h += 20;
					d2i++;
				}
			} catch (IOException ioe) {}
			break;
		case 3:
			try {
				long[] d3t = d3getType(type);
				if (d3t != null) {
					if (index >= 0 && index < d3t[2]) {
						raf.seek(d3t[3]+24L*index);
						return sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					}
				}
			} catch (IOException ioe) {}
			break;
		}
		return 0;
	}
	@Override
	public synchronized int[] getIDs(long type) {
		switch (version) {
		case 1:
			List<Integer> d1s = new ArrayList<Integer>();
			try {
				raf.seek(4L);
				while (raf.getFilePointer()+16L <= raf.length()) {
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					short i = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					raf.readShort();
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					if (t == type) {
						if (!d1s.contains((int)i)) d1s.add((int)i);
					}
					raf.skipBytes(l);
				}
			} catch (IOException ioe) {}
			int[] d1a = new int[d1s.size()];
			Iterator<Integer> d1si = d1s.iterator();
			int d1ai = 0;
			while (d1si.hasNext()) d1a[d1ai++] = d1si.next();
			return d1a;
		case 2:
			List<Integer> d2s = new ArrayList<Integer>();
			int d2i = 0;
			long d2h = d2headerSpace;
			try {
				raf.seek(d2headerSpace);
				while (d2i < d2objectCount && d2h < d2nameSpace) {
					long ty = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					if (ty == type) {
						short id1 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
						raf.readShort();
						raf.readShort();
						short id2 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
						int id = (id1 & 0xFFFF) | ((id2 << 16) & 0xFFFF0000);
						if (!d2s.contains(id)) d2s.add(id);
						raf.skipBytes(4);
					} else raf.skipBytes(12);
					d2h += 20;
					d2i++;
				}
			} catch (IOException ioe) {}
			int[] d2a = new int[d2s.size()];
			Iterator<Integer> d2si = d2s.iterator();
			int d2ai = 0;
			while (d2si.hasNext()) d2a[d2ai++] = d2si.next();
			return d2a;
		case 3:
			try {
				long[] d3t = d3getType(type);
				if (d3t != null) {
					int[] d3a = new int[(int)d3t[2]];
					raf.seek(d3t[3]);
					for (int d3i = 0; d3i < d3t[2]; d3i++) {
						d3a[d3i] = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
						raf.skipBytes(20);
					}
					return d3a;
				} else {
					return new int[0];
				}
			} catch (IOException ioe) {}
			return new int[0];
		}
		return null;
	}
	@Override
	public synchronized String getName(long type, int index) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			List<String> d2s = new ArrayList<String>();
			int d2i = 0;
			long d2h = d2headerSpace;
			long d2n = d2nameSpace;
			try {
				while (d2i < d2objectCount && d2h < d2nameSpace && d2n < d2dataSpace) {
					raf.seek(d2h);
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					raf.seek(d2n);
					int nl = (raf.readByte() & 0xFF);
					if (t == type) {
						byte[] nb = new byte[nl];
						raf.read(nb);
						String n = ns(nb);
						if (d2s.size() == index) return n;
						else d2s.add(n);
					}
					d2n += 1+nl;
					d2h += 20;
					d2i++;
				}
			} catch (IOException ioe) {}
			break;
		case 3:
			try {
				long[] d3t = d3getType(type);
				if (d3t != null) {
					if (index >= 0 && index < d3t[2]) {
						raf.seek(d3t[3]+24L*index+8L);
						int nameOfst = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
						String n = "";
						if (nameOfst > 0) {
							raf.seek(nameOfst);
							int nameLen = (raf.readByte()&0xFF);
							byte[] nb = new byte[nameLen];
							raf.read(nb);
							n = ns(nb);
						}
						return n;
					}
				}
			} catch (IOException ioe) {}
			break;
		}
		return null;
	}
	@Override
	public synchronized String[] getNames(long type) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			List<String> d2s = new ArrayList<String>();
			int d2i = 0;
			long d2h = d2headerSpace;
			long d2n = d2nameSpace;
			try {
				while (d2i < d2objectCount && d2h < d2nameSpace && d2n < d2dataSpace) {
					raf.seek(d2h);
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					raf.seek(d2n);
					int nl = (raf.readByte() & 0xFF);
					if (t == type) {
						byte[] nb = new byte[nl];
						raf.read(nb);
						String n = ns(nb);
						d2s.add(n);
					}
					d2n += 1+nl;
					d2h += 20;
					d2i++;
				}
			} catch (IOException ioe) {}
			return d2s.toArray(new String[0]);
		case 3:
			List<String> d3s = new ArrayList<String>();
			try {
				long[] d3t = d3getType(type);
				if (d3t != null) {
					for (int d3i = 0; d3i < d3t[2]; d3i++) {
						raf.seek(d3t[3]+24L*d3i+8L);
						int nameOfst = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
						String n = "";
						if (nameOfst > 0) {
							raf.seek(nameOfst);
							int nameLen = (raf.readByte()&0xFF);
							byte[] nb = new byte[nameLen];
							raf.read(nb);
							n = ns(nb);
						}
						d3s.add(n);
					}
				}
			} catch (IOException ioe) {}
			return d3s.toArray(new String[0]);
		}
		return null;
	}
	
	@Override
	public synchronized int getNextAvailableID(long type, int start) {
		switch (version) {
		case 1:
			List<Integer> d1s = new ArrayList<Integer>();
			try {
				raf.seek(4L);
				while (raf.getFilePointer()+16L <= raf.length()) {
					long t = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					short i = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					raf.readShort();
					int l = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					if (t == type) {
						if (!d1s.contains((int)i)) d1s.add((int)i);
					}
					raf.skipBytes(l);
				}
			} catch (IOException ioe) {}
			int d1n = start;
			while (d1s.contains(d1n)) d1n++;
			return d1n;
		case 2:
			List<Integer> d2s = new ArrayList<Integer>();
			int d2i = 0;
			long d2h = d2headerSpace;
			try {
				raf.seek(d2headerSpace);
				while (d2i < d2objectCount && d2h < d2nameSpace) {
					long ty = sb?Long.reverseBytes(raf.readLong()):raf.readLong();
					if (ty == type) {
						short id1 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
						raf.readShort();
						raf.readShort();
						short id2 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
						int id = (id1 & 0xFFFF) | ((id2 << 16) & 0xFFFF0000);
						if (!d2s.contains(id)) d2s.add(id);
						raf.skipBytes(4);
					} else raf.skipBytes(12);
					d2h += 20;
					d2i++;
				}
			} catch (IOException ioe) {}
			int d2n = start;
			while (d2s.contains(d2n)) d2n++;
			return d2n;
		case 3:
			try {
				List<Integer> d3s = new ArrayList<Integer>();
				long[] d3t = d3getType(type);
				if (d3t != null) {
					raf.seek(d3t[3]);
					for (int d3i = 0; d3i < d3t[2]; d3i++) {
						d3s.add(sb?Integer.reverseBytes(raf.readInt()):raf.readInt());
						raf.skipBytes(20);
					}
				}
				int d3n = start;
				while (d3s.contains(d3n)) d3n++;
				return d3n;
			} catch (IOException ioe) {}
			break;
		}
		return start;
	}
	
	@Override
	public synchronized String getNameFromID(long type, int id) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			long[] d2i = d2locate(type,id);
			if (d2i != null) {
				try {
					raf.seek(d2i[1]);
					int nl = (raf.readByte() & 0xFF);
					byte[] nb = new byte[nl];
					raf.read(nb);
					String n = ns(nb);
					return n;
				} catch (IOException ioe) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,id);
				if (d3i != null) {
					String n = "";
					if (d3i[7] > 0) {
						byte[] nb = new byte[(int)d3i[8]-1];
						raf.seek(d3i[7]+1);
						raf.read(nb);
						n = ns(nb);
					}
					return n;
				}
			} catch (IOException ioe) {}
			break;
		}
		return null;
	}
	
	@Override
	public synchronized int getIDFromName(long type, String name) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			long[] d2i = d2locate(type,name);
			if (d2i != null) {
				try {
					raf.seek(d2i[0]+8);
					short id1 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					raf.readShort();
					raf.readShort();
					short id2 = sb?Short.reverseBytes(raf.readShort()):raf.readShort();
					int i = (id1 & 0xFFFF) | ((id2 << 16) & 0xFFFF0000);
					return i;
				} catch (IOException ioe) {}
			}
			break;
		case 3:
			try {
				long[] d3i = d3locate(type,name);
				if (d3i != null) {
					raf.seek(d3i[5]);
					int i = sb?Integer.reverseBytes(raf.readInt()):raf.readInt();
					return i;
				}
			} catch (IOException ioe) {}
			break;
		}
		return 0;
	}
}
