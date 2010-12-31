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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import com.kreative.ksfl.KSFLUtilities;

/**
 * The <code>DFFDatabase</code> class provides a DFF interface
 * to a table in a SQL database containing DFF objects.
 * <p>
 * The table must contain the six following fields:
 * <ul>
 * <li><code>type</code> (a <code>BIGINT</code>)</li>
 * <li><code>id</code> (an <code>INT</code>)</li>
 * <li><code>datatype</code> (a <code>SMALLINT</code>)</li>
 * <li><code>name</code> (a <code>VARCHAR(255)</code>)</li>
 * <li><code>attributes</code> (a <code>SMALLINT</code>)</li>
 * <li><code>data</code> (a <code>BLOB</code>)</li>
 * </ul>
 * @since KSFL 1.0
 * @author Rebecca G. Bettencourt, Kreative Software
 */
public class DFFResourceDatabase extends DFFResourceProvider {
	private Connection conn;
	private String table;
	
	/**
	 * Creates a new <code>DFFDatabase</code> using the provided
	 * <code>Connection</code> and table name.
	 * <p>
	 * The specified table must contain the six following fields:
	 * <ul>
	 * <li><code>type</code> (a <code>BIGINT</code>)</li>
	 * <li><code>id</code> (an <code>INT</code>)</li>
	 * <li><code>datatype</code> (a <code>SMALLINT</code>)</li>
	 * <li><code>name</code> (a <code>VARCHAR(255)</code>)</li>
	 * <li><code>attributes</code> (a <code>SMALLINT</code>)</li>
	 * <li><code>data</code> (a <code>BLOB</code>)</li>
	 * </ul>
	 * @param conn the database connection to use.
	 * @param table the name of the table containing DFF objects.
	 * @throws NotADFFFileException if <code>conn</code> or <code>table</code> is <code>null</code>, <code>table</code> is an empty string, <code>table</code> is an invalid table name, the table does not exist, or the table does not contain the fields required to store DFF objects.
	 */
	public DFFResourceDatabase(Connection conn, String table) throws NotADFFFileException {
		if (conn == null || table == null || table.length() < 1) throw new NotADFFFileException("Connection or table name invalid.");
		try {
			boolean hasType=false, hasId=false, hasDataType=false, hasName=false, hasAttributes=false, hasData=false;
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getColumns(null, null, table, null);
			while (rs.next()) {
				String n = rs.getString("COLUMN_NAME");
				if (n.equals("type")) hasType = true;
				else if (n.equals("id")) hasId = true;
				else if (n.equals("datatype")) hasDataType = true;
				else if (n.equals("name")) hasName = true;
				else if (n.equals("attributes")) hasAttributes = true;
				else if (n.equals("data")) hasData = true;
			}
			if (!hasType || !hasId || !hasDataType || !hasName || !hasAttributes || !hasData) {
				throw new NotADFFFileException("Required field missing.");
			}
		} catch (SQLException se) {
			throw new NotADFFFileException("Connection or table name invalid.");
		}
		this.conn = conn;
		this.table = table;
	}
	
	/**
	 * Returns the database connection used by this <code>DFFDatabase</code>.
	 * @return the database connection used by this <code>DFFDatabase</code>.
	 */
	public Connection getConnection() {
		return conn;
	}
	
	/**
	 * Returns the table name containing DFF objects.
	 * @return the table name.
	 */
	public String getTableName() {
		return table;
	}
	
	@Override
	public boolean isReadOnly() {
		return false;
	}
	
	@Override
	public void flush() {
		try {
			conn.commit();
		} catch (Exception e) {}
	}
	
	@Override
	public void close() {
		try {
			conn.close();
		} catch (Exception e) {}
	}
	
