/*
 * Copyright &copy; 2008-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.cff;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public enum FieldSize {
	BYTE	(1,	Byte.class,		new char[]{'1','B','b'},			(byte)0x10),
	SHORT	(2,	Short.class,	new char[]{'2','S','s'},			(byte)0x20),
	MEDIUM	(4,	Integer.class,	new char[]{'4','M','m','I','i'},	(byte)0x40),
	LONG	(8,	Long.class,		new char[]{'8','L','l'},			(byte)0x60),
	DATA	(0,	null,			new char[]{'0','D','d'},			(byte)0x70);
	
	private int byteCount;
	private Class<? extends Number> nativeType;
	private char[] letters;
	private byte bitPattern;
	
	private FieldSize(int bc, Class<? extends Number> nt, char[] letters, byte bitPattern) {
		byteCount = bc;
		nativeType = nt;
		this.letters = letters;
		this.bitPattern = bitPattern;
	}
	
	public static FieldSize forChar(char letter) {
		for (FieldSize fs : FieldSize.values()) {
			for (char ch : fs.letters) {
				if (ch == letter) return fs;
			}
		}
		return null;
	}
	
	public static FieldSize forBitPattern(byte bitPattern) {
		for (FieldSize fs : FieldSize.values()) {
			if (((fs.bitPattern >>> 4) & 0x07) == ((bitPattern >>> 4) & 0x07)) return fs;
		}
		return null;
	}
	
	public int byteCount() {
		return byteCount;
	}
	
	public int bitCount() {
		return byteCount*8;
	}
	
	public Class<? extends Number> nativeType() {
		return nativeType;
	}
	
	public char[] allCharRepresentations() {
		return letters;
	}
	
	public char canonicalCharRepresentation() {
		return letters[0];
	}
	
	public byte bitPatternRepresentation() {
		return bitPattern;
	}
	
	public Number readBE(DataInput in) throws IOException {
		switch (this) {
		case BYTE: return in.readByte();
		case SHORT: return in.readShort();
		case MEDIUM: return in.readInt();
		case LONG: return in.readLong();
		default: return null;
		}
	}
	
	public void writeBE(DataOutput out, Number n) throws IOException {
		switch (this) {
		case BYTE: out.writeByte(n.byteValue()); break;
		case SHORT: out.writeShort(n.shortValue()); break;
		case MEDIUM: out.writeInt(n.intValue()); break;
		case LONG: out.writeLong(n.longValue()); break;
		}
	}
	
	public Number readLE(DataInput in) throws IOException {
		switch (this) {
		case BYTE: return in.readByte();
		case SHORT: return Short.reverseBytes(in.readShort());
		case MEDIUM: return Integer.reverseBytes(in.readInt());
		case LONG: return Long.reverseBytes(in.readLong());
		default: return null;
		}
	}
	
	public void writeLE(DataOutput out, Number n) throws IOException {
		switch (this) {
		case BYTE: out.writeByte(n.byteValue()); break;
		case SHORT: out.writeShort(Short.reverseBytes(n.shortValue())); break;
		case MEDIUM: out.writeInt(Integer.reverseBytes(n.intValue())); break;
		case LONG: out.writeLong(Long.reverseBytes(n.longValue())); break;
		}
	}
}
