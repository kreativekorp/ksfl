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

import java.io.Serializable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FieldSpec implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final FieldSpec DATA = new FieldSpec(FieldType.DATA, FieldSize.DATA);
	
	private FieldType type;
	private FieldSize size;
	private boolean littleEndian;
	
	public FieldSpec(FieldType type, FieldSize size) {
		this.type = type;
		this.size = size;
		this.littleEndian = false;
	}
	
	public FieldSpec(FieldType type, FieldSize size, boolean littleEndian) {
		this.type = type;
		this.size = size;
		this.littleEndian = littleEndian;
	}
	
	public FieldSpec(String spec) {
		this.type = null;
		this.size = null;
		this.littleEndian = false;
		spec = spec.trim();
		if (spec.length() > 0) this.type = FieldType.forChar(spec.charAt(0));
		if (this.type == null) this.type = FieldType.CHARACTER_TYPE;
		if (spec.length() > 1) this.size = FieldSize.forChar(spec.charAt(1));
		if (this.size == null) this.size = ((this.type == FieldType.DATA) ? FieldSize.DATA : FieldSize.MEDIUM);
		if (spec.length() > 2) switch (spec.charAt(2)) {
		case 'L': case 'l': case 'I': case 'i': this.littleEndian = true; break;
		case 'B': case 'b': case 'M': case 'm': this.littleEndian = false; break;
		}
	}
	
	public FieldSpec(byte spec) {
		this.type = FieldType.forBitPattern(spec);
		if (this.type == null) this.type = FieldType.CHARACTER_TYPE;
		this.size = FieldSize.forBitPattern(spec);
		if (this.size == null) this.size = ((this.type == FieldType.DATA) ? FieldSize.DATA : FieldSize.MEDIUM);
		this.littleEndian = ((spec & 0x80) != 0);
	}
	
	public FieldType type() {
		return type;
	}
	
	public FieldSize size() {
		return size;
	}
	
	public boolean littleEndian() {
		return littleEndian;
	}
	
	public boolean bigEndian() {
		return !littleEndian;
	}
	
	public int byteCount() {
		return size.byteCount();
	}
	
	public int bitCount() {
		return size.bitCount();
	}
	
	public Class<? extends Number> nativeType() {
		return size.nativeType();
	}
	
	public String stringRepresentation() {
		String s = ""+type.canonicalCharRepresentation();
		if (size.nativeType() != null) {
			s += size.canonicalCharRepresentation();
			if (littleEndian) s += "l";
		}
		return s;
	}
	
	public byte bitPatternRepresentation() {
		return (byte)(
				type.bitPatternRepresentation() |
				size.bitPatternRepresentation() |
				(byte)(littleEndian ? (byte)0x80 : (byte)0x00)
		);
	}
	
	public Number read(DataInput in) throws IOException {
		return littleEndian ? size.readLE(in) : size.readBE(in);
	}
	
	public void write(DataOutput out, Number n) throws IOException {
		if (littleEndian) size.writeLE(out, n); else size.writeBE(out, n);
	}
	
	public boolean equals(Object o) {
		return (
				o instanceof FieldSpec
				&& ((FieldSpec)o).type == type
				&& ((FieldSpec)o).size == size
				&& ((FieldSpec)o).littleEndian == littleEndian
		);
	}
	
	public int hashCode() {
		return type.hashCode() ^ size.hashCode() ^ (littleEndian ? -1 : 0);
	}
}
