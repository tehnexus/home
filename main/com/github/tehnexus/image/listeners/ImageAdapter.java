package com.github.tehnexus.image.listeners;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.SwingUtilities;

import com.github.tehnexus.image.ImagePanel;

public class ImageAdapter extends ComponentAdapter {

	private final ImagePanel panImage;

	public ImageAdapter(ImagePanel panImage) {
		this.panImage = panImage;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (panImage.hasImage()) {
			SwingUtilities.invokeLater(() -> {
				panImage.fitImage();
				panImage.validateImageLocation();
			});
		}
	}

}
