package com.github.tehnexus.home.warranty;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import com.github.tehnexus.awt.Dimension;
import com.github.tehnexus.home.util.Identifier;
import com.github.tehnexus.home.util.Util;
import com.github.tehnexus.home.warranty.classes.Attachment;
import com.github.tehnexus.home.warranty.classes.Properties;
import com.github.tehnexus.home.warranty.classes.Property;
import com.github.tehnexus.sqlite.SQLStrings;
import com.github.tehnexus.sqlite.SQLUtil;

public class AttachmentTable extends JDialog {

	private final Property									product;
	private final Properties								properties;
	private AttachmentTableModel							tableModel;
	private HashMap<Integer, LoadAttachmentViewerWorker>	workers			= new HashMap<>(0);
	private HashMap<Integer, AttachmentViewer>				finishedWorkers	= new HashMap<>(0);

	// private JButton btnExport = new JButton("Export");
	private JTable											table			= new JTable();

	// private AttachmentViewer attViewer;

	public AttachmentTable(Properties properties, Property product) {
		this.product = product;
		this.properties = properties;

		loadAttachmentViewers(); // concurrent

		createAndShowGUI();
	}

	private void addAttachment(Attachment attach, byte[] bytes) {
		String sqlString = SQLStrings.insertIntotblAttachment();

		Object[] args = new Object[] { attach.getId(), attach.getIdForeign(),
				attach.getIdType(Identifier.ATTACHMENTTYPE).get(0), bytes, attach.getComment() };

		SQLUtil.executePreparedStatement(sqlString, args);
	}

	private void addAttachmentFromFile() {

		AttachmentViewer attViewer = new AttachmentViewer();
		if (attViewer.hasValidFile()) {

			Properties allAttachments = properties.getTypes(Identifier.ATTACHMENT);
			int id = allAttachments.getNewId();

			Properties attAttachmentTypes = allAttachments.getTypes(Identifier.ATTACHMENTTYPE);
			Property attachmentType = attAttachmentTypes.get(0);

			Attachment attach = new Attachment.Builder(id).comment("").type(attachmentType).idForeign(product.getId())
					.build();

			addAttachment(attach, attViewer.getFile());

			product.setType(Identifier.ATTACHMENT, attach, -1);
			tableModel.addRecord(attach);
			tableModel.fireTableDataChanged();

			if (attach != null)
				attViewer.setVisible(true);
		}
	}

