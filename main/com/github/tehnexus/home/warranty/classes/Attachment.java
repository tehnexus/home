package org.tehnexus.home.warranty.classes;

import org.tehnexus.home.util.Identifier;

public class Attachment extends Property {

	private String comment = "";

	private Attachment(Builder builder) {
		super(builder.id, "");
		comment = builder.comment;

		setIdForeign(builder.idForeign);
		setIdType(Identifier.ATTACHMENTTYPE, builder.idType);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public static class Builder {

		private int		id;
		private String	comment	= "";
		private int		idType;
		private int		idForeign;

		public Builder(int id) {
			this.id = id;
		}

		public Attachment build() {
			return new Attachment(this);
		}

		public Builder comment(String comment) {
			this.comment = comment;
			return this;
		}

		public Builder idForeign(int idForeign) {
			this.idForeign = idForeign;
			return this;
		}

		public Builder typeId(int idType) {
			this.idType = idType;
			return this;
		}
	}
}
