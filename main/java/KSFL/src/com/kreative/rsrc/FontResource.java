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
import java.awt.image.MemoryImageSource;
import java.io.UnsupportedEncodingException;
import com.kreative.ksfl.*;

/**
 * The <code>RFont</code> class represents a Mac OS bitmap font resource.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class FontResource extends MacResource {
	/**
	 * The resource type of a Mac OS bitmap font resource,
	 * the four-character constant <code>NFNT</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.NFNT;
	
	/**
	 * The resource type of an old-style Mac OS bitmap font resource,
	 * the four-character constant <code>FONT</code>.
	 */
	public static final int RESOURCE_TYPE_OLD = KSFLConstants.FONT;
	
	/**
	 * Checks if a resource type is one this class knows how to handle.
	 * The default implementation is to always return true.
	 * It is recommended that subclasses override this.
	 * @param type A resource type to check.
	 * @return True if this class can handle this resource type, false otherwise.
	 */
	public static boolean isMyType(int type) {
		return (type == RESOURCE_TYPE || type == RESOURCE_TYPE_OLD);
	}
	
	/**
	 * Constructs a new resource of type <code>NFNT</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public FontResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
	}
	
	/**
	 * Constructs a new resource of type <code>NFNT</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public FontResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
	}
	
	/**
	 * Constructs a new resource of type <code>NFNT</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public FontResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
	}
	
	/**
	 * Constructs a new resource of type <code>NFNT</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public FontResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public FontResource(int type, short id, byte[] data) {
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
	public FontResource(int type, short id, String name, byte[] data) {
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
	public FontResource(int type, short id, byte attr, byte[] data) {
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
	public FontResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
	}
	
	/**
	 * Flag bit indicating that this font has an image height table.
	 */
	public static final short FLAG_HAS_IMAGE_HEIGHT_TABLE = 0x0001;
	/**
	 * Flag bit indicating that this font has a glyph width table with fractional values.
	 */
	public static final short FLAG_HAS_GLYPH_WIDTH_TABLE  = 0x0002;
	/**
	 * Flag bits indicating the bit depth of the font.
	 */
	public static final short FLAG_BIT_DEPTH              = 0x000C;
	/**
	 * Value of the bit depth flags representing a 1-bit font.
	 */
	public static final short   FLAG_BIT_DEPTH_1BIT = 0x0000;
	/**
	 * Value of the bit depth flags representing a 2-bit font.
	 */
	public static final short   FLAG_BIT_DEPTH_2BIT = 0x0004;
	/**
	 * Value of the bit depth flags representing a 4-bit font.
	 */
	public static final short   FLAG_BIT_DEPTH_4BIT = 0x0008;
	/**
	 * Value of the bit depth flags representing an 8-bit font.
	 */
	public static final short   FLAG_BIT_DEPTH_8BIT = 0x000C;
	/**
	 * Flag bit indicating that this font has an accompanying <code>fctb</code>
	 * resource containing the font's color table.
	 */
	public static final short FLAG_HAS_FONT_COLOR_TABLE   = 0x0080;
	/**
	 * Flag bit indicating that this font is synthetic, or automatically created by QuickDraw.
	 */
	public static final short FLAG_IS_SYNTHETIC           = 0x0100;
	/**
	 * Flag bit indicating that this font is in color.
	 */
	public static final short FLAG_HAS_COLOR              = 0x0200;
	/**
	 * Reserved flag bit that must be set to one, as opposed to zero.
	 */
	public static final short FLAG_RESERVED_SET_BIT       = 0x1000;
	/**
	 * Flag bit indicating that this font is fixed-width.
	 */
	public static final short FLAG_FIXED_WIDTH            = 0x2000;
	/**
	 * Flag bit indicating that QuickDraw should not create synthetic fonts based on this font.
	 */
	public static final short FLAG_DONT_MAKE_SYNTHETIC    = 0x4000;
	
	/** Returns the flag bits describing the properties of this font. */
	public short getFlags() { return KSFLUtilities.getShort(data, 0); }
	/** Returns the character value of the first character in this font. This is in the font's encoding, which is not going to be Unicode in most cases. */
	public char getFirstChar() { return (char)KSFLUtilities.getShort(data, 2); }
	/** Returns the character value of the last character in this font. This is in the font's encoding, which is not going to be Unicode in most cases. */
	public char getLastChar() { return (char)KSFLUtilities.getShort(data, 4); }
	/** Returns the width of the widest character, in pixels. */
	public short getMaxCharWidth() { return KSFLUtilities.getShort(data, 6); }
	/** Returns the offset from the current X position at which all characters are drawn. This is either a negative number or zero. */
	public short getKerning() { return KSFLUtilities.getShort(data, 8); }
	/** Returns the negative descent of this font. If the font is properly formed, this should be equivalent to <code>-getDescent()</code>. */
	public short getNDescent() { return KSFLUtilities.getShort(data, 10); }
	/** Returns the maximum bounding box width, in pixels. This value actually varies widely and I'm not sure what exactly it means. */
	public short getRectWidth() { return KSFLUtilities.getShort(data, 12); }
	/** Returns the height of the font bitmap, in pixels. Same as <code>getBitmapHeight()</code>. */
	public short getHeight() { return KSFLUtilities.getShort(data, 14); }
	/** Returns the offset to the widths and offsets table, in number of short words from this field. Use <code>getOffsetToWidthOffsetTable()</code> to get the offset in number of bytes from the start of the font. I don't think Mac OS actually pays any attention to this field, and just assumes the table immediately follows the bitmap. */
	public short getRawOffsetToWidthOffsetTable() { return KSFLUtilities.getShort(data, 16); }
	/** Returns the offset to the widths and offsets table, in number of bytes from the beginning of the font. I don't think Mac OS actually pays any attention to this field, and just assumes the table immediately follows the bitmap. */
	public int getOffsetToWidthOffsetTable() { return KSFLUtilities.getShort(data, 16)*2+16; }
	/** Returns the ascent of this font. */
	public short getAscent() { return KSFLUtilities.getShort(data, 18); }
	/** Returns the descent of this font. If the font is properly formed, this should be equivalent to <code>-getNDescent()</code>. */
	public short getDescent() { return KSFLUtilities.getShort(data, 20); }
	/** Returns the leading, or number of pixels between each line of text, of this font. */
	public short getLeading() { return KSFLUtilities.getShort(data, 22); }
	/** Returns the number of short words in each scan line of the font bitmap. Use <code>getBitmapWidth()</code> to get the width in pixels (for 1-bit only). */
	public short getRowBytes() { return KSFLUtilities.getShort(data, 24); }
	
	/** Returns the offset where the font bitmap starts. */
	public int getBitmapOffset() { return 26; }
	/** Returns the length of the font bitmap in bytes. */
	public int getBitmapLength() { return KSFLUtilities.getShort(data, 14)*(KSFLUtilities.getShort(data, 24)*2); }
	/** Returns the height of the font bitmap, in pixels. Same as <code>getHeight()</code>. */
	public int getBitmapHeight() { return KSFLUtilities.getShort(data, 14); }
	/** Returns the width of the font bitmap, in pixels. This assumes the font is 1-bit. */
	public int getBitmapWidth() { return KSFLUtilities.getShort(data, 24)*16; }
	/** Returns the raw bitmap as binary data. */
	public byte[] getBitmap() { return KSFLUtilities.copy(data, 26, KSFLUtilities.getShort(data, 14)*(KSFLUtilities.getShort(data, 24)*2)); }
	/** Returns the font bitmap as an image, using black and white. This assumes the font is 1-bit. */
	public Image getBitmapImage() {
		return Toolkit.getDefaultToolkit().createImage(new BitmapImageSource(
				getBitmapWidth(), getBitmapHeight(), getBitmap(), 0, getRowBytes()*2));
	}
	/** Returns the font bitmap as an image, using the specified background and foreground colors. This assumes the font is 1-bit. */
	public Image getBitmapImage(Color bgColor, Color fgColor) {
		return Toolkit.getDefaultToolkit().createImage(new BitmapImageSource(
				getBitmapWidth(), getBitmapHeight(), getBitmap(), 0, getRowBytes()*2,
				bgColor, fgColor));
	}
	
	/** Returns the offset where the bitmap offset table starts. */
	public int getBOTOffset() { return getBitmapOffset()+getBitmapLength(); }
	/** Returns the length of the bitmap offset table in bytes. */
	public int getBOTLength() { return (KSFLUtilities.getShort(data, 4)-KSFLUtilities.getShort(data, 2)+3)*2; }
	
	/** Returns the offset where the widths and offsets table starts. */
	public int getWOTOffset() { return getBOTOffset()+getBOTLength(); }
	/** Returns the length of the widths and offsets table in bytes. */
	public int getWOTLength() { return (KSFLUtilities.getShort(data, 4)-KSFLUtilities.getShort(data, 2)+2)*2; }
	
	/** Returns the offset where the glyph width table starts--or, if the font does not have one, where it would start if it did have one. */
	public int getGWTOffset() { return getWOTOffset()+getWOTLength(); }
	/** Returns the length of the glyph width table in bytes, or zero if the font does not have one. */
	public int getGWTLength() { return ((KSFLUtilities.getShort(data, 0) & FLAG_HAS_GLYPH_WIDTH_TABLE) != 0) ? ((KSFLUtilities.getShort(data, 4)-KSFLUtilities.getShort(data, 2)+2)*2) : 0; }
	
	/**
	 * Converts a character value to the equivalent offset in the font's tables.
	 * If the specified character value is not defined for this font, this maps
	 * to the index immediately after the last character, which is the index for
	 * the catchall character.
	 * <p>
	 * Character values are in the font's encoding, which is not going to be Unicode in most cases.
	 * @param ch the character value.
	 * @return the table index.
	 */
	public int charValueToIndex(int ch) {
		char fc = getFirstChar();
		char lc = getLastChar();
		int b = getWOTOffset();
		int offset = data[b+(ch-fc)*2] & 0xFF;
		int width = data[b+(ch-fc)*2+1] & 0xFF;
		if (ch < fc || ch > lc || width < 0 || offset < 0)
			return (lc+1)-fc;
		else
			return ch-fc;
	}
	
	/**
	 * Converts an offset in the font's tables into the equivalent character value.
	 * Character values are in the font's encoding, which is not going to be Unicode in most cases.
	 * @param idx the table index.
	 * @return the character value.
	 */
	public int indexToCharValue(int idx) {
		return getFirstChar()+idx;
	}
	
	/** Returns all of the above in a single object for efficient access. Also contains methods for drawing text with the font. */
	public FontInfo getInfo() { return new FontInfo(); }
	
	public class FontInfo {
		public short flags;
		public char firstChar;
		public char lastChar;
		public short maxCharWidth;
		public short kerning;
		public short nDescent;
		public short rectWidth;
		public short height;
		public short rawOffsetToWidthOffsetTable;
		public short ascent;
		public short descent;
		public short leading;
		public short rowBytes;
		public byte[] bitmap;
		public Image bitmapImg;
		public int[] bitmapLoc;
		public int[] offset;
		public int[] width;
		public float[] widthx;
		
		/**
		 * Creates a new font info record for this font.
		 */
		public FontInfo() {
			flags = KSFLUtilities.getShort(data, 0);
			firstChar = (char)KSFLUtilities.getShort(data, 2);
			lastChar = (char)KSFLUtilities.getShort(data, 4);
			maxCharWidth = KSFLUtilities.getShort(data, 6);
			kerning = KSFLUtilities.getShort(data, 8);
			nDescent = KSFLUtilities.getShort(data, 10);
			rectWidth = KSFLUtilities.getShort(data, 12);
			height = KSFLUtilities.getShort(data, 14);
			rawOffsetToWidthOffsetTable = KSFLUtilities.getShort(data, 16);
			ascent = KSFLUtilities.getShort(data, 18);
			descent = KSFLUtilities.getShort(data, 20);
			leading = KSFLUtilities.getShort(data, 22);
			rowBytes = KSFLUtilities.getShort(data, 24);
			int p = 26;
			bitmap = KSFLUtilities.copy(data, p, height*rowBytes*2); p += height*rowBytes*2;
			bitmapImg = Toolkit.getDefaultToolkit().createImage(new BitmapImageSource(rowBytes*16, height, bitmap, 0, rowBytes*2));
			bitmapLoc = new int[lastChar-firstChar+3];
			for (int i=0; i<bitmapLoc.length; i++) {
				bitmapLoc[i] = (KSFLUtilities.getShort(data, p) & 0xFFFF); p+=2;
			}
			// p = ofstToWidthOffsetTable*2 + 16; // I don't think Mac OS actually pays attention to this field, so neither do I
			offset = new int[lastChar-firstChar+2];
			width = new int[lastChar-firstChar+2];
			for (int i=0; i<offset.length && i<width.length; i++) {
				offset[i] = ((data[p] == -1) ? (-1) : (data[p] & 0xFF)); p++;
				width[i] = ((data[p] == -1) ? (-1) : (data[p] & 0xFF)); p++;
			}
			if ((flags & FLAG_HAS_GLYPH_WIDTH_TABLE) != 0) {
				widthx = new float[lastChar-firstChar+2];
				for (int i=0; i<widthx.length; i++) {
					widthx[i] = (KSFLUtilities.getShort(data, p) & 0xFFFF)/256.0f; p+=2;
				}
			} else {
				widthx = null;
			}
		}
		
		/** Returns the font bitmap as an image, using black and white. This assumes the font is 1-bit. */
		public Image getBitmapImage() {
			return Toolkit.getDefaultToolkit().createImage(new BitmapImageSource(
					rowBytes*16, height, bitmap, 0, rowBytes*2, 0xFFFFFFFF, 0xFF000000));
		}
		
		/** Returns the font bitmap as an image, using the foreground color of the specified graphics context and a transparent background. This assumes the font is 1-bit. */
		public Image getBitmapImage(Graphics g) {
			return Toolkit.getDefaultToolkit().createImage(new BitmapImageSource(
					rowBytes*16, height, bitmap, 0, rowBytes*2, 0x00000000, g.getColor().getRGB()));
		}
		
		/** Returns the font bitmap as an image, using the specified background and foreground colors. This assumes the font is 1-bit. */
		public Image getBitmapImage(int bgColor, int fgColor) {
			return Toolkit.getDefaultToolkit().createImage(new BitmapImageSource(
					rowBytes*16, height, bitmap, 0, rowBytes*2, bgColor, fgColor));
		}
		
		/** Returns the font bitmap as an image, using the specified background and foreground colors. This assumes the font is 1-bit. */
		public Image getBitmapImage(Color bgColor, Color fgColor) {
			return Toolkit.getDefaultToolkit().createImage(new BitmapImageSource(
					rowBytes*16, height, bitmap, 0, rowBytes*2, bgColor, fgColor));
		}
		
		/**
		 * Converts a character value to the equivalent offset in the font's tables.
		 * If the specified character value is not defined for this font, this maps
		 * to the index immediately after the last character, which is the index for
		 * the catchall character.
		 * <p>
		 * Character values are in the font's encoding, which is not going to be Unicode in most cases.
		 * @param ch the character value.
		 * @return the table index.
		 */
		public int charValueToIndex(int ch) {
			if (ch < firstChar || ch > lastChar || width[ch-firstChar] < 0 || offset[ch-firstChar] < 0)
				return (lastChar+1)-firstChar;
			else
				return ch-firstChar;
		}
		
		/**
		 * Converts an offset in the font's tables into the equivalent character value.
		 * Character values are in the font's encoding, which is not going to be Unicode in most cases.
		 * @param idx the table index.
		 * @return the character value.
		 */
		public int indexToCharValue(int idx) {
			return firstChar+idx;
		}
		
		public int drawCharacter(Graphics g, int x, int y, int ch) {
			return drawCharacter(g, x, y, ch, getBitmapImage(g));
		}
		
		public int drawCharacter(Graphics g, int x, int y, int ch, int bg, int fg) {
			return drawCharacter(g, x, y, ch, getBitmapImage(bg, fg));
		}
		
		public int drawCharacter(Graphics g, int x, int y, int ch, Color bg, Color fg) {
			return drawCharacter(g, x, y, ch, getBitmapImage(bg, fg));
		}
		
		private int drawCharacter(Graphics g, int x, int y, int ch, Image bmp) {
			// note: ch is in the font's encoding, not unicode
			ch = charValueToIndex(ch);
			int off = offset[ch];
			int wid = width[ch];
			int bx1 = bitmapLoc[ch];
			int bx2 = bitmapLoc[ch+1];
			g.drawImage(
					bmp,
					x+off+kerning, y-ascent,
					x+off+kerning+(bx2-bx1), y-ascent+height,
					bx1, 0, bx2, height,
					null
			);
			return wid;
		}
		
		public void drawAlphabet(Graphics g, int x, int y, int w) {
			drawAlphabet(g, x, y, w, getBitmapImage(g));
		}
		
		public void drawAlphabet(Graphics g, int x, int y, int w, int bg, int fg) {
			drawAlphabet(g, x, y, w, getBitmapImage(bg, fg));
		}
		
		public void drawAlphabet(Graphics g, int x, int y, int w, Color bg, Color fg) {
			drawAlphabet(g, x, y, w, getBitmapImage(bg, fg));
		}
		
		private void drawAlphabet(Graphics g, int x, int y, int w, Image bmp) {
			int cx = x, cy = y;
			for (int i = 0; i <= lastChar-firstChar+1; i++) {
				if (offset[i] >= 0 && width[i] >= 0) {
					int off = offset[i];
					int wid = width[i];
					int bx1 = bitmapLoc[i];
					int bx2 = bitmapLoc[i+1];
					if (cx+wid >= w) {
						cx = x;
						cy += ascent+descent+leading;
					}
					g.drawImage(
							bmp,
							cx+off+kerning, cy-ascent,
							cx+off+kerning+(bx2-bx1), cy-ascent+height,
							bx1, 0, bx2, height,
							null
					);
					cx += wid;
				}
			}
		}
		
		public int getStringWidth(String s, String te) {
			int w = 0;
			byte[] b;
			try {
				b = s.getBytes(te);
			} catch (UnsupportedEncodingException uee) {
				b = s.getBytes();
			}
			for (int ch : b) {
				ch = ch & 0xFF;
				switch (ch) {
				case '\t':
					w -= w % 36;
					w += 36;
					break;
				default:
					ch = charValueToIndex(ch);
					w += width[ch];
					break;
				}
			}
			return w;
		}
		
		public int getStringHeight(String s, String te, int w) {
			int cx = 0, h = ascent+descent+leading;
			byte[] b;
			try {
				b = s.getBytes(te);
			} catch (UnsupportedEncodingException uee) {
				b = s.getBytes();
			}
			for (int ch : b) {
				ch = ch & 0xFF;
				switch (ch) {
				case '\t':
					cx -= cx % 36;
					cx += 36;
					break;
				case '\n':
				case '\r':
					cx = 0;
					h += ascent+descent+leading;
					break;
				default:
					{
						ch = charValueToIndex(ch);
						int wid = width[ch];
						if (cx+wid >= w) {
							cx = 0;
							h += ascent+descent+leading;
						}
						cx += wid;
					}
					break;
				}
			}
			return h;
		}
		
		public void drawString(Graphics g, int x, int y, String s, String te, int w) {
			drawString(g, x, y, s, te, w, getBitmapImage(g));
		}
		
		public void drawString(Graphics g, int x, int y, String s, String te, int w, int bg, int fg) {
			drawString(g, x, y, s, te, w, getBitmapImage(bg, fg));
		}
		
		public void drawString(Graphics g, int x, int y, String s, String te, int w, Color bg, Color fg) {
			drawString(g, x, y, s, te, w, getBitmapImage(bg, fg));
		}
		
		private void drawString(Graphics g, int x, int y, String s, String te, int w, Image bmp) {
			int cx = x, cy = y;
			byte[] b;
			try {
				b = s.getBytes(te);
			} catch (UnsupportedEncodingException uee) {
				b = s.getBytes();
			}
			for (int ch : b) {
				ch = ch & 0xFF;
				switch (ch) {
				case '\t':
					cx -= (cx-x) % 36;
					cx += 36;
					break;
				case '\n':
				case '\r':
					cx = x;
					cy += ascent+descent+leading;
					break;
				default:
					{
						ch = charValueToIndex(ch);
						int off = offset[ch];
						int wid = width[ch];
						int bx1 = bitmapLoc[ch];
						int bx2 = bitmapLoc[ch+1];
						if (cx+wid >= w) {
							cx = x;
							cy += ascent+descent+leading;
						}
						g.drawImage(
								bmp,
								cx+off+kerning, cy-ascent,
								cx+off+kerning+(bx2-bx1), cy-ascent+height,
								bx1, 0, bx2, height,
								null
						);
						cx += wid;
					}
					break;
				}
			}
		}
	}
	
	private static class BitmapImageSource extends MemoryImageSource {
		private static int[] makePixels(byte[] bmp, int bgColor, int fgColor) {
			int[] pix = new int[bmp.length*8];
			for (int s=0, d=0; s < bmp.length && d < pix.length; s++, d+=8) {
				for (int k=0, b=bmp[s] & 0xFF; k < 8; k++, b<<=1) {
					pix[d+k] = ( ((b & 0x80) != 0) ? fgColor : bgColor );
				}
			}
			return pix;
		}
		
		public BitmapImageSource(int w, int h, byte[] bmp, int off, int scan) {
			super(w, h, makePixels(bmp, 0xFFFFFFFF, 0xFF000000), off*8, scan*8);
		}
		
		public BitmapImageSource(int w, int h, byte[] bmp, int off, int scan, int bgColor, int fgColor) {
			super(w, h, makePixels(bmp, bgColor, fgColor), off*8, scan*8);
		}
		
		public BitmapImageSource(int w, int h, byte[] bmp, int off, int scan, Color bgColor, Color fgColor) {
			super(w, h, makePixels(bmp, bgColor.getRGB(), fgColor.getRGB()), off*8, scan*8);
		}
	}
}
