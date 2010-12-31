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
import com.kreative.ksfl.KSFLConstants;

/**
 * The <code>UnicodeTextResource</code> class represents a Unicode text resource.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class UnicodeTextResource extends MacResource {
	/**
	 * The resource type of a Unicode text resource,
	 * the four-character constant <code>utxt</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.utxt;
	
	/**
	 * Checks if a resource type is one this class knows how to handle.
	 * The default implementation is to always return true.
	 * It is recommended that subclasses override this.
	 * @param type A resource type to check.
	 * @return True if this class can handle this resource type, false otherwise.
	 */
	public static boolean isMyType(int type) {
		return (type == RESOURCE_TYPE);
	}
	
	/**
	 * Constructs a new resource of type <code>utxt</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public UnicodeTextResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
	}
	
	/**
	 * Constructs a new resource of type <code>utxt</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public UnicodeTextResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
	}
	
	/**
	 * Constructs a new resource of type <code>utxt</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public UnicodeTextResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
	}
	
	/**
	 * Constructs a new resource of type <code>utxt</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public UnicodeTextResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public UnicodeTextResource(int type, short id, byte[] data) {
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
	public UnicodeTextResource(int type, short id, String name, byte[] data) {
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
	public UnicodeTextResource(int type, short id, byte attr, byte[] data) {
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
	public UnicodeTextResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
	}
	
	/**
	 * Returns the text contained in this resource.
	 * @return the text contained in this resource.
	 */
	public String getText() {
		try {
			return new String(data, "UTF-16BE");
		} catch (UnsupportedEncodingException uee) {
			char[] chars = new char[data.length/2];
			for (int i = 0, j = 0; i < chars.length && j < data.length; i++, j+=2) {
				chars[i] = (char)(((data[j] & 0xFF) << 8) | (data[j+1] & 0xFF));
			}
			return new String(chars);
		}
	}
}
