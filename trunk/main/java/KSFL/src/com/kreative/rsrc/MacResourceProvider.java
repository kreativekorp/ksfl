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
 * other provisions required by the LGPL License. If you do not remove
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.rsrc;

/**
 * A class extending the <code>MacResourceProvider</code> abstract class
 * represents any structure resembling a Mac OS resource fork that can
 * provide <code>MacResource</code> objects. It does not matter how
 * the resources are stored or read; that is up to the subclasses.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public abstract class MacResourceProvider {
	/**
	 * The changed bit of the resource map attribute field, indicating the map has been modified.
	 * <p>
	 * The provided subclasses of <code>MacResourceProvider</code> do not affect this bit.
	 */
	public static final short RESOURCE_MAP_ATTRIBUTES_CHANGED = 0x20;
	/**
	 * The needs compression bit of the resource map attribute field, indicating
	 * that the map needs to be reorganized to remove empty space.
	 * <p>
	 * The provided subclasses of <code>MacResourceProvider</code> keep the resource map
	 * compressed automatically and do not affect this bit.
	 */
	public static final short RESOURCE_MAP_ATTRIBUTES_NEEDS_COMPRESSION = 0x40;
	/**
	 * The read only bit of the resource map attribute field, indicating that the
	 * resource fork should not be modified.
	 * <p>
	 * The provided subclasses of <code>MacResourceProvider</code> ignore this bit.
	 */
	public static final short RESOURCE_MAP_ATTRIBUTES_READ_ONLY = 0x80;

	/**
	 * Determines if this <code>MacResourceProvider</code> is capable
	 * of modifying resources, and if so, whether it is currently allowed to.
	 * If the resources are read-only, this returns true.
	 * If the resources can be modified, this returns false.
	 * @return true if the resources cannot be modified (read only), false if they can be modified.
	 */
	public abstract boolean isReadOnly();
	
	/**
	 * Performs any cleanup required by this MacResourceProvider
	 * without closing any streams.
	 */
	public abstract void flush();
	
	/**
	 * Performs any cleanup required by this MacResourceProvider
	 * and closes all open streams.
	 */
	public abstract void close();
	
	/**
	 * Returns the attributes of the resource map as a short integer.
	 * @return the attributes of the resource map as a short integer.
	 */
	public abstract short getResourceMapAttributes();
	
	/**
	 * Changes the attributes of the resource map.
	 * @param attr the new attributes.
	 */
	public abstract void setResourceMapAttributes(short attr);
	
	/**
	 * Adds a resource. If a resource of the same type and ID number
	 * already exists, throws a <code>MacResourceAlreadyExistsException</code>.
	 * @param r a resource to add.
	 * @return true if the resource was successfully added, false otherwise.
	 * @throws MacResourceAlreadyExistsException if a resource of the same type and ID number already exists.
	 */
	public abstract boolean add(MacResource r) throws MacResourceAlreadyExistsException;
	
	/**
	 * Determines if the specified resource exists.
	 * @param type the type of the resource.
	 * @param id the ID number of the resource.
	 * @return true if a resource of the specified type and ID number exists, false otherwise.
	 */
	public abstract boolean contains(int type, short id);
	/**
	 * Determines if the specified resource exists.
	 * @param type the type of the resource.
	 * @param name the name of the resource.
	 * @return true if a resource of the specified type and name exists, false otherwise.
	 */
	public abstract boolean contains(int type, String name);
	
	/**
	 * Retrieves a resource from this <code>MacResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the resource.
	 * Modifying the returned object will not affect other objects
	 * using this <code>MacResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param id the ID number of the resource to retrieve.
	 * @return the requested resource.
	 */
	public abstract MacResource get(int type, short id);
	/**
	 * Retrieves a resource from this <code>MacResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the resource.
	 * Modifying the returned object will not affect other objects
	 * using this <code>MacResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param name the name of the resource to retrieve.
	 * @return the requested resource.
	 */
	public abstract MacResource get(int type, String name);
	
	/**
	 * Retrieves the attributes of a resource from this <code>MacResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the attributes of the resource.
	 * Modifying the returned object will not affect other objects
	 * using this <code>MacResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param id the ID number of the resource to retrieve.
	 * @return the requested resource's attributes.
	 */
	public abstract MacResource getAttributes(int type, short id);
	/**
	 * Retrieves the attributes of a resource from this <code>MacResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the attributes of the resource.
	 * Modifying the returned object will not affect other objects
	 * using this <code>MacResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param name the name of the resource to retrieve.
	 * @return the requested resource's attributes.
	 */
	public abstract MacResource getAttributes(int type, String name);
	
	/**
	 * Retrieves the data of a resource from this <code>MacResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the resource's data.
	 * Modifying the returned object will not affect other objects
	 * using this <code>MacResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param id the ID number of the resource to retrieve.
	 * @return the requested resource's data.
	 */
	public abstract byte[] getData(int type, short id);
	/**
	 * Retrieves the data of a resource from this <code>MacResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the resource's data.
	 * Modifying the returned object will not affect other objects
	 * using this <code>MacResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param name the name of the resource to retrieve.
	 * @return the requested resource's data.
	 */
	public abstract byte[] getData(int type, String name);
	
	/**
	 * Removes a resource.
	 * @param type the type of the resource to remove.
	 * @param id the ID number of the resource to remove.
	 * @return true if the resource was successfully removed, false otherwise.
	 */
	public abstract boolean remove(int type, short id);
	/**
	 * Removes a resource.
	 * @param type the type of the resource to remove.
	 * @param name the name of the resource to remove.
	 * @return true if the resource was successfully removed, false otherwise.
	 */
	public abstract boolean remove(int type, String name);
	
	/**
	 * Modifies a resource by replacing its attributes and data with the attributes and data of the specified <code>MacResource</code>.
	 * @param type the type of the resource to modify.
	 * @param id the ID number of the resource to modify.
	 * @param r a <code>MacResource</code> with the new attributes and data of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 */
	public abstract boolean set(int type, short id, MacResource r) throws MacResourceAlreadyExistsException;
	/**
	 * Modifies a resource by replacing its attributes and data with the attributes and data of the specified <code>MacResource</code>.
	 * @param type the type of the resource to modify.
	 * @param name the name of the resource to modify.
	 * @param r a <code>MacResource</code> with the new attributes and data of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 */
	public abstract boolean set(int type, String name, MacResource r) throws MacResourceAlreadyExistsException;
	
	/**
	 * Changes the properties (type, ID number, name, and attributes) of a resource
	 * to those of the specified <code>MacResource</code> object.
	 * The resource's data is not affected.
	 * <br><br>
	 * If the type or ID number of the resource being modified does not match
	 * the type or ID number of the specified <code>MacResource</code> object, and
	 * a resource with the type and ID number of the specified <code>MacResource</code>
	 * object already exists, this will throw a <code>MacResourceAlreadyExistsException</code>.
	 * @param type the type of the resource to modify.
	 * @param id the ID number of the resource to modify.
	 * @param r a <code>MacResource</code> object with the new properties of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 * @throws MacResourceAlreadyExistsException if the old type or ID number does not match the new type or ID number, and a resource with the new type and ID number already exists.
	 */
	public abstract boolean setAttributes(int type, short id, MacResource r) throws MacResourceAlreadyExistsException;
	/**
	 * Changes the properties (type, ID number, name, and attributes) of a resource
	 * to those of the specified <code>MacResource</code> object.
	 * The resource's data is not affected.
	 * <br><br>
	 * If the type or ID number of the resource being modified does not match
	 * the type or ID number of the specified <code>MacResource</code> object, and
	 * a resource with the type and ID number of the specified <code>MacResource</code>
	 * object already exists, this will throw a <code>MacResourceAlreadyExistsException</code>.
	 * @param type the type of the resource to modify.
	 * @param name the name of the resource to modify.
	 * @param r a <code>MacResource</code> object with the new properties of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 * @throws MacResourceAlreadyExistsException if the old type or ID number does not match the new type or ID number, and a resource with the new type and ID number already exists.
	 */
	public abstract boolean setAttributes(int type, String name, MacResource r) throws MacResourceAlreadyExistsException;
	
	/**
	 * Changes the data of a resource
	 * to those of the specified <code>MacResource</code> object.
	 * @param type the type of the resource to modify.
	 * @param id the ID number of the resource to modify.
	 * @param data the new data of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 */
	public abstract boolean setData(int type, short id, byte[] data);
	/**
	 * Changes the data of a resource
	 * to those of the specified <code>MacResource</code> object.
	 * @param type the type of the resource to modify.
	 * @param name the name of the resource to modify.
	 * @param data the new data of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 */
	public abstract boolean setData(int type, String name, byte[] data);
	
	/**
	 * Returns the number of resource types.
	 * @return the number of resource types.
	 */
	public abstract int getTypeCount();
	/**
	 * Returns the <code>index</code>th resource type.
	 * @param index the index of the resource type.
	 * @return the resource type.
	 */
	public abstract int getType(int index);
	/**
	 * Returns an array of all the resource types.
	 * @return an array of all the resource types.
	 */
	public abstract int[] getTypes();
	
	/**
	 * Returns the number of resources of the specified type.
	 * @param type the resource type.
	 * @return the number of resources of that type.
	 */
	public abstract int getResourceCount(int type);
	/**
	 * Returns the ID number of the <code>index</code>th resource of the specified type.
	 * @param type the resource type.
	 * @param index the index of the resource, starting with zero.
	 * @return the ID number of the resource.
	 */
	public abstract short getID(int type, int index);
	/**
	 * Returns an array of all the ID numbers of resources of the specified type.
	 * @param type the resource type.
	 * @return an array of all the ID numbers of resources of that type.
	 */
	public abstract short[] getIDs(int type);
	/**
	 * Returns the name of the <code>index</code>th resource of the specified type.
	 * @param type the resource type.
	 * @param index the index of the resource, starting with zero.
	 * @return the name of the resource.
	 */
	public abstract String getName(int type, int index);
	/**
	 * Returns an array of all the names of resources of the specified type.
	 * @param type the resource type.
	 * @return an array of all the names of resources of that type.
	 */
	public abstract String[] getNames(int type);
	
	/**
	 * Returns an ID number that is not used by any resources of the specified type.
	 * The exact ID number returned is implementation-dependent, but is subject
	 * to the following three requirements:
	 * <ul>
	 * <li>There must not already exist a resource with the specified type and the returned "next available ID number."</li>
	 * <li>The "next available ID number" must not be less than 128.</li>
	 * <li>The "next available ID number" must not be more than one plus the largest ID number of resources of this type.</li>
	 * </ul>
	 * @param type the resource type.
	 * @return an ID number not used by any pre-existing resources of this type.
	 */
	public final short getNextAvailableID(int type) {
		return getNextAvailableID(type, (short)128);
	}
	/**
	 * Returns an ID number that is not used by any resources of the specified type.
	 * The exact ID number returned is implementation-dependent, but is subject
	 * to the following three requirements:
	 * <ul>
	 * <li>There must not already exist a resource with the specified type and the returned "next available ID number."</li>
	 * <li>The "next available ID number" must not be less than <code>start</code>.</li>
	 * <li>The "next available ID number" must not be more than one plus the largest ID number of resources of this type.</li>
	 * </ul>
	 * @param type the resource type.
	 * @param start the minimum acceptable ID number.
	 * @return an ID number not used by any pre-existing resources of this type.
	 */
	public abstract short getNextAvailableID(int type, short start);
	
	/**
	 * Retrieves a resource's name given its ID number.
	 * @param type the resource's type.
	 * @param id the resource's ID number.
	 * @return the resource's name.
	 */
	public abstract String getNameFromID(int type, short id);
	
	/**
	 * Retrieves a resource's ID number given its name.
	 * @param type the resource's type.
	 * @param name the resource's name.
	 * @return the resource's ID number.
	 */
	public abstract short getIDFromName(int type, String name);
	
	/**
	 * Copies all resources from this MacResourceProvider to another MacResourceProvider.
	 * @param rp the MacResourceProvider to copy to.
	 * @throws MacResourceAlreadyExistsException if two resources of the same type and ID exist in both MacResourceProviders.
	 */
	public final MacResourceProvider copyTo(MacResourceProvider rp) throws MacResourceAlreadyExistsException {
		for (int type : getTypes()) {
			for (short id : getIDs(type)) {
				rp.add(get(type,id));
			}
		}
		rp.setResourceMapAttributes(getResourceMapAttributes());
		return rp;
	}
	
	/**
	 * Copies all resources from another MacResourceProvider to this MacResourceProvider.
	 * @param rp the MacResourceProvider to copy from.
	 * @throws MacResourceAlreadyExistsException if two resources of the same type and ID exist in both MacResourceProviders.
	 */
	public final MacResourceProvider copyFrom(MacResourceProvider rp) throws MacResourceAlreadyExistsException {
		for (int type : rp.getTypes()) {
			for (short id : rp.getIDs(type)) {
				add(rp.get(type,id));
			}
		}
		setResourceMapAttributes(rp.getResourceMapAttributes());
		return this;
	}
}
