package com.github.tehnexus.home.warranty;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.github.tehnexus.awt.XFont;
import com.github.tehnexus.home.util.Identifier;
import com.github.tehnexus.home.warranty.classes.Properties;
import com.github.tehnexus.home.warranty.tree.TreePanel;
import com.github.tehnexus.sqlite.HomeData;

public class TabWarranty extends JPanel {

	private PropertyListener	propertyListener	= new PropertyListener();

	private HomeData			homeData;

	private TreePanel			treePanel;
	private Editor				editor;

	public TabWarranty() {
		homeData = new HomeData();
		homeData.addPropertyChangeListener(propertyListener);
		homeData.loadDatabaseData(); // worker thread
		createGUI();
	}

	private void createGUI() {
		// set layout of this tab
		setLayout(new BorderLayout(0, 0));

		// create a split pane and add it to the tab
		JSplitPane splitPane = new JSplitPane();
		add(splitPane);

		// add the tree to the left side of split pane
		treePanel = new TreePanel();
		splitPane.setLeftComponent(treePanel);

		// create new tablayout and add it to right side of plit pane
		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.setFont(XFont.FONT_BTNCONFIRM_DEFAULT);
		splitPane.setRightComponent(tabbedPane);

		// create viewer
		// viewer = new JPanel();
		// tabbedPane.addTab("Viewer", null, new JPanel(), null);

		// create editor
		editor = new Editor();
		
		tabbedPane.addTab("Editor", null, editor, null);
		tabbedPane.setSelectedComponent(editor);

		editor.addPropertyChangeListener(propertyListener);
		treePanel.addPropertyChangeListener(propertyListener);
	}

	private void initializeViewer() {
		Properties products = homeData.getProducts();
		products.setTypeValues(Identifier.SHOP, homeData.getShops(), false);
		products.setTypeValues(Identifier.MANUFACTURER, homeData.getManufacturers(), false);
		products.setTypeValues(Identifier.PAYMENT, homeData.getPayments(), false);
		products.setTypeValues(Identifier.ATTACHMENT, homeData.getAttachments(), true);

		editor.initialize(products);

		treePanel.setProducts(products);
		treePanel.refresh(true);
		treePanel.selectTreeRoot();
		treePanel.repaint();
	}

	private class PropertyListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			switch (pce.getPropertyName()) {

				case "HOMEDATA_DONE":
					initializeViewer();
					break;

				case "sqlDataChange":
					try {
						throw new IOException(
								"unhandled propertyChangeString: 'sqlDataChange' in TabWarranty.PropertyListener");
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					break;

				case "treeSelectionEvent":
//					editor.setVisible(true);
					editor.loadProduct(pce.getNewValue());
					break;

				default:
					break;
			}
		}

	}
}
