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

import java.awt.Color;
import com.kreative.ksfl.*;

/**
 * The <code>ColorPaletteResource</code> class represents a
 * Mac OS color palette resource.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class ColorPaletteResource extends MacResource {
	/**
	 * The resource type of a Mac OS color palette resource,
	 * the four-character constant <code>pltt</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.pltt;
	
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
	 * Constructs a new resource of type <code>pltt</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public ColorPaletteResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
	}
	
	/**
	 * Constructs a new resource of type <code>pltt</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public ColorPaletteResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
	}
	
	/**
	 * Constructs a new resource of type <code>pltt</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public ColorPaletteResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
	}
	
	/**
	 * Constructs a new resource of type <code>pltt</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public ColorPaletteResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public ColorPaletteResource(int type, short id, byte[] data) {
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
	public ColorPaletteResource(int type, short id, String name, byte[] data) {
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
	public ColorPaletteResource(int type, short id, byte attr, byte[] data) {
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
	public ColorPaletteResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
	}
	
	public static final short USAGE_COURTEOUS = 0x0000;
	public static final short USAGE_DITHERED = 0x0001;
	public static final short USAGE_TOLERANT = 0x0002;
	public static final short USAGE_ANIMATED = 0x0004;
	public static final short USAGE_EXPLICIT = 0x0008;
	public static final short USAGE_INHIBIT_2BIT_GRAY = 0x0100;
	public static final short USAGE_INHIBIT_2BIT_COLOR = 0x0200;
	public static final short USAGE_INHIBIT_4BIT_GRAY = 0x0400;
	public static final short USAGE_INHIBIT_4BIT_COLOR = 0x0800;
	public static final short USAGE_INHIBIT_8BIT_GRAY = 0x1000;
	public static final short USAGE_INHIBIT_8BIT_COLOR = 0x2000;
	
	/**
	 * Returns the number of colors in this color palette.
	 * @return the number of colors in this color palette.
	 */
	public int getColorCount() {
		return KSFLUtilities.getShort(data, 0) & 0xFFFF;
	}
	
	/**
	 * Returns the red, green, and blue components of the color
	 * at the specified array index
	 * in the range of 0-255.
	 * <p>
	 * Following the red, green, and blue components will be
	 * the usage flags, tolerance, and other flags of this color,
	 * in the range of 0-65536.
	 * <p>
	 * If the array index is out of range (negative or greater or equal to
	 * <code>getColorCount()</code>), an exception will be thrown.
	 * @param ai the array index.
	 * @return the components of that color.
	 */
	public int[] getColor8(int ai) {
		return new int[]{
				(KSFLUtilities.getShort(data, 16+ai*16+0) & 0xFFFF)/257, //red
				(KSFLUtilities.getShort(data, 16+ai*16+2) & 0xFFFF)/257, //green
				(KSFLUtilities.getShort(data, 16+ai*16+4) & 0xFFFF)/257 //blue
		};
	}
	
	/**
	 * Returns the red, green, and blue components of the color
	 * at the specified array index
	 * in the range of 0-65536.
	 * <p>
	 * Following the red, green, and blue components will be
	 * the usage flags, tolerance, and other flags of this color,
	 * in the range of 0-65536.
	 * <p>
	 * This is the native representation.
	 * <p>
	 * If the array index is out of range (negative or greater or equal to
	 * <code>getColorCount()</code>), an exception will be thrown.
	 * @param ai the array index.
	 * @return the components of that color.
	 */
	public int[] getColor16(int ai) {
		return new int[]{
				KSFLUtilities.getShort(data, 16+ai*16+0) & 0xFFFF, //red
				KSFLUtilities.getShort(data, 16+ai*16+2) & 0xFFFF, //green
				KSFLUtilities.getShort(data, 16+ai*16+4) & 0xFFFF //blue
		};
	}
	
	/**
	 * Returns the red, green, and blue components of the color
	 * at the specified array index
	 * in the range of 0.0-1.0.
	 * <p>
	 * No extra information (flags, etc.) will be returned.
	 * <p>
	 * If the array index is out of range (negative or greater or equal to
	 * <code>getColorCount()</code>), an exception will be thrown.
	 * @param ai the array index.
	 * @return the components of that color.
	 */
	public float[] getColorFloats(int ai) {
		return new float[]{
				(KSFLUtilities.getShort(data, 16+ai*16+0) & 0xFFFF)/65535.0f,
				(KSFLUtilities.getShort(data, 16+ai*16+2) & 0xFFFF)/65535.0f,
				(KSFLUtilities.getShort(data, 16+ai*16+4) & 0xFFFF)/65535.0f
		};
	}
	
	/**
	 * Returns the color
	 * at the specified array index
	 * as a <code>java.awt.Color</code>.
	 * <p>
	 * No extra information (flags, etc.) will be returned.
	 * <p>
	 * If the array index is out of range (negative or greater or equal to
	 * <code>getColorCount()</code>), an exception will be thrown.
	 * @param ai the array index.
	 * @return the color.
	 */
	public Color getColor(int ai) {
		return new Color(
				(KSFLUtilities.getShort(data, 16+ai*16+0) & 0xFFFF)/65535.0f,
				(KSFLUtilities.getShort(data, 16+ai*16+2) & 0xFFFF)/65535.0f,
				(KSFLUtilities.getShort(data, 16+ai*16+4) & 0xFFFF)/65535.0f
		);
	}
	
	/**
	 * Returns all the colors in this color table as an array of <code>java.awt.Color</code>s.
	 * @return all the colors in this color table in an array.
	 */
	public Color[] getColors() {
		int cc = KSFLUtilities.getShort(data, 0) & 0xFFFF;
		Color[] ret = new Color[cc];
		for (int i=0, p=16; i<cc && p<data.length; i++, p+=16) {
			ret[i] = new Color(
					(KSFLUtilities.getShort(data, p+0) & 0xFFFF)/65535.0f,
					(KSFLUtilities.getShort(data, p+2) & 0xFFFF)/65535.0f,
					(KSFLUtilities.getShort(data, p+4) & 0xFFFF)/65535.0f
			);
		}
		return ret;
	}
}
