package com.github.tehnexus.awt;

/**
 * The <code>Dimension</code> class encapsulates the width and height of a
 * component (in integer precision) in a single object. The class is associated
 * with certain properties of components. Several methods defined by the
 * <code>Component</code> class and the <code>LayoutManager</code> interface
 * return a <code>Dimension</code> object.
 * <p>
 * Normally the values of <code>width</code> and <code>height</code> are
 * non-negative integers. The constructors that allow you to create a dimension
 * do not prevent you from setting a negative value for these properties. If the
 * value of <code>width</code> or <code>height</code> is negative, the behavior
 * of some methods defined by other objects is undefined.
 * <p> 
 *
 * @author Sami Shaio
 * @author Arthur van Hoff
 * @author teHneXus
 * @see java.awt.Component
 * @see java.awt.LayoutManager
 * @since 1.0
 */

public class Dimension extends java.awt.Dimension {

	public static final int	FRAME_MARGIN_HORIZONTAL	= 20;
	public static final int	FRAME_MARGIN_VERTICAL	= 30;

	/**
	 * Creates an instance of <code>Dimension</code> with a width of zero and a height of zero.
	 */
	public Dimension() {
		super();
	}

	/**
	 * Creates an instance of <code>Dimension</code> whose width and height are the same as for the
	 * specified dimension.
	 *
	 * @param d
	 *            the specified dimension for the <code>width</code> and <code>height</code> values
	 */
	public Dimension(Dimension d) {
		super(d);
	}

	public Dimension(double width, double height) {
		setSize(width, height);
	}

	/**
	 * Constructs a <code>Dimension</code> and initializes it to the specified width and specified
	 * height.
	 *
	 * @param width
	 *            the specified width
	 * @param height
	 *            the specified height
	 */
	public Dimension(int width, int height) {
		super(width, height);
	}

	/**
	 * Creates an instance of <code>Dimension</code> whose width and height are the same as for the
	 * specified dimension.
	 *
	 * @param d
	 *            the specified dimension for the <code>width</code> and <code>height</code> values
	 */
	public Dimension(java.awt.Dimension d) {
		super(d);
	}

	public static Dimension fromImageIcon(javax.swing.ImageIcon imgIcon) {
		return new Dimension(imgIcon.getIconWidth(), imgIcon.getIconHeight());
	}

	public boolean equalsSizeOf(Dimension dimension) {
		if (getWidth() == dimension.getWidth() && getHeight() == dimension.getHeight())
			return true;
		return false;
	}

	public void fitInto(Dimension dimensionFit) {
		fitIntoWidth(dimensionFit);
		fitIntoHeight(dimensionFit);
	}

	public void fitInto(double width, double height) {
		fitInto(new Dimension(width, height));
	}

	public void fitInto(int width, int height) {
		fitInto(new Dimension(width, height));
	}

	/**
	 * Uses this <code>Dimension</code> as the biggest possible to decrease either the
	 * <code>width</code> and/or <code>height</code> value(s) of another another
	 * <code>Dimension</code>.
	 * 
	 * @param dimension
	 *            <code>Dimension</code> to resize
	 * @param widthBuffer
	 *            margin left and right
	 * @param heightBuffer
	 *            margin top and bottom
	 * @author neXus
	 */
	public Dimension resize(Dimension dimension, int widthBuffer, int heightBuffer) {

		double width = dimension.getWidth();
		double height = dimension.getHeight();

		int wBuff = widthBuffer * 2;
		int hBuff = heightBuffer * 2;

		if (getWidth() - wBuff < dimension.getWidth())
			width = getWidth() - wBuff;

		if (getHeight() - hBuff < dimension.getHeight())
			height = getHeight() - hBuff;

		return new Dimension(width, height);
	}

	private void fitIntoHeight(Dimension dimensionFit) {
		if (getHeight() > dimensionFit.getHeight()) {
			double f = dimensionFit.getHeight() / getHeight();
			setSize(getWidth() * f, dimensionFit.getHeight());
		}
	}

	private void fitIntoWidth(Dimension dimensionFit) {
		if (getWidth() > dimensionFit.getWidth()) {
			double f = dimensionFit.getWidth() / getWidth();
			setSize(dimensionFit.getWidth(), getHeight() * f);
		}
	}
	
	@Override
	public String toString() {
		return width + " x " + height;
	}

}
