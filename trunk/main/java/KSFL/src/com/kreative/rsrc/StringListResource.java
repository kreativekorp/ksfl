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

import java.io.UnsupportedEncodingException;
import java.util.Vector;
import com.kreative.ksfl.KSFLConstants;

/**
 * The <code>RStringList</code> class represents a Pascal string list resource.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class StringListResource extends MacResource {
	/**
	 * The resource type of a Pascal string list resource,
	 * the four-character constant <code>STR#</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.STR$;
	/**
	 * The resource type of a Mac OS MultiFinder string list resource,
	 * the four-character constant <code>mst#</code>.
	 */
	public static final int RESOURCE_TYPE_MULTIFINDER_STRING = KSFLConstants.mst$;
	
	/**
	 * Checks if a resource type is one this class knows how to handle.
	 * The default implementation is to always return true.
	 * It is recommended that subclasses override this.
	 * @param type A resource type to check.
	 * @return True if this class can handle this resource type, false otherwise.
	 */
	public static boolean isMyType(int type) {
		return (type == RESOURCE_TYPE || type == RESOURCE_TYPE_MULTIFINDER_STRING);
	}
	
	/**
	 * Constructs a new resource of type <code>STR#</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public StringListResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
	}
	
	/**
	 * Constructs a new resource of type <code>STR#</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public StringListResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
	}
	
	/**
	 * Constructs a new resource of type <code>STR#</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public StringListResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
	}
	
	/**
	 * Constructs a new resource of type <code>STR#</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public StringListResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public StringListResource(int type, short id, byte[] data) {
		super(type, id, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, name, and data.
	 * All attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public StringListResource(int type, short id, String name, byte[] data) {
		super(type, id, name, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public StringListResource(int type, short id, byte attr, byte[] data) {
		super(type, id, attr, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, attributes, name, and data.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public StringListResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
	}
	
	/**
	 * Retrieves the number of strings in the string list.
	 * @return The number of strings in the string list.
	 */
	public int getStringCount() {
		return ((data[0] & 0xFF)<<8 | (data[1] & 0xFF));
	}
	
	/**
	 * Retrieves a string from the string list, using the default text encoding.
	 * @param i The position of the string.
	 * @return The string.
	 */
	public String getString(int i) {
		if (i < 0 || i >= getStringCount()) throw new IndexOutOfBoundsException();
		int p = 2;
		while (i > 0) {
			p += (data[p] & 0xFF)+1;
			i--;
		}
		return new String(data, p+1, data[p] & 0xFF);
	}
	
	/**
	 * Retrieves a string from the string list, using the specified text encoding.
	 * @param i The position of the string.
	 * @param tenc The text encoding of the string.
	 * @return The string.
	 * @throws UnsupportedEncodingException
	 */
	public String getString(int i, String tenc) throws UnsupportedEncodingException {
		if (i < 0 || i >= getStringCount()) throw new IndexOutOfBoundsException();
		int p = 2;
		while (i > 0) {
			p += (data[p] & 0xFF)+1;
			i--;
		}
		return new String(data, p+1, data[p] & 0xFF, tenc);
	}
	
	/**
	 * Retrieves all the strings from the string list as an array of strings, using the default text encoding.
	 * @return The string list as an array of strings.
	 */
	public String[] getStrings() {
		Vector<String> strs = new Vector<String>();
		int i = getStringCount();
		int p = 2;
		while (i > 0) {
			strs.add(new String(data, p+1, data[p] & 0xFF));
			p += (data[p] & 0xFF)+1;
			i--;
		}
		return strs.toArray(new String[0]);
	}
	
	/**
	 * Retrieves all the strings from the string list as an array of strings, using the specified text encoding.
	 * @param tenc The text encoding of the string list.
	 * @return The string list as an array of strings.
	 * @throws UnsupportedEncodingException
	 */
	public String[] getStrings(String tenc) throws UnsupportedEncodingException {
		Vector<String> strs = new Vector<String>();
		int i = getStringCount();
		int p = 2;
		while (i > 0) {
			strs.add(new String(data, p+1, data[p] & 0xFF, tenc));
			p += (data[p] & 0xFF)+1;
			i--;
		}
		return strs.toArray(new String[0]);
	}
}
