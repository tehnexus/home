package com.github.tehnexus.home.warranty.classes;

import com.github.tehnexus.home.util.Identifier;

public class Shop extends Property {

	private String	fullname;
	private String	address;
	private String	eMail;
	private String	phone;
	private String	fax;
	private String	comment;

	private Shop(Builder builder) {
		super(builder.id, builder.name, builder.dummy);
		fullname = builder.fullname;
		address = builder.address;
		eMail = builder.eMail;
		phone = builder.phone;
		fax = builder.fax;
		comment = builder.comment;

		setIdType(Identifier.SHOPTYPE, builder.idType);
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setEmail(String eMail) {
		this.eMail = eMail;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getAddress() {
		return address;
	}

	public String getComment() {
		return comment;
	}

	public String getEmail() {
		return eMail;
	}

	public String getFax() {
		return fax;
	}

	public String getFullname() {
		return fullname;
	}

	public String getPhone() {
		return phone;
	}

	public String toLowerString() {
		return toString().toLowerCase();
	}

	public static class Builder {

		// required
		private final int	id;

		// optional
		private String		name		= "";
		private String		fullname	= "";
		private String		address		= "";
		private String		eMail		= "";
		private String		phone		= "";
		private String		fax			= "";
		private String		comment		= "";
		private boolean		dummy		= false;

		private int			idType;

		public Builder(int id) {
			this.id = id;
		}

		public Builder address(String address) {
			this.address = address;
			return this;
		}

		public Shop build() {
			return new Shop(this);
		}

		public Builder comment(String comment) {
			this.comment = comment;
			return this;
		}

		public Builder dummy(boolean flag) {
			dummy = flag;
			return this;
		}

		public Builder email(String eMail) {
			this.eMail = eMail;
			return this;
		}

		public Builder fax(String fax) {
			this.fax = fax;
			return this;
		}

		public Builder fullname(String fullname) {
			this.fullname = fullname;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder phone(String phone) {
			this.phone = phone;
			return this;
		}

		public Builder idType(int id) {
			this.idType = id;
			return this;
		}
	}

}
