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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import com.kreative.ksfl.KSFLUtilities;

/**
 * The <code>DFFCollection</code> class provides a DFF interface to a Map.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class DFFResourceCollection extends DFFResourceProvider implements Map<Long,Map<Integer,DFFResource>> {
	private Map<Long,Map<Integer,DFFResource>> map;
	
	/**
	 * Creates a new, empty DFFCollection.
	 */
	public DFFResourceCollection() {
		map = new HashMap<Long,Map<Integer,DFFResource>>();
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
		if (map.containsKey(r.type) && map.get(r.type).containsKey(r.id)) {
			throw new DFFResourceAlreadyExistsException();
		} else {
			if (map.containsKey(r.type)) {
				map.get(r.type).put(r.id, r.deepCopy());
			} else {
				Map<Integer,DFFResource> nmap = new HashMap<Integer,DFFResource>();
				nmap.put(r.id, r.deepCopy());
				map.put(r.type, nmap);
			}
			return true;
		}
	}
	
	@Override
	public synchronized boolean contains(long type, int id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			return true;
		}
		return false;
	}
	@Override
	public synchronized boolean contains(long type, String name) {
		if (map.containsKey(type)) {
			for (DFFResource r : map.get(type).values()) {
				if (r.name.equals(name)) return true;
			}
		}
		return false;
	}
	
	private DFFResource find(long type, int id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			return map.get(type).get(id);
		}
		return null;
	}
	private DFFResource find(long type, String name) {
		if (map.containsKey(type)) {
			for (DFFResource r : map.get(type).values()) {
				if (r.name.equals(name)) return r;
			}
		}
		return null;
	}
	
	@Override
	public synchronized DFFResource get(long type, int id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			return map.get(type).get(id).deepCopy();
		}
		return null;
	}
	@Override
	public synchronized DFFResource get(long type, String name) {
		if (map.containsKey(type)) {
			for (DFFResource r : map.get(type).values()) {
				if (r.name.equals(name)) return r.deepCopy();
			}
		}
		return null;
	}
	
	@Override
	public synchronized DFFResource getAttributes(long type, int id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			DFFResource r = map.get(type).get(id);
			return new DFFResource(r.type, r.id, r.datatype, r.getAttributes(), r.name, new byte[0]);
		}
		return null;
	}
	@Override
	public synchronized DFFResource getAttributes(long type, String name) {
		if (map.containsKey(type)) {
			for (DFFResource r : map.get(type).values()) {
				if (r.name.equals(name)) {
					return new DFFResource(r.type, r.id, r.datatype, r.getAttributes(), r.name, new byte[0]);
				}
			}
		}
		return null;
	}
	
	@Override
	public synchronized long getLength(long type, int id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			return map.get(type).get(id).data.length;
		}
		return 0;
	}
	@Override
	public synchronized long getLength(long type, String name) {
		if (map.containsKey(type)) {
			for (DFFResource r : map.get(type).values()) {
				if (r.name.equals(name)) return r.data.length;
			}
		}
		return 0;
	}
	
	@Override
	public synchronized byte[] getData(long type, int id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			return KSFLUtilities.copy(map.get(type).get(id).data);
		}
		return null;
	}
	@Override
	public synchronized byte[] getData(long type, String name) {
		if (map.containsKey(type)) {
			for (DFFResource r : map.get(type).values()) {
				if (r.name.equals(name)) return KSFLUtilities.copy(r.data);
			}
		}
		return null;
	}
	
	@Override
	public synchronized int read(long type, int id, long doffset, byte[] data, int off, int len) {
		int written = 0;
		try {
			DFFResource r = find(type, id);
			if (r == null) return 0;
			byte[] d = r.data;
			if (d == null) return 0;
			while (written < len && off < data.length && doffset < d.length) {
				data[off] = d[(int)doffset];
				off++; doffset++; written++;
			}
			while (written < len && off < data.length) {
				data[off] = 0;
				off++; written++;
			}
		} catch (Exception e) {}
		return written;
	}
	@Override
	public synchronized int read(long type, String name, long doffset, byte[] data, int off, int len) {
		int written = 0;
		try {
			DFFResource r = find(type, name);
			if (r == null) return 0;
			byte[] d = r.data;
			if (d == null) return 0;
			while (written < len && off < data.length && doffset < d.length) {
				data[off] = d[(int)doffset];
				off++; doffset++; written++;
			}
			while (written < len && off < data.length) {
				data[off] = 0;
				off++; written++;
			}
		} catch (Exception e) {}
		return written;
	}
	
	@Override
	public synchronized boolean remove(long type, int id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			map.get(type).remove(id);
			if (map.get(type).isEmpty()) {
				map.remove(type);
			}
			return true;
		}
		return false;
	}
	@Override
	public synchronized boolean remove(long type, String name) {
		if (map.containsKey(type)) {
			for (DFFResource r : map.get(type).values()) {
				if (r.name.equals(name)) {
					map.get(type).remove(r);
					if (map.get(type).isEmpty()) {
						map.remove(type);
					}
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public synchronized boolean set(long type, int id, DFFResource r) throws DFFResourceAlreadyExistsException {
		if (!contains(type, id)) {
			return false;
		} else {
			DFFResource o = find(type, id);
			if ((o.type != r.type || o.id != r.id) && contains(r.type, r.id)) {
				throw new DFFResourceAlreadyExistsException();
			} else {
				return remove(type, id) && add(r);
			}
		}
	}
	@Override
	public synchronized boolean set(long type, String name, DFFResource r) throws DFFResourceAlreadyExistsException {
		if (!contains(type, name)) {
			return false;
		} else {
			DFFResource o = find(type, name);
			if ((o.type != r.type || o.id != r.id) && contains(r.type, r.id)) {
				throw new DFFResourceAlreadyExistsException();
			} else {
				return remove(type, name) && add(r);
			}
		}
	}
	
	@Override
	public synchronized boolean setAttributes(long type, int id, DFFResource r) throws DFFResourceAlreadyExistsException {
		if (!contains(type, id)) {
			return false;
		} else {
			DFFResource o = find(type, id);
			if ((o.type != r.type || o.id != r.id) && contains(r.type, r.id)) {
				throw new DFFResourceAlreadyExistsException();
			} else {
				r = r.deepCopy();
				r.data = o.data;
				return remove(type, id) && add(r);
			}
		}
	}
	@Override
	public synchronized boolean setAttributes(long type, String name, DFFResource r) throws DFFResourceAlreadyExistsException {
		if (!contains(type, name)) {
			return false;
		} else {
			DFFResource o = find(type, name);
			if ((o.type != r.type || o.id != r.id) && contains(r.type, r.id)) {
				throw new DFFResourceAlreadyExistsException();
			} else {
				r = r.deepCopy();
				r.data = o.data;
				return remove(type, name) && add(r);
			}
		}
	}
	
	@Override
	public synchronized boolean setLength(long type, int id, long len) {
		if (len > Integer.MAX_VALUE) throw new DFFResourceTooBigException();
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			DFFResource r = map.get(type).get(id);
			r.data = KSFLUtilities.resize(r.data, (int)len);
			return true;
		}
		return false;
	}
	@Override
	public synchronized boolean setLength(long type, String name, long len) {
		if (len > Integer.MAX_VALUE) throw new DFFResourceTooBigException();
		if (map.containsKey(type)) {
			for (DFFResource r : map.get(type).values()) {
				if (r.name.equals(name)) {
					r.data = KSFLUtilities.resize(r.data, (int)len);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public synchronized boolean setData(long type, int id, byte[] data) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			map.get(type).get(id).data = KSFLUtilities.copy(data);
			return true;
		}
		return false;
	}
	@Override
	public synchronized boolean setData(long type, String name, byte[] data) {
		if (map.containsKey(type)) {
			for (DFFResource r : map.get(type).values()) {
				if (r.name.equals(name)) {
					r.data = KSFLUtilities.copy(data);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public synchronized int write(long type, int id, long doffset, byte[] data, int off, int len) {
		if (doffset + len > Integer.MAX_VALUE) throw new DFFResourceTooBigException();
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			DFFResource r = map.get(type).get(id);
			int written = 0;
			try {
				if (doffset + len > r.data.length) {
					r.data = KSFLUtilities.resize(r.data, (int)(doffset + len));
				}
				while (written < len && off < data.length && doffset < r.data.length) {
					r.data[(int)doffset] = data[off];
					off++; doffset++; written++;
				}
			} catch (Exception e) {}
			return written;
		}
		return 0;
	}
	@Override
	public synchronized int write(long type, String name, long doffset, byte[] data, int off, int len) {
		if (doffset + len > Integer.MAX_VALUE) throw new DFFResourceTooBigException();
		if (map.containsKey(type)) {
			for (DFFResource r : map.get(type).values()) {
				if (r.name.equals(name)) {
					int written = 0;
					try {
						if (doffset + len > r.data.length) {
							r.data = KSFLUtilities.resize(r.data, (int)(doffset + len));
						}
						while (written < len && off < data.length && doffset < r.data.length) {
							r.data[(int)doffset] = data[off];
							off++; doffset++; written++;
						}
					} catch (Exception e) {}
					return written;
				}
			}
		}
		return 0;
	}
	
	@Override
	public synchronized int getTypeCount() {
		return map.size();
	}
	@Override
	public synchronized long getType(int index) {
		return map.keySet().toArray(new Long[0])[index];
	}
	@Override
	public synchronized long[] getTypes() {
		Long[] types = map.keySet().toArray(new Long[0]);
		long[] types2 = new long[types.length];
		for (int i = 0; i < types.length; i++) types2[i] = types[i];
		return types2;
	}
	
	@Override
	public synchronized int getResourceCount(long type) {
		if (map.containsKey(type)) {
			return map.get(type).size();
		}
		return 0;
	}
	@Override
	public synchronized int getID(long type, int index) {
		if (map.containsKey(type)) {
			return map.get(type).keySet().toArray(new Integer[0])[index];
		}
		return 0;
	}
	@Override
	public synchronized int[] getIDs(long type) {
		if (map.containsKey(type)) {
			Integer[] ids = map.get(type).keySet().toArray(new Integer[0]);
			int[] ids2 = new int[ids.length];
			for (int i = 0; i < ids.length; i++) ids2[i] = ids[i];
			return ids2;
		}
		return new int[0];
	}
	@Override
	public synchronized String getName(long type, int index) {
		if (map.containsKey(type)) {
			int id = map.get(type).keySet().toArray(new Integer[0])[index];
			return getNameFromID(type, id);
		}
		return "";
	}
	@Override
	public synchronized String[] getNames(long type) {
		if (map.containsKey(type)) {
			Integer[] ids = map.get(type).keySet().toArray(new Integer[0]);
			String[] names = new String[ids.length];
			for (int i = 0; i < ids.length; i++) names[i] = getNameFromID(type, ids[i]);
			return names;
		}
		return new String[0];
	}
	
	@Override
	public synchronized int getNextAvailableID(long type, int start) {
		if (map.containsKey(type)) {
			while (map.get(type).containsKey(start)) start++;
		}
		return start;
	}
	
	@Override
	public synchronized String getNameFromID(long type, int id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			return map.get(type).get(id).name;
		}
		return "";
	}
	
	@Override
	public synchronized int getIDFromName(long type, String name) {
		if (map.containsKey(type)) {
			for (DFFResource r : map.get(type).values()) {
				if (r.name.equals(name)) return r.id;
			}
		}
		return 0;
	}
	
	public synchronized void clear() {
		map.clear();
	}

	public synchronized boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public synchronized boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public synchronized Set<java.util.Map.Entry<Long, Map<Integer, DFFResource>>> entrySet() {
		return map.entrySet();
	}

	public synchronized Map<Integer, DFFResource> get(Object key) {
		return map.get(key);
	}

	public synchronized boolean isEmpty() {
		return map.isEmpty();
	}

	public synchronized Set<Long> keySet() {
		return map.keySet();
	}

	public synchronized Map<Integer, DFFResource> put(Long key, Map<Integer, DFFResource> value) {
		return map.put(key, value);
	}

	public synchronized void putAll(Map<? extends Long, ? extends Map<Integer, DFFResource>> t) {
		map.putAll(t);
	}

	public synchronized Map<Integer, DFFResource> remove(Object key) {
		return map.remove(key);
	}

	public synchronized int size() {
		return map.size();
	}

	public synchronized Collection<Map<Integer, DFFResource>> values() {
		return map.values();
	}
}
