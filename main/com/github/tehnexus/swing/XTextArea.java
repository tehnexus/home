package com.github.tehnexus.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import com.github.tehnexus.awt.XFont;

public class XTextArea extends JTextArea {

	private final Border	border	= new LineBorder(new Color(171, 173, 179));

	private final Object	tag;

	public XTextArea() {
		super();
		tag = "";
		setBorder(border);
	}

	private XTextArea(Builder builder) {
		super(builder.text);
		setBorder(border);
		tag = builder.tag;
		setFont(builder.font);
		setEditable(builder.editable);
		setTabSize(4);
		setLineWrap(true);
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

		private Font			font		= XFont.FONT_DEFAULT;

		public Builder(JPanel parent) {
			this.parent = parent;
		}

		public XTextArea build() {
			return new XTextArea(this);
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
