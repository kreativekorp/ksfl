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

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.kreative.ksfl.KSFLUtilities;

public class PEImage implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	public static final byte[] MSDOS_STUB_DEFAULT = new byte[] {
		(byte) 'M', (byte) 'Z', (byte)0x90, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF, (byte)0x00, (byte)0x00,
		(byte)0xB8, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x40, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00,
		(byte)0x0E, (byte)0x1F, (byte)0xBA, (byte)0x0E, (byte)0x00, (byte)0xB4, (byte)0x09, (byte)0xCD,
		(byte)0x21, (byte)0xB8, (byte)0x01, (byte)0x4C, (byte)0xCD, (byte)0x21, (byte) 'T', (byte) 'h',
		(byte) 'i', (byte) 's', (byte) ' ', (byte) 'p', (byte) 'r', (byte) 'o', (byte) 'g', (byte) 'r',
		(byte) 'a', (byte) 'm', (byte) ' ', (byte) 'c', (byte) 'a', (byte) 'n', (byte) 'n', (byte) 'o',
		(byte) 't', (byte) ' ', (byte) 'b', (byte) 'e', (byte) ' ', (byte) 'r', (byte) 'u', (byte) 'n',
		(byte) ' ', (byte) 'i', (byte) 'n', (byte) ' ', (byte) 'D', (byte) 'O', (byte) 'S', (byte) ' ',
		(byte) 'm', (byte) 'o', (byte) 'd', (byte) 'e', (byte) '.', (byte)'\r', (byte)'\r', (byte)'\n',
		(byte)0x24, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
	};
	public byte[] msdosStub;
	public static final int PE_SIGNATURE = 0x4550;
	public int peSignature;
	public static final short MACHINE_UNKNOWN = 0x0; // The contents of this field are assumed to be applicable to any machine type
	public static final short MACHINE_AM33 = 0x1d3; // Matsushita AM33
	public static final short MACHINE_AMD64 = (short)0x8664; // x64
	public static final short MACHINE_ARM = 0x1c0; // ARM little endian
	public static final short MACHINE_EBC = 0xebc; // EFI byte code
	public static final short MACHINE_I386 = 0x14c; // Intel 386 or later processors and compatible processors
	public static final short MACHINE_IA64 = 0x200; // Intel Itanium processor family
	public static final short MACHINE_M32R = (short)0x9041; // Mitsubishi M32R little endian
	public static final short MACHINE_MIPS16 = 0x266; // MIPS16
	public static final short MACHINE_MIPSFPU = 0x366; // MIPS with FPU
	public static final short MACHINE_MIPSFPU16 = 0x466; // MIPS16 with FPU
	public static final short MACHINE_POWERPC = 0x1f0; // Power PC little endian
	public static final short MACHINE_POWERPCFP = 0x1f1; // Power PC with floating point support
	public static final short MACHINE_R4000 = 0x166; // MIPS little endian
	public static final short MACHINE_SH3 = 0x1a2; // Hitachi SH3
	public static final short MACHINE_SH3DSP = 0x1a3; // Hitachi SH3 DSP
	public static final short MACHINE_SH4 = 0x1a6; // Hitachi SH4
	public static final short MACHINE_SH5 = 0x1a8; // Hitachi SH5
	public static final short MACHINE_THUMB = 0x1c2; // Thumb
	public static final short MACHINE_WCEMIPSV2 = 0x169; // MIPS little-endian WCE v2
	public short machine;
	public short numSections;
	public int creationDate;
	public int symbolTablePtr;
	public int numSymbols;
	public short optHeaderSize;
	public static final short CHARACTERISTICS_RELOCS_STRIPPED = 0x0001; // Image only, Windows CE, and Microsoft Windows NT¨ and later. This indicates that the file does not contain base relocations and must therefore be loaded at its preferred base address. If the base address is not available, the loader reports an error. The default behavior of the linker is to strip base relocations from executable (EXE) files.
	public static final short CHARACTERISTICS_EXECUTABLE_IMAGE = 0x0002; // Image only. This indicates that the image file is valid and can be run. If this flag is not set, it indicates a linker error.
	public static final short CHARACTERISTICS_LINE_NUMS_STRIPPED = 0x0004; // COFF line numbers have been removed. This flag is deprecated and should be zero.
	public static final short CHARACTERISTICS_LOCAL_SYMS_STRIPPED = 0x0008; // COFF symbol table entries for local symbols have been removed. This flag is deprecated and should be zero.
	public static final short CHARACTERISTICS_AGGRESSIVE_WS_TRIM = 0x0010; // Obsolete. Aggressively trim working set. This flag is deprecated for Windows 2000 and later and must be zero.
	public static final short CHARACTERISTICS_LARGE_ADDRESS_AWARE = 0x0020; // Application can handle > 2 GB addresses.
	public static final short CHARACTERISTICS_RESERVED = 0x0040; // This flag is reserved for future use.
	public static final short CHARACTERISTICS_BYTES_REVERSED_LO = 0x0080; // Little endian: the least significant bit (LSB) precedes the most significant bit (MSB) in memory. This flag is deprecated and should be zero.
	public static final short CHARACTERISTICS_32BIT_MACHINE = 0x0100; // Machine is based on a 32-bit-word architecture.
	public static final short CHARACTERISTICS_DEBUG_STRIPPED = 0x0200; // Debugging information is removed from the image file.
	public static final short CHARACTERISTICS_REMOVABLE_RUN_FROM_SWAP = 0x0400; // If the image is on removable media, fully load it and copy it to the swap file.
	public static final short CHARACTERISTICS_NET_RUN_FROM_SWAP = 0x0800; // If the image is on network media, fully load it and copy it to the swap file.
	public static final short CHARACTERISTICS_SYSTEM = 0x1000; // The image file is a system file, not a user program.
	public static final short CHARACTERISTICS_DLL = 0x2000; // The image file is a dynamic-link library (DLL). Such files are considered executable files for almost all purposes, although they cannot be directly run.
	public static final short CHARACTERISTICS_UP_SYSTEM_ONLY = 0x4000; // The file should be run only on a uniprocessor machine.
	public static final short CHARACTERISTICS_BYTES_REVERSED_HI = (short)0x8000; // Big endian: the MSB precedes the LSB in memory. This flag is deprecated and should be zero.
	public short characteristics;
	public static final short MAGIC_ROM_IMAGE = 0x107;
	public static final short MAGIC_PE32 = 0x10B;
	public static final short MAGIC_PE32PLUS = 0x20B;
	public short magic;
	public short linkerVersion;
	public int codeSize;
	public int dataSize;
	public int bssSize;
	public int entryPointOfst;
	public int codeOfst;
	public int dataOfst;
	public long base;
	public int sectionAlign;
	public int fileAlign;
	public int osVersion;
	public int imageVersion;
	public static final int SUBSYSTEM_VERSION_WIN32 = 0x40000;
	public int subsystemVersion;
	public int win32versionValue;
	public int sizeOfImage;
	public int sizeOfHeaders;
	public int checksum;
	public static final short SUBSYSTEM_UNKNOWN = 0; // An unknown subsystem
	public static final short SUBSYSTEM_NATIVE = 1; // Device drivers and native Windows processes
	public static final short SUBSYSTEM_WINDOWS_GUI = 2; // The Windows graphical user interface (GUI) subsystem
	public static final short SUBSYSTEM_WINDOWS_CUI = 3; // The Windows character subsystem
	public static final short SUBSYSTEM_OS2_CUI = 5; // The OS/2 character subsystem
	public static final short SUBSYSTEM_POSIX_CUI = 7; // The Posix character subsystem
	public static final short SUBSYSTEM_WINDOWS_CE_GUI = 9; // Windows CE
	public static final short SUBSYSTEM_EFI_APPLICATION = 10; // An Extensible Firmware Interface (EFI) application
	public static final short SUBSYSTEM_EFI_BOOT_SERVICE_DRIVER = 11; // An EFI driver with boot services
	public static final short SUBSYSTEM_EFI_RUNTIME_DRIVER = 12; // An EFI driver with run-time services
	public static final short SUBSYSTEM_EFI_ROM = 13; // An EFI ROM image
	public static final short SUBSYSTEM_XBOX = 14; // XBOX
	public short subsystem;
	public static final short DLL_CHARACTERISTICS_NOTIFY_PROC_ATTACH = 0x0001;
	public static final short DLL_CHARACTERISTICS_NOTIFY_THREAD_DETACH = 0x0002;
	public static final short DLL_CHARACTERISTICS_NOTIFY_THREAD_ATTACH = 0x0004;
	public static final short DLL_CHARACTERISTICS_NOTIFY_PROC_DETACH = 0x0008;
	public static final short DLL_CHARACTERISTICS_DYNAMIC_BASE = 0x0040; // DLL can be relocated at load time.
	public static final short DLL_CHARACTERISTICS_FORCE_INTEGRITY = 0x0080; // Code Integrity checks are enforced.
	public static final short DLL_CHARACTERISTICS_NX_COMPAT = 0x0100; // Image is NX compatible.
	public static final short DLL_CHARACTERISTICS_NO_ISOLATION = 0x0200; // Isolation aware, but do not isolate the image.
	public static final short DLL_CHARACTERISTICS_NO_SEH = 0x0400; // Does not use structured exception (SE) handling. No SE handler may be called in this image.
	public static final short DLL_CHARACTERISTICS_NO_BIND = 0x0800; // Do not bind the image.
	public static final short DLL_CHARACTERISTICS_WDM_DRIVER = 0x2000; // A WDM driver.
	public static final short DLL_CHARACTERISTICS_TERMINAL_SERVER_AWARE = (short)0x8000; // Terminal Server aware.
	public short dllCharacteristics;
	public long stackReserveSize;
	public long stackCommitSize;
	public long heapReserveSize;
	public long heapCommitSize;
	public int loaderFlags;
	public int numberOfRvaAndSizes;
	public static final int DIR_ENTRY_EXPORTED_SYMBOLS = 0;
	public static final int DIR_ENTRY_IMPORTED_SYMBOLS = 1;
	public static final int DIR_ENTRY_RESOURCES = 2;
	public static final int DIR_ENTRY_EXCEPTIONS = 3;
	public static final int DIR_ENTRY_CERTIFICATES = 4;
	public static final int DIR_ENTRY_BASE_RELOCATION = 5;
	public static final int DIR_ENTRY_DEBUG = 6;
	public static final int DIR_ENTRY_ARCHITECTURE = 7;
	public static final int DIR_ENTRY_GLOBAL_POINTER = 8;
	public static final int DIR_ENTRY_THREAD_LOCAL_STORAGE = 9;
	public static final int DIR_ENTRY_LOAD_CONFIGURATION = 10;
	public static final int DIR_ENTRY_BOUND_IMPORT = 11;
	public static final int DIR_ENTRY_IMPORT_ADDRESS_TABLE = 12;
	public static final int DIR_ENTRY_DELAY_IMPORT_DESCRIPTOR = 13;
	public static final int DIR_ENTRY_CLR_RUNTIME_HEADER = 14;
	public static final int DIR_ENTRY_RESERVED = 15;
	public List<PEDirectoryEntry> dirEntries = new ArrayList<PEDirectoryEntry>();
	public List<PESection> sections = new ArrayList<PESection>();
	public List<PEDLLImport> dllImports = new ArrayList<PEDLLImport>();
	
	public PEImage clone() {
		PEImage img = new PEImage();
		img.msdosStub = KSFLUtilities.copy(msdosStub);
		img.peSignature = peSignature;
		img.machine = machine;
		img.numSections = numSections;
		img.creationDate = creationDate;
		img.symbolTablePtr = symbolTablePtr;
		img.numSymbols = numSymbols;
		img.optHeaderSize = optHeaderSize;
		img.characteristics = characteristics;
		img.magic = magic;
		img.linkerVersion = linkerVersion;
		img.codeSize = codeSize;
		img.dataSize = dataSize;
		img.bssSize = bssSize;
		img.entryPointOfst = entryPointOfst;
		img.codeOfst = codeOfst;
		img.dataOfst = dataOfst;
		img.base = base;
		img.sectionAlign = sectionAlign;
		img.fileAlign = fileAlign;
		img.osVersion = osVersion;
		img.imageVersion = imageVersion;
		img.subsystemVersion = subsystemVersion;
		img.win32versionValue = win32versionValue;
		img.sizeOfImage = sizeOfImage;
		img.sizeOfHeaders = sizeOfHeaders;
		img.checksum = checksum;
		img.subsystem = subsystem;
		img.dllCharacteristics = dllCharacteristics;
		img.stackReserveSize = stackReserveSize;
		img.stackCommitSize = stackCommitSize;
		img.heapReserveSize = heapReserveSize;
		img.heapCommitSize = heapCommitSize;
		img.loaderFlags = loaderFlags;
		img.numberOfRvaAndSizes = numberOfRvaAndSizes;
		for (PEDirectoryEntry de : dirEntries) {
			img.dirEntries.add(de.clone());
		}
		for (PESection s : sections) {
			img.sections.add(s.clone());
		}
		for (PEDLLImport i : dllImports) {
			img.dllImports.add(i.clone());
		}
		return img;
	}
	
	private static int verswap(int v) {
		short major = (short)((v >>> 16) & 0xFFFF);
		short minor = (short)(v & 0xFFFF);
		major = Short.reverseBytes(major);
		minor = Short.reverseBytes(minor);
		return ((major & 0xFFFF) << 16) | (minor & 0xFFFF);
	}
	
	private static int nonZeroSize(byte[] b) {
		int l = b.length;
		while (l > 0 && b[l-1] == 0) l--;
		return l;
	}
	
	public int headerSize() {
		int hs
			= msdosStub.length //ms-dos stub
			+ 24 //pe signature and header
			+ ((magic != MAGIC_PE32PLUS)?96:112) //pe optional header (minus directories)
			+ (dirEntries.size()*8) //data directories
			+ (sections.size()*40) //section headers
			+ (dllImports.size()*9)+8 //dll imports and null terminators (minus string data)
		;
		Iterator<PEDLLImport> i = dllImports.iterator();
		while (i.hasNext()) hs += i.next().name.length(); //dll import strings (minus null terminators)
		return hs;
	}
	
	private int alignInMemory(int a) {
		int m = a % sectionAlign;
		if (m == 0) return a;
		else return a - m + sectionAlign;
	}
	
	private int alignInFile(int a) {
		int m = a % fileAlign;
		if (m == 0) return a;
		else return a - m + fileAlign;
	}
	
	public int[] ofstToSNO(int ofst, int hs) {
		if (ofst < 0) return null;
		else if (ofst >= 0 && ofst < alignInMemory(hs)) return new int[]{-1,ofst};
		else {
			Iterator<PESection> i = sections.iterator(); int ii = 0;
			while (i.hasNext()) {
				PESection s = i.next();
				if (ofst >= s.virtualAddress && ofst < s.virtualAddress+s.virtualSize) {
					return new int[]{ii,ofst-s.virtualAddress};
				}
				ii++;
			}
			return null;
		}
	}
	
	public int snoToOfst(int[] sno) {
		if (sno == null) return 0;
		else if (sno[0] < 0) return sno[1];
		else return sections.get(sno[0]).virtualAddress+sno[1];
	}
	
	public void decompile(RandomAccessFile f) throws IOException {
		f.seek(0);
		if (f.readShort() != 0x4D5A) throw new IOException("Not a valid EXE file.");
		f.seek(0x3C);
		int h = Integer.reverseBytes(f.readInt());
		f.seek(0);
		msdosStub = new byte[h]; f.read(msdosStub);
		peSignature = Integer.reverseBytes(f.readInt());
		if (peSignature != PE_SIGNATURE) throw new IOException("Not a valid PE file.");
		machine = Short.reverseBytes(f.readShort());
		numSections = Short.reverseBytes(f.readShort());
		creationDate = Integer.reverseBytes(f.readInt());
		symbolTablePtr = Integer.reverseBytes(f.readInt());
		numSymbols = Integer.reverseBytes(f.readInt());
		optHeaderSize = Short.reverseBytes(f.readShort());
		characteristics = Short.reverseBytes(f.readShort());
		long oh = f.getFilePointer();
		magic = Short.reverseBytes(f.readShort());
		if (!(magic == MAGIC_ROM_IMAGE || magic == MAGIC_PE32 || magic == MAGIC_PE32PLUS)) throw new IOException("Not a valid PE file.");
		linkerVersion = f.readShort();
		codeSize = Integer.reverseBytes(f.readInt());
		dataSize = Integer.reverseBytes(f.readInt());
		bssSize = Integer.reverseBytes(f.readInt());
		entryPointOfst = Integer.reverseBytes(f.readInt());
		codeOfst = Integer.reverseBytes(f.readInt());
		dataOfst = (magic != MAGIC_PE32PLUS)?Integer.reverseBytes(f.readInt()):0;
		base = (magic != MAGIC_PE32PLUS)?Integer.reverseBytes(f.readInt()):Long.reverseBytes(f.readLong());
		sectionAlign = Integer.reverseBytes(f.readInt());
		fileAlign = Integer.reverseBytes(f.readInt());
		osVersion = verswap(f.readInt());
		imageVersion = verswap(f.readInt());
		subsystemVersion = verswap(f.readInt());
		win32versionValue = Integer.reverseBytes(f.readInt());
		sizeOfImage = Integer.reverseBytes(f.readInt());
		sizeOfHeaders = Integer.reverseBytes(f.readInt());
		checksum = Integer.reverseBytes(f.readInt());
		subsystem = Short.reverseBytes(f.readShort());
		dllCharacteristics = Short.reverseBytes(f.readShort());
		stackReserveSize = (magic != MAGIC_PE32PLUS)?Integer.reverseBytes(f.readInt()):Long.reverseBytes(f.readLong());
		stackCommitSize = (magic != MAGIC_PE32PLUS)?Integer.reverseBytes(f.readInt()):Long.reverseBytes(f.readLong());
		heapReserveSize = (magic != MAGIC_PE32PLUS)?Integer.reverseBytes(f.readInt()):Long.reverseBytes(f.readLong());
		heapCommitSize = (magic != MAGIC_PE32PLUS)?Integer.reverseBytes(f.readInt()):Long.reverseBytes(f.readLong());
		loaderFlags = Integer.reverseBytes(f.readInt());
		numberOfRvaAndSizes = Integer.reverseBytes(f.readInt());
		dirEntries.clear();
		for (int i=0; i<numberOfRvaAndSizes; i++) {
			PEDirectoryEntry de = new PEDirectoryEntry();
			de.virtualAddress = Integer.reverseBytes(f.readInt());
			de.size = Integer.reverseBytes(f.readInt());
			dirEntries.add(de);
		}
		f.seek(oh+optHeaderSize);
		sections.clear();
		for (int i=0; i<numSections; i++) {
			PESection s = new PESection();
			s.name = new byte[8]; f.read(s.name);
			s.virtualSize = Integer.reverseBytes(f.readInt());
			s.virtualAddress = Integer.reverseBytes(f.readInt());
			s.rawDataSize = Integer.reverseBytes(f.readInt());
			s.rawDataOfst = Integer.reverseBytes(f.readInt());
			s.relocOfst = Integer.reverseBytes(f.readInt());
			s.lineNumOfst = Integer.reverseBytes(f.readInt());
			s.relocCnt = Short.reverseBytes(f.readShort());
			s.lineNumCnt = Short.reverseBytes(f.readShort());
			s.characteristics = Integer.reverseBytes(f.readInt());
			sections.add(s);
		}
		long dlli = f.getFilePointer();
		dllImports.clear();
		while (true) {
			int ts = Integer.reverseBytes(f.readInt());
			short no = Short.reverseBytes(f.readShort());
			short ch = Short.reverseBytes(f.readShort());
			if (ts == 0 && no == 0 && ch == 0) break;
			PEDLLImport di = new PEDLLImport();
			di.timeStamp = ts;
			di.nameOfst = no;
			di.characteristics = ch;
			dllImports.add(di);
		}
		Iterator<PEDLLImport> dit = dllImports.iterator();
		while (dit.hasNext()) {
			PEDLLImport di = dit.next();
			f.seek(dlli+di.nameOfst);
			di.name = ""; byte b;
			while ((b = f.readByte()) != 0) di.name += (char)(b & 0xFF);
		}
		for (int i=0; i<numSections; i++) {
			PESection s = sections.get(i);
			s.data = new byte[s.virtualSize];
			f.seek(s.rawDataOfst);
			f.read(s.data, 0, Math.min(s.virtualSize, s.rawDataSize));
		}
		//special magic for certain sections
		//this is simply recalculating RVAs as if the VA of the section is zero
		if (dirEntries.size() > 2) {
			PEDirectoryEntry de = dirEntries.get(2);
			int[] sno = ofstToSNO(de.virtualAddress, sizeOfHeaders);
			if (sno[0] >= 0) {
				PESection s = sections.get(sno[0]);
				PEResourceDirectory.subtractVA(s.data, sno[1], s.virtualAddress);
			}
		}
	}
	
	public void recalculate() {
		int hs = headerSize();
		//pe header
		peSignature = PE_SIGNATURE;
		numSections = (short)sections.size();
		symbolTablePtr = 0;
		numSymbols = 0;
		optHeaderSize = (short)(((magic != MAGIC_PE32PLUS)?96:112) + (dirEntries.size()*8));
		//optional header
		codeSize = 0; //codeSize will be added to
		dataSize = 0; //dataSize will be added to
		bssSize = 0; //bssSize will be added to
		int[] entryPointSNO = ofstToSNO(entryPointOfst,hs); //entryPointOfst to be reset later
		int[] codeSNO = ofstToSNO(codeOfst,hs); //codeOfst to be reset later
		int[] dataSNO = ofstToSNO(dataOfst,hs); //dataOfst to be reset later
		win32versionValue = 0;
		sizeOfImage = alignInMemory(hs); //sizeOfImage will be added to
		sizeOfHeaders = alignInFile(hs);
		checksum = 0; //has to be recalculated in recompile()
		loaderFlags = 0;
		numberOfRvaAndSizes = dirEntries.size();
		//directory entries
		int[][] dirEntrySNO = new int[dirEntries.size()][];
		int[] dirEntrySD = new int[dirEntries.size()];
		for (int i=0; i<dirEntries.size(); i++) {
			PEDirectoryEntry de = dirEntries.get(i);
			dirEntrySNO[i] = ofstToSNO(de.virtualAddress,hs); //virtualAddress to be reset later
			if (dirEntrySNO[i] != null) {
				if (dirEntrySNO[i][0] >= 0) {
					dirEntrySD[i] = sections.get(dirEntrySNO[i][0]).virtualSize - de.size; //size to be reset later
				} else {
					dirEntrySD[i] = de.size;
				}
			} else {
				dirEntrySD[i] = 0;
			}
		}
		//section headers
		int fa = sizeOfHeaders;
		Iterator<PESection> si = sections.iterator();
		while (si.hasNext()) {
			PESection s = si.next();
			int ms = alignInMemory(s.data.length);
			int fs = alignInFile(nonZeroSize(s.data));
			int fus = alignInFile(s.data.length);
			s.virtualSize = s.data.length;
			s.virtualAddress = sizeOfImage;
			s.rawDataSize = fs;
			s.rawDataOfst = fa;
			s.relocOfst = s.lineNumOfst = s.relocCnt = s.lineNumCnt = 0;
			sizeOfImage += ms;
			fa += fs;
			if ((s.characteristics & PESection.CHARACTERISTICS_CODE) != 0) codeSize += fus;
			if ((s.characteristics & PESection.CHARACTERISTICS_INITIALIZED_DATA) != 0) dataSize += fus;
			if ((s.characteristics & PESection.CHARACTERISTICS_UNINITIALIZED_DATA) != 0) bssSize += fus;
		}
		//dll imports
		int da = dllImports.size()*8 + 8;
		Iterator<PEDLLImport> di = dllImports.iterator();
		while (di.hasNext()) {
			PEDLLImport d = di.next();
			d.nameOfst = (short)da;
			da += d.name.length()+1;
		}
		//as promised
		entryPointOfst = snoToOfst(entryPointSNO);
		codeOfst = snoToOfst(codeSNO);
		dataOfst = snoToOfst(dataSNO);
		for (int i=0; i<dirEntries.size(); i++) {
			PEDirectoryEntry de = dirEntries.get(i);
			de.virtualAddress = snoToOfst(dirEntrySNO[i]);
			if (dirEntrySNO[i] != null) {
				if (dirEntrySNO[i][0] >= 0) {
					de.size = sections.get(dirEntrySNO[i][0]).virtualSize - dirEntrySD[i];
				} else {
					de.size = dirEntrySD[i];
				}
			} else {
				de.size = 0;
			}
		}
	}
	
	public void recompile(RandomAccessFile f) throws IOException {
		f.seek(0);
		f.write(msdosStub);
		f.seek(0x3C);
		f.writeInt(Integer.reverseBytes(msdosStub.length));
		f.seek(msdosStub.length);
		f.writeInt(Integer.reverseBytes(peSignature));
		f.writeShort(Short.reverseBytes(machine));
		f.writeShort(Short.reverseBytes(numSections));
		f.writeInt(Integer.reverseBytes(creationDate));
		f.writeInt(Integer.reverseBytes(symbolTablePtr));
		f.writeInt(Integer.reverseBytes(numSymbols));
		f.writeShort(Short.reverseBytes(optHeaderSize));
		f.writeShort(Short.reverseBytes(characteristics));
		long oh = f.getFilePointer();
		f.writeShort(Short.reverseBytes(magic));
		f.writeShort(linkerVersion);
		f.writeInt(Integer.reverseBytes(codeSize));
		f.writeInt(Integer.reverseBytes(dataSize));
		f.writeInt(Integer.reverseBytes(bssSize));
		f.writeInt(Integer.reverseBytes(entryPointOfst));
		f.writeInt(Integer.reverseBytes(codeOfst));
		if (magic != MAGIC_PE32PLUS) {
			f.writeInt(Integer.reverseBytes(dataOfst));
			f.writeInt(Integer.reverseBytes((int)base));
		} else {
			f.writeLong(Long.reverseBytes(base));
		}
		f.writeInt(Integer.reverseBytes(sectionAlign));
		f.writeInt(Integer.reverseBytes(fileAlign));
		f.writeInt(verswap(osVersion));
		f.writeInt(verswap(imageVersion));
		f.writeInt(verswap(subsystemVersion));
		f.writeInt(Integer.reverseBytes(win32versionValue));
		f.writeInt(Integer.reverseBytes(sizeOfImage));
		f.writeInt(Integer.reverseBytes(sizeOfHeaders));
		long cs = f.getFilePointer();
		f.writeInt(0); //checksum; tbd later
		f.writeShort(Short.reverseBytes(subsystem));
		f.writeShort(Short.reverseBytes(dllCharacteristics));
		if (magic != MAGIC_PE32PLUS) {
			f.writeInt(Integer.reverseBytes((int)stackReserveSize));
			f.writeInt(Integer.reverseBytes((int)stackCommitSize));
			f.writeInt(Integer.reverseBytes((int)heapReserveSize));
			f.writeInt(Integer.reverseBytes((int)heapCommitSize));
		} else {
			f.writeLong(Long.reverseBytes(stackReserveSize));
			f.writeLong(Long.reverseBytes(stackCommitSize));
			f.writeLong(Long.reverseBytes(heapReserveSize));
			f.writeLong(Long.reverseBytes(heapCommitSize));
		}
		f.writeInt(Integer.reverseBytes(loaderFlags));
		f.writeInt(Integer.reverseBytes(numberOfRvaAndSizes));
		for (int i=0; i<numberOfRvaAndSizes; i++) {
			PEDirectoryEntry de = (i < dirEntries.size())?dirEntries.get(i):new PEDirectoryEntry();
			f.writeInt(Integer.reverseBytes(de.virtualAddress));
			f.writeInt(Integer.reverseBytes(de.size));
		}
		f.seek(oh+optHeaderSize);
		for (int i=0; i<numSections; i++) {
			PESection s = (i < sections.size())?sections.get(i):new PESection();
			long tmp = f.getFilePointer();
			f.write(s.name);
			f.seek(tmp+8);
			f.writeInt(Integer.reverseBytes(s.virtualSize));
			f.writeInt(Integer.reverseBytes(s.virtualAddress));
			f.writeInt(Integer.reverseBytes(s.rawDataSize));
			f.writeInt(Integer.reverseBytes(s.rawDataOfst));
			f.writeInt(Integer.reverseBytes(s.relocOfst));
			f.writeInt(Integer.reverseBytes(s.lineNumOfst));
			f.writeShort(Short.reverseBytes(s.relocCnt));
			f.writeShort(Short.reverseBytes(s.lineNumCnt));
			f.writeInt(Integer.reverseBytes(s.characteristics));
		}
		long dlli = f.getFilePointer();
		Iterator<PEDLLImport> di;
		for (di = dllImports.iterator(); di.hasNext();) {
			PEDLLImport d = di.next();
			f.writeInt(Integer.reverseBytes(d.timeStamp));
			f.writeShort(Short.reverseBytes(d.nameOfst));
			f.writeShort(Short.reverseBytes(d.characteristics));
		}
		for (di = dllImports.iterator(); di.hasNext();) {
			PEDLLImport d = di.next();
			int nl = d.name.length();
			byte[] nb = new byte[nl+1];
			for (int k=0; k<nl; k++) nb[k] = (byte)d.name.charAt(k);
			nb[nl] = 0;
			f.seek(dlli+d.nameOfst);
			f.write(nb);
		}
		for (int i=0; i<numSections; i++) {
			PESection s = (i < sections.size())?sections.get(i):new PESection();
			if (s.rawDataSize > 0 && s.rawDataOfst > 0) {
				f.seek(s.rawDataOfst);
				f.write(new byte[s.rawDataSize]);
				f.seek(s.rawDataOfst);
				f.write(s.data, 0, Math.min(s.rawDataSize, s.data.length));
			}
		}
		//special magic for certain sections
		//this is simply recalculating RVAs back to the new VA
		if (dirEntries.size() > 2) {
			PEDirectoryEntry de = dirEntries.get(2);
			int[] sno = ofstToSNO(de.virtualAddress, sizeOfHeaders);
			if (sno[0] >= 0) {
				PESection s = sections.get(sno[0]);
				PEResourceDirectory.addVA(s.data, sno[1], s.virtualAddress);
			}
		}
		//checksum
		//See Peter Szor, The Art of Computer Virus Research and Defense, Sec 6.2.8.1.12
		//If Microsoft C&D's us, this can lift right out; without it, the checksum will
		//be left at zero, and Windows will execute most things with a zero checksum.
		checksum = 0;
		f.seek(0);
		long len = f.length();
		for (int i=0; i<len; i+=2) {
			checksum += (Short.reverseBytes(f.readShort()) & 0xFFFF);
			if (checksum > 0xFFFF) checksum = (checksum & 0xFFFF) + 1;
		}
		checksum += (int)len;
		f.seek(cs);
		f.writeInt(Integer.reverseBytes(checksum));
	}
}
