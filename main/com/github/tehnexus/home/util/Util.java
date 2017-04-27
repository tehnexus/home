package com.github.tehnexus.home.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.ImageIcon;

import com.github.tehnexus.sqlite.SQLUtil;
import com.github.tehnexus.sqlite.SQLiteCon;

public class Util {

	public final static int DEFAULT_ICONWIDTH = 20;

	private static ImageIcon checkIconSize(ImageIcon icon, int maxWidth) {
		if (maxWidth < 0)
			maxWidth = DEFAULT_ICONWIDTH;
		if (icon.getIconWidth() > maxWidth) {
			icon = new ImageIcon(icon.getImage().getScaledInstance(maxWidth, -1, Image.SCALE_SMOOTH));
		}
		return icon;
	}

	public static String defaultDatabaseLocation() {
		String dbLoc = getXMLProperty("databaselocation");
		if (!dbLoc.substring(dbLoc.length() - 1).equalsIgnoreCase("\\"))
			dbLoc = dbLoc + "\\";
		return dbLoc + getXMLProperty("databasename");
	}

	public static ImageIcon getIcon(String fileName) {
		URL url = ClassLoader.getSystemResource(fileName);
		Image image = Toolkit.getDefaultToolkit().getImage(url);
		return new ImageIcon(image);
	}

	public static ImageIcon getIcon(String fileName, int maxWidth) {
		ImageIcon icon = getIcon(fileName);
		return checkIconSize(icon, maxWidth);
	}

	public static String getProperty(String key) {
		String sqlString = "SELECT value FROM config WHERE key='" + key + "'";
		try (SQLiteCon connectionSQLite = new SQLiteCon(com.github.tehnexus.home.util.Util.defaultDatabaseLocation());
				ResultSet rs = connectionSQLite.executeQuery(sqlString)) {

			return rs.getString("value");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getXMLProperty(String propertyName) {
		Properties prop = new Properties();
		try {
			prop.loadFromXML(new FileInputStream("config.xml"));
			return prop.getProperty(propertyName);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> boolean isAnyOf(T find, @SuppressWarnings("unchecked") T... args) {
		for (T search : args) {
			if (find.equals(search))
				return true;
		}
		return false;
	}

	public static void setProperty(String key, String value) {
		String sqlString = "UPDATE config SET value=? WHERE key=?";
		SQLUtil.executePreparedStatement(sqlString, new Object[] { value, key });
	}
}
