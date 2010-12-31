/*
 * Copyright &copy; 2010-2011 Rebecca G. Bettencourt / Kreative Software
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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import com.kreative.ksfl.*;
import com.kreative.rsrc.pict.*;

/**
 * The <code>PictureResource</code> class represents a QuickDraw picture resource.
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class PictureResource extends MacResource {
	/**
	 * The resource type of a QuickDraw picture resource,
	 * the four-character constant <code>PICT</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.PICT;
	
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
	 * Constructs a new resource of type <code>PICT</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public PictureResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
	}
	
	/**
	 * Constructs a new resource of type <code>PICT</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public PictureResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
	}
	
	/**
	 * Constructs a new resource of type <code>PICT</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public PictureResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
	}
	
	/**
	 * Constructs a new resource of type <code>PICT</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public PictureResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public PictureResource(int type, short id, byte[] data) {
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
	public PictureResource(int type, short id, String name, byte[] data) {
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
	public PictureResource(int type, short id, byte attr, byte[] data) {
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
	public PictureResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
	}
	
	/**
	 * Returns the bounding rectangle for this picture resource.
	 * @return the bounding rectangle for this picture resource.
	 */
	public Rectangle getBoundingRect() {
		return KSFLUtilities.getRect(data, 2);
	}
	
	/**
	 * Returns a PICTInputStream for reading instructions from this picture resource.
	 * The size and bounding rectangle must be read from the stream before instructions
	 * can be read.
	 * @return a PICTInputStream for reading instructions from this picture resource.
	 */
	public PICTInputStream getPICTInputStream() {
		return new PICTInputStream(new ByteArrayInputStream(data));
	}
	
	/**
	 * Draws this picture resource into an AWT Graphics2D context.
	 * @param g a Graphics2D to draw into.
	 * @param x the X coordinate of the top left corner of the image.
	 * @param y the Y coordinate of the top left corner of the image.
	 * @return true if the picture was fully drawn, false if an error occurred.
	 */
	public boolean draw(Graphics2D g, int x, int y) {
		try {
			PICTInputStream in = new PICTInputStream(new ByteArrayInputStream(data));
			in.readUnsignedShort();
			Rect bounds = in.readRect();
			AffineTransform tx = g.getTransform();
			g.translate(-bounds.left+x, -bounds.top+y);
			PICTGraphics pg = new PICTGraphics(g);
			while (true) {
				PICTInstruction inst = in.readInstruction();
				pg.executeInstruction(inst);
				if (inst instanceof PICTInstruction.OpEndPic) break;
			}
			g.setTransform(tx);
			return true;
		} catch (IOException ioe) {
			return false;
		}
	}
	
	/**
	 * Converts this picture resource to an AWT image and returns the converted image.
	 * If the picture data is improperly formed, returns null.
	 * @return the converted image.
	 */
	public Image toImage() {
		try {
			PICTInputStream in = new PICTInputStream(new ByteArrayInputStream(data));
			in.readUnsignedShort();
			Rect bounds = in.readRect();
			BufferedImage bi = new BufferedImage(bounds.right-bounds.left, bounds.bottom-bounds.top, BufferedImage.TYPE_INT_ARGB);
			Graphics2D bg = bi.createGraphics();
			bg.translate(-bounds.left, -bounds.top);
			PICTGraphics pg = new PICTGraphics(bg);
			while (true) {
				PICTInstruction inst = in.readInstruction();
				pg.executeInstruction(inst);
				if (inst instanceof PICTInstruction.OpEndPic) break;
			}
			bg.dispose();
			return bi;
		} catch (IOException ioe) {
			return null;
		}
	}
}
