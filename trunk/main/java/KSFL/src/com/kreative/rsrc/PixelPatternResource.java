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
import java.awt.image.BufferedImage;
import java.io.*;
import com.kreative.ksfl.KSFLConstants;
import com.kreative.rsrc.misc.PatternPaint;
import com.kreative.rsrc.pict.*;

/**
 * The <code>PixelPatternResource</code> class represents a Mac OS color pattern
 * (pixel pattern) resource.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class PixelPatternResource extends MacResource {
	/**
	 * The resource type of a Mac OS pixel pattern resource,
	 * the four-character constant <code>ppat</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.ppat;
	
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
	 * Constructs a new resource of type <code>ppat</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public PixelPatternResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
	}
	
	/**
	 * Constructs a new resource of type <code>ppat</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public PixelPatternResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
	}
	
	/**
	 * Constructs a new resource of type <code>ppat</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public PixelPatternResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
	}
	
	/**
	 * Constructs a new resource of type <code>ppat</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public PixelPatternResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public PixelPatternResource(int type, short id, byte[] data) {
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
	public PixelPatternResource(int type, short id, String name, byte[] data) {
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
	public PixelPatternResource(int type, short id, byte attr, byte[] data) {
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
	public PixelPatternResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
	}
	
	/**
	 * Creates a paint for drawing with this pattern.
	 * @param fg the foreground paint.
	 * @param bg the background paint.
	 * @return a <code>PatternPaint</code> for drawing with this pattern.
	 */
	public Paint toPaint(Paint fg, Paint bg) {
		try {
			return PixelPattern.read(data).toPaint(fg, bg);
		} catch (IOException ioe) {
			return null;
		}
	}
	
	private static class PixelPattern {
		public static final int DITHER_PAT = 2;
		public static final int PIXEL_PAT = 1;
		//public static final int BW_PAT = 0;
		
		public int patType;
		public int pmHandle;
		public int pdHandle;
		//public int pxHandle;
		//public int pxValid;
		//public int pxMap;
		public long pat1Data;
		public RGBColor rgb;
		public PixMap pixMap;
		public ColorTable colorTable;
		public byte[] pixData;
		
		public static PixelPattern read(byte[] data) throws IOException {
			DataInputStream in;
			PixelPattern pp = new PixelPattern();
			in = new DataInputStream(new ByteArrayInputStream(data));
			pp.patType = in.readShort();
			pp.pmHandle = in.readInt();
			pp.pdHandle = in.readInt();
			/* pp.pxHandle = */ in.readInt();
			/* pp.pxValid = */ in.readShort();
			/* pp.pxMap = */ in.readInt();
			pp.pat1Data = in.readLong();
			switch (pp.patType) {
			case DITHER_PAT:
				pp.rgb = RGBColor.read(in);
				pp.pixMap = null;
				pp.colorTable = null;
				pp.pixData = null;
				break;
			case PIXEL_PAT:
				pp.rgb = null;
				in = new DataInputStream(new ByteArrayInputStream(data));
				in.skip(pp.pmHandle);
				pp.pixMap = PixMap.read(in, true);
				in = new DataInputStream(new ByteArrayInputStream(data));
				in.skip(pp.pixMap.pmTable);
				pp.colorTable = pp.pixMap.hasColorTable() ? ColorTable.read(in) : null;
				in = new DataInputStream(new ByteArrayInputStream(data));
				in.skip(pp.pdHandle);
				pp.pixData = pp.pixMap.readPixData(in, false);
				break;
			default:
				pp.rgb = null;
				pp.pixMap = null;
				pp.colorTable = null;
				pp.pixData = null;
				break;
			}
			return pp;
		}
		
		public Paint toPaint(Paint fg, Paint bg) {
			if (pixMap != null) {
				BufferedImage bi = PICTUtilities.pixmapToImage(pixMap, colorTable, pixData, paintToRGB(bg), paintToRGB(fg), true, false);
				return new TexturePaint(bi, new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
			} else if (rgb != null) {
				return rgb.toColor();
			} else {
				return new PatternPaint(fg, bg, pat1Data);
			}
		}
		
		private static int paintToRGB(Paint p) {
			if (p instanceof Color) {
				return ((Color)p).getRGB();
			} else {
				BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = img.createGraphics();
				g.setPaint(p);
				g.fillRect(0, 0, 1, 1);
				g.dispose();
				return img.getRGB(0, 0);
			}
		}
	}
}
