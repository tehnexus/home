package com.github.tehnexus.home.warranty;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings.TimeIncrement;
import com.github.tehnexus.awt.XFont;
import com.github.tehnexus.home.util.Identifier;
import com.github.tehnexus.home.util.Util;
import com.github.tehnexus.home.warranty.classes.Product;
import com.github.tehnexus.home.warranty.classes.Properties;
import com.github.tehnexus.sqlite.SQLStrings;
import com.github.tehnexus.sqlite.SQLUtil;
import com.github.tehnexus.sqlite.SQLiteCon;
import com.github.tehnexus.swing.XFormattedTextField;
import com.github.tehnexus.swing.XLabel;
import com.github.tehnexus.swing.XTextArea;
import com.github.tehnexus.swing.XTextField;
import net.miginfocom.swing.MigLayout;

public class Editor extends JPanel {

	public final static String	PLACEHOLDER_NEW		= "  add new ...";

	private XFormattedTextField	ftxtID;
	private XTextField			txtFullname;
	private XTextField			txtName;
	private XTextField			txtSerial;
	private XFormattedTextField	ftxtWarranty;
	private DatePickerSettings	dateSettings;
	private DatePicker			datePicker;
	private TimePickerSettings	timeSettings;
	private TimePicker			timePicker;
	private XFormattedTextField	ftxtPrice;
	private XTextField			txtOrder;
	private XTextField			txtInvoice;
	private XTextField			txtCustomer;
	private PropertyComboBox	cboShop;
	private PropertyComboBox	cboManu;
	private PropertyComboBox	cboPay;
	private XTextArea			txtComment;

	private JButton				datePickerButton;
	private JButton				timePickerButton;
	private JButton				btnEditShop			= new JButton("Edit");
	private JButton				btnEditManufacturer	= new JButton("Edit");
	private JButton				btnSave				= new JButton("Save");
	private JButton				btnNew				= new JButton("New");
	private JButton				btnDelete			= new JButton("Delete");

	private Product				currentTreeSelection;
	private Properties			products;

	private PropertyListener	propertyListener	= new PropertyListener();
	private ButtonListener		buttonListener		= new ButtonListener();
	private AttachmentTableView	attachTableView		= new AttachmentTableView();

	public Editor() {
		setVisible(false);
		createGUI();
		resetFields();
		com.github.tehnexus.swing.Util.setEnabled(com.github.tehnexus.swing.Util.getAllComponents(this), false);
		addPropertyChangeListener(propertyListener);
	}

