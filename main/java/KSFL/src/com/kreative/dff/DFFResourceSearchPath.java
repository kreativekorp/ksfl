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
import java.util.Vector;
import java.util.Set;
import java.util.TreeSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * The <code>DFFSearchPath</code> class maintains a list of <code>DFFResourceProvider</code>s
 * that are searched in order whenever a request for a DFF object is made.
 * Search begins at the element specified by <code>setCurrentDFFResourceProvider</code>.
 * Methods that write DFF objects or reference DFF objects by index operate solely
 * on the current DFFResourceProvider. Methods that only read DFF objects by name or ID
 * start by looking at the current DFFResourceProvider, then keep looking at later elements
 * in the list until the DFF object is found or all elements have been searched.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class DFFResourceSearchPath extends DFFResourceProvider implements List<DFFResourceProvider> {
	private Vector<DFFResourceProvider> list = new Vector<DFFResourceProvider>();
	private int start = 0;
	
	/**
	 * Adds a Provider to this list, in front of the current Provider.
	 * The Provider just added becomes the current Provider.
	 * @param dp the Provider to add to this list.
	 */
	public void pushProvider(DFFResourceProvider dp) {
		list.add(start, dp);
	}
	
	/**
	 * Removes the current Provider from this list and returns it.
	 * The next Provider becomes the current Provider.
	 * @return the old current Provider.
	 */
	public DFFResourceProvider popProvider() {
		return list.remove(start);
	}
	
	/**
	 * Returns the current Provider.
	 * @return the current Provider.
	 */
	public DFFResourceProvider getCurrentProvider() {
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
	public void setCurrentProvider(DFFResourceProvider dp) {
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
	public boolean add(DFFResource r) throws DFFResourceAlreadyExistsException {
		return list.get(start).add(r);
	}
	
	@Override
	public boolean contains(long type, int id) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, id)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean contains(long type, String name) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, name)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public DFFResource get(long type, int id) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, id)) {
				return list.get(i).get(type, id);
			}
		}
		return null;
	}
	@Override
	public DFFResource get(long type, String name) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, name)) {
				return list.get(i).get(type, name);
			}
		}
		return null;
	}
	
	@Override
	public DFFResource getAttributes(long type, int id) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, id)) {
				return list.get(i).getAttributes(type, id);
			}
		}
		return null;
	}
	@Override
	public DFFResource getAttributes(long type, String name) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, name)) {
				return list.get(i).getAttributes(type, name);
			}
		}
		return null;
	}
	
	@Override
	public long getLength(long type, int id) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, id)) {
				return list.get(i).getLength(type, id);
			}
		}
		return 0;
	}
	@Override
	public long getLength(long type, String name) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, name)) {
				return list.get(i).getLength(type, name);
			}
		}
		return 0;
	}
	
	@Override
	public byte[] getData(long type, int id) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, id)) {
				return list.get(i).getData(type, id);
			}
		}
		return null;
	}
	@Override
	public byte[] getData(long type, String name) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, name)) {
				return list.get(i).getData(type, name);
			}
		}
		return null;
	}
	
	@Override
	public int read(long type, int id, long doffset, byte[] data, int off, int len) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, id)) {
				return list.get(i).read(type, id, doffset, data, off, len);
			}
		}
		return 0;
	}
	@Override
	public int read(long type, String name, long doffset, byte[] data, int off, int len) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, name)) {
				return list.get(i).read(type, name, doffset, data, off, len);
			}
		}
		return 0;
	}
	
	@Override
	public boolean remove(long type, int id) {
		return list.get(start).remove(type, id);
	}
	@Override
	public boolean remove(long type, String name) {
		return list.get(start).remove(type, name);
	}
	
	@Override
	public boolean set(long type, int id, DFFResource r) throws DFFResourceAlreadyExistsException {
		return list.get(start).set(type, id, r);
	}
	@Override
	public boolean set(long type, String name, DFFResource r) throws DFFResourceAlreadyExistsException {
		return list.get(start).set(type, name, r);
	}
	
	@Override
	public boolean setAttributes(long type, int id, DFFResource r) throws DFFResourceAlreadyExistsException {
		return list.get(start).setAttributes(type, id, r);
	}
	@Override
	public boolean setAttributes(long type, String name, DFFResource r) throws DFFResourceAlreadyExistsException {
		return list.get(start).setAttributes(type, name, r);
	}
	
	@Override
	public boolean setLength(long type, int id, long len) {
		return list.get(start).setLength(type, id, len);
	}
	@Override
	public boolean setLength(long type, String name, long len) {
		return list.get(start).setLength(type, name, len);
	}
	
	@Override
	public boolean setData(long type, int id, byte[] data) {
		return list.get(start).setData(type, id, data);
	}
	@Override
	public boolean setData(long type, String name, byte[] data) {
		return list.get(start).setData(type, name, data);
	}
	
	@Override
	public int write(long type, int id, long doffset, byte[] data, int off, int len) {
		return list.get(start).write(type, id, doffset, data, off, len);
	}
	@Override
	public int write(long type, String name, long doffset, byte[] data, int off, int len) {
		return list.get(start).write(type, name, doffset, data, off, len);
	}
	
	@Override
	public int getTypeCount() {
		Set<Long> types = new TreeSet<Long>();
		for (int i = start; i < list.size(); i++) {
			for (long type : list.get(i).getTypes()) {
				types.add(type);
			}
		}
		return types.size();
	}
	@Override
	public long getType(int index) {
		Set<Long> types = new TreeSet<Long>();
		for (int i = start; i < list.size(); i++) {
			for (long type : list.get(i).getTypes()) {
				types.add(type);
			}
		}
		int i = 0;
		for (Long type : types) {
			if (i == index) return type;
			else i++;
		}
		return 0;
	}
	@Override
	public long[] getTypes() {
		Set<Long> types = new TreeSet<Long>();
		for (int i = start; i < list.size(); i++) {
			for (long type : list.get(i).getTypes()) {
				types.add(type);
			}
		}
		long[] types2 = new long[types.size()];
		int i = 0; for (Long type : types) types2[i++] = type;
		return types2;
	}
	
	@Override
	public int getResourceCount(long type) {
		Set<Integer> ids = new TreeSet<Integer>();
		for (int i = start; i < list.size(); i++) {
			for (int id : list.get(i).getIDs(type)) {
				ids.add(id);
			}
		}
		return ids.size();
	}
	@Override
	public int getID(long type, int index) {
		Set<Integer> ids = new TreeSet<Integer>();
		for (int i = start; i < list.size(); i++) {
			for (int id : list.get(i).getIDs(type)) {
				ids.add(id);
			}
		}
		int i = 0;
		for (Integer id : ids) {
			if (i == index) return id;
			else i++;
		}
		return 0;
	}
	@Override
	public int[] getIDs(long type) {
		Set<Integer> ids = new TreeSet<Integer>();
		for (int i = start; i < list.size(); i++) {
			for (int id : list.get(i).getIDs(type)) {
				ids.add(id);
			}
		}
		int[] ids2 = new int[ids.size()];
		int i = 0; for (Integer id : ids) ids2[i++] = id;
		return ids2;
	}
	@Override
	public String getName(long type, int index) {
		Set<Integer> ids = new TreeSet<Integer>();
		for (int i = start; i < list.size(); i++) {
			for (int id : list.get(i).getIDs(type)) {
				ids.add(id);
			}
		}
		int i = 0;
		for (Integer id : ids) {
			if (i == index) return getNameFromID(type, id);
			else i++;
		}
		return "";
	}
	@Override
	public String[] getNames(long type) {
		Set<Integer> ids = new TreeSet<Integer>();
		for (int i = start; i < list.size(); i++) {
			for (int id : list.get(i).getIDs(type)) {
				ids.add(id);
			}
		}
		String[] names = new String[ids.size()];
		int i = 0; for (Integer id : ids) names[i++] = getNameFromID(type, id);
		return names;
	}
	
	@Override
	public int getNextAvailableID(long type, int start) {
		while (contains(type, start)) start++;
		return start;
	}
	
	@Override
	public String getNameFromID(long type, int id) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, id)) {
				return list.get(i).getNameFromID(type, id);
			}
		}
		return "";
	}
	
	@Override
	public int getIDFromName(long type, String name) {
		for (int i = start; i < list.size(); i++) {
			if (list.get(i).contains(type, name)) {
				return list.get(i).getIDFromName(type, name);
			}
		}
		return 0;
	}
	
	public boolean add(DFFResourceProvider elem) {
		return list.add(elem);
	}

	public void add(int index, DFFResourceProvider elem) {
		list.add(index, elem);
	}

	public boolean addAll(Collection<? extends DFFResourceProvider> c) {
		return list.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends DFFResourceProvider> c) {
		return list.addAll(index, c);
	}

	public void clear() {
		list.clear();
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	public DFFResourceProvider get(int index) {
		return list.get(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<DFFResourceProvider> iterator() {
		return list.iterator();
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<DFFResourceProvider> listIterator() {
		return list.listIterator();
	}

	public ListIterator<DFFResourceProvider> listIterator(int index) {
		return list.listIterator(index);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public DFFResourceProvider remove(int index) {
		return list.remove(index);
	}

	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	public DFFResourceProvider set(int index, DFFResourceProvider element) {
		return list.set(index, element);
	}

	public int size() {
		return list.size();
	}

	public List<DFFResourceProvider> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}
}
