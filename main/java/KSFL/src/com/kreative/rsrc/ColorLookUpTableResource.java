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
import java.util.*;
import com.kreative.ksfl.*;

/**
 * The <code>ColorLookUpTableResource</code> class represents a
 * Mac OS color lookup table resource.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class ColorLookUpTableResource extends MacResource {
	/**
	 * The resource type of a Mac OS color lookup table resource,
	 * the four-character constant <code>clut</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.clut;
	
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
	 * Constructs a new resource of type <code>clut</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public ColorLookUpTableResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
	}
	
	/**
	 * Constructs a new resource of type <code>clut</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public ColorLookUpTableResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
	}
	
	/**
	 * Constructs a new resource of type <code>clut</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public ColorLookUpTableResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
	}
	
	/**
	 * Constructs a new resource of type <code>clut</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public ColorLookUpTableResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public ColorLookUpTableResource(int type, short id, byte[] data) {
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
	public ColorLookUpTableResource(int type, short id, String name, byte[] data) {
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
	public ColorLookUpTableResource(int type, short id, byte attr, byte[] data) {
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
	public ColorLookUpTableResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
	}
	
	/**
	 * Flag bit that indicates this color table is for a device, as opposed to a pixmap.
	 */
	public static final short FLAG_FOR_DEVICE = (short)0x8000;
	
	/**
	 * Returns the seed for this color table.
	 * @return the seed for this color table.
	 */
	public int getSeed() {
		return KSFLUtilities.getInt(data, 0);
	}
	
	/**
	 * Returns the flag bits for this color table.
	 * @return the flag bits for this color table.
	 */
	public short getFlags() {
		return KSFLUtilities.getShort(data, 4);
	}
	
	/**
	 * Returns the number of colors in this color table.
	 * @return the number of colors in this color table.
	 */
	public int getColorCount() {
		return (KSFLUtilities.getShort(data, 6)+1) & 0xFFFF;
	}
	
	/**
	 * Returns the pixel index of the color at the specified array index in this color table.
	 * If the array index is out of range (negative or greater or equal to
	 * <code>getColorCount()</code>), an exception will be thrown.
	 * @param aIndex the array index.
	 * @return the pixel index.
	 */
	public int arrayIndexToPixelIndex(int aIndex) {
		return KSFLUtilities.getShort(data, 8+aIndex*8) & 0xFFFF;
	}
	
	/**
	 * Returns the array index of the color with the specified pixel index.
	 * If no color with the specified pixel index exists, -1 is returned.
	 * @param pIndex the pixel index.
	 * @return the array index, or -1 if no color with the specified pixel index exists.
	 */
	public int pixelIndexToArrayIndex(int pIndex) {
		for (int i=0, p=8; p<data.length; i++, p+=8) {
			if (KSFLUtilities.getShort(data, p) == (short)pIndex) return i;
		}
		return -1;
	}
	
	/**
	 * Returns the red, green, and blue components of the color
	 * at the specified array index
	 * in the range of 0-255.
	 * If the array index is out of range (negative or greater or equal to
	 * <code>getColorCount()</code>), an exception will be thrown.
	 * @param ai the array index.
	 * @return the components of that color.
	 */
	public int[] getColor8ByArrayIndex(int ai) {
		return new int[]{
				(KSFLUtilities.getShort(data, 8+ai*8+2) & 0xFFFF)/257,
				(KSFLUtilities.getShort(data, 8+ai*8+4) & 0xFFFF)/257,
				(KSFLUtilities.getShort(data, 8+ai*8+6) & 0xFFFF)/257
		};
	}
	
	/**
	 * Returns the red, green, and blue components of the color
	 * at the specified array index
	 * in the range of 0-65536.
	 * This is the native representation.
	 * If the array index is out of range (negative or greater or equal to
	 * <code>getColorCount()</code>), an exception will be thrown.
	 * @param ai the array index.
	 * @return the components of that color.
	 */
	public int[] getColor16ByArrayIndex(int ai) {
		return new int[]{
				KSFLUtilities.getShort(data, 8+ai*8+2) & 0xFFFF,
				KSFLUtilities.getShort(data, 8+ai*8+4) & 0xFFFF,
				KSFLUtilities.getShort(data, 8+ai*8+6) & 0xFFFF
		};
	}
	
	/**
	 * Returns the red, green, and blue components of the color
	 * at the specified array index
	 * in the range of 0.0-1.0.
	 * If the array index is out of range (negative or greater or equal to
	 * <code>getColorCount()</code>), an exception will be thrown.
	 * @param ai the array index.
	 * @return the components of that color.
	 */
	public float[] getColorFloatsByArrayIndex(int ai) {
		return new float[]{
				(KSFLUtilities.getShort(data, 8+ai*8+2) & 0xFFFF)/65535.0f,
				(KSFLUtilities.getShort(data, 8+ai*8+4) & 0xFFFF)/65535.0f,
				(KSFLUtilities.getShort(data, 8+ai*8+6) & 0xFFFF)/65535.0f
		};
	}
	
	/**
	 * Returns the color
	 * at the specified array index
	 * as a <code>java.awt.Color</code>.
	 * If the array index is out of range (negative or greater or equal to
	 * <code>getColorCount()</code>), an exception will be thrown.
	 * @param ai the array index.
	 * @return the color.
	 */
	public Color getColorByArrayIndex(int ai) {
		return new Color(
				(KSFLUtilities.getShort(data, 8+ai*8+2) & 0xFFFF)/65535.0f,
				(KSFLUtilities.getShort(data, 8+ai*8+4) & 0xFFFF)/65535.0f,
				(KSFLUtilities.getShort(data, 8+ai*8+6) & 0xFFFF)/65535.0f
		);
	}
	
	/**
	 * Returns the red, green, and blue components of the color
	 * with the specified pixel index
	 * in the range of 0-255.
	 * If no color with the specified pixel index exists,
	 * an exception will be thrown.
	 * @param pi the pixel index.
	 * @return the components of that color.
	 */
	public int[] getColor8ByPixelIndex(int pi) {
		return getColor8ByArrayIndex(pixelIndexToArrayIndex(pi));
	}
	
	/**
	 * Returns the red, green, and blue components of the color
	 * with the specified pixel index
	 * in the range of 0-65536.
	 * This is the native representation.
	 * If no color with the specified pixel index exists,
	 * an exception will be thrown.
	 * @param pi the pixel index.
	 * @return the components of that color.
	 */
	public int[] getColor16ByPixelIndex(int pi) {
		return getColor16ByArrayIndex(pixelIndexToArrayIndex(pi));
	}
	
	/**
	 * Returns the red, green, and blue components of the color
	 * with the specified pixel index
	 * in the range of 0.0-1.0.
	 * If no color with the specified pixel index exists,
	 * an exception will be thrown.
	 * @param pi the pixel index.
	 * @return the components of that color.
	 */
	public float[] getColorFloatsByPixelIndex(int pi) {
		return getColorFloatsByArrayIndex(pixelIndexToArrayIndex(pi));
	}
	
	/**
	 * Returns the color
	 * with the specified pixel index
	 * as a <code>java.awt.Color</code>.
	 * If no color with the specified pixel index exists,
	 * an exception will be thrown.
	 * @param pi the pixel index.
	 * @return the color.
	 */
	public Color getColorByPixelIndex(int pi) {
		return getColorByArrayIndex(pixelIndexToArrayIndex(pi));
	}
	
	/**
	 * Returns all the colors in this color table as an array of <code>java.awt.Color</code>s.
	 * @return all the colors in this color table in an array.
	 */
	public Color[] getColorsByArrayIndex() {
		int cc = (KSFLUtilities.getShort(data, 6)+1) & 0xFFFF;
		Color[] ret = new Color[cc];
		for (int i=0, p=8; i<cc && p<data.length; i++, p+=8) {
			ret[i] = new Color(
					(KSFLUtilities.getShort(data, p+2) & 0xFFFF)/65535.0f,
					(KSFLUtilities.getShort(data, p+4) & 0xFFFF)/65535.0f,
					(KSFLUtilities.getShort(data, p+6) & 0xFFFF)/65535.0f
			);
		}
		return ret;
	}
	
	/**
	 * Returns all the colors in this color table as a map of pixel indices
	 * to <code>java.awt.Color</code>s.
	 * @return all the colors in this color table in a map.
	 */
	public Map<Integer,Color> getColorsByPixelIndex() {
		int cc = (KSFLUtilities.getShort(data, 6)+1) & 0xFFFF;
		Map<Integer,Color> ret = new HashMap<Integer,Color>();
		for (int i=0, p=8; i<cc && p<data.length; i++, p+=8) {
			ret.put(KSFLUtilities.getShort(data, p) & 0xFFFF, new Color(
					(KSFLUtilities.getShort(data, p+2) & 0xFFFF)/65535.0f,
					(KSFLUtilities.getShort(data, p+4) & 0xFFFF)/65535.0f,
					(KSFLUtilities.getShort(data, p+6) & 0xFFFF)/65535.0f
			));
		}
		return ret;
	}
}
