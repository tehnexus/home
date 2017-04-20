package com.github.tehnexus.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.github.tehnexus.awt.Dimension;
import com.github.tehnexus.awt.ImageHelper;
import com.github.tehnexus.image.listeners.ImageAdapter;
import com.github.tehnexus.image.listeners.XMouseAdapter;

public class ImagePanel extends JPanel {

	private Point			location;
	private BufferedImage	imageOriginal;
	private BufferedImage	image;

	public ImagePanel() {
		init();
	}

	public BufferedImage getImage() {
		return image;
	}

	public Point getImageLocation() {
		return location;
	}

	// public Dimension getImageSize() {
	// return new Dimension(image.getWidth(), image.getHeight());
	// }

	@Override
	public Dimension getSize() {
		return new Dimension(getWidth(), getHeight());
	}

	public boolean hasImage() {
		return (imageOriginal != null);
	}

	private void init() {
		setDoubleBuffered(true);
		setLayout(new BorderLayout());
		setBackground(Color.BLACK);

		this.addComponentListener(new ImageAdapter(this));
		XMouseAdapter ma = new XMouseAdapter(this);
		this.addMouseListener(ma);
		this.addMouseMotionListener(ma);
		this.addMouseWheelListener(ma);

		this.grabFocus();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		g2D.drawImage(image, location.x, location.y, this);
	}

	public void setImage(InputStream imageStream) throws IOException {
		imageOriginal = ImageIO.read(imageStream);
		imageStream.close();
		image = imageOriginal;

		Dimension pan = new Dimension(getWidth(), getHeight());
		Dimension img = new Dimension(image.getWidth(), image.getHeight());

		int x = pan.width > img.width ? (pan.width - img.width) / 2 : 0;
		int y = pan.height > img.height ? (pan.height - img.height) / 2 : 0;

		setImageLocation(x, y);
		return;
	}

	public void setImageLocation() {
		setImageLocation(location.x, location.y);
	}

	public void setImageLocation(int x, int y) {
		// Prevent user from moving image out of screen
		// x = image.getWidth() + x < getWidth() ? location.x : x;
		// y = image.getHeight() + y < getHeight() ? location.y : y;

		// Move image if window is resized
		// x = getWidth() > image.getWidth() + x ? x + getWidth() -
		// (image.getWidth() + x) : x;
		// y = getHeight() > image.getHeight() + y ? y + getHeight() -
		// (image.getHeight() + y) : y;

		// Prevent user from moving image out of screen
		// x = x > 0 ? 0 : x;
		// y = y > 0 ? 0 : y;

		setImageLocation(new Point(x, y));
	}

	private void setImageLocation(Point p) {
		location = p;
	}

	// private void setSize(Dimension imgSize) {
	// image = ImageHelper.getScaledInstanceToFit(image, imgSize);
	// }

	public void resizeImage(double scaleFactor) {

		Dimension sizeO = new Dimension(imageOriginal.getWidth(), imageOriginal.getHeight());
		Dimension size = new Dimension(image.getWidth(), image.getHeight());

		double scaleX = (double) (size.width) / sizeO.width;

		double scale = scaleFactor > 0 ? scaleX * scaleFactor : scaleX / Math.abs(scaleFactor);

		System.out.println("scaleX: " + scaleX + " - scale: " + scale);

		// size.setSize(size.width * scaleFactor / 100, size.height *
		// scaleFactor / 100);

		// setSize(size);

		// image = ImageHelper.getScaledInstanceToFit(image, imgSize);
		image = ImageHelper.getScaledInstanceToFit(imageOriginal, scale);
		repaint();
	}

}