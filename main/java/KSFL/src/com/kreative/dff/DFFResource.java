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

import java.util.Arrays;
import com.kreative.ksfl.KSFLUtilities;

/**
 * The <code>DFFResource</code> class represents an resource
 * stored in a DFF file.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class DFFResource {
	/**
	 * Checks if a DFF type is one this class knows how to handle.
	 * The default implementation is to always return true.
	 * It is recommended that subclasses override this.
	 * @param type A DFF type to check.
	 * @return True if this class can handle this DFF type, false otherwise.
	 */
	public static boolean isMyType(long type) {
		return true;
	}
	
	/**
	 * The DFF type, which indicates what kind of data is contained in the resource.
	 * Usually this is expressed as an eight-character constant, with the first
	 * character representing the most significant byte and the last character
	 * representing the least significant byte. The encoding used is usually
	 * ISO Latin 1, but UTF-16 (with four-character constants) is also supported.
	 */
	public long type;
	/**
	 * The resource's ID number.
	 */
	public int id;
	/**
	 * The resource's data type. This is a long-obsolete field, now allowed
	 * to be used by applications for their own purposes.
	 */
	public short datatype;
	/**
	 * The resource's name.
	 */
	public String name;
	/**
	 * The read-only attribute, indicating that the resource
	 * should not be modified by editors.
	 */
	public boolean readonly;
	/**
	 * The system attribute, indicating that the resource belongs
	 * to the operating system.
	 */
	public boolean system;
	/**
	 * The preload attribute, indicating that the resource should
	 * be loaded into memory as soon as the DFF file is loaded.
	 */
	public boolean preload;
	/**
	 * The purgeable attribute, indicating that the resource can
	 * be removed from memory if more memory is needed.
	 */
	public boolean purgeable;
	/**
	 * The from file attribute, indicating that the resource
	 * was originally contained in a file.
	 */
	public boolean fromfile;
	/**
	 * The from resource attribute, indicating that the resource
	 * was originally contained in another platform's resource
	 * database format, usually a Mac OS resource fork.
	 */
	public boolean fromrsrc;
	/**
	 * The invisible attribute, indicating that the resource
	 * should not appear when applications present lists of
	 * this kind of resource.
	 */
	public boolean invisible;
	/**
	 * The disabled attribute, indicating that the resource
	 * should be ignored when loading resources of this type.
	 */
	public boolean disabled;
	/**
	 * The protected attribute, indicating that the resource
	 * should not be modified when in memory.
	 */
	public boolean protect;
	/**
	 * The fixed attribute, indicating that the resource should
	 * not be moved around in memory.
	 */
	public boolean fixed;
	/**
	 * The multilingual attribute, indicating that the resource
	 * contains multiple versions of its data for different
	 * languages or regions. Multilingualism is not transparent
	 * and must be handled separately from DFF I/O.
	 * <p>
	 * This flag is new to version 3 of the DFF format.
	 * In version 2 of the DFF format, this was a reserved flag.
	 * Version 1 did not have flags at all.
	 */
	public boolean multilingual;
	/**
	 * The compressed attribute, indicating that the resource's
	 * data is compressed. Compression is not transparent
	 * and must be handled separately from DFF I/O.
	 */
	public boolean compressed;
	/**
	 * The application use 1 attribute, used by applications
	 * for their own purposes.
	 */
	public boolean appuse1;
	/**
	 * The application use 2 attribute, used by applications
	 * for their own purposes.
	 */
	public boolean appuse2;
	/**
	 * The application use 3 attribute, used by applications
	 * for their own purposes.
	 */
	public boolean appuse3;
	/**
	 * The application use 4 attribute, used by applications
	 * for their own purposes.
	 */
	public boolean appuse4;
	/**
	 * The resource's data.
	 */
	public byte[] data;
	
	/**
	 * The legacy value for <code>datatype</code> corresponding to raw binary data.
	 */
	public static final short DATA_TYPE_BINARY = 0;
	/**
	 * The legacy value for <code>datatype</code> corresponding to plain text.
	 */
	public static final short DATA_TYPE_PLAINTEXT = 1;
	/**
	 * The legacy value for <code>datatype</code> corresponding to Mac OS TextEdit styled text.
	 */
	public static final short DATA_TYPE_MACTEXT = 2;
	/**
	 * The legacy value for <code>datatype</code> corresponding to DFF formatted text.
	 */
	public static final short DATA_TYPE_DFFTEXT = 3;
	/**
	 * The legacy value for <code>datatype</code> corresponding to an image.
	 */
	public static final short DATA_TYPE_IMAGE = 4;
	/**
	 * The legacy value for <code>datatype</code> corresponding to a DFF image.
	 */
	public static final short DATA_TYPE_DFFIMAGE = 5;
	/**
	 * The legacy value for <code>datatype</code> corresponding to a sound.
	 */
	public static final short DATA_TYPE_SOUND = 6;
	/**
	 * The legacy value for <code>datatype</code> corresponding to a list of Pascal-style strings with single-byte length fields.
	 */
	public static final short DATA_TYPE_PSTRINGLIST = 7;
	/**
	 * The legacy value for <code>datatype</code> corresponding to a list of Pascal-style strings with two-byte length fields.
	 */
	public static final short DATA_TYPE_WSTRINGLIST = 8;
	/**
	 * The legacy value for <code>datatype</code> corresponding to a list of Pascal-style strings with four-byte length fields.
	 */
	public static final short DATA_TYPE_LSTRINGLIST = 9;
	/**
	 * The legacy value for <code>datatype</code> corresponding to a list of C-style strings.
	 */
	public static final short DATA_TYPE_CSTRINGLIST = 10;
	
	/**
	 * Constructs a new DFF resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The DFF type as an integer.
	 * @param id The ID number.
	 * @param data The data.
	 */
	public DFFResource(long type, int id, byte[] data) {
		this.type = type;
		this.id = id;
		this.datatype = 0;
		this.name = "";
		this.data = data;
		readonly = false;
		system = false;
		preload = false;
		purgeable = false;
		fromfile = false;
		fromrsrc = false;
		invisible = false;
		disabled = false;
		protect = false;
		fixed = false;
		multilingual = false;
		compressed = false;
		appuse1 = false;
		appuse2 = false;
		appuse3 = false;
		appuse4 = false;
	}
	
	/**
	 * Constructs a new DFF resource with the specified type, ID, name, and data.
	 * All attributes are cleared.
	 * @param type The DFF type as an integer.
	 * @param id The ID number.
	 * @param name The name.
	 * @param data The data.
	 */
	public DFFResource(long type, int id, String name, byte[] data) {
		this.type = type;
		this.id = id;
		this.datatype = 0;
		this.name = name;
		this.data = data;
		readonly = false;
		system = false;
		preload = false;
		purgeable = false;
		fromfile = false;
		fromrsrc = false;
		invisible = false;
		disabled = false;
		protect = false;
		fixed = false;
		multilingual = false;
		compressed = false;
		appuse1 = false;
		appuse2 = false;
		appuse3 = false;
		appuse4 = false;
	}
	
	/**
	 * Constructs a new DFF resource with the specified type, ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param type The DFF type as an integer.
	 * @param id The ID number.
	 * @param attr The attributes as a short.
	 * @param data The data.
	 */
	public DFFResource(long type, int id, short attr, byte[] data) {
		this.type = type;
		this.id = id;
		this.datatype = 0;
		this.name = "";
		this.data = data;
		setAttributes(attr);
	}
	
	/**
	 * Constructs a new DFF resource with the specified type, ID, attributes, name, and data.
	 * @param type The DFF type as an integer.
	 * @param id The ID number.
	 * @param attr The attributes as a short.
	 * @param name The name.
	 * @param data The data.
	 */
	public DFFResource(long type, int id, short attr, String name, byte[] data) {
		this.type = type;
		this.id = id;
		this.datatype = 0;
		this.name = name;
		this.data = data;
		setAttributes(attr);
	}
	
	/**
	 * Constructs a new DFF resource with the specified type, ID, data type, attributes, and data.
	 * The name is set to an empty string.
	 * @param type The DFF type as an integer.
	 * @param id The ID number.
	 * @param dtype The data type.
	 * @param attr The attributes as a short.
	 * @param data The data.
	 */
	public DFFResource(long type, int id, short dtype, short attr, byte[] data) {
		this.type = type;
		this.id = id;
		this.datatype = dtype;
		this.name = "";
		this.data = data;
		setAttributes(attr);
	}
	
	/**
	 * Constructs a new DFF resource with the specified type, ID, data type, attributes, name, and data.
	 * @param type The DFF type as an integer.
	 * @param id The ID number.
	 * @param dtype The data type.
	 * @param attr The attributes as a short.
	 * @param name The name.
	 * @param data The data.
	 */
	public DFFResource(long type, int id, short dtype, short attr, String name, byte[] data) {
		this.type = type;
		this.id = id;
		this.datatype = dtype;
		this.name = name;
		this.data = data;
		setAttributes(attr);
	}
	
	/**
	 * Returns the attributes of the resource as a short.
	 * The bits are as follows:
	 * <table border=1>
	 * <tr><td><strong>Bit</strong></td><td><strong>Attribute</strong></td></tr>
	 * <tr><td>0</td><td>Read Only</td></tr>
	 * <tr><td>1</td><td>System</td></tr>
	 * <tr><td>2</td><td>Preload</td></tr>
	 * <tr><td>3</td><td>Purgeable</td></tr>
	 * <tr><td>4</td><td>From File</td></tr>
	 * <tr><td>5</td><td>From Resource</td></tr>
	 * <tr><td>6</td><td>Invisible</td></tr>
	 * <tr><td>7</td><td>Disabled</td></tr>
	 * <tr><td>8</td><td>Protected</td></tr>
	 * <tr><td>9</td><td>Fixed</td></tr>
	 * <tr><td>10</td><td>Multilingual</td></tr>
	 * <tr><td>11</td><td>Compressed</td></tr>
	 * <tr><td>12</td><td>Application Use 1</td></tr>
	 * <tr><td>13</td><td>Application Use 2</td></tr>
	 * <tr><td>14</td><td>Application Use 3</td></tr>
	 * <tr><td>15</td><td>Application Use 4</td></tr>
	 * </table>
	 * @return The short representing the resource's attributes.
	 */
	public short getAttributes() {
		short a = 0;
		if (readonly) a |= 0x0001;
		if (system) a |= 0x0002;
		if (preload) a |= 0x0004;
		if (purgeable) a |= 0x0008;
		if (fromfile) a |= 0x0010;
		if (fromrsrc) a |= 0x0020;
		if (invisible) a |= 0x0040;
		if (disabled) a |= 0x0080;
		if (protect) a |= 0x0100;
		if (fixed) a |= 0x0200;
		if (multilingual) a |= 0x0400;
		if (compressed) a |= 0x0800;
		if (appuse1) a |= 0x1000;
		if (appuse2) a |= 0x2000;
		if (appuse3) a |= 0x4000;
		if (appuse4) a |= 0x8000;
		return a;
	}
	
	/**
	 * Returns the attributes of the resource as a string of letters.
	 * The letters and the attributes they represent are as follows:
	 * <table border=1>
	 * <tr><td><strong>Character</strong></td><td><strong>Attribute</strong></td></tr>
	 * <tr><td>R</td><td>Read Only</td></tr>
	 * <tr><td>S</td><td>System</td></tr>
	 * <tr><td>L</td><td>Preload</td></tr>
	 * <tr><td>U</td><td>Purgeable</td></tr>
	 * <tr><td>A</td><td>From File</td></tr>
	 * <tr><td>B</td><td>From Resource</td></tr>
	 * <tr><td>I</td><td>Invisible</td></tr>
	 * <tr><td>D</td><td>Disabled</td></tr>
	 * <tr><td>P</td><td>Protected</td></tr>
	 * <tr><td>F</td><td>Fixed</td></tr>
	 * <tr><td>M</td><td>Multilingual</td></tr>
	 * <tr><td>C</td><td>Compressed</td></tr>
	 * <tr><td>W</td><td>Application Use 1</td></tr>
	 * <tr><td>X</td><td>Application Use 2</td></tr>
	 * <tr><td>Y</td><td>Application Use 3</td></tr>
	 * <tr><td>Z</td><td>Application Use 4</td></tr>
	 * </table>
	 * @return The string representing the resource's attributes.
	 */
	public String getAttributeString() {
		StringBuffer a = new StringBuffer();
		if (readonly)     a.append("R"); else a.append("-");
		if (system)       a.append("S"); else a.append("-");
		if (preload)      a.append("L"); else a.append("-");
		if (purgeable)    a.append("U"); else a.append("-");
		if (fromfile)     a.append("A"); else a.append("-");
		if (fromrsrc)     a.append("B"); else a.append("-");
		if (invisible)    a.append("I"); else a.append("-");
		if (disabled)     a.append("D"); else a.append("-");
		if (protect)      a.append("P"); else a.append("-");
		if (fixed)        a.append("F"); else a.append("-");
		if (multilingual) a.append("M"); else a.append("-");
		if (compressed)   a.append("C"); else a.append("-");
		if (appuse1)      a.append("W"); else a.append("-");
		if (appuse2)      a.append("X"); else a.append("-");
		if (appuse3)      a.append("Y"); else a.append("-");
		if (appuse4)      a.append("Z"); else a.append("-");
		return a.toString();
	}
	
	/**
	 * Sets the attributes of the resource, using
	 * the short for the resource attributes as stored in the
	 * DFF file. The bits are as follows:
	 * <table border=1>
	 * <tr><td><strong>Bit</strong></td><td><strong>Attribute</strong></td></tr>
	 * <tr><td>0</td><td>Read Only</td></tr>
	 * <tr><td>1</td><td>System</td></tr>
	 * <tr><td>2</td><td>Preload</td></tr>
	 * <tr><td>3</td><td>Purgeable</td></tr>
	 * <tr><td>4</td><td>From File</td></tr>
	 * <tr><td>5</td><td>From Resource</td></tr>
	 * <tr><td>6</td><td>Invisible</td></tr>
	 * <tr><td>7</td><td>Disabled</td></tr>
	 * <tr><td>8</td><td>Protected</td></tr>
	 * <tr><td>9</td><td>Fixed</td></tr>
	 * <tr><td>10</td><td>Multilingual</td></tr>
	 * <tr><td>11</td><td>Compressed</td></tr>
	 * <tr><td>12</td><td>Application Use 1</td></tr>
	 * <tr><td>13</td><td>Application Use 2</td></tr>
	 * <tr><td>14</td><td>Application Use 3</td></tr>
	 * <tr><td>15</td><td>Application Use 4</td></tr>
	 * </table>
	 * @param a The short representing the resource's attributes.
	 */
	public void setAttributes(short a) {
		readonly     = ((a & 0x0001) != 0);
		system       = ((a & 0x0002) != 0);
		preload      = ((a & 0x0004) != 0);
		purgeable    = ((a & 0x0008) != 0);
		fromfile     = ((a & 0x0010) != 0);
		fromrsrc     = ((a & 0x0020) != 0);
		invisible    = ((a & 0x0040) != 0);
		disabled     = ((a & 0x0080) != 0);
		protect      = ((a & 0x0100) != 0);
		fixed        = ((a & 0x0200) != 0);
		multilingual = ((a & 0x0400) != 0);
		compressed   = ((a & 0x0800) != 0);
		appuse1      = ((a & 0x1000) != 0);
		appuse2      = ((a & 0x2000) != 0);
		appuse3      = ((a & 0x4000) != 0);
		appuse4      = ((a & 0x8000) != 0);
	}
	
	/**
	 * Sets the attributes of the resource, using
	 * the characters of a string to determine which attributes are set.
	 * <table border=1>
	 * <tr><td><strong>Character</strong></td><td><strong>Attribute</strong></td></tr>
	 * <tr><td>R</td><td>Read Only</td></tr>
	 * <tr><td>S</td><td>System</td></tr>
	 * <tr><td>L</td><td>Preload</td></tr>
	 * <tr><td>U, G</td><td>Purgeable</td></tr>
	 * <tr><td>A</td><td>From File</td></tr>
	 * <tr><td>B</td><td>From Resource</td></tr>
	 * <tr><td>I, H</td><td>Invisible</td></tr>
	 * <tr><td>D</td><td>Disabled</td></tr>
	 * <tr><td>P</td><td>Protected</td></tr>
	 * <tr><td>F</td><td>Fixed</td></tr>
	 * <tr><td>M</td><td>Multilingual</td></tr>
	 * <tr><td>C</td><td>Compressed</td></tr>
	 * <tr><td>W, 1</td><td>Application Use 1</td></tr>
	 * <tr><td>X, 2</td><td>Application Use 2</td></tr>
	 * <tr><td>Y, 3</td><td>Application Use 3</td></tr>
	 * <tr><td>Z, 4</td><td>Application Use 4</td></tr>
	 * </table>
	 * @param a The string representing the resource's attributes.
	 */
	public void setAttributeString(String a) {
		readonly     = a.contains("R") || a.contains("r");
		system       = a.contains("S") || a.contains("s");
		preload      = a.contains("L") || a.contains("l");
		purgeable    = a.contains("U") || a.contains("u") || a.contains("G") || a.contains("g");
		fromfile     = a.contains("A") || a.contains("a");
		fromrsrc     = a.contains("B") || a.contains("b");
		invisible    = a.contains("I") || a.contains("i") || a.contains("H") || a.contains("h");
		disabled     = a.contains("D") || a.contains("d");
		protect      = a.contains("P") || a.contains("p");
		fixed        = a.contains("F") || a.contains("f");
		multilingual = a.contains("M") || a.contains("m");
		compressed   = a.contains("C") || a.contains("c");
		appuse1      = a.contains("W") || a.contains("w") || a.contains("1");
		appuse2      = a.contains("X") || a.contains("x") || a.contains("2");
		appuse3      = a.contains("Y") || a.contains("y") || a.contains("3");
		appuse4      = a.contains("Z") || a.contains("z") || a.contains("4");
	}
	
	/**
	 * Creates a shallow copy of this resource.<p>
	 * This operation is not recommended, especially when modifying DFF resources.
	 * Since the resource returned is a shallow copy, modifying the <code>data</code>
	 * member of the shallow copy will modify the <code>data</code> member of the
	 * original, but replacing the <code>data</code> member with a different array
	 * of bytes or changing the type, ID, name, or attributes of the copy will
	 * not do the same to the original. Some methods of the subclasses of <code>DFFResource</code>
	 * will modify <code>data</code> itself, and some will replace <code>data</code>
	 * with another array. This will result in a terrible consistency problem.<p>
	 * The <code>deepCopy</code> method should be used instead of <code>shallowCopy</code>.
	 * @return A shallow copy of this resource.
	 */
	public DFFResource shallowCopy() {
		return new DFFResource(this.type, this.id, this.datatype, this.getAttributes(), this.name, this.data);
	}
	
	/**
	 * Creates a deep copy of this resource.
	 * @return A deep copy of this resource.
	 */
	public DFFResource deepCopy() {
		return new DFFResource(this.type, this.id, this.datatype, this.getAttributes(), this.name, KSFLUtilities.copy(this.data));
	}
	
	/**
	 * Casts this <code>DFFResource</code> to a subclass of <code>DFFResource</code>
	 * by creating a shallow copy of this resource as an instance of that subclass.
	 * This is done by calling the <code>DFFResource(long type, int id, short dtype, short attr,
	 * String name, byte[] data)</code> constructor of the subclass with the type,
	 * ID, data type, attributes, name, and data of this resource, effectively creating a shallow
	 * copy but as an instance of a subclass of <code>DFFResource</code>.<p>
	 * If some kind of error happens (the subclass does not have the right constructor,
	 * the constructor throws an exception, the passed class is not a subclass of
	 * <code>DFFResource</code>, or some other error), a shallow copy of class <code>DFFResource</code>
	 * is returned instead.<p>
	 * This operation is not recommended, especially when modifying DFF resources.
	 * Since the resource returned is a shallow copy, modifying the <code>data</code>
	 * member of the shallow copy will modify the <code>data</code> member of the
	 * original, but replacing the <code>data</code> member with a different array
	 * of bytes or changing the type, ID, name, or attributes of the copy will
	 * not do the same to the original. Some methods of the subclasses of <code>DFFResource</code>
	 * will modify <code>data</code> itself, and some will replace <code>data</code>
	 * with another array. This will result in a terrible consistency problem.<p>
	 * The <code>deepRecast</code> method or the <code>get</code> methods of
	 * <code>DFFProvider</code> should be used instead of <code>shallowRecast</code>.
	 * @param dffoSubclass A subclass of <code>DFFResource</code>.
	 * @return A shallow copy of this resource, an instance of class <code>dffoSubclass</code>.
	 */
	public <R extends DFFResource> R shallowRecast(Class<R> dffoSubclass) {
		Class<?>[] fparam = { long.class, int.class, short.class, short.class, String.class, byte[].class };
		Object[] aparam = { this.type, this.id, this.datatype, this.getAttributes(), this.name, this.data };
		try {
			return dffoSubclass.getConstructor(fparam).newInstance(aparam);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Casts this <code>DFFResource</code> to a subclass of <code>DFFResource</code>
	 * by creating a deep copy of this resource as an instance of that subclass.
	 * This is done by calling the <code>DFFResource(long type, int id, short dtype, short attr,
	 * String name, byte[] data)</code> constructor of the subclass with the type,
	 * ID, data type, attributes, name, and a copy of the data of this resource, effectively creating
	 * a deep copy but as an instance of a subclass of <code>DFFResource</code>.<p>
	 * If some kind of error happens (the subclass does not have the right constructor,
	 * the constructor throws an exception, the passed class is not a subclass of
	 * <code>DFFResource</code>, or some other error), a deep copy of class <code>DFFResource</code>
	 * is returned instead.
	 * @param dffoSubclass A subclass of <code>DFFResource</code>.
	 * @return A deep copy of this resource, an instance of class <code>dffoSubclass</code>.
	 */
	public <R extends DFFResource> R deepRecast(Class<R> dffoSubclass) {
		Class<?>[] fparam = { long.class, int.class, short.class, short.class, String.class, byte[].class };
		Object[] aparam = { this.type, this.id, this.datatype, this.getAttributes(), this.name, KSFLUtilities.copy(this.data) };
		try {
			return dffoSubclass.getConstructor(fparam).newInstance(aparam);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Compares this resource against another resource to check if they are equal.
	 * Two DFF resources are equal if their types, ID numbers, data types, attributes, names,
	 * and data are equal.
	 * If <code>o</code> is not a <code>DFFResource</code> or a subclass of
	 * <code>DFFResource</code>, this returns false.
	 * @return True if <code>o</code> equals this resource, false otherwise.
	 */
	public boolean equals(Object o) {
		if (o instanceof DFFResource) {
			DFFResource other = (DFFResource)o;
			return (
					this.type == other.type &&
					this.id == other.id &&
					this.datatype == other.datatype &&
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
	 * @return A hash code value for this resource.
	 */
	public int hashCode() {
		return (
			(int)(type >> 32) ^ (int)(type) ^ id ^ ((datatype << 16) | (getAttributes() & 0xFFFF)) ^ name.hashCode() ^ Arrays.hashCode(data)
		);
	}
}
