package com.github.tehnexus.home.warranty.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class XTreeModel implements TreeModel {

	// JDK 1.8.0
	private static Comparator<XTreeNode> tnc = Comparator.comparing(XTreeNode::isLeaf)
			.thenComparing(n -> n.toString().toLowerCase());

	private static void sort(XTreeNode parent) {
		int n = parent.getChildCount();
		List<XTreeNode> children = new ArrayList<>(n);
		for (int i = 0; i < n; i++) {
			children.add((XTreeNode) parent.getChildAt(i));
		}
		Collections.sort(children, tnc);
		parent.removeAllChildren();
		for (MutableTreeNode node : children) {
			parent.add(node);
		}
		System.out.println("sorted");
	}

	private Vector<TreeModelListener>	treeModelListeners	= new Vector<>();
	private XTreeNode					root;
	private TreeStrings					filter				= TreeStrings.FLTR_OFF;

	public XTreeModel(XTreeNode root) {
		this.root = root;
	}

	/**
	 * Adds a listener for the TreeModelEvent posted after the tree changes.
	 */
	@Override
	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.addElement(l);
	}

	/**
	 * The only event raised by this model is TreeStructureChanged with the root
	 * as path, i.e. the whole tree has changed.
	 */
	protected void fireTreeStructureChanged(XTreeNode oldRoot) {
		TreeModelEvent e = new TreeModelEvent(this, new Object[] { oldRoot });
		for (TreeModelListener tml : treeModelListeners) {
			tml.treeStructureChanged(e);
		}
	}

	/**
	 * Returns the child of parent at index index in the parent's child array.
	 */
	@Override
	public Object getChild(Object parent, int index) {
		if (filter.equals(TreeStrings.FLTR_OFF))
			return ((XTreeNode) parent).getChildAt(index);

		XTreeNode o = (XTreeNode) parent;
		return o.getChildAt(o, index, filter);
	}

	//////////////// Fire events //////////////////////////////////////////////

	/**
	 * Returns the number of children of parent.
	 */
	@Override
	public int getChildCount(Object parent) {
		if (filter.equals(TreeStrings.FLTR_OFF))
			return ((XTreeNode) parent).getChildCount();

		XTreeNode o = (XTreeNode) parent;
		return o.getChildCount(o, filter);
	}

	//////////////// TreeModel interface implementation ///////////////////////

	/**
	 * Returns the index of child in parent.
	 */
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		System.err.println("getIndexOfChild");
		return -1;
	}

	/**
	 * Returns the root of the tree.
	 */
	@Override
	public XTreeNode getRoot() {
		return root;
	}

	/**
	 * Returns true if node is a leaf.
	 */
	@Override
	public boolean isLeaf(Object node) {
		return ((XTreeNode) node).getChildCount() == 0;
	}

	/**
	 * Removes a listener previously added with addTreeModelListener().
	 */
	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.removeElement(l);
	}

	public void setFilter(TreeStrings filter) {
		this.filter = filter;
		// TODO: filter to child objects
	}

	/**
	 * Used to toggle between show ancestors/show descendant and to change the
	 * root of the tree.
	 */
	public void setRoot(XTreeNode newRoot) {
		XTreeNode oldRoot = root;
		if (newRoot != null) {
			root = newRoot;
		}
		fireTreeStructureChanged(oldRoot);
	}

	public void sort() {
		Enumeration<?> e = root.preorderEnumeration();
		while (e.hasMoreElements()) {
			XTreeNode node = (XTreeNode) e.nextElement();
			if (!node.isLeaf()) {
				sort(node);
			}
		}
		fireTreeStructureChanged(root);
	}

	/**
	 * Messaged when the user has altered the value for the item identified by
	 * path to newValue. Not used by this model.
	 */
	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		System.out.println("*** valueForPathChanged : " + path + " --> " + newValue);
	}
}