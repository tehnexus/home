package com.github.tehnexus.awt;

import java.awt.Font;

public class XFont extends Font {

	public static Font	FONT_DEFAULT			= new Font("Tahoma", Font.PLAIN, 12);
	public static Font	FONT_DIALOG_TITLE		= new Font("Tahoma", Font.BOLD, 14);
	public static Font	FONT_MONOSPACED			= new Font("Courier New", Font.PLAIN, 13);
	public static Font	FONT_BTNCONFIRM_DEFAULT	= new Font("Tahoma", Font.BOLD, 13);

	protected XFont(Font font) {
		super(font);
	}

}
