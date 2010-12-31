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

package com.kreative.prc;

import java.util.Arrays;
import com.kreative.ksfl.KSFLUtilities;

/**
 * The <code>PalmResource</code> class represents a resource as defined by
 * the Palm OS PRC format.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class PalmResource {
	/**
	 * Checks if a resource type is one this class knows how to handle.
	 * The default implementation is to always return true.
	 * It is recommended that subclasses override this.
	 * @param type A resource type to check.
	 * @return True if this class can handle this resource type, false otherwise.
	 */
	public static boolean isMyType(int type) {
		return true;
	}
	
	/**
	 * The resource type, which describes what kind of data
	 * is contained in the resource. Usually this is expressed
	 * as a four-character constant, with the first character
	 * representing the most significant byte and the last
	 * character representing the least significant byte. The
	 * encoding used is MacRoman.
	 */
	public int type;
	/**
	 * The resource ID number.
	 */
	public short id;
	/**
	 * The data contained in the resource.
	 */
	public byte[] data;
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public PalmResource(int type, short id, byte[] data) {
		this.type = type;
		this.id = id;
		this.data = data;
	}
	
	/**
	 * Creates a shallow copy of this resource.<p>
	 * This operation is not recommended, especially when modifying resources.
	 * Since the object returned is a shallow copy, modifying the <code>data</code>
	 * member of the shallow copy will modify the <code>data</code> member of the
	 * original, but replacing the <code>data</code> member with a different array
	 * of bytes or changing the type, ID, name, or attributes of the copy will
	 * not do the same to the original. Some methods of the subclasses of <code>PalmResource</code>
	 * will modify <code>data</code> itself, and some will replace <code>data</code>
	 * with another array. This will result in a terrible consistency problem.<p>
	 * The <code>deepCopy</code> method should be used instead of <code>shallowCopy</code>.
	 * @return A shallow copy of this resource.
	 */
	public PalmResource shallowCopy() {
		return new PalmResource(this.type, this.id, this.data);
	}
	
	/**
	 * Creates a deep copy of this resource.
	 * @return A deep copy of this resource.
	 */
	public PalmResource deepCopy() {
		return new PalmResource(this.type, this.id, KSFLUtilities.copy(this.data));
	}
	
	/**
	 * Casts this <code>PalmResource</code> object to a subclass of <code>PalmResource</code>
	 * by creating a shallow copy of this resource as an instance of that subclass.
	 * This is done by calling the <code>PalmResource(int type, short id,
	 * byte[] data)</code> constructor of the subclass with the type,
	 * ID, and data of this resource, effectively creating a shallow
	 * copy but as an instance of a subclass of <code>PalmResource</code>.<p>
	 * If some kind of error happens (the subclass does not have the right constructor,
	 * the constructor throws an exception, the passed class is not a subclass of
	 * <code>PalmResource</code>, or some other error), a shallow copy of class <code>PalmResource</code>
	 * is returned instead.<p>
	 * This operation is not recommended, especially when modifying resources.
	 * Since the object returned is a shallow copy, modifying the <code>data</code>
	 * member of the shallow copy will modify the <code>data</code> member of the
	 * original, but replacing the <code>data</code> member with a different array
	 * of bytes or changing the type, ID, name, or attributes of the copy will
	 * not do the same to the original. Some methods of the subclasses of <code>PalmResource</code>
	 * will modify <code>data</code> itself, and some will replace <code>data</code>
	 * with another array. This will result in a terrible consistency problem.<p>
	 * The <code>deepRecast</code> method or the <code>get</code> methods of
	 * <code>PRCProvider</code> should be used instead of <code>shallowRecast</code>.
	 * @param resourceSubclass A subclass of <code>PalmResource</code>.
	 * @return A shallow copy of this resource, an instance of class <code>resourceSubclass</code>.
	 */
	public <R extends PalmResource> R shallowRecast(Class<R> resourceSubclass) {
		Class<?>[] fparam = { int.class, short.class, byte[].class };
		Object[] aparam = { this.type, this.id, this.data };
		try {
			return resourceSubclass.getConstructor(fparam).newInstance(aparam);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Casts this <code>PalmResource</code> object to a subclass of <code>PalmResource</code>
	 * by creating a deep copy of this resource as an instance of that subclass.
	 * This is done by calling the <code>PalmResource(int type, short id,
	 * byte[] data)</code> constructor of the subclass with the type,
	 * ID, and a copy of the data of this resource, effectively creating
	 * a deep copy but as an instance of a subclass of <code>PalmResource</code>.<p>
	 * If some kind of error happens (the subclass does not have the right constructor,
	 * the constructor throws an exception, the passed class is not a subclass of
	 * <code>PalmResource</code>, or some other error), a deep copy of class <code>PalmResource</code>
	 * is returned instead.
	 * @param resourceSubclass A subclass of <code>PalmResource</code>.
	 * @return A deep copy of this resource, an instance of class <code>resourceSubclass</code>.
	 */
	public <R extends PalmResource> R deepRecast(Class<R> resourceSubclass) {
		Class<?>[] fparam = { int.class, short.class, byte[].class };
		Object[] aparam = { this.type, this.id, KSFLUtilities.copy(this.data) };
		try {
			return resourceSubclass.getConstructor(fparam).newInstance(aparam);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Compares this resource against another object to check if they are equal.
	 * Two resources are equal if their types, ID numbers, and data are equal.
	 * If <code>o</code> is not a <code>PalmResource</code> or a subclass of
	 * <code>PalmResource</code>, this returns false.
	 * @return True if <code>o</code> equals this resource, false otherwise.
	 */
	public boolean equals(Object o) {
		if (o instanceof PalmResource) {
			PalmResource other = (PalmResource)o;
			return this.type == other.type && this.id == other.id && Arrays.equals(this.data, other.data);
		} else {
			return false;
		}
	}
	
	/**
	 * Returns a hashcode for this resource.
	 * @return A hash code value for this object.
	 */
	public int hashCode() {
		return type ^ id ^ Arrays.hashCode(data);
	}
}
