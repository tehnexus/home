package com.github.tehnexus.home.util;

public enum Identifier {

	SIMPLETYPE(0),
	PRODUCT(1),
	SHOP(11),
	SHOPTYPE(111),
	MANUFACTURER(12),
	PAYMENT(13),
	ATTACHMENT(14),
	ATTACHMENTTYPE(141);

	private int index;

	Identifier(int index) {
		this.index = index;
	}

	public int index() {
		return index;
	}
}
