package com.github.tehnexus.sqlite;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class SQLUtil {

	public static InputStream blobFromDatabase(String sql) throws SQLException {

		try (SQLiteCon connectionSQLite = new SQLiteCon(defaultDatabaseLocation());
				ResultSet rs = connectionSQLite.executeQuery(sql)) {

			if (rs.next()) { // check if record exists, if yes use it
				// InputStream is = rs.getBinaryStream("Attachment");
				return rs.getBinaryStream("Attachment");
			}
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
		throw new SQLException("Cannot retreive stream from database.");
	}

	public static String defaultDatabaseLocation() {
		Properties prop = new Properties();
		try {
			prop.loadFromXML(new FileInputStream("config.xml"));
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.toString());
		}
		return prop.getProperty("databaselocation") + prop.getProperty("databasename");
	}

	public static int executePreparedStatement(String sqlString, Object[] args) {
		int i = 0;
		try (SQLiteCon connectionSQLite = new SQLiteCon(defaultDatabaseLocation())) {
			i = connectionSQLite.executePreparedStatement(sqlString, args);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}
}
