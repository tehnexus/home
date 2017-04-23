package com.github.tehnexus.image.listeners;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.github.tehnexus.image.ImagePanel;

public class XMouseAdapter extends MouseAdapter {

	private final ImagePanel	panImage;
	private Point				clickLoc;
	private Point				imgLocation;

	public XMouseAdapter(ImagePanel panImage) {
		this.panImage = panImage;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		clickLoc = e.getPoint();
		imgLocation = panImage.getImageLocation();
		Component c = panImage;
		c.setCursor(new Cursor(Cursor.MOVE_CURSOR));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Component c = panImage;
		c.setCursor(Cursor.getDefaultCursor());
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		Point mouseLoc = e.getPoint();

		// Determine how much the mouse moved since the initial click
		int xOffset = (imgLocation.x + mouseLoc.x) - (imgLocation.x + clickLoc.x);
		int yOffset = (imgLocation.y + mouseLoc.y) - (imgLocation.y + clickLoc.y);

		// if false is returned the image was not moved for any reason
		if (!panImage.setImageLocation(imgLocation.x + xOffset, imgLocation.y + yOffset)) {
			mousePressed(e);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		imgLocation = panImage.getImageLocation();

		int notches = e.getWheelRotation();

		// CTRL+scroll, zoom
		if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
			// zoom in
			if (notches < 0) {
				panImage.resizeImage(1.2);
			}
			else { // zoom out
				panImage.resizeImage(-1.2);
			}
		}
		else if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK) {
			// SHIFT+scroll, manipulate x-coordinate
			panImage.setImageLocation(imgLocation.x + (notches * -10), imgLocation.y);
		}
		else { // usual scroll, manipulate y-coordinate
			panImage.setImageLocation(imgLocation.x, imgLocation.y + (notches * -10));
		}
	}

}
