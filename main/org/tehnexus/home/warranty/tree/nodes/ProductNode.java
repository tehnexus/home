package org.tehnexus.home.warranty.tree.nodes;

import org.tehnexus.home.warranty.classes.Product;
import org.tehnexus.home.warranty.tree.XTreeNode;

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