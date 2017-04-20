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
	private Point				imgLocOld;

	public XMouseAdapter(ImagePanel panImage) {
		this.panImage = panImage;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// System.out.println("mousePressed");
		clickLoc = e.getPoint();
		imgLocOld = panImage.getImageLocation();
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
		int xOffset = (imgLocOld.x + mouseLoc.x) - (imgLocOld.x + clickLoc.x);
		int yOffset = (imgLocOld.y + mouseLoc.y) - (imgLocOld.y + clickLoc.y);

		// Move picture to this position
		panImage.setImageLocation(imgLocOld.x + xOffset, imgLocOld.y + yOffset);
		panImage.repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {

			String message;
			int notches = e.getWheelRotation();
			if (notches < 0) {
				message = "Mouse wheel moved UP " + -notches + " notch(es)";
				panImage.resizeImage(1.2);
				// updateImage(SizeFit.NONE);
			} else {
				message = "Mouse wheel moved DOWN " + notches + " notch(es)";
				panImage.resizeImage(-1.2);
				// updateImage(SizeFit.NONE);
			}

			if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
				message += "    Scroll type: WHEEL_UNIT_SCROLL";
				message += "    Scroll amount: " + e.getScrollAmount() + " unit increments per notch";
				message += "    Units to scroll: " + e.getUnitsToScroll() + " unit increments";

			} else { // scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
				message += "    Scroll type: WHEEL_BLOCK_SCROLL";

			}
//			System.out.println(message);
		}
	}

}
