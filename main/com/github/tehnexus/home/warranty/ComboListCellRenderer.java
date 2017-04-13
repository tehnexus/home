package org.tehnexus.home.warranty;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import org.tehnexus.home.warranty.classes.Property;
import org.tehnexus.swing.XSeparator;

public class ComboListCellRenderer extends JLabel implements ListCellRenderer<Property> {

	public ComboListCellRenderer() {
		setOpaque(true);
		setBorder(new EmptyBorder(1, 1, 1, 1));
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Property> list, Property p, int index,
			boolean selected, boolean hasFocus) {

		if (selected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setFont(list.getFont());

		setText(" " + p.getName());

		if (p.isDummy() && p.getName().equals(XSeparator.getPlaceholder())) {
			return new XSeparator(Color.WHITE, Color.GRAY);
		}

		return this;
	}
}