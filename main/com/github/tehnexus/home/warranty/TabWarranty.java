package com.github.tehnexus.home.warranty;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import com.github.tehnexus.awt.XFont;
import com.github.tehnexus.home.util.Identifier;
import com.github.tehnexus.home.warranty.classes.Properties;
import com.github.tehnexus.home.warranty.tree.TreePanel;
import com.github.tehnexus.sqlite.SQLStrings;
import com.github.tehnexus.sqlite.SQLUtil;
import com.github.tehnexus.sqlite.SQLiteCon;

public class TabWarranty extends JPanel {

	private PropertyListener				propertyListener			= new PropertyListener();

	private TreePanel						treePanel;
	private Editor							editor;
//	private JPanel							viewer						= null;

	private Properties						shop;
	private Properties						attachment;
	private Properties						payment;
	private Properties						manufacturer;
	private Properties						products;

	private boolean							configRunningProduct		= false;
	private boolean							configDoneShop				= false;
	private boolean							configRunningShop			= false;
	private boolean							configDonePayment			= false;
	private boolean							configRunningPayment		= false;
	private boolean							configDoneManufacturer		= false;
	private boolean							configRunningManufacturer	= false;
	private boolean							configDoneAttachment		= false;
	private boolean							configRunningAttachment		= false;

	private HashMap<Identifier, Properties>	finishedWorkers				= new HashMap<>(0);

	public TabWarranty() {
		loadDatabaseData(); // concurrent
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
//		viewer = new JPanel();
//		tabbedPane.addTab("Viewer", null, new JPanel(), null);

		// create editor
		editor = new Editor();
		tabbedPane.addTab("Editor", null, editor, null);

		tabbedPane.setSelectedComponent(editor);

		editor.addPropertyChangeListener(propertyListener);
		treePanel.addPropertyChangeListener(propertyListener);

	}

	private void loadDatabaseData() {

		// shopTypes
		LoadFromDatabaseWorker shopTypeWorker = new LoadFromDatabaseWorker(Identifier.SHOPTYPE,
				SQLStrings.queryShopTypes());
		shopTypeWorker.addPropertyChangeListener(new WorkerListener(shopTypeWorker));
		shopTypeWorker.execute();

		// shops
		LoadFromDatabaseWorker shopWorker = new LoadFromDatabaseWorker(Identifier.SHOP, SQLStrings.queryShops());
		shopWorker.addPropertyChangeListener(new WorkerListener(shopWorker));
		shopWorker.execute();

		// manufacturers
		LoadFromDatabaseWorker manufacturerWorker = new LoadFromDatabaseWorker(Identifier.MANUFACTURER,
				SQLStrings.queryManufacturers());
		manufacturerWorker.addPropertyChangeListener(new WorkerListener(manufacturerWorker));
		manufacturerWorker.execute();

		// paymentTypes
		LoadFromDatabaseWorker payWorker = new LoadFromDatabaseWorker(Identifier.PAYMENT,
				SQLStrings.queryPayments());
		payWorker.addPropertyChangeListener(new WorkerListener(payWorker));
		payWorker.execute();

		// attachmentTypes
		LoadFromDatabaseWorker attachTypeWorker = new LoadFromDatabaseWorker(Identifier.ATTACHMENTTYPE,
				SQLStrings.queryAttachmentTypes());
		attachTypeWorker.addPropertyChangeListener(new WorkerListener(attachTypeWorker));
		attachTypeWorker.execute();

		// attachments
		LoadFromDatabaseWorker attachWorker = new LoadFromDatabaseWorker(Identifier.ATTACHMENT,
				SQLStrings.queryAttachments());
		attachWorker.addPropertyChangeListener(new WorkerListener(attachWorker));
		attachWorker.execute();

		// products
		LoadFromDatabaseWorker productWorker = new LoadFromDatabaseWorker(Identifier.PRODUCT,
				SQLStrings.queryProducts());
		productWorker.addPropertyChangeListener(new WorkerListener(productWorker));
		productWorker.execute();

	}

