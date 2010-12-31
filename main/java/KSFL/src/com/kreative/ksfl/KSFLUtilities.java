/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.ksfl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.RandomAccessFile;
import java.io.PrintStream;

public class KSFLUtilities {
	private KSFLUtilities() {}
	
	/**
	 * The maximum value of an unsigned 48-bit integer.
	 */
	public static final long UINT48_MAX_VALUE = (1L << 48L) - 1L;
	
	/**
	 * Reverses the bytes of a 48-bit integer.
	 * @param v a 48-bit integer.
	 * @return a 48-bit integer with the bytes reversed.
	 */
	public static long reverseUInt48(long v) {
		return Long.reverseBytes(v) >>> 16L;
	}
	
	/**
	 * Reads an unsigned 48-bit integer from a data input.
	 * @param in a data input to read from.
	 * @return an unsigned 48-bit integer.
	 * @throws IOException
	 */
	public static long readUInt48(DataInput in) throws IOException {
		long l = ((long)in.readShort() & 0xFFFFL) << 32L;
		l |= ((long)in.readInt() & 0xFFFFFFFFL);
		return l;
	}
	
	/**
	 * Writes an unsigned 48-bit integer to a data output.
	 * @param out a data output to write to.
	 * @param v an unsigned 48-bit integer.
	 * @throws IOException
	 */
	public static void writeUInt48(DataOutput out, long v) throws IOException {
		out.writeShort((short)((v >>> 32L) & 0xFFFFL));
		out.writeInt((int)(v & 0xFFFFFFFFL));
	}
	
	/**
	 * Creates a one-character constant, used for magic numbers, from an array of bytes.
	 * Missing bytes are padded with spaces.
	 * If the array is longer than one byte, only the first is used.
	 * @param type An array of bytes.
	 * @return The full type as a byte.
	 */
	public static byte occ(byte[] type) {
		if (type.length < 1) return 0x20;
		else return type[0];
	}
	
	/**
	 * Creates a one-character constant, used for magic numbers, from a string.
	 * The string is converted to bytes using the ISO-Latin-1 encoding.
	 * If the string is shorter than one character, it is padded with spaces.
	 * If the string is longer than one character, only the first is used.
	 * @param type The type as a string.
	 * @return The same type as a byte.
	 */
	public static byte occ(String type) {
		if (type.length() < 1) return 0x20;
		else return (byte)type.charAt(0);
	}
	
	/**
	 * Creates a one-character constant, used for magic numbers, from an array of characters.
	 * The characters are converted to bytes using the ISO-Latin-1 encoding.
	 * Missing characters are padded with spaces.
	 * If the array is longer than one character, only the first is used.
	 * @param type The type as an array of characters.
	 * @return The same type as a byte.
	 */
	public static byte occ(char[] type) {
		if (type.length < 1) return 0x20;
		else return (byte)type[0];
	}
	
	/**
	 * Turns a resource type back into its string representation.
	 * The bytes are converted to a string using the ISO-Latin-1 encoding.
	 * @param type The type as a byte.
	 * @return The same type as a string.
	 */
	public static String occs(byte type) {
		return Character.toString((char)(type & 0xFF));
	}
	
	/**
	 * Creates a two-character constant, used for magic numbers, from an array of bytes.
	 * Missing bytes are padded with spaces.
	 * If the array is longer than two bytes, only the first two are used.
	 * @param type An array of bytes.
	 * @return The full type as a short.
	 */
	public static short tcc(byte[] type) {
		switch (type.length) {
		case 0:
			return 0x2020;
		case 1:
			return (short)(((type[0] & 0xFF) << 8) | 0x20);
		default:
			return (short)(((type[0] & 0xFF) << 8) | (type[1] & 0xFF));
		}
	}
	
