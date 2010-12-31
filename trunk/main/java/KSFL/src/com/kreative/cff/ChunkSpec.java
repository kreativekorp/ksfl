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
import java.util.Vector;
import java.util.Collection;
import java.util.Arrays;

public class ChunkSpec extends Vector<FieldSpec> {
	private static final long serialVersionUID = 1L;
	
	private boolean evenPadded;
	
	public ChunkSpec() {
		super();
		evenPadded = false;
	}
	
	public ChunkSpec(FieldSpec[] a) {
		super(Arrays.asList(a));
		evenPadded = false;
	}

	public ChunkSpec(Collection<? extends FieldSpec> c) {
		super(c);
		evenPadded = false;
	}

	public ChunkSpec(boolean evenPadded) {
		super();
		this.evenPadded = evenPadded;
	}
	
	public ChunkSpec(FieldSpec[] a, boolean evenPadded) {
		super(Arrays.asList(a));
		this.evenPadded = evenPadded;
	}

	public ChunkSpec(Collection<? extends FieldSpec> c, boolean evenPadded) {
		super(c);
		this.evenPadded = evenPadded;
	}

	public ChunkSpec(String spec) {
		super();
		evenPadded = false;
		String[] things = spec.trim().split("[ .,-]+");
		for (String thing : things) {
			thing = thing.trim();
			if (thing.equalsIgnoreCase("e") || thing.equalsIgnoreCase("even") || thing.equalsIgnoreCase("evenpadded")) {
				evenPadded = true;
			} else if (thing.length() > 0) {
				add(new FieldSpec(thing));
			}
		}
	}
	
	public ChunkSpec(byte[] spec, int index) {
		super();
		evenPadded = ((spec[index] & 0x80) != 0);
		int n = (spec[index] & 0x7F);
		for (int i = 0; i < n; i++) {
			add(new FieldSpec(spec[++index]));
		}
	}
	
	public boolean evenPadded() {
		return evenPadded;
	}
	
	public boolean containsType(FieldType ft) {
		for (FieldSpec fs : this) {
			if (fs.type() == ft) return true;
		}
		return false;
	}
	
	public FieldSpec getField(FieldType ft) {
		for (FieldSpec fs : this) {
			if (fs.type() == ft) return fs;
		}
		return null;
	}
	
	public int byteCount() {
		int cnt = 0;
		for (FieldSpec f : this) cnt += f.byteCount();
		return cnt;
	}

	public int bitCount() {
		int cnt = 0;
		for (FieldSpec f : this) cnt += f.bitCount();
		return cnt;
	}
	
	public String stringRepresentation() {
		String s = "";
		for (FieldSpec fs : this) {
			s += "."+fs.stringRepresentation();
		}
		if (evenPadded) s += ".e";
		return (s.length() >= 1) ? s.substring(1) : s;
	}
	
	public byte[] bitPatternRepresentation() {
		byte[] bp = new byte[size()+1];
		bp[0] = (byte)(size() & 0x7F);
		if (evenPadded) bp[0] |= 0x80;
		int idx = 1;
		for (FieldSpec fs : this) bp[idx++] = fs.bitPatternRepresentation();
		return bp;
	}
	
	public Header createHeader() {
		Header h = new Header();
		for (FieldSpec fs : this) {
			switch (fs.size()) {
			case LONG: h.put(fs.type(), 0L); break;
			case MEDIUM: h.put(fs.type(), 0); break;
			case SHORT: h.put(fs.type(), (short)0); break;
			case BYTE: h.put(fs.type(), (byte)0); break;
			}
		}
		return h;
	}
	
	public Header readHeader(DataInput in) throws IOException {
		Header h = new Header();
		Number lastSize = 0;
		for (FieldSpec f : this) {
			if (f.nativeType() == null) {
				in.skipBytes(lastSize.intValue());
				if (evenPadded && (lastSize.longValue() % 2) == 1) in.readByte();
			} else {
				Number n = f.read(in);
				if (f.type().equals(FieldType.SIZE_WITHOUT_HEADER)) {
					if (n.longValue() < 0) throw new IOException("Negative Size");
					lastSize = n;
				} else if (f.type().equals(FieldType.SIZE_WITH_HEADER)) {
					if (n.longValue() < byteCount()) throw new IOException("Negative Size");
					lastSize = n.longValue() - byteCount();
				}
				h.put(f.type(), n);
			}
		}
		return h;
	}
	
	public Chunk readChunk(DataInput in) throws IOException {
		Header h = new Header();
		Number lastSize = 0;
		byte[] d = new byte[0];
		for (FieldSpec f : this) {
			if (f.nativeType() == null) {
				in.readFully(d = new byte[lastSize.intValue()]);
				if (evenPadded && (lastSize.longValue() % 2) == 1) in.readByte();
			} else {
				Number n = f.read(in);
				if (f.type().equals(FieldType.SIZE_WITHOUT_HEADER)) {
					if (n.longValue() < 0) throw new IOException("Negative Size");
					lastSize = n;
				} else if (f.type().equals(FieldType.SIZE_WITH_HEADER)) {
					if (n.longValue() < byteCount()) throw new IOException("Negative Size");
					lastSize = n.longValue() - byteCount();
				}
				h.put(f.type(), n);
			}
		}
		return new Chunk(h,d);
	}
	
	public void writeHeader(DataOutput out, Header h) throws IOException {
		Number lastSize = 0;
		for (FieldSpec f : this) {
			if (f.nativeType() == null) {
				out.write(new byte[lastSize.intValue()]);
				if (evenPadded && (lastSize.longValue() % 2) == 1) out.writeByte(0);
			} else {
				Number n = h.get(f.type());
				if (f.type().equals(FieldType.SIZE_WITHOUT_HEADER)) {
					if (n.longValue() < 0) throw new IllegalArgumentException("Negative Size");
					lastSize = n;
				} else if (f.type().equals(FieldType.SIZE_WITH_HEADER)) {
					if (n.longValue() < byteCount()) throw new IllegalArgumentException("Negative Size");
					lastSize = n.longValue() - byteCount();
				}
				f.write(out, n);
			}
		}
	}
	
	public void writeChunk(DataOutput out, Chunk ch) throws IOException {
		Header h = ch.getHeader();
		for (FieldSpec f : this) {
			if (f.nativeType() == null) {
				out.write(ch.getData());
				if (evenPadded && (ch.getData().length % 2) == 1) out.writeByte(0);
			} else {
				Number n;
				if (f.type().equals(FieldType.SIZE_WITHOUT_HEADER)) {
					n = ch.getData().length;
					h.put(f.type(), n);
				} else if (f.type().equals(FieldType.SIZE_WITH_HEADER)) {
					n = ch.getData().length + byteCount();
					h.put(f.type(), n);
				} else {
					n = h.get(f.type());
				}
				f.write(out, n);
			}
		}
	}
	
	public boolean equals(Object o) {
		return (
				o instanceof ChunkSpec
				&& super.equals(o)
				&& ((ChunkSpec)o).evenPadded == evenPadded
		);
	}
	
	public int hashCode() {
		return super.hashCode() ^ (evenPadded ? -1 : 0);
	}
}
