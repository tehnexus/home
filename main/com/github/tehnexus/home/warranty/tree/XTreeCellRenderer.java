package com.github.tehnexus.home.warranty.tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

// import org.tehnexus.home.warranty.tree.objects.ProductNode;

public class XTreeCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {

		// Allow the original renderer to set up the label
		Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		// XTreeNode o = (XTreeNode) value;

		// if (o instanceof ProductNode) {
		// ProductNode p = (ProductNode) o;
		// if (p.isWarrantyCovered()) {
		// c.setForeground(new Color(0, 102, 0));
		// } else {
		// c.setForeground(new Color(153, 0, 0));
		// }
		// }

		return c;
	}

}
