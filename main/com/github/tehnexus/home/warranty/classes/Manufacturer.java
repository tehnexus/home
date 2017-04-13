package org.tehnexus.home.warranty.classes;

public class Manufacturer extends Property {

	private String	fullname;
	private String	address;
	private String	url;
	private String	email;
	private String	comment;

	public Manufacturer(Builder builder) {
		super(builder.id, builder.name, builder.dummy);
		fullname = builder.fullname;
		url = builder.url;
		address = builder.address;
		email = builder.email;
		comment = builder.comment;
	}

	public String getAddress() {
		return address;
	}

	public String getComment() {
		return comment;
	}

	public String getEmail() {
		return email;
	}

	public String getFullname() {
		return fullname;
	}

	public String getUrl() {
		return url;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String toLowerString() {
		return toString().toLowerCase();
	}

	public static class Builder {

		// required
		private final int	id;

		// optional
		private String		name;
		private String		fullname;
		private String		address;
		private String		url;
		private String		email;
		private String		comment;
		private boolean		dummy	= false;

		public Builder(int id) {
			this.id = id;
		}

		public Builder address(String address) {
			this.address = address;
			return this;
		}

		public Manufacturer build() {
			return new Manufacturer(this);
		}

		public Builder comment(String comment) {
			this.comment = comment;
			return this;
		}

		public Builder dummy(boolean flag) {
			dummy = flag;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
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

		public Builder url(String url) {
			this.url = url;
			return this;
		}
	}
}