	private void createGUI() {

		setLayout(new MigLayout("",
				"[3px][fill][20px,grow,fill][20px,grow,fill][20px,grow,fill][20px,grow,fill][3px][250px:250px,grow,fill]",
				"[][][][][][][][][][][][][][grow][]"));

		// formatter for id field
		MaskFormatter formatterID = null;
		try {
			formatterID = new MaskFormatter("####");
		}
		catch (ParseException e) {
			System.err.println("formatterID is bad: " + e.getMessage());
		}

		// formatter for warranry field
		MaskFormatter formatterWarranty = null;
		try {
			formatterWarranty = new MaskFormatter("#");
		}
		catch (ParseException e) {
			System.err.println("formatterWarranty is bad: " + e.getMessage());
		}
		// formatterWarranty.setAllowsInvalid(false);
		formatterWarranty.setOverwriteMode(true);

		// formatter for price field
		NumberFormat formatPrice = NumberFormat.getCurrencyInstance(Locale.GERMANY);
		formatPrice.setMaximumIntegerDigits(6);
		formatPrice.setMaximumFractionDigits(2);
		NumberFormatter formatterPrice = new NumberFormatter(formatPrice);
		formatterPrice.setAllowsInvalid(false);
		formatterPrice.setOverwriteMode(true);

		// Create and add fields
		ftxtID = new XFormattedTextField.Builder(this, formatterID).editable(false).font(XFont.FONT_MONOSPACED)
				.constraints("cell 2 0,growx").build();

		txtName = new XTextField.Builder(this).font(XFont.FONT_DEFAULT).constraints("cell 2 1 5 1,growx").build();

		txtFullname = new XTextField.Builder(this).font(XFont.FONT_DEFAULT).constraints("cell 2 2 5 1,growx").build();

		txtSerial = new XTextField.Builder(this).font(XFont.FONT_MONOSPACED).constraints("cell 2 3 5 1,growx").build();

		ftxtWarranty = new XFormattedTextField.Builder(this, formatterWarranty).value(2.0).font(XFont.FONT_MONOSPACED)
				.constraints("cell 2 5 5 1,growx").build();

		ftxtPrice = new XFormattedTextField.Builder(this, formatterPrice).value(.0).font(XFont.FONT_MONOSPACED)
				.constraints("cell 2 6 5 1,growx").build();

		txtOrder = new XTextField.Builder(this).font(XFont.FONT_MONOSPACED).constraints("cell 2 7 5 1,growx").build();

		txtInvoice = new XTextField.Builder(this).font(XFont.FONT_MONOSPACED).constraints("cell 2 8 5 1,growx").build();

		txtCustomer = new XTextField.Builder(this).font(XFont.FONT_MONOSPACED).constraints("cell 2 9 5 1,growx")
				.build();

		cboShop = new PropertyComboBox();
		cboShop.addPropertyChangeListener(propertyListener);
		add(cboShop, "cell 2 10 4 1,growx");

		// add buttons
		btnNew.addActionListener(buttonListener);
		add(btnNew, "cell 3 0");
		btnNew.setFont(XFont.FONT_DEFAULT);

		cboManu = new PropertyComboBox();
		cboManu.addPropertyChangeListener(propertyListener);
		add(cboManu, "cell 2 11 4 1,growx");

		cboPay = new PropertyComboBox();
		cboPay.addPropertyChangeListener(propertyListener);
		add(cboPay, "cell 2 12 4 1,growx");

		txtComment = new XTextArea.Builder(this).font(XFont.FONT_DEFAULT).constraints("cell 2 13 5 1,grow").build();

		dateSettings = new DatePickerSettings(Locale.getDefault(Locale.Category.FORMAT));
		datePicker = new DatePicker(this.dateSettings);
		add(datePicker, "cell 2 4,growx");
		timeSettings = new TimePickerSettings(Locale.getDefault(Locale.Category.FORMAT));
		timePicker = new TimePicker(this.timeSettings);
		add(timePicker, "cell 3 4,growx");

		btnEditShop.addActionListener(buttonListener);
		add(btnEditShop, "cell 6 10,growx");

		btnEditManufacturer.addActionListener(buttonListener);
		add(btnEditManufacturer, "cell 6 11,growx");

		btnDelete.addActionListener(buttonListener);
		add(btnDelete, "cell 1 14,grow");

		btnSave.addActionListener(buttonListener);
		add(btnSave, "cell 2 14 5 1,grow");

		add(attachTableView, "cell 7 0 1 15,grow");

		// Field settings
		txtComment.setLineWrap(true);
		txtComment.setTabSize(4);

		dateSettings.setAllowEmptyDates(false);
		dateSettings.setAllowKeyboardEditing(false);
		datePicker.setDateToToday();
		datePicker.setFont(XFont.FONT_MONOSPACED);
		datePickerButton = datePicker.getComponentToggleCalendarButton();
		datePickerButton.setText(null);
		datePickerButton.setIcon(Util.getIcon("images/datepickerbutton1.png", -1));

		timeSettings.setAllowEmptyTimes(false);
		timeSettings.setDisplaySpinnerButtons(true);
		timeSettings.use24HourClockFormat();
		timeSettings.generatePotentialMenuTimes(TimeIncrement.FifteenMinutes, null, null);
		timeSettings.setAllowKeyboardEditing(false);
		timeSettings.setInitialTimeToNow();
		timePickerButton = timePicker.getComponentToggleTimeMenuButton();
		timePickerButton.setText(null);
		timePickerButton.setIcon(Util.getIcon("images/timepickerbutton1.png", -1));
		timePicker.setFont(XFont.FONT_MONOSPACED);
		btnSave.setFont(XFont.FONT_BTNCONFIRM_DEFAULT);

		// Create and add labels
		add(new XLabel.Builder("ID:").labelFor(this.ftxtID).build(), "cell 1 0");
		add(new XLabel.Builder("*Name:").labelFor(this.txtName).build(), "cell 1 1");
		add(new XLabel.Builder("*Description:").labelFor(this.txtFullname).build(), "cell 1 2");
		add(new XLabel.Builder("Serial:").labelFor(this.txtSerial).build(), "cell 1 3");
		add(new XLabel.Builder("Buying date:").labelFor(this.datePicker).build(), "cell 1 4");
		add(new XLabel.Builder("Warranty:").labelFor(this.ftxtWarranty).build(), "cell 1 5");
		add(new XLabel.Builder("Price:").labelFor(this.ftxtPrice).build(), "cell 1 6");
		add(new XLabel.Builder("Order:").labelFor(this.txtOrder).build(), "cell 1 7");
		add(new XLabel.Builder("Invoice:").labelFor(this.txtInvoice).build(), "cell 1 8");
		add(new XLabel.Builder("Customer:").labelFor(this.txtCustomer).build(), "cell 1 9");
		add(new XLabel.Builder("Shop:").labelFor(this.cboShop).build(), "cell 1 10");
		add(new XLabel.Builder("Manufacturer:").labelFor(this.cboManu).build(), "cell 1 11");
		add(new XLabel.Builder("Payed:").labelFor(this.cboPay).build(), "cell 1 12");
		add(new XLabel.Builder("Comment:").labelFor(this.txtComment).build(), "cell 1 13,aligny top");
	}

