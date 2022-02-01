package ch.oliverfritz.util;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;

/**
 * Overrides some logic of a Canvas in order to allow for fast and smooth
 * plotting.
 */
public abstract class BufferedCanvas extends Canvas {
	private static final int numBuffers = 2;
	private boolean hasBS = false;
	private GraphicsConfiguration graphicsConfiguration = null;
	private VolatileImage steadyImage = null;
	private BufferStrategy bufferStrategy;

	/**
	 * Override this to paint the steady part
	 * 
	 * @param g The Graphics2D to paint on
	 */
	protected abstract void paintSteadyPart(Graphics2D g);

	/**
	 * Override this to calculate and update the dynamic part
	 */
	protected abstract void updateDynamicPart();

	/**
	 * Override this to paint the dynamic part
	 * 
	 * @param g the Graphics2D to paint on
	 */
	protected abstract void paintDynamicPart(Graphics2D g);

	/**
	 * Call this to clear the canvas completely (e.g. after a mouse action or a
	 * control parameter change).
	 */
	public void clear() {
		if (steadyImage != null)
			steadyImage.flush();
	}

	/**
	 * Call this to get the drawing on a standard Graphics context if a buffering
	 * strategy is used.
	 */
	@Override
	public void paintAll(Graphics g) {
		if (hasBS) {
			hasBS = false;
			// Call only paint() here. paintAll() would also call validate() and recreate
			// the buffer strategy.
			paint(g);
			hasBS = true;
		} else
			super.paintAll(g);
	}

	/**
	 * Painting is done in two steps: the "steady" image does not change from paint
	 * to paint, but the "dynamic" one will. Logic for buffering strategies is
	 * included. In addition, the overhead and graphics strategies of paint() and
	 * update() are eliminated to avoid flicker.
	 */
	@Override
	public final void paint(Graphics g) {
		Graphics2D g2D;
		while (steadyImage == null || steadyImage.contentsLost()) {
			// Recreate and paint the steady image here.
			steadyImage = graphicsConfiguration.createCompatibleVolatileImage(getWidth(), getHeight());
			g2D = (Graphics2D) steadyImage.getGraphics();
			paintSteadyPart(g2D);
			g2D.dispose();
		}
		do {
			// Paint the dynamic part here on top of the steady image.
			g2D = (Graphics2D) (hasBS ? bufferStrategy.getDrawGraphics() : g);
			g2D.drawImage(steadyImage, 0, 0, null);
			paintDynamicPart(g2D);
			g2D.dispose();
			if (hasBS)
				bufferStrategy.show();
		} while (hasBS && bufferStrategy.contentsLost());
	}

	/**
	 * This method is overridden in order to allow for calculating any necessary
	 * updates of the dynamics before calling paint. In addition, the clearing logic
	 * of the superclass is removed.
	 */
	@Override
	public final void update(Graphics g) {
		// Do dynamic calculations here, then paint. Why? update() is only called if a
		// repaint request has been issued. paint(), however, can be called for various
		// reasons and in an uncontrollable way.
		updateDynamicPart();
		paint(g);
	}

	/**
	 * This function is called after the component has been made visible and given a
	 * (new) size. So, all graphics objects can be initialized here. A full repaint
	 * is triggered by calling clear().
	 */
	@Override
	public void validate() {
		if (graphicsConfiguration == null) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			graphicsConfiguration = ge.getDefaultScreenDevice().getDefaultConfiguration();
		}
		if (!hasBS) {
			createBufferStrategy(numBuffers);
			bufferStrategy = getBufferStrategy();
			hasBS = (bufferStrategy != null);
		}
		clear();
	}
}
