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

package com.kreative.pe;

public class PEResourceData extends PEResourceEntry {
	private static final long serialVersionUID = 1L;
	public byte[] data;
	public int codePage;
	public int reserved;
	
	public PEResourceData() {
		super();
		this.data = new byte[0];
		this.codePage = 0;
		this.reserved = 0;
	}
	
	public PEResourceData(int id, byte[] data, int cp) {
		super(id);
		this.data = data;
		this.codePage = cp;
		this.reserved = 0;
	}
	
	public PEResourceData(String name, byte[] data, int cp) {
		super(name);
		this.data = data;
		this.codePage = cp;
		this.reserved = 0;
	}
	
	public PEResourceData(int id, String name, byte[] data, int cp) {
		super(id, name);
		this.data = data;
		this.codePage = cp;
		this.reserved = 0;
	}
	
	public PEResourceData clone() {
		PEResourceData rd = new PEResourceData(id, name, data, codePage);
		rd.reserved = this.reserved;
		return rd;
	}
}