	private void dbOperationDelete() {
		String sql1 = SQLStrings.deleteFromtblProduct();
		String sql2 = SQLStrings.deleteFromtblProductProperties();

		Object[] args = new Object[] { currentTreeSelection.getId() };

		products.remove(currentTreeSelection);

		firePropertyChange("sqlDataChange", null, true);

		try (SQLiteCon connectionSQLite = new SQLiteCon(SQLUtil.defaultDatabaseLocation())) {
			connectionSQLite.executePreparedStatement(sql1, args);
			connectionSQLite.executePreparedStatement(sql2, args);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void dbOperationInsert() {

		double warranty = Double.parseDouble(String.valueOf(ftxtWarranty.getValue()));
		double price = Double.parseDouble(String.valueOf(ftxtPrice.getValue()));

		ZonedDateTime local = ZonedDateTime.of(datePicker.getDate(), timePicker.getTime(), ZoneId.systemDefault());
		ZonedDateTime gmt = local.withZoneSameInstant(ZoneId.of("GMT"));

		// create new product
		Product p = new Product.Builder((int) ftxtID.getValue()).name(txtName.getText()).fullname(txtFullname.getText())
				.serial(txtSerial.getText()).date(gmt.toEpochSecond()).warranty(warranty).price(price)
				.order(txtOrder.getText()).invoice(txtInvoice.getText()).customer(txtCustomer.getText()).build();

		p.setType(Identifier.SHOP, cboShop.getSelectedProperty(), -1);
		p.setType(Identifier.MANUFACTURER, cboManu.getSelectedProperty(), -1);
		p.setType(Identifier.PAYMENT, cboPay.getSelectedProperty(), -1);

		products.put(p.getId(), p);

		String sql1 = SQLStrings.insertIntotblProduct();
		String sql2 = SQLStrings.insertIntotblProductProperties();

		Object[] args1 = new Object[] { p.getId(), txtName.getText(), txtFullname.getText(), txtSerial.getText(),
				gmt.toEpochSecond(), warranty, price, txtOrder.getText(), txtInvoice.getText(), txtCustomer.getText() };

		Object[] args2 = new Object[] { p.getId(), cboManu.getSelectedProperty().getId(),
				cboShop.getSelectedProperty().getId(), cboPay.getSelectedProperty().getId() };

		firePropertyChange("sqlDataChange", null, true);

		try (SQLiteCon connectionSQLite = new SQLiteCon(SQLUtil.defaultDatabaseLocation())) {
			connectionSQLite.executePreparedStatement(sql1, args1);
			connectionSQLite.executePreparedStatement(sql2, args2);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void dbOperationUpdate() {

		ZonedDateTime local = ZonedDateTime.of(datePicker.getDate(), timePicker.getTime(), ZoneId.systemDefault());
		ZonedDateTime gmt = local.withZoneSameInstant(ZoneId.of("GMT"));

		// manipulate product
		Product p = (Product) products.get((int) ftxtID.getValue());
		p.setValues(txtName.getText(), txtFullname.getText(), txtSerial.getText(), gmt.toEpochSecond(),
				ftxtWarranty.getValue(), ftxtPrice.getValue(), txtOrder.getText(), txtInvoice.getText(),
				txtCustomer.getText(), txtComment.getText());

		p.setType(Identifier.SHOP, cboShop.getSelectedProperty(), 0);
		p.setType(Identifier.MANUFACTURER, cboManu.getSelectedProperty(), 0);
		p.setType(Identifier.PAYMENT, cboPay.getSelectedProperty(), 0);

		String sql1 = SQLStrings.updatetblProduct();
		String sql2 = SQLStrings.updatetblProductProperties();

		Object[] args1 = new Object[] { p.getName(), p.getFullname(), p.getSerial(), gmt.toEpochSecond(),
				p.getWarranty(), p.getPrice(), p.getOrder(), p.getInvoice(), p.getCustomer(), p.getComment(),
				p.getId() };

		Object[] args2 = new Object[] { p.getType(Identifier.MANUFACTURER).get(0).getId(),
				p.getType(Identifier.SHOP).get(0).getId(), p.getType(Identifier.PAYMENT).get(0).getId(), p.getId() };

		try (SQLiteCon connectionSQLite = new SQLiteCon(SQLUtil.defaultDatabaseLocation())) {
			connectionSQLite.executePreparedStatement(sql1, args1);
			connectionSQLite.executePreparedStatement(sql2, args2);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadProduct(Object obj) {
		if (!(obj instanceof Product)) {
			resetFields();
			com.github.tehnexus.swing.Util.setEnabled(com.github.tehnexus.swing.Util.getAllComponents(this), false);
			return;
		}
		com.github.tehnexus.swing.Util.setEnabled(com.github.tehnexus.swing.Util.getAllComponents(this), true);
		currentTreeSelection = (Product) obj;
		
		this.attachTableView.buildTable(currentTreeSelection);
		
		ftxtID.setValue(currentTreeSelection.getId());
		txtName.setText(currentTreeSelection.getName());
		txtFullname.setText(currentTreeSelection.getFullname());
		txtSerial.setText(currentTreeSelection.getSerial());
		ftxtWarranty.setValue(currentTreeSelection.getWarranty());
		datePicker.setDate(currentTreeSelection.getBuyDateLocal().toLocalDate());
		timePicker.setTime(currentTreeSelection.getBuyDateLocal().toLocalTime());
		ftxtPrice.setValue(currentTreeSelection.getPrice());
		txtOrder.setText(currentTreeSelection.getOrder());
		txtInvoice.setText(currentTreeSelection.getInvoice());
		txtCustomer.setText(currentTreeSelection.getCustomer());
		txtComment.setText(currentTreeSelection.getComment());

		cboShop.setSelectedItem(currentTreeSelection.getType(Identifier.SHOP).get(0));
		cboManu.setSelectedItem(currentTreeSelection.getType(Identifier.MANUFACTURER).get(0));
		cboPay.setSelectedItem(currentTreeSelection.getType(Identifier.PAYMENT).get(0));
	}

	private void resetFields() {
		ftxtID.setValue(0);
		txtName.setText(null);
		txtFullname.setText(null);
		txtSerial.setText(null);
		ftxtWarranty.setValue(2.0);
		datePicker.setDateToToday();
		timePicker.setTimeToNow();
		ftxtPrice.setValue(.0);
		txtOrder.setText(null);
		txtInvoice.setText(null);
		cboShop.setSelectedIndex(0);
		cboManu.setSelectedIndex(0);
		cboPay.setSelectedIndex(0);
		txtCustomer.setText(null);
		txtComment.setText(null);
	}

	public void initialize(Properties properties) {
		products = properties;
		attachTableView.initialize(properties);

		cboShop.setSource(products.getTypes(Identifier.SHOP));
		cboManu.setSource(products.getTypes(Identifier.MANUFACTURER));
		cboPay.setSource(products.getTypes(Identifier.PAYMENT));
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(btnSave)) {
				if (products.containsKey((int) ftxtID.getValue())) // update
					dbOperationUpdate();
				else
					dbOperationInsert();
			}
			else if (ae.getSource().equals(btnNew)) {
				resetFields();
				ftxtID.setValue(products.getNewId());
			}
			else if (ae.getSource().equals(btnDelete)) {
				dbOperationDelete();
			}
		}
	}

	private class PropertyListener implements PropertyChangeListener {

		@SuppressWarnings("incomplete-switch")
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			switch (pce.getPropertyName()) {

				case "propertyEdit":
					// if (pce.getNewValue().equals(btnAttachment)) {
					// AttachmentTableView attEdit = new
					// AttachmentTableView(products, currentTreeSelection);
					// attEdit.setVisible(true);
					// }

					// propEdit = new PropertyEditor(cboManu.getSelected());
					// propEdit.setVisible(true);
					// // next line is only run is dialog is closed
					// if (!propEdit.isWindowExited())
					// cboManu.setSelectedProductProperty(propEdit.getProductProperty());
					break;

				case "propertyNew":
					Properties props = (Properties) pce.getNewValue();
					switch (props.getIdentifier()) {
						case SHOP:

							break;
						case PAYMENT:

							break;
						case MANUFACTURER:

							break;
					}

					PropertyEditor propEdit = new PropertyEditor(props);
					propEdit.setVisible(true);
					// next line is only run is dialog is closed
					if (!propEdit.isWindowExited()) {
						// cboShop.addProductProperty(propEdit.getProductProperty());
						// cboShop.setSelectedProductProperty(propEdit.getProductProperty());
						cboShop.repaint();
					}

					break;

				default:
					break;
			}
		}

	}

}
