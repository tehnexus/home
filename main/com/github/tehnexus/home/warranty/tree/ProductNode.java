package com.github.tehnexus.home.warranty.tree;

import com.github.tehnexus.home.warranty.classes.Product;

public final class ProductNode extends XTreeNode {

	private final Product obj;

	public ProductNode(Product obj) {
		this.obj = obj;
		setAllowsChildren(false);
	}

	public Product getProduct() {
		return obj;
	}

	@Override
	public String toString() {
		return obj.getFullname() + " (" + obj.getName() + ")";
	}

}