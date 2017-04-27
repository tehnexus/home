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
import com.github.tehnexus.exception.UnsupportedFileTypeException;
import com.github.tehnexus.filetypedetector.FileType;
import com.github.tehnexus.filetypedetector.FileTypeDetector;
import com.github.tehnexus.home.util.Util;
import com.github.tehnexus.home.warranty.classes.Attachment;
import com.github.tehnexus.image.ImagePanel;
import com.github.tehnexus.image.ImagePreview;
import com.github.tehnexus.image.SizeFit;
import com.github.tehnexus.sqlite.SQLStrings;
import com.github.tehnexus.sqlite.SQLUtil;

public class AttachmentViewer extends JDialog {

	private Attachment		attach;
	private ImagePanel		panImage;

	private JButton			btnExport		= new JButton("Export");
	private JButton			btnReset		= new JButton("Original Size");
	private JButton			btnFitPanel		= new JButton("Fit Window");
	private JButton			btnClose		= new JButton("Close");

	private ActionListener	actionListener	= new ActionListener();

	public AttachmentViewer() {
		createGUI();
		importUserFile();
		setUpWindow();
	}

	public AttachmentViewer(Attachment attach) throws IOException, SQLException, UnsupportedFileTypeException {
		this.attach = attach;

		createGUI();
		load(attach);
		setUpWindow();
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

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(500, 500);
	}

	private File getUserFile() {
		String defaultFileLocation = Util.getProperty("attachmentFileLocation");
		File dir = new File(defaultFileLocation);
		JFileChooser fc = new JFileChooser(dir);
		ImagePreview previewPane = new ImagePreview();
		fc.setAccessory(previewPane);
		fc.addPropertyChangeListener(previewPane);
		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			String folder = file.getParent();
			if (!folder.equalsIgnoreCase(defaultFileLocation))
				Util.setProperty("attachmentFileLocation", folder);

			return file;
		}
		return null;
	}

	public boolean hasValidFile() {
		return panImage.hasImage();
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
				else
					throw new UnsupportedFileTypeException("File type not supported.");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (UnsupportedFileTypeException e) {
				e.printStackTrace();
			}
		}
		else {
			dispose();
		}
	}

	private void load(Attachment attach) throws UnsupportedFileTypeException, IOException, SQLException {

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
				throw new UnsupportedFileTypeException();
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
				panImage.setSizeFit(SizeFit.IMAGE);
			}
			else if (e.getSource().equals(btnExport)) {
				// TODO: export it!
			}
			else if (e.getSource().equals(btnFitPanel)) {
				panImage.setSizeFit(SizeFit.WINDOW);
			}
		}
	}

}
