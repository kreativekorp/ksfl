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

import java.util.List;
import java.util.Vector;
import java.util.Set;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * The <code>ResourceSearchPath</code> class maintains a list of <code>MacResourceProvider</code>s
 * that are searched in order whenever a request for a resource is made.
 * Search begins at the element specified by <code>setCurrentMacResourceProvider</code>.
 * Methods that write resources or reference resources by index operate solely
 * on the current MacResourceProvider. Methods that only read resources by name or ID
 * start by looking at the current MacResourceProvider, then keep looking at later elements
 * in the list until the resource is found or all elements have been searched.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class MacResourceSearchPath extends MacResourceProvider implements List<MacResourceProvider> {
	private Vector<MacResourceProvider> list = new Vector<MacResourceProvider>();
	private int start = 0;
	
	/**
	 * Adds a Provider to this list, in front of the current Provider.
	 * The Provider just added becomes the current Provider.
	 * @param dp the Provider to add to this list.
	 */
	public void pushProvider(MacResourceProvider dp) {
		list.add(start, dp);
	}
	
	/**
	 * Removes the current Provider from this list and returns it.
	 * The next Provider becomes the current Provider.
	 * @return the old current Provider.
	 */
	public MacResourceProvider popProvider() {
		return list.remove(start);
	}
	
	/**
	 * Returns the current Provider.
	 * @return the current Provider.
	 */
	public MacResourceProvider getCurrentProvider() {
		return list.get(start);
	}
	
	/**
	 * Returns the index of the current Provider in this list.
	 * @return the index of the current Provider in this list.
	 */
	public int getCurrentProviderIndex() {
		return start;
	}
	
	/**
	 * Sets the current Provider, which is the Provider write operations
	 * work on and searches start on. If the specified Provider is not
	 * in this list, nothing happens.
	 * @param dp the new current Provider.
	 */
	public void setCurrentProvider(MacResourceProvider dp) {
		if (contains(dp)) start = indexOf(dp);
	}
	
	/**
	 * Sets the current Provider to the Provider at the specified
	 * index in this list.
	 * @param index the index of the new current Provider.
	 */
	public void setCurrentProvider(int index) {
		start = index;
	}
	
	/**
	 * Sets the current Provider to the Provider at the specified
	 * index in this list.
	 * @param index the index of the new current Provider.
	 */
	public void setCurrentProviderIndex(int index) {
		start = index;
	}
	
	@Override
	public boolean isReadOnly() {
		return list.get(start).isReadOnly();
	}
	
	@Override
	public void flush() {
		list.get(start).flush();
	}
	
	@Override
	public void close() {
		list.remove(start).close();
	}
	
	@Override
	public short getResourceMapAttributes() {
		return list.get(start).getResourceMapAttributes();
	}
	
	@Override
	public void setResourceMapAttributes(short attr) {
		list.get(start).setResourceMapAttributes(attr);
	}
	
	@Override
	public boolean add(MacResource r) throws MacResourceAlreadyExistsException {
		return list.get(start).add(r);
	}
	
	@Override
	public boolean contains(int type, short id) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, id)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean contains(int type, String name) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, name)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public MacResource get(int type, short id) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, id)) {
				return list.get(i).get(type, id);
			}
		}
		return null;
	}
	@Override
	public MacResource get(int type, String name) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, name)) {
				return list.get(i).get(type, name);
			}
		}
		return null;
	}
	
	@Override
	public MacResource getAttributes(int type, short id) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, id)) {
				return list.get(i).getAttributes(type, id);
			}
		}
		return null;
	}
	@Override
	public MacResource getAttributes(int type, String name) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, name)) {
				return list.get(i).getAttributes(type, name);
			}
		}
		return null;
	}
	
	@Override
	public byte[] getData(int type, short id) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, id)) {
				return list.get(i).getData(type, id);
			}
		}
		return null;
	}
	@Override
	public byte[] getData(int type, String name) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, name)) {
				return list.get(i).getData(type, name);
			}
		}
		return null;
	}
	
	@Override
	public boolean remove(int type, short id) {
		return list.get(start).remove(type, id);
	}
	@Override
	public boolean remove(int type, String name) {
		return list.get(start).remove(type, name);
	}
	
	@Override
	public boolean set(int type, short id, MacResource r) throws MacResourceAlreadyExistsException {
		return list.get(start).set(type, id, r);
	}
	@Override
	public boolean set(int type, String name, MacResource r) throws MacResourceAlreadyExistsException {
		return list.get(start).set(type, name, r);
	}
	
	@Override
	public boolean setAttributes(int type, short id, MacResource r) throws MacResourceAlreadyExistsException {
		return list.get(start).setAttributes(type, id, r);
	}
	@Override
	public boolean setAttributes(int type, String name, MacResource r) throws MacResourceAlreadyExistsException {
		return list.get(start).setAttributes(type, name, r);
	}
	
	@Override
	public boolean setData(int type, short id, byte[] data) {
		return list.get(start).setData(type, id, data);
	}
	@Override
	public boolean setData(int type, String name, byte[] data) {
		return list.get(start).setData(type, name, data);
	}
	
	@Override
	public int getTypeCount() {
		Set<Integer> types = new TreeSet<Integer>();
		for (int i = start; i < list.size(); i++) {
			for (int type : list.get(i).getTypes()) {
				types.add(type);
			}
		}
		return types.size();
	}
	@Override
	public int getType(int index) {
		Set<Integer> types = new TreeSet<Integer>();
		for (int i = start; i < list.size(); i++) {
			for (int type : list.get(i).getTypes()) {
				types.add(type);
			}
		}
		int i = 0;
		for (Integer type : types) {
			if (i == index) return type;
			else i++;
		}
		return 0;
	}
	@Override
	public int[] getTypes() {
		Set<Integer> types = new TreeSet<Integer>();
		for (int i = start; i < list.size(); i++) {
			for (int type : list.get(i).getTypes()) {
				types.add(type);
			}
		}
		int[] types2 = new int[types.size()];
		int i = 0; for (Integer type : types) types2[i++] = type;
		return types2;
	}
	
	@Override
	public int getResourceCount(int type) {
		Set<Short> ids = new TreeSet<Short>();
		for (int i = start; i < list.size(); i++) {
			for (short id : list.get(i).getIDs(type)) {
				ids.add(id);
			}
		}
		return ids.size();
	}
	@Override
	public short getID(int type, int index) {
		Set<Short> ids = new TreeSet<Short>();
		for (int i = start; i < list.size(); i++) {
			for (short id : list.get(i).getIDs(type)) {
				ids.add(id);
			}
		}
		int i = 0;
		for (Short id : ids) {
			if (i == index) return id;
			else i++;
		}
		return 0;
	}
	@Override
	public short[] getIDs(int type) {
		Set<Short> ids = new TreeSet<Short>();
		for (int i = start; i < list.size(); i++) {
			for (short id : list.get(i).getIDs(type)) {
				ids.add(id);
			}
		}
		short[] ids2 = new short[ids.size()];
		int i = 0; for (Short id : ids) ids2[i++] = id;
		return ids2;
	}
	@Override
	public String getName(int type, int index) {
		Set<Short> ids = new TreeSet<Short>();
		for (int i = start; i < list.size(); i++) {
			for (short id : list.get(i).getIDs(type)) {
				ids.add(id);
			}
		}
		int i = 0;
		for (Short id : ids) {
			if (i == index) return getNameFromID(type, id);
			else i++;
		}
		return "";
	}
	@Override
	public String[] getNames(int type) {
		Set<Short> ids = new TreeSet<Short>();
		for (int i = start; i < list.size(); i++) {
			for (short id : list.get(i).getIDs(type)) {
				ids.add(id);
			}
		}
		String[] names = new String[ids.size()];
		int i = 0; for (Short id : ids) names[i++] = getNameFromID(type, id);
		return names;
	}
	
	@Override
	public short getNextAvailableID(int type, short start) {
		while (contains(type, start)) start++;
		return start;
	}
	
	@Override
	public String getNameFromID(int type, short id) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, id)) {
				return list.get(i).getNameFromID(type, id);
			}
		}
		return "";
	}
	
	@Override
	public short getIDFromName(int type, String name) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, name)) {
				return list.get(i).getIDFromName(type, name);
			}
		}
		return 0;
	}
	
	public boolean add(MacResourceProvider o) {
		return list.add(o);
	}

	public void add(int index, MacResourceProvider element) {
		list.add(index, element);
	}

	public boolean addAll(Collection<? extends MacResourceProvider> c) {
		return list.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends MacResourceProvider> c) {
		return list.addAll(index, c);
	}

	public void clear() {
		list.clear();
	}

	public boolean contains(Object elem) {
		return list.contains(elem);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	public MacResourceProvider get(int index) {
		return list.get(index);
	}

	public int indexOf(Object elem) {
		return list.indexOf(elem);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<MacResourceProvider> iterator() {
		return list.iterator();
	}

	public int lastIndexOf(Object elem) {
		return list.lastIndexOf(elem);
	}

	public ListIterator<MacResourceProvider> listIterator() {
		return list.listIterator();
	}

	public ListIterator<MacResourceProvider> listIterator(int index) {
		return list.listIterator(index);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public MacResourceProvider remove(int index) {
		return list.remove(index);
	}

	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	public MacResourceProvider set(int index, MacResourceProvider element) {
		return list.set(index, element);
	}

	public int size() {
		return list.size();
	}

	public List<MacResourceProvider> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}
}
