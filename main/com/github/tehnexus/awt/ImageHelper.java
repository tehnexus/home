package com.github.tehnexus.awt;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

public class ImageHelper extends BufferedImage {

	private final static int DEAFULT_TYPE = BufferedImage.TYPE_INT_ARGB;

	public ImageHelper(int width, int height) {
		super(width, height, DEAFULT_TYPE);
	}

	public ImageHelper(int width, int height, int type) {
		super(width, height, type);
	}

	public static BufferedImage getScaledInstanceToFit(BufferedImage img, double scaleFactor) {
		return getScaledInstance(img, scaleFactor);
	}

	public static BufferedImage getScaledInstanceToFit(BufferedImage img, Dimension size) {
		double scaleFactor = getScaleFactorToFit(img, size);
		return getScaledInstance(img, scaleFactor);
	}

	private static BufferedImage getScaledInstance(BufferedImage img, double dScaleFactor) {

		BufferedImage imgScale = img;

		int iImageWidth = (int) Math.round(img.getWidth() * dScaleFactor);
		int iImageHeight = (int) Math.round(img.getHeight() * dScaleFactor);

		if (dScaleFactor < 1.0d) {
			imgScale = getScaledDownInstance(img, iImageWidth, iImageHeight);
		}
		else if (dScaleFactor > 1.0d) {
			imgScale = getScaledUpInstance(img, iImageWidth, iImageHeight);
		}
		else {
			imgScale = img;
		}
		return imgScale;
	}

	private static BufferedImage getScaledDownInstance(BufferedImage img, int targetWidth, int targetHeight) {

		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;

		BufferedImage ret = img;

		if (targetHeight > 0 || targetWidth > 0) {

			int w = img.getWidth();
			int h = img.getHeight();

			do {

				if (w > targetWidth) {
					w /= 2;
					if (w < targetWidth) {
						w = targetWidth;
					}
				}

				if (h > targetHeight) {
					h /= 2;
					if (h < targetHeight) {
						h = targetHeight;
					}
				}

				BufferedImage tmp = new BufferedImage(Math.max(w, 1), Math.max(h, 1), type);
				Graphics2D g2 = tmp.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.drawImage(ret, 0, 0, w, h, null);
				g2.dispose();

				ret = tmp;
			} while (w != targetWidth || h != targetHeight);
		}
		else {
			ret = new BufferedImage(1, 1, type);
		}
		return ret;
	}

	private static BufferedImage getScaledUpInstance(BufferedImage img, int targetWidth, int targetHeight) {

		int type = BufferedImage.TYPE_INT_ARGB;

		BufferedImage ret = img;
		int w = img.getWidth();
		int h = img.getHeight();

		do {

			if (w < targetWidth) {
				w *= 2;
				if (w > targetWidth) {
					w = targetWidth;
				}
			}

			if (h < targetHeight) {
				h *= 2;
				if (h > targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
			tmp = null;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}

	private static double getScaleFactorToFit(BufferedImage img, Dimension size) {

		double dScale = 1;

		if (img != null) {
			int imageWidth = img.getWidth();
			int imageHeight = img.getHeight();

			dScale = getScaleFactorToFit(new Dimension(imageWidth, imageHeight), size);
		}
		return dScale;
	}

	private static double getScaleFactor(int iMasterSize, int iTargetSize) {

		double dScale = 1;
		dScale = (double) iTargetSize / (double) iMasterSize;

		return dScale;
	}

	private static double getScaleFactorToFit(Dimension original, Dimension toFit) {

		double dScale = 1d;

		if (original != null && toFit != null) {
			double dScaleWidth = getScaleFactor(original.width, toFit.width);
			double dScaleHeight = getScaleFactor(original.height, toFit.height);

			dScale = Math.min(dScaleHeight, dScaleWidth);
		}
		return dScale;
	}

	// private static double getScaleFactorToFill(Dimension masterSize,
	// Dimension targetSize) {
	//
	// double dScaleWidth = getScaleFactor(masterSize.width, targetSize.width);
	// double dScaleHeight = getScaleFactor(masterSize.height,
	// targetSize.height);
	//
	// double dScale = Math.max(dScaleHeight, dScaleWidth);
	// return dScale;
	// }
}
