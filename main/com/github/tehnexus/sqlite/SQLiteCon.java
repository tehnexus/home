package com.github.tehnexus.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteCon implements AutoCloseable {

	private Connection			c		= null;
	private PreparedStatement	pstmt	= null;

	public SQLiteCon(String dataBaseLocation) throws SQLException {
		connect(dataBaseLocation);
	}

	@Override
	public void close() throws SQLException {
		closePreparedStatement();
		c.close();
	}

	public void closePreparedStatement() throws SQLException {
		try {
			pstmt.close();
		}
		catch (NullPointerException ignore) {
		}
	}

	public void connect(String dbLocation) throws SQLException {
		c = DriverManager.getConnection("jdbc:sqlite:" + dbLocation);
	}

	public ResultSet executePreparedQueryStatement(String sqlString, Object[] args) throws SQLException {
		pstmt = c.prepareStatement(sqlString);
		for (int i = 0; i < args.length; i++) {
			Object obj = args[i];
			int parameterIndex = i + 1;
			if (obj instanceof String) {
				pstmt.setString(parameterIndex, (String) obj);
			}
			else {
				System.err.println("uncovered datatype: " + obj.toString());
			}
		}
		return pstmt.executeQuery();
	}

	public int executePreparedStatement(String sqlString, Object[] args) throws SQLException {
		pstmt = c.prepareStatement(sqlString);

		for (int i = 0; i < args.length; i++) {
			Object obj = args[i];
			int parameterIndex = i + 1;
			if (obj instanceof Integer) {
				pstmt.setInt(parameterIndex, (Integer) obj);

			}
			else if (obj instanceof String) {
				pstmt.setString(parameterIndex, (String) obj);

			}
			else if (obj instanceof Long) {
				pstmt.setLong(parameterIndex, (Long) obj);

			}
			else if (obj instanceof Double) {
				pstmt.setDouble(parameterIndex, (Double) obj);

			}
			else if (obj instanceof byte[]) {
				pstmt.setBytes(parameterIndex, (byte[]) obj);

			}
			else {
				System.err.println("uncovered datatype: " + obj.toString());
			}
		}
		return pstmt.executeUpdate();
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		return c.createStatement().executeQuery(sql);
	}

}