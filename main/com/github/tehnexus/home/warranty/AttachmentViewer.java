package com.github.tehnexus.home.warranty;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import com.github.tehnexus.awt.Dimension;
import com.github.tehnexus.filetypedetector.FileType;
import com.github.tehnexus.filetypedetector.FileTypeDetector;
import com.github.tehnexus.home.util.Util;
import com.github.tehnexus.home.warranty.classes.Attachment;
import com.github.tehnexus.image.ImagePanel;
import com.github.tehnexus.image.SizeFit;
import com.github.tehnexus.sqlite.SQLStrings;
import com.github.tehnexus.sqlite.SQLUtil;

public class AttachmentViewer extends JDialog {

	private Attachment		attach;
	private ImagePanel		panImage;

	private JButton			btnExport		= new JButton("Export");
	private JButton			btnReset		= new JButton("Reset");
	private JButton			btnFitPanel		= new JButton("Fit Window");
	private JButton			btnClose		= new JButton("Close");

	private ActionListener	actionListener	= new ActionListener();

	public Attachment getAttachment() {
		return attach;
	}

	public byte[] getFile() {
		BufferedImage img = panImage.getImage(true);

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ImageIO.write(img, "png", baos);
			return baos.toByteArray();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public AttachmentViewer() {
		createGUI();
		importUserFile();
		setUpWindow();
	}

	public AttachmentViewer(Attachment attach) throws IOException, SQLException {
		this.attach = attach;

		createGUI();
		load(attach);
		setUpWindow();
	}

	private void importUserFile() {
		File file = getUserFile();
		if (file != null) {
			try (BufferedInputStream bis = new BufferedInputStream(
					Files.newInputStream(file.toPath(), StandardOpenOption.READ))) {
				FileType fileType = FileTypeDetector.detectFileType(bis);
				if (Util.isAnyOf(fileType, FileType.PNG, FileType.JPEG)) {
					panImage.setImage(bis);
				}
				else if (fileType == FileType.PDF) {

				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			dispose();
		}
	}

	public boolean hasValidFile() {
		return panImage.hasImage();
	}

	private File getUserFile() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file;
		}
		return null;
	}

	private void createGUI() {

		setLayout(new BorderLayout());

		panImage = new ImagePanel();
		add(panImage, BorderLayout.CENTER);

		JPanel panNorth = new JPanel(new BorderLayout());
		JPanel panSouth = new JPanel(new BorderLayout());

		add(panNorth, BorderLayout.PAGE_START);
		add(panSouth, BorderLayout.PAGE_END);

		panNorth.add(btnFitPanel, BorderLayout.LINE_START);
		panNorth.add(btnReset, BorderLayout.LINE_END);

		panSouth.add(btnExport, BorderLayout.LINE_START);
		panSouth.add(btnClose, BorderLayout.LINE_END);

		btnFitPanel.addActionListener(actionListener);
		btnExport.addActionListener(actionListener);
		btnReset.addActionListener(actionListener);
		btnClose.addActionListener(actionListener);

		btnFitPanel.setFocusable(false);
		btnExport.setFocusable(false);
		btnReset.setFocusable(false);
		btnClose.setFocusable(false);

		pack();
		panImage.grabFocus();

	}

	private void load(Attachment attach) throws IOException, SQLException {

		try (InputStream inputStream = SQLUtil.blobFromDatabase(SQLStrings.queryAttachments(attach.getId()))) {
			FileType fileType = FileTypeDetector.detectFileType(inputStream);

			if (Util.isAnyOf(fileType, FileType.PNG, FileType.JPEG)) {
				panImage.setImage(inputStream);
			}
			else if (fileType == FileType.PDF) {
				// try (PDDocument document = PDDocument.load(inputStream)) {
				//
				// PDPageTree pages = document.getDocumentCatalog().getPages();
				// PDPage page = pages.get(0);
				//
				// PDFRenderer pdfRenderer = new PDFRenderer(document);
				// int pageCounter = 0;
				// for (PDPage page : document.getPages()) {
				// // note that the page number parameter is zero based
				// BufferedImage bm =
				// pdfRenderer.renderImageWithDPI(pageCounter++,
				// 300, ImageType.RGB);
				//
				// // suffix in filename will be used as the file format
				// ImageIOUtil.writeImage(bm, pdfFilename + "-" +
				// (pageCounter++) +
				// ".png", 300);
				// }
				// }
			}
			else {
				throw new IOException("File type not supported: " + fileType.toString());
			}
		}
	}

	private void setUpWindow() {
		setMinimumSize(new Dimension(500, 500));
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(null);
	}

	private class ActionListener implements java.awt.event.ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(btnClose)) {
				dispose();
			}
			else if (e.getSource().equals(btnReset)) {
				panImage.fitImage(SizeFit.IMAGE);
			}
			else if (e.getSource().equals(btnExport)) {
				// TODO: export it!
			}
			else if (e.getSource().equals(btnFitPanel)) {
				panImage.fitImage(SizeFit.WINDOW);
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(500, 500);
	}

}
