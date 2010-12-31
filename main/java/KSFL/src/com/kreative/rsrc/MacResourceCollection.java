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

package com.kreative.rsrc;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import com.kreative.ksfl.KSFLUtilities;

/**
 * The <code>ResourceCollection</code> class represents a resource fork as defined by
 * the Mac OS Resource Manager in a <code>Map</code>. The Resource Manager was originally
 * designed and implemented by Bruce Horn for use in the original
 * Macintosh Operating System. It remains today as a feature unique
 * to the Mac OS. Other operating systems have the concept of
 * resources, but the Mac OS implementation remains unique.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class MacResourceCollection extends MacResourceProvider implements Map<Integer,Map<Short,MacResource>> {
	private Map<Integer,Map<Short,MacResource>> map;
	private short attr;
	
	/**
	 * Creates a new, empty ResourceCollection.
	 */
	public MacResourceCollection() {
		map = new HashMap<Integer,Map<Short,MacResource>>();
		attr = 0;
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
	public synchronized short getResourceMapAttributes() {
		return attr;
	}
	
	@Override
	public synchronized void setResourceMapAttributes(short attr) {
		this.attr = attr;
	}
	
	@Override
	public synchronized boolean add(MacResource r) throws MacResourceAlreadyExistsException {
		if (map.containsKey(r.type) && map.get(r.type).containsKey(r.id)) {
			throw new MacResourceAlreadyExistsException();
		} else {
			if (map.containsKey(r.type)) {
				map.get(r.type).put(r.id, r.deepCopy());
			} else {
				Map<Short,MacResource> nmap = new HashMap<Short,MacResource>();
				nmap.put(r.id, r.deepCopy());
				map.put(r.type, nmap);
			}
			return true;
		}
	}
	
	@Override
	public synchronized boolean contains(int type, short id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			return true;
		}
		return false;
	}
	@Override
	public synchronized boolean contains(int type, String name) {
		if (map.containsKey(type)) {
			for (MacResource r : map.get(type).values()) {
				if (r.name.equals(name)) return true;
			}
		}
		return false;
	}
	
	private MacResource find(int type, short id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			return map.get(type).get(id);
		}
		return null;
	}
	private MacResource find(int type, String name) {
		if (map.containsKey(type)) {
			for (MacResource r : map.get(type).values()) {
				if (r.name.equals(name)) return r;
			}
		}
		return null;
	}
	
	@Override
	public synchronized MacResource get(int type, short id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			return map.get(type).get(id).deepCopy();
		}
		return null;
	}
	@Override
	public synchronized MacResource get(int type, String name) {
		if (map.containsKey(type)) {
			for (MacResource r : map.get(type).values()) {
				if (r.name.equals(name)) return r.deepCopy();
			}
		}
		return null;
	}
	
	@Override
	public synchronized MacResource getAttributes(int type, short id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			MacResource r = map.get(type).get(id);
			return new MacResource(r.type, r.id, r.getAttributes(), r.name, new byte[0]);
		}
		return null;
	}
	@Override
	public synchronized MacResource getAttributes(int type, String name) {
		if (map.containsKey(type)) {
			for (MacResource r : map.get(type).values()) {
				if (r.name.equals(name)) {
					return new MacResource(r.type, r.id, r.getAttributes(), r.name, new byte[0]);
				}
			}
		}
		return null;
	}
	
	@Override
	public synchronized byte[] getData(int type, short id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			return KSFLUtilities.copy(map.get(type).get(id).data);
		}
		return null;
	}
	@Override
	public synchronized byte[] getData(int type, String name) {
		if (map.containsKey(type)) {
			for (MacResource r : map.get(type).values()) {
				if (r.name.equals(name)) return KSFLUtilities.copy(r.data);
			}
		}
		return null;
	}
	
	@Override
	public synchronized boolean remove(int type, short id) {
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
	public synchronized boolean remove(int type, String name) {
		if (map.containsKey(type)) {
			for (MacResource r : map.get(type).values()) {
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
	public synchronized boolean set(int type, short id, MacResource r) throws MacResourceAlreadyExistsException {
		if (!contains(type, id)) {
			return false;
		} else {
			MacResource o = find(type, id);
			if ((o.type != r.type || o.id != r.id) && contains(r.type, r.id)) {
				throw new MacResourceAlreadyExistsException();
			} else {
				return remove(type, id) && add(r);
			}
		}
	}
	@Override
	public synchronized boolean set(int type, String name, MacResource r) throws MacResourceAlreadyExistsException {
		if (!contains(type, name)) {
			return false;
		} else {
			MacResource o = find(type, name);
			if ((o.type != r.type || o.id != r.id) && contains(r.type, r.id)) {
				throw new MacResourceAlreadyExistsException();
			} else {
				return remove(type, name) && add(r);
			}
		}
	}
	
	@Override
	public synchronized boolean setAttributes(int type, short id, MacResource r) throws MacResourceAlreadyExistsException {
		if (!contains(type, id)) {
			return false;
		} else {
			MacResource o = find(type, id);
			if ((o.type != r.type || o.id != r.id) && contains(r.type, r.id)) {
				throw new MacResourceAlreadyExistsException();
			} else {
				r = r.deepCopy();
				r.data = o.data;
				return remove(type, id) && add(r);
			}
		}
	}
	@Override
	public synchronized boolean setAttributes(int type, String name, MacResource r) throws MacResourceAlreadyExistsException {
		if (!contains(type, name)) {
			return false;
		} else {
			MacResource o = find(type, name);
			if ((o.type != r.type || o.id != r.id) && contains(r.type, r.id)) {
				throw new MacResourceAlreadyExistsException();
			} else {
				r = r.deepCopy();
				r.data = o.data;
				return remove(type, name) && add(r);
			}
		}
	}
	
	@Override
	public synchronized boolean setData(int type, short id, byte[] data) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			map.get(type).get(id).data = KSFLUtilities.copy(data);
			return true;
		}
		return false;
	}
	@Override
	public synchronized boolean setData(int type, String name, byte[] data) {
		if (map.containsKey(type)) {
			for (MacResource r : map.get(type).values()) {
				if (r.name.equals(name)) {
					r.data = KSFLUtilities.copy(data);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public synchronized int getTypeCount() {
		return map.size();
	}
	@Override
	public synchronized int getType(int index) {
		return map.keySet().toArray(new Integer[0])[index];
	}
	@Override
	public synchronized int[] getTypes() {
		Integer[] types = map.keySet().toArray(new Integer[0]);
		int[] types2 = new int[types.length];
		for (int i = 0; i < types.length; i++) types2[i] = types[i];
		return types2;
	}
	
	@Override
	public synchronized int getResourceCount(int type) {
		if (map.containsKey(type)) {
			return map.get(type).size();
		}
		return 0;
	}
	@Override
	public synchronized short getID(int type, int index) {
		if (map.containsKey(type)) {
			return map.get(type).keySet().toArray(new Short[0])[index];
		}
		return 0;
	}
	@Override
	public synchronized short[] getIDs(int type) {
		if (map.containsKey(type)) {
			Short[] ids = map.get(type).keySet().toArray(new Short[0]);
			short[] ids2 = new short[ids.length];
			for (int i = 0; i < ids.length; i++) ids2[i] = ids[i];
			return ids2;
		} else {
			return new short[0];
		}
	}
	@Override
	public synchronized String getName(int type, int index) {
		if (map.containsKey(type)) {
			short id = map.get(type).keySet().toArray(new Short[0])[index];
			return getNameFromID(type, id);
		}
		return "";
	}
	@Override
	public synchronized String[] getNames(int type) {
		if (map.containsKey(type)) {
			Short[] ids = map.get(type).keySet().toArray(new Short[0]);
			String[] names = new String[ids.length];
			for (int i = 0; i < ids.length; i++) names[i] = getNameFromID(type, ids[i]);
			return names;
		}
		return new String[0];
	}
	
	@Override
	public synchronized short getNextAvailableID(int type, short start) {
		if (map.containsKey(type)) {
			while (map.get(type).containsKey(start)) start++;
		}
		return start;
	}
	
	@Override
	public synchronized String getNameFromID(int type, short id) {
		if (map.containsKey(type) && map.get(type).containsKey(id)) {
			return map.get(type).get(id).name;
		}
		return "";
	}
	
	@Override
	public synchronized short getIDFromName(int type, String name) {
		if (map.containsKey(type)) {
			for (MacResource r : map.get(type).values()) {
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

	public synchronized Set<java.util.Map.Entry<Integer, Map<Short, MacResource>>> entrySet() {
		return map.entrySet();
	}

	public synchronized Map<Short, MacResource> get(Object key) {
		return map.get(key);
	}

	public synchronized boolean isEmpty() {
		return map.isEmpty();
	}

	public synchronized Set<Integer> keySet() {
		return map.keySet();
	}

	public synchronized Map<Short, MacResource> put(Integer key, Map<Short, MacResource> value) {
		return map.put(key, value);
	}

	public synchronized void putAll(Map<? extends Integer, ? extends Map<Short, MacResource>> t) {
		map.putAll(t);
	}

	public synchronized Map<Short, MacResource> remove(Object key) {
		return map.remove(key);
	}

	public synchronized int size() {
		return map.size();
	}

	public synchronized Collection<Map<Short, MacResource>> values() {
		return map.values();
	}
}
