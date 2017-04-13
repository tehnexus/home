package org.tehnexus.image.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;

import org.tehnexus.image.ImagePanel;

public class MouseWheelImageListener implements java.awt.event.MouseWheelListener {

	private final ImagePanel component;
	
	public MouseWheelImageListener(ImagePanel component) {
		this.component = component;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
			
			System.out.println("ctrl+wheel");
			
			String message;
			int notches = e.getWheelRotation();
			if (notches < 0) {
				message = "Mouse wheel moved UP " + -notches + " notch(es)";
//				updateImage(SizeFit.NONE);
			}
			else {
				message = "Mouse wheel moved DOWN " + notches + " notch(es)";
//				updateImage(SizeFit.NONE);
			}

			if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
				message += "    Scroll type: WHEEL_UNIT_SCROLL";
				message += "    Scroll amount: " + e.getScrollAmount() + " unit increments per notch";
				message += "    Units to scroll: " + e.getUnitsToScroll() + " unit increments";
				message += "    Vertical unit increment: " // +
															// scrollPane.getVerticalScrollBar().getUnitIncrement(1)
						+ " pixels";
			}
			else { // scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
				message += "    Scroll type: WHEEL_BLOCK_SCROLL";
				message += "    Vertical block increment: " // +
															// scrollPane.getVerticalScrollBar().getBlockIncrement(1)
						+ " pixels";
			}
//			System.out.println(message);
		}
	}

}
