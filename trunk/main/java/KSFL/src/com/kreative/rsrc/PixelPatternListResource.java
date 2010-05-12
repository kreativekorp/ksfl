/*
 * Copyright &copy; 2007-2010 Rebecca G. Bettencourt / Kreative Software
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
 * The <code>RPixelPatternList</code> class represents a Mac OS color pattern
 * (pixel pattern) list resource.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class PixelPatternListResource extends MacResource {
	/**
	 * The resource type of a Mac OS pixel pattern list resource,
	 * the four-character constant <code>ppt#</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.ppt$;
	
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
	 * Constructs a new resource of type <code>ppt#</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public PixelPatternListResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
	}
	
	/**
	 * Constructs a new resource of type <code>ppt#</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public PixelPatternListResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
	}
	
	/**
	 * Constructs a new resource of type <code>ppt#</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public PixelPatternListResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
	}
	
	/**
	 * Constructs a new resource of type <code>ppt#</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public PixelPatternListResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public PixelPatternListResource(int type, short id, byte[] data) {
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
	public PixelPatternListResource(int type, short id, String name, byte[] data) {
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
	public PixelPatternListResource(int type, short id, byte attr, byte[] data) {
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
	public PixelPatternListResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
	}
	
	/**
	 * Returns the number of patterns in this pattern list.
	 * @return the number of patterns in this pattern list.
	 */
	public int getPatternCount() {
		return KSFLUtilities.getShort(data, 0);
	}
	
	/**
	 * Returns the offset to a pattern from the beginning of this resource.
	 * @param idx the index of the pattern.
	 * @return the offset where the pattern starts.
	 */
	public int getPatternOffset(int idx) {
		return KSFLUtilities.getInt(data, 2+idx*4);
	}
	
	/**
	 * Returns a pattern in this pattern list as a PixelPatternResource.
	 * The returned PixelPatternResource has the same id, attributes,
	 * and name as this PixelPatternListResource, but has a type of
	 * <code>ppat</code> and only the specified pattern as
	 * its data.
	 * @param idx the index of the desired pattern, starting at zero.
	 * @return the desired pattern as a PixelPatternResource object.
	 */
	public PixelPatternResource getPattern(int idx) {
		int o = getPatternOffset(idx);
		byte[] pat = new byte[data.length-o];
		for (int j=0, k=o; j<pat.length && k<data.length; j++, k++) pat[j] = data[k];
		return new PixelPatternResource(id, getAttributes(), name, pat);
	}
	
	/**
	 * Returns all the patterns in this pattern list as an array of PixelPatternResources.
	 * Each returned PixelPatternResource has the same id, attributes,
	 * and name as this PixelPatternResourceList, but has a type of
	 * <code>PAT </code>.
	 * @return the patterns of this resource as an array of PixelPatternResources.
	 */
	public PixelPatternResource[] getPatterns() {
		PixelPatternResource[] pats = new PixelPatternResource[getPatternCount()];
		for (int i = 0; i < pats.length; i++) pats[i] = getPattern(i);
		return pats;
	}
}
