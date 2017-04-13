package com.github.tehnexus.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.github.tehnexus.awt.Dimension;
import com.github.tehnexus.awt.ImageHelper;
import com.github.tehnexus.image.listeners.ImageAdapter;
import com.github.tehnexus.image.listeners.MouseWheelImageListener;

public class ImagePanel extends JPanel {

	private JLabel			imgLabel	= new JLabel();
	private BufferedImage	image;

	public ImagePanel() {
		init();
	}

	public void setImage(InputStream imageStream) throws IOException  {
		image = ImageIO.read(imageStream);
		imageStream.close();
		imgLabel.setIcon(new ImageIcon(image));
	}

	private void init() {
		setLayout(new BorderLayout());
		setBackground(Color.MAGENTA);
		add(imgLabel, BorderLayout.CENTER);
		this.addComponentListener(new ImageAdapter(this));
		this.addMouseWheelListener(new MouseWheelImageListener(this));
	}

	@Override
	public void grabFocus() {
		imgLabel.grabFocus();
	}

	public void resizeImage(Dimension imgSize) {
		imgLabel.setIcon(new ImageIcon(ImageHelper.getScaledInstanceToFit(image, imgSize)));
	}

	public boolean hasImage() {
		return (image != null);
	}

	public Dimension getImageSize() {
		return Dimension.fromImageIcon((ImageIcon) imgLabel.getIcon());
	}
}