package org.tehnexus.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.tehnexus.awt.Dimension;
import org.tehnexus.awt.ImageHelper;

public class ImagePanel extends JPanel {

	private Dimension		maxSize;
	private JLabel			imgLabel	= new JLabel();
	private BufferedImage	image;

	public ImagePanel() {
	}
	

	public void initialize(Dimension maxSize) {
		this.maxSize = maxSize;
		init();
	}

	private void init() {
		setLayout(new BorderLayout());
		setBackground(Color.MAGENTA);
		add(imgLabel, BorderLayout.CENTER);
	}

	public void setImage(InputStream imageStream) throws IOException {
		image = ImageIO.read(imageStream);
		imageStream.close();
	}

	@Override
	public void grabFocus() {
		imgLabel.grabFocus();
	}

	public void resizeImage(Dimension imgSize) {
		imgLabel.setIcon(new ImageIcon(ImageHelper.getScaledInstanceToFit(image, imgSize)));
	}

	public Dimension getDisplayedImageSize() {
		return Dimension.fromImageIcon((ImageIcon) imgLabel.getIcon());
	}

	public boolean hasImage() {
		return (image != null);
	}

	public void fit(SizeFit fitTo) {
		switch (fitTo) {
		case IMAGE:
			// size of original image, converted to icon so no waiting for imageObserver
			Dimension imgSize = Dimension.fromImageIcon(new ImageIcon(image));

			// fit image size into max content size
			imgSize.fitInto(maxSize);

			// resize the image
			resizeImage(imgSize);

			break;
		case NONE:
			break;
		case WINDOW:
			
			
			
			break;
		default:
			break;

		}
	}


	public Dimension getImageSize() {
		return Dimension.fromImageIcon((ImageIcon) imgLabel.getIcon());
	}


}
