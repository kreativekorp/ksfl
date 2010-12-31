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

package com.kreative.ksfl;

import com.kreative.dff.DFFResource;
import com.kreative.prc.PalmResource;
import com.kreative.rsrc.MacResource;

public class KSFLConverter {
	private KSFLConverter() {}
	
	/**
	 * Creates a new <code>DFFResource</code> equivalent to
	 * the specified <code>MacResource</code>.
	 * @param r the <code>MacResource</code> to convert.
	 * @return an equivalent <code>DFFResource</code>.
	 */
	public static DFFResource makeDFFResourceFromMacResource(MacResource r) {
		if (r == null) return null;
		DFFResource d = new DFFResource(KSFLUtilities.eccAdaptMacResType(r.type), (int)r.id, r.name, r.data);
		d.compressed = r.compressed;
		d.fixed = r.locked;
		d.fromrsrc = true;
		d.preload = r.preload;
		d.protect = r.protect;
		d.purgeable = r.purgeable;
		d.readonly = r.protect;
		d.system = r.sysheap;
		return d;
	}
	
	/**
	 * Creates a new <code>DFFResource</code> equivalent to
	 * the specified <code>PalmResource</code>.
	 * @param r the <code>PalmResource</code> to convert.
	 * @return an equivalent <code>DFFResource</code>.
	 */
	public static DFFResource makeDFFResourceFromPalmResource(PalmResource r) {
		if (r == null) return null;
		DFFResource d = new DFFResource(KSFLUtilities.eccAdaptPalmResType(r.type), (int)r.id, r.data);
		d.fromrsrc = true;
		return d;
	}
	
	/**
	 * Creates a new <code>MacResource</code> equivalent to
	 * the specified <code>PalmResource</code>.
	 * @param r the <code>PalmResource</code> to convert.
	 * @return an equivalent <code>MacResource</code>.
	 */
	public static MacResource makeMacResourceFromPalmResource(PalmResource r) {
		if (r == null) return null;
		return new MacResource(r.type, r.id, r.data);
	}
	
	/**
	 * Creates a new <code>PalmResource</code> equivalent to
	 * the specified <code>MacResource</code>.
	 * Name and attributes are lost in the process.
	 * @param r the <code>MacResource</code> to convert.
	 * @return an equivalent <code>PalmResource</code>.
	 */
	public static PalmResource makePalmResourceFromMacResource(MacResource r) {
		if (r == null) return null;
		return new PalmResource(r.type, r.id, r.data);
	}
	
	/**
	 * Creates a new <code>PalmResource</code> equivalent to
	 * the specified <code>DFFResource</code>.
	 * The original type, out-of-range ID numbers, data type,
	 * name, and attributes are lost in the process.
	 * @param d the <code>DFFResource</code> to convert.
	 * @return an equivalent <code>PalmResource</code>.
	 */
	public static PalmResource makePalmResourceFromDFFResource(DFFResource d) {
		if (d == null) return null;
		long tmp; int type;
		if (
				((tmp = d.type & 0xFFFFFFFF00000000l) == 0x4D61632000000000l) ||
				(tmp == 0x4D61635F00000000l) ||
				(tmp == 0x4D63527300000000l) ||
				(tmp == 0x50616C6D00000000l) ||
				(tmp == 0x5052432000000000l) ||
				(tmp == 0x5052435F00000000l) ||
				(tmp == 0x496D672000000000l) ||
				(tmp == 0x4175642000000000l) ||
				(tmp == 0x536E642000000000l) ||
				(tmp == 0x5669642000000000l) ||
				(tmp == 0x4D6F762000000000l)
		) {
			type = (int)(d.type & 0xFFFFFFFFl);
		}
		else if (
				((tmp = d.type & 0xFFFFFFFFFF000000l) == 0x496D616765000000l) ||
				(tmp == 0x417564696F000000l) ||
				(tmp == 0x536F756E64000000l) ||
				(tmp == 0x566964656F000000l) ||
				(tmp == 0x4D6F766965000000l)
		) {
			type = ((int)(d.type & 0xFFFFFFl) << 8) | 0x20;
		}
		else {
			type = (int)(d.type >>> 32l);
		}
		short id = (d.id > Short.MAX_VALUE) ? Short.MAX_VALUE : (d.id < Short.MIN_VALUE) ? Short.MIN_VALUE : (short)d.id;
		return new PalmResource(type, id, d.data);
	}
	
	/**
	 * Creates a new <code>Resource</code> equivalent to
	 * the specified <code>DFFResource</code>.
	 * The original type, out-of-range ID numbers, data type,
	 * and some attributes are lost in the process.
	 * @param d the <code>DFFResource</code> to convert.
	 * @return an equivalent <code>Resource</code>.
	 */
	public static MacResource makeMacResourceFromDFFResource(DFFResource d) {
		if (d == null) return null;
		long tmp; int type;
		if (
				((tmp = d.type & 0xFFFFFFFF00000000l) == 0x4D61632000000000l) ||
				(tmp == 0x4D61635F00000000l) ||
				(tmp == 0x4D63527300000000l) ||
				(tmp == 0x50616C6D00000000l) ||
				(tmp == 0x5052432000000000l) ||
				(tmp == 0x5052435F00000000l) ||
				(tmp == 0x496D672000000000l) ||
				(tmp == 0x4175642000000000l) ||
				(tmp == 0x536E642000000000l) ||
				(tmp == 0x5669642000000000l) ||
				(tmp == 0x4D6F762000000000l)
		) {
			type = (int)(d.type & 0xFFFFFFFFl);
		}
		else if (
				((tmp = d.type & 0xFFFFFFFFFF000000l) == 0x496D616765000000l) ||
				(tmp == 0x417564696F000000l) ||
				(tmp == 0x536F756E64000000l) ||
				(tmp == 0x566964656F000000l) ||
				(tmp == 0x4D6F766965000000l)
		) {
			type = ((int)(d.type & 0xFFFFFFl) << 8) | 0x20;
		}
		else {
			type = (int)(d.type >>> 32l);
		}
		short id = (d.id > Short.MAX_VALUE) ? Short.MAX_VALUE : (d.id < Short.MIN_VALUE) ? Short.MIN_VALUE : (short)d.id;
		MacResource r = new MacResource(type, id, d.name, d.data);
		r.compressed = d.compressed;
		r.locked = d.fixed;
		r.preload = d.preload;
		r.protect = d.readonly || d.protect;
		r.purgeable = d.purgeable;
		r.sysheap = d.system;
		return r;
	}
}
