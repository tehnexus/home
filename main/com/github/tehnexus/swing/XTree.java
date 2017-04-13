package com.github.tehnexus.swing;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.github.tehnexus.home.warranty.tree.XTreeModel;

public class XTree extends JTree {

	public XTree() {
		super();
	}

	public XTree(XTreeModel treeModel) {
		super(treeModel);
	}

	public void collapse() {
		int row = 0;
		while (row < getRowCount()) {
			collapseRow(row++);
		}
	}

	public void expand() {
		int row = 0;
		while (row < getRowCount()) {
			expandRow(row++);
		}
	}

	public void setRootSelectionPath() {
		this.setSelectionPath(new TreePath(this.getModel().getRoot()));
	}

}
