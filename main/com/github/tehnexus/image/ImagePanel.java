package com.github.tehnexus.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
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

	private static final int	MARGIN_PX	= 20;

	private Point				location;
	private BufferedImage		imageOriginal;
	private BufferedImage		image;
	private SizeFit				sizeFit		= SizeFit.WINDOW;

	public ImagePanel() {
		init();
	}

	public void fitImage() {
		switch (sizeFit) {
			case IMAGE:
				image = imageOriginal;
				initImageLocation();
				break;
			case NONE:
				break;
			case WINDOW:
				resizeImage(0d);
				break;
			default:
				break;

		}
	}

	public BufferedImage getImage(boolean original) {
		if (original)
			return imageOriginal;

		return image;
	}

	public Point getImageLocation() {
		if (location == null)
			return new Point(0, 0);
		return location;
	}

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

		addComponentListener(new ImageAdapter(this));
		XMouseAdapter ma = new XMouseAdapter(this);
		addMouseListener(ma);
		addMouseMotionListener(ma);
		addMouseWheelListener(ma);

		grabFocus();
	}

	private void initImageLocation() {

		Dimension pan = new Dimension(getWidth(), getHeight());
		Dimension img = new Dimension(image.getWidth(), image.getHeight());

		int x = pan.width > img.width ? (pan.width - img.width) / 2 : 0;
		int y = pan.height > img.height ? (pan.height - img.height) / 2 : 0;

		setImageLocation(x, y);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D) g;
		g2D.drawImage(image, location.x, location.y, this);
	}

	public void resizeImage(double scaleFactor) {

		setCursor(new Cursor(Cursor.WAIT_CURSOR));

		if (scaleFactor == 0d) { // fit window
			Dimension panDim = new Dimension(getWidth() - MARGIN_PX, getHeight() - MARGIN_PX);
			Dimension imgDim = new Dimension(image.getWidth(), image.getHeight());
			imgDim.fitInto(panDim, true); // TODO: rewrite so fitInto also works
											// for
			// increasing size of img
			image = ImageHelper.getScaledInstanceToFit(imageOriginal, imgDim);
		}
		else { // according to zoom level
			double scaleX = (double) (image.getWidth()) / imageOriginal.getWidth();
			double scale = scaleFactor > 0 ? scaleX * scaleFactor : scaleX / Math.abs(scaleFactor);
			image = ImageHelper.getScaledInstanceToFit(imageOriginal, scale);
		}

		validateImageLocation();
		setCursor(Cursor.getDefaultCursor());
	}

	public void setImage(InputStream inputStream) throws IOException {
		imageOriginal = ImageIO.read(inputStream);
		image = imageOriginal;

		initImageLocation();
	}

	public boolean setImageLocation(int x, int y) {

		Dimension panDim = new Dimension(getWidth(), getHeight());
		Dimension imgDim = new Dimension(image.getWidth(), image.getHeight());

		// moved in
		x = x > MARGIN_PX ? MARGIN_PX : x;
		y = y > MARGIN_PX ? MARGIN_PX : y;

		// moved out
		x = imgDim.width + x + MARGIN_PX < getWidth() ? x + Math.abs(imgDim.width + x - getWidth()) - MARGIN_PX : x;
		y = imgDim.height + y + MARGIN_PX < getHeight() ? y + Math.abs(imgDim.height + y - getHeight()) - MARGIN_PX : y;

		// center image if image smaller than panel
		x = panDim.width > imgDim.width ? (panDim.width - imgDim.width) / 2 : x;
		y = panDim.height > imgDim.height ? (panDim.height - imgDim.height) / 2 : y;

		Point pOld = getImageLocation();
		Point pNew = new Point(x, y);

		setImageLocation(new Point(x, y));

		boolean b = (pOld.x == pNew.x && pOld.y == pNew.y) ? true : false;
		return b;
	}

	private void setImageLocation(Point p) {
		location = p;
		repaint();
	}

	public void setSizeFit(SizeFit fit) {
		sizeFit = fit;
		fitImage();
	}

	public void validateImageLocation() {
		setImageLocation(location.x, location.y);
	}

}