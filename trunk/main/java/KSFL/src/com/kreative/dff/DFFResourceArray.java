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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import com.kreative.ksfl.KSFLUtilities;

/**
 * The <code>DFFResourceArray</code> class provides a DFF interface
 * to an array of bytes.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class DFFResourceArray extends DFFResourceProvider {
	private byte[] arr;
	private int version;
	private boolean le;
	private int d2objectCount, d2headerSpace, d2nameSpace, d2dataSpace;
	private String textEncoding = "UTF-8";
	
	private String ns(byte[] a, int b, int c) {
		try {
			return new String(a,b,c,textEncoding);
		} catch (java.io.UnsupportedEncodingException uue) {
			return new String(a,b,c);
		}
	}
	
	private byte[] gb(String a) {
		try {
			return a.getBytes(textEncoding);
		} catch (java.io.UnsupportedEncodingException uue) {
			return a.getBytes();
		}
	}
	
	private int d1locate(long type, int id) {
		int i = 4;
		while (i+16 <= arr.length) {
			if (KSFLUtilities.getLong(arr, i, le) == type && KSFLUtilities.getShort(arr, i+8, le) == (short)id) return i;
			else i += 16+KSFLUtilities.getInt(arr, i+12, le);
		}
		return 0;
	}
	
	private int[] d2locate(long type, int id) {
		int i = 0;
		int hi = d2headerSpace;
		int ni = d2nameSpace;
		int di = d2dataSpace;
		while (i < d2objectCount && hi < d2nameSpace && ni < d2dataSpace && di < arr.length) {
			if (KSFLUtilities.getLong(arr, hi, le) == type && (KSFLUtilities.getShort(arr, hi+8, le) & 0xFFFF) == (id & 0xFFFF) && ((KSFLUtilities.getShort(arr, hi+14, le) << 16) & 0xFFFF0000) == (id & 0xFFFF0000)) {
				return new int[]{hi,ni,di,20,(arr[ni]&0xFF)+1,KSFLUtilities.getInt(arr,hi+16,le)};
			} else {
				di += KSFLUtilities.getInt(arr, hi+16, le);
				ni += 1+(arr[ni] & 0xFF);
				hi += 20;
				i++;
			}
		}
		return null;
	}
	
	private int[] d2locate(long type, String name) {
		int i = 0;
		int hi = d2headerSpace;
		int ni = d2nameSpace;
		int di = d2dataSpace;
		while (i < d2objectCount && hi < d2nameSpace && ni < d2dataSpace && di < arr.length) {
			if (KSFLUtilities.getLong(arr, hi, le) == type && (ns(arr, ni+1, arr[ni]&0xFF)).equals(name)) {
				return new int[]{hi,ni,di,20,(arr[ni]&0xFF)+1,KSFLUtilities.getInt(arr,hi+16,le)};
			} else {
				di += KSFLUtilities.getInt(arr, hi+16, le);
				ni += 1+(arr[ni] & 0xFF);
				hi += 20;
				i++;
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
	
	private void d3adjustTypeMap(int offset, int length, int whatInserted, long typeInserted, int idInserted) {
		// adjust the type map
		int typeCnt = KSFLUtilities.getInt(arr, 4, le);
		for (int ti=0; ti<typeCnt; ti++) {
			long thisType = KSFLUtilities.getLong(arr, 8+16*ti, le);
			int objTblOfst = KSFLUtilities.getInt(arr, 8+16*ti+12, le);
			if (objTblOfst > offset || (objTblOfst == offset  && length > 0 && !(whatInserted == INSERTED_OBJECT_RECORD && typeInserted == thisType))) {
				if ((long)objTblOfst+(long)length > (long)Integer.MAX_VALUE)
					throw new DFFResourceTooBigException("Map too big. Map now corrupted. You're screwed.");
				else KSFLUtilities.putInt(arr, 8+16*ti+12, le, objTblOfst+length);
			}
		}
	}
	
	private void d3adjustObjectMaps(int offset, int length, int whatInserted, long typeInserted, int idInserted) {
		// adjust the object maps
		int typeCnt = KSFLUtilities.getInt(arr, 4, le);
		for (int ti=0; ti<typeCnt; ti++) {
			long thisType = KSFLUtilities.getLong(arr, 8+16*ti, le);
			int objCnt = KSFLUtilities.getInt(arr, 8+16*ti+8, le);
			int objTblOfst = KSFLUtilities.getInt(arr, 8+16*ti+12, le);
			for (int oi=0; oi<objCnt; oi++) {
				int thisID = KSFLUtilities.getInt(arr, objTblOfst+24*oi, le);
				int nameOfst = KSFLUtilities.getInt(arr, objTblOfst+24*oi+8, le);
				if ((nameOfst > 0) && (nameOfst > offset || (nameOfst == offset  && length > 0 && !(whatInserted == INSERTED_NAME && typeInserted == thisType && idInserted == thisID)))) {
					if ((long)nameOfst+(long)length > (long)Integer.MAX_VALUE)
						throw new DFFResourceTooBigException("Map too big. Map now corrupted. You're screwed.");
					else KSFLUtilities.putInt(arr, objTblOfst+24*oi+8, le, nameOfst+length);
				}
				long dataOfst = KSFLUtilities.getUInt48(arr, objTblOfst+24*oi+12, le);
				if ((dataOfst > 0) && (dataOfst > offset || (dataOfst == offset  && length > 0 && !(whatInserted == INSERTED_DATA && typeInserted == thisType && idInserted == thisID)))) {
					if (dataOfst+(long)length > KSFLUtilities.UINT48_MAX_VALUE)
						throw new DFFResourceTooBigException("Map too big. Map now corrupted. You're screwed.");
					else KSFLUtilities.putUInt48(arr, objTblOfst+24*oi+12, le, dataOfst+(long)length);
				}
			}
		}
	}
	
	private void d3adjustMetrics(int[] a, int offset, int length, int whatInserted) {
		if (a[1] > offset || (a[1] == offset && length > 0 && whatInserted != INSERTED_TYPE_RECORD)) a[1] += length;
		if (a[3] > offset || (a[3] == offset && length > 0 && whatInserted != INSERTED_OBJECT_RECORD)) a[3] += length;
		if (a[4] > offset || (a[4] == offset && length > 0 && whatInserted != INSERTED_OBJECT_RECORD)) a[4] += length;
		if (a[5] > offset || (a[5] == offset && length > 0 && whatInserted != INSERTED_NAME)) a[5] += length;
		if (a[6] > offset || (a[6] == offset && length > 0 && whatInserted != INSERTED_NAME)) a[6] += length;
		if (a[7] > offset || (a[7] == offset && length > 0 && whatInserted != INSERTED_DATA)) a[7] += length;
		if (a[8] > offset || (a[8] == offset && length > 0 && whatInserted != INSERTED_DATA)) a[8] += length;
	}
	
	private void d3adjustLocation(int[] a, int offset, int length, int whatInserted) {
		if (a[0] > offset || (a[0] == offset && length > 0 && whatInserted != INSERTED_TYPE_RECORD)) a[0] += length;
		if (a[3] > offset || (a[3] == offset && length > 0 && whatInserted != INSERTED_OBJECT_RECORD)) a[3] += length;
		if (a[5] > offset || (a[5] == offset && length > 0 && whatInserted != INSERTED_OBJECT_RECORD)) a[5] += length;
		if (a[7] > offset || (a[7] == offset && length > 0 && whatInserted != INSERTED_NAME)) a[7] += length;
		if (a[9] > offset || (a[9] == offset && length > 0 && whatInserted != INSERTED_DATA)) a[9] += length;
	}
	
	private int[] d3metrics() {
		// a[0] = number of types in type table
		// a[1] = offset of type table
		// a[2] = length of type table
		// a[3] = lowest offset of any object table
		// a[4] = highest offset of any object table + that object table's length
		// a[5] = lowest offset of any name
		// a[6] = highest offset of any name + that name's length + 1
		// a[7] = lowest offset of any data
		// a[8] = highest offset of any data + that data's length
		int objTblMin=Integer.MAX_VALUE, objTblMax=0;
		int nameMin=Integer.MAX_VALUE, nameMax=0;
		int dataMin=Integer.MAX_VALUE, dataMax=0;
		int typeCnt = KSFLUtilities.getInt(arr, 4, le);
		for (int ti=0; ti<typeCnt; ti++) {
			int objCnt = KSFLUtilities.getInt(arr, 8+16*ti+8, le);
			int objTblOfst = KSFLUtilities.getInt(arr, 8+16*ti+12, le);
			if (objTblOfst > 0) {
				if (objTblOfst < objTblMin) objTblMin = objTblOfst;
				if (objTblOfst+24*objCnt > objTblMax) objTblMax = objTblOfst+24*objCnt;
			}
			for (int oi=0; oi<objCnt; oi++) {
				int nameOfst = KSFLUtilities.getInt(arr, objTblOfst+24*oi+8, le);
				if (nameOfst > 0) {
					int nameLen = (arr[nameOfst]&0xFF)+1;
					if (nameOfst < nameMin) nameMin = nameOfst;
					if (nameOfst+nameLen > nameMax) nameMax = nameOfst+nameLen;
				}
				long dataOfst = KSFLUtilities.getUInt48(arr, objTblOfst+24*oi+12, le);
				if (dataOfst > 0) {
					long dataLen = KSFLUtilities.getUInt48(arr, objTblOfst+24*oi+18, le);
					int dOfst = (dataOfst > (long)Integer.MAX_VALUE)?(Integer.MAX_VALUE):((int)dataOfst);
					int dLen = (dataLen > (long)Integer.MAX_VALUE)?(Integer.MAX_VALUE):((int)dataLen);
					if (dataOfst < dataMin) dataMin = dOfst;
					if (dataOfst+dataLen > dataMax) dataMax = dOfst+dLen;
				}
			}
		}
		if (objTblMin > objTblMax) {
			objTblMin = objTblMax = 8 + typeCnt*16;
		}
		if (nameMin > nameMax) {
			nameMin = nameMax = objTblMax;
		}
		if (dataMin > dataMax) {
			dataMin = dataMax = nameMax;
		}
		return new int[]{typeCnt,8,typeCnt*16,objTblMin,objTblMax,nameMin,nameMax,dataMin,dataMax};
	}
	
	private int[] d3getType(long type) {
		// a[0] = offset of type record
		// a[1] = length of type record (16)
		// a[2] = number of objects in object table
		// a[3] = offset of object table
		// a[4] = length of object table
		int typeCnt = KSFLUtilities.getInt(arr, 4, le);
		for (int ti=0; ti<typeCnt; ti++) {
			long t = KSFLUtilities.getLong(arr, 8+16*ti, le);
			int objCnt = KSFLUtilities.getInt(arr, 8+16*ti+8, le);
			int objTblOfst = KSFLUtilities.getInt(arr, 8+16*ti+12, le);
			if (t == type) return new int[]{8+16*ti,16,objCnt,objTblOfst,24*objCnt};
		}
		return null;
	}
	
	private int[] d3locate(long type, int id) {
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
		int[] ty = d3getType(type);
		if (ty != null) {
			for (int oi=0; oi<ty[2]; oi++) {
				int i = KSFLUtilities.getInt(arr, ty[3]+24*oi, le);
				if (i == id) {
					int nameOfst = KSFLUtilities.getInt(arr, ty[3]+24*oi+8, le);
					long dataOfst = KSFLUtilities.getUInt48(arr, ty[3]+24*oi+12, le);
					long dataLen = KSFLUtilities.getUInt48(arr, ty[3]+24*oi+18, le);
					if (dataOfst > (long)Integer.MAX_VALUE || dataLen > (long)Integer.MAX_VALUE) throw new DFFResourceTooBigException();
					int nameLen = (nameOfst>0)?((arr[nameOfst]&0xFF)+1):0;
					return new int[]{ty[0],ty[1],ty[2],ty[3],ty[4],ty[3]+24*oi,24,nameOfst,nameLen,(int)dataOfst,(int)dataLen};
				}
			}
		}
		return null;
	}
	
	private int[] d3locate(long type, String name) {
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
		int[] ty = d3getType(type);
		if (ty != null) {
			for (int oi=0; oi<ty[2]; oi++) {
				int nameOfst = KSFLUtilities.getInt(arr, ty[3]+24*oi+8, le);
				int nameLen = (nameOfst>0)?((arr[nameOfst]&0xFF)+1):0;
				String n = (nameOfst>0)?(ns(arr,nameOfst+1,nameLen-1)):"";
				if (n.equals(name)) {
					long dataOfst = KSFLUtilities.getUInt48(arr, ty[3]+24*oi+12, le);
					long dataLen = KSFLUtilities.getUInt48(arr, ty[3]+24*oi+18, le);
					if (dataOfst > (long)Integer.MAX_VALUE || dataLen > (long)Integer.MAX_VALUE) throw new DFFResourceTooBigException();
					return new int[]{ty[0],ty[1],ty[2],ty[3],ty[4],ty[3]+24*oi,24,nameOfst,nameLen,(int)dataOfst,(int)dataLen};
				}
			}
		}
		return null;
	}
	
	private void d3cut(int[] metrics, int[] loc, int offset, int length, int what, long type, int id) {
		arr = KSFLUtilities.cut(arr, offset, length);
		d3adjustTypeMap(offset, -length, what, type, id);
		d3adjustObjectMaps(offset, -length, what, type, id);
		if (loc != null) d3adjustLocation(loc, offset, -length, what);
		if (metrics != null) d3adjustMetrics(metrics, offset, -length, what);
	}
	
	private void d3paste(int[] metrics, int[] loc, int offset, byte[] stuff, int what, long type, int id) {
		arr = KSFLUtilities.paste(arr, offset, stuff);
		d3adjustTypeMap(offset, stuff.length, what, type, id);
		d3adjustObjectMaps(offset, stuff.length, what, type, id);
		if (loc != null) d3adjustLocation(loc, offset, stuff.length, what);
		if (metrics != null) d3adjustMetrics(metrics, offset, stuff.length, what);
	}
	
	private void d3paste(int[] metrics, int[] loc, int offset, int length, int what, long type, int id) {
		arr = KSFLUtilities.paste(arr, offset, length);
		d3adjustTypeMap(offset, length, what, type, id);
		d3adjustObjectMaps(offset, length, what, type, id);
		if (loc != null) d3adjustLocation(loc, offset, length, what);
		if (metrics != null) d3adjustMetrics(metrics, offset, length, what);
	}
	
	/**
	 * Creates an empty <code>DFFResourceArray</code> of the specified DFF version.
	 * @param version the DFF version to use; one of 1, 2, or 3.
	 * @throws NotADFFFileException if <code>version</code> is less than 1 or more than 3.
	 */
	public DFFResourceArray(int version) throws NotADFFFileException {
		this.version = version;
		this.le = false;
		switch (version) {
		case 1:
			arr = new byte[]{'%','D','F','F'};
			break;
		case 2:
			arr = new byte[]{'%','D','F','2',0,0,0,0};
			d2objectCount = 0;
			d2headerSpace = d2nameSpace = d2dataSpace = 8;
			break;
		case 3:
			arr = new byte[]{'%','D','F','3',0,0,0,0};
			break;
		default:
			throw new NotADFFFileException("Invalid version number: "+version);
		}
	}
	
	/**
	 * Creates an empty <code>DFFResourceArray</code> of the specified DFF version.
	 * @param version the DFF version to use; one 1, 2, or 3.
	 * @param le true if the DFF structure should use little-endian fields, false for big-endian.
	 * @throws NotADFFFileException if <code>version</code> is less than 1 or more than 3.
	 */
	public DFFResourceArray(int version, boolean le) throws NotADFFFileException {
		this.version = version;
		this.le = le;
		if (le) {
			switch (version) {
			case 1:
				arr = new byte[]{'F','F','D','%'};
				break;
			case 2:
				arr = new byte[]{'2','F','D','%',0,0,0,0};
				d2objectCount = 0;
				d2headerSpace = d2nameSpace = d2dataSpace = 8;
				break;
			case 3:
				arr = new byte[]{'3','F','D','%',0,0,0,0};
				break;
			default:
				throw new NotADFFFileException("Invalid version number: "+version);
			}
		} else {
			switch (version) {
			case 1:
				arr = new byte[]{'%','D','F','F'};
				break;
			case 2:
				arr = new byte[]{'%','D','F','2',0,0,0,0};
				d2objectCount = 0;
				d2headerSpace = d2nameSpace = d2dataSpace = 8;
				break;
			case 3:
				arr = new byte[]{'%','D','F','3',0,0,0,0};
				break;
			default:
				throw new NotADFFFileException("Invalid version number: "+version);
			}
		}
	}
	
	/**
	 * Creates a new <code>DFFResourceArray</code> around the specified byte array.
	 * The array should not be modified or read after this call.
	 * @param data a byte array containing a DFF structure of some sort.
	 * @throws NotADFFFileException if the array does not begin with any of the magic numbers for DFF files.
	 */
	public DFFResourceArray(byte[] data) throws NotADFFFileException {
		if (data.length < 4) throw new NotADFFFileException("Byte array is too small");
		int v = KSFLUtilities.getInt(data, 0, false);
		switch (v) {
		case MAGIC_NUMBER_DFF1:
			arr = data;
			version = 1;
			le = false;
			break;
		case MAGIC_NUMBER_DFF1R:
			arr = data;
			version = 1;
			le = true;
			break;
		case MAGIC_NUMBER_DFF2:
			if (data.length < 8) throw new NotADFFFileException("Byte array is too small");
			arr = data;
			version = 2;
			le = false;
			d2objectCount = KSFLUtilities.getInt(data, 4, le);
			d2headerSpace = 8;
			d2nameSpace = d2headerSpace+20*d2objectCount;
			d2dataSpace = d2nameSpace;
			for (int nh1 = d2objectCount; nh1 > 0; nh1--) {
				d2dataSpace += ((int)data[d2dataSpace] & (int)0xFF)+1;
			}
			break;
		case MAGIC_NUMBER_DFF2R:
			if (data.length < 8) throw new NotADFFFileException("Byte array is too small");
			arr = data;
			version = 2;
			le = true;
			d2objectCount = KSFLUtilities.getInt(data, 4, le);
			d2headerSpace = 8;
			d2nameSpace = d2headerSpace+20*d2objectCount;
			d2dataSpace = d2nameSpace;
			for (int nh2 = d2objectCount; nh2 > 0; nh2--) {
				d2dataSpace += ((int)data[d2dataSpace] & (int)0xFF)+1;
			}
			break;
		case MAGIC_NUMBER_DFF3:
			if (data.length < 8) throw new NotADFFFileException("Byte array is too small");
			arr = data;
			version = 3;
			le = false;
			break;
		case MAGIC_NUMBER_DFF3R:
			if (data.length < 8) throw new NotADFFFileException("Byte array is too small");
			arr = data;
			version = 3;
			le = true;
			break;
		default:
			throw new NotADFFFileException("Invalid magic number: "+Integer.toHexString(v));
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
	 * Returns the byte array this <code>DFFResourceArray</code> is wrapped around.
	 * The array should not be modified after this call.
	 * Subsequent calls to this method may not return the same array.
	 * @return a byte array containing this DFF structure.
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
	public synchronized boolean add(DFFResource r) throws DFFResourceAlreadyExistsException {
		if (r.name == null) r.name = "";
		if (r.data == null) r.data = new byte[0];
		switch (version) {
		case 1:
			if (((long)arr.length + 16L + (long)r.data.length) > Integer.MAX_VALUE) {
				throw new DFFResourceTooBigException();
			} else if (d1locate(r.type,r.id) > 0) {
				throw new DFFResourceAlreadyExistsException();
			} else {
				byte[] stuff = KSFLUtilities.paste(r.data, 0, 16);
				KSFLUtilities.putLong(stuff, 0, le, r.type);
				KSFLUtilities.putShort(stuff, 8, le, (short)r.id);
				KSFLUtilities.putInt(stuff, 12, le, r.data.length);
				arr = KSFLUtilities.paste(arr, arr.length, stuff);
				return true;
			}
		case 2:
			if (((long)arr.length + 21L + (long)gb(r.name).length + (long)r.data.length) > Integer.MAX_VALUE) {
				throw new DFFResourceTooBigException();
			} else if (d2locate(r.type,r.id) != null) {
				throw new DFFResourceAlreadyExistsException();
			} else {
				byte[] h = new byte[20];
				KSFLUtilities.putLong(h, 0, le, r.type);
				KSFLUtilities.putShort(h, 8, le, (short)(r.id & 0xFFFF));
				KSFLUtilities.putShort(h, 10, le, r.datatype);
				KSFLUtilities.putShort(h, 12, le, r.getAttributes());
				KSFLUtilities.putShort(h, 14, le, (short)((r.id >> 16) & 0xFFFF));
				KSFLUtilities.putInt(h, 16, le, r.data.length);
				byte[] n = gb(r.name);
				if (n.length > 255) n = KSFLUtilities.cut(n, 255, n.length-255);
				n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
				arr = KSFLUtilities.paste(arr, arr.length, r.data);
				arr = KSFLUtilities.paste(arr, d2dataSpace, n);
				arr = KSFLUtilities.paste(arr, d2nameSpace, h);
				d2dataSpace += h.length+n.length;
				d2nameSpace += h.length;
				KSFLUtilities.putInt(arr, 4, le, ++d2objectCount);
				return true;
			}
		case 3:
			if (((long)arr.length + 24L + ((r.name.length() > 0)?((long)gb(r.name).length+1L):0) + ((d3getType(r.type) == null)?(16L):0) + r.data.length) > Integer.MAX_VALUE) {
				throw new DFFResourceTooBigException();
			} else if (d3locate(r.type,r.id) != null) {
				throw new DFFResourceAlreadyExistsException();
			} else {
				int[] t = d3getType(r.type);
				int[] m = d3metrics();
				if (t == null) {
					if (m[0] == Integer.MAX_VALUE) {
						throw new DFFResourceTooBigException("Too many types.");
					} else {
						byte[] th = new byte[16];
						KSFLUtilities.putLong(th, 0, le, r.type);
						KSFLUtilities.putInt(th, 8, le, 0);
						KSFLUtilities.putInt(th, 12, le, m[4]); //offset to object table

						KSFLUtilities.putInt(arr, 4, le, m[0]+1);
						d3paste(m, null, m[1]+m[2], th, INSERTED_TYPE_RECORD, r.type, r.id);
						t = d3getType(r.type);
					}
				} else if (t[2] == Integer.MAX_VALUE) {
					throw new DFFResourceTooBigException("Too many objects of this type.");
				}

				byte[] h = new byte[24];
				KSFLUtilities.putInt(h, 0, le, r.id);
				KSFLUtilities.putShort(h, 4, le, r.datatype);
				KSFLUtilities.putShort(h, 6, le, r.getAttributes());
				KSFLUtilities.putInt(h, 8, le, (r.name.length() > 0)?m[6]:0); //offset to name
				KSFLUtilities.putUInt48(h, 12, le, (r.data.length > 0)?m[8]:0); //offset to data
				KSFLUtilities.putUInt48(h, 18, le, (long)r.data.length);

				KSFLUtilities.putInt(arr, t[0]+8, le, t[2]+1);
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
		return false;
	}
	
	@Override
	public synchronized boolean contains(long type, int id) {
		switch (version) {
		case 1:
			int d1i = d1locate(type,id);
			return (d1i > 0);
		case 2:
			int[] d2i = d2locate(type,id);
			return (d2i != null);
		case 3:
			int[] d3i = d3locate(type,id);
			return (d3i != null);
		}
		return false;
	}
	@Override
	public synchronized boolean contains(long type, String name) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			int[] d2i = d2locate(type,name);
			return (d2i != null);
		case 3:
			int[] d3i = d3locate(type,name);
			return (d3i != null);
		}
		return false;
	}
	
	@Override
	public synchronized DFFResource get(long type, int id) {
		switch (version) {
		case 1:
			int d1i = d1locate(type,id);
			if (d1i > 0) {
				long d1t = KSFLUtilities.getLong(arr, d1i, le);
				int d1id = KSFLUtilities.getShort(arr, d1i+8, le);
				int d1l = KSFLUtilities.getInt(arr, d1i+12, le);
				byte[] d1a = KSFLUtilities.copy(arr, d1i+16, d1l);
				return new DFFResource(d1t, d1id, d1a);
			}
			break;
		case 2:
			int[] d2i = d2locate(type,id);
			if (d2i != null) {
				long t = KSFLUtilities.getLong(arr, d2i[0], le);
				int i = (KSFLUtilities.getShort(arr, d2i[0]+8, le) & 0xFFFF) | ((KSFLUtilities.getShort(arr, d2i[0]+14, le) << 16) & 0xFFFF0000);
				short dt = KSFLUtilities.getShort(arr, d2i[0]+10, le);
				short f = KSFLUtilities.getShort(arr, d2i[0]+12, le);
				String n = ns(arr, d2i[1]+1, d2i[4]-1);
				byte[] d = KSFLUtilities.copy(arr, d2i[2], d2i[5]);
				return new DFFResource(t,i,dt,f,n,d);
			}
			break;
		case 3:
			int[] d3i = d3locate(type,id);
			if (d3i != null) {
				long t = KSFLUtilities.getLong(arr, d3i[0], le);
				int i = KSFLUtilities.getInt(arr, d3i[5], le);
				short dt = KSFLUtilities.getShort(arr, d3i[5]+4, le);
				short f = KSFLUtilities.getShort(arr, d3i[5]+6, le);
				String n = (d3i[7] > 0)?(ns(arr, d3i[7]+1, d3i[8]-1)):"";
				byte[] d = (d3i[9] > 0)?KSFLUtilities.copy(arr, d3i[9], d3i[10]):(new byte[0]);
				return new DFFResource(t,i,dt,f,n,d);
			}
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
			int[] d2i = d2locate(type,name);
			if (d2i != null) {
				long t = KSFLUtilities.getLong(arr, d2i[0], le);
				int id = (KSFLUtilities.getShort(arr, d2i[0]+8, le) & 0xFFFF) | ((KSFLUtilities.getShort(arr, d2i[0]+14, le) << 16) & 0xFFFF0000);
				short dt = KSFLUtilities.getShort(arr, d2i[0]+10, le);
				short f = KSFLUtilities.getShort(arr, d2i[0]+12, le);
				String n = ns(arr, d2i[1]+1, d2i[4]-1);
				byte[] d = KSFLUtilities.copy(arr, d2i[2], d2i[5]);
				return new DFFResource(t,id,dt,f,n,d);
			}
			break;
		case 3:
			int[] d3i = d3locate(type,name);
			if (d3i != null) {
				long t = KSFLUtilities.getLong(arr, d3i[0], le);
				int i = KSFLUtilities.getInt(arr, d3i[5], le);
				short dt = KSFLUtilities.getShort(arr, d3i[5]+4, le);
				short f = KSFLUtilities.getShort(arr, d3i[5]+6, le);
				String n = (d3i[7] > 0)?(ns(arr, d3i[7]+1, d3i[8]-1)):"";
				byte[] d = (d3i[9] > 0)?KSFLUtilities.copy(arr, d3i[9], d3i[10]):(new byte[0]);
				return new DFFResource(t,i,dt,f,n,d);
			}
			break;
		}
		return null;
	}
	
	@Override
	public synchronized DFFResource getAttributes(long type, int id) {
		switch (version) {
		case 1:
			int d1i = d1locate(type,id);
			if (d1i > 0) {
				long d1t = KSFLUtilities.getLong(arr, d1i, le);
				int d1id = KSFLUtilities.getShort(arr, d1i+8, le);
				return new DFFResource(d1t, d1id, new byte[0]);
			}
			break;
		case 2:
			int[] d2i = d2locate(type,id);
			if (d2i != null) {
				long t = KSFLUtilities.getLong(arr, d2i[0], le);
				int i = (KSFLUtilities.getShort(arr, d2i[0]+8, le) & 0xFFFF) | ((KSFLUtilities.getShort(arr, d2i[0]+14, le) << 16) & 0xFFFF0000);
				short dt = KSFLUtilities.getShort(arr, d2i[0]+10, le);
				short f = KSFLUtilities.getShort(arr, d2i[0]+12, le);
				String n = ns(arr, d2i[1]+1, d2i[4]-1);
				byte[] d = new byte[0];
				return new DFFResource(t,i,dt,f,n,d);
			}
			break;
		case 3:
			int[] d3i = d3locate(type,id);
			if (d3i != null) {
				long t = KSFLUtilities.getLong(arr, d3i[0], le);
				int i = KSFLUtilities.getInt(arr, d3i[5], le);
				short dt = KSFLUtilities.getShort(arr, d3i[5]+4, le);
				short f = KSFLUtilities.getShort(arr, d3i[5]+6, le);
				String n = (d3i[7] > 0)?(ns(arr, d3i[7]+1, d3i[8]-1)):"";
				byte[] d = new byte[0];
				return new DFFResource(t,i,dt,f,n,d);
			}
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
			int[] d2i = d2locate(type,name);
			if (d2i != null) {
				long t = KSFLUtilities.getLong(arr, d2i[0], le);
				int i = (KSFLUtilities.getShort(arr, d2i[0]+8, le) & 0xFFFF) | ((KSFLUtilities.getShort(arr, d2i[0]+14, le) << 16) & 0xFFFF0000);
				short dt = KSFLUtilities.getShort(arr, d2i[0]+10, le);
				short f = KSFLUtilities.getShort(arr, d2i[0]+12, le);
				String n = ns(arr, d2i[1]+1, d2i[4]-1);
				byte[] d = new byte[0];
				return new DFFResource(t,i,dt,f,n,d);
			}
			break;
		case 3:
			int[] d3i = d3locate(type,name);
			if (d3i != null) {
				long t = KSFLUtilities.getLong(arr, d3i[0], le);
				int i = KSFLUtilities.getInt(arr, d3i[5], le);
				short dt = KSFLUtilities.getShort(arr, d3i[5]+4, le);
				short f = KSFLUtilities.getShort(arr, d3i[5]+6, le);
				String n = (d3i[7] > 0)?(ns(arr, d3i[7]+1, d3i[8]-1)):"";
				byte[] d = new byte[0];
				return new DFFResource(t,i,dt,f,n,d);
			}
			break;
		}
		return null;
	}
	
	@Override
	public synchronized long getLength(long type, int id) {
		switch (version) {
		case 1:
			int d1i = d1locate(type,id);
			if (d1i > 0) {
				int d1l = KSFLUtilities.getInt(arr, d1i+12, le);
				return d1l;
			}
			break;
		case 2:
			int[] d2i = d2locate(type,id);
			if (d2i != null) {
				return d2i[5];
			}
			break;
		case 3:
			int[] d3i = d3locate(type,id);
			if (d3i != null) {
				return d3i[10];
			}
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
			int[] d2i = d2locate(type,name);
			if (d2i != null) {
				return d2i[5];
			}
			break;
		case 3:
			int[] d3i = d3locate(type,name);
			if (d3i != null) {
				return d3i[10];
			}
			break;
		}
		return 0;
	}
	
	@Override
	public synchronized byte[] getData(long type, int id) {
		switch (version) {
		case 1:
			int d1i = d1locate(type,id);
			if (d1i > 0) {
				int d1l = KSFLUtilities.getInt(arr, d1i+12, le);
				byte[] d1a = KSFLUtilities.copy(arr, d1i+16, d1l);
				return d1a;
			}
			break;
		case 2:
			int[] d2i = d2locate(type,id);
			if (d2i != null) {
				byte[] d = KSFLUtilities.copy(arr, d2i[2], d2i[5]);
				return d;
			}
			break;
		case 3:
			int[] d3i = d3locate(type,id);
			if (d3i != null) {
				byte[] d = (d3i[9] > 0)?KSFLUtilities.copy(arr, d3i[9], d3i[10]):(new byte[0]);
				return d;
			}
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
			int[] d2i = d2locate(type,name);
			if (d2i != null) {
				byte[] d = KSFLUtilities.copy(arr, d2i[2], d2i[5]);
				return d;
			}
			break;
		case 3:
			int[] d3i = d3locate(type,name);
			if (d3i != null) {
				byte[] d = (d3i[9] > 0)?KSFLUtilities.copy(arr, d3i[9], d3i[10]):(new byte[0]);
				return d;
			}
			break;
		}
		return null;
	}
	
	@Override
	public synchronized int read(long type, int id, long doffset, byte[] data, int off, int len) {
		switch (version) {
		case 1:
			int d1i = d1locate(type,id);
			if (d1i > 0) {
				int d1l = KSFLUtilities.getInt(arr, d1i+12, le);
				int begin = (int)(d1i+16+doffset);
				int end = (d1i+16+d1l);
				for (int d=off, s=begin; (d<off+len) && (s<end); d++, s++) {
					data[d] = arr[s];
				}
				return Math.max(0, Math.min(len, end-begin));
			}
			break;
		case 2:
			int[] d2i = d2locate(type,id);
			if (d2i != null) {
				int begin = (int)(d2i[2]+doffset);
				int end = (int)(d2i[2]+d2i[5]);
				for (int d=off, s=begin; (d<off+len) && (s<end); d++, s++) {
					data[d] = arr[s];
				}
				return Math.max(0, Math.min(len, end-begin));
			}
			break;
		case 3:
			int[] d3i = d3locate(type,id);
			if (d3i != null) {
				int begin = (int)(d3i[9]+doffset);
				int end = (int)(d3i[9]+d3i[10]);
				for (int d=off, s=begin; (d<off+len) && (s<end); d++, s++) {
					data[d] = arr[s];
				}
				return Math.max(0, Math.min(len, end-begin));
			}
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
			int[] d2i = d2locate(type,name);
			if (d2i != null) {
				int begin = (int)(d2i[2]+doffset);
				int end = (int)(d2i[2]+d2i[5]);
				for (int d=off, s=begin; (d<off+len) && (s<end); d++, s++) {
					data[d] = arr[s];
				}
				return Math.max(0, Math.min(len, end-begin));
			}
			break;
		case 3:
			int[] d3i = d3locate(type,name);
			if (d3i != null) {
				int begin = (int)(d3i[9]+doffset);
				int end = (int)(d3i[9]+d3i[10]);
				for (int d=off, s=begin; (d<off+len) && (s<end); d++, s++) {
					data[d] = arr[s];
				}
				return Math.max(0, Math.min(len, end-begin));
			}
			break;
		}
		return 0;
	}
	
	@Override
	public synchronized boolean remove(long type, int id) {
		switch (version) {
		case 1:
			int d1i = d1locate(type,id);
			if (d1i > 0) {
				int d1l = KSFLUtilities.getInt(arr, d1i+12, le);
				arr = KSFLUtilities.cut(arr, d1i, 16+d1l);
				return true;
			}
			break;
		case 2:
			int[] d2i = d2locate(type,id);
			if (d2i != null) {
				arr = KSFLUtilities.cut(arr, d2i[2], d2i[5]);
				arr = KSFLUtilities.cut(arr, d2i[1], d2i[4]);
				arr = KSFLUtilities.cut(arr, d2i[0], d2i[3]);
				d2dataSpace -= (d2i[3]+d2i[4]);
				d2nameSpace -= d2i[3];
				KSFLUtilities.putInt(arr, 4, le, --d2objectCount);
				return true;
			}
			break;
		case 3:
			int[] d3i = d3locate(type,id);
			if (d3i != null) {
				if (d3i[9] > 0) d3cut(null, d3i, d3i[9], d3i[10], REMOVED_DATA, type, id);
				if (d3i[7] > 0) d3cut(null, d3i, d3i[7], d3i[8], REMOVED_NAME, type, id);
				if (d3i[2] < 2) {
					KSFLUtilities.putInt(arr, 4, le, KSFLUtilities.getInt(arr, 4, le)-1);
					d3cut(null, d3i, d3i[0], d3i[1], REMOVED_TYPE_RECORD, type, id);
				} else {
					KSFLUtilities.putInt(arr, d3i[0]+8, le, d3i[2]-1);
				}
				if (d3i[5] > 0) d3cut(null, d3i, d3i[5], d3i[6], REMOVED_OBJECT_RECORD, type, id);
				return true;
			}
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
			int[] d2i = d2locate(type,name);
			if (d2i != null) {
				arr = KSFLUtilities.cut(arr, d2i[2], d2i[5]);
				arr = KSFLUtilities.cut(arr, d2i[1], d2i[4]);
				arr = KSFLUtilities.cut(arr, d2i[0], d2i[3]);
				d2dataSpace -= (d2i[3]+d2i[4]);
				d2nameSpace -= d2i[3];
				KSFLUtilities.putInt(arr, 4, le, --d2objectCount);
				return true;
			}
			break;
		case 3:
			int[] d3i = d3locate(type,name);
			if (d3i != null) {
				int id = KSFLUtilities.getInt(arr, d3i[5], le);
				if (d3i[9] > 0) d3cut(null, d3i, d3i[9], d3i[10], REMOVED_DATA, type, id);
				if (d3i[7] > 0) d3cut(null, d3i, d3i[7], d3i[8], REMOVED_NAME, type, id);
				if (d3i[2] < 2) {
					KSFLUtilities.putInt(arr, 4, le, KSFLUtilities.getInt(arr, 4, le)-1);
					d3cut(null, d3i, d3i[0], d3i[1], REMOVED_TYPE_RECORD, type, id);
				} else {
					KSFLUtilities.putInt(arr, d3i[0]+8, le, d3i[2]-1);
				}
				if (d3i[5] > 0) d3cut(null, d3i, d3i[5], d3i[6], REMOVED_OBJECT_RECORD, type, id);
				return true;
			}
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
			int d1i = d1locate(type,id);
			if (d1i > 0) {
				int d1e = d1locate(r.type,r.id);
				if ((d1e > 0) && (d1e != d1i)) {
					throw new DFFResourceAlreadyExistsException();
				} else {
					KSFLUtilities.putLong(arr, d1i, le, r.type);
					KSFLUtilities.putShort(arr, d1i+8, le, (short)r.id);
					return true;
				}
			}
			break;
		case 2:
			int[] d2i = d2locate(type,id);
			if (d2i != null) {
				int[] d2e = d2locate(r.type,r.id);
				if ((d2e != null) && ((d2e[0] != d2i[0]) || (d2e[1] != d2i[1]) || (d2e[2] != d2i[2]))) {
					throw new DFFResourceAlreadyExistsException();
				} else if (((long)arr.length - (long)d2i[4] + 1L + (long)gb(r.name).length) > (long)Integer.MAX_VALUE) {
					throw new DFFResourceTooBigException();
				} else {
					KSFLUtilities.putLong(arr, d2i[0], le, r.type);
					KSFLUtilities.putShort(arr, d2i[0]+8, le, (short)(r.id & 0xFFFF));
					KSFLUtilities.putShort(arr, d2i[0]+10, le, r.datatype);
					KSFLUtilities.putShort(arr, d2i[0]+12, le, r.getAttributes());
					KSFLUtilities.putShort(arr, d2i[0]+14, le, (short)((r.id >> 16) & 0xFFFF));
					byte[] n = gb(r.name);
					if (n.length > 255) n = KSFLUtilities.cut(n, 255, n.length-255);
					n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
					arr = KSFLUtilities.cut(arr, d2i[1], d2i[4]);
					arr = KSFLUtilities.paste(arr, d2i[1], n);
					d2dataSpace = d2dataSpace - d2i[4] + n.length;
					return true;
				}
			}
			break;
		case 3:
			int[] d3i = d3locate(type,id);
			if (d3i != null) {
				int[] d3e = d3locate(r.type,r.id);
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
				} else if (((long)arr.length - (long)d3i[8] + 1L + (long)gb(r.name).length) > (long)Integer.MAX_VALUE) {
					throw new DFFResourceTooBigException();
				} else {
					if (type != r.type) {
						int[] m = d3metrics();
						// add type entry for new type if necessary
						int[] t = d3getType(r.type);
						if (t == null) {
							if (m[0] == Integer.MAX_VALUE) {
								throw new DFFResourceTooBigException("Too many types.");
							} else {
								byte[] th = new byte[16];
								KSFLUtilities.putLong(th, 0, le, r.type);
								KSFLUtilities.putInt(th, 8, le, 0);
								KSFLUtilities.putInt(th, 12, le, m[4]);
								KSFLUtilities.putInt(arr, 4, le, ++m[0]);
								d3paste(m, d3i, m[1]+m[2], th, INSERTED_TYPE_RECORD, r.type, r.id);
								t = d3getType(r.type);
							}
						} else if (t[2] == Integer.MAX_VALUE) {
							throw new DFFResourceTooBigException("Too many objects of this type.");
						}
						// increment new object count
						// add new object record
						byte[] h = new byte[24];
						KSFLUtilities.putInt(h, 0, le, r.id);
						KSFLUtilities.putShort(h, 4, le, r.datatype);
						KSFLUtilities.putShort(h, 6, le, r.getAttributes());
						KSFLUtilities.putInt(h, 8, le, d3i[7]);
						KSFLUtilities.putUInt48(h, 12, le, d3i[9]);
						KSFLUtilities.putUInt48(h, 18, le, d3i[10]);
						KSFLUtilities.putInt(arr, t[0]+8, le, t[2]+1);
						d3paste(m, d3i, t[3]+t[4], h, INSERTED_OBJECT_RECORD, r.type, r.id);
						// decrement old object count
						// delete type entry for old type if necessary
						// remove old object record
						if (d3i[2] < 2) {
							KSFLUtilities.putInt(arr, 4, le, --m[0]);
							d3cut(m, d3i, d3i[0], d3i[1], REMOVED_TYPE_RECORD, type, id);
						} else {
							KSFLUtilities.putInt(arr, d3i[0]+8, le, d3i[2]-1);
						}
						if (d3i[5] > 0) d3cut(m, d3i, d3i[5], d3i[6], REMOVED_OBJECT_RECORD, type, id);
					} else {
						int[] t = d3getType(r.type);
						KSFLUtilities.putInt(arr, t[0]+8, le, t[2]-1);
						d3cut(null, d3i, d3i[5], d3i[6], REMOVED_OBJECT_RECORD, type, id);
						byte[] h = new byte[24];
						KSFLUtilities.putInt(h, 0, le, r.id);
						KSFLUtilities.putShort(h, 4, le, r.datatype);
						KSFLUtilities.putShort(h, 6, le, r.getAttributes());
						KSFLUtilities.putInt(h, 8, le, d3i[7]);
						KSFLUtilities.putUInt48(h, 12, le, d3i[9]);
						KSFLUtilities.putUInt48(h, 18, le, d3i[10]);
						KSFLUtilities.putInt(arr, t[0]+8, le, t[2]);
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
							KSFLUtilities.putInt(arr, d3i[5]+8, le, (d3i[7] = d3metrics()[6]));
							d3paste(null, d3i, d3i[7], n, INSERTED_NAME, r.type, r.id);
						}
						d3i[8] = n.length;
					} else if (d3i[7] > 0) {
						d3cut(null, d3i, d3i[7], d3i[8], REMOVED_NAME, r.type, r.id);
						KSFLUtilities.putInt(arr, d3i[5]+8, le, (d3i[7] = 0));
						d3i[8] = 0;
					}
					return true;
				}
			}
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
			int[] d2i = d2locate(type,name);
			if (d2i != null) {
				int[] d2e = d2locate(r.type,r.id);
				if ((d2e != null) && ((d2e[0] != d2i[0]) || (d2e[1] != d2i[1]) || (d2e[2] != d2i[2]))) {
					throw new DFFResourceAlreadyExistsException();
				} else if (((long)arr.length - (long)d2i[4] + 1L + (long)gb(r.name).length) > (long)Integer.MAX_VALUE) {
					throw new DFFResourceTooBigException();
				} else {
					KSFLUtilities.putLong(arr, d2i[0], le, r.type);
					KSFLUtilities.putShort(arr, d2i[0]+8, le, (short)(r.id & 0xFFFF));
					KSFLUtilities.putShort(arr, d2i[0]+10, le, r.datatype);
					KSFLUtilities.putShort(arr, d2i[0]+12, le, r.getAttributes());
					KSFLUtilities.putShort(arr, d2i[0]+14, le, (short)((r.id >> 16) & 0xFFFF));
					byte[] n = gb(r.name);
					if (n.length > 255) n = KSFLUtilities.cut(n, 255, n.length-255);
					n = KSFLUtilities.paste(n, 0, new byte[]{(byte)n.length});
					arr = KSFLUtilities.cut(arr, d2i[1], d2i[4]);
					arr = KSFLUtilities.paste(arr, d2i[1], n);
					d2dataSpace = d2dataSpace - d2i[4] + n.length;
					return true;
				}
			}
			break;
		case 3:
			int[] d3i = d3locate(type,name);
			if (d3i != null) {
				int[] d3e = d3locate(r.type,r.id);
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
				} else if (((long)arr.length - (long)d3i[8] + 1L + (long)gb(r.name).length) > (long)Integer.MAX_VALUE) {
					throw new DFFResourceTooBigException();
				} else {
					int id = KSFLUtilities.getInt(arr, d3i[5], le);
					if (type != r.type) {
						int[] m = d3metrics();
						// add type entry for new type if necessary
						int[] t = d3getType(r.type);
						if (t == null) {
							if (m[0] == Integer.MAX_VALUE) {
								throw new DFFResourceTooBigException("Too many types.");
							} else {
								byte[] th = new byte[16];
								KSFLUtilities.putLong(th, 0, le, r.type);
								KSFLUtilities.putInt(th, 8, le, 0);
								KSFLUtilities.putInt(th, 12, le, m[4]);
								KSFLUtilities.putInt(arr, 4, le, ++m[0]);
								d3paste(m, d3i, m[1]+m[2], th, INSERTED_TYPE_RECORD, r.type, r.id);
								t = d3getType(r.type);
							}
						} else if (t[2] == Integer.MAX_VALUE) {
							throw new DFFResourceTooBigException("Too many objects of this type.");
						}
						// increment new object count
						// add new object record
						byte[] h = new byte[24];
						KSFLUtilities.putInt(h, 0, le, r.id);
						KSFLUtilities.putShort(h, 4, le, r.datatype);
						KSFLUtilities.putShort(h, 6, le, r.getAttributes());
						KSFLUtilities.putInt(h, 8, le, d3i[7]);
						KSFLUtilities.putUInt48(h, 12, le, d3i[9]);
						KSFLUtilities.putUInt48(h, 18, le, d3i[10]);
						KSFLUtilities.putInt(arr, t[0]+8, le, t[2]+1);
						d3paste(m, d3i, t[3]+t[4], h, INSERTED_OBJECT_RECORD, r.type, r.id);
						// decrement old object count
						// delete type entry for old type if necessary
						// remove old object record
						if (d3i[2] < 2) {
							KSFLUtilities.putInt(arr, 4, le, --m[0]);
							d3cut(m, d3i, d3i[0], d3i[1], REMOVED_TYPE_RECORD, type, id);
						} else {
							KSFLUtilities.putInt(arr, d3i[0]+8, le, d3i[2]-1);
						}
						if (d3i[5] > 0) d3cut(m, d3i, d3i[5], d3i[6], REMOVED_OBJECT_RECORD, type, id);
					} else {
						int[] t = d3getType(r.type);
						KSFLUtilities.putInt(arr, t[0]+8, le, t[2]-1);
						d3cut(null, d3i, d3i[5], d3i[6], REMOVED_OBJECT_RECORD, type, id);
						byte[] h = new byte[24];
						KSFLUtilities.putInt(h, 0, le, r.id);
						KSFLUtilities.putShort(h, 4, le, r.datatype);
						KSFLUtilities.putShort(h, 6, le, r.getAttributes());
						KSFLUtilities.putInt(h, 8, le, d3i[7]);
						KSFLUtilities.putUInt48(h, 12, le, d3i[9]);
						KSFLUtilities.putUInt48(h, 18, le, d3i[10]);
						KSFLUtilities.putInt(arr, t[0]+8, le, t[2]);
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
							KSFLUtilities.putInt(arr, d3i[5]+8, le, (d3i[7] = d3metrics()[6]));
							d3paste(null, d3i, d3i[7], n, INSERTED_NAME, r.type, r.id);
						}
						d3i[8] = n.length;
					} else if (d3i[7] > 0) {
						d3cut(null, d3i, d3i[7], d3i[8], REMOVED_NAME, r.type, r.id);
						KSFLUtilities.putInt(arr, d3i[5]+8, le, (d3i[7] = 0));
						d3i[8] = 0;
					}
					return true;
				}
			}
			break;
		}
		return false;
	}
	
	@Override
	public synchronized boolean setLength(long type, int id, long len) {
		switch (version) {
		case 1:
			int d1i = d1locate(type,id);
			if (d1i > 0) {
				int d1l = KSFLUtilities.getInt(arr, d1i+12, le);
				if (((long)arr.length - (long)d1l + len) > (long)Integer.MAX_VALUE) {
					throw new DFFResourceTooBigException();
				}
				KSFLUtilities.putInt(arr, d1i+12, le, (int)len);
				if (len < d1l) {
					arr = KSFLUtilities.cut(arr, (int)(d1i+16+len), (int)(d1l-len));
				} else {
					arr = KSFLUtilities.paste(arr, (int)(d1i+16+d1l), (int)(len-d1l));
				}
				return true;
			}
			break;
		case 2:
			int[] d2i = d2locate(type,id);
			if (d2i != null) {
				if (((long)arr.length - (long)d2i[5] + len) > (long)Integer.MAX_VALUE) {
					throw new DFFResourceTooBigException();
				}
				KSFLUtilities.putInt(arr, d2i[0]+16, le, (int)len);
				if (len < d2i[5]) {
					arr = KSFLUtilities.cut(arr, (int)(d2i[2]+len), (int)(d2i[5]-len));
				} else {
					arr = KSFLUtilities.paste(arr, (int)(d2i[2]+d2i[5]), (int)(len-d2i[5]));
				}
				return true;
			}
			break;
		case 3:
			int[] d3i = d3locate(type,id);
			if (d3i != null) {
				if (((long)arr.length - (long)d3i[10] + len) > (long)Integer.MAX_VALUE) {
					throw new DFFResourceTooBigException();
				}
				KSFLUtilities.putUInt48(arr, d3i[5]+18, le, len);
				if (len < d3i[10]) {
					d3cut(null, d3i, (int)(d3i[9]+len), (int)(d3i[10]-len), REMOVED_DATA, type, id);
				} else {
					d3paste(null, d3i, (int)(d3i[9]+d3i[10]), (int)(len-d3i[10]), INSERTED_DATA, type, id);
				}
				return true;
			}
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
			int[] d2i = d2locate(type,name);
			if (d2i != null) {
				if (((long)arr.length - (long)d2i[5] + len) > (long)Integer.MAX_VALUE) {
					throw new DFFResourceTooBigException();
				}
				KSFLUtilities.putInt(arr, d2i[0]+16, le, (int)len);
				if (len < d2i[5]) {
					arr = KSFLUtilities.cut(arr, (int)(d2i[2]+len), (int)(d2i[5]-len));
				} else {
					arr = KSFLUtilities.paste(arr, (int)(d2i[2]+d2i[5]), (int)(len-d2i[5]));
				}
				return true;
			}
			break;
		case 3:
			int[] d3i = d3locate(type,name);
			if (d3i != null) {
				int id = KSFLUtilities.getInt(arr, d3i[5], le);
				if (((long)arr.length - (long)d3i[10] + len) > (long)Integer.MAX_VALUE) {
					throw new DFFResourceTooBigException();
				}
				KSFLUtilities.putUInt48(arr, d3i[5]+18, le, len);
				if (len < d3i[10]) {
					d3cut(null, d3i, (int)(d3i[9]+len), (int)(d3i[10]-len), REMOVED_DATA, type, id);
				} else {
					d3paste(null, d3i, (int)(d3i[9]+d3i[10]), (int)(len-d3i[10]), INSERTED_DATA, type, id);
				}
				return true;
			}
			break;
		}
		return false;
	}
	
	@Override
	public synchronized boolean setData(long type, int id, byte[] data) {
		switch (version) {
		case 1:
			int d1i = d1locate(type,id);
			if (d1i > 0) {
				int d1l = KSFLUtilities.getInt(arr, d1i+12, le);
				if (((long)arr.length - (long)d1l + (long)data.length) > (long)Integer.MAX_VALUE) {
					throw new DFFResourceTooBigException();
				}
				KSFLUtilities.putInt(arr, d1i+12, le, data.length);
				arr = KSFLUtilities.cut(arr, d1i+16, d1l);
				arr = KSFLUtilities.paste(arr, d1i+16, data);
				return true;
			}
			break;
		case 2:
			int[] d2i = d2locate(type,id);
			if (d2i != null) {
				if (((long)arr.length - (long)d2i[5] + (long)data.length) > (long)Integer.MAX_VALUE) {
					throw new DFFResourceTooBigException();
				}
				KSFLUtilities.putInt(arr, d2i[0]+16, le, data.length);
				arr = KSFLUtilities.cut(arr, d2i[2], d2i[5]);
				arr = KSFLUtilities.paste(arr, d2i[2], data);
				return true;
			}
			break;
		case 3:
			int[] d3i = d3locate(type,id);
			if (d3i != null) {
				if (d3i[9] > 0) d3cut(null, d3i, d3i[9], d3i[10], REMOVED_DATA, type, id);
				if (data.length > 0) {
					if (d3i[9] <= 0) KSFLUtilities.putUInt48(arr, d3i[5]+12, le, (d3i[9] = d3metrics()[8]));
					KSFLUtilities.putUInt48(arr, d3i[5]+18, le, data.length);
					d3paste(null, d3i, d3i[9], data, INSERTED_DATA, type, id);
				} else if (d3i[9] > 0) {
					KSFLUtilities.putUInt48(arr, d3i[5]+18, le, 0);
				}
				return true;
			}
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
			int[] d2i = d2locate(type,name);
			if (d2i != null) {
				if (((long)arr.length - (long)d2i[5] + (long)data.length) > (long)Integer.MAX_VALUE) {
					throw new DFFResourceTooBigException();
				}
				KSFLUtilities.putInt(arr, d2i[0]+16, le, data.length);
				arr = KSFLUtilities.cut(arr, d2i[2], d2i[5]);
				arr = KSFLUtilities.paste(arr, d2i[2], data);
				return true;
			}
			break;
		case 3:
			int[] d3i = d3locate(type,name);
			if (d3i != null) {
				int id = KSFLUtilities.getInt(arr, d3i[5], le);
				if (d3i[9] > 0) d3cut(null, d3i, d3i[9], d3i[10], REMOVED_DATA, type, id);
				if (data.length > 0) {
					if (d3i[9] <= 0) KSFLUtilities.putUInt48(arr, d3i[5]+12, le, (d3i[9] = d3metrics()[8]));
					KSFLUtilities.putUInt48(arr, d3i[5]+18, le, data.length);
					d3paste(null, d3i, d3i[9], data, INSERTED_DATA, type, id);
				} else if (d3i[9] > 0) {
					KSFLUtilities.putUInt48(arr, d3i[5]+18, le, 0);
				}
				return true;
			}
			break;
		}
		return false;
	}
	
	@Override
	public synchronized int write(long type, int id, long doffset, byte[] data, int off, int len) {
		switch (version) {
		case 1:
			int d1i = d1locate(type,id);
			if (d1i > 0) {
				int d1l = KSFLUtilities.getInt(arr, d1i+12, le);
				if (doffset+len > d1l) {
					if (((long)arr.length - (long)d1l + doffset + (long)len) > (long)Integer.MAX_VALUE) {
						throw new DFFResourceTooBigException();
					}
					if (doffset > d1l) {
						arr = KSFLUtilities.paste(arr, d1i+16+d1l, (int)(doffset-d1l));
					} else {
						arr = KSFLUtilities.cut(arr, (int)(d1i+16+doffset), (int)(d1l-doffset));
					}
					arr = KSFLUtilities.paste(arr, (int)(d1i+16+doffset), KSFLUtilities.copy(data, off, len));
					KSFLUtilities.putInt(arr, d1i+12, le, (int)(doffset+len));
				} else {
					arr = KSFLUtilities.cut(arr, (int)(d1i+16+doffset), len);
					arr = KSFLUtilities.paste(arr, (int)(d1i+16+doffset), KSFLUtilities.copy(data, off, len));
				}
				return len;
			}
			break;
		case 2:
			int[] d2i = d2locate(type,id);
			if (d2i != null) {
				if (doffset+len > d2i[5]) {
					if (((long)arr.length - (long)d2i[5] + doffset + (long)len) > (long)Integer.MAX_VALUE) {
						throw new DFFResourceTooBigException();
					}
					if (doffset > d2i[5]) {
						arr = KSFLUtilities.paste(arr, d2i[2]+d2i[5], (int)(doffset-d2i[5]));
					} else {
						arr = KSFLUtilities.cut(arr, (int)(d2i[2]+doffset), (int)(d2i[5]-doffset));
					}
					arr = KSFLUtilities.paste(arr, (int)(d2i[2]+doffset), KSFLUtilities.copy(data, off, len));
					KSFLUtilities.putInt(arr, d2i[0]+16, le, (int)(doffset+len));
				} else {
					arr = KSFLUtilities.cut(arr, (int)(d2i[2]+doffset), len);
					arr = KSFLUtilities.paste(arr, (int)(d2i[2]+doffset), KSFLUtilities.copy(data, off, len));
				}
				return len;
			}
			break;
		case 3:
			int[] d3i = d3locate(type,id);
			if (d3i != null) {
				if (doffset+len > d3i[10]) {
					if (((long)arr.length - (long)d3i[10] + doffset + (long)len) > (long)Integer.MAX_VALUE) {
						throw new DFFResourceTooBigException();
					}
					KSFLUtilities.putUInt48(arr, d3i[5]+18, le, (long)(doffset+len));
					if (doffset > d3i[10]) {
						d3paste(null, d3i, d3i[9]+d3i[10], (int)(doffset-d3i[10]), INSERTED_DATA, type, id);
					} else {
						d3cut(null, d3i, (int)(d3i[9]+doffset), (int)(d3i[10]-doffset), INSERTED_DATA, type, id);
					}
					d3paste(null, d3i, (int)(d3i[9]+doffset), KSFLUtilities.copy(data, off, len), INSERTED_DATA, type, id);
				} else {
					arr = KSFLUtilities.cut(arr, (int)(d3i[9]+doffset), len);
					arr = KSFLUtilities.paste(arr, (int)(d3i[9]+doffset), KSFLUtilities.copy(data, off, len));
				}
				return len;
			}
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
			int[] d2i = d2locate(type,name);
			if (d2i != null) {
				if (doffset+len > d2i[5]) {
					if (((long)arr.length - (long)d2i[5] + doffset + (long)len) > (long)Integer.MAX_VALUE) {
						throw new DFFResourceTooBigException();
					}
					if (doffset > d2i[5]) {
						arr = KSFLUtilities.paste(arr, d2i[2]+d2i[5], (int)(doffset-d2i[5]));
					} else {
						arr = KSFLUtilities.cut(arr, (int)(d2i[2]+doffset), (int)(d2i[5]-doffset));
					}
					arr = KSFLUtilities.paste(arr, (int)(d2i[2]+doffset), KSFLUtilities.copy(data, off, len));
					KSFLUtilities.putInt(arr, d2i[0]+16, le, (int)(doffset+len));
				} else {
					arr = KSFLUtilities.cut(arr, (int)(d2i[2]+doffset), len);
					arr = KSFLUtilities.paste(arr, (int)(d2i[2]+doffset), KSFLUtilities.copy(data, off, len));
				}
				return len;
			}
			break;
		case 3:
			int[] d3i = d3locate(type,name);
			if (d3i != null) {
				int id = KSFLUtilities.getInt(arr, d3i[5], le);
				if (doffset+len > d3i[10]) {
					if (((long)arr.length - (long)d3i[10] + doffset + (long)len) > (long)Integer.MAX_VALUE) {
						throw new DFFResourceTooBigException();
					}
					KSFLUtilities.putUInt48(arr, d3i[5]+18, le, (long)(doffset+len));
					if (doffset > d3i[10]) {
						d3paste(null, d3i, d3i[9]+d3i[10], (int)(doffset-d3i[10]), INSERTED_DATA, type, id);
					} else {
						d3cut(null, d3i, (int)(d3i[9]+doffset), (int)(d3i[10]-doffset), INSERTED_DATA, type, id);
					}
					d3paste(null, d3i, (int)(d3i[9]+doffset), KSFLUtilities.copy(data, off, len), INSERTED_DATA, type, id);
				} else {
					arr = KSFLUtilities.cut(arr, (int)(d3i[9]+doffset), len);
					arr = KSFLUtilities.paste(arr, (int)(d3i[9]+doffset), KSFLUtilities.copy(data, off, len));
				}
				return len;
			}
			break;
		}
		return 0;
	}
	
	@Override
	public synchronized int getTypeCount() {
		switch (version) {
		case 1:
			List<Long> d1s = new ArrayList<Long>();
			int d1i = 4;
			while (d1i+16 <= arr.length) {
				long ty = KSFLUtilities.getLong(arr, d1i, le);
				if (!d1s.contains(ty)) d1s.add(ty);
				d1i += 16+KSFLUtilities.getInt(arr, d1i+12, le);
			}
			return d1s.size();
		case 2:
			List<Long> d2s = new ArrayList<Long>();
			int d2i = 0;
			int d2h = d2headerSpace;
			while (d2i < d2objectCount && d2h < d2nameSpace) {
				long ty = KSFLUtilities.getLong(arr, d2h, le);
				if (!d2s.contains(ty)) d2s.add(ty);
				d2h += 20;
				d2i++;
			}
			return d2s.size();
		case 3:
			int d3typeCnt = KSFLUtilities.getInt(arr, 4, le);
			return d3typeCnt;
		}
		return 0;
	}
	@Override
	public synchronized long getType(int index) {
		switch (version) {
		case 1:
			List<Long> d1s = new ArrayList<Long>();
			int d1i = 4;
			while (d1i+16 <= arr.length) {
				long ty = KSFLUtilities.getLong(arr, d1i, le);
				if (!d1s.contains(ty)) {
					if (d1s.size() == index) return ty;
					else d1s.add(ty);
				}
				d1i += 16+KSFLUtilities.getInt(arr, d1i+12, le);
			}
			break;
		case 2:
			List<Long> d2s = new ArrayList<Long>();
			int d2i = 0;
			int d2h = d2headerSpace;
			while (d2i < d2objectCount && d2h < d2nameSpace) {
				long ty = KSFLUtilities.getLong(arr, d2h, le);
				if (!d2s.contains(ty)) {
					if (d2s.size() == index) return ty;
					else d2s.add(ty);
				}
				d2h += 20;
				d2i++;
			}
			break;
		case 3:
			int d3typeCnt = KSFLUtilities.getInt(arr, 4, le);
			if (index >= 0 && index < d3typeCnt) {
				long t = KSFLUtilities.getLong(arr, 8+16*index, le);
				return t;
			}
			break;
		}
		return 0;
	}
	@Override
	public synchronized long[] getTypes() {
		switch (version) {
		case 1:
			List<Long> d1s = new ArrayList<Long>();
			int d1i = 4;
			while (d1i+16 <= arr.length) {
				long ty = KSFLUtilities.getLong(arr, d1i, le);
				if (!d1s.contains(ty)) d1s.add(ty);
				d1i += 16+KSFLUtilities.getInt(arr, d1i+12, le);
			}
			long[] d1a = new long[d1s.size()];
			Iterator<Long> d1si = d1s.iterator();
			int d1ai = 0;
			while (d1si.hasNext()) d1a[d1ai++] = d1si.next();
			return d1a;
		case 2:
			List<Long> d2s = new ArrayList<Long>();
			int d2i = 0;
			int d2h = d2headerSpace;
			while (d2i < d2objectCount && d2h < d2nameSpace) {
				long ty = KSFLUtilities.getLong(arr, d2h, le);
				if (!d2s.contains(ty)) d2s.add(ty);
				d2h += 20;
				d2i++;
			}
			long[] d2a = new long[d2s.size()];
			Iterator<Long> d2si = d2s.iterator();
			int d2ai = 0;
			while (d2si.hasNext()) d2a[d2ai++] = d2si.next();
			return d2a;
		case 3:
			int d3typeCnt = KSFLUtilities.getInt(arr, 4, le);
			long[] d3a = new long[d3typeCnt];
			for (int d3i = 0; d3i < d3typeCnt; d3i++) {
				d3a[d3i] = KSFLUtilities.getLong(arr, 8+16*d3i, le);
			}
			return d3a;
		}
		return null;
	}
	
	@Override
	public synchronized int getResourceCount(long type) {
		switch (version) {
		case 1:
			List<Integer> d1s = new ArrayList<Integer>();
			int d1i = 4;
			while (d1i+16 <= arr.length) {
				if (KSFLUtilities.getLong(arr, d1i, le) == type) {
					int id = KSFLUtilities.getShort(arr, d1i+8, le);
					if (!d1s.contains(id)) d1s.add(id);
				}
				d1i += 16+KSFLUtilities.getInt(arr, d1i+12, le);
			}
			return d1s.size();
		case 2:
			List<Integer> d2s = new ArrayList<Integer>();
			int d2i = 0;
			int d2h = d2headerSpace;
			while (d2i < d2objectCount && d2h < d2nameSpace) {
				if (KSFLUtilities.getLong(arr, d2h, le) == type) {
					int id = (KSFLUtilities.getShort(arr, d2h+8, le) & 0xFFFF) | ((KSFLUtilities.getShort(arr, d2h+14, le) << 16) & 0xFFFF0000);
					d2s.add(id);
				}
				d2h += 20;
				d2i++;
			}
			return d2s.size();
		case 3:
			int[] d3t = d3getType(type);
			if (d3t != null) return d3t[2];
			break;
		}
		return 0;
	}
	@Override
	public synchronized int getID(long type, int index) {
		switch (version) {
		case 1:
			List<Integer> d1s = new ArrayList<Integer>();
			int d1i = 4;
			while (d1i+16 <= arr.length) {
				if (KSFLUtilities.getLong(arr, d1i, le) == type) {
					int id = KSFLUtilities.getShort(arr, d1i+8, le);
					if (!d1s.contains(id)) {
						if (d1s.size() == index) return id;
						else d1s.add(id);
					}
				}
				d1i += 16+KSFLUtilities.getInt(arr, d1i+12, le);
			}
			break;
		case 2:
			List<Integer> d2s = new ArrayList<Integer>();
			int d2i = 0;
			int d2h = d2headerSpace;
			while (d2i < d2objectCount && d2h < d2nameSpace) {
				if (KSFLUtilities.getLong(arr, d2h, le) == type) {
					int id = (KSFLUtilities.getShort(arr, d2h+8, le) & 0xFFFF) | ((KSFLUtilities.getShort(arr, d2h+14, le) << 16) & 0xFFFF0000);
					if (d2s.size() == index) return id;
					else d2s.add(id);
				}
				d2h += 20;
				d2i++;
			}
			break;
		case 3:
			int[] d3t = d3getType(type);
			if (d3t != null) {
				if (index >= 0 && index < d3t[2]) {
					int id = KSFLUtilities.getInt(arr, d3t[3]+24*index, le);
					return id;
				}
			}
			break;
		}
		return 0;
	}
	@Override
	public synchronized int[] getIDs(long type) {
		switch (version) {
		case 1:
			List<Integer> d1s = new ArrayList<Integer>();
			int d1i = 4;
			while (d1i+16 <= arr.length) {
				if (KSFLUtilities.getLong(arr, d1i, le) == type) {
					int id = KSFLUtilities.getShort(arr, d1i+8, le);
					if (!d1s.contains(id)) d1s.add(id);
				}
				d1i += 16+KSFLUtilities.getInt(arr, d1i+12, le);
			}
			int[] d1a = new int[d1s.size()];
			Iterator<Integer> d1si = d1s.iterator();
			int d1ai = 0;
			while (d1si.hasNext()) d1a[d1ai++] = d1si.next();
			return d1a;
		case 2:
			List<Integer> d2s = new ArrayList<Integer>();
			int d2i = 0;
			int d2h = d2headerSpace;
			while (d2i < d2objectCount && d2h < d2nameSpace) {
				if (KSFLUtilities.getLong(arr, d2h, le) == type) {
					int id = (KSFLUtilities.getShort(arr, d2h+8, le) & 0xFFFF) | ((KSFLUtilities.getShort(arr, d2h+14, le) << 16) & 0xFFFF0000);
					d2s.add(id);
				}
				d2h += 20;
				d2i++;
			}
			int[] d2a = new int[d2s.size()];
			Iterator<Integer> d2si = d2s.iterator();
			int d2ai = 0;
			while (d2si.hasNext()) d2a[d2ai++] = d2si.next();
			return d2a;
		case 3:
			int[] d3t = d3getType(type);
			if (d3t != null) {
				int[] d3a = new int[d3t[2]];
				for (int d3i = 0; d3i < d3t[2]; d3i++) {
					d3a[d3i] = KSFLUtilities.getInt(arr, d3t[3]+24*d3i, le);
				}
				return d3a;
			} else {
				return new int[0];
			}
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
			int d2h = d2headerSpace;
			int d2n = d2nameSpace;
			while (d2i < d2objectCount && d2h < d2nameSpace && d2n < d2dataSpace) {
				if (KSFLUtilities.getLong(arr, d2h, le) == type) {
					String n = (ns(arr, d2n+1, arr[d2n]&0xFF));
					if (d2s.size() == index) {
						return n;
					}
					else d2s.add(n);
				}
				d2n += 1+(arr[d2n] & 0xFF);
				d2h += 20;
				d2i++;
			}
			break;
		case 3:
			int[] d3t = d3getType(type);
			if (d3t != null) {
				if (index >= 0 && index < d3t[2]) {
					int nameOfst = KSFLUtilities.getInt(arr, d3t[3]+24*index+8, le);
					int nameLen = (nameOfst>0)?((arr[nameOfst]&0xFF)+1):0;
					String n = (nameOfst>0)?(ns(arr,nameOfst+1,nameLen-1)):"";
					return n;
				}
			}
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
			int d2h = d2headerSpace;
			int d2n = d2nameSpace;
			while (d2i < d2objectCount && d2h < d2nameSpace && d2n < d2dataSpace) {
				if (KSFLUtilities.getLong(arr, d2h, le) == type) {
					String n = (ns(arr, d2n+1, arr[d2n]&0xFF));
					d2s.add(n);
				}
				d2n += 1+(arr[d2n] & 0xFF);
				d2h += 20;
				d2i++;
			}
			return d2s.toArray(new String[0]);
		case 3:
			int[] d3t = d3getType(type);
			if (d3t != null) {
				String[] d3a = new String[d3t[2]];
				for (int d3i = 0; d3i < d3t[2]; d3i++) {
					int nameOfst = KSFLUtilities.getInt(arr, d3t[3]+24*d3i+8, le);
					int nameLen = (nameOfst>0)?((arr[nameOfst]&0xFF)+1):0;
					String n = (nameOfst>0)?(ns(arr,nameOfst+1,nameLen-1)):"";
					d3a[d3i] = n;
				}
				return d3a;
			} else {
				return new String[0];
			}
		}
		return null;
	}
	
	@Override
	public synchronized int getNextAvailableID(long type, int start) {
		switch (version) {
		case 1:
			List<Integer> d1s = new ArrayList<Integer>();
			int d1i = 4;
			while (d1i+16 <= arr.length) {
				if (KSFLUtilities.getLong(arr, d1i, le) == type) {
					int id = KSFLUtilities.getShort(arr, d1i+8, le);
					if (!d1s.contains(id)) d1s.add(id);
				}
				d1i += 16+KSFLUtilities.getInt(arr, d1i+12, le);
			}
			int d1n = start;
			while (d1s.contains(d1n)) d1n++;
			return d1n;
		case 2:
			List<Integer> d2s = new ArrayList<Integer>();
			int d2i = 0;
			int d2h = d2headerSpace;
			while (d2i < d2objectCount && d2h < d2nameSpace) {
				if (KSFLUtilities.getLong(arr, d2h, le) == type) {
					int id = (KSFLUtilities.getShort(arr, d2h+8, le) & 0xFFFF) | ((KSFLUtilities.getShort(arr, d2h+14, le) << 16) & 0xFFFF0000);
					if (!d2s.contains(id)) d2s.add(id);
				}
				d2h += 20;
				d2i++;
			}
			int d2n = start;
			while (d2s.contains(d2n)) d2n++;
			return d2n;
		case 3:
			List<Integer> d3s = new ArrayList<Integer>();
			int[] d3t = d3getType(type);
			if (d3t != null) {
				for (int d3i = 0; d3i < d3t[2]; d3i++) {
					d3s.add(KSFLUtilities.getInt(arr, d3t[3]+24*d3i, le));
				}
			}
			int d3n = start;
			while (d3s.contains(d3n)) d3n++;
			return d3n;
		}
		return start;
	}
	
	@Override
	public synchronized String getNameFromID(long type, int id) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			int[] d2i = d2locate(type,id);
			if (d2i != null) {
				return ns(arr, d2i[1]+1, d2i[4]-1);
			}
			break;
		case 3:
			int[] d3i = d3locate(type,id);
			if (d3i != null) {
				return (d3i[7] > 0)?(ns(arr, d3i[7]+1, d3i[8]-1)):"";
			}
			break;
		}
		return "";
	}
	
	@Override
	public synchronized int getIDFromName(long type, String name) {
		switch (version) {
		case 1:
			throw new UnsupportedOperationException();
		case 2:
			int[] d2i = d2locate(type,name);
			if (d2i != null) {
				int i = (KSFLUtilities.getShort(arr, d2i[0]+8, le) & 0xFFFF) | ((KSFLUtilities.getShort(arr, d2i[0]+14, le) << 16) & 0xFFFF0000);
				return i;
			}
			break;
		case 3:
			int[] d3i = d3locate(type,name);
			if (d3i != null) {
				int i = KSFLUtilities.getInt(arr, d3i[5], le);
				return i;
			}
			break;
		}
		return 0;
	}
}
