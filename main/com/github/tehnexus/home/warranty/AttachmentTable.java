package com.github.tehnexus.home.warranty;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;
import javax.swing.table.AbstractTableModel;

import com.github.tehnexus.awt.Dimension;
import com.github.tehnexus.home.util.Identifier;
import com.github.tehnexus.home.warranty.classes.Attachment;
import com.github.tehnexus.home.warranty.classes.Properties;
import com.github.tehnexus.home.warranty.classes.Property;

public class AttachmentTable extends JDialog {

	private final Property product;
	private final Properties properties;
	private AttachmentTableModel tableModel;
	private HashMap<Integer, LoadAttachmentViewerWorker> workers = new HashMap<>(0);
	private HashMap<Integer, AttachmentViewer> finishedWorkers = new HashMap<>(0);
//	private JButton btnExport = new JButton("Export");

	// private AttachmentViewer attViewer;

	public AttachmentTable(Properties properties, Property product) {
		this.product = product;
		this.properties = properties;

		loadAttachmentViewers(); // concurrent

		createAndShowGUI();
	}

	private void loadAttachmentViewers() {

		// loop attachments in of this product and load as viewer
		List<Property> listAttachment = product.getType(Identifier.ATTACHMENT);
		if (listAttachment == null)
			return;

		for (Property p : listAttachment) {
			Attachment attach = (Attachment) p;
			LoadAttachmentViewerWorker attWorker = new LoadAttachmentViewerWorker(attach);
			attWorker.addPropertyChangeListener(new WorkerListener(attWorker));
			workers.put(attach.getId(), attWorker);
			attWorker.execute();
		}

	}

	private void createAndShowGUI() {
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setMinimumSize(new Dimension(500, 600));
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		setTitle("Attachments");

		JTable table = new JTable();
		table.setRowHeight(25);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
//		add(btnExport, BorderLayout.NORTH);

		// getting all possible values for type combo
		Properties allAttachTypes = properties.getTypes(Identifier.ATTACHMENT).getTypes(Identifier.ATTACHMENTTYPE);
		List<Property> allAttachTypesList = new ArrayList<>(allAttachTypes.values());

		tableModel = new AttachmentTableModel(allAttachTypesList, new String[] { "Type", "Comment", "Attachment" });
		table.setModel(tableModel);

		// set comboBox as editor and renderer for type column
		table.setDefaultRenderer(Property.class, new ComboTableCellRenderer());
		table.setDefaultEditor(Property.class, new ComboCellEditor(allAttachTypesList));

		// set button as editor and renderer for attachment column
		TableButtonColumn tableButtonColumn = new TableButtonColumn(table, new TableButtonAction(), 2);

		// loop attachments in of this product and add to tablemodel
		List<Property> listAttachment = product.getType(Identifier.ATTACHMENT);
		if (listAttachment == null)
			return;
		for (Property p : listAttachment) {
			Attachment attach = (Attachment) p;
			tableModel.addRecord(attach);
		}
	}

	private class TableButtonAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent ae) {
			String btnText = ae.getActionCommand();
			if (Editor.PLACEHOLDER_NEW.equalsIgnoreCase(btnText)) {
				try {
					AttachmentViewer attViewer = new AttachmentViewer(null);
				} catch (IOException | SQLException e) {
					e.printStackTrace();
				}
			} else {
				int row = ae.getModifiers();
				int id = tableModel.getAttachmentAt(row).getId();
				AttachmentViewer attViewer = null;
				// check if worker is done
				if (!finishedWorkers.containsKey(id)) {
					try { // id not done, wait
						attViewer = workers.get(id).get();
						finishedWorkers.put(id, attViewer);
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				} else { // id done show
					attViewer = finishedWorkers.get(id);
				}
				attViewer.setVisible(true);
			}
		}
	}

	public class AttachmentTableModel extends AbstractTableModel {

		private final Comparator<Attachment> tnc = Comparator.comparing(Attachment::isDummy)
				.thenComparing(Attachment::getId);

		private final List<Property> allAttachTypesList;
		private final String[] colNames;
		private List<Attachment> data = new ArrayList<>(0);

		public AttachmentTableModel(List<Property> allAttachTypesList, String[] columnNames) {
			this.allAttachTypesList = allAttachTypesList;
			this.colNames = columnNames;
			addDummies();
		}

		private void addDummies() {
			Property dummyType = null;
			for (Property prop : allAttachTypesList) {
				if (prop.getId() == 0) {
					dummyType = prop;
					break;
				}
			}
			if (dummyType == null)
				return;

			Attachment a = new Attachment.Builder(-99).build();
			a.setDummy(true);
			a.setType(Identifier.ATTACHMENTTYPE, dummyType, -1);
			addRecord(a);
		}

		public void addRecord(Attachment row) {
			data.add(row);
			Collections.sort(data, tnc);
		}

		@Override
		public int getColumnCount() {
			return colNames.length;
		}

		@Override
		public int getRowCount() {
			return data.size();
		}

		public Attachment getAttachmentAt(int rowIndex) {
			return data.get(rowIndex);
		}

		@Override
		public Object getValueAt(int rowIndex, int colIndex) {
			Object returnValue = null;
			Attachment att = data.get(rowIndex);

			switch (colIndex) {
			case 0: // type
				returnValue = att.getType(Identifier.ATTACHMENTTYPE).get(0);
				break;
			case 1: // comment
				returnValue = att.getComment();
				break;
			case 2: // attachment
				if (att.isDummy())
					returnValue = Editor.PLACEHOLDER_NEW;
				else
					returnValue = "View";
				break;
			default:
				break;
			}
			return returnValue;
		}

		@Override
		public Class<?> getColumnClass(int colIndex) {
			return getValueAt(0, colIndex).getClass();
		}

		@Override
		public String getColumnName(int colIndex) {
			return colNames[colIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int colIndex) {
			return true;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int colIndex) {
			Attachment att = data.get(rowIndex);

			switch (colIndex) {
			case 0:
				att.setType(Identifier.ATTACHMENTTYPE, (Property) aValue, -1);
				break;
			case 1:
				att.setComment((String) aValue);
				break;
			case 2:
				// Button is used
				// att.setAttachment(aValue);
				break;
			default:
				break;
			}
		}

	}

	private class WorkerListener implements PropertyChangeListener {

		private final LoadAttachmentViewerWorker worker;

		public WorkerListener(LoadAttachmentViewerWorker worker) {
			this.worker = worker;
		}

		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			if (pce.getPropertyName().equalsIgnoreCase("state")
					&& ((StateValue) pce.getNewValue()).equals(StateValue.DONE)) {
				try {
					finishedWorkers.put(worker.getAttachmentId(), worker.get());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class LoadAttachmentViewerWorker extends SwingWorker<AttachmentViewer, Void> {

		private final Attachment attach;

		public LoadAttachmentViewerWorker(Attachment attach) {
			this.attach = attach;
		}

		public int getAttachmentId() {
			return attach.getId();
		}

		@Override
		protected AttachmentViewer doInBackground() throws Exception {
			return new AttachmentViewer(attach);
		}
	}

}
