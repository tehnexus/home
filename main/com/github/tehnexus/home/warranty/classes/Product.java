package com.github.tehnexus.home.warranty.classes;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.github.tehnexus.home.util.Identifier;

public class Product extends Property {

	private static ZoneId	zoneGMT	= ZoneId.of("GMT");

	private String			fullname;
	private String			serial;
	private Instant			instantBuy;
	private double			warranty;
	private double			price;
	private String			order;
	private String			invoice;
	private String			customer;
	private String			comment;

	private ZonedDateTime	dateBuyGMT;
	private ZonedDateTime	dateBuyLocal;
	private ZonedDateTime	dateWarEndGMT;
	private ZonedDateTime	dateWarEndLocal;

	// private HashMap<Long, Attachment> attachments;

	private Product(Builder builder) {
		super(builder.id, builder.name, false);
		fullname = builder.fullname;
		serial = builder.serial;
		instantBuy = builder.instantBuy;
		warranty = builder.warranty;
		price = builder.price;
		order = builder.order;
		invoice = builder.invoice;
		customer = builder.customer;
		comment = builder.comment;

		setIdType(Identifier.SHOP, builder.idShop);
		setIdType(Identifier.MANUFACTURER, builder.idManufacturer);
		setIdType(Identifier.PAYMENT, builder.idPay);

		computeDates();
	}

	public ZonedDateTime getBuyDateGMT() {
		return dateBuyGMT;
	}

	public ZonedDateTime getBuyDateLocal() {
		return dateBuyLocal;
	}

	public String getComment() {
		return this.comment;
	}

	public String getCustomer() {
		return customer;
	}

	public String getFullname() {
		return fullname;
	}

	public String getInvoice() {
		return invoice;
	}

	public String getOrder() {
		return order;
	}

	public double getPrice() {
		return price;
	}

	public String getSerial() {
		return serial;
	}

	public double getWarranty() {
		return warranty;
	}

	public ZonedDateTime getWarrantyEndGMT() {
		return dateWarEndGMT;
	}

	public ZonedDateTime getWarrantyEndLocal() {
		return dateWarEndLocal;
	}

	public boolean isWarrantyCovered() {
		ZonedDateTime nowGMT = Instant.now().atZone(zoneGMT);
		return nowGMT.isBefore(dateWarEndGMT);
	}

	public void print() {
		System.out.printf(
				"ID: %d%nName: %s%nFullname: %s%nSerial: %s%nBought: %td.%tm.%tY - %tl:%tM%nWarranty: %s%n"
						+ "Warranty ends: %td.%tm.%tY - %tl:%tM%nCovered: %s%nPrice: €%8.2f%nOrder: %s%nInvoice: %s%nCustomer: %s%n",
				getId(), getName(), fullname, serial, instantBuy, instantBuy, instantBuy, instantBuy, instantBuy,
				warranty, dateWarEndGMT, dateWarEndGMT, dateWarEndGMT, dateWarEndGMT, dateWarEndGMT,
				isWarrantyCovered(), price, order, invoice, customer);
	}

	public void setValues(String name, String fullname, String serial, long date, Object warranty, Object price,
			String order, String invoice, String customer, String comment) {

		setName(name);
		this.fullname = fullname;
		this.serial = serial;
		this.instantBuy = Instant.ofEpochSecond(date);
		this.warranty = (double) warranty;
		this.price = (double) price;
		this.order = order;
		this.invoice = invoice;
		this.customer = customer;
		this.comment = comment;

		computeDates();
	}

	@Override
	public String toString() {
		return fullname + " (" + getName() + ")";
	}

	private void computeDates() {
		ZoneId zoneLocal = ZoneId.systemDefault();

		dateBuyGMT = Instant.ofEpochSecond(instantBuy.getEpochSecond()).atZone(zoneGMT);

		dateBuyLocal = dateBuyGMT.withZoneSameInstant(zoneLocal);

		int years = (int) warranty;
		dateWarEndGMT = dateBuyGMT.plusYears(years);
		dateWarEndLocal = dateBuyLocal.plusYears(years);

		if (warranty % 1 != 0) { // double has no decimals -> add days
			double dec = warranty - years;
			long months = (long) (12 * dec);
			dateWarEndGMT = dateWarEndGMT.plusMonths(months);
			dateWarEndLocal = dateWarEndLocal.plusMonths(months);
		}

	}

	public static class Builder {

		private int		id				= 0;
		private String	name			= "";
		private String	fullname		= "";
		private String	serial			= "";
		private Instant	instantBuy;
		private double	warranty		= 0;
		private double	price			= 0;
		private String	order			= "";
		private String	invoice			= "";
		private String	customer		= "";
		private String	comment			= "";
		private int		idManufacturer	= 0;
		private int		idShop			= 0;
		private int		idPay			= 0;

		public Builder(int id) {
			this.id = id;
		}

		public Product build() {
			return new Product(this);
		}

		public Builder comment(String comment) {
			this.comment = comment;
			return this;
		}

		public Builder customer(String customer) {
			this.customer = customer;
			return this;
		}

		public Builder date(long date) {
			this.instantBuy = Instant.ofEpochSecond(date);
			return this;
		}

		public Builder fullname(String fullname) {
			this.fullname = fullname;
			return this;
		}

		public Builder idManu(int idManufacturer) {
			this.idManufacturer = idManufacturer;
			return this;
		}

		public Builder idPay(int idPay) {
			this.idPay = idPay;
			return this;
		}

		public Builder idShop(int idShop) {
			this.idShop = idShop;
			return this;
		}

		public Builder invoice(String invoice) {
			this.invoice = invoice;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder order(String order) {
			this.order = order;
			return this;
		}

		public Builder price(double price) {
			this.price = price;
			return this;
		}

		public Builder serial(String serial) {
			this.serial = serial;
			return this;
		}

		public Builder warranty(double warranty) {
			this.warranty = warranty;
			return this;
		}

	}

}