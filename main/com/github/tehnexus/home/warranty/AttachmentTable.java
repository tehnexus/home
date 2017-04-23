package com.github.tehnexus.home.warranty;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
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
import javax.swing.table.AbstractTableModel;

import com.github.tehnexus.awt.Dimension;
import com.github.tehnexus.home.util.Identifier;
import com.github.tehnexus.home.warranty.classes.Attachment;
import com.github.tehnexus.home.warranty.classes.Properties;
import com.github.tehnexus.home.warranty.classes.Property;
import com.github.tehnexus.sqlite.SQLStrings;
import com.github.tehnexus.sqlite.SQLUtil;
import com.github.tehnexus.sqlite.SQLiteCon;

public class AttachmentTable extends JDialog {

	private final Property									product;
	private final Properties								properties;
	private AttachmentTableModel							tableModel;
	private HashMap<Integer, LoadAttachmentViewerWorker>	workers			= new HashMap<>(0);
	private HashMap<Integer, AttachmentViewer>				finishedWorkers	= new HashMap<>(0);

	// private JButton btnExport = new JButton("Export");
	private JTable											table			= new JTable();
	private int												rowSelected		= -1;

	private JPopupMenu										popupMenu		= new JPopupMenu();
	private JMenuItem										deleteItem		= new JMenuItem("Delete");

	// private AttachmentViewer attViewer;

	public AttachmentTable(Properties properties, Property product) {
		this.product = product;
		this.properties = properties;

		loadAttachmentViewers(); // concurrent

		createAndShowGUI();
	}

	private void addAttachmentFromFile() {

		AttachmentViewer attViewer = new AttachmentViewer();
		if (attViewer.hasValidFile()) {

			Properties allAttachments = properties.getTypes(Identifier.ATTACHMENT);
			int id = allAttachments.getNewId();

			Properties attAttachmentTypes = allAttachments.getTypes(Identifier.ATTACHMENTTYPE);
			Property attachmentType = attAttachmentTypes.get(0); // ToDo: type
																	// from
																	// table

			Attachment attach = new Attachment.Builder(id).comment("").type(attachmentType).idForeign(product.getId())
					.build();
			// todo: comment from table

			product.setType(Identifier.ATTACHMENT, attach, -1);
			tableModel.addRecord(attach);

			writeAttachmentToDatabase(attach, attViewer.getFile());

			if (attach != null) {
				attViewer.setVisible(true);
			}
		}
	}

	private void createAndShowGUI() {
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setMinimumSize(new Dimension(500, 600));
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		setTitle("Attachments");

		TablePopupMenuAction tablePopupMenuAction = new TablePopupMenuAction();
		popupMenu.add(deleteItem);
		deleteItem.addActionListener(tablePopupMenuAction);

		table.setComponentPopupMenu(popupMenu);
		table.setRowHeight(25);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
		// add(btnExport, BorderLayout.NORTH);

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

	private void writeAttachmentToDatabase(Attachment attach, byte[] bytes) {
		String sql = SQLStrings.insertIntotblAttachment();

		Object[] args = new Object[] { attach.getId(), attach.getIdForeign(),
				attach.getIdType(Identifier.ATTACHMENTTYPE), bytes, attach.getComment() };

		try (SQLiteCon connectionSQLite = new SQLiteCon(SQLUtil.defaultDatabaseLocation())) {
			connectionSQLite.executePreparedStatement(sql, args);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public class AttachmentTableModel extends AbstractTableModel {

		private final Comparator<Attachment>	tnc		= Comparator.comparing(Attachment::isDummy)
				.thenComparing(Attachment::getId);

		private final List<Property>			allAttachTypesList;
		private final String[]					colNames;
		private List<Attachment>				data	= new ArrayList<>(0);

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

		@Override
		public void actionPerformed(ActionEvent ae) {

			if (Editor.PLACEHOLDER_NEW.equalsIgnoreCase(ae.getActionCommand())) {
				addAttachmentFromFile();
			}
			else {
				int row = ae.getModifiers();
				int id = tableModel.getAttachmentAt(row).getId();
				loadAttachment(id);
			}
		}
	}

	private class TablePopupMenuListener implements PopupMenuListener {

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			int rowSelected = table.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), table));
			if (rowSelected > -1) {
				table.setRowSelectionInterval(rowSelected, rowSelected);
			}
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		}
	}

	private class TablePopupMenuAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource().equals(deleteItem)) {
				Point p = SwingUtilities.convertPoint(popupMenu, new Point(0, 0), table);
				int row = table.rowAtPoint(p);
				if (row > -1) {
					System.out.println(row);
				}
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
				}
				catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
