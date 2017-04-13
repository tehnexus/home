package com.github.tehnexus.home.warranty.tree;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.github.tehnexus.home.util.Identifier;
import com.github.tehnexus.home.warranty.classes.Properties;
import com.github.tehnexus.home.warranty.tree.nodes.Root;
import com.github.tehnexus.swing.XTree;

import net.miginfocom.swing.MigLayout;

public class TreePanel extends JPanel implements ActionListener, TreeSelectionListener {

	private static final String	AC_GRP			= "AC_GRP";
	private static final String	AC_FLTR			= "AC_FLTR";

	private XTree				xTree;
	private XTreeModel			treeModel;

	private JCheckBox			chckbxGroup;
	private JComboBox<Object>	comboBoxGroup;
	private JCheckBox			chckbxFilter;
	private JComboBox<Object>	comboBoxFilter;
	private Properties			products;
	private MouseEvent			validMouseEvent;
	private JMenuItem			menuItemEdit	= new JMenuItem("Edit");

	private XTreeNode			treeRoot;

	public TreePanel() {
		createGUI();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (menuItemEdit.equals(e.getSource())) {
			TreePath selPath = xTree.getPathForLocation(validMouseEvent.getX(), validMouseEvent.getY());
			if (selPath != null) {
				firePropertyChange("editNode", null, selPath.getLastPathComponent());
			}
		}
	}

	public XTreeNode getRoot() {
		return treeModel.getRoot();
	}

	public void refresh(boolean isNewTree) {
		if (isNewTree) {
			treeModel = new XTreeModel(treeRoot);
			xTree.setModel(treeModel);
			treeModel.sort();
			return;
		}
		treeModel.setRoot(treeRoot);
		treeModel.sort();

		TreeStrings filter = TreeStrings.getObject(comboBoxFilter.getSelectedItem().toString());
		if (!chckbxFilter.isSelected())
			filter = TreeStrings.FLTR_OFF;

		treeModel.setFilter(filter);

		TreeStrings group = TreeStrings.getObject(comboBoxGroup.getSelectedItem().toString());
		if (!chckbxGroup.isSelected())
			group = TreeStrings.GRP_OFF;

		switch (group) {
		case GRP_BYMANU:
			treeRoot = new Root(products.getTypes(Identifier.MANUFACTURER));
			break;

		case GRP_BYSHOP:
			treeRoot = new Root(products.getTypes(Identifier.SHOP));
			break;

		case GRP_OFF:
			treeRoot = new Root(products);
			break;

		default:
			break;
		}
		treeModel.setRoot(treeRoot);
		treeModel.sort();
	}

	public void selectTreeRoot() {
		xTree.setRootSelectionPath();
	}

	public void setProducts(Properties products) {
		this.products = products;
		treeRoot = new Root(products);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		firePropertyChange("treeSelectionEvent", null, xTree.getLastSelectedPathComponent());
	}

	private void createGUI() {
		setLayout(new MigLayout("", "[150px,grow,fill][150px,grow,fill]", "[fill][grow,fill][fill]"));

		// group by
		comboBoxGroup = new JComboBox<>(
				new String[] { TreeStrings.GRP_BYSHOP.getText(), TreeStrings.GRP_BYMANU.getText() });
		comboBoxGroup.setAction(new ActionComboBox("comboBoxGroupBy", null));
		comboBoxGroup.setEnabled(false);
		add(comboBoxGroup, "cell 1 0,growx");
		chckbxGroup = new JCheckBox(new ActionCheckBox("Group by:", null, comboBoxGroup, AC_GRP));
		add(chckbxGroup, "cell 0 0");

		// filter
		comboBoxFilter = new JComboBox<>(
				new String[] { TreeStrings.FLTR_WAR_ACTIVE.getText(), TreeStrings.FLTR_WAR_OVER.getText() });
		comboBoxFilter.setAction(new ActionComboBox("comboBoxShowOnly", null));
		comboBoxFilter.setEnabled(false);
		add(comboBoxFilter, "cell 1 2,growx");
		chckbxFilter = new JCheckBox(new ActionCheckBox("Filter Warranty:", null, comboBoxFilter, AC_FLTR));
		add(chckbxFilter, "cell 0 2");

		// Construct the tree.
		xTree = new XTree();
		// xTree = new XTree(treeModel);
		xTree.addTreeSelectionListener(this);
		xTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		xTree.setCellRenderer(new XTreeCellRenderer());

		JScrollPane scrollPane = new JScrollPane(xTree);
		scrollPane.setPreferredSize(new Dimension(200, 200));
		add(scrollPane, "cell 0 1 2 1,grow");

	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null)
			return new ImageIcon(imgURL);

		System.err.println("Couldn't find file: " + path);
		return null;
	}

	private class ActionCheckBox extends AbstractAction {

		private JComboBox<Object>	comboBoxControl;
		private String				actionCommand;

		public ActionCheckBox(String name, Integer mnemonic, JComboBox<Object> comboBoxControl, String actionCommand) {
			super(name);
			putValue(MNEMONIC_KEY, mnemonic);
			this.actionCommand = actionCommand;
			this.comboBoxControl = comboBoxControl;
		}

		@Override
		public void actionPerformed(ActionEvent a) {
			JCheckBox chckbx = (JCheckBox) a.getSource();
			comboBoxControl.setEnabled(chckbx.isSelected());
			comboBoxControl.getAction()
					.actionPerformed(new ActionEvent(chckbx, ActionEvent.ACTION_PERFORMED, actionCommand));
		}

	}

	private class ActionComboBox extends AbstractAction {

		public ActionComboBox(String name, Integer mnemonic) {
			super(name);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent a) {
			refresh(false);
		}
	}

	class PopupListener extends MouseAdapter {

		JPopupMenu popup;

		PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			TreePath selPath = xTree.getPathForLocation(e.getX(), e.getY());
			if (selPath != null) {
				xTree.setSelectionPath(selPath);
				if (SwingUtilities.isRightMouseButton(e)) {
					validMouseEvent = e;
					maybeShowPopup(e);
				}
			}
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
}
