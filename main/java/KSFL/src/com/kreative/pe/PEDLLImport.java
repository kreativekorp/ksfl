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

import java.io.Serializable;

public class PEDLLImport implements Cloneable, Serializable {
	public static final long serialVersionUID = 1L;
	public int timeStamp;
	public short nameOfst;
	public short characteristics;
	public String name;
	
	public PEDLLImport() {
		this.timeStamp = 0;
		this.nameOfst = 0;
		this.characteristics = 0;
		this.name = "";
	}
	
	public PEDLLImport(int time, short nameofst, short chars, String name) {
		this.timeStamp = time;
		this.nameOfst = nameofst;
		this.characteristics = chars;
		this.name = name;
	}
	
	public PEDLLImport clone() {
		return new PEDLLImport(this.timeStamp, this.nameOfst, this.characteristics, this.name);
	}
}
