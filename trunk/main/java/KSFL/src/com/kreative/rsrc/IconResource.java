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

import java.awt.*;
import java.awt.image.*;
import com.kreative.ksfl.KSFLConstants;
import com.kreative.rsrc.pict.PICTUtilities;

/**
 * The <code>IconResource</code> class represents a variety of Mac OS icon resources.
 * (This does not include the 256x256 <code>ic08</code> and 512x512 <code>ic09</code>
 * types introduced with Mac OS X Leopard, since those require a separate JPEG2000 decoder.)
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class IconResource extends MacResource {
	/**
	 * The resource type of a Mac OS 32x32 black-and-white icon resource with no mask,
	 * the four-character constant <code>ICON</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.ICON;
	
	/**
	 * The resource type of a Mac OS 128x128 black-and-white icon resource with mask,
	 * the four-character constant <code>ict#</code>.
	 */
	public static final int RESOURCE_TYPE_THUMBNAIL_BW = KSFLConstants.ict$;
	/**
	 * The resource type of a Mac OS 128x128 16-color icon resource,
	 * the four-character constant <code>ict4</code>.
	 */
	public static final int RESOURCE_TYPE_THUMBNAIL_4BIT = KSFLConstants.ict4;
	/**
	 * The resource type of a Mac OS 128x128 256-color icon resource,
	 * the four-character constant <code>ict8</code>.
	 */
	public static final int RESOURCE_TYPE_THUMBNAIL_8BIT = KSFLConstants.ict8;
	/**
	 * The resource type of a Mac OS 128x128 32-bit icon resource,
	 * the four-character constant <code>it32</code>.
	 */
	public static final int RESOURCE_TYPE_THUMBNAIL_32BIT = KSFLConstants.it32;
	/**
	 * The resource type of a Mac OS 128x128 8-bit icon mask resource,
	 * the four-character constant <code>t8mk</code>.
	 */
	public static final int RESOURCE_TYPE_THUMBNAIL_MASK = KSFLConstants.t8mk;
	
	/**
	 * The resource type of a Mac OS 48x48 black-and-white icon resource with mask,
	 * the four-character constant <code>ich#</code>.
	 */
	public static final int RESOURCE_TYPE_HUGE_BW = KSFLConstants.ich$;
	/**
	 * The resource type of a Mac OS 48x48 16-color icon resource,
	 * the four-character constant <code>ich4</code>.
	 */
	public static final int RESOURCE_TYPE_HUGE_4BIT = KSFLConstants.ich4;
	/**
	 * The resource type of a Mac OS 48x48 256-color icon resource,
	 * the four-character constant <code>ich8</code>.
	 */
	public static final int RESOURCE_TYPE_HUGE_8BIT = KSFLConstants.ich8;
	/**
	 * The resource type of a Mac OS 48x48 32-bit icon resource,
	 * the four-character constant <code>ih32</code>.
	 */
	public static final int RESOURCE_TYPE_HUGE_32BIT = KSFLConstants.ih32;
	/**
	 * The resource type of a Mac OS 48x48 8-bit icon mask resource,
	 * the four-character constant <code>h8mk</code>.
	 */
	public static final int RESOURCE_TYPE_HUGE_MASK = KSFLConstants.h8mk;
	
	/**
	 * The resource type of a Mac OS 32x32 black-and-white icon resource with mask,
	 * the four-character constant <code>ICN#</code>.
	 */
	public static final int RESOURCE_TYPE_LARGE_BW = KSFLConstants.ICN$;
	/**
	 * The resource type of a Mac OS 32x32 16-color icon resource,
	 * the four-character constant <code>icl4</code>.
	 */
	public static final int RESOURCE_TYPE_LARGE_4BIT = KSFLConstants.icl4;
	/**
	 * The resource type of a Mac OS 32x32 256-color icon resource,
	 * the four-character constant <code>icl8</code>.
	 */
	public static final int RESOURCE_TYPE_LARGE_8BIT = KSFLConstants.icl8;
	/**
	 * The resource type of a Mac OS 32x32 32-bit icon resource,
	 * the four-character constant <code>il32</code>.
	 */
	public static final int RESOURCE_TYPE_LARGE_32BIT = KSFLConstants.il32;
	/**
	 * The resource type of a Mac OS 32x32 8-bit icon mask resource,
	 * the four-character constant <code>l8mk</code>.
	 */
	public static final int RESOURCE_TYPE_LARGE_MASK = KSFLConstants.l8mk;
	
	/**
	 * The resource type of a Mac OS 16x16 black-and-white icon resource with mask,
	 * the four-character constant <code>ics#</code>.
	 */
	public static final int RESOURCE_TYPE_SMALL_BW = KSFLConstants.ics$;
	/**
	 * The resource type of a Mac OS 16x16 16-color icon resource,
	 * the four-character constant <code>ics4</code>.
	 */
	public static final int RESOURCE_TYPE_SMALL_4BIT = KSFLConstants.ics4;
	/**
	 * The resource type of a Mac OS 16x16 256-color icon resource,
	 * the four-character constant <code>ics8</code>.
	 */
	public static final int RESOURCE_TYPE_SMALL_8BIT = KSFLConstants.ics8;
	/**
	 * The resource type of a Mac OS 16x16 32-bit icon resource,
	 * the four-character constant <code>is32</code>.
	 */
	public static final int RESOURCE_TYPE_SMALL_32BIT = KSFLConstants.is32;
	/**
	 * The resource type of a Mac OS 16x16 8-bit icon mask resource,
	 * the four-character constant <code>s8mk</code>.
	 */
	public static final int RESOURCE_TYPE_SMALL_MASK = KSFLConstants.s8mk;
	
	/**
	 * The resource type of a Mac OS 16x12 black-and-white icon resource with mask,
	 * the four-character constant <code>icm#</code>.
	 */
	public static final int RESOURCE_TYPE_MINI_BW = KSFLConstants.icm$;
	/**
	 * The resource type of a Mac OS 16x12 16-color icon resource,
	 * the four-character constant <code>icm4</code>.
	 */
	public static final int RESOURCE_TYPE_MINI_4BIT = KSFLConstants.icm4;
	/**
	 * The resource type of a Mac OS 16x12 256-color icon resource,
	 * the four-character constant <code>icm8</code>.
	 */
	public static final int RESOURCE_TYPE_MINI_8BIT = KSFLConstants.icm8;
	/**
	 * The resource type of a Mac OS 16x12 32-bit icon resource,
	 * the four-character constant <code>im32</code>.
	 */
	public static final int RESOURCE_TYPE_MINI_32BIT = KSFLConstants.im32;
	/**
	 * The resource type of a Mac OS 16x12 8-bit icon mask resource,
	 * the four-character constant <code>m8mk</code>.
	 */
	public static final int RESOURCE_TYPE_MINI_MASK = KSFLConstants.m8mk;
	
	/**
	 * The resource type of a Mac OS 512x512 JPEG2000-encoded icon resource,
	 * the four-character constant <code>ic09</code>.
	 * This format cannot be handled by <code>IconResource</code>, but its constant
	 * is listed here for completeness.
	 */
	public static final int RESOURCE_TYPE_JPEG2000_512 = KSFLConstants.ic09;
	/**
	 * The resource type of a Mac OS 256x256 JPEG2000-encoded icon resource,
	 * the four-character constant <code>ic08</code>.
	 * This format cannot be handled by <code>IconResource</code>, but its constant
	 * is listed here for completeness.
	 */
	public static final int RESOURCE_TYPE_JPEG2000_256 = KSFLConstants.ic08;
	/**
	 * The resource type of a Mac OS 128x128 JPEG2000-encoded icon resource,
	 * the four-character constant <code>ic07</code>.
	 * This format cannot be handled by <code>IconResource</code>, but its constant
	 * is listed here for completeness.
	 */
	public static final int RESOURCE_TYPE_JPEG2000_128 = KSFLConstants.ic07;
	/**
	 * The resource type of a Mac OS 64x64 JPEG2000-encoded icon resource,
	 * the four-character constant <code>ic06</code>.
	 * This format cannot be handled by <code>IconResource</code>, but its constant
	 * is listed here for completeness.
	 */
	public static final int RESOURCE_TYPE_JPEG2000_64 = KSFLConstants.ic06;
	/**
	 * The resource type of a Mac OS 32x32 JPEG2000-encoded icon resource,
	 * the four-character constant <code>ic05</code>.
	 * This format cannot be handled by <code>IconResource</code>, but its constant
	 * is listed here for completeness.
	 */
	public static final int RESOURCE_TYPE_JPEG2000_32 = KSFLConstants.ic05;
	/**
	 * The resource type of a Mac OS 16x16 JPEG2000-encoded icon resource,
	 * the four-character constant <code>ic04</code>.
	 * This format cannot be handled by <code>IconResource</code>, but its constant
	 * is listed here for completeness.
	 */
	public static final int RESOURCE_TYPE_JPEG2000_16 = KSFLConstants.ic04;
	
	/**
	 * The resource type of a Mac OS 16x16 black-and-white icon resource with mask for keyboard layouts,
	 * the four-character constant <code>kcs#</code>.
	 */
	public static final int RESOURCE_TYPE_KEYBOARD_BW = KSFLConstants.kcs$;
	/**
	 * The resource type of a Mac OS 16x16 16-color icon resource for keyboard layouts,
	 * the four-character constant <code>kcs4</code>.
	 */
	public static final int RESOURCE_TYPE_KEYBOARD_4BIT = KSFLConstants.kcs4;
	/**
	 * The resource type of a Mac OS 16x16 256-color icon resource for keyboard layouts,
	 * the four-character constant <code>kcs8</code>.
	 */
	public static final int RESOURCE_TYPE_KEYBOARD_8BIT = KSFLConstants.kcs8;
	
	/**
	 * Checks if a resource type is one this class knows how to handle.
	 * The default implementation is to always return true.
	 * It is recommended that subclasses override this.
	 * @param type A resource type to check.
	 * @return True if this class can handle this resource type, false otherwise.
	 */
	public static boolean isMyType(int type) {
		return (type == RESOURCE_TYPE
			|| type == RESOURCE_TYPE_THUMBNAIL_BW || type == RESOURCE_TYPE_THUMBNAIL_4BIT
			|| type == RESOURCE_TYPE_THUMBNAIL_8BIT || type == RESOURCE_TYPE_THUMBNAIL_32BIT
			|| type == RESOURCE_TYPE_THUMBNAIL_MASK
			|| type == RESOURCE_TYPE_HUGE_BW || type == RESOURCE_TYPE_HUGE_4BIT
			|| type == RESOURCE_TYPE_HUGE_8BIT || type == RESOURCE_TYPE_HUGE_32BIT
			|| type == RESOURCE_TYPE_HUGE_MASK
			|| type == RESOURCE_TYPE_LARGE_BW || type == RESOURCE_TYPE_LARGE_4BIT
			|| type == RESOURCE_TYPE_LARGE_8BIT || type == RESOURCE_TYPE_LARGE_32BIT
			|| type == RESOURCE_TYPE_LARGE_MASK
			|| type == RESOURCE_TYPE_SMALL_BW || type == RESOURCE_TYPE_SMALL_4BIT
			|| type == RESOURCE_TYPE_SMALL_8BIT || type == RESOURCE_TYPE_SMALL_32BIT
			|| type == RESOURCE_TYPE_SMALL_MASK
			|| type == RESOURCE_TYPE_MINI_BW || type == RESOURCE_TYPE_MINI_4BIT
			|| type == RESOURCE_TYPE_MINI_8BIT || type == RESOURCE_TYPE_MINI_32BIT
			|| type == RESOURCE_TYPE_MINI_MASK
			|| type == RESOURCE_TYPE_KEYBOARD_BW || type == RESOURCE_TYPE_KEYBOARD_4BIT
			|| type == RESOURCE_TYPE_KEYBOARD_8BIT
		);
	}
	
	/**
	 * Determines the appropriate resource type for an icon of the
	 * specified width, height, depth, and mask depth.
	 * If the resource type cannot be determined, this returns zero. 
	 * @param width the icon width.
	 * @param height the icon height.
	 * @param depth the depth of the icon image.
	 * @param maskdepth the depth of the icon mask.
	 * @return the appropriate resource type for that kind of icon.
	 */
	public static int getTypeFor(int width, int height, int depth, int maskdepth) {
		if (!(  (width == height) || (width == 16 && height == 12)  )) return 0;
		else if (depth == 0 && maskdepth == 0) {
			switch (height) {
			case 16: return RESOURCE_TYPE_JPEG2000_16;
			case 32: return RESOURCE_TYPE_JPEG2000_32;
			case 64: return RESOURCE_TYPE_JPEG2000_64;
			case 128: return RESOURCE_TYPE_JPEG2000_128;
			case 256: return RESOURCE_TYPE_JPEG2000_256;
			case 512: return RESOURCE_TYPE_JPEG2000_512;
			}
		}
		else if (depth == 1 && maskdepth == 1) {
			switch (height) {
			case 12: return RESOURCE_TYPE_MINI_BW;
			case 16: return RESOURCE_TYPE_SMALL_BW;
			case 32: return RESOURCE_TYPE_LARGE_BW;
			case 48: return RESOURCE_TYPE_HUGE_BW;
			case 128: return RESOURCE_TYPE_THUMBNAIL_BW;
			}
		}
		else if (depth == 0 && maskdepth == 8) {
			switch (height) {
			case 12: return RESOURCE_TYPE_MINI_MASK;
			case 16: return RESOURCE_TYPE_SMALL_MASK;
			case 32: return RESOURCE_TYPE_LARGE_MASK;
			case 48: return RESOURCE_TYPE_HUGE_MASK;
			case 128: return RESOURCE_TYPE_THUMBNAIL_MASK;
			}
		}
		else if (maskdepth == 0) {
			switch (depth) {
			case 1:
				switch (height) {
				case 32: return RESOURCE_TYPE;
				}
			case 4:
				switch (height) {
				case 12: return RESOURCE_TYPE_MINI_4BIT;
				case 16: return RESOURCE_TYPE_SMALL_4BIT;
				case 32: return RESOURCE_TYPE_LARGE_4BIT;
				case 48: return RESOURCE_TYPE_HUGE_4BIT;
				case 128: return RESOURCE_TYPE_THUMBNAIL_4BIT;
				}
			case 8:
				switch (height) {
				case 12: return RESOURCE_TYPE_MINI_8BIT;
				case 16: return RESOURCE_TYPE_SMALL_8BIT;
				case 32: return RESOURCE_TYPE_LARGE_8BIT;
				case 48: return RESOURCE_TYPE_HUGE_8BIT;
				case 128: return RESOURCE_TYPE_THUMBNAIL_8BIT;
				}
			case 32:
				switch (height) {
				case 12: return RESOURCE_TYPE_MINI_32BIT;
				case 16: return RESOURCE_TYPE_SMALL_32BIT;
				case 32: return RESOURCE_TYPE_LARGE_32BIT;
				case 48: return RESOURCE_TYPE_HUGE_32BIT;
				case 128: return RESOURCE_TYPE_THUMBNAIL_32BIT;
				}
			}
		}
		return 0;
	}
	
	/**
	 * The width of the icon.
	 * This is usually automatically determined from the
	 * resource type when the resource is created, but if
	 * the resource type is not one of the <code>RESOURCE_TYPE</code>
	 * constants defined for this class, this will need
	 * to be set manually.
	 */
	public int width = 0;
	/**
	 * The height of the icon.
	 * This is usually automatically determined from the
	 * resource type when the resource is created, but if
	 * the resource type is not one of the <code>RESOURCE_TYPE</code>
	 * constants defined for this class, this will need
	 * to be set manually.
	 */
	public int height = 0;
	/**
	 * The color depth of the icon image. Set to 0 if this is only a mask.
	 * This is usually automatically determined from the
	 * resource type when the resource is created, but if
	 * the resource type is not one of the <code>RESOURCE_TYPE</code>
	 * constants defined for this class, this will need
	 * to be set manually.
	 */
	public int depth = 0;
	/**
	 * The color depth of the icon mask. Set to 0 if this is only an image.
	 * This is usually automatically determined from the
	 * resource type when the resource is created, but if
	 * the resource type is not one of the <code>RESOURCE_TYPE</code>
	 * constants defined for this class, this will need
	 * to be set manually.
	 */
	public int maskdepth = 0;
	
	/**
	 * Constructs a new resource of type <code>ICON</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public IconResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
		autoSize();
	}
	
	/**
	 * Constructs a new resource of type <code>ICON</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public IconResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
		autoSize();
	}
	
	/**
	 * Constructs a new resource of type <code>ICON</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public IconResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
		autoSize();
	}
	
	/**
	 * Constructs a new resource of type <code>ICON</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public IconResource(short id, byte attr, String name, byte[] data) {
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
	public IconResource(int type, short id, byte[] data) {
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
	public IconResource(int type, short id, String name, byte[] data) {
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
	public IconResource(int type, short id, byte attr, byte[] data) {
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
	public IconResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
		autoSize();
	}
	
	/**
	 * Sets the <code>width</code>, <code>height</code>, <code>depth</code>,
	 * and <code>maskdepth</code> appropriately according to the resource
	 * type. If the resource type is not one of the <code>RESOURCE_TYPE</code>
	 * constants, nothing will change.
	 */
	public void autoSize() {
		switch (type) {
		case RESOURCE_TYPE: width = height = 32; depth = 1; maskdepth = 0; break;
		case RESOURCE_TYPE_THUMBNAIL_BW: width = height = 128; depth = 1; maskdepth = 1; break;
		case RESOURCE_TYPE_THUMBNAIL_4BIT: width = height = 128; depth = 4; maskdepth = 0; break;
		case RESOURCE_TYPE_THUMBNAIL_8BIT: width = height = 128; depth = 8; maskdepth = 0; break;
		case RESOURCE_TYPE_THUMBNAIL_32BIT: width = height = 128; depth = 32; maskdepth = 0; break;
		case RESOURCE_TYPE_THUMBNAIL_MASK: width = height = 128; depth = 0; maskdepth = 8; break;
		case RESOURCE_TYPE_HUGE_BW: width = height = 48; depth = 1; maskdepth = 1; break;
		case RESOURCE_TYPE_HUGE_4BIT: width = height = 48; depth = 4; maskdepth = 0; break;
		case RESOURCE_TYPE_HUGE_8BIT: width = height = 48; depth = 8; maskdepth = 0; break;
		case RESOURCE_TYPE_HUGE_32BIT: width = height = 48; depth = 32; maskdepth = 0; break;
		case RESOURCE_TYPE_HUGE_MASK: width = height = 48; depth = 0; maskdepth = 8; break;
		case RESOURCE_TYPE_LARGE_BW: width = height = 32; depth = 1; maskdepth = 1; break;
		case RESOURCE_TYPE_LARGE_4BIT: width = height = 32; depth = 4; maskdepth = 0; break;
		case RESOURCE_TYPE_LARGE_8BIT: width = height = 32; depth = 8; maskdepth = 0; break;
		case RESOURCE_TYPE_LARGE_32BIT: width = height = 32; depth = 32; maskdepth = 0; break;
		case RESOURCE_TYPE_LARGE_MASK: width = height = 32; depth = 0; maskdepth = 8; break;
		case RESOURCE_TYPE_SMALL_BW: width = height = 16; depth = 1; maskdepth = 1; break;
		case RESOURCE_TYPE_SMALL_4BIT: width = height = 16; depth = 4; maskdepth = 0; break;
		case RESOURCE_TYPE_SMALL_8BIT: width = height = 16; depth = 8; maskdepth = 0; break;
		case RESOURCE_TYPE_SMALL_32BIT: width = height = 16; depth = 32; maskdepth = 0; break;
		case RESOURCE_TYPE_SMALL_MASK: width = height = 16; depth = 0; maskdepth = 8; break;
		case RESOURCE_TYPE_MINI_BW: width = 16; height = 12; depth = 1; maskdepth = 1; break;
		case RESOURCE_TYPE_MINI_4BIT: width = 16; height = 12; depth = 4; maskdepth = 0; break;
		case RESOURCE_TYPE_MINI_8BIT: width = 16; height = 12; depth = 8; maskdepth = 0; break;
		case RESOURCE_TYPE_MINI_32BIT: width = 16; height = 12; depth = 32; maskdepth = 0; break;
		case RESOURCE_TYPE_MINI_MASK: width = 16; height = 12; depth = 0; maskdepth = 8; break;
		case RESOURCE_TYPE_JPEG2000_512: width = height = 512; depth = 0; maskdepth = 0; break;
		case RESOURCE_TYPE_JPEG2000_256: width = height = 256; depth = 0; maskdepth = 0; break;
		case RESOURCE_TYPE_JPEG2000_128: width = height = 128; depth = 0; maskdepth = 0; break;
		case RESOURCE_TYPE_JPEG2000_64: width = height = 64; depth = 0; maskdepth = 0; break;
		case RESOURCE_TYPE_JPEG2000_32: width = height = 32; depth = 0; maskdepth = 0; break;
		case RESOURCE_TYPE_JPEG2000_16: width = height = 16; depth = 0; maskdepth = 0; break;
		case RESOURCE_TYPE_KEYBOARD_BW: width = height = 16; depth = 1; maskdepth = 1; break;
		case RESOURCE_TYPE_KEYBOARD_4BIT: width = height = 16; depth = 4; maskdepth = 0; break;
		case RESOURCE_TYPE_KEYBOARD_8BIT: width = height = 16; depth = 8; maskdepth = 0; break;
		}
	}
	
	/**
	 * Returns the size of the icon image in the data array.
	 * If this resource only has a mask, or the depth of the icon image is not one of
	 * 1, 2, 4, 8, or 32, this returns zero.
	 * @return the size of the icon image in the data array.
	 */
	public int getImageSize() {
		switch (depth) {
		case 1:
			return (width*height)/8;
		case 2:
			return (width*height)/4;
		case 4:
			return (width*height)/2;
		case 8:
			return (width*height);
		case 32:
			int i, j;
			for (i=0, j=0; i<data.length && j<width*height*3; i++) {
				int run = data[i] & 0xFF;
				if (run < 128) {
					i += run+1;
					j += run+1;
				} else {
					i++;
					j += run-125;
				}
			}
			return i;
		default:
			return 0;
		}
	}
	
	/**
	 * Returns the size of the icon mask in the data array.
	 * If this resource only has an image, or the depth of the icon mask is not one of
	 * 1, 2, 4, or 8, this returns zero.
	 * @return the size of the icon mask in the data array.
	 */
	public int getMaskSize() {
		switch (maskdepth) {
		case 1:
			return (width*height)/8;
		case 2:
			return (width*height)/4;
		case 4:
			return (width*height)/2;
		case 8:
			return (width*height);
		default:
			return 0;
		}
	}
	
	/**
	 * Creates an AWT image of this icon's image.
	 * If this resource only has a mask, or the depth of the icon image is not one of
	 * 1, 2, 4, 8, or 32, this returns <code>null</code>.
	 * @return the icon image.
	 */
	public Image getImage() {
		if (depth == 32) {
			byte[] stuff = new byte[width*height*3];
			int[] pixels = new int[width*height];
			int i,j,k;
			for (i=0, j=0; i<data.length && j<stuff.length; i++) {
				int run = data[i] & 0xFF;
				if (run < 128) {
					for (k=0; k<=run && j<stuff.length; k++) stuff[j++] = data[++i];
				} else {
					i++;
					for (k=0; k<(run-125) && j<stuff.length; k++) stuff[j++] = data[i];
				}
			}
			for (i=0, j=width*height, k=width*height*2; i<pixels.length; i++, j++, k++) {
				int r = stuff[i] & 0xFF;
				int g = stuff[j] & 0xFF;
				int b = stuff[k] & 0xFF;
				pixels[i] = (0xFF000000 | (r << 16) | (g << 8) | b);
			}
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			img.setRGB(0, 0, width, height, pixels, 0, width);
			return img;
		} else {
			int[] pixels = new int[width*height];
			switch (depth) {
			case 1:
				for (int i=0, j=0; i<data.length && j<pixels.length; i++, j+=8) {
					pixels[j+0] = PICTUtilities.COLORS_1BIT[(data[i] & 0x80) >> 7];
					pixels[j+1] = PICTUtilities.COLORS_1BIT[(data[i] & 0x40) >> 6];
					pixels[j+2] = PICTUtilities.COLORS_1BIT[(data[i] & 0x20) >> 5];
					pixels[j+3] = PICTUtilities.COLORS_1BIT[(data[i] & 0x10) >> 4];
					pixels[j+4] = PICTUtilities.COLORS_1BIT[(data[i] & 0x08) >> 3];
					pixels[j+5] = PICTUtilities.COLORS_1BIT[(data[i] & 0x04) >> 2];
					pixels[j+6] = PICTUtilities.COLORS_1BIT[(data[i] & 0x02) >> 1];
					pixels[j+7] = PICTUtilities.COLORS_1BIT[(data[i] & 0x01) >> 0];
				}
				break;
			case 2:
				for (int i=0, j=0; i<data.length && j<pixels.length; i++, j+=4) {
					pixels[j+0] = PICTUtilities.COLORS_2BIT[(data[i] & 0xC0) >> 6];
					pixels[j+1] = PICTUtilities.COLORS_2BIT[(data[i] & 0x30) >> 4];
					pixels[j+2] = PICTUtilities.COLORS_2BIT[(data[i] & 0x0C) >> 2];
					pixels[j+3] = PICTUtilities.COLORS_2BIT[(data[i] & 0x03) >> 0];
				}
				break;
			case 4:
				for (int i=0, j=0; i<data.length && j<pixels.length; i++, j+=2) {
					pixels[j+0] = PICTUtilities.COLORS_4BIT[(data[i] & 0xF0) >> 4];
					pixels[j+1] = PICTUtilities.COLORS_4BIT[(data[i] & 0x0F) >> 0];
				}
				break;
			case 8:
				for (int i=0, j=0; i<data.length && j<pixels.length; i++, j++) {
					pixels[j] = PICTUtilities.COLORS_8BIT[data[i] & 0xFF];
				}
				break;
			default:
				return null;
			}
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			img.setRGB(0, 0, width, height, pixels, 0, width);
			return img;
		}
	}
	
	/**
	 * Creates an AWT image of this icon's image with a mask.
	 * If this resource only has a mask, or the depth of the icon image is not one of
	 * 1, 2, 4, 8, or 32, this returns <code>null</code>. If the given IconResource
	 * only has an image, or the depth of the mask is not one of 1, 2, 4, or 8, this
	 * returns <code>null</code>. If this icon's width and height do not match the
	 * given IconResource's width and height, this returns <code>null</code>.
	 * @return the icon image.
	 */
	public Image getImageWithMask(IconResource mask) {
		if (width != mask.width && height != mask.height) return null;
		int[] alphas = mask.getMaskAlphaValues();
		if (alphas == null || alphas.length < width*height) return null;
		if (depth == 32) {
			byte[] stuff = new byte[width*height*3];
			int[] pixels = new int[width*height];
			int i,j,k;
			for (i=0, j=0; i<data.length && j<stuff.length; i++) {
				int run = data[i] & 0xFF;
				if (run < 128) {
					for (k=0; k<=run && j<stuff.length; k++) stuff[j++] = data[++i];
				} else {
					i++;
					for (k=0; k<(run-125) && j<stuff.length; k++) stuff[j++] = data[i];
				}
			}
			for (i=0, j=width*height, k=width*height*2; i<pixels.length; i++, j++, k++) {
				int r = stuff[i] & 0xFF;
				int g = stuff[j] & 0xFF;
				int b = stuff[k] & 0xFF;
				pixels[i] = ((alphas[i] << 24) | (r << 16) | (g << 8) | b);
			}
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			img.setRGB(0, 0, width, height, pixels, 0, width);
			return img;
		} else {
			int[] pixels = new int[width*height];
			switch (depth) {
			case 1:
				for (int i=0, j=0; i<data.length && j<pixels.length; i++, j+=8) {
					pixels[j+0] = PICTUtilities.COLORS_1BIT[(data[i] & 0x80) >> 7];
					pixels[j+1] = PICTUtilities.COLORS_1BIT[(data[i] & 0x40) >> 6];
					pixels[j+2] = PICTUtilities.COLORS_1BIT[(data[i] & 0x20) >> 5];
					pixels[j+3] = PICTUtilities.COLORS_1BIT[(data[i] & 0x10) >> 4];
					pixels[j+4] = PICTUtilities.COLORS_1BIT[(data[i] & 0x08) >> 3];
					pixels[j+5] = PICTUtilities.COLORS_1BIT[(data[i] & 0x04) >> 2];
					pixels[j+6] = PICTUtilities.COLORS_1BIT[(data[i] & 0x02) >> 1];
					pixels[j+7] = PICTUtilities.COLORS_1BIT[(data[i] & 0x01) >> 0];
				}
				break;
			case 2:
				for (int i=0, j=0; i<data.length && j<pixels.length; i++, j+=4) {
					pixels[j+0] = PICTUtilities.COLORS_2BIT[(data[i] & 0xC0) >> 6];
					pixels[j+1] = PICTUtilities.COLORS_2BIT[(data[i] & 0x30) >> 4];
					pixels[j+2] = PICTUtilities.COLORS_2BIT[(data[i] & 0x0C) >> 2];
					pixels[j+3] = PICTUtilities.COLORS_2BIT[(data[i] & 0x03) >> 0];
				}
				break;
			case 4:
				for (int i=0, j=0; i<data.length && j<pixels.length; i++, j+=2) {
					pixels[j+0] = PICTUtilities.COLORS_4BIT[(data[i] & 0xF0) >> 4];
					pixels[j+1] = PICTUtilities.COLORS_4BIT[(data[i] & 0x0F) >> 0];
				}
				break;
			case 8:
				for (int i=0, j=0; i<data.length && j<pixels.length; i++, j++) {
					pixels[j] = PICTUtilities.COLORS_8BIT[data[i] & 0xFF];
				}
				break;
			default:
				return null;
			}
			for (int i = 0; i < pixels.length; i++) {
				pixels[i] = ((alphas[i] << 24) | (pixels[i] & 0xFFFFFF));
			}
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			img.setRGB(0, 0, width, height, pixels, 0, width);
			return img;
		}
	}
	
	/**
	 * Creates an array of integers from 0 to 255 from the icon mask.
	 * This can be used to find the proper alpha values for each pixel
	 * of the icon image.
	 * If this resource only has an image, or the depth of the icon mask is not one of
	 * 1, 2, 4, or 8, this returns <code>null</code>.
	 * @return the icon mask as an array of integers.
	 */
	public int[] getMaskAlphaValues() {
		int[] alphas = new int[width*height];
		int o = getImageSize();
		switch (maskdepth) {
		case 1:
			for (int i=o, j=0; i<data.length && j<alphas.length; i++, j+=8) {
				alphas[j+0] = ((data[i] & 0x80) >> 7) * 255;
				alphas[j+1] = ((data[i] & 0x40) >> 6) * 255;
				alphas[j+2] = ((data[i] & 0x20) >> 5) * 255;
				alphas[j+3] = ((data[i] & 0x10) >> 4) * 255;
				alphas[j+4] = ((data[i] & 0x08) >> 3) * 255;
				alphas[j+5] = ((data[i] & 0x04) >> 2) * 255;
				alphas[j+6] = ((data[i] & 0x02) >> 1) * 255;
				alphas[j+7] = ((data[i] & 0x01) >> 0) * 255;
			}
			break;
		case 2:
			for (int i=o, j=0; i<data.length && j<alphas.length; i++, j+=4) {
				alphas[j+0] = ((data[i] & 0xC0) >> 6) * 85;
				alphas[j+1] = ((data[i] & 0x30) >> 4) * 85;
				alphas[j+2] = ((data[i] & 0x0C) >> 2) * 85;
				alphas[j+3] = ((data[i] & 0x03) >> 0) * 85;
			}
			break;
		case 4:
			for (int i=o, j=0; i<data.length && j<alphas.length; i++, j+=2) {
				alphas[j+0] = ((data[i] & 0xF0) >> 4) * 17;
				alphas[j+1] = ((data[i] & 0x0F) >> 0) * 17;
			}
			break;
		case 8:
			for (int i=o, j=0; i<data.length && j<alphas.length; i++, j++) {
				alphas[j] = (data[i] & 0xFF);
			}
			break;
		default:
			return null;
		}
		return alphas;
	}
	
	/**
	 * Creates an AWT image of this icon's mask.
	 * If this resource only has an image, or the depth of the icon mask is not one of
	 * 1, 2, 4, or 8, this returns <code>null</code>.
	 * @return the icon mask.
	 */
	public Image getMask() {
		int[] pixels = new int[width*height];
		int o = getImageSize();
		switch (maskdepth) {
		case 1:
			for (int i=o, j=0; i<data.length && j<pixels.length; i++, j+=8) {
				pixels[j+0] = (((data[i] & 0x80) >> 7) * 255) << 24;
				pixels[j+1] = (((data[i] & 0x40) >> 6) * 255) << 24;
				pixels[j+2] = (((data[i] & 0x20) >> 5) * 255) << 24;
				pixels[j+3] = (((data[i] & 0x10) >> 4) * 255) << 24;
				pixels[j+4] = (((data[i] & 0x08) >> 3) * 255) << 24;
				pixels[j+5] = (((data[i] & 0x04) >> 2) * 255) << 24;
				pixels[j+6] = (((data[i] & 0x02) >> 1) * 255) << 24;
				pixels[j+7] = (((data[i] & 0x01) >> 0) * 255) << 24;
			}
			break;
		case 2:
			for (int i=o, j=0; i<data.length && j<pixels.length; i++, j+=4) {
				pixels[j+0] = (((data[i] & 0xC0) >> 6) * 85) << 24;
				pixels[j+1] = (((data[i] & 0x30) >> 4) * 85) << 24;
				pixels[j+2] = (((data[i] & 0x0C) >> 2) * 85) << 24;
				pixels[j+3] = (((data[i] & 0x03) >> 0) * 85) << 24;
			}
			break;
		case 4:
			for (int i=o, j=0; i<data.length && j<pixels.length; i++, j+=2) {
				pixels[j+0] = (((data[i] & 0xF0) >> 4) * 17) << 24;
				pixels[j+1] = (((data[i] & 0x0F) >> 0) * 17) << 24;
			}
			break;
		case 8:
			for (int i=o, j=0; i<data.length && j<pixels.length; i++, j++) {
				pixels[j] = (data[i] & 0xFF) << 24;
			}
			break;
		default:
			return null;
		}
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, width, height, pixels, 0, width);
		return img;
	}
}
