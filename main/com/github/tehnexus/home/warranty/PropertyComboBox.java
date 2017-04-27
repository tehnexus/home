package com.github.tehnexus.home.warranty;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataListener;

import com.github.tehnexus.home.warranty.classes.Properties;
import com.github.tehnexus.home.warranty.classes.Property;
import com.github.tehnexus.swing.XSeparator;

public class PropertyComboBox extends JComboBox<Property> {

	private Properties source;

	public PropertyComboBox() {
		setModel(new DefaultComboBoxModel<>(new Property[] { new Property(-1, "", true) }));
		setRenderer(new ComboListCellRenderer());
		addActionListener(new ComboListener(this));
	}

	private void addNew() {
		firePropertyChange("propertyNew", null, source);
	}

	public Property getSelectedProperty() {
		return (Property) this.getSelectedItem();
	}

	public void setSource(Properties source) {
		this.source = source;

		source.put(-99, new Property(-99, XSeparator.getPlaceholder(), true));
		source.put(-88, new Property(-88, Editor.PLACEHOLDER_NEW, true));

		setModel(new ComboModel(source));
	}

	private class ComboListener implements ActionListener {

		private final PropertyComboBox	cbo;
		private Object					currentItem;

		public ComboListener(PropertyComboBox cbo) {
			this.cbo = cbo;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object newItem = cbo.getSelectedItem();

			// seperator selected
			if (XSeparator.getPlaceholder().equalsIgnoreCase(newItem.toString())) {
				cbo.setSelectedItem(currentItem);

				// new selected
			}
			else if (newItem.toString().equalsIgnoreCase(Editor.PLACEHOLDER_NEW)) {
				cbo.setSelectedItem(currentItem);
				addNew();

				// sth else selected
			}
			else {
				currentItem = newItem;
			}
		}
	}

	private class ComboModel implements MutableComboBoxModel<Property> {

		Comparator<Property>	tnc		= Comparator.comparing(Property::isDummy)
				.thenComparing(p -> p.getName().toLowerCase());

		private int				index	= 0;
		private List<Property>	values	= new ArrayList<>(0);

		public ComboModel(Properties source) {

			for (Entry<Integer, Property> e : source.entrySet()) {
				addElement(e.getValue());
			}
		}

		@Override
		public void addElement(Property item) {
			values.add(item);
			sort();
		}

		@Override
		public void addListDataListener(ListDataListener l) {
		}

		@Override
		public Property getElementAt(int ind) {
			return values.get(ind);
		}

		@Override
		public Property getSelectedItem() {
			return values.get(index);
		}

		@Override
		public int getSize() {
			return values.size();
		}

		@Override
		public void insertElementAt(Property item, int ind) {
			values.add(ind, item);
			sort();
		}

		@Override
		public void removeElement(Object obj) {
			values.remove(obj);
		}

		@Override
		public void removeElementAt(int ind) {
			values.remove(ind);
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
		}

		@Override
		public void setSelectedItem(Object obj) {
			index = values.indexOf(obj);
		}

		private void sort() {
			values.sort(tnc);
		}

	}

}
