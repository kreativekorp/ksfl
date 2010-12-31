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

/**
 * The <code>IconListResource</code> class represents two kinds of Mac OS icon list resources.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class IconListResource extends MacResource {
	/**
	 * The resource type of a Mac OS 16x16 black-and-white icon list resource,
	 * the four-character constant <code>SICN</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.SICN;
	/**
	 * The resource type of a Mac OS 32x32 black-and-white icon list resource,
	 * the four-character constant <code>ICN#</code>. While the <code>IconListResource</code>
	 * class can be used to define <code>ICN#</code> resources, in most cases
	 * the class <code>IconResource</code> should be used instead.
	 */
	public static final int RESOURCE_TYPE_32 = KSFLConstants.ICN$;
	
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
	 * The width of the icons.
	 * This is usually automatically determined from the
	 * resource type when the resource is created, but if
	 * the resource type is not one of the <code>RESOURCE_TYPE</code>
	 * constants defined for this class, this will need
	 * to be set manually.
	 */
	public int width;
	/**
	 * The height of the icons.
	 * This is usually automatically determined from the
	 * resource type when the resource is created, but if
	 * the resource type is not one of the <code>RESOURCE_TYPE</code>
	 * constants defined for this class, this will need
	 * to be set manually.
	 */
	public int height;
	/**
	 * The color depth of the icon images.
	 * This is usually automatically determined from the
	 * resource type when the resource is created, but if
	 * the resource type is not one of the <code>RESOURCE_TYPE</code>
	 * constants defined for this class, this will need
	 * to be set manually.
	 */
	public int depth;
	
	/**
	 * Constructs a new resource of type <code>SICN</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public IconListResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
		autoSize();
	}
	
	/**
	 * Constructs a new resource of type <code>SICN</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public IconListResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
		autoSize();
	}
	
	/**
	 * Constructs a new resource of type <code>SICN</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public IconListResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
		autoSize();
	}
	
	/**
	 * Constructs a new resource of type <code>SICN</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public IconListResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
		autoSize();
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public IconListResource(int type, short id, byte[] data) {
		super(type, id, data);
		autoSize();
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, name, and data.
	 * All attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public IconListResource(int type, short id, String name, byte[] data) {
		super(type, id, name, data);
		autoSize();
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public IconListResource(int type, short id, byte attr, byte[] data) {
		super(type, id, attr, data);
		autoSize();
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, attributes, name, and data.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public IconListResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
		autoSize();
	}
	
	/**
	 * Sets the <code>width</code>, <code>height</code>, and <code>depth</code>
	 * appropriately according to the resource type.
	 */
	public void autoSize() {
		switch (type) {
		case RESOURCE_TYPE: width = height = 16; depth = 1; break;
		case RESOURCE_TYPE_32: width = height = 32; depth = 1; break;
		}
	}
	
	/**
	 * Returns the number of icons in this icon list.
	 * @return the number of icons in this icon list.
	 */
	public int getIconCount() {
		return (8*data.length) / (width*height*depth);
	}
	
	/**
	 * Returns an icon in this icon list as an IconResource.
	 * The returned IconResource has the same type, id, attributes,
	 * and name as this IconListResource, but has only the
	 * specified icon as its data. Also, the returned IconResource
	 * has the same width, height, and depth fields as this
	 * IconListResource, and no mask.
	 * <p>
	 * If an invalid index is specified, an empty icon
	 * is returned.
	 * @param idx the index of the desired icon, starting at zero.
	 * @return the desired icon as an IconResource object.
	 */
	public IconResource getIcon(int idx) {
		int size = (width*height*depth)/8;
		byte[] icon = new byte[size];
		for (int j=0, k=idx*size; j<icon.length && k<data.length; j++, k++) icon[j] = data[k];
		IconResource i = new IconResource(type, id, getAttributes(), name, icon);
		i.width = width; i.height = height; i.depth = depth; i.maskdepth = 0;
		return i;
	}
	
	/**
	 * Returns all the icons in this icon list as an array of IconResources.
	 * Each returned IconResource has the same id, attributes,
	 * and name as this IconListResource.
	 * @return the icons of this resource as an array of IconResources.
	 */
	public IconResource[] getIcons() {
		IconResource[] icons = new IconResource[getIconCount()];
		for (int i = 0; i < icons.length; i++) icons[i] = getIcon(i);
		return icons;
	}
}
