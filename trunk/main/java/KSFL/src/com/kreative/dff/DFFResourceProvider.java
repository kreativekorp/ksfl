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

package com.kreative.dff;

/**
 * A class extending the <code>DFFProvider</code> abstract class
 * represents any structure resembling a DFF file that can
 * provide <code>DFFResource</code> resources. It does not matter how
 * the resources are stored or read; that is up to the subclasses.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public abstract class DFFResourceProvider {
	/**
	 * The 'magic number' for DFF version 1.0 files,
	 * the four-character constant <code>%DFF</code>.
	 */
	public static final int MAGIC_NUMBER_DFF1 = 0x25444646;
	/**
	 * The 'magic number' for DFF version 2.0.1 files,
	 * the four-character constant <code>%DF2</code>.
	 */
	public static final int MAGIC_NUMBER_DFF2 = 0x25444632;
	/**
	 * The 'magic number' for DFF version 3.0 files,
	 * the four-character constant <code>%DF3</code>.
	 */
	public static final int MAGIC_NUMBER_DFF3 = 0x25444633;
	/**
	 * The 'magic number' for DFF version 1.0 files
	 * with byte order opposite what was expected,
	 * the four-character constant <code>FFD%</code>.
	 */
	public static final int MAGIC_NUMBER_DFF1R = 0x46464425;
	/**
	 * The 'magic number' for DFF version 2.0.1 files
	 * with byte order opposite what was expected,
	 * the four-character constant <code>2FD%</code>.
	 */
	public static final int MAGIC_NUMBER_DFF2R = 0x32464425;
	/**
	 * The 'magic number' for DFF version 3.0 files
	 * with byte order opposite what was expected,
	 * the four-character constant <code>3FD%</code>.
	 */
	public static final int MAGIC_NUMBER_DFF3R = 0x33464425;
	
	/**
	 * Determines if this <code>DFFResourceProvider</code> is capable
	 * of modifying resources, and if so, whether it is currently allowed to.
	 * If the resources are read-only, this returns true.
	 * If the resources can be modified, this returns false.
	 * @return true if the resources cannot be modified (read only), false if they can be modified.
	 */
	public abstract boolean isReadOnly();
	
	/**
	 * Performs any cleanup required by this DFFResourceProvider
	 * without closing any streams.
	 */
	public abstract void flush();
	
	/**
	 * Performs any cleanup required by this DFFResourceProvider
	 * and closes all open streams.
	 */
	public abstract void close();
	
	/**
	 * Adds a resource. If a resource of the same type and ID number
	 * already exists, throws a <code>DFFResourceAlreadyExistsException</code>.
	 * @param r a resource to add.
	 * @return true if the resource was successfully added, false otherwise.
	 * @throws DFFResourceAlreadyExistsException if a resource of the same type and ID number already exists.
	 */
	public abstract boolean add(DFFResource r) throws DFFResourceAlreadyExistsException;
	
	/**
	 * Determines if the specified resource exists.
	 * @param type the type of the resource.
	 * @param id the ID number of the resource.
	 * @return true if a resource of the specified type and ID number exists, false otherwise.
	 */
	public abstract boolean contains(long type, int id);
	/**
	 * Determines if the specified resource exists.
	 * @param type the type of the resource.
	 * @param name the name of the resource.
	 * @return true if a resource of the specified type and name exists, false otherwise.
	 */
	public abstract boolean contains(long type, String name);
	
	/**
	 * Retrieves a resource from this <code>DFFResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the resource.
	 * Modifying the returned object will not affect other objects
	 * using this <code>DFFResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param id the ID number of the resource to retrieve.
	 * @return the requested resource.
	 */
	public abstract DFFResource get(long type, int id);
	/**
	 * Retrieves a resource from this <code>DFFResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the resource.
	 * Modifying the returned object will not affect other objects
	 * using this <code>DFFResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param name the name of the resource to retrieve.
	 * @return the requested resource.
	 */
	public abstract DFFResource get(long type, String name);
	
	/**
	 * Retrieves the attributes of a resource from this <code>DFFResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the attributes of the resource.
	 * Modifying the returned object will not affect other objects
	 * using this <code>DFFResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param id the ID number of the resource to retrieve.
	 * @return the requested resource's attributes.
	 */
	public abstract DFFResource getAttributes(long type, int id);
	/**
	 * Retrieves the attributes of a resource from this <code>DFFResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the attributes of the resource.
	 * Modifying the returned object will not affect other objects
	 * using this <code>DFFResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param name the name of the resource to retrieve.
	 * @return the requested resource's attributes.
	 */
	public abstract DFFResource getAttributes(long type, String name);
	
	/**
	 * Returns the length of a resource's data.
	 * @param type the type of the resource.
	 * @param id the ID number of the resource.
	 * @return the length of the requested resource's data.
	 */
	public abstract long getLength(long type, int id);
	/**
	 * Returns the length of a resource's data.
	 * @param type the type of the resource.
	 * @param name the name of the resource.
	 * @return the length of the requested resource's data.
	 */
	public abstract long getLength(long type, String name);
	
	/**
	 * Retrieves the data of a resource from this <code>DFFResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the resource's data.
	 * Modifying the returned object will not affect other objects
	 * using this <code>DFFResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param id the ID number of the resource to retrieve.
	 * @return the requested resource's data.
	 */
	public abstract byte[] getData(long type, int id);
	/**
	 * Retrieves the data of a resource from this <code>DFFResourceProvider</code>.
	 * <br><br>
	 * This will always return a unique copy of the resource's data.
	 * Modifying the returned object will not affect other objects
	 * using this <code>DFFResourceProvider</code>.
	 * @param type the type of the resource to retrieve.
	 * @param name the name of the resource to retrieve.
	 * @return the requested resource's data.
	 */
	public abstract byte[] getData(long type, String name);
	
	/**
	 * Reads a section of a resource's data into a byte array. The number of bytes read is returned.
	 * Reading past the end of the resource's data will result in fewer bytes read than requested.
	 * @param type the type of the resource.
	 * @param id the ID number of the resource.
	 * @param doffset the offset into the resource's data at which to start reading.
	 * @param data the byte array to read into.
	 * @param off the offset into the byte array.
	 * @param len the number of bytes to read.
	 * @return the number of bytes actually read.
	 */
	public abstract int read(long type, int id, long doffset, byte[] data, int off, int len);
	/**
	 * Reads a section of a resource's data into a byte array. The number of bytes read is returned.
	 * Reading past the end of the resource's data will result in fewer bytes read than requested.
	 * @param type the type of the resource.
	 * @param name the name of the resource.
	 * @param doffset the offset into the resource's data at which to start reading.
	 * @param data the byte array to read into.
	 * @param off the offset into the byte array.
	 * @param len the number of bytes to read.
	 * @return the number of bytes actually read.
	 */
	public abstract int read(long type, String name, long doffset, byte[] data, int off, int len);
	
	/**
	 * Removes a resource.
	 * @param type the type of the resource to remove.
	 * @param id the ID number of the resource to remove.
	 * @return true if the resource was successfully removed, false otherwise.
	 */
	public abstract boolean remove(long type, int id);
	/**
	 * Removes a resource.
	 * @param type the type of the resource to remove.
	 * @param name the name of the resource to remove.
	 * @return true if the resource was successfully removed, false otherwise.
	 */
	public abstract boolean remove(long type, String name);
	
	/**
	 * Modifies a resource by replacing its attributes and data with the attributes and data of the specified <code>DFFResource</code>.
	 * @param type the type of the resource to modify.
	 * @param id the ID number of the resource to modify.
	 * @param r a <code>DFFResource</code> with the new attributes and data of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 */
	public abstract boolean set(long type, int id, DFFResource r) throws DFFResourceAlreadyExistsException;
	/**
	 * Modifies a resource by replacing its attributes and data with the attributes and data of the specified <code>DFFResource</code>.
	 * @param type the type of the resource to modify.
	 * @param name the name of the resource to modify.
	 * @param r a <code>DFFResource</code> with the new attributes and data of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 */
	public abstract boolean set(long type, String name, DFFResource r) throws DFFResourceAlreadyExistsException;
	
	/**
	 * Changes the properties (type, ID number, name, data type, and attributes) of a resource
	 * to those of the specified <code>DFFResource</code>.
	 * The resource's data is not affected.
	 * <br><br>
	 * If the type or ID number of the resource being modified does not match
	 * the type or ID number of the specified <code>DFFResource</code>, and
	 * a resource with the type and ID number of the specified <code>DFFResource</code>
	 * already exists, this will throw a <code>DFFResourceAlreadyExistsException</code>.
	 * @param type the type of the resource to modify.
	 * @param id the ID number of the resource to modify.
	 * @param r a <code>DFFResource</code> with the new properties of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 * @throws DFFResourceAlreadyExistsException if the old type or ID number does not match the new type or ID number, and a resource with the new type and ID number already exists.
	 */
	public abstract boolean setAttributes(long type, int id, DFFResource r) throws DFFResourceAlreadyExistsException;
	/**
	 * Changes the properties (type, ID number, name, data type, and attributes) of a resource
	 * to those of the specified <code>DFFResource</code>.
	 * The resource's data is not affected.
	 * <br><br>
	 * If the type or ID number of the resource being modified does not match
	 * the type or ID number of the specified <code>DFFResource</code>, and
	 * a resource with the type and ID number of the specified <code>DFFResource</code>
	 * already exists, this will throw a <code>DFFResourceAlreadyExistsException</code>.
	 * @param type the type of the resource to modify.
	 * @param name the name of the resource to modify.
	 * @param r a <code>DFFResource</code> with the new properties of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 * @throws DFFResourceAlreadyExistsException if the old type or ID number does not match the new type or ID number, and a resource with the new type and ID number already exists.
	 */
	public abstract boolean setAttributes(long type, String name, DFFResource r) throws DFFResourceAlreadyExistsException;
	
	/**
	 * Changes the length of a resource's data.
	 * If the new length is less than the current length, the data is truncated.
	 * Otherwise, the data is padded with zeros.
	 * @param type the type of the resource.
	 * @param id the ID number of the resource.
	 * @param len the new data length.
	 * @return true if the resource was modified successfully, false otherwise.
	 */
	public abstract boolean setLength(long type, int id, long len);
	/**
	 * Changes the length of a resource's data.
	 * If the new length is less than the current length, the data is truncated.
	 * Otherwise, the data is padded with zeros.
	 * @param type the type of the resource.
	 * @param name the name of the resource.
	 * @param len the new data length.
	 * @return true if the resource was modified successfully, false otherwise.
	 */
	public abstract boolean setLength(long type, String name, long len);
	
	/**
	 * Changes the data of a resource.
	 * @param type the type of the resource to modify.
	 * @param id the ID number of the resource to modify.
	 * @param data the new data of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 */
	public abstract boolean setData(long type, int id, byte[] data);
	/**
	 * Changes the data of a resource.
	 * @param type the type of the resource to modify.
	 * @param name the name of the resource to modify.
	 * @param data the new data of this resource.
	 * @return true if the resource was successfully modified, false otherwise.
	 */
	public abstract boolean setData(long type, String name, byte[] data);
	
	/**
	 * Writes a byte array into a resource's data. The number of bytes written is returned.
	 * Writing past the end of the resource's data will increase the size of the resource.
	 * @param type the type of the resource.
	 * @param id the ID number of the resource.
	 * @param doffset the offset into the resource's data at which to start writing.
	 * @param data the byte array to write.
	 * @param off the offset into the byte array.
	 * @param len the number of bytes to write.
	 * @return the number of bytes actually written.
	 */
	public abstract int write(long type, int id, long doffset, byte[] data, int off, int len);
	/**
	 * Writes a byte array into a resource's data. The number of bytes written is returned.
	 * Writing past the end of the resource's data will increase the size of the resource.
	 * @param type the type of the resource.
	 * @param name the name of the resource.
	 * @param doffset the offset into the resource's data at which to start writing.
	 * @param data the byte array to write.
	 * @param off the offset into the byte array.
	 * @param len the number of bytes to write.
	 * @return the number of bytes actually written.
	 */
	public abstract int write(long type, String name, long doffset, byte[] data, int off, int len);
	
	/**
	 * Returns the number of DFF types.
	 * @return the number of DFF types.
	 */
	public abstract int getTypeCount();
	/**
	 * Returns the <code>index</code>th DFF type.
	 * @param index the index of the DFF type.
	 * @return the DFF type.
	 */
	public abstract long getType(int index);
	/**
	 * Returns an array of all the DFF types.
	 * @return an array of all the DFF types.
	 */
	public abstract long[] getTypes();
	
	/**
	 * Returns the number of resources of the specified type.
	 * @param type the DFF type.
	 * @return the number of resources of that type.
	 */
	public abstract int getResourceCount(long type);
	/**
	 * Returns the ID number of the <code>index</code>th resource of the specified type.
	 * @param type the DFF type.
	 * @param index the index of the resource, starting with zero.
	 * @return the ID number of the resource.
	 */
	public abstract int getID(long type, int index);
	/**
	 * Returns an array of all the ID numbers of resources of the specified type.
	 * @param type the DFF type.
	 * @return an array of all the ID numbers of resources of that type.
	 */
	public abstract int[] getIDs(long type);
	/**
	 * Returns the name of the <code>index</code>th resource of the specified type.
	 * @param type the DFF type.
	 * @param index the index of the resource, starting with zero.
	 * @return the name of the resource.
	 */
	public abstract String getName(long type, int index);
	/**
	 * Returns an array of all the names of resources of the specified type.
	 * @param type the DFF type.
	 * @return an array of all the names of resources of that type.
	 */
	public abstract String[] getNames(long type);
	
	/**
	 * Returns an ID number that is not used by any resources of the specified type.
	 * The exact ID number returned is implementation-dependent, but is subject
	 * to the following three requirements:
	 * <ul>
	 * <li>There must not already exist a resource with the specified type and the returned "next available ID number."</li>
	 * <li>The "next available ID number" must not be negative.</li>
	 * <li>The "next available ID number" must not be more than one plus the largest ID number of resources of this type.</li>
	 * </ul>
	 * @param type the DFF type.
	 * @return an ID number not used by any pre-existing resources of this type.
	 */
	public final int getNextAvailableID(long type) {
		return getNextAvailableID(type, 0);
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
	 * @param type the DFF type.
	 * @param start the minimum acceptable ID number.
	 * @return an ID number not used by any pre-existing resources of this type.
	 */
	public abstract int getNextAvailableID(long type, int start);
	
	/**
	 * Retrieves a resource's name given its ID number.
	 * @param type the resource's type.
	 * @param id the resource's ID number.
	 * @return the resource's name.
	 */
	public abstract String getNameFromID(long type, int id);
	
	/**
	 * Retrieves a resource's ID number given its name.
	 * @param type the resource's type.
	 * @param name the resource's name.
	 * @return the resource's ID number.
	 */
	public abstract int getIDFromName(long type, String name);

	/**
	 * Copies all resources from this DFFResourceProvider to another DFFResourceProvider.
	 * @param rp the DFFResourceProvider to copy to.
	 * @throws DFFResourceAlreadyExistsException if two resources of the same type and ID exist in both DFFResourceProviders.
	 */
	public final DFFResourceProvider copyTo(DFFResourceProvider rp) throws DFFResourceAlreadyExistsException {
		for (long type : getTypes()) {
			for (int id : getIDs(type)) {
				rp.add(get(type,id));
			}
		}
		return rp;
	}
	
	/**
	 * Copies all resources from another DFFResourceProvider to this DFFResourceProvider.
	 * @param rp the DFFResourceProvider to copy from.
	 * @throws DFFResourceAlreadyExistsException if two resources of the same type and ID exist in both DFFResourceProviders.
	 */
	public final DFFResourceProvider copyFrom(DFFResourceProvider rp) throws DFFResourceAlreadyExistsException {
		for (long type : rp.getTypes()) {
			for (int id : rp.getIDs(type)) {
				add(rp.get(type,id));
			}
		}
		return this;
	}
}
