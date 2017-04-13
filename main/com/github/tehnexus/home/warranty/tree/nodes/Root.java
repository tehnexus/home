package org.tehnexus.home.warranty.tree.nodes;

import java.util.Map.Entry;

import org.tehnexus.home.warranty.classes.Properties;
import org.tehnexus.home.warranty.classes.Property;
import org.tehnexus.home.warranty.tree.XTreeNode;

public class Root extends XTreeNode {

	// private TreeStrings filter;
	private Properties childrenSource;

	public Root(Properties childrenSource) {
		this.childrenSource = childrenSource;
		addChildren();
	}

	public Properties getChildrenSource() {
		return childrenSource;
	}

	@Override
	public String toString() {
		return "Products";
	}

	private void addChildren() {
		for (Entry<Integer, Property> e : childrenSource.entrySet()) {
			add(e.getValue());
		}
	}

	// public void setFilter(TreeStrings filter) {
	// this.filter = filter;
	// for (Entry<Long, ProductNode> entry : products.entrySet()) {
	// ProductNode p = entry.getValue();
	// switch (filter) {
	// case FLTR_OFF:
	// p.setVisible(true);
	// break;
	// case FLTR_WAR_ACTIVE:
	// p.setVisible(p.isWarrantyCovered() == true);
	// break;
	// case FLTR_WAR_OVER:
	// p.setVisible(p.isWarrantyCovered() == false);
	// break;
	// default:
	// break;
	// }
	// }
	// return;
	// }

	// public ProductsNode unassign() {
	// for (Entry<Long, ProductNode> entry : products.entrySet())
	// this.add(entry.getValue());
	// return this;
	// }

	// private void applyFilterToManufacturers() {
	// @SuppressWarnings("unchecked")
	// Enumeration<ManufacturerNode> e = manufacturers.children();
	// while (e.hasMoreElements()) {
	// ManufacturerNode manufacturer = e.nextElement();
	// manufacturer.setVisible(manufacturer.getChildCount(manufacturer, filter)
	// > 0);
	// }
	// }

	// private void applyFilterToShops() {
	// @SuppressWarnings("unchecked")
	// Enumeration<ShopNode> e = shops.children();
	// while (e.hasMoreElements()) {
	// ShopNode shop = e.nextElement();
	// shop.setVisible(shop.getChildCount(shop, filter) > 0);
	// }
	// }

}