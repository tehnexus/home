package com.github.tehnexus.home.warranty;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.text.MaskFormatter;

import com.github.tehnexus.awt.XFont;
import com.github.tehnexus.home.warranty.classes.Properties;
import com.github.tehnexus.home.warranty.classes.Property;
import com.github.tehnexus.swing.XFormattedTextField;
import com.github.tehnexus.swing.XLabel;

import net.miginfocom.swing.MigLayout;

public class PropertyEditor extends JDialog {

	private static boolean validateFields() {

		return true;
	}

	private JPanel					pan;
	private XFormattedTextField		ftxtID;

	private HashMap<String, Object>	fields				= new HashMap<>(0);
	private boolean					isNew;
	private boolean					isWindowExited;
	private Properties				properties;

	private Property				property;
	private PropertyListener		propertyListener	= new PropertyListener();
	private ButtonListener			buttonListener		= new ButtonListener();

	private DialogWindowListener	windowListener		= new DialogWindowListener();

	private JButton					btnSave				= new JButton("Save");

	// public PropertyEditor() {
	// createAndShowGUI();
	// }

	public PropertyEditor(Properties properties) {
		this.properties = properties;
		isNew = true;

		createAndShowGUI();
		ftxtID.setValue(properties.getNewId());
	}

	public PropertyEditor(Property property) {
		this.property = property;
		isNew = false;

		createAndShowGUI();
		loadRecord(property);
	}

	private void createAndShowGUI() {
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setMinimumSize(new Dimension(500, 600));
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		addWindowListener(windowListener);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		pan = new JPanel();

		// formatter for id field
		MaskFormatter formatterID = null;
		try {
			formatterID = new MaskFormatter("####");
		}
		catch (ParseException e) {
			System.err.println("formatterID is bad: " + e.getMessage());
		}

		// Create and add fields

		pan.setLayout(new MigLayout("", "[fill][grow,fill]", "[][]"));
		//
		ftxtID = new XFormattedTextField.Builder(pan, formatterID).editable(false).font(XFont.FONT_MONOSPACED)
				.constraints("cell 1 0,growx").build();
		pan.add(new XLabel.Builder("ID:").labelFor(ftxtID).build(), "cell 0 0");

		// add buttons
		pan.add(btnSave, "cell 1 9");
		btnSave.addActionListener(buttonListener);
		btnSave.setFont(XFont.FONT_BTNCONFIRM_DEFAULT);

		add(pan);
	}

	public boolean isNew() {
		return isNew;
	}

	public boolean isWindowExited() {
		return isWindowExited;
	}

	public void loadRecord(Property pp) {
		ftxtID.setValue(pp.getId());

		for (Entry<String, Object> entry : fields.entrySet()) {
			String key = entry.getKey();
			Object field = entry.getValue();

			// if (field instanceof XTextField) {
			// XTextField txt = (XTextField) field;
			// String value = (String) pp.getPropertyValue(key);
			// txt.setText(value);
			//
			// } else if (field instanceof XTextArea) {
			// XTextArea area = (XTextArea) field;
			// String value = (String) pp.getPropertyValue(key);
			// area.setText(value);
			//
			// } else if (field instanceof PropertyComboBox) {
			// PropertyComboBox cbo = (PropertyComboBox) field;
			// Property value = (Property) pp.getPropertyValue(key);
			//// cbo.setSelectedProductProperty(value);
			// }

		}
	}

	private void saveShopToDatabase() {

		// try (SQLiteCon sqliteCon = new
		// SQLiteCon(SqlUtils.getDefaultDatabaseLocation())) {

		// long id = (long) ftxtID.getValue();
		// String name = txtName.getText();
		// String fullname = txtFullname.getText();
		// String address = txtAddress.getText();
		// String eMail = txtEmail.getText();
		// String phone = txtPhone.getText();
		// String fax = txtFax.getText();
		// String comment = txtComment.getText();
		// ProductProperty type = cboShopType.getSelectedProductProperty();

		// Object[] args = new Object[] {};
		// if (isNew) {

		// shop = new
		// Shop.Builder(id).name(name).fullname(fullname).address(address).eMail(eMail).phone(phone)
		// .fax(fax).comment(comment).build();
		//
		// shop.setShopType(type);
		//
		// args = new Object[] { shop.getId(), shop.getName(),
		// shop.getFullname(), shop.getAddress(),
		// shop.getEmail(), shop.getPhone(), shop.getFax(),
		// shop.getComment() };
		// sqliteCon.executePreparedStatement(SqlUtils.Strings.insertIntotblShop(),
		// args);
		//
		// args = new Object[] { shop.getId(), shop.getTypeId() };
		// sqliteCon.executePreparedStatement(SqlUtils.Strings.insertIntotblShopProperties(),
		// args);

		// } else {

		// shop.setName(name);
		// shop.setFullname(fullname);
		// shop.setAddress(address);
		// shop.setEmail(eMail);
		// shop.setPhone(phone);
		// shop.setFax(fax);
		// shop.setComment(comment);
		// shop.setShopType(type);

		// }

		// } catch (SQLException e) {
		// e.printStackTrace();
		// }

	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(btnSave)) {
				if (!validateFields())
					return;

				saveShopToDatabase();
				isWindowExited = false;
				dispose();
			}
		}
	}

	private class DialogWindowListener implements WindowListener {

		@Override
		public void windowActivated(WindowEvent we) {
		}

		@Override
		public void windowClosed(WindowEvent we) {
		}

		@Override
		public void windowClosing(WindowEvent we) {
			isWindowExited = true;
			dispose();
		}

		@Override
		public void windowDeactivated(WindowEvent we) {
		}

		@Override
		public void windowDeiconified(WindowEvent we) {
		}

		@Override
		public void windowIconified(WindowEvent we) {
		}

		@Override
		public void windowOpened(WindowEvent we) {
		}

	}

	private class PropertyListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// TODO Auto-generated method stub

		}

	}
}