	private void createAndShowGUI() {
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setMinimumSize(new Dimension(500, 600));
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		setTitle("Attachments");

		JPopupMenu popupMenu = new JPopupMenu("Attachment");
		popupMenu.addPopupMenuListener(new TablePopupMenuListener(table, popupMenu));

		JMenuItem deleteItem = new JMenuItem("Delete", Util.getIcon("images/1492965615_f-cross_256.png", 16));
		deleteItem.setActionCommand(deleteItem.getText());
		JMenuItem addItem = new JMenuItem("Add", Util.getIcon("images/1492965666_f-top_256.png", 16));
		addItem.setActionCommand(addItem.getText());

		popupMenu.add(deleteItem);
		popupMenu.add(addItem);
		deleteItem.addActionListener(new TablePopupMenuAction(deleteItem));
		addItem.addActionListener(new TablePopupMenuAction(addItem));

		table.setComponentPopupMenu(popupMenu);
		// table.addMouseListener(new TablePopupMenuListener(popupMenu));

		table.setRowHeight(25);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
		// add(btnExport, BorderLayout.NORTH);

		// getting all possible values for type combo
		Properties allAttachTypes = properties.getTypes(Identifier.ATTACHMENT).getTypes(Identifier.ATTACHMENTTYPE);
		List<Property> allAttachTypesList = new ArrayList<>(allAttachTypes.values());

		tableModel = new AttachmentTableModel(new String[] { "Type", "Comment", "Attachment" });
		tableModel.addTableModelListener(new AttachTableModelListener());
		table.setModel(tableModel);

		// set comboBox as editor and renderer for type column
		ComboCellEditor comboCellEditor = new ComboCellEditor(allAttachTypesList, new TableComboListener());
		table.setDefaultEditor(Property.class, comboCellEditor);
		table.setDefaultRenderer(Property.class, new ComboTableCellRenderer());

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

	private void loadAttachment(int id) {
		AttachmentViewer attViewer = null;
		// check if worker is done
		if (!finishedWorkers.containsKey(id)) {
			try { // id not done, wait
				attViewer = workers.get(id).get();
				finishedWorkers.put(id, attViewer);
			}
			catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		else { // id done show
			attViewer = finishedWorkers.get(id);
		}
		attViewer.setVisible(true);
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

	private void removeAttachment() {
		// TODO: ask user to confirm by typing "DELETE" into textfield in popup
		String sqlString = SQLStrings.deleteFromtblAttachment();

		int tableRow = table.getSelectedRow();
		Attachment attach = tableModel.getAttachmentAt(table.convertRowIndexToModel(tableRow));

		Object[] args = new Object[] { attach.getId() };
		SQLUtil.executePreparedStatement(sqlString, args);
		tableModel.removeRecord(attach);
		product.removeType(Identifier.ATTACHMENT, attach);
		// TODO remove attachment from list of all attachments and assignment to
		// product
	}

	public class AttachmentTableModel extends AbstractTableModel {

		private final Comparator<Attachment>	tnc		= Comparator.comparing(Attachment::isDummy)
				.thenComparing(Attachment::getId);

		private final String[]					colNames;
		private List<Attachment>				data	= new ArrayList<>(0);

		public AttachmentTableModel(String[] columnNames) {
			this.colNames = columnNames;
		}

		public void addRecord(Attachment row) {
			data.add(row);
			Collections.sort(data, tnc);
		}

		public Attachment getAttachmentAt(int rowIndex) {
			return data.get(rowIndex);
		}

		@Override
		public Class<?> getColumnClass(int colIndex) {
			return getValueAt(0, colIndex).getClass();
		}

		@Override
		public int getColumnCount() {
			return colNames.length;
		}

		@Override
		public String getColumnName(int colIndex) {
			return colNames[colIndex];
		}

		@Override
		public int getRowCount() {
			return data.size();
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
		public boolean isCellEditable(int rowIndex, int colIndex) {
			return true;
		}

		public void removeRecord(Attachment attach) {
			data.remove(attach);
			fireTableDataChanged();
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
					fireTableCellUpdated(rowIndex, colIndex);
					break;
				case 2:
					// Button is used
					break;
				default:
					break;
			}
		}

	}

	private class AttachTableModelListener implements TableModelListener {

		// handles changes to the comment column
		@Override
		public void tableChanged(TableModelEvent e) {
			if (e.getColumn() == 1) {
				int modelRow = e.getFirstRow();
				Attachment attach = tableModel.getAttachmentAt(modelRow);
				attach.setComment((String) tableModel.getValueAt(modelRow, e.getColumn()));
				attach.propertyChange();
			}
		}

	}

	private class LoadAttachmentViewerWorker extends SwingWorker<AttachmentViewer, Void> {

		private final Attachment attach;

		public LoadAttachmentViewerWorker(Attachment attach) {
			this.attach = attach;
		}

		@Override
		protected AttachmentViewer doInBackground() throws Exception {
			return new AttachmentViewer(attach);
		}

		public int getAttachmentId() {
			return attach.getId();
		}
	}

	private class TableButtonAction extends AbstractAction {

		// handles button clicks in table
		@Override
		public void actionPerformed(ActionEvent ae) {
			int row = ae.getModifiers();
			int id = tableModel.getAttachmentAt(row).getId();
			loadAttachment(id);
		}
	}

	private class TableComboListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				Property property = (Property) e.getItem();
				int tableRow = table.getSelectedRow();
				Attachment attach = tableModel.getAttachmentAt(table.convertRowIndexToModel(tableRow));
				attach.setType(Identifier.ATTACHMENTTYPE, property, 0);
				attach.propertyChange();
			}
		}
	}

	private class TablePopupMenuAction extends AbstractAction {

		// handles clicks on table
		private final JMenuItem menuItem;

		public TablePopupMenuAction(JMenuItem menuItem) {
			this.menuItem = menuItem;
		}

		@Override
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(menuItem)) {
				int tableRow = table.getSelectedRow();
				if (tableRow > -1) {

					switch (menuItem.getText()) {
						case "Delete":
							removeAttachment();
							break;
						case "Add":
							addAttachmentFromFile();
							break;
					}
				}
			}
		}

	}

	private class TablePopupMenuListener implements PopupMenuListener {

		// handles popupmenu on the table
		private final JPopupMenu	popup;
		private final JTable		table;

		public TablePopupMenuListener(JTable table, JPopupMenu popup) {
			this.table = table;
			this.popup = popup;
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent arg0) {
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
			SwingUtilities.invokeLater(() -> {
				int rowAtPoint = table.rowAtPoint(SwingUtilities.convertPoint(popup, new Point(0, 0), table));
				if (rowAtPoint > -1) {
					table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
				}
			});
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
				}
				catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
