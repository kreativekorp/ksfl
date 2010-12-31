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

package com.kreative.prc;

/**
 * A class extending the <code>PalmResourceProvider</code> abstract class
 * represents any structure resembling a Palm OS PRC file that can
 * provide <code>PalmResource</code> objects. It does not matter how
 * the resources are stored or read; that is up to the subclasses.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public abstract class PalmResourceProvider {
	/**
	 * Determines if this <code>PalmResourceProvider</code> is capable
	 * of modifying resources, and if so, whether it is currently allowed to.
	 * If the resources are read-only, this returns true.
	 * If the resources can be modified, this returns false.
	 * @return true if the resources cannot be modified (read only), false if they can be modified.
	 */
	public abstract boolean isReadOnly();
	
	/**
	 * Performs any cleanup required by this PalmResourceProvider
	 * without closing any streams.
	 */
	public abstract void flush();
	
	/**
	 * Performs any cleanup required by this PalmResourceProvider
	 * and closes all open streams.
	 */
	public abstract void close();
	
	/**
	 * Returns the PRC header. The header is defined as follows:
	 * <table>
	 * <tr><th>Offset</th><th>Name</th><th>Type</th></tr>
	 * <tr><td>0x00</td><td>Name</td><td>string</td></tr>
	 * <tr><td>0x20</td><td>Attributes</td><td>short</td></tr>
	 * <tr><td>0x22</td><td>Version</td><td>short</td></tr>
	 * <tr><td>0x24</td><td>Create Time</td><td>int</td></tr>
	 * <tr><td>0x28</td><td>Modify Time</td><td>int</td></tr>
	 * <tr><td>0x2C</td><td>Backup Time</td><td>int</td></tr>
	 * <tr><td>0x30</td><td>Modification Number</td><td>int</td></tr>
	 * <tr><td>0x34</td><td>App Info ID</td><td>int</td></tr>
	 * <tr><td>0x38</td><td>Sort Info ID</td><td>int</td></tr>
	 * <tr><td>0x3C</td><td>Type</td><td>int</td></tr>
	 * <tr><td>0x40</td><td>Creator</td><td>int</td></tr>
	 * <tr><td>0x44</td><td>ID Seed</td><td>int</td></tr>
	 * <tr><td>0x48</td><td>Next Record List</td><td>int</td></tr>
	 * <tr><td>0x4C</td><td>Number of Records</td><td>short</td></tr>
	 * </table>
	 * All fields are big-endian. Dates are in seconds since January 1, 1904.
	 * This method may or may not include the number of records field.
	 * @return the PRC header in a byte array.
	 */
	public abstract byte[] getPRCHeader();
	
	/**
	 * Changes the PRC header. The header is defined as follows:
	 * <table>
	 * <tr><th>Offset</th><th>Name</th><th>Type</th></tr>
	 * <tr><td>0x00</td><td>Name</td><td>string</td></tr>
	 * <tr><td>0x20</td><td>Attributes</td><td>short</td></tr>
	 * <tr><td>0x22</td><td>Version</td><td>short</td></tr>
	 * <tr><td>0x24</td><td>Create Time</td><td>int</td></tr>
	 * <tr><td>0x28</td><td>Modify Time</td><td>int</td></tr>
	 * <tr><td>0x2C</td><td>Backup Time</td><td>int</td></tr>
	 * <tr><td>0x30</td><td>Modification Number</td><td>int</td></tr>
	 * <tr><td>0x34</td><td>App Info ID</td><td>int</td></tr>
	 * <tr><td>0x38</td><td>Sort Info ID</td><td>int</td></tr>
	 * <tr><td>0x3C</td><td>Type</td><td>int</td></tr>
	 * <tr><td>0x40</td><td>Creator</td><td>int</td></tr>
	 * <tr><td>0x44</td><td>ID Seed</td><td>int</td></tr>
	 * <tr><td>0x48</td><td>Next Record List</td><td>int</td></tr>
	 * <tr><td>0x4C</td><td>Number of Records</td><td>short</td></tr>
	 * </table>
	 * All fields are big-endian. Dates are in seconds since January 1, 1904.
	 * This method will not change the number of records field.
	 * @param header the PRC header in a byte array.
	 */
	public abstract void setPRCHeader(byte[] header);
	
	/**
	 * Adds a resource. If a resource of the same type and ID number
	 * already exists, throws a <code>PalmResourceAlreadyExistsException</code>.
	 * @param r a resource to add.
	 * @return true if the resource was successfully added, false otherwise.
	 * @throws PalmResourceAlreadyExistsException if a resource of the same type and ID number already exists.
	 */
	public abstract boolean add(PalmResource r) throws PalmResourceAlreadyExistsException;
	
	/**
	 * Determines if the specified resource exists.
	 * @param type the type of the resource.
	 * @param id the ID number of the resource.
	 * @return true if a resource of the specified type and ID number exists, false otherwise.
	 */
	public abstract boolean contains(int type, short id);
	
	/**
	 * Retrieves a resource from this <code>PalmResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the resource.
	 * Modifying the returned object will not affect other objects
	 * using this <code>PalmResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param id the ID number of the resource to retrieve.
	 * @return the requested resource.
	 */
	public abstract PalmResource get(int type, short id);
	
	/**
	 * Retrieves the attributes of a resource from this <code>PalmResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the attributes of the resource.
	 * Modifying the returned object will not affect other objects
	 * using this <code>PalmResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param id the ID number of the resource to retrieve.
	 * @return the requested resource's attributes.
	 */
	public abstract PalmResource getAttributes(int type, short id);
	
	/**
	 * Retrieves the data of a resource from this <code>PalmResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the resource data.
	 * Modifying the returned object will not affect other objects
	 * using this <code>PalmResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param id the ID number of the resource to retrieve.
	 * @return the requested resource's data.
	 */
	public abstract byte[] getData(int type, short id);
	
	/**
	 * Removes a resource.
	 * @param type the type of the resource to remove.
	 * @param id the ID number of the resource to remove.
	 * @return true if the resource was successfully removed, false otherwise.
	 */
	public abstract boolean remove(int type, short id);
	
	/**
	 * Modifies a resource by replacing its attributes and data with the attributes and data of the specified <code>PalmResource</code>.
	 * @param type the type of the resource to modify.
	 * @param id the ID number of the resource to modify.
	 * @param r a <code>PalmResource</code> with the new attributes and data of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 */
	public abstract boolean set(int type, short id, PalmResource r) throws PalmResourceAlreadyExistsException;

	/**
	 * Changes the properties (type and ID number) of a resource
	 * to those of the specified <code>PalmResource</code> object.
	 * The resource's data is not affected.
	 * <br><br>
	 * If the type or ID number of the resource being modified does not match
	 * the type or ID number of the specified <code>PalmResource</code> object, and
	 * a resource with the type and ID number of the specified <code>PalmResource</code>
	 * object already exists, this will throw a <code>PalmResourceAlreadyExistsException</code>.
	 * @param type the type of the resource to modify.
	 * @param id the ID number of the resource to modify.
	 * @param r a <code>PalmResource</code> object with the new properties of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 * @throws PalmResourceAlreadyExistsException if the old type or ID number does not match the new type or ID number, and a resource with the new type and ID number already exists.
	 */
	public abstract boolean setAttributes(int type, short id, PalmResource r) throws PalmResourceAlreadyExistsException;
	
	/**
	 * Changes the data of a resource
	 * to those of the specified <code>PalmResource</code> object.
	 * @param type the type of the resource to modify.
	 * @param id the ID number of the resource to modify.
	 * @param data the new data of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 */
	public abstract boolean setData(int type, short id, byte[] data);
	
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
	 * Returns an ID number that is not used by any resources of the specified type.
	 * The exact ID number returned is implementation-dependent, but is subject
	 * to the following three requirements:
	 * <ul>
	 * <li>There must not already exist a resource with the specified type and the returned "next available ID number."</li>
	 * <li>The "next available ID number" must not be negative.</li>
	 * <li>The "next available ID number" must not be more than one plus the largest ID number of resources of this type.</li>
	 * </ul>
	 * @param type the resource type.
	 * @return an ID number not used by any pre-existing resources of this type.
	 */
	public final short getNextAvailableID(int type) {
		return getNextAvailableID(type, (short)0);
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
	 * Copies all resources from this PalmResourceProvider to another PalmResourceProvider.
	 * @param rp the PalmResourceProvider to copy to.
	 * @throws PalmResourceAlreadyExistsException if two resources of the same type and ID exist in both PalmResourceProviders.
	 */
	public final PalmResourceProvider copyTo(PalmResourceProvider rp) throws PalmResourceAlreadyExistsException {
		for (int type : getTypes()) {
			for (short id : getIDs(type)) {
				rp.add(get(type,id));
			}
		}
		rp.setPRCHeader(getPRCHeader());
		return rp;
	}
	
	/**
	 * Copies all resources from another PalmResourceProvider to this PalmResourceProvider.
	 * @param rp the PalmResourceProvider to copy from.
	 * @throws PalmResourceAlreadyExistsException if two resources of the same type and ID exist in both PalmResourceProviders.
	 */
	public final PalmResourceProvider copyFrom(PalmResourceProvider rp) throws PalmResourceAlreadyExistsException {
		for (int type : rp.getTypes()) {
			for (short id : rp.getIDs(type)) {
				add(rp.get(type,id));
			}
		}
		setPRCHeader(rp.getPRCHeader());
		return this;
	}
}