	@Override
	public boolean add(DFFResource r) throws DFFResourceAlreadyExistsException {
		try {
			PreparedStatement ps;
			ps = conn.prepareStatement("SELECT * FROM "+table+" WHERE type=? AND id=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, r.type);
			ps.setInt(2, r.id);
			if (ps.executeQuery().next()) throw new DFFResourceAlreadyExistsException();
			ps = conn.prepareStatement("INSERT INTO "+table+" (type, id, datatype, name, attributes, data) VALUES (?, ?, ?, ?, ?, ?)", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			ps.setLong(1, r.type);
			ps.setInt(2, r.id);
			ps.setShort(3, r.datatype);
			ps.setString(4, r.name);
			ps.setShort(5, r.getAttributes());
			ps.setBytes(6, r.data);
			return (ps.executeUpdate() > 0);
		} catch (SQLException ioe) {}
		return false;
	}
	
	@Override
	public boolean contains(long type, int id) {
		try {
			PreparedStatement ps;
			ps = conn.prepareStatement("SELECT * FROM "+table+" WHERE type=? AND id=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			ps.setInt(2, id);
			return (ps.executeQuery().next());
		} catch (SQLException ioe) {}
		return false;
	}
	@Override
	public boolean contains(long type, String name) {
		try {
			PreparedStatement ps;
			ps = conn.prepareStatement("SELECT * FROM "+table+" WHERE type=? AND name=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			ps.setString(2, name);
			return (ps.executeQuery().next());
		} catch (SQLException ioe) {}
		return false;
	}
	
	@Override
	public DFFResource get(long type, int id) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ps = conn.prepareStatement("SELECT * FROM "+table+" WHERE type=? AND id=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			ps.setInt(2, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				return new DFFResource(
						rs.getLong("type"),
						rs.getInt("id"),
						rs.getShort("datatype"),
						rs.getShort("attributes"),
						rs.getString("name"),
						rs.getBytes("data")
				);
			}
		} catch (SQLException ioe) {}
		return null;
	}
	@Override
	public DFFResource get(long type, String name) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ps = conn.prepareStatement("SELECT * FROM "+table+" WHERE type=? AND name=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			ps.setString(2, name);
			rs = ps.executeQuery();
			if (rs.next()) {
				return new DFFResource(
						rs.getLong("type"),
						rs.getInt("id"),
						rs.getShort("datatype"),
						rs.getShort("attributes"),
						rs.getString("name"),
						rs.getBytes("data")
				);
			}
		} catch (SQLException ioe) {}
		return null;
	}
	
