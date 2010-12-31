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

import java.util.Map;
import java.util.HashMap;

public class DFFClassLoader extends ClassLoader {
	private Map<String,Class<?>> classes = new HashMap<String,Class<?>>();
	private DFFResourceProvider dp;
	
	public DFFClassLoader(DFFResourceProvider dp) {
		super(DFFClassLoader.class.getClassLoader());
		this.dp = dp;
	}
	
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		if (classes.containsKey(className)) return classes.get(className);
		
		if (
				className.startsWith("java.") ||
				className.startsWith("javax.") ||
				className.startsWith("sun.") ||
				className.startsWith("sunw.")
		) throw new ClassNotFoundException("Could not find "+className);
		
		try {
			byte[] data = dp.getData(0x457865634A617661L, className);
			if (data != null && data.length > 0) {
				Class<?> c = defineClass(className, data, 0, data.length);
				classes.put(className, c);
				return c;
			}
		} catch (Exception e) {}
		
		try {
			byte[] data = dp.getData(0x457865634A617661L, className+".class");
			if (data != null && data.length > 0) {
				Class<?> c = defineClass(className, data, 0, data.length);
				classes.put(className, c);
				return c;
			}
		} catch (Exception e) {}
		
		throw new ClassNotFoundException("Could not find "+className);
	}
}
