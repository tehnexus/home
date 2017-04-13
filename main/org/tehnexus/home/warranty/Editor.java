package org.tehnexus.home.warranty;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import org.tehnexus.awt.XFont;
import org.tehnexus.home.util.Identifier;
import org.tehnexus.home.util.Util;
import org.tehnexus.home.warranty.classes.Product;
import org.tehnexus.home.warranty.classes.Properties;
import org.tehnexus.sqlite.SQLStrings;
import org.tehnexus.sqlite.SQLUtil;
import org.tehnexus.sqlite.SQLiteCon;
import org.tehnexus.swing.XFormattedTextField;
import org.tehnexus.swing.XLabel;
import org.tehnexus.swing.XTextArea;
import org.tehnexus.swing.XTextField;
import org.tehnexus.swing.utils.Utils;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings.TimeIncrement;

import net.miginfocom.swing.MigLayout;

public class Editor extends JPanel {

	public static String		PLACEHOLDER_NEW		= "  add new ...";

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
	private JButton				btnAttachment		= new JButton("Attachments");

	private Product				currentTreeSelection;
	private Properties			products;

	private PropertyListener	propertyListener	= new PropertyListener();
	private ButtonListener		buttonListener		= new ButtonListener();

	public Editor() {
		createGUI();
		resetFields();
		Utils.setEnabled(Utils.getAllComponents(this), false);
		addPropertyChangeListener(propertyListener);
	}