	private void refreshTree(boolean newTree) {
		treePanel.setProducts(products);
		treePanel.refresh(newTree);
		treePanel.selectTreeRoot();
		treePanel.repaint();
		return;
	}

	private void finalizeProducts() {
		products.setTypeValues(Identifier.SHOP, shop, false);
		products.setTypeValues(Identifier.MANUFACTURER, manufacturer, false);
		products.setTypeValues(Identifier.PAYMENT, payment, false);
		products.setTypeValues(Identifier.ATTACHMENT, attachment, true);
	}

	private synchronized void watchWorkerObjects() {

		// if shop and shoptype are done
		if (!configDoneShop && !configRunningShop && finishedWorkers.containsKey(Identifier.SHOP)
				&& finishedWorkers.containsKey(Identifier.SHOPTYPE)) {

			configRunningShop = true;
			shop = finishedWorkers.get(Identifier.SHOP);
			shop.setTypeValues(Identifier.SHOPTYPE, finishedWorkers.get(Identifier.SHOPTYPE), false);
			configDoneShop = true;
		}

		if (!configDoneAttachment && !configRunningAttachment && finishedWorkers.containsKey(Identifier.ATTACHMENT)
				&& finishedWorkers.containsKey(Identifier.ATTACHMENTTYPE)) {

			configRunningAttachment = true;
			attachment = finishedWorkers.get(Identifier.ATTACHMENT);
			attachment.setTypeValues(Identifier.ATTACHMENTTYPE, finishedWorkers.get(Identifier.ATTACHMENTTYPE), false);
			configDoneAttachment = true;
		}

		if (!configDonePayment && !configRunningPayment && finishedWorkers.containsKey(Identifier.PAYMENT)) {
			configRunningPayment = true;
			payment = finishedWorkers.get(Identifier.PAYMENT);
			configDonePayment = true;
		}

		if (!configDoneManufacturer && !configRunningManufacturer
				&& finishedWorkers.containsKey(Identifier.MANUFACTURER)) {
			configRunningManufacturer = true;
			manufacturer = finishedWorkers.get(Identifier.MANUFACTURER);
			configDoneManufacturer = true;
		}

		if (!configRunningProduct && finishedWorkers.containsKey(Identifier.PRODUCT) && configDoneShop
				&& configDoneManufacturer && configDonePayment && configDoneAttachment) {

			configRunningProduct = true;
			products = finishedWorkers.get(Identifier.PRODUCT);
			finalizeProducts();
			editor.setProperties(products);
			finishedWorkers.clear();

			refreshTree(true);
		}
	}

	private class LoadFromDatabaseWorker extends SwingWorker<Properties, Void> {

		private final Identifier	identifier;
		private final String		sql;

		public LoadFromDatabaseWorker(Identifier identifier, String sql) {
			this.sql = sql;
			this.identifier = identifier;
		}

		public Identifier getIdentifier() {
			return identifier;
		}

		@Override
		protected Properties doInBackground() throws Exception {
			try (SQLiteCon connectionSQLite = new SQLiteCon(SQLUtil.defaultDatabaseLocation());
					ResultSet rs = connectionSQLite.executeQuery(sql)) {

				return new Properties(identifier, rs);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private class PropertyListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			switch (pce.getPropertyName()) {

			case "sqlDataChange":
				refreshTree(false);
				break;

			case "treeSelectionEvent":
				// viewer.loadRecord(e.getNewValue());

				editor.loadProduct(pce.getNewValue());
				break;

			default:
				break;
			}
		}

	}

	// --- SwingWorker and WorkerListener
	// --------------------------------------------------------- //
	private class WorkerListener implements PropertyChangeListener {

		private final LoadFromDatabaseWorker worker;

		public WorkerListener(LoadFromDatabaseWorker worker) {
			this.worker = worker;
		}

		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			if (pce.getPropertyName().equalsIgnoreCase("state")
					&& ((StateValue) pce.getNewValue()).equals(StateValue.DONE)) {
				try {
					finishedWorkers.put(worker.getIdentifier(), worker.get());
					watchWorkerObjects();
				}
				catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
