package com.kreative.cff;

import java.io.*;
import java.util.Vector;

public class ChunkFileEditor {
	public static final int CREATE_NEVER = 0;
	public static final int CREATE_IF_EMPTY = 1;
	public static final int CREATE_ALWAYS = 2;
	
	private ChunkFileSpec spec;
	private ChunkFile cf;
	private File f;
	
	public ChunkFileEditor(ChunkFileSpec spec) {
		this.spec = spec;
		this.cf = new ChunkFile(spec.fileHeaderSpec().createHeader());
		this.f = null;
	}
	
	public ChunkFileEditor(ChunkFileSpec spec, ChunkFile cf) {
		this.spec = spec;
		this.cf = cf;
		this.f = null;
	}
	
	public ChunkFileEditor(ChunkFileSpec spec, File f, int create) throws IOException {
		this.spec = spec;
		if ((create == CREATE_ALWAYS) || ((create == CREATE_IF_EMPTY) && ((!f.exists()) || (f.length() == 0)))) {
			this.cf = new ChunkFile(spec.fileHeaderSpec().createHeader());
		} else {
			this.cf = spec.readChunkFile(new DataInputStream(new FileInputStream(f)));
		}
		this.f = f;
	}
	
	public ChunkFileSpec getChunkFileSpec() {
		return spec;
	}
	
	public ChunkFile getChunkFile() {
		return cf;
	}
	
	public File getFile() {
		return f;
	}
	
