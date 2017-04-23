package com.github.tehnexus.home.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;

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

	public static ImageIcon getIcon(String fileName) {
		URL url = ClassLoader.getSystemResource(fileName);
		Image image = Toolkit.getDefaultToolkit().getImage(url);
		return new ImageIcon(image);
	}

	public static ImageIcon getIcon(String fileName, int maxWidth) {
		ImageIcon icon = getIcon(fileName);

		return checkIconSize(icon, maxWidth);
	}

	public static <T> boolean isAnyOf(T find, @SuppressWarnings("unchecked") T... args) {
		for (T search : args) {
			if (find.equals(search))
				return true;
		}
		return false;
	}
}