	public void loadProduct(Object obj) {
		if (!(obj instanceof Product)) {
			resetFields();
			Utils.setEnabled(Utils.getAllComponents(this), false);
			return;
		}
		Utils.setEnabled(Utils.getAllComponents(this), true);
		currentTreeSelection = (Product) obj;

		ftxtID.setValue(currentTreeSelection.getId());
		txtName.setText(currentTreeSelection.getName());
		txtFullname.setText(currentTreeSelection.getFullname());
		txtSerial.setText(currentTreeSelection.getSerial());
		ftxtWarranty.setValue(currentTreeSelection.getWarranty());
		// System.out.println("Buy GMT: " + p.getBuyDateGMT() + "\nBuy Local: "
		// + p.getBuyDateLocal() + "\n"
		// + "Warranty End GMT: " + p.getWarrantyEndGMT() + "\nWarranty End
		// Local: " + p.getWarrantyEndLocal());
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

	public void setProperties(Properties properties) {
		this.products = properties;
		cboShop.setSource(products.getTypes(Identifier.SHOP));
		cboManu.setSource(products.getTypes(Identifier.MANUFACTURER));
		cboPay.setSource(products.getTypes(Identifier.PAYMENT));
	}

	private void createGUI() {

		setLayout(new MigLayout("", "[3px][fill][20px,grow,fill][20px,grow,fill][20px,grow,fill][20px,grow,fill][3px]",
				"[][][][][][][][][][][][][][][][][grow][][][][]"));

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
				.constraints("cell 2 2,growx").build();

		txtName = new XTextField.Builder(this).font(XFont.FONT_DEFAULT).constraints("cell 2 3 5 1,growx").build();

		txtFullname = new XTextField.Builder(this).font(XFont.FONT_DEFAULT).constraints("cell 2 4 5 1,growx").build();

		txtSerial = new XTextField.Builder(this).font(XFont.FONT_MONOSPACED).constraints("cell 2 6 5 1,growx").build();

		ftxtWarranty = new XFormattedTextField.Builder(this, formatterWarranty).value(2.0).font(XFont.FONT_MONOSPACED)
				.constraints("cell 2 8 5 1,growx").build();

		ftxtPrice = new XFormattedTextField.Builder(this, formatterPrice).value(.0).font(XFont.FONT_MONOSPACED)
				.constraints("cell 2 9 5 1,growx").build();

		txtOrder = new XTextField.Builder(this).font(XFont.FONT_MONOSPACED).constraints("cell 2 10 5 1,growx").build();

		txtInvoice = new XTextField.Builder(this).font(XFont.FONT_MONOSPACED).constraints("cell 2 11 5 1,growx")
				.build();

		txtCustomer = new XTextField.Builder(this).font(XFont.FONT_MONOSPACED).constraints("cell 2 12 5 1,growx")
				.build();

		cboShop = new PropertyComboBox();
		cboShop.addPropertyChangeListener(propertyListener);
		add(cboShop, "cell 2 13 4 1,growx");

		cboManu = new PropertyComboBox();
		cboManu.addPropertyChangeListener(propertyListener);
		add(cboManu, "cell 2 14 4 1,growx");

		cboPay = new PropertyComboBox();
		cboPay.addPropertyChangeListener(propertyListener);
		add(cboPay, "cell 2 15 4 1,growx");

		txtComment = new XTextArea.Builder(this).font(XFont.FONT_DEFAULT).constraints("cell 2 16 5 1,grow").build();

		dateSettings = new DatePickerSettings(Locale.getDefault(Locale.Category.FORMAT));
		datePicker = new DatePicker(this.dateSettings);
		add(datePicker, "cell 2 7,growx");
		timeSettings = new TimePickerSettings(Locale.getDefault(Locale.Category.FORMAT));
		timePicker = new TimePicker(this.timeSettings);
		add(timePicker, "cell 3 7,growx");

		// add buttons
		btnNew.addActionListener(buttonListener);
		add(btnNew, "cell 3 2");

		btnAttachment.addActionListener(buttonListener);
		add(btnAttachment, "cell 5 2 2 1,growx");

		btnEditShop.addActionListener(buttonListener);
		add(btnEditShop, "cell 6 13,growx");

		btnEditManufacturer.addActionListener(buttonListener);
		add(btnEditManufacturer, "cell 6 14,growx");

		btnDelete.addActionListener(buttonListener);
		add(btnDelete, "cell 1 17,grow");
		
		btnSave.addActionListener(buttonListener);
		add(btnSave, "cell 2 17 5 1,grow");

		// Field settings
		txtComment.setLineWrap(true);
		txtComment.setTabSize(4);

		dateSettings.setAllowEmptyDates(false);
		dateSettings.setAllowKeyboardEditing(false);
		datePicker.setDateToToday();
		datePicker.setFont(XFont.FONT_MONOSPACED);
		datePickerButton = datePicker.getComponentToggleCalendarButton();
		datePickerButton.setText("");

		URL dateImageURL = getClass().getClassLoader().getResource("images/datepickerbutton1.png");
		Image dateExampleImage = Toolkit.getDefaultToolkit().getImage(dateImageURL);
		ImageIcon dateExampleIcon = new ImageIcon(dateExampleImage);
		datePickerButton.setIcon(dateExampleIcon);

		timeSettings.setAllowEmptyTimes(false);
		timeSettings.setDisplaySpinnerButtons(true);
		timeSettings.use24HourClockFormat();
		timeSettings.generatePotentialMenuTimes(TimeIncrement.FifteenMinutes, null, null);
		timeSettings.setAllowKeyboardEditing(false);
		timeSettings.setInitialTimeToNow();
		timePickerButton = timePicker.getComponentToggleTimeMenuButton();
		timePickerButton.setText("");
		timePicker.setFont(XFont.FONT_MONOSPACED);

		URL timeIconURL = getClass().getClassLoader().getResource("images/timepickerbutton1.png");
		Image timeExampleImage = Toolkit.getDefaultToolkit().getImage(timeIconURL);
		ImageIcon timeExampleIcon = new ImageIcon(timeExampleImage);
		timePickerButton.setIcon(timeExampleIcon);

		btnNew.setFont(XFont.FONT_DEFAULT);
		btnSave.setFont(XFont.FONT_BTNCONFIRM_DEFAULT);

		// Create and add labels
		add(new XLabel.Builder("ID:").labelFor(this.ftxtID).build(), "cell 1 2");
		add(new XLabel.Builder("*Name:").labelFor(this.txtName).build(), "cell 1 3");
		add(new XLabel.Builder("*Description:").labelFor(this.txtFullname).build(), "cell 1 4");
		add(new XLabel.Builder("Serial:").labelFor(this.txtSerial).build(), "cell 1 6");
		add(new XLabel.Builder("Buying date:").labelFor(this.datePicker).build(), "cell 1 7");
		add(new XLabel.Builder("Warranty:").labelFor(this.ftxtWarranty).build(), "cell 1 8");
		add(new XLabel.Builder("Price:").labelFor(this.ftxtPrice).build(), "cell 1 9");
		add(new XLabel.Builder("Order:").labelFor(this.txtOrder).build(), "cell 1 10");
		add(new XLabel.Builder("Invoice:").labelFor(this.txtInvoice).build(), "cell 1 11");
		add(new XLabel.Builder("Customer:").labelFor(this.txtCustomer).build(), "cell 1 12");
		add(new XLabel.Builder("Shop:").labelFor(this.cboShop).build(), "cell 1 13");
		add(new XLabel.Builder("Manufacturer:").labelFor(this.cboManu).build(), "cell 1 14");
		add(new XLabel.Builder("Payed:").labelFor(this.cboPay).build(), "cell 1 15");
		add(new XLabel.Builder("Comment:").labelFor(this.txtComment).build(), "cell 1 16,aligny top");
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
			else if (Util.isAnyOf(ae.getSource(), btnEditShop, btnEditManufacturer, btnAttachment)) {
				firePropertyChange("propertyEdit", null, ae.getSource());
			}
		}

	}

	private class PropertyListener implements PropertyChangeListener {

		@SuppressWarnings("incomplete-switch")
		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			switch (pce.getPropertyName()) {

			case "propertyEdit":
				if (pce.getNewValue().equals(btnAttachment)) {
					AttachmentTable attEdit = new AttachmentTable(products, currentTreeSelection);
					attEdit.setVisible(true);
				}

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
					System.out.println("SHOP");
					break;
				case PAYMENT:
					System.out.println("PAYMENT");
					break;
				case MANUFACTURER:
					System.out.println("MANUFACTURER");
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
				// System.out.println(pce.getPropertyName());
				break;
			}
		}

	}

}
