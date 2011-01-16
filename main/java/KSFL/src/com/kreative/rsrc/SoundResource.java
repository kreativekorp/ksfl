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

import java.io.*;
import com.kreative.ksfl.*;
import com.kreative.rsrc.misc.MACEDecoder;

/**
 * The <code>SoundResource</code> class represents a Mac OS sound resource.
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class SoundResource extends MacResource {
	/**
	 * The resource type of a Mac OS sound resource,
	 * the four-character constant <code>snd </code>.
	 */
	public static final int RESOURCE_TYPE = KSFLConstants.snd;
	
	public static final int SQUAREWAVESYNTH	= (short)0x0001;
	public static final int WAVETABLESYNTH	= (short)0x0003;
	public static final int SAMPLEDSYNTH	= (short)0x0005;
	
	public static final int INITCHANLEFT	= (int)0x0002;
	public static final int INITCHANRIGHT	= (int)0x0003;
	public static final int WAVECHANNEL0	= (int)0x0004;
	public static final int WAVECHANNEL1	= (int)0x0005;
	public static final int WAVECHANNEL2	= (int)0x0006;
	public static final int WAVECHANNEL3	= (int)0x0007;
	public static final int INITMONO		= (int)0x0080;
	public static final int INITSTEREO		= (int)0x00C0;
	public static final int INITMACE3		= (int)0x0300;
	public static final int INITMACE6		= (int)0x0400;
	public static final int INITNOINTERP	= (int)0x0004;
	public static final int INITNODROP		= (int)0x0008;
	public static final int INITPANMASK		= (int)0x0003;
	public static final int INITSRATEMASK	= (int)0x0030;
	public static final int INITSTEREOMASK	= (int)0x00C0;
	public static final int INITCOMPMASK	= (int)0xFF00;
	
	public static final int NULLCMD			= (short)0x0000;
	public static final int QUIETCMD		= (short)0x0003;
	public static final int FLUSHCMD		= (short)0x0004;
	public static final int REINITCMD		= (short)0x0005;
	public static final int WAITCMD     	= (short)0x000A;
	public static final int PAUSECMD		= (short)0x000B;
	public static final int RESUMECMD		= (short)0x000C;
	public static final int CALLBACKCMD		= (short)0x000D;
	public static final int SYNCCMD			= (short)0x000E;
	public static final int AVAILABLECMD	= (short)0x0018;
	public static final int VERSIONCMD		= (short)0x0019;
	public static final int TOTALLOADCMD	= (short)0x001A;
	public static final int LOADCMD			= (short)0x001B;
	public static final int FREQDURATIONCMD	= (short)0x0028;
	public static final int RESTCMD			= (short)0x0029;
	public static final int FREQCMD     	= (short)0x002A;
	public static final int AMPCMD			= (short)0x002B;
	public static final int TIMBRECMD		= (short)0x002C;
	public static final int GETAMPCMD		= (short)0x002D;
	public static final int VOLUMECMD		= (short)0x002E;
	public static final int GETVOLUMECMD	= (short)0x002F;
	public static final int WAVETABLECMD	= (short)0x003C;
	public static final int SOUNDCMD		= (short)0x0050;
	public static final int BUFFERCMD		= (short)0x0051;
	public static final int RATECMD			= (short)0x0052;
	public static final int GETRATECMD		= (short)0x0055;
	public static final int DATAOFFSETFLAG	= (short)0x8000;
	
	public static final int RATE_44KHZ		= (int)0xAC440000;
	public static final int RATE_22KHZ		= (int)0x56EE8BA3;
	public static final int RATE_22050KHZ	= (int)0x56220000;
	public static final int RATE_11KHZ		= (int)0x2B7745D1;
	public static final int RATE_11025KHZ	= (int)0x2B110000;
	
	public static final int STDSH = (byte)0x00;
	public static final int EXTSH = (byte)0xFF;
	public static final int CMPSH = (byte)0xFE;
	
	private static final int RIFF = KSFLUtilities.fcc("RIFF");
	private static final int WAVE = KSFLUtilities.fcc("WAVE");
	private static final int FMT  = KSFLUtilities.fcc("fmt ");
	private static final int DATA = KSFLUtilities.fcc("data");
	private static final int CYNH = KSFLUtilities.fcc("Cynh");

	private static final int FORM = KSFLUtilities.fcc("FORM");
	private static final int AIFC = KSFLUtilities.fcc("AIFC");
	private static final int FVER = KSFLUtilities.fcc("FVER");
	private static final int COMM = KSFLUtilities.fcc("COMM");
	private static final int SSND = KSFLUtilities.fcc("SSND");

	private static final short EXPONENT_44KHZ    = 0x400E;
	private static final long  MANTISSA_44KHZ    = 0xAC44000000000000L;
	private static final short EXPONENT_22KHZ    = 0x400D;
	private static final long  MANTISSA_22KHZ    = 0xADDD1745D145826BL;
	private static final short EXPONENT_22050KHZ = 0x400D;
	private static final long  MANTISSA_22050KHZ = 0xAC44000000000000L;
	private static final short EXPONENT_11KHZ    = 0x400C;
	private static final long  MANTISSA_11KHZ    = 0xADDD1745D145826BL;
	private static final short EXPONENT_11025KHZ = 0x400C;
	private static final long  MANTISSA_11025KHZ = 0xAC44000000000000L;
	
	private static final short COMPID_NONE       = 0;
	private static final short COMPID_ACE_2TO1   = 1;
	private static final short COMPID_ACE_8TO3   = 2;
	private static final short COMPID_MACE_3TO1  = 3;
	private static final short COMPID_MACE_6TO1  = 4;
	private static final short COMPID_USE_FORMAT = -1;
	
	private static final int FORMAT_NONE      = 0x4E4F4E45; // KSFLUtilities.fcc("NONE");
	private static final int FORMAT_ACE_2TO1  = 0x41434532; // KSFLUtilities.fcc("ACE2");
	private static final int FORMAT_ACE_8TO3  = 0x41434538; // KSFLUtilities.fcc("ACE8");
	private static final int FORMAT_MACE_3TO1 = 0x4D414333; // KSFLUtilities.fcc("MAC3");
	private static final int FORMAT_MACE_6TO1 = 0x4D414336; // KSFLUtilities.fcc("MAC6");
	
	private static final byte[] COMPNAME_NONE      = {0x0E,'n','o','t',' ','c','o','m','p','r','e','s','s','e','d', 0 };
	private static final byte[] COMPNAME_ACE_2TO1  = {0x0A,'A','C','E',' ','2','-','t','o','-','1', 0 , 0 , 0 , 0 , 0 };
	private static final byte[] COMPNAME_ACE_8TO3  = {0x0A,'A','C','E',' ','8','-','t','o','-','3', 0 , 0 , 0 , 0 , 0 };
	private static final byte[] COMPNAME_MACE_3TO1 = {0x0B,'M','A','C','E',' ','3','-','t','o','-','1', 0 , 0 , 0 , 0 };
	private static final byte[] COMPNAME_MACE_6TO1 = {0x0B,'M','A','C','E',' ','6','-','t','o','-','1', 0 , 0 , 0 , 0 };
	
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
	 * Constructs a new resource of type <code>snd </code> with the specified ID and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public SoundResource(short id, byte[] data) {
		super(RESOURCE_TYPE, id, data);
	}
	
	/**
	 * Constructs a new resource of type <code>snd </code> with the specified ID, name, and data.
	 * All attributes are cleared.
	 * @param id The resource ID.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public SoundResource(short id, String name, byte[] data) {
		super(RESOURCE_TYPE, id, name, data);
	}
	
	/**
	 * Constructs a new resource of type <code>snd </code> with the specified ID, attributes, and data.
	 * The name is set to an empty string.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param data The resource data.
	 */
	public SoundResource(short id, byte attr, byte[] data) {
		super(RESOURCE_TYPE, id, attr, data);
	}
	
	/**
	 * Constructs a new resource of type <code>snd </code> with the specified ID, attributes, name, and data.
	 * @param id The resource ID.
	 * @param attr The resource attributes as a byte.
	 * @param name The resource name.
	 * @param data The resource data.
	 */
	public SoundResource(short id, byte attr, String name, byte[] data) {
		super(RESOURCE_TYPE, id, attr, name, data);
	}
	
	/**
	 * Constructs a new resource with the specified type, ID, and data.
	 * The name is set to an empty string, and all attributes are cleared.
	 * @param type The resource type as an integer.
	 * @param id The resource ID.
	 * @param data The resource data.
	 */
	public SoundResource(int type, short id, byte[] data) {
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
	public SoundResource(int type, short id, String name, byte[] data) {
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
	public SoundResource(int type, short id, byte attr, byte[] data) {
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
	public SoundResource(int type, short id, byte attr, String name, byte[] data) {
		super(type, id, attr, name, data);
	}
	
	/**
	 * Returns the format of the sound.
	 * @return the format of the sound.
	 */
	public int getFormat() {
		return KSFLUtilities.getShort(data, 0);
	}
	
	/**
	 * Returns the reference constant for a type 2 sound.
	 * @return the reference constant for a type 2 sound.
	 */
	public int getRefCon() {
		switch (KSFLUtilities.getShort(data, 0)) {
		case 2:
			return KSFLUtilities.getShort(data, 2);
		default:
			return 0;
		}
	}
	
	/**
	 * Returns the number of data formats for a type 1 sound.
	 * @return the number of data formats for a type 1 sound.
	 */
	public int getDataFormatCount() {
		switch (KSFLUtilities.getShort(data, 0)) {
		case 1:
			return KSFLUtilities.getShort(data, 2);
		default:
			return 0;
		}
	}
	
	/**
	 * Returns the data format ID for a type 1 sound.
	 * @param index the index of the data format.
	 * @return the data format ID.
	 */
	public int getDataFormatID(int index) {
		switch (KSFLUtilities.getShort(data, 0)) {
		case 1:
			return KSFLUtilities.getShort(data, 4 + 6*index);
		default:
			return 0;
		}
	}
	
	/**
	 * Returns the data format init option for a type 1 sound.
	 * @param index the index of the data format.
	 * @return the data format init option.
	 */
	public int getDataFormatInitOption(int index) {
		switch (KSFLUtilities.getShort(data, 0)) {
		case 1:
			return KSFLUtilities.getInt(data, 6 + 6*index);
		default:
			return 0;
		}
	}
	
	/**
	 * Returns the number of sound commands.
	 * @return the number of sound commands.
	 */
	public int getCommandCount() {
		switch (KSFLUtilities.getShort(data, 0)) {
		case 1:
			return KSFLUtilities.getShort(data, 4 + 6*KSFLUtilities.getShort(data, 2));
		case 2:
			return KSFLUtilities.getShort(data, 4);
		default:
			return 0;
		}
	}
	
	/**
	 * Returns the sound command.
	 * @param index the index of the sound command.
	 * @return the sound command.
	 */
	public int getCommand(int index) {
		switch (KSFLUtilities.getShort(data, 0)) {
		case 1:
			return KSFLUtilities.getShort(data, 6 + 6*KSFLUtilities.getShort(data, 2) + 8*index);
		case 2:
			return KSFLUtilities.getShort(data, 6 + 8*index);
		default:
			return 0;
		}
	}
	
	/**
	 * Returns the sound command first parameter.
	 * @param index the index of the sound command.
	 * @return the sound command first parameter.
	 */
	public int getCommandParam1(int index) {
		switch (KSFLUtilities.getShort(data, 0)) {
		case 1:
			return KSFLUtilities.getShort(data, 8 + 6*KSFLUtilities.getShort(data, 2) + 8*index);
		case 2:
			return KSFLUtilities.getShort(data, 8 + 8*index);
		default:
			return 0;
		}
	}
	
	/**
	 * Returns the sound command second parameter.
	 * @param index the index of the sound command.
	 * @return the sound command second parameter.
	 */
	public int getCommandParam2(int index) {
		switch (KSFLUtilities.getShort(data, 0)) {
		case 1:
			return KSFLUtilities.getShort(data, 10 + 6*KSFLUtilities.getShort(data, 2) + 8*index);
		case 2:
			return KSFLUtilities.getShort(data, 10 + 8*index);
		default:
			return 0;
		}
	}
	
	/**
	 * Returns the offset where sound data begins.
	 * @return the offset where sound data begins.
	 */
	public int getSoundDataOffset() {
		switch (KSFLUtilities.getShort(data, 0)) {
		case 1:
			return 6 + 6*KSFLUtilities.getShort(data, 2) + 8*KSFLUtilities.getShort(data, 4 + 6*KSFLUtilities.getShort(data, 2));
		case 2:
			return 6 + 8*KSFLUtilities.getShort(data, 4);
		default:
			return 2;
		}
	}
	
	/**
	 * Returns the name of the codec used to compress the sound data.
	 * If the codec is unrecognized, returns null.
	 * @return the name of the codec used to compress the sound data.
	 */
	public String getCodecName() {
		if (getFormat() < 1 || getFormat() > 2) return null;
		for (int i = 0; i < getCommandCount(); i++) {
			int cmd = getCommand(i) & ~DATAOFFSETFLAG;
			if (!(cmd == NULLCMD || cmd == SOUNDCMD || cmd == BUFFERCMD)) return null;
		}
		int o = getSoundDataOffset();
		int encoding = data[o+20];
		if (encoding == -2) {
			int format = KSFLUtilities.getInt(data, o+40);
			short compressionID = KSFLUtilities.getShort(data, o+56);
			switch (compressionID) {
			case COMPID_NONE: return new String(COMPNAME_NONE).trim();
			case COMPID_ACE_2TO1: return new String(COMPNAME_ACE_2TO1).trim();
			case COMPID_ACE_8TO3: return new String(COMPNAME_ACE_8TO3).trim();
			case COMPID_MACE_3TO1: return new String(COMPNAME_MACE_3TO1).trim();
			case COMPID_MACE_6TO1: return new String(COMPNAME_MACE_6TO1).trim();
			case COMPID_USE_FORMAT:
				switch (format) {
				case FORMAT_NONE: return new String(COMPNAME_NONE).trim();
				case FORMAT_ACE_2TO1: return new String(COMPNAME_ACE_2TO1).trim();
				case FORMAT_ACE_8TO3: return new String(COMPNAME_ACE_8TO3).trim();
				case FORMAT_MACE_3TO1: return new String(COMPNAME_MACE_3TO1).trim();
				case FORMAT_MACE_6TO1: return new String(COMPNAME_MACE_6TO1).trim();
				default: return null;
				}
			default: return null;
			}
		} else if (encoding == -1 || encoding == 0) {
			return new String(COMPNAME_NONE).trim();
		} else {
			return null;
		}
	}
	
	/**
	 * Converts this sound resource to the WAV format, if possible, and returns the WAV data.
	 * If the sound cannot be converted, returns null.
	 * @return WAV data.
	 */
	public byte[] toWav() {
		if (getFormat() < 1 || getFormat() > 2) return null;
		for (int i = 0; i < getCommandCount(); i++) {
			int cmd = getCommand(i) & ~DATAOFFSETFLAG;
			if (!(cmd == NULLCMD || cmd == SOUNDCMD || cmd == BUFFERCMD)) return null;
		}
		int o = getSoundDataOffset();
		int numBytes = KSFLUtilities.getInt(data, o+4);
		int padding = 0; while (((numBytes+padding)&3)!=0) padding++;
		int sampleRate = KSFLUtilities.getInt(data, o+8);
		int loopStart = KSFLUtilities.getInt(data, o+12);
		int loopEnd = KSFLUtilities.getInt(data, o+16);
		int encoding = data[o+20];
		int baseFrequency = data[o+21];
		if (encoding == -2) {
			// compressed
			int numFrames = KSFLUtilities.getInt(data, o+22);
			int format = KSFLUtilities.getInt(data, o+40);
			short compressionID = KSFLUtilities.getShort(data, o+56);
			short sampleSize = KSFLUtilities.getShort(data, o+62);
			int sampleBytes = (sampleSize + 7) / 8;
			byte[] newdata = KSFLUtilities.copy(data, o+22, numBytes * numFrames * sampleBytes);
			switch (compressionID) {
			case COMPID_NONE:
				// nothin' doin'
				break;
			case COMPID_MACE_3TO1:
				newdata = MACEDecoder.decompressMACE3(newdata, numBytes);
				break;
			case COMPID_MACE_6TO1:
				newdata = MACEDecoder.decompressMACE6(newdata, numBytes);
				break;
			case COMPID_USE_FORMAT:
				switch (format) {
				case FORMAT_NONE:
					// nothin' doin'
					break;
				case FORMAT_MACE_3TO1:
					newdata = MACEDecoder.decompressMACE3(newdata, numBytes);
					break;
				case FORMAT_MACE_6TO1:
					newdata = MACEDecoder.decompressMACE6(newdata, numBytes);
					break;
				default:
					return null;
				}
				break;
			default:
				return null;
			}
			int flength = newdata.length;
			int fpadding = 0; while (((flength+fpadding)&3)!=0) fpadding++;
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream out2 = new DataOutputStream(out);
				// RIFF header
				out2.writeInt(RIFF);
				out2.writeInt(Integer.reverseBytes(56 + flength + fpadding)); // length of following data
				out2.writeInt(WAVE); // WAVE format
				// FORMAT chunk, 24 bytes total
				out2.writeInt(FMT );
				out2.writeInt(0x10000000); // length of following data, little-endian
				out2.writeShort(0x0100); // codec; 1 = PCM
				out2.writeShort(Short.reverseBytes((short)numBytes)); // number of channels
				out2.writeInt(Integer.reverseBytes(sampleRate >>> 16)); // sample rate in Hz
				out2.writeInt(Integer.reverseBytes(numBytes * (sampleRate >>> 16) * sampleBytes)); // bytes per second
				out2.writeShort(Short.reverseBytes((short)(numBytes * sampleBytes))); // bytes per sample
				out2.writeShort(Short.reverseBytes((short)sampleSize)); // bits per sample
				// DATA chunk, (8 + numBytes + padding) bytes total
				out2.writeInt(DATA);
				out2.writeInt(Integer.reverseBytes(flength + fpadding)); // number of bytes to follow
				if (sampleBytes == 1) {
					for (int i = 0; i < newdata.length; i++) newdata[i] ^= 0x80;
					out2.write(newdata);
					for (int i = 0; i < fpadding; i++) out2.writeByte(0x80);
				} else {
					for (int i = 0; i < newdata.length; i += sampleBytes) {
						for (int j = 0, k = sampleBytes-1; j < sampleBytes/2; j++, k--) {
							byte t = newdata[i+j];
							newdata[i+j] = newdata[i+k];
							newdata[i+k] = t;
						}
					}
					out2.write(newdata);
					for (int i = 0; i < fpadding; i++) out2.writeByte(0);
				}
				// CYNTH chunk, 20 bytes total
				out2.writeInt(CYNH);
				out2.writeInt(0x0C000000); // length of following data, little-endian
				out2.writeInt(Integer.reverseBytes(loopStart)); // loop start, little-endian
				out2.writeInt(Integer.reverseBytes(loopEnd)); // loop end, little-endian
				out2.writeInt(Integer.reverseBytes(baseFrequency)); // base frequency, little-endian
				// done
				out2.close();
				out.close();
				return out.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		} else if (encoding == -1) {
			// extended
			int numFrames = KSFLUtilities.getInt(data, o+22);
			short sampleSize = KSFLUtilities.getShort(data, o+48);
			int sampleBytes = (sampleSize + 7) / 8;
			byte[] newdata = KSFLUtilities.copy(data, o+64, numBytes * numFrames * sampleBytes);
			int flength = newdata.length;
			int fpadding = 0; while (((flength+fpadding)&3)!=0) fpadding++;
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream out2 = new DataOutputStream(out);
				// RIFF header
				out2.writeInt(RIFF);
				out2.writeInt(Integer.reverseBytes(56 + flength + fpadding)); // length of following data
				out2.writeInt(WAVE); // WAVE format
				// FORMAT chunk, 24 bytes total
				out2.writeInt(FMT );
				out2.writeInt(0x10000000); // length of following data, little-endian
				out2.writeShort(0x0100); // codec; 1 = PCM
				out2.writeShort(Short.reverseBytes((short)numBytes)); // number of channels
				out2.writeInt(Integer.reverseBytes(sampleRate >>> 16)); // sample rate in Hz
				out2.writeInt(Integer.reverseBytes(numBytes * (sampleRate >>> 16) * sampleBytes)); // bytes per second
				out2.writeShort(Short.reverseBytes((short)(numBytes * sampleBytes))); // bytes per sample
				out2.writeShort(Short.reverseBytes((short)sampleSize)); // bits per sample
				// DATA chunk, (8 + numBytes + padding) bytes total
				out2.writeInt(DATA);
				out2.writeInt(Integer.reverseBytes(flength + fpadding)); // number of bytes to follow
				if (sampleBytes == 1) {
					for (int i = 0; i < newdata.length; i++) newdata[i] ^= 0x80;
					out2.write(newdata);
					for (int i = 0; i < fpadding; i++) out2.writeByte(0x80);
				} else {
					for (int i = 0; i < newdata.length; i += sampleBytes) {
						for (int j = 0, k = sampleBytes-1; j < sampleBytes/2; j++, k--) {
							byte t = newdata[i+j];
							newdata[i+j] = newdata[i+k];
							newdata[i+k] = t;
						}
					}
					out2.write(newdata);
					for (int i = 0; i < fpadding; i++) out2.writeByte(0);
				}
				// CYNTH chunk, 20 bytes total
				out2.writeInt(CYNH);
				out2.writeInt(0x0C000000); // length of following data, little-endian
				out2.writeInt(Integer.reverseBytes(loopStart)); // loop start, little-endian
				out2.writeInt(Integer.reverseBytes(loopEnd)); // loop end, little-endian
				out2.writeInt(Integer.reverseBytes(baseFrequency)); // base frequency, little-endian
				// done
				out2.close();
				out.close();
				return out.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		} else if (encoding == 0) {
			// uncompressed
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream out2 = new DataOutputStream(out);
				// RIFF header
				out2.writeInt(RIFF);
				out2.writeInt(Integer.reverseBytes(56 + numBytes + padding)); // length of following data
				out2.writeInt(WAVE); // WAVE format
				// FORMAT chunk, 24 bytes total
				out2.writeInt(FMT );
				out2.writeInt(0x10000000); // length of following data, little-endian
				out2.writeShort(0x0100); // codec; 1 = PCM
				out2.writeShort(0x0100); // number of channels; 1 = mono
				out2.writeInt(Integer.reverseBytes(sampleRate >>> 16)); // sample rate in Hz
				out2.writeInt(Integer.reverseBytes(sampleRate >>> 16)); // bytes per second
				out2.writeShort(0x0100); // bytes per sample; 1 = 8-bit mono
				out2.writeShort(0x0800); // bits per sample; 8 = 8-bit mono
				// DATA chunk, (8 + numBytes + padding) bytes total
				out2.writeInt(DATA);
				out2.writeInt(Integer.reverseBytes(numBytes + padding)); // number of bytes to follow
				out2.write(data, o+22, numBytes);
				for (int i = 0; i < padding; i++) out2.writeByte(0x80);
				// CYNTH chunk, 20 bytes total
				out2.writeInt(CYNH);
				out2.writeInt(0x0C000000); // length of following data, little-endian
				out2.writeInt(Integer.reverseBytes(loopStart)); // loop start, little-endian
				out2.writeInt(Integer.reverseBytes(loopEnd)); // loop end, little-endian
				out2.writeInt(Integer.reverseBytes(baseFrequency)); // base frequency, little-endian
				// done
				out2.close();
				out.close();
				return out.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Converts this sound resource to the AIFF format, if possible, and returns the AIFF data.
	 * If the sound cannot be converted, returns null.
	 * @return AIFF data.
	 */
	public byte[] toAiff() {
		if (getFormat() < 1 || getFormat() > 2) return null;
		for (int i = 0; i < getCommandCount(); i++) {
			int cmd = getCommand(i) & ~DATAOFFSETFLAG;
			if (!(cmd == NULLCMD || cmd == SOUNDCMD || cmd == BUFFERCMD)) return null;
		}
		int o = getSoundDataOffset();
		int numBytes = KSFLUtilities.getInt(data, o+4);
		int padding = 0; while (((numBytes+padding)&1)!=0) padding++;
		int sampleRate = KSFLUtilities.getInt(data, o+8);
		int loopStart = KSFLUtilities.getInt(data, o+12);
		int loopEnd = KSFLUtilities.getInt(data, o+16);
		int encoding = data[o+20];
		int baseFrequency = data[o+21];
		if (encoding == -2) {
			// compressed
			int numFrames = KSFLUtilities.getInt(data, o+22);
			short aiffSampleRateExponent = KSFLUtilities.getShort(data, o+26);
			long aiffSampleRateMantissa = KSFLUtilities.getLong(data, o+28);
			int format = KSFLUtilities.getInt(data, o+40);
			short compressionID = KSFLUtilities.getShort(data, o+56);
			short sampleSize = KSFLUtilities.getShort(data, o+62);
			int sampleBytes = (sampleSize + 7) / 8;
			int flength = numBytes * numFrames * sampleBytes;
			int fpadding = 0; while (((flength+fpadding)&1)!=0) fpadding++;
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream out2 = new DataOutputStream(out);
				// form chunk
				out2.writeInt(FORM);
				out2.writeInt(98 + flength + fpadding);
				out2.writeInt(AIFC);
				// format version chunk
				out2.writeInt(FVER);
				out2.writeInt(4);
				out2.writeInt(0xA2805140);
				// common chunk
				out2.writeInt(COMM);
				out2.writeInt(38);
				out2.writeShort(numBytes); // number of channels
				out2.writeInt(numFrames); // number of frames
				out2.writeShort(sampleSize); // bits per sample
				out2.writeShort(aiffSampleRateExponent);
				out2.writeLong(aiffSampleRateMantissa);
				switch (compressionID) {
				case COMPID_NONE: out2.writeInt(FORMAT_NONE); out2.write(COMPNAME_NONE); break;
				case COMPID_ACE_2TO1: out2.writeInt(FORMAT_ACE_2TO1); out2.write(COMPNAME_ACE_2TO1); break;
				case COMPID_ACE_8TO3: out2.writeInt(FORMAT_ACE_8TO3); out2.write(COMPNAME_ACE_8TO3); break;
				case COMPID_MACE_3TO1: out2.writeInt(FORMAT_MACE_3TO1); out2.write(COMPNAME_MACE_3TO1); break;
				case COMPID_MACE_6TO1: out2.writeInt(FORMAT_MACE_6TO1); out2.write(COMPNAME_MACE_6TO1); break;
				case COMPID_USE_FORMAT:
					switch (format) {
					case FORMAT_NONE: out2.writeInt(FORMAT_NONE); out2.write(COMPNAME_NONE); break;
					case FORMAT_ACE_2TO1: out2.writeInt(FORMAT_ACE_2TO1); out2.write(COMPNAME_ACE_2TO1); break;
					case FORMAT_ACE_8TO3: out2.writeInt(FORMAT_ACE_8TO3); out2.write(COMPNAME_ACE_8TO3); break;
					case FORMAT_MACE_3TO1: out2.writeInt(FORMAT_MACE_3TO1); out2.write(COMPNAME_MACE_3TO1); break;
					case FORMAT_MACE_6TO1: out2.writeInt(FORMAT_MACE_6TO1); out2.write(COMPNAME_MACE_6TO1); break;
					default: out2.writeInt(format); out2.write(new byte[16]); break;
					}
					break;
				default: return null;
				}
				// cynth chunk
				out2.writeInt(CYNH);
				out2.writeInt(12);
				out2.writeInt(loopStart);
				out2.writeInt(loopEnd);
				out2.writeInt(baseFrequency);
				// sound data chunk
				out2.writeInt(SSND);
				out2.writeInt(8 + flength);
				out2.writeInt(0); // offset
				out2.writeInt(0); // block size
				out2.write(data, o+64, flength);
				for (int i = 0; i < fpadding; i++) out2.writeByte(0);
				// done
				out2.close();
				out.close();
				return out.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		} else if (encoding == -1) {
			// extended
			int numFrames = KSFLUtilities.getInt(data, o+22);
			short aiffSampleRateExponent = KSFLUtilities.getShort(data, o+26);
			long aiffSampleRateMantissa = KSFLUtilities.getLong(data, o+28);
			short sampleSize = KSFLUtilities.getShort(data, o+48);
			int sampleBytes = (sampleSize + 7) / 8;
			int flength = numBytes * numFrames * sampleBytes;
			int fpadding = 0; while (((flength+fpadding)&1)!=0) fpadding++;
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream out2 = new DataOutputStream(out);
				// form chunk
				out2.writeInt(FORM);
				out2.writeInt(98 + flength + fpadding);
				out2.writeInt(AIFC);
				// format version chunk
				out2.writeInt(FVER);
				out2.writeInt(4);
				out2.writeInt(0xA2805140);
				// common chunk
				out2.writeInt(COMM);
				out2.writeInt(38);
				out2.writeShort(numBytes); // number of channels
				out2.writeInt(numFrames); // number of frames
				out2.writeShort(sampleSize); // bits per sample
				out2.writeShort(aiffSampleRateExponent);
				out2.writeLong(aiffSampleRateMantissa);
				out2.writeInt(FORMAT_NONE);
				out2.write(COMPNAME_NONE);
				// cynth chunk
				out2.writeInt(CYNH);
				out2.writeInt(12);
				out2.writeInt(loopStart);
				out2.writeInt(loopEnd);
				out2.writeInt(baseFrequency);
				// sound data chunk
				out2.writeInt(SSND);
				out2.writeInt(8 + flength);
				out2.writeInt(0); // offset
				out2.writeInt(0); // block size
				out2.write(data, o+64, flength);
				for (int i = 0; i < fpadding; i++) out2.writeByte(0);
				// done
				out2.close();
				out.close();
				return out.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		} else if (encoding == 0) {
			// uncompressed
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream out2 = new DataOutputStream(out);
				// form chunk
				out2.writeInt(FORM);
				out2.writeInt(98 + numBytes + padding);
				out2.writeInt(AIFC);
				// format version chunk
				out2.writeInt(FVER);
				out2.writeInt(4);
				out2.writeInt(0xA2805140);
				// common chunk
				out2.writeInt(COMM);
				out2.writeInt(38);
				out2.writeShort(1); // number of channels
				out2.writeInt(numBytes); // number of frames
				out2.writeShort(8); // bits per sample
				switch (sampleRate) {
				case RATE_44KHZ   : out2.writeShort(EXPONENT_44KHZ   ); out2.writeLong(MANTISSA_44KHZ   ); break;
				case RATE_22KHZ   : out2.writeShort(EXPONENT_22KHZ   ); out2.writeLong(MANTISSA_22KHZ   ); break;
				case RATE_22050KHZ: out2.writeShort(EXPONENT_22050KHZ); out2.writeLong(MANTISSA_22050KHZ); break;
				case RATE_11KHZ   : out2.writeShort(EXPONENT_11KHZ   ); out2.writeLong(MANTISSA_11KHZ   ); break;
				case RATE_11025KHZ: out2.writeShort(EXPONENT_11025KHZ); out2.writeLong(MANTISSA_11025KHZ); break;
				default: return null;
				}
				out2.writeInt(FORMAT_NONE); // compression type
				out2.write(COMPNAME_NONE); // compression name
				// cynth chunk
				out2.writeInt(CYNH);
				out2.writeInt(12);
				out2.writeInt(loopStart);
				out2.writeInt(loopEnd);
				out2.writeInt(baseFrequency);
				// sound data chunk
				out2.writeInt(SSND);
				out2.writeInt(8 + numBytes);
				out2.writeInt(0); // offset
				out2.writeInt(0); // block size
				byte[] newdata = KSFLUtilities.copy(data, o+22, numBytes);
				for (int i = 0; i < newdata.length; i++) newdata[i] ^= 0x80;
				out2.write(newdata);
				for (int i = 0; i < padding; i++) out2.writeByte(0);
				// done
				out2.close();
				out.close();
				return out.toByteArray();
			} catch (IOException ioe) {
				return null;
			}
		} else {
			return null;
		}
	}
}
