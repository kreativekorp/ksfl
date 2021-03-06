/*
 * Copyright &copy; 2009-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.rsrc.pict;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.ColorModel;
import java.io.*;

public class ColorSpec implements Paint {
	public int value;
	public RGBColor rgb;
	
	public static ColorSpec read(DataInputStream in) throws IOException {
		ColorSpec c = new ColorSpec();
		c.value = in.readUnsignedShort();
		c.rgb = RGBColor.read(in);
		return c;
	}
	
	public ColorSpec() {
		this.value = 0;
		this.rgb = new RGBColor();
	}
	
	public ColorSpec(int rgb) {
		this.value = 0;
		this.rgb = new RGBColor(rgb);
	}
	
	public ColorSpec(int r, int g, int b) {
		this.value = 0;
		this.rgb = new RGBColor(r,g,b);
	}
	
	public ColorSpec(float r, float g, float b) {
		this.value = 0;
		this.rgb = new RGBColor(r,g,b);
	}
	
	public ColorSpec(Color c) {
		this.value = 0;
		this.rgb = new RGBColor(c);
	}
	
	public ColorSpec(int value, int rgb) {
		this.value = value;
		this.rgb = new RGBColor(rgb);
	}
	
	public ColorSpec(int value, int r, int g, int b) {
		this.value = value;
		this.rgb = new RGBColor(r,g,b);
	}
	
	public ColorSpec(int value, float r, float g, float b) {
		this.value = value;
		this.rgb = new RGBColor(r,g,b);
	}
	
	public ColorSpec(int value, Color c) {
		this.value = value;
		this.rgb = new RGBColor(c);
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeShort(value);
		rgb.write(out);
	}
	
	public Color toColor() {
		return rgb.toColor();
	}
	
	public int toRGB() {
		return rgb.toRGB();
	}
	
	public String toString() {
		return value+","+rgb.toString();
	}

	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		return rgb.toColor().createContext(cm, deviceBounds, userBounds, xform, hints);
	}

	public int getTransparency() {
		return OPAQUE;
	}
}
