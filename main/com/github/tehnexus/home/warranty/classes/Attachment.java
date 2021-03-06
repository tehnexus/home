package com.github.tehnexus.home.warranty.classes;

import com.github.tehnexus.home.util.Identifier;
import com.github.tehnexus.sqlite.SQLStrings;
import com.github.tehnexus.sqlite.SQLUtil;

public class Attachment extends Property {

	private String	comment;
	private boolean	isThumb;

	private Attachment(Builder builder) {
		super(builder.id, "");
		comment = builder.comment;
		isThumb = builder.isThumb;

		setIdForeign(builder.idForeign);

		if (builder.type != null)
			setType(Identifier.ATTACHMENTTYPE, builder.type, -1);
		else
			setIdType(Identifier.ATTACHMENTTYPE, builder.idType, -1);
	}

	public String getComment() {
		return comment;
	}

	public boolean isThumb() {
		return isThumb;
	}

	public void propertyChange() {
		String sqlString = SQLStrings.updatetblAttachment();
		Object[] args = new Object[] { getType(Identifier.ATTACHMENTTYPE).get(0).getId(), getComment(), getId() };
		SQLUtil.executePreparedStatement(sqlString, args);
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setIsThumb(boolean b) {
		isThumb = b;
	}

	public static class Builder {

		private int			id;
		private String		comment	= "";
		private int			idType;
		private Property	type	= null;
		private int			idForeign;
		private boolean		isThumb	= false;

		public Builder(int id) {
			this.id = id;
		}

		public Attachment build() {
			return new Attachment(this);
		}

		public Builder comment(String comment) {
			if (comment == null)
				comment = "";
			this.comment = comment;
			return this;
		}

		public Builder idForeign(int idForeign) {
			this.idForeign = idForeign;
			return this;
		}

		public Builder thumb(boolean isThumb) {
			this.isThumb = isThumb;
			return this;
		}

		public Builder type(Property type) {
			this.type = type;
			this.idType = type.getId();
			return this;
		}

		public Builder typeId(int idType) {
			this.idType = idType;
			return this;
		}
	}
}
