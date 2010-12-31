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

import com.kreative.ksfl.*;
import com.kreative.rsrc.MacResource;

/**
 * The <code>IconSuiteResource</code> class represents a Mac OS icon suite resource.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class IconSuiteResource extends MacResource {
	/**
	 * The resource type of a Mac OS icon suite resource,
	 * the four-character constant <code>icns</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.icns;
	/**
	 * The resource type of a Mac OS icon suite resource for keyboard layouts,
	 * the four-character constant <code>kcns</code>.
	 */
	public static final int RESOURCE_TYPE_KEYBOARD = KSFLConstants.kcns;
	
	/**
	 * Checks if a resource type is one this class knows how to handle.
	 * The default implementation is to always return true.
	 * It is recommended that subclasses override this.
	 * @param type A resource type to check.
	 * @return True if this class can handle this resource type, false otherwise.
	 */
	public static boolean isMyType(int type) {
		return (type == RESOURCE_TYPE || type == RESOURCE_TYPE_KEYBOARD);
	}
	
	/**
	 * Constructs a new resource of type <code>icns</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public IconSuiteResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
	}
	
	/**
	 * Constructs a new resource of type <code>icns</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public IconSuiteResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
	}
	
	/**
	 * Constructs a new resource of type <code>icns</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public IconSuiteResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
	}
	
	/**
	 * Constructs a new resource of type <code>icns</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public IconSuiteResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public IconSuiteResource(int type, short id, byte[] data) {
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
	public IconSuiteResource(int type, short id, String name, byte[] data) {
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
	public IconSuiteResource(int type, short id, byte attr, byte[] data) {
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
	public IconSuiteResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
	}
	
	/**
	 * Finds the location and size of a member in this icon suite.
	 * The first integer returned is the location; the second is the size.
	 * The type can be a constant from <code>IconResource</code> or some other
	 * type that just happens to be in there. If the icon suite
	 * does not contain the requested member, <code>null</code> is returned.
	 * @param type the type of member to find.
	 * @return the location and size of the member, or null if the member is not found.
	 */
	public int[] getMemberOffset(int type) {
		int p = 8;
		while (p+8 <= data.length) {
			int ct = KSFLUtilities.getInt(data, p);
			int cs = KSFLUtilities.getInt(data, p+4);
			if (ct == type) return new int[]{ p, cs };
			else p += cs;
		}
		return null;
	}
	
	/**
	 * Retrieves the raw data of a member of this icon suite.
	 * The type can be a constant from <code>IconResource</code> or some other
	 * type that just happens to be in there. If the icon suite
	 * does not contain the requested member, <code>null</code> is returned.
	 * @param type the type of member to retrieve.
	 * @return the member's data.
	 */
	public byte[] getMemberData(int type) {
		int[] loc = getMemberOffset(type);
		if (loc == null) return null;
		return KSFLUtilities.copy(data, loc[0]+8, loc[1]);
	}
	
	/**
	 * Retrieves a member of this icon suite.
	 * The type can be a constant from <code>IconResource</code> or some other
	 * type that just happens to be in there. If the icon suite
	 * does not contain the requested member, <code>null</code> is returned.
	 * <p>
	 * If the type is not one of the types supported by <code>IconResource</code>,
	 * an <code>IconResource</code> will be returned but the width, height, etc.
	 * fields will not be set and the methods specific to <code>IconResource</code>
	 * will return <code>null</code>.
	 * If the type is one of the JPEG2000 types introduced in Mac OS X Leopard,
	 * the width and height will be set but the depth will not and the methods
	 * specific to <code>IconResource</code> will return <code>null</code>.
	 * @param type the type of member to retrieve.
	 * @return the member.
	 */
	public IconResource getMember(int type) {
		int[] loc = getMemberOffset(type);
		if (loc == null) return null;
		return new IconResource(type, id, getAttributes(), name, KSFLUtilities.copy(data, loc[0]+8, loc[1]));
	}
}