	public synchronized byte[] toByteArray() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		spec.writeChunkFile(dout, cf);
		dout.flush();
		bout.flush();
		dout.close();
		bout.close();
		return bout.toByteArray();
	}
	
	public synchronized void flush() throws IOException {
		if (f != null) {
			FileOutputStream fout = new FileOutputStream(f);
			DataOutputStream dout = new DataOutputStream(fout);
			spec.writeChunkFile(dout, cf);
			dout.flush();
			fout.flush();
			dout.close();
			fout.close();
		}
	}
	
	public synchronized void close() throws IOException {
		if (f != null) {
			FileOutputStream fout = new FileOutputStream(f);
			DataOutputStream dout = new DataOutputStream(fout);
			spec.writeChunkFile(dout, cf);
			dout.flush();
			fout.flush();
			dout.close();
			fout.close();
		}
	}
	
	public synchronized Header getHeader() {
		return cf.getHeader();
	}
	
	public synchronized void setHeader(Header h) {
		cf.setHeader(h);
	}
	
	public synchronized boolean add(Chunk ch) throws ChunkAlreadyExistsException {
		Header h = ch.getHeader();
		if (h.containsKey(FieldType.ID_NUMBER) && contains(h.get(FieldType.CHARACTER_TYPE), h.get(FieldType.INTEGER_TYPE), h.get(FieldType.ID_NUMBER))) {
			throw new ChunkAlreadyExistsException();
		} else {
			cf.add(ch);
			return true;
		}
	}
	
	public synchronized boolean contains(Number ctype, Number itype, Number id) {
		return contains(getChunkIndex(ctype, itype, id));
	}
	
	public synchronized boolean contains(int index) {
		return (index >= 0 && index < cf.size());
	}
	
	public synchronized Chunk get(Number ctype, Number itype, Number id) {
		return get(getChunkIndex(ctype, itype, id));
	}
	
	public synchronized Chunk get(int index) {
		if (index >= 0 && index < cf.size()) {
			return cf.get(index);
		} else {
			return null;
		}
	}
	
	public synchronized Header getAttributes(Number ctype, Number itype, Number id) {
		return getAttributes(getChunkIndex(ctype, itype, id));
	}
	
	public synchronized Header getAttributes(int index) {
		if (index >= 0 && index < cf.size()) {
			return cf.get(index).getHeader();
		} else {
			return null;
		}
	}
	
	public synchronized byte[] getData(Number ctype, Number itype, Number id) {
		return getData(getChunkIndex(ctype, itype, id));
	}
	
	public synchronized byte[] getData(int index) {
		if (index >= 0 && index < cf.size()) {
			return cf.get(index).getData();
		} else {
			return null;
		}
	}
	
	public synchronized boolean insert(Number ctype, Number itype, Number id, Chunk ch) throws ChunkAlreadyExistsException {
		int index = getChunkIndex(ctype, itype, id);
		return (index < 0) ? add(ch) : insert(index, ch);
	}
	
	public synchronized boolean insert(int index, Chunk ch) throws ChunkAlreadyExistsException {
		Header h = ch.getHeader();
		if (h.containsKey(FieldType.ID_NUMBER) && contains(h.get(FieldType.CHARACTER_TYPE), h.get(FieldType.INTEGER_TYPE), h.get(FieldType.ID_NUMBER))) {
			throw new ChunkAlreadyExistsException();
		} else {
			if (index < 0) index = 0;
			if (index > cf.size()) index = cf.size();
			cf.add(index, ch);
			return true;
		}
	}
	
	public synchronized boolean remove(Number ctype, Number itype, Number id) {
		return remove(getChunkIndex(ctype, itype, id));
	}
	
	public synchronized boolean remove(int index) {
		if (index >= 0 && index < cf.size()) {
			cf.remove(index);
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized boolean set(Number ctype, Number itype, Number id, Chunk ch) throws ChunkAlreadyExistsException {
		return set(getChunkIndex(ctype, itype, id), ch);
	}
	
	public synchronized boolean set(int index, Chunk ch) throws ChunkAlreadyExistsException {
		if (index >= 0 && index < cf.size()) {
			Header h = ch.getHeader();
			Header eh = cf.get(index).getHeader();
			if (
					eq(eh.get(FieldType.CHARACTER_TYPE), h.get(FieldType.CHARACTER_TYPE)) &&
					eq(eh.get(FieldType.INTEGER_TYPE), h.get(FieldType.INTEGER_TYPE)) &&
					eq(eh.get(FieldType.ID_NUMBER), h.get(FieldType.ID_NUMBER))
			) {
				cf.set(index, ch);
				return true;
			} else if (h.containsKey(FieldType.ID_NUMBER) && contains(h.get(FieldType.CHARACTER_TYPE), h.get(FieldType.INTEGER_TYPE), h.get(FieldType.ID_NUMBER))) {
				throw new ChunkAlreadyExistsException();
			} else {
				cf.set(index, ch);
				return true;
			}
		} else {
			return false;
		}
	}

	public synchronized boolean setAttributes(Number ctype, Number itype, Number id, Header h) throws ChunkAlreadyExistsException {
		return setAttributes(getChunkIndex(ctype, itype, id), h);
	}

	public synchronized boolean setAttributes(int index, Header h) throws ChunkAlreadyExistsException {
		if (index >= 0 && index < cf.size()) {
			Header eh = cf.get(index).getHeader();
			if (
					eq(eh.get(FieldType.CHARACTER_TYPE), h.get(FieldType.CHARACTER_TYPE)) &&
					eq(eh.get(FieldType.INTEGER_TYPE), h.get(FieldType.INTEGER_TYPE)) &&
					eq(eh.get(FieldType.ID_NUMBER), h.get(FieldType.ID_NUMBER))
			) {
				cf.get(index).setHeader(h);
				return true;
			} else if (h.containsKey(FieldType.ID_NUMBER) && contains(h.get(FieldType.CHARACTER_TYPE), h.get(FieldType.INTEGER_TYPE), h.get(FieldType.ID_NUMBER))) {
				throw new ChunkAlreadyExistsException();
			} else {
				cf.get(index).setHeader(h);
				return true;
			}
		} else {
			return false;
		}
	}
	
	private static boolean eq(Number a, Number b) {
		return (a == null) ? (b == null) : (b == null) ? (a == null) : (a.longValue() == b.longValue());
	}
	
	public synchronized boolean setData(Number ctype, Number itype, Number id, byte[] data) {
		return setData(getChunkIndex(ctype, itype, id), data);
	}
	
	public synchronized boolean setData(int index, byte[] data) {
		if (index >= 0 && index < cf.size()) {
			cf.get(index).setData(data);
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized int getChunkCount() {
		return cf.size();
	}
	
	public synchronized int getChunkCount(Number ctype, Number itype) {
		int count = 0;
		for (int i = 0; i < cf.size(); i++) {
			Header h = cf.get(i).getHeader();
			if (!h.containsKey(FieldType.CHARACTER_TYPE) || (h.get(FieldType.CHARACTER_TYPE).longValue() == ctype.longValue())) {
				if (!h.containsKey(FieldType.INTEGER_TYPE) || (h.get(FieldType.INTEGER_TYPE).longValue() == itype.longValue())) {
					count++;
				}
			}
		}
		return count;
	}
	
	public synchronized Number[] getChunkIDs(Number ctype, Number itype) {
		Vector<Number> ids = new Vector<Number>();
		long localIndex = 0;
		for (int i = 0; i < cf.size(); i++) {
			Header h = cf.get(i).getHeader();
			if (!h.containsKey(FieldType.CHARACTER_TYPE) || (h.get(FieldType.CHARACTER_TYPE).longValue() == ctype.longValue())) {
				if (!h.containsKey(FieldType.INTEGER_TYPE) || (h.get(FieldType.INTEGER_TYPE).longValue() == itype.longValue())) {
					if (h.containsKey(FieldType.ID_NUMBER)) {
						ids.add(h.get(FieldType.ID_NUMBER));
					} else {
						ids.add(localIndex);
					}
					localIndex++;
				}
			}
		}
		return ids.toArray(new Number[0]);
	}
	
	public synchronized Number getChunkID(Number ctype, Number itype, int index) {
		long localIndex = 0;
		for (int i = 0; i < cf.size(); i++) {
			Header h = cf.get(i).getHeader();
			if (!h.containsKey(FieldType.CHARACTER_TYPE) || (h.get(FieldType.CHARACTER_TYPE).longValue() == ctype.longValue())) {
				if (!h.containsKey(FieldType.INTEGER_TYPE) || (h.get(FieldType.INTEGER_TYPE).longValue() == itype.longValue())) {
					if (index == 0) {
						if (h.containsKey(FieldType.ID_NUMBER)) return h.get(FieldType.ID_NUMBER);
						else return localIndex;
					} else {
						localIndex++;
						index--;
					}
				}
			}
		}
		return null;
	}
	
	public synchronized int getChunkIndex(Number ctype, Number itype, Number id) {
		long localIndex = 0;
		for (int i = 0; i < cf.size(); i++) {
			Header h = cf.get(i).getHeader();
			if (!h.containsKey(FieldType.CHARACTER_TYPE) || (h.get(FieldType.CHARACTER_TYPE).longValue() == ctype.longValue())) {
				if (!h.containsKey(FieldType.INTEGER_TYPE) || (h.get(FieldType.INTEGER_TYPE).longValue() == itype.longValue())) {
					if (h.containsKey(FieldType.ID_NUMBER)) {
						if (h.get(FieldType.ID_NUMBER).longValue() == id.longValue()) {
							return i;
						}
					} else {
						if (localIndex == id.longValue()) {
							return i;
						}
					}
					localIndex++;
				}
			}
		}
		return -1;
	}
	
	public synchronized Number getNextAvailableID(Number ctype, Number itype) {
		return getNextAvailableID(ctype, itype, 0);
	}
	
	public synchronized Number getNextAvailableID(Number ctype, Number itype, Number start) {
		Vector<Long> ids = new Vector<Long>();
		long localIndex = 0;
		for (int i = 0; i < cf.size(); i++) {
			Header h = cf.get(i).getHeader();
			if (!h.containsKey(FieldType.CHARACTER_TYPE) || (h.get(FieldType.CHARACTER_TYPE).longValue() == ctype.longValue())) {
				if (!h.containsKey(FieldType.INTEGER_TYPE) || (h.get(FieldType.INTEGER_TYPE).longValue() == itype.longValue())) {
					if (h.containsKey(FieldType.ID_NUMBER)) {
						ids.add(h.get(FieldType.ID_NUMBER).longValue());
					} else {
						ids.add(localIndex);
					}
					localIndex++;
				}
			}
		}
		long next = start.longValue();
		while (ids.contains(next)) next++;
		if (spec.chunkHeaderSpec().containsType(FieldType.ID_NUMBER)) {
			switch (spec.chunkHeaderSpec().getField(FieldType.ID_NUMBER).size()) {
			case BYTE: return (byte)next;
			case SHORT: return (short)next;
			case MEDIUM: return (int)next;
			case LONG: return (long)next;
			default: return next;
			}
		} else {
			return next;
		}
	}
}