	/**
	 * Creates a two-character constant, used for magic numbers, from a string.
	 * The string is converted to bytes using the ISO-Latin-1 encoding.
	 * If the string is shorter than two characters, it is padded with spaces.
	 * If the string is longer than two characters, only the first two are used.
	 * @param type The type as a string.
	 * @return The same type as a short.
	 */
	public static int tcc(String type) {
		try {
			return tcc(type.getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException uee) {
			return tcc(type.getBytes());
		}
	}
	
	/**
	 * Creates a two-character constant, used for magic numbers, from an array of characters.
	 * The characters are converted to bytes using the ISO-Latin-1 encoding.
	 * Missing characters are padded with spaces.
	 * If the array is longer than two characters, only the first two are used.
	 * @param type The type as an array of characters.
	 * @return The same type as a short.
	 */
	public static int tcc(char[] type) {
		return tcc(new String(type));
	}
	
	/**
	 * Turns a resource type back into its string representation.
	 * The bytes are converted to a string using the ISO-Latin-1 encoding.
	 * @param type The type as a short.
	 * @return The same type as a string.
	 */
	public static String tccs(short type) {
		byte[] a = new byte[2];
		a[0] = (byte)((type >>> 8) & 0xFF);
		a[1] = (byte)((type >>> 0) & 0xFF);
		try {
			return new String(a, "ISO-8859-1");
		} catch (UnsupportedEncodingException uee) {
			return new String(a);
		}
	}
	
	/**
	 * Creates a four-character constant, used for resource types, from an array of bytes.
	 * Missing bytes are padded with spaces. In other words,
	 * <code>fcc(new byte[] b = {0x73, 0x6E, 0x64})</code> returns 0x736E6420.
	 * If the array is longer than four bytes, only the first four are used.
	 * @param type An array of bytes.
	 * @return The full type as an integer.
	 */
	public static int fcc(byte[] type) {
		switch (type.length) {
		case 0:
			return 0x20202020;
		case 1:
			return (((type[0] & 0xFF)<<24)|0x202020);
		case 2:
			return (((((type[0] & 0xFF)<<8)|(type[1] & 0xFF))<<16)|0x2020);
		case 3:
			return (((((((type[0] & 0xFF)<<8)|(type[1] & 0xFF))<<8)|(type[2] & 0xFF))<<8)|0x20);
		default:
			return (((((((type[0] & 0xFF)<<8)|(type[1] & 0xFF))<<8)|(type[2] & 0xFF))<<8)|(type[3] & 0xFF));
		}
	}
	
	/**
	 * Creates a four-character constant, used for resource types, from a string.
	 * The string is converted to bytes using the MacRoman encoding.
	 * If MacRoman is not supported, the default encoding is used instead.
	 * If the string is shorter than four characters, it is padded with spaces.
	 * In other words, <code>"STR"</code> is the same as <code>"STR "</code>.
	 * If the string is longer than four characters, only the first four are used.
	 * @param type The type as a string.
	 * @return The same type as an integer.
	 */
	public static int fcc(String type) {
		try {
			return fcc(type.getBytes("MACROMAN"));
		} catch (UnsupportedEncodingException e) {
			return fcc(type.getBytes());
		}
	}

	/**
	 * Creates a four-character constant, used for resource types, from an array of characters.
	 * The characters are converted to bytes using the MacRoman encoding.
	 * If MacRoman is not supported, the default encoding is used instead.
	 * Missing characters are padded with spaces. In other words,
	 * <code>fcc(new char[] c = {'s', 'n', 'd'})</code> returns 0x736E6420.
	 * If the array is longer than four characters, only the first four are used.
	 * @param type The type as an array of characters.
	 * @return The same type as an integer.
	 */
	public static int fcc(char[] type) {
		return fcc(new String(type));
	}
	
	/**
	 * Turns a resource type back into its string representation.
	 * The bytes are converted to a string using the MacRoman encoding.
	 * If MacRoman is not supported, the default encoding is used instead.
	 * @param type The type as an integer.
	 * @return The same type as a string.
	 */
	public static String fccs(int type) {
		byte[] a = new byte[4];
		a[0] = (byte)((type>>24) & 0xFF);
		a[1] = (byte)((type>>16) & 0xFF);
		a[2] = (byte)((type>> 8) & 0xFF);
		a[3] = (byte)((type>> 0) & 0xFF);
		try {
			return new String(a,"MACROMAN");
		} catch (UnsupportedEncodingException e) {
			return new String(a);
		}
	}
	
	/**
	 * Creates a eight-character constant, used for DFF types, from an array of bytes.
	 * Missing bytes are padded with spaces. In other words,
	 * <code>ecc(new byte[] b = {0x73, 0x6E, 0x64})</code> returns 0x736E642020202020.
	 * If the array is longer than eight bytes, only the first eight are used.
	 * @param type An array of bytes.
	 * @return The full type as a long.
	 */
	public static long ecc(byte[] type) {
		long l = 0l;
		int i = 0;
		while (i < type.length && i < 8) {
			l <<= 8;
			l |= (type[i] & 0xFFl);
			i++;
		}
		while (i < 8) {
			l <<= 8;
			l |= 0x20l;
			i++;
		}
		return l;
	}
	
	/**
	 * Creates a eight-character constant, used for DFF types, from a string.
	 * If the string is four or less characters in length, UTF-16 encoding is used.
	 * Otherwise, ISO Latin 1 encoding is used.
	 * Missing characters are padded with spaces. In other words,
	 * "Img PNG" is encoded the same as "Img PNG ".
	 * If the string is longer than eight characters, only the first eight are used.
	 * @param type The type as a string.
	 * @return The same type as a long.
	 */
	public static long ecc(String type) {
		try {
			if (type.length() > 4) return ecc(type.getBytes("ISO-8859-1"));
			else return ecc(type.getBytes("UTF-16BE"));
		} catch (UnsupportedEncodingException e) {
			return ecc(type.getBytes());
		}
	}

	/**
	 * Creates a eight-character constant, used for DFF types, from an array of characters.
	 * If the array is four or less characters in length, UTF-16 encoding is used.
	 * Otherwise, ISO Latin 1 encoding is used.
	 * Missing characters are padded with spaces. In other words,
	 * <code>ecc(new char[] c = {'I', 'm', 'g', ' ', 'B', 'M', 'P'})</code> returns 0x496D6720424D5020.
	 * If the array is longer than eight characters, only the first eight are used.
	 * @param type The type as an array of characters.
	 * @return The same type as a long.
	 */
	public static long ecc(char[] type) {
		return ecc(new String(type));
	}
	
	/**
	 * Turns a Mac OS resource type into its DFF type representation.
	 * @param type the Mac OS resource type.
	 * @return a corresponding DFF type.
	 */
	public static long eccAdaptMacResType(int type) {
		return 0x4D61632000000000l | (type & 0xFFFFFFFFl);
	}
	
	/**
	 * Turns a Mac OS resource type into its DFF type representation.
	 * @param type the Mac OS resource type.
	 * @return a corresponding DFF type.
	 */
	public static long eccAdaptMacResType(String type) {
		return ecc("Mac "+type);
	}
	
	/**
	 * Turns a Palm OS resource type into its DFF type representation.
	 * @param type the Palm OS resource type.
	 * @return a corresponding DFF type.
	 */
	public static long eccAdaptPalmResType(int type) {
		return 0x50616C6D00000000l | (type & 0xFFFFFFFFl);
	}
	
	/**
	 * Turns a Palm OS resource type into its DFF type representation.
	 * @param type the Palm OS resource type.
	 * @return a corresponding DFF type.
	 */
	public static long eccAdaptPalmResType(String type) {
		return ecc("Palm"+type);
	}
	
	/**
	 * Turns a ProDOS file type and auxiliary type into its DFF type representation.
	 * @param ftype the ProDOS file type.
	 * @param atype the ProDOS auxiliary type.
	 * @return a corresponding DFF type.
	 */
	public static long eccAdaptProDOSFileType(int ftype, int atype) {
		byte[] a = new byte[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		return ecc(new byte[]{'P','D',a[(ftype>>4)&0xF],a[ftype&0xF],a[(atype>>12)&0xF],a[(atype>>8)&0xF],a[(atype>>4)&0xF],a[atype&0xF]});
	}
	
	/**
	 * Turns a file extension into its DFF type representation.
	 * @param ext the file extension, with or without a preceding dot.
	 * @return a corresponding DFF type.
	 */
	public static long eccAdaptFileExtension(String ext) {
		if (ext.contains(".")) {
			String[] ss = ext.split("\\.");
			ext = ss[ss.length-1];
		}
		switch (ext.length()) {
		case 0:
			return ecc("Miscella");
		case 1:
			return ecc("Miscell"+ext.toUpperCase());
		case 2: case 3:
			return ecc("Misc "+ext.toUpperCase());
		case 4:
			return ecc("Misc"+ext.toUpperCase());
		case 5:
			return ecc("Msc"+ext.toUpperCase());
		case 6:
			return ecc("Ms"+ext.toUpperCase());
		default:
			return ecc(ext.toUpperCase());
		}
	}
	
	/**
	 * Turns a DFF type back into its string representation.
	 * Text encoding is attempted to be determined by byte values.
	 * If any bytes are in the ranges 0x00-0x1F or 0x7F-0x9F,
	 * UTF-16 is assumed. Otherwise ISO Latin 1 is assumed.
	 * @param type The type as an integer.
	 * @return The same type as a string.
	 */
	public static String eccs(long type) {
		byte[] a = new byte[8];
		a[0] = (byte)((type>>56) & 0xFF);
		a[1] = (byte)((type>>48) & 0xFF);
		a[2] = (byte)((type>>40) & 0xFF);
		a[3] = (byte)((type>>32) & 0xFF);
		a[4] = (byte)((type>>24) & 0xFF);
		a[5] = (byte)((type>>16) & 0xFF);
		a[6] = (byte)((type>> 8) & 0xFF);
		a[7] = (byte)((type>> 0) & 0xFF);
		boolean utf16 = (
				((a[0] >= (byte)0x00 && a[0] < (byte)0x20) || (a[0] == (byte)0x7F) || (a[0] >= (byte)0x80 && a[0] < (byte)0xA0)) ||
				((a[1] >= (byte)0x00 && a[1] < (byte)0x20) || (a[1] == (byte)0x7F) || (a[1] >= (byte)0x80 && a[1] < (byte)0xA0)) ||
				((a[2] >= (byte)0x00 && a[2] < (byte)0x20) || (a[2] == (byte)0x7F) || (a[2] >= (byte)0x80 && a[2] < (byte)0xA0)) ||
				((a[3] >= (byte)0x00 && a[3] < (byte)0x20) || (a[3] == (byte)0x7F) || (a[3] >= (byte)0x80 && a[3] < (byte)0xA0)) ||
				((a[4] >= (byte)0x00 && a[4] < (byte)0x20) || (a[4] == (byte)0x7F) || (a[4] >= (byte)0x80 && a[4] < (byte)0xA0)) ||
				((a[5] >= (byte)0x00 && a[5] < (byte)0x20) || (a[5] == (byte)0x7F) || (a[5] >= (byte)0x80 && a[5] < (byte)0xA0)) ||
				((a[6] >= (byte)0x00 && a[6] < (byte)0x20) || (a[6] == (byte)0x7F) || (a[6] >= (byte)0x80 && a[6] < (byte)0xA0)) ||
				((a[7] >= (byte)0x00 && a[7] < (byte)0x20) || (a[7] == (byte)0x7F) || (a[7] >= (byte)0x80 && a[7] < (byte)0xA0))
			);
		try {
			return new String(a,utf16?"UTF-16BE":"ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			return new String(a);
		}
	}
	
	/**
	 * Gets an 8-bit big-endian integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @return The 8-bit big-endian integer at index <code>i</code>.
	 */
	public static byte getByte(byte[] data, int i) {
		return data[i+0];
	}
	
	/**
	 * Gets a 16-bit big-endian integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @return The 16-bit big-endian integer at index <code>i</code>.
	 */
	public static short getShort(byte[] data, int i) {
		return (short)(
				((data[i+0] & 0xFF) << 8) |
				((data[i+1] & 0xFF) << 0)
		);
	}
	
	/**
	 * Gets a 32-bit big-endian integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @return The 32-bit big-endian integer at index <code>i</code>.
	 */
	public static int getInt(byte[] data, int i) {
		return (int)(
				((data[i+0] & 0xFF) << 24) |
				((data[i+1] & 0xFF) << 16) |
				((data[i+2] & 0xFF) <<  8) |
				((data[i+3] & 0xFF) <<  0)
		);
	}
	
	/**
	 * Gets a 48-bit big-endian integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @return The 48-bit big-endian integer at index <code>i</code>.
	 */
	public static long getUInt48(byte[] data, int i) {
		return (long)(
				(((long)data[i+0] & 0xFFl) << 40l) |
				(((long)data[i+1] & 0xFFl) << 32l) |
				(((long)data[i+2] & 0xFFl) << 24l) |
				(((long)data[i+3] & 0xFFl) << 16l) |
				(((long)data[i+4] & 0xFFl) <<  8l) |
				(((long)data[i+5] & 0xFFl) <<  0l)
		);
	}
	
	/**
	 * Gets a 64-bit big-endian integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @return The 64-bit big-endian integer at index <code>i</code>.
	 */
	public static long getLong(byte[] data, int i) {
		return (long)(
				(((long)data[i+0] & 0xFFl) << 56l) |
				(((long)data[i+1] & 0xFFl) << 48l) |
				(((long)data[i+2] & 0xFFl) << 40l) |
				(((long)data[i+3] & 0xFFl) << 32l) |
				(((long)data[i+4] & 0xFFl) << 24l) |
				(((long)data[i+5] & 0xFFl) << 16l) |
				(((long)data[i+6] & 0xFFl) <<  8l) |
				(((long)data[i+7] & 0xFFl) <<  0l)
		);
	}
	
	/**
	 * Gets a 32-bit big-endian fixed-point number out of an array of bytes.
	 * The radix point is fixed at the center, right between the middle two bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the fixed-point number.
	 * @return The fixed-point number at index <code>i</code>.
	 */
	public static double getFixed(byte[] data, int i) {
		return (double)getInt(data,i) / 65536.0;
	}
	
	/**
	 * Gets an IEEE single-precision floating point number out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the float.
	 * @return The float at index <code>i</code>.
	 */
	public static float getFloat(byte[] data, int i) {
		return Float.intBitsToFloat(getInt(data, i));
	}
	
	/**
	 * Gets an IEEE double-precision floating point number out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the double.
	 * @return The double at index <code>i</code>.
	 */
	public static double getDouble(byte[] data, int i) {
		return Double.longBitsToDouble(getLong(data, i));
	}
	
	/**
	 * Gets a point out of an array of bytes.
	 * A point is defined by two 16-bit big-endian integers,
	 * the first representing the Y coordinate and the second
	 * representing the X coordinate.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the point.
	 * @return The point at index <code>i</code>.
	 */
	public static java.awt.Point getPoint(byte[] data, int i) {
		return new java.awt.Point(
				getShort(data, i+2),
				getShort(data, i+0)
		);
	}
	
	/**
	 * Gets a rectangle out of an array of bytes.
	 * A rectangle is defined by four 16-bit big-endian integers,
	 * representing the top, left, bottom, and right.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the rectangle.
	 * @return The rectangle at index <code>i</code>.
	 */
	public static java.awt.Rectangle getRect(byte[] data, int i) {
		return new java.awt.Rectangle(
				getShort(data, i+2),
				getShort(data, i+0),
				getShort(data, i+6)-getShort(data, i+2),
				getShort(data, i+4)-getShort(data, i+0)
		);
	}
	
	/**
	 * Gets a 24-bit color out of an array of bytes.
	 * The red byte comes first, then green, then blue.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the color.
	 * @return The 24-bit color at index <code>i</code>.
	 */
	public static java.awt.Color getColor24(byte[] data, int i) {
		return new java.awt.Color(
				getByte(data, i+0) & 0xFF,
				getByte(data, i+1) & 0xFF,
				getByte(data, i+2) & 0xFF
		);
	}
	
	/**
	 * Gets a 32-bit color out of an array of bytes.
	 * The alpha byte comes first, then red, then green, then blue.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the color.
	 * @return The 32-bit color at index <code>i</code>.
	 */
	public static java.awt.Color getColor32(byte[] data, int i) {
		return new java.awt.Color(
				getByte(data, i+1) & 0xFF,
				getByte(data, i+2) & 0xFF,
				getByte(data, i+3) & 0xFF,
				getByte(data, i+0) & 0xFF
		);
	}
	
	/**
	 * Gets a 48-bit color out of an array of bytes.
	 * The red short comes first, then green, then blue.
	 * All shorts are big-endian.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the color.
	 * @return The 48-bit color at index <code>i</code>.
	 */
	public static java.awt.Color getColor48(byte[] data, int i) {
		return new java.awt.Color(
				(getShort(data, i+0) & 0xFFFF) / 65535.0f,
				(getShort(data, i+2) & 0xFFFF) / 65535.0f,
				(getShort(data, i+4) & 0xFFFF) / 65535.0f
		);
	}
	
	/**
	 * Gets a 64-bit color out of an array of bytes.
	 * The alpha short comes first, then red, then green, then blue.
	 * All shorts are big-endian.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the color.
	 * @return The 64-bit color at index <code>i</code>.
	 */
	public static java.awt.Color getColor64(byte[] data, int i) {
		return new java.awt.Color(
				(getShort(data, i+2) & 0xFFFF) / 65535.0f,
				(getShort(data, i+4) & 0xFFFF) / 65535.0f,
				(getShort(data, i+6) & 0xFFFF) / 65535.0f,
				(getShort(data, i+0) & 0xFFFF) / 65535.0f
		);
	}
	
	/**
	 * Gets an 8-bit little-endian integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @return The 8-bit little-endian integer at index <code>i</code>.
	 */
	public static byte getByteLE(byte[] data, int i) {
		return data[i+0];
	}
	
	/**
	 * Gets a 16-bit little-endian integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @return The 16-bit little-endian integer at index <code>i</code>.
	 */
	public static short getShortLE(byte[] data, int i) {
		return (short)(
				((data[i+0] & 0xFF) << 0) |
				((data[i+1] & 0xFF) << 8)
		);
	}
	
	/**
	 * Gets a 32-bit little-endian integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @return The 32-bit little-endian integer at index <code>i</code>.
	 */
	public static int getIntLE(byte[] data, int i) {
		return (int)(
				((data[i+0] & 0xFF) <<  0) |
				((data[i+1] & 0xFF) <<  8) |
				((data[i+2] & 0xFF) << 16) |
				((data[i+3] & 0xFF) << 24)
		);
	}
	
	/**
	 * Gets a 48-bit little-endian integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @return The 48-bit little-endian integer at index <code>i</code>.
	 */
	public static long getUInt48LE(byte[] data, int i) {
		return (long)(
				(((long)data[i+0] & 0xFFl) <<  0l) |
				(((long)data[i+1] & 0xFFl) <<  8l) |
				(((long)data[i+2] & 0xFFl) << 16l) |
				(((long)data[i+3] & 0xFFl) << 24l) |
				(((long)data[i+4] & 0xFFl) << 32l) |
				(((long)data[i+5] & 0xFFl) << 40l)
		);
	}
	
	/**
	 * Gets a 64-bit little-endian integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @return The 64-bit little-endian integer at index <code>i</code>.
	 */
	public static long getLongLE(byte[] data, int i) {
		return (long)(
				(((long)data[i+0] & 0xFFl) <<  0l) |
				(((long)data[i+1] & 0xFFl) <<  8l) |
				(((long)data[i+2] & 0xFFl) << 16l) |
				(((long)data[i+3] & 0xFFl) << 24l) |
				(((long)data[i+4] & 0xFFl) << 32l) |
				(((long)data[i+5] & 0xFFl) << 40l) |
				(((long)data[i+6] & 0xFFl) << 48l) |
				(((long)data[i+7] & 0xFFl) << 56l)
		);
	}
	
	/**
	 * Gets a 32-bit little-endian fixed-point number out of an array of bytes.
	 * The radix point is fixed at the center, right between the middle two bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the fixed-point number.
	 * @return The fixed-point number at index <code>i</code>.
	 */
	public static double getFixedLE(byte[] data, int i) {
		return (double)getIntLE(data,i) / 65536.0;
	}
	
	/**
	 * Gets an IEEE single-precision floating point number out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the float.
	 * @return The float at index <code>i</code>.
	 */
	public static float getFloatLE(byte[] data, int i) {
		return Float.intBitsToFloat(getIntLE(data, i));
	}
	
	/**
	 * Gets an IEEE double-precision floating point number out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the double.
	 * @return The double at index <code>i</code>.
	 */
	public static double getDoubleLE(byte[] data, int i) {
		return Double.longBitsToDouble(getLongLE(data, i));
	}
	
	/**
	 * Gets a point out of an array of bytes.
	 * A point is defined by two 16-bit little-endian integers,
	 * the first representing the Y coordinate and the second
	 * representing the X coordinate.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the point.
	 * @return The point at index <code>i</code>.
	 */
	public static java.awt.Point getPointLE(byte[] data, int i) {
		return new java.awt.Point(
				getShortLE(data, i+2),
				getShortLE(data, i+0)
		);
	}
	
	/**
	 * Gets a rectangle out of an array of bytes.
	 * A rectangle is defined by four 16-bit little-endian integers,
	 * representing the top, left, bottom, and right.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the rectangle.
	 * @return The rectangle at index <code>i</code>.
	 */
	public static java.awt.Rectangle getRectLE(byte[] data, int i) {
		return new java.awt.Rectangle(
				getShortLE(data, i+2),
				getShortLE(data, i+0),
				getShortLE(data, i+6)-getShortLE(data, i+2),
				getShortLE(data, i+4)-getShortLE(data, i+0)
		);
	}
	
	/**
	 * Gets a 24-bit color out of an array of bytes.
	 * The red byte comes first, then green, then blue.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the color.
	 * @return The 24-bit color at index <code>i</code>.
	 */
	public static java.awt.Color getColor24LE(byte[] data, int i) {
		return new java.awt.Color(
				getByteLE(data, i+0) & 0xFF,
				getByteLE(data, i+1) & 0xFF,
				getByteLE(data, i+2) & 0xFF
		);
	}
	
	/**
	 * Gets a 32-bit color out of an array of bytes.
	 * The alpha byte comes first, then red, then green, then blue.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the color.
	 * @return The 32-bit color at index <code>i</code>.
	 */
	public static java.awt.Color getColor32LE(byte[] data, int i) {
		return new java.awt.Color(
				getByteLE(data, i+1) & 0xFF,
				getByteLE(data, i+2) & 0xFF,
				getByteLE(data, i+3) & 0xFF,
				getByteLE(data, i+0) & 0xFF
		);
	}
	
	/**
	 * Gets a 48-bit color out of an array of bytes.
	 * The red short comes first, then green, then blue.
	 * All shorts are little-endian.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the color.
	 * @return The 48-bit color at index <code>i</code>.
	 */
	public static java.awt.Color getColor48LE(byte[] data, int i) {
		return new java.awt.Color(
				(getShortLE(data, i+0) & 0xFFFF) / 65535.0f,
				(getShortLE(data, i+2) & 0xFFFF) / 65535.0f,
				(getShortLE(data, i+4) & 0xFFFF) / 65535.0f
		);
	}
	
	/**
	 * Gets a 64-bit color out of an array of bytes.
	 * The alpha short comes first, then red, then green, then blue.
	 * All shorts are little-endian.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the color.
	 * @return The 64-bit color at index <code>i</code>.
	 */
	public static java.awt.Color getColor64LE(byte[] data, int i) {
		return new java.awt.Color(
				(getShortLE(data, i+2) & 0xFFFF) / 65535.0f,
				(getShortLE(data, i+4) & 0xFFFF) / 65535.0f,
				(getShortLE(data, i+6) & 0xFFFF) / 65535.0f,
				(getShortLE(data, i+0) & 0xFFFF) / 65535.0f
		);
	}

	/**
	 * Gets an 8-bit integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The 8-bit integer at index <code>i</code>.
	 */
	public static byte getByte(byte[] data, int i, boolean le) {
		return le ? getByteLE(data, i) : getByte(data, i);
	}
	
	/**
	 * Gets a 16-bit integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The 16-bit integer at index <code>i</code>.
	 */
	public static short getShort(byte[] data, int i, boolean le) {
		return le ? getShortLE(data, i) : getShort(data, i);
	}
	
	/**
	 * Gets a 32-bit integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The 32-bit integer at index <code>i</code>.
	 */
	public static int getInt(byte[] data, int i, boolean le) {
		return le ? getIntLE(data, i) : getInt(data, i);
	}
	
	/**
	 * Gets a 48-bit integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The 48-bit integer at index <code>i</code>.
	 */
	public static long getUInt48(byte[] data, int i, boolean le) {
		return le ? getUInt48LE(data, i) : getUInt48(data, i);
	}
	
	/**
	 * Gets a 64-bit integer out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the integer.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The 64-bit integer at index <code>i</code>.
	 */
	public static long getLong(byte[] data, int i, boolean le) {
		return le ? getLongLE(data, i) : getLong(data, i);
	}
	
	/**
	 * Gets a 32-bit fixed-point number out of an array of bytes.
	 * The radix point is fixed at the center, right between the middle two bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the fixed-point number.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The fixed-point number at index <code>i</code>.
	 */
	public static double getFixed(byte[] data, int i, boolean le) {
		return le ? getFixedLE(data, i) : getFixed(data, i);
	}
	
	/**
	 * Gets an IEEE single-precision floating point number out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the float.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The float at index <code>i</code>.
	 */
	public static float getFloat(byte[] data, int i, boolean le) {
		return le ? getFloatLE(data, i) : getFloat(data, i);
	}
	
	/**
	 * Gets an IEEE double-precision floating point number out of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the double.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The double at index <code>i</code>.
	 */
	public static double getDouble(byte[] data, int i, boolean le) {
		return le ? getDoubleLE(data, i) : getDouble(data, i);
	}
	
	/**
	 * Gets a point out of an array of bytes.
	 * A point is defined by two 16-bit integers,
	 * the first representing the Y coordinate and the second
	 * representing the X coordinate.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the point.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The point at index <code>i</code>.
	 */
	public static java.awt.Point getPoint(byte[] data, int i, boolean le) {
		return le ? getPointLE(data, i) : getPoint(data, i);
	}
	
	/**
	 * Gets a rectangle out of an array of bytes.
	 * A rectangle is defined by four 16-bit integers,
	 * representing the top, left, bottom, and right.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the rectangle.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The rectangle at index <code>i</code>.
	 */
	public static java.awt.Rectangle getRect(byte[] data, int i, boolean le) {
		return le ? getRectLE(data, i) : getRect(data, i);
	}
	
	/**
	 * Gets a 24-bit color out of an array of bytes.
	 * The red byte comes first, then green, then blue.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the color.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The 24-bit color at index <code>i</code>.
	 */
	public static java.awt.Color getColor24(byte[] data, int i, boolean le) {
		return le ? getColor24LE(data, i) : getColor24(data, i);
	}
	
	/**
	 * Gets a 32-bit color out of an array of bytes.
	 * The alpha byte comes first, then red, then green, then blue.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the color.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The 32-bit color at index <code>i</code>.
	 */
	public static java.awt.Color getColor32(byte[] data, int i, boolean le) {
		return le ? getColor32LE(data, i) : getColor32(data, i);
	}
	
	/**
	 * Gets a 48-bit color out of an array of bytes.
	 * The red short comes first, then green, then blue.
	 * All shorts are big-endian.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the color.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The 48-bit color at index <code>i</code>.
	 */
	public static java.awt.Color getColor48(byte[] data, int i, boolean le) {
		return le ? getColor48LE(data, i) : getColor48(data, i);
	}
	
	/**
	 * Gets a 64-bit color out of an array of bytes.
	 * The alpha short comes first, then red, then green, then blue.
	 * All shorts are big-endian.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the color.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @return The 64-bit color at index <code>i</code>.
	 */
	public static java.awt.Color getColor64(byte[] data, int i, boolean le) {
		return le ? getColor64LE(data, i) : getColor64(data, i);
	}
	
	/**
	 * Gets a Pascal string out of an array of bytes using the default text encoding.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the Pascal string, the length byte.
	 * @return The Pascal string at index <code>i</code>.
	 */
	public static String getPString(byte[] data, int i) {
		return new String(data, i+1, data[i] & 0xFF);
	}
	
	/**
	 * Gets a Pascal string out of an array of bytes using the specified text encoding.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the Pascal string, the length byte.
	 * @param encoding The name of the text encoding to use.
	 * @return The Pascal string at index <code>i</code>.
	 * @throws UnsupportedEncodingException
	 */
	public static String getPString(byte[] data, int i, String encoding) throws UnsupportedEncodingException {
		return new String(data, i+1, data[i] & 0xFF, encoding);
	}
	
	/**
	 * Gets a C string out of an array of bytes using the default text encoding.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the C string.
	 * @return The C string at index <code>i</code>.
	 */
	public static String getCString(byte[] data, int i) {
		int j = i;
		while (j < data.length && data[j] != 0) j++;
		return new String(data, i, j-i);
	}
	
	/**
	 * Gets a C string out of an array of bytes using the specified text encoding.
	 * @param data The array of bytes to copy from.
	 * @param i The index of the first byte of the C string.
	 * @param encoding The name of the text encoding to use.
	 * @return The C string at index <code>i</code>.
	 * @throws UnsupportedEncodingException
	 */
	public static String getCString(byte[] data, int i, String encoding) throws UnsupportedEncodingException {
		int j = i;
		while (j < data.length && data[j] != 0) j++;
		return new String(data, i, j-i, encoding);
	}
	
	/**
	 * Puts an 8-bit big-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param v The 8-bit big-endian integer at index <code>i</code>.
	 */
	public static void putByte(byte[] data, int i, byte v) {
		data[i+0] = v;
	}
	
	/**
	 * Puts a 16-bit big-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param v The 16-bit big-endian integer at index <code>i</code>.
	 */
	public static void putShort(byte[] data, int i, short v) {
		data[i+0] = (byte)((v >>> 8) & 0xFF);
		data[i+1] = (byte)((v >>> 0) & 0xFF);
	}
	
	/**
	 * Puts a 32-bit big-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param v The 32-bit big-endian integer at index <code>i</code>.
	 */
	public static void putInt(byte[] data, int i, int v) {
		data[i+0] = (byte)((v >>> 24) & 0xFF);
		data[i+1] = (byte)((v >>> 16) & 0xFF);
		data[i+2] = (byte)((v >>>  8) & 0xFF);
		data[i+3] = (byte)((v >>>  0) & 0xFF);
	}
	
	/**
	 * Puts a 48-bit big-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param v The 48-bit big-endian integer at index <code>i</code>.
	 */
	public static void putUInt48(byte[] data, int i, long v) {
		data[i+0] = (byte)((v >>> 40l) & 0xFFl);
		data[i+1] = (byte)((v >>> 32l) & 0xFFl);
		data[i+2] = (byte)((v >>> 24l) & 0xFFl);
		data[i+3] = (byte)((v >>> 16l) & 0xFFl);
		data[i+4] = (byte)((v >>>  8l) & 0xFFl);
		data[i+5] = (byte)((v >>>  0l) & 0xFFl);
	}
	
	/**
	 * Puts a 64-bit big-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param v The 64-bit big-endian integer at index <code>i</code>.
	 */
	public static void putLong(byte[] data, int i, long v) {
		data[i+0] = (byte)((v >>> 56l) & 0xFFl);
		data[i+1] = (byte)((v >>> 48l) & 0xFFl);
		data[i+2] = (byte)((v >>> 40l) & 0xFFl);
		data[i+3] = (byte)((v >>> 32l) & 0xFFl);
		data[i+4] = (byte)((v >>> 24l) & 0xFFl);
		data[i+5] = (byte)((v >>> 16l) & 0xFFl);
		data[i+6] = (byte)((v >>>  8l) & 0xFFl);
		data[i+7] = (byte)((v >>>  0l) & 0xFFl);
	}
	
	/**
	 * Puts a 32-bit big-endian fixed-point number into an array of bytes.
	 * The radix point is fixed at the center, right between the middle two bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the fixed-point number.
	 * @param v The fixed-point number at index <code>i</code>.
	 */
	public static void putFixed(byte[] data, int i, double v) {
		putInt(data, i, (int)(v * 65536.0));
	}
	
	/**
	 * Puts an IEEE single-precision floating point number into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the float.
	 * @param v The float at index <code>i</code>.
	 */
	public static void putFloat(byte[] data, int i, float v) {
		putInt(data, i, Float.floatToRawIntBits(v));
	}
	
	/**
	 * Puts an IEEE double-precision floating point number into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the double.
	 * @param v The double at index <code>i</code>.
	 */
	public static void putDouble(byte[] data, int i, double v) {
		putLong(data, i, Double.doubleToRawLongBits(v));
	}
	
	/**
	 * Puts a point into an array of bytes.
	 * A point is defined by two 16-bit big-endian integers,
	 * the first representing the Y coordinate and the second
	 * representing the X coordinate.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the point.
	 * @param v The point at index <code>i</code>.
	 */
	public static void putPoint(byte[] data, int i, java.awt.Point v) {
		putShort(data, i+2, (short)v.x);
		putShort(data, i+0, (short)v.y);
	}
	
	/**
	 * Puts a rectangle into an array of bytes.
	 * A rectangle is defined by four 16-bit big-endian integers,
	 * representing the top, left, bottom, and right.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the rectangle.
	 * @param v The rectangle at index <code>i</code>.
	 */
	public static void putRect(byte[] data, int i, java.awt.Rectangle v) {
		putShort(data, i+2, (short)v.x);
		putShort(data, i+0, (short)v.y);
		putShort(data, i+6, (short)(v.x+v.width));
		putShort(data, i+4, (short)(v.y+v.height));
	}
	
	/**
	 * Puts a 24-bit color into an array of bytes.
	 * The red byte comes first, then green, then blue.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the color.
	 * @param v The 24-bit color at index <code>i</code>.
	 */
	public static void putColor24(byte[] data, int i, java.awt.Color v) {
		putByte(data, i+0, (byte)v.getRed());
		putByte(data, i+1, (byte)v.getGreen());
		putByte(data, i+2, (byte)v.getBlue());
	}
	
	/**
	 * Puts a 32-bit color into an array of bytes.
	 * The alpha byte comes first, then red, then green, then blue.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the color.
	 * @param v The 32-bit color at index <code>i</code>.
	 */
	public static void putColor32(byte[] data, int i, java.awt.Color v) {
		putByte(data, i+1, (byte)v.getRed());
		putByte(data, i+2, (byte)v.getGreen());
		putByte(data, i+3, (byte)v.getBlue());
		putByte(data, i+0, (byte)v.getAlpha());
	}
	
	/**
	 * Puts a 48-bit color into an array of bytes.
	 * The red short comes first, then green, then blue.
	 * All shorts are big-endian.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the color.
	 * @param v The 48-bit color at index <code>i</code>.
	 */
	public static void putColor48(byte[] data, int i, java.awt.Color v) {
		float[] rgb = v.getRGBComponents(null);
		putShort(data, i+0, (short)Math.round(rgb[0] * 65535.0f));
		putShort(data, i+2, (short)Math.round(rgb[1] * 65535.0f));
		putShort(data, i+4, (short)Math.round(rgb[2] * 65535.0f));
	}
	
	/**
	 * Puts a 64-bit color into an array of bytes.
	 * The alpha short comes first, then red, then green, then blue.
	 * All shorts are big-endian.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the color.
	 * @param v The 64-bit color at index <code>i</code>.
	 */
	public static void putColor64(byte[] data, int i, java.awt.Color v) {
		float[] rgb = v.getRGBComponents(null);
		putShort(data, i+2, (short)Math.round(rgb[0] * 65535.0f));
		putShort(data, i+4, (short)Math.round(rgb[1] * 65535.0f));
		putShort(data, i+6, (short)Math.round(rgb[2] * 65535.0f));
		putShort(data, i+0, (short)Math.round(rgb[3] * 65535.0f));
	}
	
	/**
	 * Puts an 8-bit little-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param v The 8-bit little-endian integer at index <code>i</code>.
	 */
	public static void putByteLE(byte[] data, int i, byte v) {
		data[i+0] = v;
	}
	
	/**
	 * Puts a 16-bit little-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param v The 16-bit little-endian integer at index <code>i</code>.
	 */
	public static void putShortLE(byte[] data, int i, short v) {
		data[i+0] = (byte)((v >>> 0) & 0xFF);
		data[i+1] = (byte)((v >>> 8) & 0xFF);
	}
	
	/**
	 * Puts a 32-bit little-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param v The 32-bit little-endian integer at index <code>i</code>.
	 */
	public static void putIntLE(byte[] data, int i, int v) {
		data[i+0] = (byte)((v >>>  0) & 0xFF);
		data[i+1] = (byte)((v >>>  8) & 0xFF);
		data[i+2] = (byte)((v >>> 16) & 0xFF);
		data[i+3] = (byte)((v >>> 24) & 0xFF);
	}
	
	/**
	 * Puts a 48-bit little-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param v The 48-bit little-endian integer at index <code>i</code>.
	 */
	public static void putUInt48LE(byte[] data, int i, long v) {
		data[i+0] = (byte)((v >>>  0l) & 0xFFl);
		data[i+1] = (byte)((v >>>  8l) & 0xFFl);
		data[i+2] = (byte)((v >>> 16l) & 0xFFl);
		data[i+3] = (byte)((v >>> 24l) & 0xFFl);
		data[i+4] = (byte)((v >>> 32l) & 0xFFl);
		data[i+5] = (byte)((v >>> 40l) & 0xFFl);
	}
	
	/**
	 * Puts a 64-bit little-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param v The 64-bit little-endian integer at index <code>i</code>.
	 */
	public static void putLongLE(byte[] data, int i, long v) {
		data[i+0] = (byte)((v >>>  0l) & 0xFFl);
		data[i+1] = (byte)((v >>>  8l) & 0xFFl);
		data[i+2] = (byte)((v >>> 16l) & 0xFFl);
		data[i+3] = (byte)((v >>> 24l) & 0xFFl);
		data[i+4] = (byte)((v >>> 32l) & 0xFFl);
		data[i+5] = (byte)((v >>> 40l) & 0xFFl);
		data[i+6] = (byte)((v >>> 48l) & 0xFFl);
		data[i+7] = (byte)((v >>> 56l) & 0xFFl);
	}
	
	/**
	 * Puts a 32-bit little-endian fixed-point number into an array of bytes.
	 * The radix point is fixed at the center, right between the middle two bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the fixed-point number.
	 * @param v The fixed-point number at index <code>i</code>.
	 */
	public static void putFixedLE(byte[] data, int i, double v) {
		putIntLE(data, i, (int)(v * 65536.0));
	}
	
	/**
	 * Puts an IEEE single-precision floating point number into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the float.
	 * @param v The float at index <code>i</code>.
	 */
	public static void putFloatLE(byte[] data, int i, float v) {
		putIntLE(data, i, Float.floatToRawIntBits(v));
	}
	
	/**
	 * Puts an IEEE double-precision floating point number into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the double.
	 * @param v The double at index <code>i</code>.
	 */
	public static void putDoubleLE(byte[] data, int i, double v) {
		putLongLE(data, i, Double.doubleToRawLongBits(v));
	}
	
	/**
	 * Puts a point into an array of bytes.
	 * A point is defined by two 16-bit little-endian integers,
	 * the first representing the Y coordinate and the second
	 * representing the X coordinate.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the point.
	 * @param v The point at index <code>i</code>.
	 */
	public static void putPointLE(byte[] data, int i, java.awt.Point v) {
		putShortLE(data, i+2, (short)v.x);
		putShortLE(data, i+0, (short)v.y);
	}
	
	/**
	 * Puts a rectangle into an array of bytes.
	 * A rectangle is defined by four 16-bit little-endian integers,
	 * representing the top, left, bottom, and right.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the rectangle.
	 * @param v The rectangle at index <code>i</code>.
	 */
	public static void putRectLE(byte[] data, int i, java.awt.Rectangle v) {
		putShortLE(data, i+2, (short)v.x);
		putShortLE(data, i+0, (short)v.y);
		putShortLE(data, i+6, (short)(v.x+v.width));
		putShortLE(data, i+4, (short)(v.y+v.height));
	}
	
	/**
	 * Puts a 24-bit color into an array of bytes.
	 * The red byte comes first, then green, then blue.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the color.
	 * @param v The 24-bit color at index <code>i</code>.
	 */
	public static void putColor24LE(byte[] data, int i, java.awt.Color v) {
		putByteLE(data, i+0, (byte)v.getRed());
		putByteLE(data, i+1, (byte)v.getGreen());
		putByteLE(data, i+2, (byte)v.getBlue());
	}
	
	/**
	 * Puts a 32-bit color into an array of bytes.
	 * The alpha byte comes first, then red, then green, then blue.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the color.
	 * @param v The 32-bit color at index <code>i</code>.
	 */
	public static void putColor32LE(byte[] data, int i, java.awt.Color v) {
		putByteLE(data, i+1, (byte)v.getRed());
		putByteLE(data, i+2, (byte)v.getGreen());
		putByteLE(data, i+3, (byte)v.getBlue());
		putByteLE(data, i+0, (byte)v.getAlpha());
	}
	
	/**
	 * Puts a 48-bit color into an array of bytes.
	 * The red short comes first, then green, then blue.
	 * All shorts are little-endian.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the color.
	 * @param v The 48-bit color at index <code>i</code>.
	 */
	public static void putColor48LE(byte[] data, int i, java.awt.Color v) {
		float[] rgb = v.getRGBComponents(null);
		putShortLE(data, i+0, (short)Math.round(rgb[0] * 65535.0f));
		putShortLE(data, i+2, (short)Math.round(rgb[1] * 65535.0f));
		putShortLE(data, i+4, (short)Math.round(rgb[2] * 65535.0f));
	}
	
	/**
	 * Puts a 64-bit color into an array of bytes.
	 * The alpha short comes first, then red, then green, then blue.
	 * All shorts are little-endian.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the color.
	 * @param v The 64-bit color at index <code>i</code>.
	 */
	public static void putColor64LE(byte[] data, int i, java.awt.Color v) {
		float[] rgb = v.getRGBComponents(null);
		putShortLE(data, i+2, (short)Math.round(rgb[0] * 65535.0f));
		putShortLE(data, i+4, (short)Math.round(rgb[1] * 65535.0f));
		putShortLE(data, i+6, (short)Math.round(rgb[2] * 65535.0f));
		putShortLE(data, i+0, (short)Math.round(rgb[3] * 65535.0f));
	}
	
	/**
	 * Puts an 8-bit big-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The 8-bit big-endian integer at index <code>i</code>.
	 */
	public static void putByte(byte[] data, int i, boolean le, byte v) {
		if (le) putByteLE(data, i, v); else putByte(data, i, v);
	}
	
	/**
	 * Puts a 16-bit big-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The 16-bit big-endian integer at index <code>i</code>.
	 */
	public static void putShort(byte[] data, int i, boolean le, short v) {
		if (le) putShortLE(data, i, v); else putShort(data, i, v);
	}
	
	/**
	 * Puts a 32-bit big-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The 32-bit big-endian integer at index <code>i</code>.
	 */
	public static void putInt(byte[] data, int i, boolean le, int v) {
		if (le) putIntLE(data, i, v); else putInt(data, i, v);
	}
	
	/**
	 * Puts a 48-bit big-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The 48-bit big-endian integer at index <code>i</code>.
	 */
	public static void putUInt48(byte[] data, int i, boolean le, long v) {
		if (le) putUInt48LE(data, i, v); else putUInt48(data, i, v);
	}
	
	/**
	 * Puts a 64-bit big-endian integer into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the integer.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The 64-bit big-endian integer at index <code>i</code>.
	 */
	public static void putLong(byte[] data, int i, boolean le, long v) {
		if (le) putLongLE(data, i, v); else putLong(data, i, v);
	}
	
	/**
	 * Puts a 32-bit big-endian fixed-point number into an array of bytes.
	 * The radix point is fixed at the center, right between the middle two bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the fixed-point number.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The fixed-point number at index <code>i</code>.
	 */
	public static void putFixed(byte[] data, int i, boolean le, double v) {
		if (le) putFixedLE(data, i, v); else putFixed(data, i, v);
	}
	
	/**
	 * Puts an IEEE single-precision floating point number into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the float.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The float at index <code>i</code>.
	 */
	public static void putFloat(byte[] data, int i, boolean le, float v) {
		if (le) putFloatLE(data, i, v); else putFloat(data, i, v);
	}
	
	/**
	 * Puts an IEEE double-precision floating point number into an array of bytes.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the double.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The double at index <code>i</code>.
	 */
	public static void putDouble(byte[] data, int i, boolean le, double v) {
		if (le) putDoubleLE(data, i, v); else putDouble(data, i, v);
	}
	
	/**
	 * Puts a point into an array of bytes.
	 * A point is defined by two 16-bit big-endian integers,
	 * the first representing the Y coordinate and the second
	 * representing the X coordinate.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the point.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The point at index <code>i</code>.
	 */
	public static void putPoint(byte[] data, int i, boolean le, java.awt.Point v) {
		if (le) putPointLE(data, i, v); else putPoint(data, i, v);
	}
	
	/**
	 * Puts a rectangle into an array of bytes.
	 * A rectangle is defined by four 16-bit big-endian integers,
	 * representing the top, left, bottom, and right.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the rectangle.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The rectangle at index <code>i</code>.
	 */
	public static void putRect(byte[] data, int i, boolean le, java.awt.Rectangle v) {
		if (le) putRectLE(data, i, v); else putRect(data, i, v);
	}
	
	/**
	 * Puts a 24-bit color into an array of bytes.
	 * The red byte comes first, then green, then blue.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the color.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The 24-bit color at index <code>i</code>.
	 */
	public static void putColor24(byte[] data, int i, boolean le, java.awt.Color v) {
		if (le) putColor24LE(data, i, v); else putColor24(data, i, v);
	}
	
	/**
	 * Puts a 32-bit color into an array of bytes.
	 * The alpha byte comes first, then red, then green, then blue.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the color.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The 32-bit color at index <code>i</code>.
	 */
	public static void putColor32(byte[] data, int i, boolean le, java.awt.Color v) {
		if (le) putColor32LE(data, i, v); else putColor32(data, i, v);
	}
	
	/**
	 * Puts a 48-bit color into an array of bytes.
	 * The red short comes first, then green, then blue.
	 * All shorts are big-endian.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the color.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The 48-bit color at index <code>i</code>.
	 */
	public static void putColor48(byte[] data, int i, boolean le, java.awt.Color v) {
		if (le) putColor48LE(data, i, v); else putColor48(data, i, v);
	}
	
	/**
	 * Puts a 64-bit color into an array of bytes.
	 * The alpha short comes first, then red, then green, then blue.
	 * All shorts are big-endian.
	 * @param data The array of bytes to insert into.
	 * @param i The index of the first byte of the color.
	 * @param le True if the value is little-endian, false if the value is big-endian.
	 * @param v The 64-bit color at index <code>i</code>.
	 */
	public static void putColor64(byte[] data, int i, boolean le, java.awt.Color v) {
		if (le) putColor64LE(data, i, v); else putColor64(data, i, v);
	}
	
	/**
	 * Creates a duplicate of an array of bytes.
	 * If <code>data</code> is <code>null</code>,
	 * this returns a new <code>byte[]</code> with
	 * no elements.
	 * @param data The array of bytes to copy.
	 * @return A copy of <code>data</code>.
	 */
	public static byte[] copy(byte[] data) {
		if (data == null) return new byte[0];
		byte[] dd = new byte[data.length];
		for (int i = 0; i < data.length; i++) dd[i] = data[i];
		return dd;
	}
	
	/**
	 * Creates a duplicate of part of an array of bytes.
	 * @param data The array of bytes to copy from.
	 * @param off The index of the first byte to copy.
	 * @param len The number of bytes to copy.
	 * @return A copy of elements <code>off</code> to <code>off+len-1</code> of <code>data</code>.
	 */
	public static byte[] copy(byte[] data, int off, int len) {
		byte[] dd = new byte[len];
		for (int si = off, di = 0; si < data.length && di < dd.length; si++, di++) dd[di] = data[si];
		return dd;
	}
	
	/**
	 * Creates a duplicate of an array of bytes with part of the array removed.
	 * @param data The array of bytes to copy from.
	 * @param off The index of the first byte to remove.
	 * @param len The number of bytes to remove.
	 * @return A copy of elements <code>0</code> to <code>off-1</code> and <code>off+len</code> to <code>data.length-1</code> of <code>data</code>.
	 */
	public static byte[] cut(byte[] data, int off, int len) {
		byte[] dd = new byte[data.length - len];
		for (int si = 0, di = 0; si < off && di < off && si < data.length && di < dd.length; si++, di++) dd[di] = data[si];
		for (int si = off+len, di = off; si < data.length && di < dd.length; si++, di++) dd[di] = data[si];
		return dd;
	}
	
	/**
	 * Creates a duplicate of an array of bytes with additional bytes inserted.
	 * @param data The array of bytes to copy from.
	 * @param off The index where additional bytes will be inserted.
	 * @param pasted The additional bytes to insert.
	 * @return A copy of elements <code>0</code> to <code>off-1</code> of <code>data</code>, elements <code>0</code> to <code>pasted.length-1</code> of <code>pasted</code>, and elements <code>off</code> to <code>data.length-1</code> of <code>data</code>.
	 */
	public static byte[] paste(byte[] data, int off, byte[] pasted) {
		byte[] dd = new byte[data.length + pasted.length];
		for (int si = 0, di = 0; si < off && di < off && si < data.length && di < dd.length; si++, di++) dd[di] = data[si];
		for (int si = 0, di = off; si < pasted.length && di < dd.length; si++, di++) dd[di] = pasted[si];
		for (int si = off, di = off+pasted.length; si < data.length && di < dd.length; si++, di++) dd[di] = data[si];
		return dd;
	}
	
	/**
	 * Creates a duplicate of an array of bytes with additional bytes inserted.
	 * @param data The array of bytes to copy from.
	 * @param off The index where additional bytes will be inserted.
	 * @param pasted The number of additional bytes to insert.
	 * @return A copy of elements <code>0</code> to <code>off-1</code> of <code>data</code> and elements <code>off</code> to <code>data.length-1</code> of <code>data</code>.
	 */
	public static byte[] paste(byte[] data, int off, int pasted) {
		byte[] dd = new byte[data.length + pasted];
		for (int si = 0, di = 0; si < off && di < off && si < data.length && di < dd.length; si++, di++) dd[di] = data[si];
		for (int si = off, di = off+pasted; si < data.length && di < dd.length; si++, di++) dd[di] = data[si];
		return dd;
	}
	
	/**
	 * Creates a duplicate of an array of bytes with a certain size.
	 * @param data The array of bytes to copy from.
	 * @param len The length of the resulting array.
	 * @return A copy of elements <code>0</code> to <code>Math.min(len, data.length)-1</code> of <code>data</code>.
	 */
	public static byte[] resize(byte[] data, int len) {
		byte[] dd = new byte[len];
		for (int si = 0, di = 0; si < data.length && di < dd.length; si++, di++) dd[di] = data[si];
		return dd;
	}
	
	/**
	 * Cuts a segment of data out of a random-access file.
	 * The two parts of the file before and after the cut
	 * are joined together. For instance, if your file is
	 * <br><br>
	 * <code>05 23 75 A8 FF 2E 09 DB 3C 01 A9 99 80</code>
	 * <br><br>
	 * then <code>fileCut(f, 4, 3)</code> will return
	 * <br><br>
	 * <code>FF 2E 09</code>
	 * <br><br>
	 * and the new contents of the file will be
	 * <br><br>
	 * <code>05 23 75 A8 DB 3C 01 A9 99 80</code>
	 * <br><br>
	 * Precondition: <code>offset</code> is between zero and the length of the file, inclusive.
	 * Other values may give unexpected results.
	 * @param f a <code>RandomAccessFile</code>.
	 * @param offset the offset of the first byte of the cut.
	 * @param bytesToCut the number of bytes to cut.
	 * @return an array containing the cut bytes.
	 * @throws IOException if an I/O error occurs during the cut process.
	 */
	public static byte[] cut(RandomAccessFile f, long offset, int bytesToCut) throws IOException {
		if (bytesToCut <= 0) return new byte[0];
		byte[] stuff = new byte[bytesToCut];
		f.seek(offset);
		f.read(stuff);
		long l = f.length();
		byte[] junk = new byte[1048576];
		for (long s = offset+bytesToCut, d = offset; s < l; d += 1048576, s += 1048576) {
			f.seek(s);
			f.read(junk);
			f.seek(d);
			f.write(junk);
		}
		f.setLength(Math.max(l-bytesToCut,offset));
		return stuff;
	}
	
	/**
	 * Cuts a segment of data out of a random-access file.
	 * The two parts of the file before and after the cut
	 * are joined together. For instance, if your file is
	 * <br><br>
	 * <code>05 23 75 A8 FF 2E 09 DB 3C 01 A9 99 80</code>
	 * <br><br>
	 * then <code>fileCut(f, 4L, 3L)</code> will return 3L
	 * and the new contents of the file will be
	 * <br><br>
	 * <code>05 23 75 A8 DB 3C 01 A9 99 80</code>
	 * <br><br>
	 * Precondition: <code>offset</code> is between zero and the length of the file, inclusive.
	 * Other values may give unexpected results.
	 * @param f a <code>RandomAccessFile</code>.
	 * @param offset the offset of the first byte of the cut.
	 * @param bytesToCut the number of bytes to cut.
	 * @return the number of bytes cut.
	 * @throws IOException if an I/O error occurs during the cut process.
	 */
	public static long cut(RandomAccessFile f, long offset, long bytesToCut) throws IOException {
		if (bytesToCut <= 0l) return 0l;
		long l = f.length();
		byte[] junk = new byte[1048576];
		for (long s = offset+bytesToCut, d = offset; s < l; d += 1048576, s += 1048576) {
			f.seek(s);
			f.read(junk);
			f.seek(d);
			f.write(junk);
		}
		f.setLength(Math.max(l-bytesToCut,offset));
		return Math.min(l-offset, bytesToCut);
	}
	
	/**
	 * Copies a segment of data from a random-access file.
	 * The file itself is not affected. For instance, if your file is
	 * <br><br>
	 * <code>05 23 75 A8 FF 2E 09 DB 3C 01 A9 99 80</code>
	 * <br><br>
	 * then <code>fileCopy(f, 4, 3)</code> will return
	 * <br><br>
	 * <code>FF 2E 09</code>
	 * <br><br>
	 * and the contents of the file will not change.
	 * <br><br>
	 * Precondition: <code>offset</code> is between zero and the length of the file, inclusive.
	 * Other values may give unexpected results.
	 * @param f a <code>RandomAccessFile</code>.
	 * @param offset the offset of the first byte to copy.
	 * @param bytesToCopy the number of bytes to copy.
	 * @return an array containing the copied bytes.
	 * @throws IOException if an I/O error occurs during the copy process.
	 */
	public static byte[] copy(RandomAccessFile f, long offset, int bytesToCopy) throws IOException {
		if (bytesToCopy <= 0) return new byte[0];
		byte[] stuff = new byte[bytesToCopy];
		f.seek(offset);
		f.read(stuff);
		return stuff;
	}
	
	/**
	 * Pastes a segment of zero bytes into a random-access file.
	 * The bytes after the specified offset will be moved to
	 * later in the file. For instance, if your file is
	 * <br><br>
	 * <code>05 23 75 A8 FF 2E 09 DB 3C 01 A9 99 80</code>
	 * <br><br>
	 * then <code>filePaste(f, 4, 3)</code> will return
	 * <br><br>
	 * <code>00 00 00</code>
	 * <br><br>
	 * and the new contents of the file will be
	 * <br><br>
	 * <code>05 23 75 A8 00 00 00 FF 2E 09 DB 3C 01 A9 99 80</code>
	 * <br><br>
	 * Precondition: <code>offset</code> is between zero and the length of the file, inclusive.
	 * Other values may give unexpected results.
	 * @param f a <code>RandomAccessFile</code>.
	 * @param offset the offset where the first byte of pasted data should be written.
	 * @param bytesToPaste the number of zero bytes to paste.
	 * @return an array containing the pasted bytes.
	 * @throws IOException if an I/O error occurs during the paste process.
	 */
	public static byte[] paste(RandomAccessFile f, long offset, int bytesToPaste) throws IOException {
		if (bytesToPaste <= 0) return new byte[0];
		byte[] stuff = new byte[bytesToPaste];
		long l = f.length();
		long btm = (l-offset-1) & (~0xFFFFFl);
		byte[] junk = new byte[1048576];
		for (long s = offset+btm, d = offset+btm+bytesToPaste; s >= offset; d -= 1048576, s -= 1048576) {
			f.seek(s);
			f.read(junk);
			f.seek(d);
			f.write(junk);
		}
		f.seek(offset);
		f.write(stuff);
		f.setLength(l+bytesToPaste);
		return stuff;
	}
	
	/**
	 * Pastes a segment of zero bytes into a random-access file.
	 * The bytes after the specified offset will be moved to
	 * later in the file. For instance, if your file is
	 * <br><br>
	 * <code>05 23 75 A8 FF 2E 09 DB 3C 01 A9 99 80</code>
	 * <br><br>
	 * then <code>filePaste(f, 4L, 3L)</code> will return 3L
	 * and the new contents of the file will be
	 * <br><br>
	 * <code>05 23 75 A8 00 00 00 FF 2E 09 DB 3C 01 A9 99 80</code>
	 * <br><br>
	 * Precondition: <code>offset</code> is between zero and the length of the file, inclusive.
	 * Other values may give unexpected results.
	 * @param f a <code>RandomAccessFile</code>.
	 * @param offset the offset where the first byte of pasted data should be written.
	 * @param bytesToPaste the number of zero bytes to paste.
	 * @return the number of bytes pasted.
	 * @throws IOException if an I/O error occurs during the paste process.
	 */
	public static long paste(RandomAccessFile f, long offset, long bytesToPaste) throws IOException {
		if (bytesToPaste <= 0) return 0;
		long l = f.length();
		long btm = (l-offset-1) & (~0xFFFFFl);
		byte[] junk = new byte[1048576];
		for (long s = offset+btm, d = offset+btm+bytesToPaste; s >= offset; d -= 1048576, s -= 1048576) {
			f.seek(s);
			f.read(junk);
			f.seek(d);
			f.write(junk);
		}
		f.seek(offset);
		long w = bytesToPaste;
		byte[] stuff = new byte[1048576];
		while (w >= 1048576l) {
			f.write(stuff);
			w -= 1048576l;
		}
		if (w > 0) {
			stuff = new byte[(int)w];
			f.write(stuff);
		}
		f.setLength(l+bytesToPaste);
		return bytesToPaste;
	}
	
	/**
	 * Pastes a segment of data into a random-access file.
	 * The bytes after the specified offset will be moved to
	 * later in the file. For instance, if your file is
	 * <br><br>
	 * <code>05 23 75 A8 FF 2E 09 DB 3C 01 A9 99 80</code>
	 * <br><br>
	 * then <code>filePaste(f, 4, new byte[]{0x55, 0xEE, 0xB4})</code> will return
	 * <br><br>
	 * <code>55 EE B4</code>
	 * <br><br>
	 * and the new contents of the file will be
	 * <br><br>
	 * <code>05 23 75 A8 55 EE B4 FF 2E 09 DB 3C 01 A9 99 80</code>
	 * <br><br>
	 * Precondition: <code>offset</code> is between zero and the length of the file, inclusive.
	 * Other values may give unexpected results.
	 * @param f a <code>RandomAccessFile</code>.
	 * @param offset the offset where the first byte of pasted data should be written.
	 * @param stuff the data to paste.
	 * @return an array containing the pasted bytes.
	 * @throws IOException if an I/O error occurs during the paste process.
	 */
	public static byte[] paste(RandomAccessFile f, long offset, byte[] stuff) throws IOException {
		if (stuff.length <= 0) return new byte[0];
		long l = f.length();
		long btm = (l-offset-1) & (~0xFFFFFl);
		byte[] junk = new byte[1048576];
		for (long s = offset+btm, d = offset+btm+stuff.length; s >= offset; d -= 1048576, s -= 1048576) {
			f.seek(s);
			f.read(junk);
			f.seek(d);
			f.write(junk);
		}
		f.seek(offset);
		f.write(stuff);
		f.setLength(l+stuff.length);
		return stuff;
	}
	
	private static final String[] LOOKUP_HEX = new String[] {
		"00","01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F",
		"10","11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F",
		"20","21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F",
		"30","31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E","3F",
		"40","41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F",
		"50","51","52","53","54","55","56","57","58","59","5A","5B","5C","5D","5E","5F",
		"60","61","62","63","64","65","66","67","68","69","6A","6B","6C","6D","6E","6F",
		"70","71","72","73","74","75","76","77","78","79","7A","7B","7C","7D","7E","7F",
		"80","81","82","83","84","85","86","87","88","89","8A","8B","8C","8D","8E","8F",
		"90","91","92","93","94","95","96","97","98","99","9A","9B","9C","9D","9E","9F",
		"A0","A1","A2","A3","A4","A5","A6","A7","A8","A9","AA","AB","AC","AD","AE","AF",
		"B0","B1","B2","B3","B4","B5","B6","B7","B8","B9","BA","BB","BC","BD","BE","BF",
		"C0","C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD","CE","CF",
		"D0","D1","D2","D3","D4","D5","D6","D7","D8","D9","DA","DB","DC","DD","DE","DF",
		"E0","E1","E2","E3","E4","E5","E6","E7","E8","E9","EA","EB","EC","ED","EE","EF",
		"F0","F1","F2","F3","F4","F5","F6","F7","F8","F9","FA","FB","FC","FD","FE","FF"
	};
	
	public static void printHexDump(PrintStream out, byte[] data) {
		for (int a = 0; a < data.length; a += 16) {
			String h = "00000000" + Integer.toHexString(a).toUpperCase();
			out.print(h.substring(h.length()-8)+": ");
			for (int b = a, c = 0; c < 16; b++, c++) {
				out.print((b < data.length) ? LOOKUP_HEX[data[b] & 0xFF] : "  ");
				if ((b & 1) == 1) out.print(" ");
			}
			out.print(" ");
			for (int b = a, c = 0; b < data.length && c < 16; b++, c++) {
				if (data[b] >= 0x20 && data[b] < 0x7F) out.print((char)data[b]);
				else out.print(".");
			}
			out.println();
		}
	}
}
