package com.github.tehnexus.sqlite;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import com.github.tehnexus.home.util.Identifier;
import com.github.tehnexus.home.warranty.classes.Properties;

public class HomeData {

	private Properties						shops;
	private Properties						attachments;
	private Properties						payments;
	private Properties						manufacturers;
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
	private PropertyChangeSupport			pcs							= new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener l) {
		pcs.addPropertyChangeListener(l);
	}

	public Properties getAttachments() {
		return attachments;
	}

	public Properties getManufacturers() {
		return manufacturers;
	}

	public Properties getPayments() {
		return payments;
	}

	public Properties getProducts() {
		return products;
	}

	public Properties getShops() {
		return shops;
	}

	public void loadDatabaseData() {

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
		LoadFromDatabaseWorker payWorker = new LoadFromDatabaseWorker(Identifier.PAYMENT, SQLStrings.queryPayments());
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

	public void removePropertyChangeListener(PropertyChangeListener l) {
		pcs.removePropertyChangeListener(l);
	}

	private synchronized void watchWorkerObjects() {

		// if shop and shoptype are done
		if (!configDoneShop && !configRunningShop && finishedWorkers.containsKey(Identifier.SHOP)
				&& finishedWorkers.containsKey(Identifier.SHOPTYPE)) {

			configRunningShop = true;
			shops = finishedWorkers.get(Identifier.SHOP);
			shops.setTypeValues(Identifier.SHOPTYPE, finishedWorkers.get(Identifier.SHOPTYPE), false);
			configDoneShop = true;
		}

		if (!configDoneAttachment && !configRunningAttachment && finishedWorkers.containsKey(Identifier.ATTACHMENT)
				&& finishedWorkers.containsKey(Identifier.ATTACHMENTTYPE)) {

			configRunningAttachment = true;
			attachments = finishedWorkers.get(Identifier.ATTACHMENT);
			attachments.setTypeValues(Identifier.ATTACHMENTTYPE, finishedWorkers.get(Identifier.ATTACHMENTTYPE), false);
			configDoneAttachment = true;
		}

		if (!configDonePayment && !configRunningPayment && finishedWorkers.containsKey(Identifier.PAYMENT)) {
			configRunningPayment = true;
			payments = finishedWorkers.get(Identifier.PAYMENT);
			configDonePayment = true;
		}

		if (!configDoneManufacturer && !configRunningManufacturer
				&& finishedWorkers.containsKey(Identifier.MANUFACTURER)) {
			configRunningManufacturer = true;
			manufacturers = finishedWorkers.get(Identifier.MANUFACTURER);
			configDoneManufacturer = true;
		}

		if (!configRunningProduct && finishedWorkers.containsKey(Identifier.PRODUCT) && configDoneShop
				&& configDoneManufacturer && configDonePayment && configDoneAttachment) {

			configRunningProduct = true;
			products = finishedWorkers.get(Identifier.PRODUCT);
			finishedWorkers.clear();

			pcs.firePropertyChange("HOMEDATA_DONE", null, true);
		}
	}

	private class LoadFromDatabaseWorker extends SwingWorker<Properties, Void> {

		private final Identifier	identifier;
		private final String		sql;

		public LoadFromDatabaseWorker(Identifier identifier, String sql) {
			this.sql = sql;
			this.identifier = identifier;
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

		public Identifier getIdentifier() {
			return identifier;
		}
	}

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
