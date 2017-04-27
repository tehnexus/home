package com.github.tehnexus.swing;

import java.awt.Color;

import javax.swing.JSeparator;

public class XSeparator extends JSeparator {

	public static String getPlaceholder() {
		return "          ";
	}

	private final Color	bg	= Color.WHITE;

	private final Color	fg	= Color.LIGHT_GRAY;

	public XSeparator() {
		setBackground(bg);
		setForeground(fg);
	}

	public XSeparator(Color bg, Color fg) {
		setBackground(bg);
		setForeground(fg);
	}

}
