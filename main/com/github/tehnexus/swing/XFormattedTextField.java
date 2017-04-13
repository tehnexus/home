package org.tehnexus.swing;

import java.awt.Font;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import org.tehnexus.awt.XFont;

public class XFormattedTextField extends JFormattedTextField {

	private final Object tag;

	private XFormattedTextField(Builder builder) {
		super(builder.formatter);
		tag = builder.tag;
		setValue(builder.value);
		setFont(builder.font);
		setEditable(builder.editable);
		builder.parent.add(this, builder.constraints);
	}

	public Object getTag() {
		return tag;
	}

	public static class Builder {

		private final AbstractFormatter	formatter;
		private final JPanel			parent;

		private Object					constraints;
		private Object					tag			= "";
		private boolean					editable	= true;
		private Object					value;
		private Font					font		= XFont.FONT_DEFAULT;

		public Builder(JPanel parent, AbstractFormatter formatter) {
			this.parent = parent;
			this.formatter = formatter;
		}

		public XFormattedTextField build() {
			return new XFormattedTextField(this);
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

		public Builder value(Object value) {
			this.value = value;
			return this;
		}

	}

}
