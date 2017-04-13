package org.tehnexus.image.listeners;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import org.tehnexus.image.ImagePanel;

public class ImageAdapter extends ComponentAdapter {

	private final ImagePanel component;
	
	public ImageAdapter(ImagePanel component) {
		this.component = component;
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		System.out.println("imagepanel resized, image: " + component.hasImage());
	}
	
}
