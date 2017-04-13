package com.github.tehnexus.home.warranty.tree;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.github.tehnexus.home.warranty.classes.Property;

public class XTreeNode extends DefaultMutableTreeNode {

	private boolean			visible	= true;
	private final Property	obj;

	public XTreeNode() {
		super();
		obj = null;
	}

	public XTreeNode(Property obj) {
		this.obj = obj;
	}

	public TreeNode getChildAt(XTreeNode caller, int index, TreeStrings filter) {

		if (filter.equals(TreeStrings.FLTR_OFF))
			return caller.getChildAt(index);

		if (children == null)
			throw new ArrayIndexOutOfBoundsException("node has no children");

		int realIndex = -1;
		int visibleIndex = -1;

		@SuppressWarnings("unchecked")
		Enumeration<XTreeNode> e = caller.children();
		while (e.hasMoreElements()) {
			XTreeNode node = e.nextElement();
			if (node.isVisible())
				visibleIndex++;
			realIndex++;
			if (visibleIndex == index)
				return (TreeNode) children.elementAt(realIndex);
		}
		throw new ArrayIndexOutOfBoundsException("index unmatched");
	}

	public int getChildCount(XTreeNode caller, TreeStrings filter) {

		if (filter.equals(TreeStrings.FLTR_OFF))
			return caller.getChildCount();

		if (children == null)
			return 0;

		int count = 0;
		@SuppressWarnings("unchecked")
		Enumeration<XTreeNode> e = caller.children();
		while (e.hasMoreElements()) {
			XTreeNode node = e.nextElement();
			if (node.isVisible())
				count++;
		}
		return count;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public String toString() {
		return obj.toString();
	}
}
