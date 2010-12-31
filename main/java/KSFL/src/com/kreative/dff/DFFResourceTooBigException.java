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

package com.kreative.dff;

/**
 * The <code>DFFResourceTooBigException</code> should be thrown by
 * any class extending <code>DFFResourceProvider</code> when an attempt is
 * made to retrieve the data of a DFF object, but the data is
 * longer than the <code>DFFResourceProvider</code> can handle.
 * In particular, it will be thrown by <code>findObject</code>,
 * <code>getObject</code>, and similar when the object is
 * larger than 2GB, since that is the largest size of a
 * <code>byte[]</code> in Java.
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class DFFResourceTooBigException extends RuntimeException {
	static final long serialVersionUID = 1;
	
	/**
	 * Constructs a <code>DFFResourceTooBigException</code> with
	 * <code>null</code> as its error detail message.
	 */
	public DFFResourceTooBigException() {
		super();
	}
	
	/**
	 * Constructs a <code>DFFResourceTooBigException</code> with
	 * the specified detail message. The error message string <code>s</code>
	 * can later be retrieved by the <code>Throwable.getMessage()</code>
	 * method of class <code>java.lang.Throwable</code>.
	 * @param s the detail message.
	 */
	public DFFResourceTooBigException(String s) {
		super(s);
	}
}
