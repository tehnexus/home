package com.github.tehnexus.home.warranty;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.github.tehnexus.awt.Dimension;
import com.github.tehnexus.filetypedetector.FileType;
import com.github.tehnexus.filetypedetector.FileTypeDetector;
import com.github.tehnexus.home.util.Util;
import com.github.tehnexus.home.warranty.classes.Attachment;
import com.github.tehnexus.image.ImagePanel;
import com.github.tehnexus.image.listeners.ImageAdapter;
import com.github.tehnexus.image.listeners.MouseWheelImageListener;
import com.github.tehnexus.sqlite.SQLUtil;

public class AttachmentViewer extends JDialog {

//	private final Dimension		screenSize		= new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
//	private Dimension			windowSizeMax;

//	private Dimension			contentSizeMax;
//	private Dimension			contentMargin;

	private ImagePanel			panImage;
//	private JPanel				panNorth		= new JPanel(new BorderLayout());
//	private JButton				btnExport		= new JButton("Export");
//	private JButton				btnReset		= new JButton("Reset");
//	private JButton				btnClose		= new JButton("Close");

//	private ActionListener		actionListener	= new ActionListener();

//	private final JFileChooser	fc				= new JFileChooser();

	public AttachmentViewer(Attachment attach) throws IOException, SQLException {

		createGUI();
		load(attach);
		setUpWindow();
		
		
//		if (attach != null) {
//			
//		} else {
////			setSize(new Dimension(500, 600));
////			if (getUserFile() == null) {
////				dispose();
////				return;
////			}
//		}
		
	}

	private void createGUI() {

		setLayout(new BorderLayout());
		
		panImage = new ImagePanel();
		add(panImage, BorderLayout.CENTER);

//		windowSizeMax = new Dimension(screenSize.getWidth() - Dimension.FRAME_MARGIN_HORIZONTAL,
//				screenSize.getHeight() - Dimension.FRAME_MARGIN_VERTICAL);
//		setSize(windowSizeMax);
//		pack();
//		contentMargin = new Dimension(getWidth() - panImage.getWidth(), getHeight() - panImage.getHeight());
//
//		contentSizeMax = new Dimension(windowSizeMax.getWidth() - contentMargin.getWidth(),
//				windowSizeMax.getHeight() - contentMargin.getHeight());

//		panImage.initialize(contentSizeMax);

//		add(panNorth, BorderLayout.PAGE_START);
//		panNorth.add(btnExport, BorderLayout.LINE_START);
//		panNorth.add(btnReset, BorderLayout.LINE_END);
//		add(btnClose, BorderLayout.PAGE_END);
//
//		btnExport.addActionListener(actionListener);
//		btnReset.addActionListener(actionListener);
//		btnClose.addActionListener(actionListener);
//
//		btnExport.setFocusable(false);
//		btnReset.setFocusable(false);
//		btnClose.setFocusable(false);

//		panImage.addComponentListener(new ImageAdapter(panImage));
//		panImage.addMouseWheelListener(new MouseWheelImageListener(panImage));
		panImage.grabFocus();

	}

//	private File getUserFile() {
//		int returnVal = fc.showOpenDialog(this);
//		if (returnVal == JFileChooser.APPROVE_OPTION) {
//			File file = fc.getSelectedFile();
//			return file;
//		}
//		return null;
//	}

	private void load(Attachment attach) throws IOException, SQLException {

		InputStream inputStream = SQLUtil
				.blobFromDatabase("SELECT Attachment FROM tblAttachment WHERE ID=" + attach.getId());
		FileType fileType = FileTypeDetector.detectFileType(inputStream);

		if (Util.isAnyOf(fileType, FileType.PNG, FileType.JPEG)) {

			panImage.setImage(inputStream);
			// updateImage(SizeFit.IMAGE);

		} else if (fileType == FileType.PDF) {
//			try (PDDocument document = PDDocument.load(inputStream)) {

				// PDFRenderer pdfRenderer = new PDFRenderer(document);
				// int pageCounter = 0;
				// for (PDPage page : document.getPages()) {
				// // note that the page number parameter is zero based
				// BufferedImage bim =
				// pdfRenderer.renderImageWithDPI(pageCounter++, 300,
				// ImageType.RGB);
				// System.out.println("");
				// // suffix in filename will be used as the file format
				// // ImageIOUtil.writeImage(bim, pdfFilename + "-" +
				// (pageCounter++) + ".png",
				// // 300);
				// }
//			}
		} else {
			throw new IOException("File type not supported.");
		}
	}

	private void setUpWindow() {
		this.setMinimumSize(new Dimension(100, 100));
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(null);
	}

	// @SuppressWarnings("incomplete-switch")
	// private void updateImage(SizeFit fitTo) {
	//
	// panImage.fit(fitTo);
	// // TODO: resize this window according to img
	// Dimension imgSize = panImage.getImageSize();
	// Dimension windowSizeNew = new Dimension(imgSize.getWidth() +
	// contentMargin.getWidth(),
	// imgSize.getHeight() + contentMargin.getHeight());
	// setSize(windowSizeNew);
	//
	// switch (fitTo) {
	// case WINDOW:
	// // maximum size if image to display based on window size
	//// contentSizeMax = new Dimension(getWidth() - contentMargin.getWidth(),
	//// getHeight() - contentMargin.getHeight());
	////
	//// // resize the image
	//// panImage.resizeImage(contentSizeMax);
	////
	//// // size of displayed image
	//// imgSize = panImage.getDisplayedImageSize();
	////
	//// // resize window
	//// windowSizeNew = new Dimension(imgSize.getWidth() +
	// contentMargin.getWidth(),
	//// imgSize.getHeight() + contentMargin.getHeight());
	//// setSize(windowSizeNew);
	//
	// break;
	//
	// case NONE:
	//
	// break;
	// }
	// }

//	private class ActionListener implements java.awt.event.ActionListener {
//
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			if (e.getSource().equals(btnClose)) {
//				dispose();
//			} else if (e.getSource().equals(btnReset)) {
//				// updateImage(SizeFit.IMAGE);
//			}
//		}
//	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1000, 900);
	}

}
