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
import com.kreative.ksfl.KSFLUtilities;

public class PESection implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	public byte[] name = new byte[8];
	public int virtualSize;
	public int virtualAddress;
	public int rawDataSize;
	public int rawDataOfst;
	public int relocOfst;
	public int lineNumOfst;
	public short relocCnt;
	public short lineNumCnt;
	public static final int CHARACTERISTICS_NO_PAD = 0x00000008; // The section should not be padded to the next boundary. This flag is obsolete and is replaced by CHARACTERISTICS_ALIGN_1BYTES. This is valid only for object files.
	public static final int CHARACTERISTICS_CODE = 0x00000020; // The section contains executable code.
	public static final int CHARACTERISTICS_INITIALIZED_DATA = 0x00000040; // The section contains initialized data.
	public static final int CHARACTERISTICS_UNINITIALIZED_DATA = 0x00000080; // The section contains uninitialized data.
	public static final int CHARACTERISTICS_OTHER = 0x00000100; // Reserved for future use.
	public static final int CHARACTERISTICS_INFO = 0x00000200; // The section contains comments or other information. The .drectve section has this type. This is valid for object files only.
	public static final int CHARACTERISTICS_REMOVE = 0x00000800; // The section will not become part of the image. This is valid only for object files.
	public static final int CHARACTERISTICS_COMDAT = 0x00001000; // The section contains COMDAT data. For more information, see section 5.5.6, ÒCOMDAT Sections (Object Only).Ó This is valid only for object files.
	public static final int CHARACTERISTICS_GPREL = 0x00008000; // The section contains data referenced through the global pointer (GP).
	public static final int CHARACTERISTICS_PURGEABLE = 0x00020000; // Reserved for future use.
	public static final int CHARACTERISTICS_16BIT = 0x00020000; // Reserved for future use.
	public static final int CHARACTERISTICS_LOCKED = 0x00040000; // Reserved for future use.
	public static final int CHARACTERISTICS_PRELOAD = 0x00080000; // Reserved for future use.
	public static final int CHARACTERISTICS_ALIGN = 0x00F00000; // Power of 2 to which to align data
	public static final int CHARACTERISTICS_ALIGN_1BYTES = 0x00100000; // Align data on a 1-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_2BYTES = 0x00200000; // Align data on a 2-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_4BYTES = 0x00300000; // Align data on a 4-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_8BYTES = 0x00400000; // Align data on an 8-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_16BYTES = 0x00500000; // Align data on a 16-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_32BYTES = 0x00600000; // Align data on a 32-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_64BYTES = 0x00700000; // Align data on a 64-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_128BYTES = 0x00800000; // Align data on a 128-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_256BYTES = 0x00900000; // Align data on a 256-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_512BYTES = 0x00A00000; // Align data on a 512-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_1024BYTES = 0x00B00000; // Align data on a 1024-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_2048BYTES = 0x00C00000; // Align data on a 2048-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_4096BYTES = 0x00D00000; // Align data on a 4096-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_8192BYTES = 0x00E00000; // Align data on an 8192-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_ALIGN_16384BYTES = 0x00F00000; // Align data on a 16384-byte boundary. Valid only for object files.
	public static final int CHARACTERISTICS_NRELOC_OVFL = 0x01000000; // The section contains extended relocations.
	public static final int CHARACTERISTICS_DISCARDABLE = 0x02000000; // The section can be discarded as needed.
	public static final int CHARACTERISTICS_NOT_CACHED = 0x04000000; // The section cannot be cached.
	public static final int CHARACTERISTICS_NOT_PAGED = 0x08000000; // The section is not pageable.
	public static final int CHARACTERISTICS_SHARED = 0x10000000; // The section can be shared in memory.
	public static final int CHARACTERISTICS_EXECUTE = 0x20000000; // The section can be executed as code.
	public static final int CHARACTERISTICS_READ = 0x40000000; // The section can be read.
	public static final int CHARACTERISTICS_WRITE = 0x80000000; // The section can be written to.
	public int characteristics;
	public byte[] data;
	
	public PESection clone() {
		PESection s = new PESection();
		s.name = KSFLUtilities.resize(name, 8);
		s.virtualSize = virtualSize;
		s.virtualAddress = virtualAddress;
		s.rawDataSize = rawDataSize;
		s.rawDataOfst = rawDataOfst;
		s.relocOfst = relocOfst;
		s.lineNumOfst = lineNumOfst;
		s.relocCnt = relocCnt;
		s.lineNumCnt = lineNumCnt;
		s.characteristics = characteristics;
		s.data = KSFLUtilities.copy(data);
		return s;
	}
}
