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

import java.util.Arrays;
import com.kreative.ksfl.KSFLUtilities;

/**
 * The <code>MacResource</code> class represents a resource as defined by
 * the Mac OS Resource Manager. The Resource Manager was originally
 * designed and implemented by Bruce Horn for use in the original
 * Macintosh Operating System. It remains today as a feature unique
 * to the Mac OS. Other operating systems have the concept of
 * resources, but the Mac OS implementation remains unique.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class MacResource {
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
	 * The resource name.
	 */
	public String name;
	/**
	 * The compressed attribute, indicating that the resource
	 * is compressed.
	 */
	public boolean compressed;
	/**
	 * The changed attribute, indicating that the resource
	 * has been changed and needs to be written back to the
	 * resource file. This attribute is never set in the
	 * file stored on disk.
	 */
	public boolean changed;
	/**
	 * The preload attribute, indicating that the resource
	 * should be loaded into memory as soon as the resource
	 * file is opened.
	 */
	public boolean preload;
	/**
	 * The protected attribute, indicating that the resource
	 * should not be overwritten. This is called <code>protect</code>
	 * here because <code>protected</code> is a Java keyword.
	 */
	public boolean protect;
	/**
	 * The locked attribute, indicating that the resource
	 * should not be moved in memory. If both preload and
	 * locked are set, the resource is loaded as low in the
	 * heap as possible.
	 */
	public boolean locked;
	/**
	 * The purgeable attribute, indicating that the resource
	 * can be freed from memory if more memory is needed.
	 */
	public boolean purgeable;
	/**
	 * The sysheap attribute, indicating that the resource
	 * should be loaded in the system heap instead of the
	 * application heap.
	 */
	public boolean sysheap;
	/**
	 * An attribute bit that is reserved for future use.
	 * It is unlikely anything will use this bit, but
	 * it is provided for completeness.
	 */
	public boolean reserved;
	/**
	 * The data contained in the resource.
	 */
	public byte[] data;
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public MacResource(int type, short id, byte[] data) {
		this.type = type;
		this.id = id;
		this.name = "";
		this.data = data;
		compressed = false;
		changed = false;
		preload = false;
		protect = false;
		locked = false;
		purgeable = false;
		sysheap = false;
		reserved = false;
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, name, and data.
	 * All attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public MacResource(int type, short id, String name, byte[] data) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.data = data;
		compressed = false;
		changed = false;
		preload = false;
		protect = false;
		locked = false;
		purgeable = false;
		sysheap = false;
		reserved = false;
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public MacResource(int type, short id, byte attr, byte[] data) {
		this.type = type;
		this.id = id;
		this.name = "";
		this.data = data;
		setAttributes(attr);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, attributes, name, and data.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public MacResource(int type, short id, byte attr, String name, byte[] data) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.data = data;
		setAttributes(attr);
	}
	
	/**
	 * The owner type indicating that the resource
	 * is owned by a <code>DRVR</code> resource,
	 * a driver or desk accessory.
	 */
	public static final int OWNER_TYPE_DRVR = 24;
	/**
	 * The owner type indicating that the resource
	 * is owned by a <code>WDEF</code> resource,
	 * a window definition.
	 */
	public static final int OWNER_TYPE_WDEF = 25;
	/**
	 * The owner type indicating that the resource
	 * is owned by an <code>MDEF</code> resource,
	 * a menu definition.
	 */
	public static final int OWNER_TYPE_MDEF = 26;
	/**
	 * The owner type indicating that the resource
	 * is owned by a <code>CDEF</code> resource,
	 * a control definition.
	 */
	public static final int OWNER_TYPE_CDEF = 27;
	/**
	 * The owner type indicating that the resource
	 * is owned by a <code>PDEF</code> resource,
	 * a printer definition.
	 */
	public static final int OWNER_TYPE_PDEF = 28;
	/**
	 * The owner type indicating that the resource
	 * is owned by a <code>PACK</code> resource,
	 * an operating system package.
	 */
	public static final int OWNER_TYPE_PACK = 29;
	/**
	 * An owner type reserved for future use.
	 */
	public static final int OWNER_TYPE_RSV1 = 30;
	/**
	 * An owner type reserved for future use.
	 */
	public static final int OWNER_TYPE_RSV2 = 31;
	
	/**
	 * The owner type indicating that the resource
	 * is owned by a control panel. In addition, the
	 * owner ID must be set to <code>OWNER_ID_CDEV</code>.
	 */
	public static final int OWNER_TYPE_CDEV = 30;
	/**
	 * The owner ID indicating that the resource
	 * is owned by a control panel. In addition, the
	 * owner type must be set to <code>OWNER_TYPE_CDEV</code>.
	 */
	public static final int OWNER_ID_CDEV = 1;
	
	/**
	 * Returns an integer representing the type of resource
	 * which owns this resource. Only values from 24 to 31
	 * indicate that the resource is owned.<p>
	 * If the ID number of a resource is between the values
	 * -16384 and -1, the resource is considered owned by
	 * another resource. The most significant five bits of
	 * the ID indicate the resource type of the owner,
	 * the middle six bits of the ID indicate the ID number
	 * of the owner, and the least significant five bits of
	 * the ID indicate the sub-ID of the owned resource.
	 * Programs, such as Font/DA Mover, which install items
	 * consisting of multiple resources, such as desk accessories,
	 * use this to make sure all the necessary resources are
	 * copied over, and renumber the resources if ID number
	 * conflicts occur.
	 * @return An integer from 0 to 31 indicating the resource type of the owner. Only values from 24 to 31 indicate that the resource is owned. The values corresponding to certain resource types are defined by the <code>OWNER_TYPE</code> constants.
	 */
	public int getOwnerType() {
		return ((id >> 11) & 0x1F);
	}
	
	/**
	 * Returns the ID number of the owner of this resource.
	 * This is only useful if the owner type is from 24 to 31.<p>
	 * If the ID number of a resource is between the values
	 * -16384 and -1, the resource is considered owned by
	 * another resource. The most significant five bits of
	 * the ID indicate the resource type of the owner,
	 * the middle six bits of the ID indicate the ID number
	 * of the owner, and the least significant five bits of
	 * the ID indicate the sub-ID of the owned resource.
	 * Programs, such as Font/DA Mover, which install items
	 * consisting of multiple resources, such as desk accessories,
	 * use this to make sure all the necessary resources are
	 * copied over, and renumber the resources if ID number
	 * conflicts occur.
	 * @return The ID number of the owner, an integer from 0 to 63.
	 */
	public int getOwnerID() {
		return ((id >> 5) & 0x3F);
	}
	
	/**
	 * Returns the sub-ID of an owned resource.
	 * This is only useful if the owner type is from 24 to 31.<p>
	 * If the ID number of a resource is between the values
	 * -16384 and -1, the resource is considered owned by
	 * another resource. The most significant five bits of
	 * the ID indicate the resource type of the owner,
	 * the middle six bits of the ID indicate the ID number
	 * of the owner, and the least significant five bits of
	 * the ID indicate the sub-ID of the owned resource.
	 * Programs, such as Font/DA Mover, which install items
	 * consisting of multiple resources, such as desk accessories,
	 * use this to make sure all the necessary resources are
	 * copied over, and renumber the resources if ID number
	 * conflicts occur.
	 * @return The sub-ID of this resource, an integer from 0 to 31.
	 */
	public int getSubID() {
		return (id & 0x1F);
	}
	
	/**
	 * Changes which type of resource owns this resource.<p>
	 * If the ID number of a resource is between the values
	 * -16384 and -1, the resource is considered owned by
	 * another resource. The most significant five bits of
	 * the ID indicate the resource type of the owner,
	 * the middle six bits of the ID indicate the ID number
	 * of the owner, and the least significant five bits of
	 * the ID indicate the sub-ID of the owned resource.
	 * Programs, such as Font/DA Mover, which install items
	 * consisting of multiple resources, such as desk accessories,
	 * use this to make sure all the necessary resources are
	 * copied over, and renumber the resources if ID number
	 * conflicts occur.
	 * @param ot An integer from 0 to 31 indicating the resource type of the owner. Only values from 24 to 31 generate ID numbers in the proper range for owned resources. The values corresponding to certain resource types are defined by the <code>OWNER_TYPE</code> constants.
	 */
	public void setOwnerType(int ot) {
		id = (short)(((ot & 0x1F) << 11) | (id & 0x7FF));
	}
	
	/**
	 * Changes the owner ID of an owned resource.<p>
	 * If the ID number of a resource is between the values
	 * -16384 and -1, the resource is considered owned by
	 * another resource. The most significant five bits of
	 * the ID indicate the resource type of the owner,
	 * the middle six bits of the ID indicate the ID number
	 * of the owner, and the least significant five bits of
	 * the ID indicate the sub-ID of the owned resource.
	 * Programs, such as Font/DA Mover, which install items
	 * consisting of multiple resources, such as desk accessories,
	 * use this to make sure all the necessary resources are
	 * copied over, and renumber the resources if ID number
	 * conflicts occur.
	 * @param oi The ID number of the owner, an integer from 0 to 63.
	 */
	public void setOwnerID(int oi) {
		id = (short)(((oi & 0x3F) << 5) | (id & 0xF81F));
	}
	
	/**
	 * Changes the sub-ID of an owned resource.<p>
	 * If the ID number of a resource is between the values
	 * -16384 and -1, the resource is considered owned by
	 * another resource. The most significant five bits of
	 * the ID indicate the resource type of the owner,
	 * the middle six bits of the ID indicate the ID number
	 * of the owner, and the least significant five bits of
	 * the ID indicate the sub-ID of the owned resource.
	 * Programs, such as Font/DA Mover, which install items
	 * consisting of multiple resources, such as desk accessories,
	 * use this to make sure all the necessary resources are
	 * copied over, and renumber the resources if ID number
	 * conflicts occur.
	 * @param si The sub-ID of this resource, an integer from 0 to 31.
	 */
	public void setSubID(int si) {
		id = (short)((si & 0x1F) | (id & 0xFFE0));
	}
	
	/**
	 * Returns the attributes of the resource as a byte.
	 * The bits are as follows:
	 * <table border=1>
	 * <tr><td><strong>Bit</strong></td><td><strong>Attribute</strong></td></tr>
	 * <tr><td>0</td><td>Compressed</td></tr>
	 * <tr><td>1</td><td>Changed</td></tr>
	 * <tr><td>2</td><td>Preload</td></tr>
	 * <tr><td>3</td><td>Protected</td></tr>
	 * <tr><td>4</td><td>Locked</td></tr>
	 * <tr><td>5</td><td>Purgeable</td></tr>
	 * <tr><td>6</td><td>Sysheap</td></tr>
	 * <tr><td>7</td><td>Reserved</td></tr>
	 * </table>
	 * @return The byte representing the resource's attributes.
	 */
	public byte getAttributes() {
		byte b = 0;
		if (compressed) b |= 0x01;
		if (changed)    b |= 0x02;
		if (preload)    b |= 0x04;
		if (protect)    b |= 0x08;
		if (locked)     b |= 0x10;
		if (purgeable)  b |= 0x20;
		if (sysheap)    b |= 0x40;
		if (reserved)   b |= 0x80;
		return b;
	}
	
	/**
	 * Returns the attributes of the resource as a string of letters.
	 * The letters and the attributes they represent are as follows:
	 * <table border=1>
	 * <tr><td><strong>Character</strong></td><td><strong>Attribute</strong></td></tr>
	 * <tr><td>C</td><td>Compressed</td></tr>
	 * <tr><td>H</td><td>Changed</td></tr>
	 * <tr><td>L</td><td>Preload</td></tr>
	 * <tr><td>P</td><td>Protected</td></tr>
	 * <tr><td>F</td><td>Locked</td></tr>
	 * <tr><td>U</td><td>Purgeable</td></tr>
	 * <tr><td>S</td><td>Sysheap</td></tr>
	 * <tr><td>R</td><td>Reserved</td></tr>
	 * </table>
	 * @return The string representing the resource's attributes.
	 */
	public String getAttributeString() {
		StringBuffer a = new StringBuffer();
		if (compressed)   a.append("C"); else a.append("-");
		if (changed)      a.append("H"); else a.append("-");
		if (preload)      a.append("L"); else a.append("-");
		if (protect)      a.append("P"); else a.append("-");
		if (locked)       a.append("F"); else a.append("-");
		if (purgeable)    a.append("U"); else a.append("-");
		if (sysheap)      a.append("S"); else a.append("-");
		if (reserved)     a.append("R"); else a.append("-");
		return a.toString();
	}
	
	/**
	 * Sets the attributes of the resource, using
	 * the byte for the resource attributes from the
	 * resource map. The bits are as follows:
	 * <table border=1>
	 * <tr><td><strong>Bit</strong></td><td><strong>Attribute</strong></td></tr>
	 * <tr><td>0</td><td>Compressed</td></tr>
	 * <tr><td>1</td><td>Changed</td></tr>
	 * <tr><td>2</td><td>Preload</td></tr>
	 * <tr><td>3</td><td>Protected</td></tr>
	 * <tr><td>4</td><td>Locked</td></tr>
	 * <tr><td>5</td><td>Purgeable</td></tr>
	 * <tr><td>6</td><td>Sysheap</td></tr>
	 * <tr><td>7</td><td>Reserved</td></tr>
	 * </table>
	 * @param b The byte representing the resource's attributes.
	 */
	public void setAttributes(byte b) {
		compressed = ((b & 0x01) != 0);
		changed    = ((b & 0x02) != 0);
		preload    = ((b & 0x04) != 0);
		protect    = ((b & 0x08) != 0);
		locked     = ((b & 0x10) != 0);
		purgeable  = ((b & 0x20) != 0);
		sysheap    = ((b & 0x40) != 0);
		reserved   = ((b & 0x80) != 0);
	}
	
	/**
	 * Sets the attributes of the resource, using
	 * the characters of a string to determine which attributes are set.
	 * <table border=1>
	 * <tr><td><strong>Character</strong></td><td><strong>Attribute</strong></td></tr>
	 * <tr><td>C</td><td>Compressed</td></tr>
	 * <tr><td>H, N</td><td>Changed</td></tr>
	 * <tr><td>L</td><td>Preload</td></tr>
	 * <tr><td>P</td><td>Protected</td></tr>
	 * <tr><td>F</td><td>Locked</td></tr>
	 * <tr><td>U, G</td><td>Purgeable</td></tr>
	 * <tr><td>S</td><td>Sysheap</td></tr>
	 * <tr><td>R</td><td>Reserved</td></tr>
	 * </table>
	 * @param a The string representing the resource's attributes.
	 */
	public void setAttributeString(String a) {
		compressed = a.contains("C") || a.contains("c");
		changed    = a.contains("H") || a.contains("h") || a.contains("N") || a.contains("n");
		preload    = a.contains("L") || a.contains("l");
		protect    = a.contains("P") || a.contains("p");
		locked     = a.contains("F") || a.contains("f");
		purgeable  = a.contains("U") || a.contains("u") || a.contains("G") || a.contains("g");
		sysheap    = a.contains("S") || a.contains("s");
		reserved   = a.contains("R") || a.contains("r");
	}
	
	/**
	 * Creates a shallow copy of this resource.<p>
	 * This operation is not recommended, especially when modifying resources.
	 * Since the object returned is a shallow copy, modifying the <code>data</code>
	 * member of the shallow copy will modify the <code>data</code> member of the
	 * original, but replacing the <code>data</code> member with a different array
	 * of bytes or changing the type, ID, name, or attributes of the copy will
	 * not do the same to the original. Some methods of the subclasses of <code>MacResource</code>
	 * will modify <code>data</code> itself, and some will replace <code>data</code>
	 * with another array. This will result in a terrible consistency problem.<p>
	 * The <code>deepCopy</code> method should be used instead of <code>shallowCopy</code>.
	 * @return A shallow copy of this resource.
	 */
	public MacResource shallowCopy() {
		return new MacResource(this.type, this.id, this.getAttributes(), this.name, this.data);
	}
	
	/**
	 * Creates a deep copy of this resource.
	 * @return A deep copy of this resource.
	 */
	public MacResource deepCopy() {
		return new MacResource(this.type, this.id, this.getAttributes(), new String(this.name), KSFLUtilities.copy(this.data));
	}
	
	/**
	 * Casts this <code>MacResource</code> object to a subclass of <code>MacResource</code>
	 * by creating a shallow copy of this resource as an instance of that subclass.
	 * This is done by calling the <code>MacResource(int type, short id, byte attr,
	 * String name, byte[] data)</code> constructor of the subclass with the type,
	 * ID, attributes, name, and data of this resource, effectively creating a shallow
	 * copy but as an instance of a subclass of <code>MacResource</code>.<p>
	 * If some kind of error happens (the subclass does not have the right constructor,
	 * the constructor throws an exception, the passed class is not a subclass of
	 * <code>MacResource</code>, or some other error), a shallow copy of class <code>MacResource</code>
	 * is returned instead.<p>
	 * This operation is not recommended, especially when modifying resources.
	 * Since the object returned is a shallow copy, modifying the <code>data</code>
	 * member of the shallow copy will modify the <code>data</code> member of the
	 * original, but replacing the <code>data</code> member with a different array
	 * of bytes or changing the type, ID, name, or attributes of the copy will
	 * not do the same to the original. Some methods of the subclasses of <code>MacResource</code>
	 * will modify <code>data</code> itself, and some will replace <code>data</code>
	 * with another array. This will result in a terrible consistency problem.<p>
	 * The <code>deepRecast</code> method or the <code>get</code> methods of
	 * <code>MacResourceFork</code> should be used instead of <code>shallowRecast</code>.
	 * @param resourceSubclass A subclass of <code>MacResource</code>.
	 * @return A shallow copy of this resource, an instance of class <code>resourceSubclass</code>.
	 */
	public <R extends MacResource> R shallowRecast(Class<R> resourceSubclass) {
		Class<?>[] fparam = { int.class, short.class, byte.class, String.class, byte[].class };
		Object[] aparam = { this.type, this.id, this.getAttributes(), this.name, this.data };
		try {
			return resourceSubclass.getConstructor(fparam).newInstance(aparam);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Casts this <code>MacResource</code> object to a subclass of <code>MacResource</code>
	 * by creating a deep copy of this resource as an instance of that subclass.
	 * This is done by calling the <code>MacResource(int type, short id, byte attr,
	 * String name, byte[] data)</code> constructor of the subclass with the type,
	 * ID, attributes, name, and a copy of the data of this resource, effectively creating
	 * a deep copy but as an instance of a subclass of <code>MacResource</code>.<p>
	 * If some kind of error happens (the subclass does not have the right constructor,
	 * the constructor throws an exception, the passed class is not a subclass of
	 * <code>MacResource</code>, or some other error), a deep copy of class <code>MacResource</code>
	 * is returned instead.
	 * @param resourceSubclass A subclass of <code>MacResource</code>.
	 * @return A deep copy of this resource, an instance of class <code>resourceSubclass</code>.
	 */
	public <R extends MacResource> R deepRecast(Class<R> resourceSubclass) {
		Class<?>[] fparam = { int.class, short.class, byte.class, String.class, byte[].class };
		Object[] aparam = { this.type, this.id, this.getAttributes(), new String(this.name), KSFLUtilities.copy(this.data) };
		try {
			return resourceSubclass.getConstructor(fparam).newInstance(aparam);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Compares this resource against another object to check if they are equal.
	 * Two resources are equal if their types, ID numbers, attributes, names,
	 * and data are equal.
	 * If <code>o</code> is not a <code>MacResource</code> or a subclass of
	 * <code>MacResource</code>, this returns false.
	 * @return True if <code>o</code> equals this resource, false otherwise.
	 */
	public boolean equals(Object o) {
		if (o instanceof MacResource) {
			MacResource other = (MacResource)o;
			return (
					this.type == other.type &&
					this.id == other.id &&
					this.name.equals(other.name) &&
					this.getAttributes() == other.getAttributes() &&
					Arrays.equals(this.data, other.data)
			);
		} else {
			return false;
		}
	}
	
	/**
	 * Returns a hashcode for this resource.
	 * @return A hash code value for this object.
	 */
	public int hashCode() {
		return type ^ ((id << 16) | (getAttributes() & 0xFF)) ^ name.hashCode() ^ Arrays.hashCode(data);
	}
}
