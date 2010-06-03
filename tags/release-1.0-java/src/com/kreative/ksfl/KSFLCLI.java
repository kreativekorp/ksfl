/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
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

import java.io.*;
import java.text.*;
import java.util.*;
import com.kreative.cff.*;
import com.kreative.dff.*;
import com.kreative.pe.*;
import com.kreative.prc.*;
import com.kreative.rsrc.*;

public class KSFLCLI {
	private KSFLCLI() {}
	
	public static void main(String[] args) {
		if (
				args.length == 0 ||
				args[0].equalsIgnoreCase("-h") ||
				args[0].equalsIgnoreCase("-help") ||
				args[0].equalsIgnoreCase("--help") ||
				args[0].equalsIgnoreCase("help")
		) {
			printUsage();
		} else if (
				args[0].equalsIgnoreCase("-v") ||
				args[0].equalsIgnoreCase("-version") ||
				args[0].equalsIgnoreCase("--version") ||
				args[0].equalsIgnoreCase("version")
		) {
			printVersion();
		} else if (args[0].equalsIgnoreCase("fcc")) {
			boolean dec = false;
			for (int i = 1; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("-d")) dec = true;
				else if (args[i].equalsIgnoreCase("-h")) dec = false;
				else if (dec) System.out.println(KSFLUtilities.fcc(args[i]));
				else {
					String h = "00000000"+Integer.toHexString(KSFLUtilities.fcc(args[i])).toUpperCase();
					System.out.println(h.substring(h.length()-8));
				}
			}
		} else if (args[0].equalsIgnoreCase("fccs")) {
			boolean dec = false;
			for (int i = 1; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("-d")) dec = true;
				else if (args[i].equalsIgnoreCase("-h")) dec = false;
				else if (dec) System.out.println(KSFLUtilities.fccs(Integer.parseInt(args[i])));
				else {
					// we do not use Integer.parseInt here because it dares to balk at the likes of "FFFFFFFF"
					int t = 0;
					CharacterIterator it = new StringCharacterIterator(args[i]);
					for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
						if (ch >= '0' && ch <= '9') t = (t << 4) | (int)(ch - '0');
						else if (ch >= 'A' && ch <= 'F') t = (t << 4) | (int)(ch - 'A' + 10);
						else if (ch >= 'a' && ch <= 'f') t = (t << 4) | (int)(ch - 'a' + 10);
					}
					System.out.println(KSFLUtilities.fccs(t));
				}
			}
		} else if (args[0].equalsIgnoreCase("ecc")) {
			boolean dec = false;
			for (int i = 1; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("-d")) dec = true;
				else if (args[i].equalsIgnoreCase("-h")) dec = false;
				else if (dec) System.out.println(KSFLUtilities.ecc(args[i]));
				else {
					String h = "0000000000000000"+Long.toHexString(KSFLUtilities.ecc(args[i])).toUpperCase();
					System.out.println(h.substring(h.length()-16));
				}
			}
		} else if (args[0].equalsIgnoreCase("eccs")) {
			boolean dec = false;
			for (int i = 1; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("-d")) dec = true;
				else if (args[i].equalsIgnoreCase("-h")) dec = false;
				else if (dec) System.out.println(KSFLUtilities.eccs(Long.parseLong(args[i])));
				else {
					// we do not use Long.parseLong here because it dares to balk at the likes of "FFFFFFFFFFFFFFFF"
					long t = 0;
					CharacterIterator it = new StringCharacterIterator(args[i]);
					for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
						if (ch >= '0' && ch <= '9') t = (t << 4L) | (long)(ch - '0');
						else if (ch >= 'A' && ch <= 'F') t = (t << 4L) | (long)(ch - 'A' + 10);
						else if (ch >= 'a' && ch <= 'f') t = (t << 4L) | (long)(ch - 'a' + 10);
					}
					System.out.println(KSFLUtilities.eccs(t));
				}
			}
		} else if (args[0].equalsIgnoreCase("tcc")) {
			boolean dec = false;
			for (int i = 1; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("-d")) dec = true;
				else if (args[i].equalsIgnoreCase("-h")) dec = false;
				else if (dec) System.out.println(KSFLUtilities.tcc(args[i]));
				else {
					String h = "0000"+Integer.toHexString(KSFLUtilities.tcc(args[i])).toUpperCase();
					System.out.println(h.substring(h.length()-4));
				}
			}
		} else if (args[0].equalsIgnoreCase("tccs")) {
			boolean dec = false;
			for (int i = 1; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("-d")) dec = true;
				else if (args[i].equalsIgnoreCase("-h")) dec = false;
				else if (dec) System.out.println(KSFLUtilities.tccs(Short.parseShort(args[i])));
				else {
					// we do not use Short.parseShort here because it dares to balk at the likes of "FFFF"
					short t = 0;
					CharacterIterator it = new StringCharacterIterator(args[i]);
					for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
						if (ch >= '0' && ch <= '9') t = (short)((t << 4) | (int)(ch - '0'));
						else if (ch >= 'A' && ch <= 'F') t = (short)((t << 4) | (int)(ch - 'A' + 10));
						else if (ch >= 'a' && ch <= 'f') t = (short)((t << 4) | (int)(ch - 'a' + 10));
					}
					System.out.println(KSFLUtilities.tccs(t));
				}
			}
		} else if (args[0].equalsIgnoreCase("occ")) {
			boolean dec = false;
			for (int i = 1; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("-d")) dec = true;
				else if (args[i].equalsIgnoreCase("-h")) dec = false;
				else if (dec) System.out.println(KSFLUtilities.occ(args[i]));
				else {
					String h = "00"+Integer.toHexString(KSFLUtilities.occ(args[i])).toUpperCase();
					System.out.println(h.substring(h.length()-2));
				}
			}
		} else if (args[0].equalsIgnoreCase("occs")) {
			boolean dec = false;
			for (int i = 1; i < args.length; i++) {
				if (args[i].equalsIgnoreCase("-d")) dec = true;
				else if (args[i].equalsIgnoreCase("-h")) dec = false;
				else if (dec) System.out.println(KSFLUtilities.occs(Byte.parseByte(args[i])));
				else {
					// we do not use Byte.parseByte here because it dares to balk at the likes of "FF"
					byte t = 0;
					CharacterIterator it = new StringCharacterIterator(args[i]);
					for (char ch = it.first(); ch != CharacterIterator.DONE; ch = it.next()) {
						if (ch >= '0' && ch <= '9') t = (byte)((t << 4) | (int)(ch - '0'));
						else if (ch >= 'A' && ch <= 'F') t = (byte)((t << 4) | (int)(ch - 'A' + 10));
						else if (ch >= 'a' && ch <= 'f') t = (byte)((t << 4) | (int)(ch - 'a' + 10));
					}
					System.out.println(KSFLUtilities.occs(t));
				}
			}
		} else if (args[0].equalsIgnoreCase("explain")) {
			System.out.println();
			for (int i = 1; i < args.length; i++) {
				if (args[i].toLowerCase().startsWith("chunk:")) {
					ChunkFileSpec cfs = new ChunkFileSpec(args[i].substring(6));
					System.out.println("==== "+cfs.stringRepresentation()+" ====");
					explainChunk(cfs);
					System.out.println();
				} else {
					Format fmt = Format.forString(args[i]);
					if (fmt == null) {
						System.err.println("Unknown format: "+args[i]);
						System.err.println("Should be one of:");
						Format.print(System.err);
					} else {
						System.out.println("==== "+fmt.name()+" ====");
						explain(fmt);
						System.out.println();
					}
				}
			}
		} else if (args.length < 2) {
			printUsage();
		} else if (args[0].toLowerCase().startsWith("chunk:")) {
			ChunkFileSpec cfs = new ChunkFileSpec(args[0].substring(6));
			File f = new File(args[1]);
			List<Operation> ops = new Vector<Operation>();
			List<List<String>> oargs = new Vector<List<String>>();
			for (int i = 2; i < args.length;) {
				Operation op = Operation.forString(args[i++]);
				if (op == null) {
					System.err.println("Unknown operation: "+args[i-1]);
					System.err.println("Should be one of:");
					Operation.print(System.err);
					while (i < args.length && !args[i].equals(":")) i++;
					if (i < args.length && args[i].equals(":")) i++;
				} else {
					List<String> oarg = new Vector<String>();
					while (i < args.length && !args[i].equals(":")) oarg.add(args[i++]);
					if (i < args.length && args[i].equals(":")) i++;
					ops.add(op);
					oargs.add(oarg);
				}
			}
			doChunkOperations(cfs, f, ops, oargs);
		} else {
			Format fmt = Format.forString(args[0]);
			if (fmt == null) {
				System.err.println("Unknown format: "+args[0]);
				System.err.println("Should be one of:");
				Format.print(System.err);
			} else {
				File f = new File(args[1]);
				List<Operation> ops = new Vector<Operation>();
				List<List<String>> oargs = new Vector<List<String>>();
				for (int i = 2; i < args.length;) {
					Operation op = Operation.forString(args[i++]);
					if (op == null) {
						System.err.println("Unknown operation: "+args[i-1]);
						System.err.println("Should be one of:");
						Operation.print(System.err);
						while (i < args.length && !args[i].equals(":")) i++;
						if (i < args.length && args[i].equals(":")) i++;
					} else {
						List<String> oarg = new Vector<String>();
						while (i < args.length && !args[i].equals(":")) oarg.add(args[i++]);
						if (i < args.length && args[i].equals(":")) i++;
						ops.add(op);
						oargs.add(oarg);
					}
				}
				doOperations(fmt, f, ops, oargs);
			}
		}
	}
	
	private static void printVersion() {
		System.out.println("KSFL - Kreative Structured Format Library - version 1.0");
	}
	
	private static void printUsage() {
		printVersion();
		System.out.println("Usage:");
		System.out.println("  ksfl <format> <file> <operation> [options] [: <operation> [options] [...]]");
		System.out.println("  ksfl explain [<format> [<format> [...]]]");
		System.out.println("  ksfl fcc|fccs|ecc|eccs|tcc|tccs|occ|occs [-dh] [<type> [<type> [...]]]");
		System.out.println("  ksfl version|help");
		System.out.println("Format:");
		Format.print(System.out);
		System.out.println("Operation:");
		Operation.print(System.out);
	}
	
	private enum Format {
		IFF, RIFF, MIDI, PNG, ICNS, HYPERCARD,
		DFFAUTO, DFF1BE, DFF1LE, DFF2BE, DFF2LE, DFF3BE, DFF3LE, 
		WINPE, PRC, RSRC;
		
		public static Format forString(String s) {
			s = s.trim().toLowerCase();
			if (s.equals("iff")) return IFF;
			if (s.equals("riff")) return RIFF;
			if (s.equals("midi")) return MIDI;
			if (s.equals("png")) return PNG;
			if (s.equals("icns")) return ICNS;
			if (s.equals("hc") || s.equals("hypercard")) return HYPERCARD;
			if (s.equals("d") || s.equals("dff") || s.equals("dffauto")) return DFFAUTO;
			if (s.equals("d1") || s.equals("d1b") || s.equals("dff1") || s.equals("dff1be")) return DFF1BE;
			if (s.equals("d1l") || s.equals("dff1le")) return DFF1LE;
			if (s.equals("d2") || s.equals("d2b") || s.equals("dff2") || s.equals("dff2be")) return DFF2BE;
			if (s.equals("d2l") || s.equals("dff2le")) return DFF2LE;
			if (s.equals("d3") || s.equals("d3b") || s.equals("dff3") || s.equals("dff3be")) return DFF3BE;
			if (s.equals("d3l") || s.equals("dff3le")) return DFF3LE;
			if (s.equals("w") || s.equals("win") || s.equals("pe") || s.equals("winpe")) return WINPE;
			if (s.equals("p") || s.equals("prc")) return PRC;
			if (s.equals("r") || s.equals("rsrc")) return RSRC;
			return null;
		}
		
		public static void print(PrintStream out) {
			out.println("  iff           - Interchange File Format (big-endian)");
			out.println("  riff          - Interchange File Format (little-endian)");
			out.println("  midi          - MIDI file format");
			out.println("  png           - PNG file format");
			out.println("  icns          - Mac OS icon file format");
			out.println("  h[yper]c[ard] - HyperCard stack file format");
			out.println("  chunk:<spec>  - custom chunk format");
			out.println("  dff[auto]     - DFF File Format (automatic)");
			out.println("  dff1[be]      - DFF 1.0 (big-endian)");
			out.println("  dff1le        - DFF 1.0 (little-endian)");
			out.println("  dff2[be]      - DFF 2.0.1 (big-endian)");
			out.println("  dff2le        - DFF 2.0.1 (little-endian)");
			out.println("  dff3[be]      - DFF 3.0 (big-endian)");
			out.println("  dff3le        - DFF 3.0 (little-endian)");
			out.println("  [win][pe]     - Windows Portable Executable");
			out.println("  prc           - Palm OS resource database");
			out.println("  rsrc          - Mac OS resource fork");
		}
	}
	
	private enum Operation {
		CREATE,   // constructor
		LIST,     // getTypes, getIDs
		NEXT,     // getNextAvailableID
		ADD,      // add
		VERIFY,   // contains
		EXTRACT,  // get
		INFO,     // getAttributes, getLength
		CAT,      // getData
		HEXCAT,   // getData
		DELETE,   // remove
		SETINFO,  // setAttributes
		TRUNCATE, // setLength
		FRESHEN,  // setData
		UPDATE;   // add, setData
		
		public static Operation forString(String s) {
			s = s.trim().toLowerCase();
			if (s.equals("k") || s.equals("kreate") || s.equals("create")) return CREATE;
			if (s.equals("l") || s.equals("list")) return LIST;
			if (s.equals("n") || s.equals("next")) return NEXT;
			if (s.equals("a") || s.equals("add")) return ADD;
			if (s.equals("v") || s.equals("verify")) return VERIFY;
			if (s.equals("e") || s.equals("x") || s.equals("extract") || s.equals("xtract")) return EXTRACT;
			if (s.equals("i") || s.equals("info")) return INFO;
			if (s.equals("c") || s.equals("cat")) return CAT;
			if (s.equals("h") || s.equals("hex") || s.equals("hexcat")) return HEXCAT;
			if (s.equals("d") || s.equals("delete")) return DELETE;
			if (s.equals("s") || s.equals("set") || s.equals("setinfo")) return SETINFO;
			if (s.equals("t") || s.equals("trunc") || s.equals("truncate")) return TRUNCATE;
			if (s.equals("f") || s.equals("freshen")) return FRESHEN;
			if (s.equals("u") || s.equals("update")) return UPDATE;
			return null;
		}
		
		public static void print(PrintStream out) {
			out.println("  k[reate]");
			out.println("  l[ist] [type]");
			out.println("  n[ext] <type> [id]");
			out.println("  a[dd] <type> [id] [name] [attributes] [datatype] <datafile>");
			out.println("  v[erify] <type> <id|name>");
			out.println("  e[xtract] [type] [id|name] <datafile>");
			out.println("  x[tract] [type] [id|name] <datafile>");
			out.println("  i[nfo] <type> <id|name>");
			out.println("  c[at] <type> <id|name>");
			out.println("  h[ex[cat]] <type> <id|name>");
			out.println("  d[elete] <type> <id|name>");
			out.println("  s[et[info]] <type> <id|name> t[ype]|i[d]|n[ame]|a[ttr]|d[atatype] <value>");
			out.println("  t[runc[ate]] <type> <id|name> <length>");
			out.println("  f[reshen] <type> <id|name> <datafile>");
			out.println("  u[pdate] <type> <id|name> <datafile>");
		}
	}
	
	private static void doOperations(Format fmt, File f, List<Operation> ops, List<List<String>> oargs) {
		switch (fmt) {
		case IFF: doChunkOperations(ChunkFileSpec.CFSPEC_IFF, f, ops, oargs); break;
		case RIFF: doChunkOperations(ChunkFileSpec.CFSPEC_RIFF, f, ops, oargs); break;
		case MIDI: doChunkOperations(ChunkFileSpec.CFSPEC_MIDI, f, ops, oargs); break;
		case PNG: doChunkOperations(ChunkFileSpec.CFSPEC_PNG, f, ops, oargs); break;
		case ICNS: doChunkOperations(ChunkFileSpec.CFSPEC_ICNS, f, ops, oargs); break;
		case HYPERCARD: doChunkOperations(ChunkFileSpec.CFSPEC_HYPERCARD, f, ops, oargs); break;
		case DFFAUTO: doDFFOperations(3, false, f, ops, oargs); break;
		case DFF1BE: doDFFOperations(1, false, f, ops, oargs); break;
		case DFF1LE: doDFFOperations(1, true, f, ops, oargs); break;
		case DFF2BE: doDFFOperations(2, false, f, ops, oargs); break;
		case DFF2LE: doDFFOperations(2, true, f, ops, oargs); break;
		case DFF3BE: doDFFOperations(3, false, f, ops, oargs); break;
		case DFF3LE: doDFFOperations(3, true, f, ops, oargs); break;
		case WINPE: doWinPEOperations(f, ops, oargs); break;
		case PRC: doPRCOperations(f, ops, oargs); break;
		case RSRC: doRsrcOperations(f, ops, oargs); break;
		default: System.err.println("Internal Error: Unimplemented Format "+fmt.name()); break;
		}
	}
	
	private static void doChunkOperations(ChunkFileSpec spec, File f, List<Operation> ops, List<List<String>> oargs) {
		ChunkFileEditor cf = null;
		boolean modified = false;
		for (int i = 0; i < ops.size() && i < oargs.size(); i++) {
			Operation op = ops.get(i);
			List<String> oarg = oargs.get(i);
			if (op == Operation.CREATE) {
				try {
					cf = new ChunkFileEditor(spec, f, ChunkFileEditor.CREATE_ALWAYS);
					modified = true;
				} catch (IOException e) {
					System.err.println("Error: Invalid file ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
					return;
				}
			} else {
				if (cf == null) {
					try {
						cf = new ChunkFileEditor(spec, f, ChunkFileEditor.CREATE_NEVER);
						modified = false;
					} catch (IOException e) {
						System.err.println("Error: Invalid file ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
						return;
					}
				}
				switch (op) {
				case LIST:
					if (oarg.isEmpty()) {
						for (int idx = 0; idx < cf.getChunkCount(); idx++) {
							Chunk ch = cf.get(idx);
							Header h = ch.getHeader();
							if (h.containsKey(FieldType.CHARACTER_TYPE)) System.out.print(xccs(h.get(FieldType.CHARACTER_TYPE))+"\t");
							if (h.containsKey(FieldType.INTEGER_TYPE)) System.out.print(h.get(FieldType.INTEGER_TYPE)+"\t");
							if (h.containsKey(FieldType.ID_NUMBER)) System.out.print(h.get(FieldType.ID_NUMBER)+"\t");
							System.out.println();
						}
					} else {
						for (int idx = 0; idx < cf.getChunkCount(); idx++) {
							Chunk ch = cf.get(idx);
							Header h = ch.getHeader();
							if (h.containsKey(FieldType.CHARACTER_TYPE)) {
								String s = xccs(h.get(FieldType.CHARACTER_TYPE));
								if (!oarg.contains(s)) continue;
								System.out.print(s+"\t");
							}
							if (h.containsKey(FieldType.INTEGER_TYPE)) System.out.print(h.get(FieldType.INTEGER_TYPE)+"\t");
							if (h.containsKey(FieldType.ID_NUMBER)) System.out.print(h.get(FieldType.ID_NUMBER)+"\t");
							System.out.println();
						}
					}
					break;
				case NEXT:
					for (int j = 0; j+ntf(spec.chunkHeaderSpec()) <= oarg.size();) {
						Number[] t = parseChunkType(spec.chunkHeaderSpec(), oarg, j, oarg.size());
						j = t[0].intValue();
						if (j < oarg.size()) {
							try {
								Number start = Long.parseLong(oarg.get(j)); j++;
								start = cf.getNextAvailableID(t[1], t[2], start);
								if (t[1] != null) System.out.print(xccs(t[1]) + "\t");
								if (t[2] != null) System.out.print(t[2].longValue() + "\t");
								System.out.println(start);
							} catch (NumberFormatException nfe) {
								Number start = cf.getNextAvailableID(t[1], t[2]);
								if (t[1] != null) System.out.print(xccs(t[1]) + "\t");
								if (t[2] != null) System.out.print(t[2].longValue() + "\t");
								System.out.println(start);
							}
						} else {
							Number start = cf.getNextAvailableID(t[1], t[2]);
							if (t[1] != null) System.out.print(xccs(t[1]) + "\t");
							if (t[2] != null) System.out.print(t[2].longValue() + "\t");
							System.out.println(start);
						}
					}
					break;
				case ADD:
					if (!oarg.isEmpty()) {
						byte[] stuff = null;
						try {
							RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(oarg.size()-1)), "r");
							stuff = new byte[(int)raf.length()];
							raf.readFully(stuff);
							raf.close();
						} catch (IOException e) {
							System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							stuff = null;
						}
						if (stuff != null) {
							Chunk ch = new Chunk(spec.chunkHeaderSpec().createHeader(), stuff);
							int j = 0;
							if (spec.chunkHeaderSpec().containsType(FieldType.CHARACTER_TYPE)) {
								if (j < oarg.size()-1) {
									switch (spec.chunkHeaderSpec().getField(FieldType.CHARACTER_TYPE).size()) {
									case LONG: ch.getHeader().put(FieldType.CHARACTER_TYPE, KSFLUtilities.ecc(oarg.get(j++))); break;
									case MEDIUM: ch.getHeader().put(FieldType.CHARACTER_TYPE, KSFLUtilities.fcc(oarg.get(j++))); break;
									case SHORT: ch.getHeader().put(FieldType.CHARACTER_TYPE, KSFLUtilities.tcc(oarg.get(j++))); break;
									case BYTE: ch.getHeader().put(FieldType.CHARACTER_TYPE, KSFLUtilities.occ(oarg.get(j++))); break;
									}
								} else {
									switch (spec.chunkHeaderSpec().getField(FieldType.CHARACTER_TYPE).size()) {
									case LONG: ch.getHeader().put(FieldType.CHARACTER_TYPE, KSFLConstants.Data_Bin); break;
									case MEDIUM: ch.getHeader().put(FieldType.CHARACTER_TYPE, KSFLConstants.DATA); break;
									case SHORT: ch.getHeader().put(FieldType.CHARACTER_TYPE, (short)0x3F3F); break;
									case BYTE: ch.getHeader().put(FieldType.CHARACTER_TYPE, (byte)0x3F); break;
									}
								}
							}
							if (spec.chunkHeaderSpec().containsType(FieldType.INTEGER_TYPE)) {
								if (j < oarg.size()-1) {
									try {
										switch (spec.chunkHeaderSpec().getField(FieldType.INTEGER_TYPE).size()) {
										case LONG: ch.getHeader().put(FieldType.INTEGER_TYPE, Long.parseLong(oarg.get(j))); j++; break;
										case MEDIUM: ch.getHeader().put(FieldType.INTEGER_TYPE, Integer.parseInt(oarg.get(j))); j++; break;
										case SHORT: ch.getHeader().put(FieldType.INTEGER_TYPE, Short.parseShort(oarg.get(j))); j++; break;
										case BYTE: ch.getHeader().put(FieldType.INTEGER_TYPE, Byte.parseByte(oarg.get(j))); j++; break;
										}
									} catch (NumberFormatException nfe) {
										switch (spec.chunkHeaderSpec().getField(FieldType.INTEGER_TYPE).size()) {
										case LONG: ch.getHeader().put(FieldType.INTEGER_TYPE, 0L); break;
										case MEDIUM: ch.getHeader().put(FieldType.INTEGER_TYPE, 0); break;
										case SHORT: ch.getHeader().put(FieldType.INTEGER_TYPE, (short)0); break;
										case BYTE: ch.getHeader().put(FieldType.INTEGER_TYPE, (byte)0); break;
										}
									}
								} else {
									switch (spec.chunkHeaderSpec().getField(FieldType.INTEGER_TYPE).size()) {
									case LONG: ch.getHeader().put(FieldType.INTEGER_TYPE, 0L); break;
									case MEDIUM: ch.getHeader().put(FieldType.INTEGER_TYPE, 0); break;
									case SHORT: ch.getHeader().put(FieldType.INTEGER_TYPE, (short)0); break;
									case BYTE: ch.getHeader().put(FieldType.INTEGER_TYPE, (byte)0); break;
									}
								}
							}
							if (spec.chunkHeaderSpec().containsType(FieldType.ID_NUMBER)) {
								if (j < oarg.size()-1) {
									try {
										switch (spec.chunkHeaderSpec().getField(FieldType.ID_NUMBER).size()) {
										case LONG: ch.getHeader().put(FieldType.ID_NUMBER, Long.parseLong(oarg.get(j))); j++; break;
										case MEDIUM: ch.getHeader().put(FieldType.ID_NUMBER, Integer.parseInt(oarg.get(j))); j++; break;
										case SHORT: ch.getHeader().put(FieldType.ID_NUMBER, Short.parseShort(oarg.get(j))); j++; break;
										case BYTE: ch.getHeader().put(FieldType.ID_NUMBER, Byte.parseByte(oarg.get(j))); j++; break;
										}
									} catch (NumberFormatException nfe) {
										Number id = cf.getNextAvailableID(ch.getHeader().get(FieldType.CHARACTER_TYPE), ch.getHeader().get(FieldType.INTEGER_TYPE));
										ch.getHeader().put(FieldType.ID_NUMBER, id);
									}
								} else {
									Number id = cf.getNextAvailableID(ch.getHeader().get(FieldType.CHARACTER_TYPE), ch.getHeader().get(FieldType.INTEGER_TYPE));
									ch.getHeader().put(FieldType.ID_NUMBER, id);
								}
							}
							try {
								if (j < oarg.size()-1) {
									if (oarg.get(j).equalsIgnoreCase("before")) {
										j++;
										if (j+nidf(spec.chunkHeaderSpec()) <= oarg.size()-1) {
											Number[] n = parseChunkID(spec.chunkHeaderSpec(), oarg, j, oarg.size()-1);
											j = n[0].intValue();
											if (cf.insert(n[1], n[2], n[3], ch)) modified = true;
										} else {
											if (cf.insert(0, ch)) modified = true;
										}
									} else if (oarg.get(j).equalsIgnoreCase("after")) {
										j++;
										if (j+nidf(spec.chunkHeaderSpec()) <= oarg.size()-1) {
											Number[] n = parseChunkID(spec.chunkHeaderSpec(), oarg, j, oarg.size()-1);
											j = n[0].intValue();
											int index = cf.getChunkIndex(n[1], n[2], n[3]);
											if (index >= 0) index++;
											if (cf.insert(index, ch)) modified = true;
										} else {
											if (cf.add(ch)) modified = true;
										}
									} else {
										if (cf.add(ch)) modified = true;
									}
								} else {
									if (cf.add(ch)) modified = true;
								}
							} catch (ChunkAlreadyExistsException e) {
								System.err.println("Error: Chunk already exists.");
							}
						}
					}
					break;
				case VERIFY:
					for (int j = 0; j+nidf(spec.chunkHeaderSpec()) <= oarg.size();) {
						Number[] n = parseChunkID(spec.chunkHeaderSpec(), oarg, j, oarg.size());
						System.out.println(cf.contains(n[1], n[2], n[3]));
						j = n[0].intValue();
					}
					break;
				case EXTRACT:
					if ((oarg.size() > 0) && (oarg.size() < (ntf(spec.chunkHeaderSpec())+1))) {
						File f1 = new File(oarg.get(oarg.size()-1)); f1.mkdir();
						for (int j = 0; j < cf.getChunkCount(); j++) {
							Chunk ch = cf.get(j);
							StringBuffer n = new StringBuffer();
							if (ch.getHeader().containsKey(FieldType.CHARACTER_TYPE))
								n.append(xccs(ch.getHeader().get(FieldType.CHARACTER_TYPE))+" ");
							if (ch.getHeader().containsKey(FieldType.INTEGER_TYPE))
								n.append(Long.toString(ch.getHeader().get(FieldType.INTEGER_TYPE).longValue())+" ");
							if (ch.getHeader().containsKey(FieldType.ID_NUMBER))
								n.append(Long.toString(ch.getHeader().get(FieldType.ID_NUMBER).longValue())+" ");
							else
								n.insert(0, Integer.toString(j)+" ");
							n.deleteCharAt(n.length()-1);
							File f2 = new File(f1, n.toString());
							try {
								FileOutputStream fos = new FileOutputStream(f2);
								fos.write(ch.getData());
								fos.close();
							} catch (IOException e) {
								System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							}
						}
					} else if (oarg.size() == (ntf(spec.chunkHeaderSpec())+1)) {
						File f1 = new File(oarg.get(oarg.size()-1)); f1.mkdir();
						Number[] t = parseChunkType(spec.chunkHeaderSpec(), oarg, 0, oarg.size()-1);
						for (Number id : cf.getChunkIDs(t[1], t[2])) {
							StringBuffer n = new StringBuffer();
							if (t[1] != null) n.append(xccs(t[1])+" ");
							if (t[2] != null) n.append(Long.toString(t[2].longValue())+" ");
							n.append(Long.toString(id.longValue()));
							File f2 = new File(f1, n.toString());
							Chunk ch = cf.get(t[1], t[2], id);
							try {
								FileOutputStream fos = new FileOutputStream(f2);
								fos.write(ch.getData());
								fos.close();
							} catch (IOException e) {
								System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							}
						}
					} else {
						for (int j = 0; j+nidf(spec.chunkHeaderSpec())+1 <= oarg.size();) {
							Number[] n = parseChunkID(spec.chunkHeaderSpec(), oarg, j, oarg.size());
							j = n[0].intValue();
							File f1 = new File(oarg.get(j++));
							Chunk ch = cf.get(n[1], n[2], n[3]);
							if (ch == null) {
								System.err.println("Error: Chunk does not exist.");
							} else {
								try {
									FileOutputStream fos = new FileOutputStream(f1);
									fos.write(ch.getData());
									fos.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
								}
							}
						}
					}
					break;
				case INFO:
					for (int j = 0; j+nidf(spec.chunkHeaderSpec()) <= oarg.size();) {
						Number[] n = parseChunkID(spec.chunkHeaderSpec(), oarg, j, oarg.size());
						Header h = cf.getAttributes(n[1], n[2], n[3]);
						if (h == null) {
							System.err.println("Error: Chunk does not exist.");
						} else {
							if (h.containsKey(FieldType.CHARACTER_TYPE))
								System.out.println("Type: "+xccs(h.get(FieldType.CHARACTER_TYPE)));
							if (h.containsKey(FieldType.INTEGER_TYPE))
								System.out.println("Type: "+h.get(FieldType.INTEGER_TYPE).longValue());
							if (h.containsKey(FieldType.ID_NUMBER))
								System.out.println("ID:   "+h.get(FieldType.ID_NUMBER).longValue());
							if (h.containsKey(FieldType.CHECKSUM))
								System.out.println("Chk:  "+h.get(FieldType.CHECKSUM).longValue());
							if (h.containsKey(FieldType.SIZE_WITHOUT_HEADER))
								System.out.println("Len:  "+h.get(FieldType.SIZE_WITHOUT_HEADER).longValue()+" (excluding header)");
							if (h.containsKey(FieldType.SIZE_WITH_HEADER))
								System.out.println("Len:  "+h.get(FieldType.SIZE_WITH_HEADER).longValue()+" (including header)");
						}
						j = n[0].intValue();
					}
					break;
				case CAT:
					for (int j = 0; j+nidf(spec.chunkHeaderSpec()) <= oarg.size();) {
						Number[] n = parseChunkID(spec.chunkHeaderSpec(), oarg, j, oarg.size());
						byte[] data = cf.getData(n[1], n[2], n[3]);
						if (data == null) System.err.println("Error: Chunk does not exist.");
						else try { System.out.write(data); } catch (IOException e) {}
						j = n[0].intValue();
					}
					break;
				case HEXCAT:
					for (int j = 0; j+nidf(spec.chunkHeaderSpec()) <= oarg.size();) {
						Number[] n = parseChunkID(spec.chunkHeaderSpec(), oarg, j, oarg.size());
						byte[] data = cf.getData(n[1], n[2], n[3]);
						if (data == null) System.err.println("Error: Chunk does not exist.");
						else KSFLUtilities.printHexDump(System.out, data);
						j = n[0].intValue();
					}
					break;
				case DELETE:
					for (int j = 0; j+nidf(spec.chunkHeaderSpec()) <= oarg.size();) {
						Number[] n = parseChunkID(spec.chunkHeaderSpec(), oarg, j, oarg.size());
						if (cf.remove(n[1], n[2], n[3])) modified = true;
						j = n[0].intValue();
					}
					break;
				case SETINFO:
					if (oarg.size() >= nidf(spec.chunkHeaderSpec())) {
						Number[] n = parseChunkID(spec.chunkHeaderSpec(), oarg, 0, oarg.size());
						Chunk ch = cf.get(n[1], n[2], n[3]);
						if (ch == null) {
							System.err.println("Error: Chunk does not exist.");
						} else {
							Header h = ch.getHeader().clone();
							boolean localModified = false;
							for (int j = n[0].intValue(); j+2 <= oarg.size(); j += 2) {
								String siop = oarg.get(j).trim().toLowerCase();
								if (siop.equals("t") || siop.equals("type") || siop.equals("ct") || siop.equals("ctype")) {
									if (spec.chunkHeaderSpec().containsType(FieldType.CHARACTER_TYPE)) {
										switch (spec.chunkHeaderSpec().getField(FieldType.CHARACTER_TYPE).size()) {
										case LONG: h.put(FieldType.CHARACTER_TYPE, KSFLUtilities.ecc(oarg.get(j+1))); localModified = true; break;
										case MEDIUM: h.put(FieldType.CHARACTER_TYPE, KSFLUtilities.fcc(oarg.get(j+1))); localModified = true; break;
										case SHORT: h.put(FieldType.CHARACTER_TYPE, KSFLUtilities.tcc(oarg.get(j+1))); localModified = true; break;
										case BYTE: h.put(FieldType.CHARACTER_TYPE, KSFLUtilities.occ(oarg.get(j+1))); localModified = true; break;
										}
									}
								}
								else if (siop.equals("m") || siop.equals("magic") || siop.equals("it") || siop.equals("itype")) {
									if (spec.chunkHeaderSpec().containsType(FieldType.INTEGER_TYPE)) {
										try {
											switch (spec.chunkHeaderSpec().getField(FieldType.INTEGER_TYPE).size()) {
											case LONG: h.put(FieldType.INTEGER_TYPE, Long.parseLong(oarg.get(j+1))); localModified = true; break;
											case MEDIUM: h.put(FieldType.INTEGER_TYPE, Integer.parseInt(oarg.get(j+1))); localModified = true; break;
											case SHORT: h.put(FieldType.INTEGER_TYPE, Short.parseShort(oarg.get(j+1))); localModified = true; break;
											case BYTE: h.put(FieldType.INTEGER_TYPE, Byte.parseByte(oarg.get(j+1))); localModified = true; break;
											}
										} catch (NumberFormatException nfe) {
											System.err.println("Error: Invalid type number "+oarg.get(j+1)+" skipped.");
										}
									}
								}
								else if (siop.equals("i") || siop.equals("id")) {
									if (spec.chunkHeaderSpec().containsType(FieldType.ID_NUMBER)) {
										try {
											switch (spec.chunkHeaderSpec().getField(FieldType.ID_NUMBER).size()) {
											case LONG: h.put(FieldType.ID_NUMBER, Long.parseLong(oarg.get(j+1))); localModified = true; break;
											case MEDIUM: h.put(FieldType.ID_NUMBER, Integer.parseInt(oarg.get(j+1))); localModified = true; break;
											case SHORT: h.put(FieldType.ID_NUMBER, Short.parseShort(oarg.get(j+1))); localModified = true; break;
											case BYTE: h.put(FieldType.ID_NUMBER, Byte.parseByte(oarg.get(j+1))); localModified = true; break;
											}
										} catch (NumberFormatException nfe) {
											System.err.println("Error: Invalid ID number "+oarg.get(j+1)+" skipped.");
										}
									}
								}
								else if (siop.equals("c") || siop.equals("checksum")) {
									if (spec.chunkHeaderSpec().containsType(FieldType.CHECKSUM)) {
										try {
											switch (spec.chunkHeaderSpec().getField(FieldType.CHECKSUM).size()) {
											case LONG: h.put(FieldType.CHECKSUM, Long.parseLong(oarg.get(j+1))); localModified = true; break;
											case MEDIUM: h.put(FieldType.CHECKSUM, Integer.parseInt(oarg.get(j+1))); localModified = true; break;
											case SHORT: h.put(FieldType.CHECKSUM, Short.parseShort(oarg.get(j+1))); localModified = true; break;
											case BYTE: h.put(FieldType.CHECKSUM, Byte.parseByte(oarg.get(j+1))); localModified = true; break;
											}
										} catch (NumberFormatException nfe) {
											System.err.println("Error: Invalid checksum "+oarg.get(j+1)+" skipped.");
										}
									}
								}
								else {
									System.err.println("Unknown attribute type: "+oarg.get(j));
									System.err.println("Should be one of: [c]t[ype] it[ype] m[agic] i[d] c[hecksum]");
								}
							}
							if (localModified) {
								try {
									if (cf.setAttributes(n[1], n[2], n[3], h)) modified = true;
								} catch (ChunkAlreadyExistsException e) {
									System.err.println("Error: Chunk already exists.");
								}
							}
						}
					}
					break;
				case TRUNCATE:
					for (int j = 0; j+nidf(spec.chunkHeaderSpec())+1 <= oarg.size();) {
						Number[] n = parseChunkID(spec.chunkHeaderSpec(), oarg, j, oarg.size());
						j = n[0].intValue();
						String sizestr = oarg.get(j++);
						try {
							int size = Integer.parseInt(sizestr);
							if (cf.contains(n[1], n[2], n[3])) {
								if (cf.setData(n[1], n[2], n[3], KSFLUtilities.resize(cf.getData(n[1], n[2], n[3]), size))) modified = true;
							} else {
								Chunk ch = new Chunk(spec.chunkHeaderSpec().createHeader(), new byte[size]);
								if (ch.getHeader().containsKey(FieldType.CHARACTER_TYPE))
									ch.getHeader().put(FieldType.CHARACTER_TYPE, n[1]);
								if (ch.getHeader().containsKey(FieldType.INTEGER_TYPE))
									ch.getHeader().put(FieldType.INTEGER_TYPE, n[2]);
								if (ch.getHeader().containsKey(FieldType.ID_NUMBER))
									ch.getHeader().put(FieldType.ID_NUMBER, n[3]);
								try { if (cf.add(ch)) modified = true; } catch (ChunkAlreadyExistsException ignored) {}
							}
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid length "+sizestr+" skipped.");
						}
					}
					break;
				case FRESHEN:
					for (int j = 0; j+nidf(spec.chunkHeaderSpec())+1 <= oarg.size();) {
						Number[] n = parseChunkID(spec.chunkHeaderSpec(), oarg, j, oarg.size());
						j = n[0].intValue();
						File f1 = new File(oarg.get(j++));
						if (cf.contains(n[1], n[2], n[3])) {
							byte[] stuff = null;
							try {
								RandomAccessFile raf = new RandomAccessFile(f1, "r");
								stuff = new byte[(int)raf.length()];
								raf.readFully(stuff);
								raf.close();
							} catch (IOException e) {
								System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
								stuff = null;
							}
							if (stuff != null) {
								if (cf.setData(n[1], n[2], n[3], stuff)) modified = true;
							}
						}
					}
					break;
				case UPDATE:
					for (int j = 0; j+nidf(spec.chunkHeaderSpec())+1 <= oarg.size();) {
						Number[] n = parseChunkID(spec.chunkHeaderSpec(), oarg, j, oarg.size());
						j = n[0].intValue();
						File f1 = new File(oarg.get(j++));
						byte[] stuff = null;
						try {
							RandomAccessFile raf = new RandomAccessFile(f1, "r");
							stuff = new byte[(int)raf.length()];
							raf.readFully(stuff);
							raf.close();
						} catch (IOException e) {
							System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							stuff = null;
						}
						if (stuff != null) {
							if (cf.contains(n[1], n[2], n[3])) {
								if (cf.setData(n[1], n[2], n[3], stuff)) modified = true;
							} else {
								Chunk ch = new Chunk(spec.chunkHeaderSpec().createHeader(), stuff);
								if (ch.getHeader().containsKey(FieldType.CHARACTER_TYPE))
									ch.getHeader().put(FieldType.CHARACTER_TYPE, n[1]);
								if (ch.getHeader().containsKey(FieldType.INTEGER_TYPE))
									ch.getHeader().put(FieldType.INTEGER_TYPE, n[2]);
								if (ch.getHeader().containsKey(FieldType.ID_NUMBER))
									ch.getHeader().put(FieldType.ID_NUMBER, n[3]);
								try { if (cf.add(ch)) modified = true; } catch (ChunkAlreadyExistsException ignored) {}
							}
						}
					}
					break;
				default:
					System.err.println("Internal Error: Unimplemented Operation "+op.name());
					break;
				}
			}
		}
		if (cf != null && modified) {
			try {
				cf.close();
			} catch (IOException e) {
				System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
			}
		}
	}
	
	private static String xccs(Number n) {
		if (n instanceof Long) return KSFLUtilities.eccs(n.longValue());
		if (n instanceof Integer) return KSFLUtilities.fccs(n.intValue());
		if (n instanceof Short) return KSFLUtilities.tccs(n.shortValue());
		if (n instanceof Byte) return KSFLUtilities.occs(n.byteValue());
		return "";
	}
	
	private static int ntf(ChunkSpec hs) {
		int n = 0;
		if (hs.containsType(FieldType.CHARACTER_TYPE)) n++;
		if (hs.containsType(FieldType.INTEGER_TYPE)) n++;
		return n;
	}
	
	private static Number[] parseChunkType(ChunkSpec hs, List<String> oarg, int j, int s) {
		Number ctype = null;
		if (j < s && hs.containsType(FieldType.CHARACTER_TYPE)) {
			switch (hs.getField(FieldType.CHARACTER_TYPE).size()) {
			case LONG: ctype = KSFLUtilities.ecc(oarg.get(j++)); break;
			case MEDIUM: ctype = KSFLUtilities.fcc(oarg.get(j++)); break;
			case SHORT: ctype = KSFLUtilities.tcc(oarg.get(j++)); break;
			case BYTE: ctype = KSFLUtilities.occ(oarg.get(j++)); break;
			}
		}
		Number itype = null;
		if (j < s && hs.containsType(FieldType.INTEGER_TYPE)) {
			try {
				switch (hs.getField(FieldType.INTEGER_TYPE).size()) {
				case LONG: itype = Long.parseLong(oarg.get(j)); j++; break;
				case MEDIUM: itype = Integer.parseInt(oarg.get(j)); j++; break;
				case SHORT: itype = Short.parseShort(oarg.get(j)); j++; break;
				case BYTE: itype = Byte.parseByte(oarg.get(j)); j++; break;
				}
			} catch (NumberFormatException nfe) {}
		}
		return new Number[]{j, ctype, itype};
	}
	
	private static int nidf(ChunkSpec hs) {
		int n = 0;
		if (hs.containsType(FieldType.CHARACTER_TYPE)) n++;
		if (hs.containsType(FieldType.INTEGER_TYPE)) n++;
		n++;
		return n;
	}
	
	private static Number[] parseChunkID(ChunkSpec hs, List<String> oarg, int j, int s) {
		Number ctype = null;
		if (j < s && hs.containsType(FieldType.CHARACTER_TYPE)) {
			switch (hs.getField(FieldType.CHARACTER_TYPE).size()) {
			case LONG: ctype = KSFLUtilities.ecc(oarg.get(j++)); break;
			case MEDIUM: ctype = KSFLUtilities.fcc(oarg.get(j++)); break;
			case SHORT: ctype = KSFLUtilities.tcc(oarg.get(j++)); break;
			case BYTE: ctype = KSFLUtilities.occ(oarg.get(j++)); break;
			}
		}
		Number itype = null;
		if (j < s && hs.containsType(FieldType.INTEGER_TYPE)) {
			try {
				switch (hs.getField(FieldType.INTEGER_TYPE).size()) {
				case LONG: itype = Long.parseLong(oarg.get(j)); j++; break;
				case MEDIUM: itype = Integer.parseInt(oarg.get(j)); j++; break;
				case SHORT: itype = Short.parseShort(oarg.get(j)); j++; break;
				case BYTE: itype = Byte.parseByte(oarg.get(j)); j++; break;
				}
			} catch (NumberFormatException nfe) {}
		}
		Number id = null;
		if (j < s) {
			try {
				if (hs.containsType(FieldType.ID_NUMBER)) {
					switch (hs.getField(FieldType.ID_NUMBER).size()) {
					case LONG: id = Long.parseLong(oarg.get(j)); j++; break;
					case MEDIUM: id = Integer.parseInt(oarg.get(j)); j++; break;
					case SHORT: id = Short.parseShort(oarg.get(j)); j++; break;
					case BYTE: id = Byte.parseByte(oarg.get(j)); j++; break;
					}
				} else {
					id = Integer.parseInt(oarg.get(j)); j++;
				}
			} catch (NumberFormatException nfe) {}
		}
		return new Number[]{j, ctype, itype, id};
	}
	
	private static void doDFFOperations(int version, boolean le, File f, List<Operation> ops, List<List<String>> oargs) {
		DFFResourceFile dp = null;
		for (int i = 0; i < ops.size() && i < oargs.size(); i++) {
			Operation op = ops.get(i);
			List<String> oarg = oargs.get(i);
			if (op == Operation.CREATE) {
				try {
					dp = new DFFResourceFile(f, "rwd", DFFResourceFile.CREATE_ALWAYS, version, le);
				} catch (IOException e) {
					System.err.println("Error: Invalid file ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
					return;
				}
			} else {
				if (dp == null) {
					try {
						try {
							dp = new DFFResourceFile(f, "rwd", DFFResourceFile.CREATE_NEVER, version, le);
						} catch (SecurityException e) {
							dp = new DFFResourceFile(f, "r", DFFResourceFile.CREATE_NEVER, version, le);
							System.err.println("Warning: Opened read-only");
						}
					} catch (IOException e) {
						System.err.println("Error: Invalid file ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
						return;
					}
				}
				switch (op) {
				case LIST:
					if (oarg.isEmpty()) {
						for (long type : dp.getTypes()) {
							System.out.println(KSFLUtilities.eccs(type)+"\t"+dp.getResourceCount(type));
						}
					} else for (String typestr : oarg) {
						long type = KSFLUtilities.ecc(typestr);
						for (int id : dp.getIDs(type)) {
							System.out.print(KSFLUtilities.eccs(type)+"\t"+id);
							try {
								System.out.print("\t"+dp.getNameFromID(type, id));
							} catch (UnsupportedOperationException uoe) {}
							System.out.println();
						}
					}
					break;
				case NEXT:
					for (int j = 0; j < oarg.size();) {
						long type = KSFLUtilities.ecc(oarg.get(j++));
						if (j < oarg.size()) {
							try {
								int id = Integer.parseInt(oarg.get(j));
								System.out.println(KSFLUtilities.eccs(type)+"\t"+dp.getNextAvailableID(type, id));
								j++;
							} catch (NumberFormatException nfe) {
								System.out.println(KSFLUtilities.eccs(type)+"\t"+dp.getNextAvailableID(type));
							}
						} else {
							System.out.println(KSFLUtilities.eccs(type)+"\t"+dp.getNextAvailableID(type));
						}
					}
					break;
				case ADD:
					if (!oarg.isEmpty()) {
						byte[] stuff = null;
						try {
							RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(oarg.size()-1)), "r");
							stuff = new byte[(int)raf.length()];
							raf.readFully(stuff);
							raf.close();
						} catch (IOException e) {
							System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							stuff = null;
						}
						if (stuff != null) try {
							long type = (oarg.size() > 1) ? KSFLUtilities.ecc(oarg.get(0)) : KSFLConstants.Data_Bin;
							int id = (oarg.size() > 2) ? Integer.parseInt(oarg.get(1)) : dp.getNextAvailableID(type);
							DFFResource r = new DFFResource(type, id, stuff);
							if (oarg.size() > 3) r.name = oarg.get(2);
							if (oarg.size() > 4) r.setAttributeString(oarg.get(3));
							if (oarg.size() > 5) r.datatype = (short)Integer.parseInt(oarg.get(4));
							try {
								dp.add(r);
							} catch (DFFResourceAlreadyExistsException e) {
								System.err.println("Error: "+KSFLUtilities.eccs(type)+" "+id+" already exists.");
							}
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid number skipped.");
						}
					}
					break;
				case VERIFY:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						long type = KSFLUtilities.ecc(oarg.get(j));
						try {
							int id = Integer.parseInt(oarg.get(j+1));
							System.out.println(dp.contains(type, id));
						} catch (NumberFormatException nfe) {
							System.out.println(dp.contains(type, oarg.get(j+1)));
						}
					}
					break;
				case EXTRACT:
					if (oarg.size() == 1) {
						File f1 = new File(oarg.get(0));
						f1.mkdir();
						for (long type : dp.getTypes()) {
							File f2 = new File(f1, KSFLUtilities.eccs(type));
							f2.mkdir();
							for (int id : dp.getIDs(type)) {
								DFFResource r = dp.get(type, id);
								String n = Integer.toString(r.id);
								if (r.name != null && r.name.length() > 0) n += " " + r.name;
								try {
									FileOutputStream fos = new FileOutputStream(new File(f2, n));
									fos.write(r.data);
									fos.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
								}
								try {
									DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(f2, n+".dffmeta")));
									dos.writeLong(r.type);
									dos.writeInt(r.id);
									dos.writeShort(r.datatype);
									dos.writeShort(r.getAttributes());
									dos.writeUTF(r.name);
									dos.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
								}
							}
						}
					} else if (oarg.size() == 2) {
						File f1 = new File(oarg.get(1));
						f1.mkdir();
						long type = KSFLUtilities.ecc(oarg.get(0));
						for (int id : dp.getIDs(type)) {
							DFFResource r = dp.get(type, id);
							String n = Integer.toString(r.id);
							if (r.name != null && r.name.length() > 0) n += " " + r.name;
							try {
								FileOutputStream fos = new FileOutputStream(new File(f1, n));
								fos.write(r.data);
								fos.close();
							} catch (IOException e) {
								System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							}
							try {
								DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(f1, n+".dffmeta")));
								dos.writeLong(r.type);
								dos.writeInt(r.id);
								dos.writeShort(r.datatype);
								dos.writeShort(r.getAttributes());
								dos.writeUTF(r.name);
								dos.close();
							} catch (IOException e) {
								System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							}
						}
					} else {
						for (int j = 0; j+3 <= oarg.size(); j += 3) {
							long type = KSFLUtilities.ecc(oarg.get(j));
							try {
								int id = Integer.parseInt(oarg.get(j+1));
								byte[] data = dp.getData(type, id);
								if (data == null) {
									System.err.println("Error: "+KSFLUtilities.eccs(type)+" "+id+" does not exist.");
								} else try {
									FileOutputStream fos = new FileOutputStream(new File(oarg.get(j+2)));
									fos.write(data);
									fos.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
								}
							} catch (NumberFormatException nfe) {
								byte[] data = dp.getData(type, oarg.get(j+1));
								if (data == null) {
									System.err.println("Error: "+KSFLUtilities.eccs(type)+" "+oarg.get(j+1)+" does not exist.");
								} else try {
									FileOutputStream fos = new FileOutputStream(new File(oarg.get(j+2)));
									fos.write(data);
									fos.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
								}
							}
						}
					}
					break;
				case INFO:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						long type = KSFLUtilities.ecc(oarg.get(j));
						try {
							int id = Integer.parseInt(oarg.get(j+1));
							DFFResource r = dp.getAttributes(type, id);
							if (r == null) {
								System.err.println("Error: "+KSFLUtilities.eccs(type)+" "+id+" does not exist.");
							} else {
								long l = dp.getLength(type, id);
								String ts = KSFLUtilities.eccs(r.type);
								String th = "0000000000000000" + Long.toHexString(r.type).toUpperCase();
								System.out.println("Type: " + ts + " (0x" + th.substring(th.length()-16) + ")");
								System.out.println("ID:   " + r.id);
								System.out.println("Attr: " + r.getAttributeString().toLowerCase());
								System.out.println("DT:   " + r.datatype);
								System.out.println("Name: " + r.name);
								System.out.println("Len:  " + l);
							}
						} catch (NumberFormatException nfe) {
							DFFResource r = dp.getAttributes(type, oarg.get(j+1));
							if (r == null) {
								System.err.println("Error: "+KSFLUtilities.eccs(type)+" "+oarg.get(j+1)+" does not exist.");
							} else {
								long l = dp.getLength(type, oarg.get(j+1));
								String ts = KSFLUtilities.eccs(r.type);
								String th = "0000000000000000" + Long.toHexString(r.type).toUpperCase();
								System.out.println("Type: " + ts + " (0x" + th.substring(th.length()-16) + ")");
								System.out.println("ID:   " + r.id);
								System.out.println("Attr: " + r.getAttributeString().toLowerCase());
								System.out.println("DT:   " + r.datatype);
								System.out.println("Name: " + r.name);
								System.out.println("Len:  " + l);
							}
						}
					}
					break;
				case CAT:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						long type = KSFLUtilities.ecc(oarg.get(j));
						try {
							int id = Integer.parseInt(oarg.get(j+1));
							byte[] data = dp.getData(type, id);
							if (data == null) System.err.println("Error: "+KSFLUtilities.eccs(type)+" "+id+" does not exist.");
							else try { System.out.write(data); } catch (IOException e) {}
						} catch (NumberFormatException nfe) {
							byte[] data = dp.getData(type, oarg.get(j+1));
							if (data == null) System.err.println("Error: "+KSFLUtilities.eccs(type)+" "+oarg.get(j+1)+" does not exist.");
							else try { System.out.write(data); } catch (IOException e) {}
						}
					}
					break;
				case HEXCAT:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						long type = KSFLUtilities.ecc(oarg.get(j));
						try {
							int id = Integer.parseInt(oarg.get(j+1));
							byte[] data = dp.getData(type, id);
							if (data == null) System.err.println("Error: "+KSFLUtilities.eccs(type)+" "+id+" does not exist.");
							else KSFLUtilities.printHexDump(System.out, data);
						} catch (NumberFormatException nfe) {
							byte[] data = dp.getData(type, oarg.get(j+1));
							if (data == null) System.err.println("Error: "+KSFLUtilities.eccs(type)+" "+oarg.get(j+1)+" does not exist.");
							else KSFLUtilities.printHexDump(System.out, data);
						}
					}
					break;
				case DELETE:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						long type = KSFLUtilities.ecc(oarg.get(j));
						try {
							int id = Integer.parseInt(oarg.get(j+1));
							dp.remove(type, id);
						} catch (NumberFormatException nfe) {
							dp.remove(type, oarg.get(j+1));
						}
					}
					break;
				case SETINFO:
					if (oarg.size() >= 2) {
						long type = KSFLUtilities.ecc(oarg.get(0));
						DFFResource r = null;
						try {
							int id = Integer.parseInt(oarg.get(1));
							r = dp.get(type, id);
						} catch (NumberFormatException nfe) {
							String name = oarg.get(1);
							r = dp.get(type, name);
						}
						if (r == null) {
							System.err.println("Error: Cannot find "+KSFLUtilities.eccs(type)+" "+oarg.get(1));
						} else {
							int id = r.id;
							boolean changed = false;
							for (int j = 2; j+2 <= oarg.size(); j += 2) {
								String siop = oarg.get(j).trim().toLowerCase();
								if (siop.equals("t") || siop.equals("type")) {
									r.type = KSFLUtilities.ecc(oarg.get(j+1));
									changed = true;
								}
								else if (siop.equals("i") || siop.equals("id")) {
									try {
										r.id = Integer.parseInt(oarg.get(j+1));
										changed = true;
									} catch (NumberFormatException nfe) {
										System.err.println("Error: Invalid ID number "+oarg.get(j+1)+" skipped.");
									}
								}
								else if (siop.equals("n") || siop.equals("name")) {
									r.name = oarg.get(j+1);
									changed = true;
								}
								else if (siop.equals("a") || siop.equals("attr") || siop.equals("attrib") || siop.equals("attributes")) {
									r.setAttributeString(oarg.get(j+1));
									changed = true;
								}
								else if (siop.equals("d") || siop.equals("dt") || siop.equals("datatype")) {
									try {
										r.datatype = (short)Integer.parseInt(oarg.get(j+1));
										changed = true;
									} catch (NumberFormatException nfe) {
										System.err.println("Error: Invalid datatype "+oarg.get(j+1)+" skipped.");
									}
								}
								else {
									System.err.println("Unknown attribute type: "+oarg.get(j));
									System.err.println("Should be one of: t[ype] i[d] n[ame] a[ttr[ib[utes]]] d[atatype]");
								}
							}
							if (changed) {
								try {
									dp.setAttributes(type, id, r);
								} catch (DFFResourceAlreadyExistsException e) {
									System.err.println("Error: "+KSFLUtilities.eccs(r.type)+" "+r.id+" already exists.");
								}
							}
						}
					}
					break;
				case TRUNCATE:
					for (int j = 0; j+3 <= oarg.size(); j += 3) {
						try {
							int len = Integer.parseInt(oarg.get(j+2));
							long type = KSFLUtilities.ecc(oarg.get(j));
							try {
								int id = Integer.parseInt(oarg.get(j+1));
								if (dp.contains(type, id)) {
									dp.setLength(type, id, len);
								} else {
									DFFResource r = new DFFResource(type, id, new byte[len]);
									try { dp.add(r); } catch (DFFResourceAlreadyExistsException ignored) {}
								}
							} catch (NumberFormatException nfe) {
								String name = oarg.get(j+1);
								if (dp.contains(type, name)) {
									dp.setLength(type, name, len);
								} else {
									DFFResource r = new DFFResource(type, dp.getNextAvailableID(type), name, new byte[len]);
									try { dp.add(r); } catch (DFFResourceAlreadyExistsException ignored) {}
								}
							}
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid length "+oarg.get(j+2)+" skipped.");
						}
					}
					break;
				case FRESHEN:
					for (int j = 0; j+3 <= oarg.size(); j += 3) {
						long type = KSFLUtilities.ecc(oarg.get(j));
						try {
							int id = Integer.parseInt(oarg.get(j+1));
							if (dp.contains(type, id)) {
								byte[] stuff = null;
								try {
									RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(j+2)), "r");
									stuff = new byte[(int)raf.length()];
									raf.readFully(stuff);
									raf.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
									stuff = null;
								}
								if (stuff != null) {
									dp.setData(type, id, stuff);
								}
							}
						} catch (NumberFormatException nfe) {
							String name = oarg.get(j+1);
							if (dp.contains(type, name)) {
								byte[] stuff = null;
								try {
									RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(j+2)), "r");
									stuff = new byte[(int)raf.length()];
									raf.readFully(stuff);
									raf.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
									stuff = null;
								}
								if (stuff != null) {
									dp.setData(type, name, stuff);
								}
							}
						}
					}
					break;
				case UPDATE:
					for (int j = 0; j+3 <= oarg.size(); j += 3) {
						byte[] stuff = null;
						try {
							RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(j+2)), "r");
							stuff = new byte[(int)raf.length()];
							raf.readFully(stuff);
							raf.close();
						} catch (IOException e) {
							System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							stuff = null;
						}
						if (stuff != null) {
							long type = KSFLUtilities.ecc(oarg.get(j));
							try {
								int id = Integer.parseInt(oarg.get(j+1));
								if (dp.contains(type, id)) {
									dp.setData(type, id, stuff);
								} else {
									DFFResource r = new DFFResource(type, id, stuff);
									try { dp.add(r); } catch (DFFResourceAlreadyExistsException ignored) {}
								}
							} catch (NumberFormatException nfe) {
								String name = oarg.get(j+1);
								if (dp.contains(type, name)) {
									dp.setData(type, name, stuff);
								} else {
									DFFResource r = new DFFResource(type, dp.getNextAvailableID(type), name, stuff);
									try { dp.add(r); } catch (DFFResourceAlreadyExistsException ignored) {}
								}
							}
						}
					}
					break;
				default:
					System.err.println("Internal Error: Unimplemented Operation "+op.name());
					break;
				}
			}
		}
		if (dp != null) {
			dp.close();
		}
	}
	
	private static void doWinPEOperations(File f, List<Operation> ops, List<List<String>> oargs) {
		PEImage img = null;
		PEResourceDirectory rd = null;
		boolean modified = false;
		for (int i = 0; i < ops.size() && i < oargs.size(); i++) {
			Operation op = ops.get(i);
			List<String> oarg = oargs.get(i);
			if (op == Operation.CREATE) {
				img = new PEImage();
				img.msdosStub = PEImage.MSDOS_STUB_DEFAULT;
				img.peSignature = PEImage.PE_SIGNATURE;
				img.machine = PEImage.MACHINE_I386;
				img.numSections = 1;
				img.creationDate = (int)(System.currentTimeMillis() / 1000);
				img.symbolTablePtr = 0;
				img.numSymbols = 0;
				img.optHeaderSize = 0xE0;
				img.characteristics = PEImage.CHARACTERISTICS_DLL | PEImage.CHARACTERISTICS_32BIT_MACHINE | PEImage.CHARACTERISTICS_EXECUTABLE_IMAGE | PEImage.CHARACTERISTICS_RELOCS_STRIPPED;
				img.magic = PEImage.MAGIC_PE32;
				img.linkerVersion = 0;
				img.codeSize = 0;
				img.dataSize = 0;
				img.bssSize = 0;
				img.entryPointOfst = 512;
				img.codeOfst = 0;
				img.dataOfst = 0;
				img.base = 0x400000;
				img.sectionAlign = 512;
				img.fileAlign = 512;
				img.osVersion = PEImage.SUBSYSTEM_VERSION_WIN32;
				img.imageVersion = 0;
				img.subsystemVersion = PEImage.SUBSYSTEM_VERSION_WIN32;
				img.win32versionValue = 0;
				img.sizeOfImage = 1024;
				img.sizeOfHeaders = 512;
				img.checksum = 0;
				img.subsystem = PEImage.SUBSYSTEM_WINDOWS_GUI;
				img.dllCharacteristics = PEImage.DLL_CHARACTERISTICS_NO_SEH;
				img.stackReserveSize = 0x100000;
				img.stackCommitSize = 0x1000;
				img.heapReserveSize = 0x100000;
				img.heapCommitSize = 0x1000;
				img.loaderFlags = 0;
				img.numberOfRvaAndSizes = 16;
				for (int j = 0; j < 16; j++) img.dirEntries.add(new PEDirectoryEntry());
				img.dirEntries.get(2).virtualAddress = 512;
				img.dirEntries.get(2).size = 0;
				img.sections.add(new PESection());
				img.sections.get(0).name = new byte[]{'.','r','s','r','c',0,0,0};
				img.sections.get(0).virtualSize = 0;
				img.sections.get(0).virtualAddress = 512;
				img.sections.get(0).rawDataSize = 0;
				img.sections.get(0).rawDataOfst = 512;
				img.sections.get(0).relocOfst = 0;
				img.sections.get(0).lineNumOfst = 0;
				img.sections.get(0).relocCnt = 0;
				img.sections.get(0).lineNumCnt = 0;
				img.sections.get(0).characteristics = PESection.CHARACTERISTICS_READ;
				img.sections.get(0).data = new byte[0];
				rd = new PEResourceDirectory();
				modified = true;
			} else {
				if (img == null) {
					try {
						RandomAccessFile raf = new RandomAccessFile(f, "r");
						img = new PEImage();
						img.decompile(raf);
						raf.close();
						int[] rl = img.ofstToSNO(img.dirEntries.get(2).virtualAddress, img.headerSize());
						PESection rs = img.sections.get(rl[0]);
						rd = new PEResourceDirectory();
						rd.decompile(rs.data, rl[1]);
						modified = false;
					} catch (IOException e) {
						System.err.println("Error: Invalid file ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
						return;
					}
				}
				switch (op) {
				case LIST:
					PEResourceDirectory listrd = rd;
					for (String name : oarg) {
						boolean found = false;
						try {
							int id = Integer.parseInt(name);
							for (PEResourceEntry e : listrd.entries) {
								if (e.name == null && e.id == id) {
									if (e instanceof PEResourceDirectory) {
										listrd = (PEResourceDirectory)e;
									} else {
										listrd = null;
									}
									found = true;
									break;
								}
							}
						} catch (NumberFormatException nfe) {}
						if (!found) {
							for (PEResourceEntry e : listrd.entries) {
								if (e.name != null && e.name.equals(name)) {
									if (e instanceof PEResourceDirectory) {
										listrd = (PEResourceDirectory)e;
									} else {
										listrd = null;
									}
									found = true;
									break;
								}
							}
						}
						if (!found) listrd = null;
						if (listrd == null) break;
					}
					if (listrd != null) {
						for (PEResourceEntry e : listrd.entries) {
							if (e instanceof PEResourceDirectory) System.out.print("d\t");
							if (e instanceof PEResourceData) System.out.print("-\t");
							System.out.print("\t" + ((e.name == null) ? "" : e.name));
							System.out.println("\t" + e.id);
						}
					}
					break;
				case NEXT:
					PEResourceDirectory nextrd = rd;
					for (String name : oarg) {
						boolean found = false;
						try {
							int id = Integer.parseInt(name);
							for (PEResourceEntry e : nextrd.entries) {
								if (e.name == null && e.id == id) {
									if (e instanceof PEResourceDirectory) {
										nextrd = (PEResourceDirectory)e;
									} else {
										nextrd = null;
									}
									found = true;
									break;
								}
							}
						} catch (NumberFormatException nfe) {}
						if (!found) {
							for (PEResourceEntry e : nextrd.entries) {
								if (e.name != null && e.name.equals(name)) {
									if (e instanceof PEResourceDirectory) {
										nextrd = (PEResourceDirectory)e;
									} else {
										nextrd = null;
									}
									found = true;
									break;
								}
							}
						}
						if (!found) nextrd = null;
						if (nextrd == null) break;
					}
					if (nextrd != null) {
						Set<Integer> ids = new HashSet<Integer>();
						for (PEResourceEntry e : nextrd.entries) ids.add(e.id);
						int id = 1;
						while (ids.contains(id)) id++;
						System.out.println(id);
					}
					break;
				case ADD:
					if (oarg.size() >= 2) {
						byte[] stuff = null;
						try {
							RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(oarg.size()-1)), "r");
							stuff = new byte[(int)raf.length()];
							raf.readFully(stuff);
							raf.close();
						} catch (IOException e) {
							System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							stuff = null;
						}
						if (stuff != null) {
							PEResourceData ne = new PEResourceData();
							ne.data = stuff;
							try {
								ne.id = Integer.parseInt(oarg.get(oarg.size()-2));
								ne.name = null;
							} catch (NumberFormatException nfe) {
								ne.id = Integer.MIN_VALUE;
								ne.name = oarg.get(oarg.size()-2);
							}
							PEResourceDirectory addrd = rd;
							for (int j = 0; j < oarg.size()-2; j++) {
								String name = oarg.get(j);
								boolean found = false;
								try {
									int id = Integer.parseInt(name);
									for (PEResourceEntry e : addrd.entries) {
										if (e.name == null && e.id == id) {
											if (e instanceof PEResourceDirectory) {
												addrd = (PEResourceDirectory)e;
											} else {
												addrd = null;
											}
											found = true;
											break;
										}
									}
								} catch (NumberFormatException nfe) {}
								if (!found) {
									for (PEResourceEntry e : addrd.entries) {
										if (e.name != null && e.name.equals(name)) {
											if (e instanceof PEResourceDirectory) {
												addrd = (PEResourceDirectory)e;
											} else {
												addrd = null;
											}
											found = true;
											break;
										}
									}
								}
								if (!found) addrd = null;
								if (addrd == null) break;
							}
							if (addrd != null) {
								boolean exists = false;
								for (PEResourceEntry e : addrd.entries) {
									if (e.name != null && ne.name != null && e.name.equals(ne.name)) {
										exists = true;
									} else if (e.name == null && ne.name == null && e.id == ne.id) {
										exists = true;
									}
								}
								if (exists) {
									System.err.println("Error: Resource already exists.");
								} else {
									addrd.entries.add(ne);
									modified = true;
								}
							}
						}
					}
					break;
				case VERIFY:
					PEResourceEntry verifyre = rd;
					for (String name : oarg) {
						if (verifyre instanceof PEResourceDirectory) {
							boolean found = false;
							try {
								int id = Integer.parseInt(name);
								for (PEResourceEntry e : ((PEResourceDirectory)verifyre).entries) {
									if (e.name == null && e.id == id) {
										verifyre = e;
										found = true;
										break;
									}
								}
							} catch (NumberFormatException nfe) {}
							if (!found) {
								for (PEResourceEntry e : ((PEResourceDirectory)verifyre).entries) {
									if (e.name != null && e.name.equals(name)) {
										verifyre = e;
										found = true;
										break;
									}
								}
							}
							if (!found) verifyre = null;
						} else {
							verifyre = null;
						}
					}
					System.out.println(verifyre != null);
					break;
				case EXTRACT:
					PEResourceEntry extre = rd;
					for (int j = 0; j < oarg.size()-1; j++) {
						String name = oarg.get(j);
						if (extre instanceof PEResourceDirectory) {
							boolean found = false;
							try {
								int id = Integer.parseInt(name);
								for (PEResourceEntry e : ((PEResourceDirectory)extre).entries) {
									if (e.name == null && e.id == id) {
										extre = e;
										found = true;
										break;
									}
								}
							} catch (NumberFormatException nfe) {}
							if (!found) {
								for (PEResourceEntry e : ((PEResourceDirectory)extre).entries) {
									if (e.name != null && e.name.equals(name)) {
										extre = e;
										found = true;
										break;
									}
								}
							}
							if (!found) extre = null;
						} else {
							extre = null;
						}
					}
					if ((extre != null) && !oarg.isEmpty()) winPEExtract(extre, new File(oarg.get(oarg.size()-1)));
					break;
				case INFO:
					PEResourceEntry infore = rd;
					for (String name : oarg) {
						if (infore instanceof PEResourceDirectory) {
							boolean found = false;
							try {
								int id = Integer.parseInt(name);
								for (PEResourceEntry e : ((PEResourceDirectory)infore).entries) {
									if (e.name == null && e.id == id) {
										infore = e;
										found = true;
										break;
									}
								}
							} catch (NumberFormatException nfe) {}
							if (!found) {
								for (PEResourceEntry e : ((PEResourceDirectory)infore).entries) {
									if (e.name != null && e.name.equals(name)) {
										infore = e;
										found = true;
										break;
									}
								}
							}
							if (!found) infore = null;
						} else {
							infore = null;
						}
					}
					if (infore != null) {
						System.out.println("ID:   " + infore.id);
						System.out.println("Name: " + ((infore.name == null) ? "" : infore.name));
						if (infore instanceof PEResourceData) {
							System.out.println("CP:   " + ((PEResourceData)infore).codePage);
							System.out.println("Len:  " + ((PEResourceData)infore).data.length);
						}
						if (infore instanceof PEResourceDirectory) {
							System.out.println("Char: " + Integer.toHexString(((PEResourceDirectory)infore).characteristics).toUpperCase());
							System.out.println("Time: " + Integer.toHexString(((PEResourceDirectory)infore).timeStamp).toUpperCase());
							System.out.println("Vers: " + Integer.toHexString(((PEResourceDirectory)infore).resourceVersion).toUpperCase());
							System.out.println("Cnt:  " + ((PEResourceDirectory)infore).entries.size());
						}
					}
					break;
				case CAT:
					PEResourceEntry catre = rd;
					for (String name : oarg) {
						if (catre instanceof PEResourceDirectory) {
							boolean found = false;
							try {
								int id = Integer.parseInt(name);
								for (PEResourceEntry e : ((PEResourceDirectory)catre).entries) {
									if (e.name == null && e.id == id) {
										catre = e;
										found = true;
										break;
									}
								}
							} catch (NumberFormatException nfe) {}
							if (!found) {
								for (PEResourceEntry e : ((PEResourceDirectory)catre).entries) {
									if (e.name != null && e.name.equals(name)) {
										catre = e;
										found = true;
										break;
									}
								}
							}
							if (!found) catre = null;
						} else {
							catre = null;
						}
					}
					if (catre instanceof PEResourceData) {
						try { System.out.write(((PEResourceData)catre).data); } catch (IOException ioe) {}
					}
					break;
				case HEXCAT:
					PEResourceEntry hexcatre = rd;
					for (String name : oarg) {
						if (hexcatre instanceof PEResourceDirectory) {
							boolean found = false;
							try {
								int id = Integer.parseInt(name);
								for (PEResourceEntry e : ((PEResourceDirectory)hexcatre).entries) {
									if (e.name == null && e.id == id) {
										hexcatre = e;
										found = true;
										break;
									}
								}
							} catch (NumberFormatException nfe) {}
							if (!found) {
								for (PEResourceEntry e : ((PEResourceDirectory)hexcatre).entries) {
									if (e.name != null && e.name.equals(name)) {
										hexcatre = e;
										found = true;
										break;
									}
								}
							}
							if (!found) hexcatre = null;
						} else {
							hexcatre = null;
						}
					}
					if (hexcatre instanceof PEResourceData) {
						KSFLUtilities.printHexDump(System.out, ((PEResourceData)hexcatre).data);
					}
					break;
				case DELETE:
					PEResourceDirectory delrd = null;
					PEResourceEntry delre = rd;
					for (String name : oarg) {
						if (delre instanceof PEResourceDirectory) {
							delrd = (PEResourceDirectory)delre;
							boolean found = false;
							try {
								int id = Integer.parseInt(name);
								for (PEResourceEntry e : ((PEResourceDirectory)delre).entries) {
									if (e.name == null && e.id == id) {
										delre = e;
										found = true;
										break;
									}
								}
							} catch (NumberFormatException nfe) {}
							if (!found) {
								for (PEResourceEntry e : ((PEResourceDirectory)delre).entries) {
									if (e.name != null && e.name.equals(name)) {
										delre = e;
										found = true;
										break;
									}
								}
							}
							if (!found) delre = null;
						} else {
							delrd = null;
							delre = null;
						}
					}
					if (delrd != null && delre != null) {
						delrd.entries.remove(delre);
						modified = true;
					}
					break;
				case SETINFO:
					if (!oarg.isEmpty()) {
						PEResourceDirectory setinford = null;
						PEResourceEntry setinfore = rd;
						for (int j = 0; j < oarg.size()-1; j++) {
							String name = oarg.get(j);
							if (setinfore instanceof PEResourceDirectory) {
								setinford = (PEResourceDirectory)setinfore;
								boolean found = false;
								try {
									int id = Integer.parseInt(name);
									for (PEResourceEntry e : ((PEResourceDirectory)setinfore).entries) {
										if (e.name == null && e.id == id) {
											setinfore = e;
											found = true;
											break;
										}
									}
								} catch (NumberFormatException nfe) {}
								if (!found) {
									for (PEResourceEntry e : ((PEResourceDirectory)setinfore).entries) {
										if (e.name != null && e.name.equals(name)) {
											setinfore = e;
											found = true;
											break;
										}
									}
								}
								if (!found) setinfore = null;
							} else {
								setinford = null;
								setinfore = null;
							}
						}
						if (setinford != null && setinfore != null) {
							try {
								int id = Integer.parseInt(oarg.get(oarg.size()-1));
								boolean exists = false;
								for (PEResourceEntry e : setinford.entries) {
									if (e.name == null && e.id == id) {
										exists = true;
									}
								}
								if (exists) {
									System.err.println("Error: Resource already exists.");
								} else {
									setinfore.id = id;
									setinfore.name = null;
									modified = true;
								}
							} catch (NumberFormatException nfe) {
								String name = oarg.get(oarg.size()-1);
								boolean exists = false;
								for (PEResourceEntry e : setinford.entries) {
									if (e.name != null && e.name.equals(name)) {
										exists = true;
									}
								}
								if (exists) {
									System.err.println("Error: Resource already exists.");
								} else {
									setinfore.id = Integer.MIN_VALUE;
									setinfore.name = name;
									modified = true;
								}
							}
						}
					}
					break;
				case TRUNCATE:
					if (oarg.size() >= 2) {
						try {
							int len = Integer.parseInt(oarg.get(oarg.size()-1));
							PEResourceData ne = new PEResourceData();
							try {
								ne.id = Integer.parseInt(oarg.get(oarg.size()-2));
								ne.name = null;
							} catch (NumberFormatException nfe) {
								ne.id = Integer.MIN_VALUE;
								ne.name = oarg.get(oarg.size()-2);
							}
							PEResourceDirectory addrd = rd;
							for (int j = 0; j < oarg.size()-2; j++) {
								String name = oarg.get(j);
								boolean found = false;
								try {
									int id = Integer.parseInt(name);
									for (PEResourceEntry e : addrd.entries) {
										if (e.name == null && e.id == id) {
											if (e instanceof PEResourceDirectory) {
												addrd = (PEResourceDirectory)e;
											} else {
												addrd = null;
											}
											found = true;
											break;
										}
									}
								} catch (NumberFormatException nfe) {}
								if (!found) {
									for (PEResourceEntry e : addrd.entries) {
										if (e.name != null && e.name.equals(name)) {
											if (e instanceof PEResourceDirectory) {
												addrd = (PEResourceDirectory)e;
											} else {
												addrd = null;
											}
											found = true;
											break;
										}
									}
								}
								if (!found) addrd = null;
								if (addrd == null) break;
							}
							if (addrd != null) {
								boolean exists = false;
								for (PEResourceEntry e : addrd.entries) {
									if (e.name != null && ne.name != null && e.name.equals(ne.name)) {
										if (e instanceof PEResourceData) {
											((PEResourceData)e).data = KSFLUtilities.resize(((PEResourceData)e).data, len);
											modified = true;
										}
										exists = true;
									} else if (e.name == null && ne.name == null && e.id == ne.id) {
										if (e instanceof PEResourceData) {
											((PEResourceData)e).data = KSFLUtilities.resize(((PEResourceData)e).data, len);
											modified = true;
										}
										exists = true;
									}
								}
								if (!exists) {
									ne.data = new byte[len];
									addrd.entries.add(ne);
									modified = true;
								}
							}
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid length "+oarg.get(oarg.size()-1)+" skipped.");
						}
					}
					break;
				case FRESHEN:
					if (oarg.size() >= 2) {
						byte[] stuff = null;
						try {
							RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(oarg.size()-1)), "r");
							stuff = new byte[(int)raf.length()];
							raf.readFully(stuff);
							raf.close();
						} catch (IOException e) {
							System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
						}
						if (stuff != null) {
							PEResourceData ne = new PEResourceData();
							ne.data = stuff;
							try {
								ne.id = Integer.parseInt(oarg.get(oarg.size()-2));
								ne.name = null;
							} catch (NumberFormatException nfe) {
								ne.id = Integer.MIN_VALUE;
								ne.name = oarg.get(oarg.size()-2);
							}
							PEResourceDirectory addrd = rd;
							for (int j = 0; j < oarg.size()-2; j++) {
								String name = oarg.get(j);
								boolean found = false;
								try {
									int id = Integer.parseInt(name);
									for (PEResourceEntry e : addrd.entries) {
										if (e.name == null && e.id == id) {
											if (e instanceof PEResourceDirectory) {
												addrd = (PEResourceDirectory)e;
											} else {
												addrd = null;
											}
											found = true;
											break;
										}
									}
								} catch (NumberFormatException nfe) {}
								if (!found) {
									for (PEResourceEntry e : addrd.entries) {
										if (e.name != null && e.name.equals(name)) {
											if (e instanceof PEResourceDirectory) {
												addrd = (PEResourceDirectory)e;
											} else {
												addrd = null;
											}
											found = true;
											break;
										}
									}
								}
								if (!found) addrd = null;
								if (addrd == null) break;
							}
							if (addrd != null) {
								for (PEResourceEntry e : addrd.entries) {
									if (e.name != null && ne.name != null && e.name.equals(ne.name)) {
										if (e instanceof PEResourceData) {
											((PEResourceData)e).data = stuff;
											modified = true;
										}
									} else if (e.name == null && ne.name == null && e.id == ne.id) {
										if (e instanceof PEResourceData) {
											((PEResourceData)e).data = stuff;
											modified = true;
										}
									}
								}
							}
						}
					}
					break;
				case UPDATE:
					if (oarg.size() >= 2) {
						byte[] stuff = null;
						try {
							RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(oarg.size()-1)), "r");
							stuff = new byte[(int)raf.length()];
							raf.readFully(stuff);
							raf.close();
						} catch (IOException e) {
							System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
						}
						if (stuff != null) {
							PEResourceData ne = new PEResourceData();
							ne.data = stuff;
							try {
								ne.id = Integer.parseInt(oarg.get(oarg.size()-2));
								ne.name = null;
							} catch (NumberFormatException nfe) {
								ne.id = Integer.MIN_VALUE;
								ne.name = oarg.get(oarg.size()-2);
							}
							PEResourceDirectory addrd = rd;
							for (int j = 0; j < oarg.size()-2; j++) {
								String name = oarg.get(j);
								boolean found = false;
								try {
									int id = Integer.parseInt(name);
									for (PEResourceEntry e : addrd.entries) {
										if (e.name == null && e.id == id) {
											if (e instanceof PEResourceDirectory) {
												addrd = (PEResourceDirectory)e;
											} else {
												addrd = null;
											}
											found = true;
											break;
										}
									}
								} catch (NumberFormatException nfe) {}
								if (!found) {
									for (PEResourceEntry e : addrd.entries) {
										if (e.name != null && e.name.equals(name)) {
											if (e instanceof PEResourceDirectory) {
												addrd = (PEResourceDirectory)e;
											} else {
												addrd = null;
											}
											found = true;
											break;
										}
									}
								}
								if (!found) addrd = null;
								if (addrd == null) break;
							}
							if (addrd != null) {
								boolean exists = false;
								for (PEResourceEntry e : addrd.entries) {
									if (e.name != null && ne.name != null && e.name.equals(ne.name)) {
										if (e instanceof PEResourceData) {
											((PEResourceData)e).data = stuff;
											modified = true;
										}
										exists = true;
									} else if (e.name == null && ne.name == null && e.id == ne.id) {
										if (e instanceof PEResourceData) {
											((PEResourceData)e).data = stuff;
											modified = true;
										}
										exists = true;
									}
								}
								if (!exists) {
									addrd.entries.add(ne);
									modified = true;
								}
							}
						}
					}
					break;
				default:
					System.err.println("Internal Error: Unimplemented Operation "+op.name());
					break;
				}
			}
		}
		if (img != null && modified) {
			try {
				RandomAccessFile raf = new RandomAccessFile(f, "rwd");
				raf.setLength(0);
				int[] rl = img.ofstToSNO(img.dirEntries.get(2).virtualAddress, img.headerSize());
				PESection rs = img.sections.get(rl[0]);
				rs.data = new byte[rd.calculateTotalLength()];
				rd.recompile(rs.data, rl[1]);
				img.recalculate();
				img.recompile(raf);
				raf.close();
			} catch (IOException e) {
				System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
			}
		}
	}
	
	private static void winPEExtract(PEResourceEntry re, File f) {
		if (re instanceof PEResourceData) {
			try {
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(((PEResourceData)re).data);
				fos.close();
			} catch (IOException e) {
				System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
			}
		} else if (re instanceof PEResourceDirectory) {
			f.mkdir();
			for (PEResourceEntry subre : ((PEResourceDirectory)re).entries) {
				winPEExtract(subre, new File(f, subre.name != null ? subre.name : Integer.toString(subre.id)));
			}
		}
	}
	
	private static void doPRCOperations(File f, List<Operation> ops, List<List<String>> oargs) {
		PalmResourceFile rp = null;
		for (int i = 0; i < ops.size() && i < oargs.size(); i++) {
			Operation op = ops.get(i);
			List<String> oarg = oargs.get(i);
			if (op == Operation.CREATE) {
				try {
					rp = new PalmResourceFile(f, "rwd", PalmResourceFile.CREATE_ALWAYS);
				} catch (IOException e) {
					System.err.println("Error: Invalid file ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
					return;
				}
			} else {
				if (rp == null) {
					try {
						try {
							rp = new PalmResourceFile(f, "rwd", PalmResourceFile.CREATE_NEVER);
						} catch (SecurityException e) {
							rp = new PalmResourceFile(f, "r", PalmResourceFile.CREATE_NEVER);
							System.err.println("Warning: Opened read-only");
						}
					} catch (IOException e) {
						System.err.println("Error: Invalid file ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
						return;
					}
				}
				switch (op) {
				case LIST:
					if (oarg.isEmpty()) {
						for (int type : rp.getTypes()) {
							System.out.println(KSFLUtilities.fccs(type)+"\t"+rp.getResourceCount(type));
						}
					} else for (String typestr : oarg) {
						int type = KSFLUtilities.fcc(typestr);
						for (short id : rp.getIDs(type)) {
							System.out.println(KSFLUtilities.fccs(type)+"\t"+id);
						}
					}
					break;
				case NEXT:
					for (int j = 0; j < oarg.size();) {
						int type = KSFLUtilities.fcc(oarg.get(j++));
						if (j < oarg.size()) {
							try {
								short id = (short)Integer.parseInt(oarg.get(j));
								System.out.println(KSFLUtilities.fccs(type)+"\t"+rp.getNextAvailableID(type, id));
								j++;
							} catch (NumberFormatException nfe) {
								System.out.println(KSFLUtilities.fccs(type)+"\t"+rp.getNextAvailableID(type));
							}
						} else {
							System.out.println(KSFLUtilities.fccs(type)+"\t"+rp.getNextAvailableID(type));
						}
					}
					break;
				case ADD:
					if (!oarg.isEmpty()) {
						byte[] stuff = null;
						try {
							RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(oarg.size()-1)), "r");
							stuff = new byte[(int)raf.length()];
							raf.readFully(stuff);
							raf.close();
						} catch (IOException e) {
							System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							stuff = null;
						}
						if (stuff != null) try {
							int type = (oarg.size() > 1) ? KSFLUtilities.fcc(oarg.get(0)) : KSFLConstants.DATA;
							short id = (oarg.size() > 2) ? (short)Integer.parseInt(oarg.get(1)) : rp.getNextAvailableID(type);
							PalmResource r = new PalmResource(type, id, stuff);
							try {
								rp.add(r);
							} catch (PalmResourceAlreadyExistsException e) {
								System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+id+" already exists.");
							}
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid number skipped.");
						}
					}
					break;
				case VERIFY:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						int type = KSFLUtilities.fcc(oarg.get(j));
						try {
							short id = (short)Integer.parseInt(oarg.get(j+1));
							System.out.println(rp.contains(type, id));
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid ID number "+oarg.get(j+1)+" skipped.");
						}
					}
					break;
				case EXTRACT:
					if (oarg.size() == 1) {
						File f1 = new File(oarg.get(0));
						f1.mkdir();
						for (int type : rp.getTypes()) {
							File f2 = new File(f1, KSFLUtilities.fccs(type));
							f2.mkdir();
							for (short id : rp.getIDs(type)) {
								PalmResource r = rp.get(type, id);
								String n = Integer.toString(r.id);
								try {
									FileOutputStream fos = new FileOutputStream(new File(f2, n));
									fos.write(r.data);
									fos.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
								}
							}
						}
						try {
							FileOutputStream fos = new FileOutputStream(new File(f1, "header.bin"));
							fos.write(rp.getPRCHeader());
							fos.close();
						} catch (IOException e) {
							System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
						}
					} else if (oarg.size() == 2) {
						File f1 = new File(oarg.get(1));
						f1.mkdir();
						int type = KSFLUtilities.fcc(oarg.get(0));
						for (short id : rp.getIDs(type)) {
							PalmResource r = rp.get(type, id);
							String n = Integer.toString(r.id);
							try {
								FileOutputStream fos = new FileOutputStream(new File(f1, n));
								fos.write(r.data);
								fos.close();
							} catch (IOException e) {
								System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							}
						}
					} else {
						for (int j = 0; j+3 <= oarg.size(); j += 3) {
							int type = KSFLUtilities.fcc(oarg.get(j));
							try {
								short id = (short)Integer.parseInt(oarg.get(j+1));
								byte[] data = rp.getData(type, id);
								if (data == null) {
									System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+id+" does not exist.");
								} else try {
									FileOutputStream fos = new FileOutputStream(new File(oarg.get(j+2)));
									fos.write(data);
									fos.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
								}
							} catch (NumberFormatException nfe) {
								System.err.println("Error: Invalid ID number "+oarg.get(j+1)+" skipped.");
							}
						}
					}
					break;
				case INFO:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						int type = KSFLUtilities.fcc(oarg.get(j));
						try {
							short id = (short)Integer.parseInt(oarg.get(j+1));
							PalmResource r = rp.get(type, id);
							if (r == null) {
								System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+id+" does not exist.");
							} else {
								String ts = KSFLUtilities.fccs(r.type);
								String th = "00000000" + Integer.toHexString(r.type).toUpperCase();
								System.out.println("Type: " + ts + " (0x" + th.substring(th.length()-8) + ")");
								System.out.println("ID:   " + r.id);
								System.out.println("Len:  " + r.data.length);
							}
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid ID number "+oarg.get(j+1)+" skipped.");
						}
					}
					break;
				case CAT:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						int type = KSFLUtilities.fcc(oarg.get(j));
						try {
							short id = (short)Integer.parseInt(oarg.get(j+1));
							byte[] data = rp.getData(type, id);
							if (data == null) System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+oarg.get(j+1)+" does not exist.");
							else try { System.out.write(data); } catch (IOException ioe) {}
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid ID number "+oarg.get(j+1)+" skipped.");
						}
					}
					break;
				case HEXCAT:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						int type = KSFLUtilities.fcc(oarg.get(j));
						try {
							short id = (short)Integer.parseInt(oarg.get(j+1));
							byte[] data = rp.getData(type, id);
							if (data == null) System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+oarg.get(j+1)+" does not exist.");
							else KSFLUtilities.printHexDump(System.out, data);
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid ID number "+oarg.get(j+1)+" skipped.");
						}
					}
					break;
				case DELETE:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						int type = KSFLUtilities.fcc(oarg.get(j));
						try {
							short id = (short)Integer.parseInt(oarg.get(j+1));
							rp.remove(type, id);
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid ID number "+oarg.get(j+1)+" skipped.");
						}
					}
					break;
				case SETINFO:
					if (oarg.size() >= 2) {
						int type = KSFLUtilities.fcc(oarg.get(0));
						PalmResource r = null;
						try {
							short id = (short)Integer.parseInt(oarg.get(1));
							r = rp.get(type, id);
							if (r == null) {
								System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+oarg.get(1)+" doesn't exist.");
							} else {
								boolean changed = false;
								for (int j = 2; j+2 <= oarg.size(); j += 2) {
									String siop = oarg.get(j).trim().toLowerCase();
									if (siop.equals("t") || siop.equals("type")) {
										r.type = KSFLUtilities.fcc(oarg.get(j+1));
										changed = true;
									}
									else if (siop.equals("i") || siop.equals("id")) {
										try {
											r.id = (short)Integer.parseInt(oarg.get(j+1));
											changed = true;
										} catch (NumberFormatException nfe) {
											System.err.println("Warning: Invalid ID number "+oarg.get(j+1)+" skipped.");
										}
									}
									else {
										System.err.println("Unknown attribute type: "+oarg.get(j));
										System.err.println("Should be one of: t[ype] i[d]");
									}
								}
								if (changed) {
									try {
										rp.setAttributes(type, id, r);
									} catch (PalmResourceAlreadyExistsException e) {
										System.err.println("Error: "+KSFLUtilities.fccs(r.type)+" "+r.id+" already exists.");
									}
								}
							}
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid ID number "+oarg.get(1)+" skipped.");
						}
					}
					break;
				case TRUNCATE:
					for (int j = 0; j+3 <= oarg.size(); j += 3) {
						try {
							int len = Integer.parseInt(oarg.get(j+2));
							int type = KSFLUtilities.fcc(oarg.get(j));
							try {
								short id = (short)Integer.parseInt(oarg.get(j+1));
								if (rp.contains(type, id)) {
									rp.setData(type, id, KSFLUtilities.resize(rp.getData(type, id), len));
								} else {
									PalmResource r = new PalmResource(type, id, new byte[len]);
									try { rp.add(r); } catch (PalmResourceAlreadyExistsException ignored) {}
								}
							} catch (NumberFormatException nfe) {
								System.err.println("Error: Invalid ID number "+oarg.get(j+1)+" skipped.");
							}
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid length "+oarg.get(j+2)+" skipped.");
						}
					}
					break;
				case FRESHEN:
					for (int j = 0; j+3 <= oarg.size(); j += 3) {
						int type = KSFLUtilities.fcc(oarg.get(j));
						try {
							short id = (short)Integer.parseInt(oarg.get(j+1));
							if (rp.contains(type, id)) {
								byte[] stuff = null;
								try {
									RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(j+2)), "r");
									stuff = new byte[(int)raf.length()];
									raf.readFully(stuff);
									raf.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
									stuff = null;
								}
								if (stuff != null) {
									rp.setData(type, id, stuff);
								}
							}
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid ID number "+oarg.get(j+1)+" skipped.");
						}
					}
					break;
				case UPDATE:
					for (int j = 0; j+3 <= oarg.size(); j += 3) {
						byte[] stuff = null;
						try {
							RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(j+2)), "r");
							stuff = new byte[(int)raf.length()];
							raf.readFully(stuff);
							raf.close();
						} catch (IOException e) {
							System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							stuff = null;
						}
						if (stuff != null) {
							int type = KSFLUtilities.fcc(oarg.get(j));
							try {
								short id = (short)Integer.parseInt(oarg.get(j+1));
								if (rp.contains(type, id)) {
									rp.setData(type, id, stuff);
								} else {
									PalmResource r = new PalmResource(type, id, stuff);
									try { rp.add(r); } catch (PalmResourceAlreadyExistsException ignored) {}
								}
							} catch (NumberFormatException nfe) {
								System.err.println("Error: Invalid ID number "+oarg.get(j+1)+" skipped.");
							}
						}
					}
					break;
				default:
					System.err.println("Internal Error: Unimplemented Operation "+op.name());
					break;
				}
			}
		}
		if (rp != null) {
			rp.close();
		}
	}
	
	private static void doRsrcOperations(File f, List<Operation> ops, List<List<String>> oargs) {
		MacResourceFile rp = null;
		for (int i = 0; i < ops.size() && i < oargs.size(); i++) {
			Operation op = ops.get(i);
			List<String> oarg = oargs.get(i);
			if (op == Operation.CREATE) {
				try {
					rp = new MacResourceFile(f, "rwd", MacResourceFile.CREATE_ALWAYS);
				} catch (IOException e) {
					System.err.println("Error: Invalid file ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
					return;
				}
			} else {
				if (rp == null) {
					try {
						try {
							rp = new MacResourceFile(f, "rwd", MacResourceFile.CREATE_NEVER);
						} catch (SecurityException e) {
							rp = new MacResourceFile(f, "r", MacResourceFile.CREATE_NEVER);
							System.err.println("Warning: Opened read-only");
						}
					} catch (IOException e) {
						System.err.println("Error: Invalid file ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
						return;
					}
				}
				switch (op) {
				case LIST:
					if (oarg.isEmpty()) {
						for (int type : rp.getTypes()) {
							System.out.println(KSFLUtilities.fccs(type)+"\t"+rp.getResourceCount(type));
						}
					} else for (String typestr : oarg) {
						int type = KSFLUtilities.fcc(typestr);
						for (short id : rp.getIDs(type)) {
							System.out.println(KSFLUtilities.fccs(type)+"\t"+id+"\t"+rp.getNameFromID(type, id));
						}
					}
					break;
				case NEXT:
					for (int j = 0; j < oarg.size();) {
						int type = KSFLUtilities.fcc(oarg.get(j++));
						if (j < oarg.size()) {
							try {
								short id = (short)Integer.parseInt(oarg.get(j));
								System.out.println(KSFLUtilities.fccs(type)+"\t"+rp.getNextAvailableID(type, id));
								j++;
							} catch (NumberFormatException nfe) {
								System.out.println(KSFLUtilities.fccs(type)+"\t"+rp.getNextAvailableID(type));
							}
						} else {
							System.out.println(KSFLUtilities.fccs(type)+"\t"+rp.getNextAvailableID(type));
						}
					}
					break;
				case ADD:
					if (!oarg.isEmpty()) {
						byte[] stuff = null;
						try {
							RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(oarg.size()-1)), "r");
							stuff = new byte[(int)raf.length()];
							raf.readFully(stuff);
							raf.close();
						} catch (IOException e) {
							System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							stuff = null;
						}
						if (stuff != null) try {
							int type = (oarg.size() > 1) ? KSFLUtilities.fcc(oarg.get(0)) : KSFLConstants.DATA;
							short id = (oarg.size() > 2) ? (short)Integer.parseInt(oarg.get(1)) : rp.getNextAvailableID(type);
							MacResource r = new MacResource(type, id, stuff);
							if (oarg.size() > 3) r.name = oarg.get(2);
							if (oarg.size() > 4) r.setAttributeString(oarg.get(3));
							try {
								rp.add(r);
							} catch (MacResourceAlreadyExistsException e) {
								System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+id+" already exists.");
							}
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid number skipped.");
						}
					}
					break;
				case VERIFY:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						int type = KSFLUtilities.fcc(oarg.get(j));
						try {
							short id = (short)Integer.parseInt(oarg.get(j+1));
							System.out.println(rp.contains(type, id));
						} catch (NumberFormatException nfe) {
							System.out.println(rp.contains(type, oarg.get(j+1)));
						}
					}
					break;
				case EXTRACT:
					if (oarg.size() == 1) {
						File f1 = new File(oarg.get(0));
						f1.mkdir();
						for (int type : rp.getTypes()) {
							File f2 = new File(f1, KSFLUtilities.fccs(type));
							f2.mkdir();
							for (short id : rp.getIDs(type)) {
								MacResource r = rp.get(type, id);
								String n = Integer.toString(r.id);
								if (r.name != null && r.name.length() > 0) n += " " + r.name;
								try {
									FileOutputStream fos = new FileOutputStream(new File(f2, n));
									fos.write(r.data);
									fos.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
								}
								try {
									DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(f2, n+".rsrcmeta")));
									dos.writeInt(r.type);
									dos.writeShort(r.id);
									dos.writeByte(r.getAttributes());
									dos.writeUTF(r.name);
									dos.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
								}
							}
						}
					} else if (oarg.size() == 2) {
						File f1 = new File(oarg.get(1));
						f1.mkdir();
						int type = KSFLUtilities.fcc(oarg.get(0));
						for (short id : rp.getIDs(type)) {
							MacResource r = rp.get(type, id);
							String n = Integer.toString(r.id);
							if (r.name != null && r.name.length() > 0) n += " " + r.name;
							try {
								FileOutputStream fos = new FileOutputStream(new File(f1, n));
								fos.write(r.data);
								fos.close();
							} catch (IOException e) {
								System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							}
							try {
								DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(f1, n+".rsrcmeta")));
								dos.writeInt(r.type);
								dos.writeShort(r.id);
								dos.writeByte(r.getAttributes());
								dos.writeUTF(r.name);
								dos.close();
							} catch (IOException e) {
								System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							}
						}
					} else {
						for (int j = 0; j+3 <= oarg.size(); j += 3) {
							int type = KSFLUtilities.fcc(oarg.get(j));
							try {
								short id = (short)Integer.parseInt(oarg.get(j+1));
								byte[] data = rp.getData(type, id);
								if (data == null) {
									System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+id+" does not exist.");
								} else try {
									FileOutputStream fos = new FileOutputStream(new File(oarg.get(j+2)));
									fos.write(data);
									fos.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
								}
							} catch (NumberFormatException nfe) {
								byte[] data = rp.getData(type, oarg.get(j+1));
								try {
									FileOutputStream fos = new FileOutputStream(new File(oarg.get(j+2)));
									fos.write(data);
									fos.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot write ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
								}
							}
						}
					}
					break;
				case INFO:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						int type = KSFLUtilities.fcc(oarg.get(j));
						try {
							short id = (short)Integer.parseInt(oarg.get(j+1));
							MacResource r = rp.get(type, id);
							if (r == null) {
								System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+id+" does not exist.");
							} else {
								String ts = KSFLUtilities.fccs(r.type);
								String th = "00000000" + Integer.toHexString(r.type).toUpperCase();
								System.out.println("Type: " + ts + " (0x" + th.substring(th.length()-8) + ")");
								System.out.println("ID:   " + r.id);
								System.out.println("Attr: " + r.getAttributeString().toLowerCase());
								System.out.println("Name: " + r.name);
								System.out.println("Len:  " + r.data.length);
							}
						} catch (NumberFormatException nfe) {
							MacResource r = rp.get(type, oarg.get(j+1));
							if (r == null) {
								System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+oarg.get(j+1)+" does not exist.");
							} else {
								String ts = KSFLUtilities.fccs(r.type);
								String th = "00000000" + Integer.toHexString(r.type).toUpperCase();
								System.out.println("Type: " + ts + " (0x" + th.substring(th.length()-8) + ")");
								System.out.println("ID:   " + r.id);
								System.out.println("Attr: " + r.getAttributeString().toLowerCase());
								System.out.println("Name: " + r.name);
								System.out.println("Len:  " + r.data.length);
							}
						}
					}
					break;
				case CAT:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						int type = KSFLUtilities.fcc(oarg.get(j));
						try {
							short id = (short)Integer.parseInt(oarg.get(j+1));
							byte[] data = rp.getData(type, id);
							if (data == null) System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+id+" does not exist.");
							else try { System.out.write(data); } catch (IOException e) {}
						} catch (NumberFormatException nfe) {
							byte[] data = rp.getData(type, oarg.get(j+1));
							if (data == null) System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+oarg.get(j+1)+" does not exist.");
							else try { System.out.write(data); } catch (IOException e) {}
						}
					}
					break;
				case HEXCAT:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						int type = KSFLUtilities.fcc(oarg.get(j));
						try {
							short id = (short)Integer.parseInt(oarg.get(j+1));
							byte[] data = rp.getData(type, id);
							if (data == null) System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+id+" does not exist.");
							else KSFLUtilities.printHexDump(System.out, data);
						} catch (NumberFormatException nfe) {
							byte[] data = rp.getData(type, oarg.get(j+1));
							if (data == null) System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+oarg.get(j+1)+" does not exist.");
							else KSFLUtilities.printHexDump(System.out, data);
						}
					}
					break;
				case DELETE:
					for (int j = 0; j+2 <= oarg.size(); j += 2) {
						int type = KSFLUtilities.fcc(oarg.get(j));
						try {
							short id = (short)Integer.parseInt(oarg.get(j+1));
							rp.remove(type, id);
						} catch (NumberFormatException nfe) {
							rp.remove(type, oarg.get(j+1));
						}
					}
					break;
				case SETINFO:
					if (oarg.size() >= 2) {
						int type = KSFLUtilities.fcc(oarg.get(0));
						MacResource r = null;
						try {
							short id = (short)Integer.parseInt(oarg.get(1));
							r = rp.get(type, id);
						} catch (NumberFormatException nfe) {
							String name = oarg.get(1);
							r = rp.get(type, name);
						}
						if (r == null) {
							System.err.println("Error: "+KSFLUtilities.fccs(type)+" "+oarg.get(1)+" doesn't exist.");
						} else {
							short id = r.id;
							boolean changed = false;
							for (int j = 2; j+2 <= oarg.size(); j += 2) {
								String siop = oarg.get(j).trim().toLowerCase();
								if (siop.equals("t") || siop.equals("type")) {
									r.type = KSFLUtilities.fcc(oarg.get(j+1));
									changed = true;
								}
								else if (siop.equals("i") || siop.equals("id")) {
									try {
										r.id = (short)Integer.parseInt(oarg.get(j+1));
										changed = true;
									} catch (NumberFormatException nfe) {
										System.err.println("Warning: Invalid ID number "+oarg.get(j+1)+" skipped.");
									}
								}
								else if (siop.equals("n") || siop.equals("name")) {
									r.name = oarg.get(j+1);
									changed = true;
								}
								else if (siop.equals("a") || siop.equals("attr") || siop.equals("attrib") || siop.equals("attributes")) {
									r.setAttributeString(oarg.get(j+1));
									changed = true;
								}
								else {
									System.err.println("Unknown attribute type: "+oarg.get(j));
									System.err.println("Should be one of: t[ype] i[d] n[ame] a[ttr[ib[utes]]]");
								}
							}
							if (changed) {
								try {
									rp.setAttributes(type, id, r);
								} catch (MacResourceAlreadyExistsException e) {
									System.err.println("Error: "+KSFLUtilities.fccs(r.type)+" "+r.id+" already exists.");
								}
							}
						}
					}
					break;
				case TRUNCATE:
					for (int j = 0; j+3 <= oarg.size(); j += 3) {
						try {
							int len = Integer.parseInt(oarg.get(j+2));
							int type = KSFLUtilities.fcc(oarg.get(j));
							try {
								short id = (short)Integer.parseInt(oarg.get(j+1));
								if (rp.contains(type, id)) {
									rp.setData(type, id, KSFLUtilities.resize(rp.getData(type, id), len));
								} else {
									MacResource r = new MacResource(type, id, new byte[len]);
									try { rp.add(r); } catch (MacResourceAlreadyExistsException ignored) {}
								}
							} catch (NumberFormatException nfe) {
								String name = oarg.get(j+1);
								if (rp.contains(type, name)) {
									rp.setData(type, name, KSFLUtilities.resize(rp.getData(type, name), len));
								} else {
									MacResource r = new MacResource(type, rp.getNextAvailableID(type), name, new byte[len]);
									try { rp.add(r); } catch (MacResourceAlreadyExistsException ignored) {}
								}
							}
						} catch (NumberFormatException nfe) {
							System.err.println("Error: Invalid length "+oarg.get(j+2)+" skipped.");
						}
					}
					break;
				case FRESHEN:
					for (int j = 0; j+3 <= oarg.size(); j += 3) {
						int type = KSFLUtilities.fcc(oarg.get(j));
						try {
							short id = (short)Integer.parseInt(oarg.get(j+1));
							if (rp.contains(type, id)) {
								byte[] stuff = null;
								try {
									RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(j+2)), "r");
									stuff = new byte[(int)raf.length()];
									raf.readFully(stuff);
									raf.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
									stuff = null;
								}
								if (stuff != null) {
									rp.setData(type, id, stuff);
								}
							}
						} catch (NumberFormatException nfe) {
							String name = oarg.get(j+1);
							if (rp.contains(type, name)) {
								byte[] stuff = null;
								try {
									RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(j+2)), "r");
									stuff = new byte[(int)raf.length()];
									raf.readFully(stuff);
									raf.close();
								} catch (IOException e) {
									System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
									stuff = null;
								}
								if (stuff != null) {
									rp.setData(type, name, stuff);
								}
							}
						}
					}
					break;
				case UPDATE:
					for (int j = 0; j+3 <= oarg.size(); j += 3) {
						byte[] stuff = null;
						try {
							RandomAccessFile raf = new RandomAccessFile(new File(oarg.get(j+2)), "r");
							stuff = new byte[(int)raf.length()];
							raf.readFully(stuff);
							raf.close();
						} catch (IOException e) {
							System.err.println("Error: Cannot read ("+e.getClass().getSimpleName()+": "+e.getMessage()+")");
							stuff = null;
						}
						if (stuff != null) {
							int type = KSFLUtilities.fcc(oarg.get(j));
							try {
								short id = (short)Integer.parseInt(oarg.get(j+1));
								if (rp.contains(type, id)) {
									rp.setData(type, id, stuff);
								} else {
									MacResource r = new MacResource(type, id, stuff);
									try { rp.add(r); } catch (MacResourceAlreadyExistsException ignored) {}
								}
							} catch (NumberFormatException nfe) {
								String name = oarg.get(j+1);
								if (rp.contains(type, name)) {
									rp.setData(type, name, stuff);
								} else {
									MacResource r = new MacResource(type, rp.getNextAvailableID(type), name, stuff);
									try { rp.add(r); } catch (MacResourceAlreadyExistsException ignored) {}
								}
							}
						}
					}
					break;
				default:
					System.err.println("Internal Error: Unimplemented Operation "+op.name());
					break;
				}
			}
		}
		if (rp != null) {
			rp.close();
		}
	}
	
	private static void explain(Format fmt) {
		switch (fmt) {
		case IFF: explainChunk(ChunkFileSpec.CFSPEC_IFF); break;
		case RIFF: explainChunk(ChunkFileSpec.CFSPEC_RIFF); break;
		case MIDI: explainChunk(ChunkFileSpec.CFSPEC_MIDI); break;
		case PNG: explainChunk(ChunkFileSpec.CFSPEC_PNG); break;
		case ICNS: explainChunk(ChunkFileSpec.CFSPEC_ICNS); break;
		case HYPERCARD: explainChunk(ChunkFileSpec.CFSPEC_HYPERCARD); break;
		case DFFAUTO: explainDFF(3, false); break;
		case DFF1BE: explainDFF(1, false); break;
		case DFF1LE: explainDFF(1, true); break;
		case DFF2BE: explainDFF(2, false); break;
		case DFF2LE: explainDFF(2, true); break;
		case DFF3BE: explainDFF(3, false); break;
		case DFF3LE: explainDFF(3, true); break;
		case WINPE: explainWinPE(); break;
		case PRC: explainPRC(); break;
		case RSRC: explainRSRC(); break;
		default: System.err.println("Internal Error: Unimplemented Format "+fmt.name()); break;
		}
	}
	
	private static void explainChunk(ChunkFileSpec cfs) {
		System.out.println("KSFL Chunk Format Specification: "+cfs.stringRepresentation());
		System.out.println();
		if (cfs.fileHeaderSpec() != null) {
			for (FieldSpec fs : cfs.fileHeaderSpec()) {
				switch (fs.size()) {
				case BYTE: System.out.print(fs.littleEndian() ? "int8le" : "int8be"); break;
				case SHORT: System.out.print(fs.littleEndian() ? "int16le" : "int16be"); break;
				case MEDIUM: System.out.print(fs.littleEndian() ? "int32le" : "int32be"); break;
				case LONG: System.out.print(fs.littleEndian() ? "int64le" : "int64be"); break;
				case DATA: System.out.print("byte[]"); break;
				}
				switch (fs.type()) {
				case FILLER: System.out.println(" filler;"); break;
				case SIZE_WITHOUT_HEADER: System.out.println(" size; // excluding header"); break;
				case SIZE_WITH_HEADER: System.out.println(" size; // including header"); break;
				case CHUNK_COUNT: System.out.println(" count;"); break;
				case INTEGER_TYPE: System.out.println(" type;"); break;
				case CHARACTER_TYPE: System.out.println(" type; // character constant"); break;
				case ID_NUMBER: System.out.println(" id;"); break;
				case CHECKSUM: System.out.println(" checksum;"); break;
				case DATA: System.out.println(" data;"); break;
				}
			}
			if (cfs.fileHeaderSpec().evenPadded()) System.out.println("short align");
		}
		System.out.println("chunk[] chunks;");
		if (cfs.chunkHeaderSpec() != null || cfs.chunkHeaderSpec().isEmpty()) {
			for (FieldSpec fs : cfs.chunkHeaderSpec()) {
				switch (fs.size()) {
				case BYTE: System.out.print(fs.littleEndian() ? "\tint8le" : "\tint8be"); break;
				case SHORT: System.out.print(fs.littleEndian() ? "\tint16le" : "\tint16be"); break;
				case MEDIUM: System.out.print(fs.littleEndian() ? "\tint32le" : "\tint32be"); break;
				case LONG: System.out.print(fs.littleEndian() ? "\tint64le" : "\tint64be"); break;
				case DATA: System.out.print("\tbyte[]"); break;
				}
				switch (fs.type()) {
				case FILLER: System.out.println(" filler;"); break;
				case SIZE_WITHOUT_HEADER: System.out.println(" size; // excluding header"); break;
				case SIZE_WITH_HEADER: System.out.println(" size; // including header"); break;
				case CHUNK_COUNT: System.out.println(" count;"); break;
				case INTEGER_TYPE: System.out.println(" type;"); break;
				case CHARACTER_TYPE: System.out.println(" type; // character constant"); break;
				case ID_NUMBER: System.out.println(" id;"); break;
				case CHECKSUM: System.out.println(" checksum;"); break;
				case DATA: System.out.println(" data;"); break;
				}
			}
			if (cfs.chunkHeaderSpec().evenPadded()) System.out.println("\tshort align");
		}
	}
	
	private static void explainDFF(int version, boolean le) {
		switch (version) {
		case 1:
			System.out.println("KSFL Chunk Format Specification: "+(le ? ChunkFileSpec.CFSPEC_DFF1LE : ChunkFileSpec.CFSPEC_DFF1BE).stringRepresentation());
			System.out.println("DFF File Format version 1.0");
			System.out.println();
			System.out.println("int32"+(le?"le":"be")+" magic; // '%DFF'");
			System.out.println("resource[] resources;");
			System.out.println("\tint64"+(le?"le":"be")+" type; // character constant");
			System.out.println("\tint16"+(le?"le":"be")+" id;");
			System.out.println("\tint16"+(le?"le":"be")+" filler;");
			System.out.println("\tint32"+(le?"le":"be")+" size; // excluding header");
			System.out.println("\tbyte[] data;");
			break;
		case 2:
			System.out.println("DFF File Format version 2.0.1");
			System.out.println();
			System.out.println("int32"+(le?"le":"be")+" magic; // '%DF2'");
			System.out.println("int32"+(le?"le":"be")+" count;");
			System.out.println("header[] headers;");
			System.out.println("\tint64"+(le?"le":"be")+" type; // character constant");
			System.out.println("\tint16"+(le?"le":"be")+" id; // least significant 16 bits");
			System.out.println("\tint16"+(le?"le":"be")+" dataType; // app use; values listed here are deprecated");
			System.out.println("\t\t0 - binary");
			System.out.println("\t\t1 - plain text");
			System.out.println("\t\t2 - Mac OS text");
			System.out.println("\t\t3 - KTF text");
			System.out.println("\t\t4 - image");
			System.out.println("\t\t5 - KIF image");
			System.out.println("\t\t6 - audio");
			System.out.println("\t\t7 - Pascal string list");
			System.out.println("\t\t8 - word string list");
			System.out.println("\t\t9 - long string list");
			System.out.println("\t\t10 - C string list");
			System.out.println("\tint16"+(le?"le":"be")+" attributes;");
			System.out.println("\t\tbit 0 - readonly");
			System.out.println("\t\tbit 1 - system");
			System.out.println("\t\tbit 2 - preload");
			System.out.println("\t\tbit 3 - purgeable");
			System.out.println("\t\tbit 4 - fromfile");
			System.out.println("\t\tbit 5 - fromrsrc");
			System.out.println("\t\tbit 6 - invisible");
			System.out.println("\t\tbit 7 - disabled");
			System.out.println("\t\tbit 8 - protected");
			System.out.println("\t\tbit 9 - fixed");
			System.out.println("\t\tbit 10 - multilingual");
			System.out.println("\t\tbit 11 - compressed");
			System.out.println("\t\tbit 12 - appuse1");
			System.out.println("\t\tbit 13 - appuse2");
			System.out.println("\t\tbit 14 - appuse3");
			System.out.println("\t\tbit 15 - appuse4");
			System.out.println("\tint16"+(le?"le":"be")+" id; // most significant 16 bits");
			System.out.println("\tint32"+(le?"le":"be")+" size; // excluding header");
			System.out.println("name[] names;");
			System.out.println("\tint8"+(le?"le":"be")+" length;");
			System.out.println("\tbyte[] name;");
			System.out.println("data[] datas;");
			System.out.println("\tbyte[] data;");
			break;
		case 3:
			System.out.println("DFF File Format version 3.0");
			System.out.println();
			System.out.println("int32"+(le?"le":"be")+" magic; // '%DF3'");
			System.out.println("int32"+(le?"le":"be")+" typeCount;");
			System.out.println("type[] types;");
			System.out.println("\tint64"+(le?"le":"be")+" type; // character constant");
			System.out.println("\tint32"+(le?"le":"be")+" resourceCount;");
			System.out.println("\tint32"+(le?"le":"be")+" resourceTableOffset; // from beginning of file");
			System.out.println("resourceTable[] resourceTables;");
			System.out.println("\tresource[] resources;");
			System.out.println("\t\tint32"+(le?"le":"be")+" id;");
			System.out.println("\t\tint16"+(le?"le":"be")+" dataType; // app use; values listed here are deprecated");
			System.out.println("\t\t\t0 - binary");
			System.out.println("\t\t\t1 - plain text");
			System.out.println("\t\t\t2 - Mac OS text");
			System.out.println("\t\t\t3 - KTF text");
			System.out.println("\t\t\t4 - image");
			System.out.println("\t\t\t5 - KIF image");
			System.out.println("\t\t\t6 - audio");
			System.out.println("\t\t\t7 - Pascal string list");
			System.out.println("\t\t\t8 - word string list");
			System.out.println("\t\t\t9 - long string list");
			System.out.println("\t\t\t10 - C string list");
			System.out.println("\t\tint16"+(le?"le":"be")+" attributes;");
			System.out.println("\t\t\tbit 0 - readonly");
			System.out.println("\t\t\tbit 1 - system");
			System.out.println("\t\t\tbit 2 - preload");
			System.out.println("\t\t\tbit 3 - purgeable");
			System.out.println("\t\t\tbit 4 - fromfile");
			System.out.println("\t\t\tbit 5 - fromrsrc");
			System.out.println("\t\t\tbit 6 - invisible");
			System.out.println("\t\t\tbit 7 - disabled");
			System.out.println("\t\t\tbit 8 - protected");
			System.out.println("\t\t\tbit 9 - fixed");
			System.out.println("\t\t\tbit 10 - multilingual");
			System.out.println("\t\t\tbit 11 - compressed");
			System.out.println("\t\t\tbit 12 - appuse1");
			System.out.println("\t\t\tbit 13 - appuse2");
			System.out.println("\t\t\tbit 14 - appuse3");
			System.out.println("\t\t\tbit 15 - appuse4");
			System.out.println("\t\tint32"+(le?"le":"be")+" nameOffset; // from beginning of file");
			System.out.println("\t\tint48"+(le?"le":"be")+" dataOffset; // from beginning of file");
			System.out.println("\t\tint48"+(le?"le":"be")+" size;");
			System.out.println("name[] names;");
			System.out.println("\tint8"+(le?"le":"be")+" length;");
			System.out.println("\tbyte[] name;");
			System.out.println("data[] datas;");
			System.out.println("\tbyte[] data;");
			break;
		}
	}
	
	private static void explainWinPE() {
		System.out.println("Windows Portable Executable");
		System.out.println();
		System.out.println("byte[128] msdosStub;");
		System.out.println("int32le peSignature; // 0x4550");
		System.out.println("int16le machine;");
		System.out.println("int16le sectionCount;");
		System.out.println("int32le creationDate; // epoch: January 1, 1970");
		System.out.println("int32le symbolTablePtr;");
		System.out.println("int32le symbolCount;");
		System.out.println("int16le optionalHeaderSize; // usually 0xE0");
		System.out.println("int16le characteristics;");
		System.out.println("int16le magic; // usually 0x010B");
		System.out.println("int16le linkerVersion;");
		System.out.println("int32le codeSize;");
		System.out.println("int32le dataSize;");
		System.out.println("int32le bssSize;");
		System.out.println("int32le entryPointOffset;");
		System.out.println("int32le codeOffset;");
		System.out.println("int32le dataOffset;");
		System.out.println("int32le loadAddress;");
		System.out.println("int32le sectionAlignment;");
		System.out.println("int32le fileAlignment;");
		System.out.println("int32le osVersion; // usually 0x40000 (Win32)");
		System.out.println("int32le binaryVersion;");
		System.out.println("int32le subsystemVersion; // usually 0x40000 (Win32)");
		System.out.println("int32le win32Version; // usually 0");
		System.out.println("int32le imageSize;");
		System.out.println("int32le headerSize;");
		System.out.println("int32le checksum;");
		System.out.println("int16le subsystem;");
		System.out.println("int16le dllCharacteristics;");
		System.out.println("int32le stackReserveSize;");
		System.out.println("int32le stackCommitSize;");
		System.out.println("int32le heapReserveSize;");
		System.out.println("int32le heapCommitSize;");
		System.out.println("int32le loaderFlags;");
		System.out.println("int32le directoryEntryCount;");
		System.out.println("directoryEntry[] directoryEntries;");
		System.out.println("\tint32le virtualAddress;");
		System.out.println("\tint32le size;");
		System.out.println("sectionHeader[] sectionHeaders;");
		System.out.println("\tbyte[8] name;");
		System.out.println("\tint32le virtualSize;");
		System.out.println("\tint32le virtualAddress;");
		System.out.println("\tint32le dataSize;");
		System.out.println("\tint32le dataOffset;");
		System.out.println("\tint32le relocationsOffset;");
		System.out.println("\tint32le lineNumbersOffset;");
		System.out.println("\tint16le relocationCount;");
		System.out.println("\tint16le lineNumberCount;");
		System.out.println("\tint32le characteristics;");
	}
	
	private static void explainPRC() {
		System.out.println("Palm Resource Database");
		System.out.println();
		System.out.println("byte[32] name;");
		System.out.println("int16be attributes;");
		System.out.println("\tbit 0 - resource database");
		System.out.println("\tbit 1 - read only");
		System.out.println("\tbit 2 - appinfo dirty");
		System.out.println("\tbit 3 - backup");
		System.out.println("\tbit 4 - OK to install newer");
		System.out.println("\tbit 5 - reset after install");
		System.out.println("\tbit 6 - non-beamable (copy-protected)");
		System.out.println("\tbit 7 - file stream database");
		System.out.println("\tbit 8 - hidden");
		System.out.println("\tbit 9 - launchable data");
		System.out.println("\tbit 10 - reserved");
		System.out.println("\tbit 11 - reserved");
		System.out.println("\tbit 12 - reserved");
		System.out.println("\tbit 13 - reserved");
		System.out.println("\tbit 14 - reserved");
		System.out.println("\tbit 15 - open");
		System.out.println("int16be version; // usually 1");
		System.out.println("int32be creationDate; // epoch: January 1, 1904");
		System.out.println("int32be modificationDate; // epoch: January 1, 1904");
		System.out.println("int32be backupDate; // epoch: January 1, 1904");
		System.out.println("int32be modificationNumber; // usually 0");
		System.out.println("int32be appInfoOffset; // usually 0");
		System.out.println("int32be sortInfoOffset; // usually 0");
		System.out.println("int32be type; // for PRC, 'appl'");
		System.out.println("int32be creator;");
		System.out.println("int32be uniqueIDSeed; // usually 0");
		System.out.println("recordList[] recordLists;");
		System.out.println("\tint32be nextRecordListOffset; // usually 0 (only one record list)");
		System.out.println("\tint16be resourceCount;");
		System.out.println("\tresource[] resources; // if no resources, just a single int32be of 0");
		System.out.println("\t\tint32be type; // character constant");
		System.out.println("\t\tint16be id;");
		System.out.println("\t\tint32be dataOffset;");
		System.out.println("byte[] appInfo; // usually empty");
		System.out.println("byte[] sortInfo; // usually empty");
		System.out.println("recordData[] recordDatas;");
		System.out.println("\tbyte[] data;");
	}
	
	private static void explainRSRC() {
		System.out.println("Mac OS Resource Fork");
		System.out.println();
		System.out.println("Beginning of File:");
		System.out.println("int32be resourceDataOffset; // from beginning of file");
		System.out.println("int32be resourceMapOffset; // from beginning of file");
		System.out.println("int32be resourceDataSize;");
		System.out.println("int32be resourceMapSize;");
		System.out.println();
		System.out.println("Resource Data:");
		System.out.println("data[] resourceData;");
		System.out.println("\tint32be size;");
		System.out.println("\tbyte[] data;");
		System.out.println();
		System.out.println("Resource Map:");
		System.out.println("int32be resourceDataOffset; // from beginning of file");
		System.out.println("int32be resourceMapOffset; // from beginning of file");
		System.out.println("int32be resourceDataSize;");
		System.out.println("int32be resourceMapSize;");
		System.out.println("int32be nextResourceMap;");
		System.out.println("int16be fileRef;");
		System.out.println("int16be attributes;");
		System.out.println("\tbit 5 - changed");
		System.out.println("\tbit 6 - needs compression");
		System.out.println("\tbit 7 - readonly");
		System.out.println("int16be typeListOffset; // from beginning of map");
		System.out.println("int16be nameListOffset; // from beginning of map");
		System.out.println();
		System.out.println("Type List:");
		System.out.println("int16be typeCount; // number of types minus one");
		System.out.println("type[] types;");
		System.out.println("\tint32be type; // character constant");
		System.out.println("\tint16be resourceCount; // number of resources minus one");
		System.out.println("\tint16be resourceListOffset; // from beginning of type list");
		System.out.println();
		System.out.println("Resource List:");
		System.out.println("resource[] resourceList;");
		System.out.println("\tint16be id;");
		System.out.println("\tint16be nameOffset; // from beginning of name list");
		System.out.println("\tint8be attributes;");
		System.out.println("\t\tbit 0 - compressed");
		System.out.println("\t\tbit 1 - changed");
		System.out.println("\t\tbit 2 - preload");
		System.out.println("\t\tbit 3 - protected (readonly)");
		System.out.println("\t\tbit 4 - locked (fixed)");
		System.out.println("\t\tbit 5 - purgeable");
		System.out.println("\t\tbit 6 - system");
		System.out.println("\t\tbit 7 - reserved");
		System.out.println("\tint24be dataOffset; // from beginning of resource data");
		System.out.println("\tint32be resourcePtr;");
		System.out.println();
		System.out.println("Name List:");
		System.out.println("name[] names;");
		System.out.println("\tint8be length;");
		System.out.println("\tbyte[] name;");
	}
}
