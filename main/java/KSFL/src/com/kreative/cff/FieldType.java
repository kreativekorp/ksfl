/*
 * Copyright &copy; 2008-2011 Rebecca G. Bettencourt / Kreative Software
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

package com.kreative.cff;

public enum FieldType {
	FILLER				(new char[]{'F','f'},			(byte)0x00),
	SIZE_WITHOUT_HEADER	(new char[]{'S','s'},			(byte)0x01),
	SIZE_WITH_HEADER	(new char[]{'Z','z'},			(byte)0x02),
	CHUNK_COUNT			(new char[]{'C','c'},			(byte)0x03),
	INTEGER_TYPE		(new char[]{'M','m'},			(byte)0x04),
	CHARACTER_TYPE		(new char[]{'T','t'},			(byte)0x05),
	ID_NUMBER			(new char[]{'N','n','I','i'},	(byte)0x06),
	CHECKSUM			(new char[]{'H','h'},			(byte)0x0E),
	DATA				(new char[]{'d','D'},			(byte)0x0F);
	
	private char[] letters;
	private byte bitPattern;
	
	private FieldType(char[] letters, byte bitPattern) {
		this.letters = letters;
		this.bitPattern = bitPattern;
	}
	
	public static FieldType forChar(char letter) {
		for (FieldType ft : FieldType.values()) {
			for (char ch : ft.letters) {
				if (ch == letter) return ft;
			}
		}
		return null;
	}
	
	public static FieldType forBitPattern(byte bitPattern) {
		for (FieldType ft : FieldType.values()) {
			if ((ft.bitPattern & 0x0F) == (bitPattern & 0x0F)) return ft;
		}
		return null;
	}
	
	public char[] allCharRepresentations() {
		return letters;
	}
	
	public char canonicalCharRepresentation() {
		return letters[0];
	}
	
	public byte bitPatternRepresentation() {
		return bitPattern;
	}
}
