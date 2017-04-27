package com.github.tehnexus.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.tehnexus.awt.XFont;

public class XTextField extends JTextField {

	private final Object tag;

	private XTextField(Builder builder) {
		super(builder.text);
		tag = builder.tag;
		this.setBackground(builder.background);
		this.setForeground(builder.foreground);
		setFont(builder.font);
		setEditable(builder.editable);
		if (builder.parent != null)
			builder.parent.add(this, builder.constraints);
	}

	public Object getTag() {
		return tag;
	}

	public static class Builder {

		private final JPanel	parent;

		private Object			constraints;
		private String			text		= "";
		private Object			tag			= "";
		private boolean			editable	= true;
		private Color			background	= Color.WHITE;
		private Color			foreground	= Color.BLACK;

		private Font			font		= XFont.FONT_DEFAULT;
		
		public Builder(JPanel parent) {
			this.parent = parent;
		}

		public Builder background(Color bg) {
			background = bg;
			return this;
		}

		public XTextField build() {
			return new XTextField(this);
		}

		public Builder constraints(Object constraints) {
			this.constraints = constraints;
			return this;
		}

		public Builder editable(boolean editable) {
			this.editable = editable;
			return this;
		}

		public Builder font(Font font) {
			this.font = font;
			return this;
		}

		public Builder foreground(Color fg) {
			foreground = fg;
			return this;
		}

		public Builder tag(Object tag) {
			this.tag = tag;
			return this;
		}

		public Builder text(String text) {
			this.text = text;
			return this;
		}
	}
}
