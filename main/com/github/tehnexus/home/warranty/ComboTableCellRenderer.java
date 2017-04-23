package com.github.tehnexus.home.warranty;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.github.tehnexus.home.warranty.classes.Property;

public class ComboTableCellRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus,
			int row, int col) {

		if (obj instanceof Property) {
			Property property = (Property) obj;
			setText(property.getName());

			if (isSelected) {
				setBackground(table.getSelectionBackground());
			}
			else {
				setBackground(table.getSelectionForeground());
			}
		}
		return this;
	}

}