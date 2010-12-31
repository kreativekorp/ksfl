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
import java.awt.Point;
import java.awt.image.*;
import java.io.*;
import com.kreative.ksfl.*;
import com.kreative.rsrc.pict.*;

/**
 * The <code>ColorCursorResource</code> class represents a Mac OS color cursor resource.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class ColorCursorResource extends MacResource {
	/**
	 * The resource type of a Mac OS color cursor resource,
	 * the four-character constant <code>crsr</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.crsr;
	
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
	 * Constructs a new resource of type <code>crsr</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public ColorCursorResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
	}
	
	/**
	 * Constructs a new resource of type <code>crsr</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public ColorCursorResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
	}
	
	/**
	 * Constructs a new resource of type <code>crsr</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public ColorCursorResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
	}
	
	/**
	 * Constructs a new resource of type <code>crsr</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public ColorCursorResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public ColorCursorResource(int type, short id, byte[] data) {
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
	public ColorCursorResource(int type, short id, String name, byte[] data) {
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
	public ColorCursorResource(int type, short id, byte attr, byte[] data) {
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
	public ColorCursorResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
	}
	
	/**
	 * The cursor type indicating this cursor is in black and white.
	 */
	public static final short CURSOR_TYPE_BW = (short)0x8000;
	
	/**
	 * The cursor type indicating this cursor is in color.
	 */
	public static final short CURSOR_TYPE_COLOR = (short)0x8001;
	
	/**
	 * Returns the type of this cursor.
	 * @return the type of this cursor.
	 */
	public short getCursorType() {
		return KSFLUtilities.getShort(data, 0);
	}
	
	/**
	 * Returns the offset to the pixmap record from the beginning of this resource.
	 * @return the offset to the pixmap record from the beginning of this resource.
	 */
	public int getPixmapRcdOffset() {
		return KSFLUtilities.getInt(data, 2);
	}
	
	/**
	 * Returns the offset to the pixmap data from the beginning of this resource.
	 * @return the offset to the pixmap data from the beginning of this resource.
	 */
	public int getImageOffset() {
		return KSFLUtilities.getInt(data, 6);
	}
	
	/**
	 * Returns the offset to the expanded data from the beginning of this resource.
	 * Usually there is no expanded data, and this is zero.
	 * @return the offset to the expanded data from the beginning of this resource.
	 */
	public int getExpandedDataOffset() {
		return KSFLUtilities.getInt(data, 10);
	}
	
	/**
	 * Returns the bit depth of the expanded data.
	 * Usually there is no expanded data, and this is zero.
	 * @return the bit depth of the expanded data.
	 */
	public short getExpandedDataBitDepth() {
		return KSFLUtilities.getShort(data, 14);
	}
	
	/**
	 * Creates an AWT image of this cursor's B&W image.
	 * @return the B&W image.
	 */
	public Image getBWImage() {
		int[] pixels = new int[512];
		for (int i=20, j=0; i<84 && j<512; i++, j+=8) {
			pixels[j+0] = PICTUtilities.COLORS_1BIT[(data[i] & 0x80) >> 7];
			pixels[j+1] = PICTUtilities.COLORS_1BIT[(data[i] & 0x40) >> 6];
			pixels[j+2] = PICTUtilities.COLORS_1BIT[(data[i] & 0x20) >> 5];
			pixels[j+3] = PICTUtilities.COLORS_1BIT[(data[i] & 0x10) >> 4];
			pixels[j+4] = PICTUtilities.COLORS_1BIT[(data[i] & 0x08) >> 3];
			pixels[j+5] = PICTUtilities.COLORS_1BIT[(data[i] & 0x04) >> 2];
			pixels[j+6] = PICTUtilities.COLORS_1BIT[(data[i] & 0x02) >> 1];
			pixels[j+7] = PICTUtilities.COLORS_1BIT[(data[i] & 0x01) >> 0];
		}
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, 16, 16, pixels, 0, 16);
		return img;
	}

	/**
	 * Creates an AWT image of this cursor's mask.
	 * @return the cursor mask.
	 */
	public Image getMask() {
		int[] pixels = new int[512];
		for (int i=20, j=0; i<84 && j<512; i++, j+=8) {
			pixels[j+0] = PICTUtilities.COLORS_1BIT[(data[i] & 0x80) >> 7];
			pixels[j+1] = PICTUtilities.COLORS_1BIT[(data[i] & 0x40) >> 6];
			pixels[j+2] = PICTUtilities.COLORS_1BIT[(data[i] & 0x20) >> 5];
			pixels[j+3] = PICTUtilities.COLORS_1BIT[(data[i] & 0x10) >> 4];
			pixels[j+4] = PICTUtilities.COLORS_1BIT[(data[i] & 0x08) >> 3];
			pixels[j+5] = PICTUtilities.COLORS_1BIT[(data[i] & 0x04) >> 2];
			pixels[j+6] = PICTUtilities.COLORS_1BIT[(data[i] & 0x02) >> 1];
			pixels[j+7] = PICTUtilities.COLORS_1BIT[(data[i] & 0x01) >> 0];
		}
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, 16, 16, pixels, 256, 16);
		return img;
	}

	/**
	 * Creates an AWT image of the cursor composited with its mask.
	 * A bit set in the mask results in the color of the bit in the image,
	 * and a bit cleared in both the image and mask is completely transparent.
	 * Usually, if a bit is cleared in the mask but set in the image,
	 * the corresponding pixel on the screen when the cursor is drawn
	 * is inverted. This is not possible with images in Java, so
	 * instead we replace these pixels with a 50% gray with 50% opacity.
	 * @return the cursor image and mask.
	 */
	public Image getBWComposite() {
		int[] pixels = new int[512];
		for (int i=20, j=0; i<84 && j<512; i++, j+=8) {
			pixels[j+0] = ((data[i] & 0x80) >> 7);
			pixels[j+1] = ((data[i] & 0x40) >> 6);
			pixels[j+2] = ((data[i] & 0x20) >> 5);
			pixels[j+3] = ((data[i] & 0x10) >> 4);
			pixels[j+4] = ((data[i] & 0x08) >> 3);
			pixels[j+5] = ((data[i] & 0x04) >> 2);
			pixels[j+6] = ((data[i] & 0x02) >> 1);
			pixels[j+7] = ((data[i] & 0x01) >> 0);
		}
		int[] pixelsx = new int[256];
		for (int i=0, j=256; i<256 && j<512; i++, j++) {
			if (pixels[j] != 0) {
				if (pixels[i] != 0)
					pixelsx[i] = 0xFF000000;
				else
					pixelsx[i] = 0xFFFFFFFF;
			} else {
				if (pixels[i] != 0)
					pixelsx[i] = 0x80808080;
				else
					pixelsx[i] = 0x00000000;
			}
		}
		BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, 16, 16, pixelsx, 0, 16);
		return img;
	}
	
	/**
	 * Returns the Y coordinate of the cursor's hot spot, or center.
	 * For example, if the hot spot is (1,1) and the mouse
	 * is currently pointing at (16,16), the top left pixel of
	 * the cursor will be drawn at (15,15).
	 * @return The Y coordinate of the hot spot.
	 */
	public int getHotspotY() {
		return KSFLUtilities.getShort(data, 84);
	}
	
	/**
	 * Returns the X coordinate of the cursor's hot spot, or center.
	 * For example, if the hot spot is (1,1) and the mouse
	 * is currently pointing at (16,16), the top left pixel of
	 * the cursor will be drawn at (15,15).
	 * @return The X coordinate of the hot spot.
	 */
	public int getHotspotX() {
		return KSFLUtilities.getShort(data, 86);
	}
	
	/**
	 * Returns the cursor's hot spot, or center.
	 * For example, if the hot spot is (1,1) and the mouse
	 * is currently pointing at (16,16), the top left pixel of
	 * the cursor will be drawn at (15,15).
	 * @return A <code>Point</code> representing the hot spot.
	 */
	public Point getHotspot() {
		return new Point(KSFLUtilities.getShort(data, 86), KSFLUtilities.getShort(data, 84));
	}
	
	/**
	 * Returns the color table seed.
	 * @return the color table seed.
	 */
	public int getColorTableSeed() {
		return KSFLUtilities.getInt(data, 92);
	}
	
	/**
	 * Creates an AWT image of this cursor's color image.
	 * @return an AWT image of this cursor's color image.
	 */
	public Image getImage() {
		try {
			DataInputStream in;
			in = new DataInputStream(new ByteArrayInputStream(data)); in.skip(getPixmapRcdOffset());
			PixMap crsrpm = PixMap.read(in, true);
			in = new DataInputStream(new ByteArrayInputStream(data)); in.skip(getImageOffset());
			byte[] crsrpd = crsrpm.readPixData(in, false);
			in = new DataInputStream(new ByteArrayInputStream(data)); in.skip(crsrpm.pmTable);
			ColorTable crsrct = crsrpm.hasColorTable() ? ColorTable.read(in) : null;
			return PICTUtilities.pixmapToImage(crsrpm, crsrct, crsrpd, 0xFFFFFFFF, 0xFF000000, false, false);
		} catch (IOException ioe) {
			return null;
		}
	}
	
	/**
	 * Creates an AWT image of this cursor's color image composited with its mask.
	 * @return an AWT image of this cursor's color image composited with its mask.
	 */
	public Image getComposite() {
		try {
			DataInputStream in;
			in = new DataInputStream(new ByteArrayInputStream(data)); in.skip(getPixmapRcdOffset());
			PixMap crsrpm = PixMap.read(in, true);
			in = new DataInputStream(new ByteArrayInputStream(data)); in.skip(getImageOffset());
			byte[] crsrpd = crsrpm.readPixData(in, false);
			in = new DataInputStream(new ByteArrayInputStream(data)); in.skip(crsrpm.pmTable);
			ColorTable crsrct = crsrpm.hasColorTable() ? ColorTable.read(in) : null;
			BufferedImage crs = PICTUtilities.pixmapToImage(crsrpm, crsrct, crsrpd, 0xFFFFFFFF, 0xFF000000, false, false);
			BufferedImage msk = (BufferedImage)getMask();
			int[] icnpixels = new int[crs.getWidth()*crs.getHeight()];
			crs.getRGB(0, 0, crs.getWidth(), crs.getHeight(), icnpixels, 0, crs.getWidth());
			int[] mskpixels = new int[msk.getWidth()*msk.getHeight()];
			msk.getRGB(0, 0, msk.getWidth(), msk.getHeight(), mskpixels, 0, msk.getWidth());
			for (int i = 0; i < icnpixels.length && i < mskpixels.length; i++) {
				if ((mskpixels[i] & 0xFFFFFF) >= 0x800000) icnpixels[i] = 0;
			}
			BufferedImage cmp = new BufferedImage(crs.getWidth(), crs.getHeight(), BufferedImage.TYPE_INT_ARGB);
			cmp.setRGB(0, 0, crs.getWidth(), crs.getHeight(), icnpixels, 0, crs.getWidth());
			return cmp;
		} catch (IOException ioe) {
			return null;
		}
	}
}
