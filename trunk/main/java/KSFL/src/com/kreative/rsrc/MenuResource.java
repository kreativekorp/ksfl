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
import java.awt.event.KeyEvent;
import java.awt.font.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.kreative.ksfl.*;

/**
 * The <code>MenuResource</code> class represents a Mac OS menu resource.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class MenuResource extends MacResource {
	/**
	 * The resource type of a Mac OS menu resource without command numbers,
	 * the four-character constant <code>MENU</code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.MENU;

	/**
	 * The resource type of a Mac OS menu resource with short (16-bit) command numbers,
	 * the four-character constant <code>cmnu</code>.
	 */
	public static final int RESOURCE_TYPE_SHORT_CMD = KSFLConstants.cmnu;
	
	/**
	 * The resource type of a Mac OS menu resource with long (32-bit) command numbers,
	 * the four-character constant <code>CMNU</code>.
	 */
	public static final int RESOURCE_TYPE_LONG_CMD = KSFLConstants.CMNU;
	
	/**
	 * Checks if a resource type is one this class knows how to handle.
	 * The default implementation is to always return true.
	 * It is recommended that subclasses override this.
	 * @param type A resource type to check.
	 * @return True if this class can handle this resource type, false otherwise.
	 */
	public static boolean isMyType(int type) {
		return (type == RESOURCE_TYPE
			|| type == RESOURCE_TYPE_SHORT_CMD
			|| type == RESOURCE_TYPE_LONG_CMD
		);
	}
	
	/**
	 * Constructs a new resource of type <code>MENU</code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public MenuResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
		autoLength();
	}
	
	/**
	 * Constructs a new resource of type <code>MENU</code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public MenuResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
		autoLength();
	}
	
	/**
	 * Constructs a new resource of type <code>MENU</code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public MenuResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
		autoLength();
	}
	
	/**
	 * Constructs a new resource of type <code>MENU</code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public MenuResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
		autoLength();
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public MenuResource(int type, short id, byte[] data) {
		super(type, id, data);
		autoLength();
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, name, and data.
	 * All attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public MenuResource(int type, short id, String name, byte[] data) {
		super(type, id, name, data);
		autoLength();
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public MenuResource(int type, short id, byte attr, byte[] data) {
		super(type, id, attr, data);
		autoLength();
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, attributes, name, and data.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public MenuResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
		autoLength();
	}
	
	/**
	 * The size of the command number field, if there is one.
	 * This can be either 2 or 4, or 0 if there is no command number field.
	 */
	public int commandIDLength = 0;
	
	/**
	 * Sets the size of the command number appropriately according to the resource
	 * type. If the resource type is not one of the <code>RESOURCE_TYPE</code>
	 * constants, nothing will change.
	 */
	public void autoLength() {
		switch (type) {
		case RESOURCE_TYPE: commandIDLength = 0; break;
		case RESOURCE_TYPE_SHORT_CMD: commandIDLength = 2; break;
		case RESOURCE_TYPE_LONG_CMD: commandIDLength = 4; break;
		}
	}
	
	public short getMenuID() {
		return KSFLUtilities.getShort(data, 0);
	}
	
	public short getWidth() {
		return KSFLUtilities.getShort(data, 2);
	}
	
	public short getHeight() {
		return KSFLUtilities.getShort(data, 4);
	}
	
	public Dimension getSize() {
		return new Dimension(KSFLUtilities.getShort(data, 2), KSFLUtilities.getShort(data, 4));
	}
	
	public short getProcID() {
		return KSFLUtilities.getShort(data, 6);
	}
	
	public int getEnableBits() {
		return KSFLUtilities.getInt(data, 10);
	}
	
	public boolean getEnabled() {
		return ((KSFLUtilities.getInt(data, 10) & 0x01) != 0);
	}
	
	public boolean getEnabled(int index) {
		index++; //-1 -> 0, 0 -> 1, etc.
		if (index < 0) index = 0;
		else if (index >= 32) index = 31;
		return ((KSFLUtilities.getInt(data, 10) & (1 << index)) != 0);
	}
	
	public String getTitle() {
		String s;
		try {
			s = KSFLUtilities.getPString(data, 14, "MACROMAN");
		} catch (UnsupportedEncodingException uue) {
			s = KSFLUtilities.getPString(data, 14);
		}
		if (s.equals("\u0014")) s = "\uF8FF";
		return s;
	}
	
	public Iterator<MenuItem> iterator() {
		return new Iterator<MenuItem>() {
			private int p = 14 + ((data[14] & 0xFF) + 1);
			private int idx = 0;
			public boolean hasNext() {
				return (p < data.length && data[p] != 0);
			}
			public MenuItem next() {
				MenuItem mi = new MenuItem();
				mi.enabled = getEnabled(idx); idx++;
				mi.menuItemName = KSFLUtilities.getPString(data, p); p += ((data[p] & 0xFF) + 1);
				mi.iconID = ((data[p] == 0) ? (short)0 : (short)(256+(data[p] & 0xFF))); p++;
				try {
					mi.commandChar = new String(new byte[]{data[p]}, "MACROMAN").charAt(0);
				} catch (UnsupportedEncodingException uee) {
					mi.commandChar = new String(new byte[]{data[p]}).charAt(0);
				} p++;
				try {
					mi.markChar = new String(new byte[]{data[p]}, "MACROMAN").charAt(0);
				} catch (UnsupportedEncodingException uee) {
					mi.markChar = new String(new byte[]{data[p]}).charAt(0);
				} mi.submenuID = (short)(data[p] & 0xFF); p++;
				mi.style = data[p]; p++;
				switch (commandIDLength) {
				case 1:
					mi.commandNumber = data[p]; p++;
					break;
				case 2:
					if ((p & 1) == 1) p++;
					mi.commandNumber = KSFLUtilities.getShort(data, p); p+=2;
					break;
				case 4:
					if ((p & 1) == 1) p++;
					mi.commandNumber = KSFLUtilities.getInt(data, p); p+=4;
					break;
				}
				mi.fixChars();
				return mi;
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public Menu makeMenu(MacResourceProvider rp) {
		Menu m = new Menu(getTitle());
		Iterator<MenuItem> i = iterator();
		while (i.hasNext()) m.add(i.next().makeMenuItem(rp));
		m.setEnabled(getEnabled());
		return m;
	}
	
	public JMenu makeJMenu(MacResourceProvider rp) {
		JMenu m = new JMenu(getTitle());
		Iterator<MenuItem> i = iterator();
		while (i.hasNext()) m.add(i.next().makeJMenuItem(rp));
		m.setEnabled(getEnabled());
		return m;
	}
	
	/**
	 * The <code>MenuItem</code> class represents an item in a Mac OS menu resource.
	 * <p>
	 * This is not a subclass of <code>Resource</code>; it has no data or attributes.
	 * It is simply a container to get information about menu items.
	 * @since KSFL 1.0
	 * @author Rebecca G. Bettencourt, Kreative Software
	 */
	public static class MenuItem {
		public static final char COMMANDCHAR_SUBMENU = 0x1B;
		public static final char COMMANDCHAR_SCRIPT = 0x1C;
		public static final char COMMANDCHAR_REDUCED_ICON = 0x1D;
		public static final char COMMANDCHAR_SICN = 0x1E;
		
		public static final char MACROMAN_COMMAND_KEY = 0x11;
		public static final char MACROMAN_CHECK_MARK = 0x12;
		public static final char MACROMAN_DIAMOND = 0x13;
		public static final char MACROMAN_CLOSED_APPLE = 0x14;
		
		public static final char UNICODE_COMMAND_KEY = '\u2318';
		public static final char UNICODE_CHECK_MARK = '\u2713';
		public static final char UNICODE_DIAMOND = '\u25C6';
		public static final char UNICODE_CLOSED_APPLE = '\uF8FF';
		
		public static final byte STYLE_BOLD = 0x01;
		public static final byte STYLE_ITALIC = 0x02;
		public static final byte STYLE_UNDERLINE = 0x04;
		public static final byte STYLE_OUTLINE = 0x08;
		public static final byte STYLE_SHADOW = 0x10;
		public static final byte STYLE_CONDENSE = 0x20;
		public static final byte STYLE_EXTEND = 0x40;
		
		public boolean enabled;
		public String menuItemName;
		public short iconID;
		public char commandChar;
		public char markChar;
		public short submenuID; // derived from markChar
		public byte style;
		public int commandNumber;
		
		public void fixChars() {
			switch (commandChar) {
			case MACROMAN_COMMAND_KEY: commandChar = UNICODE_COMMAND_KEY; break;
			case MACROMAN_CHECK_MARK: commandChar = UNICODE_CHECK_MARK; break;
			case MACROMAN_DIAMOND: commandChar = UNICODE_DIAMOND; break;
			case MACROMAN_CLOSED_APPLE: commandChar = UNICODE_CLOSED_APPLE; break;
			}
			switch (markChar) {
			case MACROMAN_COMMAND_KEY: markChar = UNICODE_COMMAND_KEY; break;
			case MACROMAN_CHECK_MARK: markChar = UNICODE_CHECK_MARK; break;
			case MACROMAN_DIAMOND: markChar = UNICODE_DIAMOND; break;
			case MACROMAN_CLOSED_APPLE: markChar = UNICODE_CLOSED_APPLE; break;
			}
			menuItemName = menuItemName.replace(MACROMAN_COMMAND_KEY, UNICODE_COMMAND_KEY);
			menuItemName = menuItemName.replace(MACROMAN_CHECK_MARK, UNICODE_CHECK_MARK);
			menuItemName = menuItemName.replace(MACROMAN_DIAMOND, UNICODE_DIAMOND);
			menuItemName = menuItemName.replace(MACROMAN_CLOSED_APPLE, UNICODE_CLOSED_APPLE);
		}
		
		public void unfixChars() {
			switch (commandChar) {
			case UNICODE_COMMAND_KEY: commandChar = MACROMAN_COMMAND_KEY; break;
			case UNICODE_CHECK_MARK: commandChar = MACROMAN_CHECK_MARK; break;
			case UNICODE_DIAMOND: commandChar = MACROMAN_DIAMOND; break;
			case UNICODE_CLOSED_APPLE: commandChar = MACROMAN_CLOSED_APPLE; break;
			}
			switch (markChar) {
			case UNICODE_COMMAND_KEY: markChar = MACROMAN_COMMAND_KEY; break;
			case UNICODE_CHECK_MARK: markChar = MACROMAN_CHECK_MARK; break;
			case UNICODE_DIAMOND: markChar = MACROMAN_DIAMOND; break;
			case UNICODE_CLOSED_APPLE: markChar = MACROMAN_CLOSED_APPLE; break;
			}
			menuItemName = menuItemName.replace(UNICODE_COMMAND_KEY, MACROMAN_COMMAND_KEY);
			menuItemName = menuItemName.replace(UNICODE_CHECK_MARK, MACROMAN_CHECK_MARK);
			menuItemName = menuItemName.replace(UNICODE_DIAMOND, MACROMAN_DIAMOND);
			menuItemName = menuItemName.replace(UNICODE_CLOSED_APPLE, MACROMAN_CLOSED_APPLE);
		}
		
		public Map<TextAttribute,?> getAttributes(Font f) {
			Map<TextAttribute,Object> m = new HashMap<TextAttribute,Object>();
			m.putAll(f.getAttributes());
			if ((style & STYLE_BOLD) != 0) {
				m.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
			} else {
				m.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
			}
			if ((style & STYLE_ITALIC) != 0) {
				m.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
			} else {
				m.put(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
			}
			if ((style & STYLE_UNDERLINE) != 0) {
				m.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			}
			// AWT has no Outline or Shadow
			if ((style & STYLE_CONDENSE) != 0) {
				m.put(TextAttribute.WIDTH, TextAttribute.WIDTH_CONDENSED);
			} else if ((style & STYLE_EXTEND) != 0) {
				m.put(TextAttribute.WIDTH, TextAttribute.WIDTH_EXTENDED);
			} else {
				m.put(TextAttribute.WIDTH, TextAttribute.WIDTH_REGULAR);
			}
			return m;
		}
		
		private Image getIcon(MacResourceProvider rp) {
			if (iconID == 0 || rp == null) return null;
			ColorIconResource ci = rp.get(ColorIconResource.RESOURCE_TYPE, iconID).shallowRecast(ColorIconResource.class);
			if (ci != null) {
				try {
					return ci.getImage();
				} catch (Exception e) {}
			}
			IconResource i = rp.get(IconResource.RESOURCE_TYPE, iconID).shallowRecast(IconResource.class);
			if (i != null) {
				try {
					return i.getImage();
				} catch (Exception e) {}
			}
			return null;
		}
		
		private Image getReducedIcon(MacResourceProvider rp) {
			Image big = getIcon(rp);
			if (big != null) {
				int w = big.getWidth(null)/2;
				int h = big.getHeight(null)/2;
				BufferedImage small = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				if (small != null) {
					Graphics g = small.getGraphics();
					if (g != null) {
						g.drawImage(big, 0, 0, w, h, null);
						return small;
					}
				}
			}
			return null;
		}
		
		private Image getSmallIcon(MacResourceProvider rp) {
			if (iconID == 0 || rp == null) return null;
			IconListResource i = rp.get(IconListResource.RESOURCE_TYPE, iconID).shallowRecast(IconListResource.class);
			if (i != null) {
				try {
					return i.getIcon(0).getImage();
				} catch (Exception e) {}
			}
			return null;
		}
		
		private MenuShortcut getShortcut() {
			switch (commandChar) {
			case 'A': case 'a': return new MenuShortcut(KeyEvent.VK_A, false);
			case 'B': case 'b': return new MenuShortcut(KeyEvent.VK_B, false);
			case 'C': case 'c': return new MenuShortcut(KeyEvent.VK_C, false);
			case 'D': case 'd': return new MenuShortcut(KeyEvent.VK_D, false);
			case 'E': case 'e': return new MenuShortcut(KeyEvent.VK_E, false);
			case 'F': case 'f': return new MenuShortcut(KeyEvent.VK_F, false);
			case 'G': case 'g': return new MenuShortcut(KeyEvent.VK_G, false);
			case 'H': case 'h': return new MenuShortcut(KeyEvent.VK_H, false);
			case 'I': case 'i': return new MenuShortcut(KeyEvent.VK_I, false);
			case 'J': case 'j': return new MenuShortcut(KeyEvent.VK_J, false);
			case 'K': case 'k': return new MenuShortcut(KeyEvent.VK_K, false);
			case 'L': case 'l': return new MenuShortcut(KeyEvent.VK_L, false);
			case 'M': case 'm': return new MenuShortcut(KeyEvent.VK_M, false);
			case 'N': case 'n': return new MenuShortcut(KeyEvent.VK_N, false);
			case 'O': case 'o': return new MenuShortcut(KeyEvent.VK_O, false);
			case 'P': case 'p': return new MenuShortcut(KeyEvent.VK_P, false);
			case 'Q': case 'q': return new MenuShortcut(KeyEvent.VK_Q, false);
			case 'R': case 'r': return new MenuShortcut(KeyEvent.VK_R, false);
			case 'S': case 's': return new MenuShortcut(KeyEvent.VK_S, false);
			case 'T': case 't': return new MenuShortcut(KeyEvent.VK_T, false);
			case 'U': case 'u': return new MenuShortcut(KeyEvent.VK_U, false);
			case 'V': case 'v': return new MenuShortcut(KeyEvent.VK_V, false);
			case 'W': case 'w': return new MenuShortcut(KeyEvent.VK_W, false);
			case 'X': case 'x': return new MenuShortcut(KeyEvent.VK_X, false);
			case 'Y': case 'y': return new MenuShortcut(KeyEvent.VK_Y, false);
			case 'Z': case 'z': return new MenuShortcut(KeyEvent.VK_Z, false);
			case '1': return new MenuShortcut(KeyEvent.VK_1, false);
			case '2': return new MenuShortcut(KeyEvent.VK_2, false);
			case '3': return new MenuShortcut(KeyEvent.VK_3, false);
			case '4': return new MenuShortcut(KeyEvent.VK_4, false);
			case '5': return new MenuShortcut(KeyEvent.VK_5, false);
			case '6': return new MenuShortcut(KeyEvent.VK_6, false);
			case '7': return new MenuShortcut(KeyEvent.VK_7, false);
			case '8': return new MenuShortcut(KeyEvent.VK_8, false);
			case '9': return new MenuShortcut(KeyEvent.VK_9, false);
			case '0': return new MenuShortcut(KeyEvent.VK_0, false);
			case '!': return new MenuShortcut(KeyEvent.VK_1, true);
			case '@': return new MenuShortcut(KeyEvent.VK_2, true);
			case '#': return new MenuShortcut(KeyEvent.VK_3, true);
			case '$': return new MenuShortcut(KeyEvent.VK_4, true);
			case '%': return new MenuShortcut(KeyEvent.VK_5, true);
			case '^': return new MenuShortcut(KeyEvent.VK_6, true);
			case '&': return new MenuShortcut(KeyEvent.VK_7, true);
			case '*': return new MenuShortcut(KeyEvent.VK_8, true);
			case '(': return new MenuShortcut(KeyEvent.VK_9, true);
			case ')': return new MenuShortcut(KeyEvent.VK_0, true);
			case '`': return new MenuShortcut(KeyEvent.VK_BACK_QUOTE, false);
			case '~': return new MenuShortcut(KeyEvent.VK_BACK_QUOTE, true);
			case '-': return new MenuShortcut(KeyEvent.VK_MINUS, false);
			case '_': return new MenuShortcut(KeyEvent.VK_MINUS, true);
			case '=': return new MenuShortcut(KeyEvent.VK_EQUALS, false);
			case '+': return new MenuShortcut(KeyEvent.VK_EQUALS, true);
			case '[': return new MenuShortcut(KeyEvent.VK_OPEN_BRACKET, false);
			case '{': return new MenuShortcut(KeyEvent.VK_OPEN_BRACKET, true);
			case ']': return new MenuShortcut(KeyEvent.VK_CLOSE_BRACKET, false);
			case '}': return new MenuShortcut(KeyEvent.VK_CLOSE_BRACKET, true);
			case '\\': return new MenuShortcut(KeyEvent.VK_BACK_SLASH, false);
			case '|': return new MenuShortcut(KeyEvent.VK_BACK_SLASH, true);
			case ';': return new MenuShortcut(KeyEvent.VK_SEMICOLON, false);
			case ':': return new MenuShortcut(KeyEvent.VK_SEMICOLON, true);
			case '\'': return new MenuShortcut(KeyEvent.VK_QUOTE, false);
			case '\"': return new MenuShortcut(KeyEvent.VK_QUOTE, true);
			case ',': return new MenuShortcut(KeyEvent.VK_COMMA, false);
			case '<': return new MenuShortcut(KeyEvent.VK_COMMA, true);
			case '.': return new MenuShortcut(KeyEvent.VK_PERIOD, false);
			case '>': return new MenuShortcut(KeyEvent.VK_PERIOD, true);
			case '/': return new MenuShortcut(KeyEvent.VK_SLASH, false);
			case '?': return new MenuShortcut(KeyEvent.VK_SLASH, true);
			default: return null;
			}
		}
		
		public java.awt.MenuItem makeMenuItem(MacResourceProvider rp) {
			if (commandChar == COMMANDCHAR_SUBMENU) {
				Menu m = rp.get(MenuResource.RESOURCE_TYPE, submenuID).shallowRecast(MenuResource.class).makeMenu(rp);
				m.setEnabled(enabled);
				m.setLabel(menuItemName);
				m.setFont(new Font(getAttributes(m.getFont())));
				return m;
			} else {
				java.awt.MenuItem m = new java.awt.MenuItem(menuItemName);
				m.setEnabled(enabled);
				switch (commandChar) {
				case 0:
				case COMMANDCHAR_SCRIPT:
					break;
				case COMMANDCHAR_REDUCED_ICON:
					break;
				case COMMANDCHAR_SICN:
					break;
				default:
					m.setShortcut(getShortcut());
					break;
				}
				m.setFont(new Font(getAttributes(m.getFont())));
				return m;
			}
		}
		
		public JMenuItem makeJMenuItem(MacResourceProvider rp) {
			if (commandChar == COMMANDCHAR_SUBMENU) {
				JMenu m = rp.get(MenuResource.RESOURCE_TYPE, submenuID).shallowRecast(MenuResource.class).makeJMenu(rp);
				m.setEnabled(enabled);
				m.setText(menuItemName);
				if (iconID != 0) {
					Image i = getIcon(rp);
					if (i != null) m.setIcon(new ImageIcon(i));
				}
				m.setFont(new Font(getAttributes(m.getFont())));
				return m;
			} else {
				JMenuItem m = new JMenuItem(menuItemName);
				m.setEnabled(enabled);
				switch (commandChar) {
				case 0:
				case COMMANDCHAR_SCRIPT:
					break;
				case COMMANDCHAR_REDUCED_ICON:
					if (iconID != 0) {
						Image i = getReducedIcon(rp);
						if (i != null) m.setIcon(new ImageIcon(i));
					}
					break;
				case COMMANDCHAR_SICN:
					if (iconID != 0) {
						Image i = getSmallIcon(rp);
						if (i != null) m.setIcon(new ImageIcon(i));
					}
					break;
				default:
					if (iconID != 0) {
						Image i = getIcon(rp);
						if (i != null) m.setIcon(new ImageIcon(i));
					}
					m.setAccelerator(KeyStroke.getKeyStroke(commandChar, m.getToolkit().getMenuShortcutKeyMask()));
					break;
				}
				m.setSelected(markChar != 0);
				m.setFont(new Font(getAttributes(m.getFont())));
				return m;
			}
		}
	}
}
