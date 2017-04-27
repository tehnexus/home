package com.github.tehnexus.sqlite;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtil {

	public static InputStream blobFromDatabase(String sql) throws SQLException {

		try (SQLiteCon connectionSQLite = new SQLiteCon(com.github.tehnexus.home.util.Util.defaultDatabaseLocation());
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

	public static void executePreparedStatement(String sqlString, Object[] args) {
		Runnable runnable = () -> {
			try (SQLiteCon connectionSQLite = new SQLiteCon(
					com.github.tehnexus.home.util.Util.defaultDatabaseLocation())) {
				connectionSQLite.executePreparedStatement(sqlString, args);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		};
		new Thread(runnable).start();
	}
}
