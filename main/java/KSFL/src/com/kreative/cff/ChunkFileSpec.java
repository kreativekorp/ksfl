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
import java.io.EOFException;

public class ChunkFileSpec implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// T4.S4.d.e
	public static final ChunkFileSpec CFSPEC_IFF = new ChunkFileSpec(
			new ChunkSpec(),
			new ChunkSpec(new FieldSpec[] {
					new FieldSpec(FieldType.CHARACTER_TYPE, FieldSize.MEDIUM),
					new FieldSpec(FieldType.SIZE_WITHOUT_HEADER, FieldSize.MEDIUM),
					FieldSpec.DATA
			}, true)
	);
	
	// T4.S4l.d.e
	public static final ChunkFileSpec CFSPEC_RIFF = new ChunkFileSpec(
			new ChunkSpec(),
			new ChunkSpec(new FieldSpec[] {
					new FieldSpec(FieldType.CHARACTER_TYPE, FieldSize.MEDIUM),
					new FieldSpec(FieldType.SIZE_WITHOUT_HEADER, FieldSize.MEDIUM, true),
					FieldSpec.DATA
			}, true)
	);
	
	// T4.S4.d
	public static final ChunkFileSpec CFSPEC_MIDI = new ChunkFileSpec(
			new ChunkSpec(),
			new ChunkSpec(new FieldSpec[] {
					new FieldSpec(FieldType.CHARACTER_TYPE, FieldSize.MEDIUM),
					new FieldSpec(FieldType.SIZE_WITHOUT_HEADER, FieldSize.MEDIUM),
					FieldSpec.DATA
			})
	);
	
	// M8:S4.T4.d.H4
	public static final ChunkFileSpec CFSPEC_PNG = new ChunkFileSpec(
			new ChunkSpec(new FieldSpec[] {
					new FieldSpec(FieldType.INTEGER_TYPE, FieldSize.LONG)
			}),
			new ChunkSpec(new FieldSpec[] {
					new FieldSpec(FieldType.SIZE_WITHOUT_HEADER, FieldSize.MEDIUM),
					new FieldSpec(FieldType.CHARACTER_TYPE, FieldSize.MEDIUM),
					FieldSpec.DATA,
					new FieldSpec(FieldType.CHECKSUM, FieldSize.MEDIUM)
			})
	);
	
	// T4.Z4:T4.Z4.d
	public static final ChunkFileSpec CFSPEC_ICNS = new ChunkFileSpec(
			new ChunkSpec(new FieldSpec[] {
					new FieldSpec(FieldType.CHARACTER_TYPE, FieldSize.MEDIUM),
					new FieldSpec(FieldType.SIZE_WITH_HEADER, FieldSize.MEDIUM)
			}),
			new ChunkSpec(new FieldSpec[] {
					new FieldSpec(FieldType.CHARACTER_TYPE, FieldSize.MEDIUM),
					new FieldSpec(FieldType.SIZE_WITH_HEADER, FieldSize.MEDIUM),
					FieldSpec.DATA
			})
	);
	
	// Z4.T4.N4.F4.d
	public static final ChunkFileSpec CFSPEC_HYPERCARD = new ChunkFileSpec(
			new ChunkSpec(),
			new ChunkSpec(new FieldSpec[] {
					new FieldSpec(FieldType.SIZE_WITH_HEADER, FieldSize.MEDIUM),
					new FieldSpec(FieldType.CHARACTER_TYPE, FieldSize.MEDIUM),
					new FieldSpec(FieldType.ID_NUMBER, FieldSize.MEDIUM),
					new FieldSpec(FieldType.FILLER, FieldSize.MEDIUM),
					FieldSpec.DATA
			})
	);
	
	// T4:T8.N2.F2.S4.d
	public static final ChunkFileSpec CFSPEC_DFF1BE = new ChunkFileSpec(
			new ChunkSpec(new FieldSpec[] {
					new FieldSpec(FieldType.CHARACTER_TYPE, FieldSize.MEDIUM)
			}),
			new ChunkSpec(new FieldSpec[] {
					new FieldSpec(FieldType.CHARACTER_TYPE, FieldSize.LONG),
					new FieldSpec(FieldType.ID_NUMBER, FieldSize.SHORT),
					new FieldSpec(FieldType.FILLER, FieldSize.SHORT),
					new FieldSpec(FieldType.SIZE_WITHOUT_HEADER, FieldSize.MEDIUM),
					FieldSpec.DATA
			})
	);
	
	// T4l:T8l.N2l.F2l.S4l.d
	public static final ChunkFileSpec CFSPEC_DFF1LE = new ChunkFileSpec(
			new ChunkSpec(new FieldSpec[] {
					new FieldSpec(FieldType.CHARACTER_TYPE, FieldSize.MEDIUM, true)
			}),
			new ChunkSpec(new FieldSpec[] {
					new FieldSpec(FieldType.CHARACTER_TYPE, FieldSize.LONG, true),
					new FieldSpec(FieldType.ID_NUMBER, FieldSize.SHORT, true),
					new FieldSpec(FieldType.FILLER, FieldSize.SHORT, true),
					new FieldSpec(FieldType.SIZE_WITHOUT_HEADER, FieldSize.MEDIUM, true),
					FieldSpec.DATA
			})
	);
	
	private ChunkSpec fileHeaderSpec;
	private ChunkSpec chunkHeaderSpec;
	
	public ChunkFileSpec(ChunkSpec fileHS, ChunkSpec chunkHS) {
		fileHeaderSpec = fileHS;
		chunkHeaderSpec = chunkHS;
	}
	
	public ChunkFileSpec(String spec) {
		String[] things = spec.trim().split("[:;=]+");
		switch (things.length) {
		case 0:
			fileHeaderSpec = new ChunkSpec();
			chunkHeaderSpec = new ChunkSpec();
			break;
		case 1:
			fileHeaderSpec = new ChunkSpec();
			chunkHeaderSpec = new ChunkSpec(things[0]);
			break;
		default:
			fileHeaderSpec = new ChunkSpec(things[0]);
			chunkHeaderSpec = new ChunkSpec(things[1]);
			break;
		}
	}
	
	public ChunkFileSpec(byte[] spec, int index) {
		fileHeaderSpec = new ChunkSpec(spec, index);
		chunkHeaderSpec = new ChunkSpec(spec, index+1+fileHeaderSpec.size());
	}
	
	public ChunkSpec fileHeaderSpec() {
		return fileHeaderSpec;
	}
	
	public ChunkSpec chunkHeaderSpec() {
		return chunkHeaderSpec;
	}
	
	public String stringRepresentation() {
		String a = fileHeaderSpec.stringRepresentation();
		String b = chunkHeaderSpec.stringRepresentation();
		if (a == null || a.length() == 0) return b;
		else return a + ":" + b;
	}
	
	public byte[] bitPatternRepresentation() {
		byte[] a = fileHeaderSpec.bitPatternRepresentation();
		byte[] b = chunkHeaderSpec.bitPatternRepresentation();
		byte[] c = new byte[a.length + b.length];
		int ci = 0;
		for (int ai = 0; ai < a.length; ai++, ci++) c[ci] = a[ai];
		for (int bi = 0; bi < b.length; bi++, ci++) c[ci] = b[bi];
		return c;
	}
	
	public ChunkFile readChunkHeaders(DataInput in) throws IOException {
		Header fh = fileHeaderSpec.readHeader(in);
		ChunkFile cf = new ChunkFile(fh);
		if (fh.containsKey(FieldType.CHUNK_COUNT)) {
			long i = 0;
			long c = fh.get(FieldType.CHUNK_COUNT).longValue();
			while (i < c) {
				Header h = chunkHeaderSpec.readHeader(in);
				i++;
				cf.add(new Chunk(h, new byte[0]));
			}
		}
		else if (fh.containsKey(FieldType.SIZE_WITHOUT_HEADER)) {
			long i = 0;
			long c = fh.get(FieldType.SIZE_WITHOUT_HEADER).longValue();
			while (i < c) {
				Header h = chunkHeaderSpec.readHeader(in);
				long dl = (
					h.containsKey(FieldType.SIZE_WITH_HEADER) ?
					h.get(FieldType.SIZE_WITH_HEADER).longValue() :
					h.containsKey(FieldType.SIZE_WITHOUT_HEADER) ?
					(chunkHeaderSpec.byteCount() + h.get(FieldType.SIZE_WITHOUT_HEADER).longValue()) :
					0
				);
				if (chunkHeaderSpec.evenPadded() && (dl % 2) == 1) dl++;
				i += dl;
				cf.add(new Chunk(h, new byte[0]));
			}
		}
		else if (fh.containsKey(FieldType.SIZE_WITH_HEADER)) {
			long i = fileHeaderSpec.byteCount();
			long c = fh.get(FieldType.SIZE_WITH_HEADER).longValue();
			while (i < c) {
				Header h = chunkHeaderSpec.readHeader(in);
				long dl = (
					h.containsKey(FieldType.SIZE_WITH_HEADER) ?
					h.get(FieldType.SIZE_WITH_HEADER).longValue() :
					h.containsKey(FieldType.SIZE_WITHOUT_HEADER) ?
					(chunkHeaderSpec.byteCount() + h.get(FieldType.SIZE_WITHOUT_HEADER).longValue()) :
					0
				);
				if (chunkHeaderSpec.evenPadded() && (dl % 2) == 1) dl++;
				i += dl;
				cf.add(new Chunk(h, new byte[0]));
			}
		}
		else {
			while (true) {
				try {
					Header h = chunkHeaderSpec.readHeader(in);
					cf.add(new Chunk(h, new byte[0]));
				} catch (EOFException eof) {
					break;
				}
			}
		}
		return cf;
	}
	
	public ChunkFile readChunkFile(DataInput in) throws IOException {
		Header fh = fileHeaderSpec.readHeader(in);
		ChunkFile cf = new ChunkFile(fh);
		if (fh.containsKey(FieldType.CHUNK_COUNT)) {
			long i = 0;
			long c = fh.get(FieldType.CHUNK_COUNT).longValue();
			while (i < c) {
				Chunk ch = chunkHeaderSpec.readChunk(in);
				i++;
				cf.add(ch);
			}
		}
		else if (fh.containsKey(FieldType.SIZE_WITHOUT_HEADER)) {
			long i = 0;
			long c = fh.get(FieldType.SIZE_WITHOUT_HEADER).longValue();
			while (i < c) {
				Chunk ch = chunkHeaderSpec.readChunk(in);
				i += chunkHeaderSpec.byteCount() + ch.getData().length;
				if (chunkHeaderSpec.evenPadded() && ((ch.getData().length % 2) == 1)) i++;
				cf.add(ch);
			}
		}
		else if (fh.containsKey(FieldType.SIZE_WITH_HEADER)) {
			long i = fileHeaderSpec.byteCount();
			long c = fh.get(FieldType.SIZE_WITH_HEADER).longValue();
			while (i < c) {
				Chunk ch = chunkHeaderSpec.readChunk(in);
				i += chunkHeaderSpec.byteCount() + ch.getData().length;
				if (chunkHeaderSpec.evenPadded() && ((ch.getData().length % 2) == 1)) i++;
				cf.add(ch);
			}
		}
		else {
			while (true) {
				try {
					Chunk ch = chunkHeaderSpec.readChunk(in);
					cf.add(ch);
				} catch (EOFException eof) {
					break;
				}
			}
		}
		return cf;
	}
	
	public void writeChunkFile(DataOutput out, ChunkFile cf) throws IOException {
		Header fh = cf.getHeader();
		if (fh.containsKey(FieldType.CHUNK_COUNT)) {
			fh.put(FieldType.CHUNK_COUNT, cf.size());
		}
		if (fh.containsKey(FieldType.SIZE_WITHOUT_HEADER)) {
			long l = chunkHeaderSpec.byteCount() * cf.size();
			for (Chunk ch : cf) {
				l += ch.getData().length;
				if (chunkHeaderSpec.evenPadded() && (ch.getData().length % 2) == 1) l++;
			}
			fh.put(FieldType.SIZE_WITHOUT_HEADER, l);
		}
		if (fh.containsKey(FieldType.SIZE_WITH_HEADER)) {
			long l = fileHeaderSpec.byteCount() + chunkHeaderSpec.byteCount() * cf.size();
			for (Chunk ch : cf) {
				l += ch.getData().length;
				if (chunkHeaderSpec.evenPadded() && (ch.getData().length % 2) == 1) l++;
			}
			fh.put(FieldType.SIZE_WITH_HEADER, l);
		}
		fileHeaderSpec.writeHeader(out, fh);
		for (Chunk ch : cf) chunkHeaderSpec.writeChunk(out, ch);
	}
	
	public boolean equals(Object o) {
		return (
				o instanceof ChunkFileSpec
				&& ((ChunkFileSpec)o).fileHeaderSpec.equals(fileHeaderSpec)
				&& ((ChunkFileSpec)o).chunkHeaderSpec.equals(chunkHeaderSpec)
		);
	}
	
	public int hashCode() {
		return fileHeaderSpec.hashCode() ^ chunkHeaderSpec.hashCode();
	}
}
