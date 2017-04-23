package com.github.tehnexus.home.warranty;

import java.awt.Component;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.github.tehnexus.home.warranty.classes.Property;

public class ComboCellEditor extends AbstractCellEditor implements TableCellEditor {

	private final List<Property>	listProperty;
	private final ItemListener		itemListener;

	private Property				property;

	public ComboCellEditor(List<Property> listProperty, ItemListener itemListener) {
		this.listProperty = listProperty;
		this.itemListener = itemListener;
	}

	@Override
	public Property getCellEditorValue() {
		return property;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

		if (value instanceof Property) {
			this.property = (Property) value;
		}

		JComboBox<Property> combo = new JComboBox<>();

		for (Property p : listProperty) {
			combo.addItem(p);
		}

		combo.setSelectedItem(property);
		combo.addItemListener(itemListener);

		if (isSelected) {
			combo.setBackground(table.getSelectionBackground());
		}
		else {
			combo.setBackground(table.getSelectionForeground());
		}
		return combo;
	}
}