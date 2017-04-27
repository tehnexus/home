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

	private Color			bgColor		= Color.WHITE;
	private int				margin_px	= 10;

	private boolean			allowZoom	= true;
	private boolean			allowMove	= true;
	private Point			location	= new Point(margin_px, margin_px);

	private BufferedImage	imageOriginal;
	private BufferedImage	image;
	private SizeFit			sizeFit		= SizeFit.WINDOW;

	public ImagePanel() {
		init();
	}

	public ImagePanel(boolean allowZoom, boolean allowMove) {
		this.allowZoom = allowZoom;
		this.allowMove = allowMove;
		init();
	}

	public void clear() {
		imageOriginal = null;
		image = null;
		repaint();
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
		setBackground(bgColor);

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

		int x = pan.width > img.width ? (pan.width - img.width) / 2 : margin_px;
		int y = pan.height > img.height ? (pan.height - img.height) / 2 : margin_px;

		setImageLocation(x, y);
	}

	public boolean isAllowMove() {
		return allowMove;
	}

	public boolean isAllowZoom() {
		return allowZoom;
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
			Dimension panDim = new Dimension(getWidth() - margin_px, getHeight() - margin_px);
			Dimension imgDim = new Dimension(image.getWidth(), image.getHeight());
			imgDim.fitInto(panDim, true);
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

	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	public void setImage(InputStream inputStream) throws IOException {
		imageOriginal = ImageIO.read(inputStream);
		image = imageOriginal;

		resizeImage(0d);
		initImageLocation();
	}

	public boolean setImageLocation(int x, int y) {

		Dimension panDim = new Dimension(getWidth(), getHeight());
		Dimension imgDim = new Dimension(image.getWidth(), image.getHeight());

		// moved in
		x = x > margin_px ? margin_px : x;
		y = y > margin_px ? margin_px : y;

		// moved out
		x = imgDim.width + x + margin_px < getWidth() ? x + Math.abs(imgDim.width + x - getWidth()) - margin_px : x;
		y = imgDim.height + y + margin_px < getHeight() ? y + Math.abs(imgDim.height + y - getHeight()) - margin_px : y;

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

	public void setMargin_px(int margin_px) {
		this.margin_px = margin_px;
	}

	public void setSizeFit(SizeFit fit) {
		if (fit != sizeFit) {
			sizeFit = fit;
			fitImage();
		}
	}

	public void validateImageLocation() {
		setImageLocation(location.x, location.y);
	}

}