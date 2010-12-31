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
import java.io.*;
import com.kreative.ksfl.*;
import com.kreative.rsrc.pict.*;

/**
 * The <code>ColorIconResource</code> class represents a Mac OS color icon resource.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class ColorIconResource extends MacResource {
	/**
	 * The resource type of a Mac OS color icon resource,
	 * the four-character constant <code>cicn</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.cicn;
	
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
	 * Constructs a new resource of type <code>cicn</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public ColorIconResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
	}
	
	/**
	 * Constructs a new resource of type <code>cicn</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public ColorIconResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
	}
	
	/**
	 * Constructs a new resource of type <code>cicn</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public ColorIconResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
	}
	
	/**
	 * Constructs a new resource of type <code>cicn</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public ColorIconResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public ColorIconResource(int type, short id, byte[] data) {
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
	public ColorIconResource(int type, short id, String name, byte[] data) {
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
	public ColorIconResource(int type, short id, byte attr, byte[] data) {
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
	public ColorIconResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
	}
	
	/**
	 * Creates an AWT image of this icon's B&W image.
	 * @return an AWT image of this icon's B&W image.
	 */
	@SuppressWarnings("unused")
	public Image getBWImage() {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			PixMap iconpm = PixMap.read(in, true);
			PixMap maskbm = PixMap.read(in, true);
			PixMap iconbm = PixMap.read(in, true);
			in.readInt(); // pixelImagePtr
			byte[] maskbd = maskbm.readPixData(in, false);
			byte[] iconbd = iconbm.readPixData(in, false);
			ColorTable iconct = iconpm.hasColorTable() ? ColorTable.read(in) : null;
			byte[] iconpd = iconpm.readPixData(in, false);
			return PICTUtilities.pixmapToImage(iconbm, null, iconbd, 0xFFFFFFFF, 0xFF000000, false, false);
		} catch (IOException ioe) {
			return null;
		}
	}
	
	/**
	 * Creates an AWT image of this icon's mask.
	 * @return an AWT image of this icon's mask.
	 */
	@SuppressWarnings("unused")
	public Image getMask() {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			PixMap iconpm = PixMap.read(in, true);
			PixMap maskbm = PixMap.read(in, true);
			PixMap iconbm = PixMap.read(in, true);
			in.readInt(); // pixelImagePtr
			byte[] maskbd = maskbm.readPixData(in, false);
			byte[] iconbd = iconbm.readPixData(in, false);
			ColorTable iconct = iconpm.hasColorTable() ? ColorTable.read(in) : null;
			byte[] iconpd = iconpm.readPixData(in, false);
			return PICTUtilities.pixmapToImage(maskbm, null, maskbd, 0xFFFFFFFF, 0xFF000000, false, false);
		} catch (IOException ioe) {
			return null;
		}
	}
	
	/**
	 * Creates an AWT image of this icon's color image.
	 * @return an AWT image of this icon's color image.
	 */
	@SuppressWarnings("unused")
	public Image getImage() {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			PixMap iconpm = PixMap.read(in, true);
			PixMap maskbm = PixMap.read(in, true);
			PixMap iconbm = PixMap.read(in, true);
			in.readInt(); // pixelImagePtr
			byte[] maskbd = maskbm.readPixData(in, false);
			byte[] iconbd = iconbm.readPixData(in, false);
			ColorTable iconct = iconpm.hasColorTable() ? ColorTable.read(in) : null;
			byte[] iconpd = iconpm.readPixData(in, false);
			return PICTUtilities.pixmapToImage(iconpm, iconct, iconpd, 0xFFFFFFFF, 0xFF000000, false, false);
		} catch (IOException ioe) {
			return null;
		}
	}
	
	/**
	 * Creates an AWT image of this icon's B&W image composited with its mask.
	 * @return an AWT image of this icon's B&W image composited with its mask.
	 */
	@SuppressWarnings("unused")
	public Image getBWComposite() {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			PixMap iconpm = PixMap.read(in, true);
			PixMap maskbm = PixMap.read(in, true);
			PixMap iconbm = PixMap.read(in, true);
			in.readInt(); // pixelImagePtr
			byte[] maskbd = maskbm.readPixData(in, false);
			byte[] iconbd = iconbm.readPixData(in, false);
			ColorTable iconct = iconpm.hasColorTable() ? ColorTable.read(in) : null;
			byte[] iconpd = iconpm.readPixData(in, false);
			BufferedImage icn = PICTUtilities.pixmapToImage(iconbm, null, iconbd, 0xFFFFFFFF, 0xFF000000, false, false);
			BufferedImage msk = PICTUtilities.pixmapToImage(maskbm, null, maskbd, 0xFFFFFFFF, 0xFF000000, false, false);
			int[] icnpixels = new int[icn.getWidth()*icn.getHeight()];
			icn.getRGB(0, 0, icn.getWidth(), icn.getHeight(), icnpixels, 0, icn.getWidth());
			int[] mskpixels = new int[msk.getWidth()*msk.getHeight()];
			msk.getRGB(0, 0, msk.getWidth(), msk.getHeight(), mskpixels, 0, msk.getWidth());
			for (int i = 0; i < icnpixels.length && i < mskpixels.length; i++) {
				if ((mskpixels[i] & 0xFFFFFF) >= 0x800000) icnpixels[i] = 0;
			}
			BufferedImage cmp = new BufferedImage(icn.getWidth(), icn.getHeight(), BufferedImage.TYPE_INT_ARGB);
			cmp.setRGB(0, 0, icn.getWidth(), icn.getHeight(), icnpixels, 0, icn.getWidth());
			return cmp;
		} catch (IOException ioe) {
			return null;
		}
	}
	
	/**
	 * Creates an AWT image of this icon's color image composited with its mask.
	 * @return an AWT image of this icon's color image composited with its mask.
	 */
	@SuppressWarnings("unused")
	public Image getComposite() {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			PixMap iconpm = PixMap.read(in, true);
			PixMap maskbm = PixMap.read(in, true);
			PixMap iconbm = PixMap.read(in, true);
			in.readInt(); // pixelImagePtr
			byte[] maskbd = maskbm.readPixData(in, false);
			byte[] iconbd = iconbm.readPixData(in, false);
			ColorTable iconct = iconpm.hasColorTable() ? ColorTable.read(in) : null;
			byte[] iconpd = iconpm.readPixData(in, false);
			BufferedImage icn = PICTUtilities.pixmapToImage(iconpm, iconct, iconpd, 0xFFFFFFFF, 0xFF000000, false, false);
			BufferedImage msk = PICTUtilities.pixmapToImage(maskbm, null, maskbd, 0xFFFFFFFF, 0xFF000000, false, false);
			int[] icnpixels = new int[icn.getWidth()*icn.getHeight()];
			icn.getRGB(0, 0, icn.getWidth(), icn.getHeight(), icnpixels, 0, icn.getWidth());
			int[] mskpixels = new int[msk.getWidth()*msk.getHeight()];
			msk.getRGB(0, 0, msk.getWidth(), msk.getHeight(), mskpixels, 0, msk.getWidth());
			for (int i = 0; i < icnpixels.length && i < mskpixels.length; i++) {
				if ((mskpixels[i] & 0xFFFFFF) >= 0x800000) icnpixels[i] = 0;
			}
			BufferedImage cmp = new BufferedImage(icn.getWidth(), icn.getHeight(), BufferedImage.TYPE_INT_ARGB);
			cmp.setRGB(0, 0, icn.getWidth(), icn.getHeight(), icnpixels, 0, icn.getWidth());
			return cmp;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}
}
