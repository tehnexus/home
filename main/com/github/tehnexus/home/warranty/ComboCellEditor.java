package com.github.tehnexus.home.warranty;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.github.tehnexus.home.warranty.classes.Property;

public class ComboCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

	private Property		property;
	private List<Property>	listProperty;

	public ComboCellEditor(List<Property> listProperty) {
		this.listProperty = listProperty;
	}

	@Override
	public Object getCellEditorValue() {
		return this.property;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

		if (value instanceof Property) {
			this.property = (Property) value;
		}

		JComboBox<Property> comboCountry = new JComboBox<>();

		for (Property p : listProperty) {
			comboCountry.addItem(p);
		}

		comboCountry.setSelectedItem(property);
		comboCountry.addActionListener(this);

		if (isSelected) {
			comboCountry.setBackground(table.getSelectionBackground());
		}
		else {
			comboCountry.setBackground(table.getSelectionForeground());
		}

		return comboCountry;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		@SuppressWarnings("unchecked")
		JComboBox<Property> comboProperty = (JComboBox<Property>) event.getSource();
		this.property = (Property) comboProperty.getSelectedItem();
	}

}
