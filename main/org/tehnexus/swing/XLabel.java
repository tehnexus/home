package org.tehnexus.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;

import org.tehnexus.awt.XFont;

public class XLabel extends JLabel {

	private XLabel(Builder builder) {
		super(builder.text);
		this.setFont(builder.font);
		this.setLabelFor(builder.c);
		this.setBackground(builder.bg);
		this.setForeground(builder.fg);
	}

	public static class Builder {

		private final String	text;

		private Component		c;
		private Font			font	= XFont.FONT_DEFAULT;
		private Color			bg		= Color.GRAY;
		private Color			fg		= Color.BLACK;

		public Builder(String text) {
			this.text = text;
		}

		public Builder backgroud(Color bg) {
			this.bg = bg;
			return this;
		}

		public XLabel build() {
			return new XLabel(this);
		}

		public Builder font(Font font) {
			this.font = font;
			return this;
		}

		public Builder foregroud(Color fg) {
			this.fg = fg;
			return this;
		}

		public Builder labelFor(Component c) {
			this.c = c;
			return this;
		}

	}

}