	@Override
	public DFFResource getAttributes(long type, int id) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ps = conn.prepareStatement("SELECT * FROM "+table+" WHERE type=? AND id=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			ps.setInt(2, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				return new DFFResource(
						rs.getLong("type"),
						rs.getInt("id"),
						rs.getShort("datatype"),
						rs.getShort("attributes"),
						rs.getString("name"),
						new byte[0]
				);
			}
		} catch (SQLException ioe) {}
		return null;
	}
	@Override
	public DFFResource getAttributes(long type, String name) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ps = conn.prepareStatement("SELECT * FROM "+table+" WHERE type=? AND name=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			ps.setString(2, name);
			rs = ps.executeQuery();
			if (rs.next()) {
				return new DFFResource(
						rs.getLong("type"),
						rs.getInt("id"),
						rs.getShort("datatype"),
						rs.getShort("attributes"),
						rs.getString("name"),
						new byte[0]
				);
			}
		} catch (SQLException ioe) {}
		return null;
	}
	
	@Override
	public long getLength(long type, int id) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ps = conn.prepareStatement("SELECT * FROM "+table+" WHERE type=? AND id=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			ps.setInt(2, id);
			rs = ps.executeQuery();
			if (rs.next()) return rs.getBytes("data").length;
		} catch (SQLException ioe) {}
		return 0;
	}
	@Override
	public long getLength(long type, String name) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ps = conn.prepareStatement("SELECT * FROM "+table+" WHERE type=? AND name=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			ps.setString(2, name);
			rs = ps.executeQuery();
			if (rs.next()) return rs.getBytes("data").length;
		} catch (SQLException ioe) {}
		return 0;
	}
	
	@Override
	public byte[] getData(long type, int id) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ps = conn.prepareStatement("SELECT * FROM "+table+" WHERE type=? AND id=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			ps.setInt(2, id);
			rs = ps.executeQuery();
			if (rs.next()) return rs.getBytes("data");
			else return new byte[0];
		} catch (SQLException ioe) {}
		return null;
	}
	@Override
	public byte[] getData(long type, String name) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ps = conn.prepareStatement("SELECT * FROM "+table+" WHERE type=? AND name=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			ps.setString(2, name);
			rs = ps.executeQuery();
			if (rs.next()) return rs.getBytes("data");
			else return new byte[0];
		} catch (SQLException ioe) {}
		return null;
	}
	
	@Override
	public int read(long type, int id, long doffset, byte[] data, int off, int len) {
		byte[] junk = getData(type,id);
		int n = 0;
		for (int s = (int)doffset, d = off;
			s < junk.length && d < data.length && n < len;
			s++, d++, n++
		) data[d] = junk[s];
		return n;
	}
	@Override
	public int read(long type, String name, long doffset, byte[] data, int off, int len) {
		byte[] junk = getData(type,name);
		int n = 0;
		for (int s = (int)doffset, d = off;
			s < junk.length && d < data.length && n < len;
			s++, d++, n++
		) data[d] = junk[s];
		return n;
	}
	
	@Override
	public boolean remove(long type, int id) {
		try {
			PreparedStatement ps;
			ps = conn.prepareStatement("DELETE FROM "+table+" WHERE type=? AND id=? LIMIT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			ps.setLong(1, type);
			ps.setInt(2, id);
			return (ps.executeUpdate() > 0);
		} catch (SQLException ioe) {}
		return false;
	}
	@Override
	public boolean remove(long type, String name) {
		try {
			PreparedStatement ps;
			ps = conn.prepareStatement("DELETE FROM "+table+" WHERE type=? AND name=? LIMIT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			ps.setLong(1, type);
			ps.setString(2, name);
			return (ps.executeUpdate() > 0);
		} catch (SQLException ioe) {}
		return false;
	}
	
	@Override
	public boolean set(long type, int id, DFFResource r) throws DFFResourceAlreadyExistsException {
		try {
			if (type != r.type || id != r.id) {
				if (contains(r.type, r.id)) throw new DFFResourceAlreadyExistsException();
			}
			PreparedStatement ps;
			ps = conn.prepareStatement("UPDATE "+table+" SET type=?, id=?, datatype=?, name=?, attributes=?, data=? WHERE type=? AND id=? LIMIT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			ps.setLong(1, r.type);
			ps.setInt(2, r.id);
			ps.setShort(3, r.datatype);
			ps.setString(4, r.name);
			ps.setShort(5, r.getAttributes());
			ps.setBytes(6, r.data);
			ps.setLong(7, type);
			ps.setInt(8, id);
			return (ps.executeUpdate() > 0);
		} catch (SQLException ioe) {}
		return false;
	}
	@Override
	public boolean set(long type, String name, DFFResource r) throws DFFResourceAlreadyExistsException {
		try {
			if (type != r.type || getIDFromName(type, name) != r.id) {
				if (contains(r.type, r.id)) throw new DFFResourceAlreadyExistsException();
			}
			PreparedStatement ps;
			ps = conn.prepareStatement("UPDATE "+table+" SET type=?, id=?, datatype=?, name=?, attributes=?, data=? WHERE type=? AND name=? LIMIT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			ps.setLong(1, r.type);
			ps.setInt(2, r.id);
			ps.setShort(3, r.datatype);
			ps.setString(4, r.name);
			ps.setShort(5, r.getAttributes());
			ps.setBytes(6, r.data);
			ps.setLong(7, type);
			ps.setString(8, name);
			return (ps.executeUpdate() > 0);
		} catch (SQLException ioe) {}
		return false;
	}
	
	@Override
	public boolean setAttributes(long type, int id, DFFResource r) throws DFFResourceAlreadyExistsException {
		try {
			if (type != r.type || id != r.id) {
				if (contains(r.type, r.id)) throw new DFFResourceAlreadyExistsException();
			}
			PreparedStatement ps;
			ps = conn.prepareStatement("UPDATE "+table+" SET type=?, id=?, datatype=?, name=?, attributes=? WHERE type=? AND id=? LIMIT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			ps.setLong(1, r.type);
			ps.setInt(2, r.id);
			ps.setShort(3, r.datatype);
			ps.setString(4, r.name);
			ps.setShort(5, r.getAttributes());
			ps.setLong(6, type);
			ps.setInt(7, id);
			return (ps.executeUpdate() > 0);
		} catch (SQLException ioe) {}
		return false;
	}
	@Override
	public boolean setAttributes(long type, String name, DFFResource r) throws DFFResourceAlreadyExistsException {
		try {
			if (type != r.type || getIDFromName(type, name) != r.id) {
				if (contains(r.type, r.id)) throw new DFFResourceAlreadyExistsException();
			}
			PreparedStatement ps;
			ps = conn.prepareStatement("UPDATE "+table+" SET type=?, id=?, datatype=?, name=?, attributes=? WHERE type=? AND name=? LIMIT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			ps.setLong(1, r.type);
			ps.setInt(2, r.id);
			ps.setShort(3, r.datatype);
			ps.setString(4, r.name);
			ps.setShort(5, r.getAttributes());
			ps.setLong(6, type);
			ps.setString(7, name);
			return (ps.executeUpdate() > 0);
		} catch (SQLException ioe) {}
		return false;
	}
	
	@Override
	public boolean setLength(long type, int id, long len) {
		if (len > (long)Integer.MAX_VALUE) throw new DFFResourceTooBigException();
		return setData(type, id, KSFLUtilities.resize(getData(type, id), (int)len));
	}
	@Override
	public boolean setLength(long type, String name, long len) {
		if (len > (long)Integer.MAX_VALUE) throw new DFFResourceTooBigException();
		int id = getIDFromName(type,name);
		return setData(type, id, KSFLUtilities.resize(getData(type, id), (int)len));
	}
	
	@Override
	public boolean setData(long type, int id, byte[] data) {
		try {
			PreparedStatement ps;
			ps = conn.prepareStatement("UPDATE "+table+" SET data=? WHERE type=? AND id=? LIMIT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			ps.setBytes(1, data);
			ps.setLong(2, type);
			ps.setInt(3, id);
			return (ps.executeUpdate() > 0);
		} catch (SQLException ioe) {}
		return false;
	}
	@Override
	public boolean setData(long type, String name, byte[] data) {
		try {
			PreparedStatement ps;
			ps = conn.prepareStatement("UPDATE "+table+" SET data=? WHERE type=? AND name=? LIMIT 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			ps.setBytes(1, data);
			ps.setLong(2, type);
			ps.setString(3, name);
			return (ps.executeUpdate() > 0);
		} catch (SQLException ioe) {}
		return false;
	}
	
	@Override
	public int write(long type, int id, long doffset, byte[] data, int off, int len) {
		if (doffset+len > (long)Integer.MAX_VALUE) throw new DFFResourceTooBigException();
		byte[] junk = getData(type,id);
		int n = len;
		if (doffset > junk.length) {
			junk = KSFLUtilities.paste(junk, junk.length, new byte[(int)doffset-junk.length]);
			junk = KSFLUtilities.paste(junk, (int)doffset, KSFLUtilities.copy(data, off, len));
		} else if (doffset+len > junk.length) {
			junk = KSFLUtilities.cut(junk, (int)doffset, junk.length-(int)doffset);
			junk = KSFLUtilities.paste(junk, (int)doffset, KSFLUtilities.copy(data, off, len));
		} else {
			n = 0;
			for (int s = off, d = (int)doffset;
				s < data.length && d < junk.length && n < len;
				s++, d++, n++
			) junk[d] = data[s];
		}
		return setData(type,id,junk)?n:0;
	}
	@Override
	public int write(long type, String name, long doffset, byte[] data, int off, int len) {
		if (doffset+len > (long)Integer.MAX_VALUE) throw new DFFResourceTooBigException();
		int id = getIDFromName(type,name);
		byte[] junk = getData(type,id);
		int n = len;
		if (doffset > junk.length) {
			junk = KSFLUtilities.paste(junk, junk.length, new byte[(int)doffset-junk.length]);
			junk = KSFLUtilities.paste(junk, (int)doffset, KSFLUtilities.copy(data, off, len));
		} else if (doffset+len > junk.length) {
			junk = KSFLUtilities.cut(junk, (int)doffset, junk.length-(int)doffset);
			junk = KSFLUtilities.paste(junk, (int)doffset, KSFLUtilities.copy(data, off, len));
		} else {
			n = 0;
			for (int s = off, d = (int)doffset;
				s < data.length && d < junk.length && n < len;
				s++, d++, n++
			) junk[d] = data[s];
		}
		return setData(type,id,junk)?n:0;
	}
	
	@Override
	public int getTypeCount() {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ArrayList<Long> a = new ArrayList<Long>();
			ps = conn.prepareStatement("SELECT type FROM "+table, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			while (rs.next()) {
				long t = rs.getLong("type");
				if (!a.contains(t)) a.add(t);
			}
			return a.size();
		} catch (SQLException ioe) {}
		return 0;
	}
	@Override
	public long getType(int index) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ArrayList<Long> a = new ArrayList<Long>();
			ps = conn.prepareStatement("SELECT type FROM "+table, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			while (rs.next()) {
				long t = rs.getLong("type");
				if (!a.contains(t)) {
					if (a.size() == index) return t;
					else a.add(t);
				}
			}
		} catch (SQLException ioe) {}
		return 0;
	}
	@Override
	public long[] getTypes() {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ArrayList<Long> a = new ArrayList<Long>();
			ps = conn.prepareStatement("SELECT type FROM "+table, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			while (rs.next()) {
				long t = rs.getLong("type");
				if (!a.contains(t)) a.add(t);
			}
			long[] aa = new long[a.size()];
			Iterator<Long> i = a.iterator();
			for (int j = 0; i.hasNext() && j<aa.length; j++) {
				aa[j] = i.next();
			}
			return aa;
		} catch (SQLException ioe) {}
		return null;
	}
	
	@Override
	public int getResourceCount(long type) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ArrayList<Integer> a = new ArrayList<Integer>();
			ps = conn.prepareStatement("SELECT id FROM "+table+" WHERE type=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			rs = ps.executeQuery();
			while (rs.next()) a.add(rs.getInt("id"));
			return a.size();
		} catch (SQLException ioe) {}
		return 0;
	}
	@Override
	public int getID(long type, int index) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ArrayList<Integer> a = new ArrayList<Integer>();
			ps = conn.prepareStatement("SELECT id FROM "+table+" WHERE type=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			rs = ps.executeQuery();
			while (rs.next()) {
				int i = rs.getInt("id");
				if (a.size() == index) return i;
				else a.add(i);
			}
		} catch (SQLException ioe) {}
		return 0;
	}
	@Override
	public int[] getIDs(long type) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ArrayList<Integer> a = new ArrayList<Integer>();
			ps = conn.prepareStatement("SELECT id FROM "+table+" WHERE type=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			rs = ps.executeQuery();
			while (rs.next()) a.add(rs.getInt("id"));
			int[] aa = new int[a.size()];
			Iterator<Integer> i = a.iterator();
			for (int j = 0; i.hasNext() && j<aa.length; j++) aa[j] = i.next();
			return aa;
		} catch (SQLException ioe) {}
		return null;
	}
	@Override
	public String getName(long type, int index) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ArrayList<String> a = new ArrayList<String>();
			ps = conn.prepareStatement("SELECT name FROM "+table+" WHERE type=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			rs = ps.executeQuery();
			while (rs.next()) {
				String s = rs.getString("name");
				if (a.size() == index) return s;
				else a.add(s);
			}
		} catch (SQLException ioe) {}
		return null;
	}
	@Override
	public String[] getNames(long type) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ArrayList<String> a = new ArrayList<String>();
			ps = conn.prepareStatement("SELECT name FROM "+table+" WHERE type=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			rs = ps.executeQuery();
			while (rs.next()) a.add(rs.getString("name"));
			return a.toArray(new String[0]);
		} catch (SQLException ioe) {}
		return null;
	}
	
	@Override
	public int getNextAvailableID(long type, int start) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ArrayList<Integer> a = new ArrayList<Integer>();
			ps = conn.prepareStatement("SELECT id FROM "+table+" WHERE type=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			rs = ps.executeQuery();
			while (rs.next()) a.add(rs.getInt("id"));
			int i = start;
			while (a.contains(i)) i++;
			return i;
		} catch (SQLException ioe) {}
		return 0;
	}
	
	@Override
	public String getNameFromID(long type, int id) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ps = conn.prepareStatement("SELECT name FROM "+table+" WHERE type=? AND id=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			ps.setInt(2, id);
			rs = ps.executeQuery();
			if (rs.next()) return rs.getString("name");
		} catch (SQLException ioe) {}
		return null;
	}
	
	@Override
	public int getIDFromName(long type, String name) {
		try {
			PreparedStatement ps;
			ResultSet rs;
			ps = conn.prepareStatement("SELECT id FROM "+table+" WHERE type=? AND name=?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setLong(1, type);
			ps.setString(2, name);
			rs = ps.executeQuery();
			if (rs.next()) return rs.getInt("id");
		} catch (SQLException ioe) {}
		return 0;
	}
}
