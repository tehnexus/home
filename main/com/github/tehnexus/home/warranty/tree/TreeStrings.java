package com.github.tehnexus.home.warranty.tree;

public enum TreeStrings {

	FLTR_WAR_ACTIVE("Active"),
	FLTR_WAR_OVER("Over"),
	FLTR_OFF(""),

	GRP_BYSHOP("Shop"),
	GRP_BYMANU("Manufacturer"),
	GRP_OFF("");

	private final String text;

	TreeStrings(String text) {
		this.text = text;
	}

	public static TreeStrings getObject(String text) {
		for (TreeStrings obj : TreeStrings.values()) {
			if (obj.getText().equalsIgnoreCase(text)) {
				return obj;
			}
		}
		return null;
	}

	public String getText() {
		return this.text;
	}
}
