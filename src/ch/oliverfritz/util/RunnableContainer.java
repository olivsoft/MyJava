package ch.oliverfritz.util;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

/**
 * This is an abstract superclass for animated visualizations.
 * 
 * @author Oliver Fritz
 */
public abstract class RunnableContainer extends Container implements Runnable {

	private Thread animation;
	private int frameRate = 50;
	private boolean running = false;
	private boolean terminated = false;
	private List<StateListener> stateListeners;

	/**
	 * Convenience method for the synchronized () wait() construct
	 * 
	 * @param WaitingObject
	 * @param SynchObject
	 * @throws InterruptedException
	 */
	public static void waitSynch(Object WaitingObject, Object SynchObject) throws InterruptedException {
		synchronized (SynchObject) {
			WaitingObject.wait();
		}
	}

	/**
	 * Convenience method for the synchronized () notify() construct
	 * 
	 * @param WaitingObject
	 * @param SynchObject
	 * @throws InterruptedException
	 */
	public static void notifySynch(Object WaitingObject, Object SynchObject) {
		synchronized (SynchObject) {
			WaitingObject.notify();
		}
	}

	/**
	 * Constructor
	 */
	public RunnableContainer() {
		super();
		animation = new Thread(this);
		stateListeners = new ArrayList<StateListener>();
	}

	/**
	 * Constructor with initial frame rate
	 */
	public RunnableContainer(int frameRate) {
		this();
		setFrameRate(frameRate);
	}

	/**
	 * This method must be overridden. It will be called once for each animation
	 * frame.
	 */
	protected abstract void repaintContainer();

	/**
	 * This interface allows implementing classes to be registered for stateChanged
	 * events from the StateListener interface
	 */
	public interface Receiver {
		public void setRunnableContainer(RunnableContainer runnableContainer);
	}

	/**
	 * This interface registers interested receivers for stateChanged events
	 */
	@FunctionalInterface
	public interface StateListener {
		public void stateChanged(boolean isRunning);
	}

	/**
	 * Add a StateListener for receiving stateChanged events
	 * 
	 * @param stateListener
	 */
	public void addStateListener(StateListener stateListener) {
		stateListeners.add(stateListener);
	}

	/**
	 * Remove a StateListener and stop it receiving stateChanged events
	 * 
	 * @param stateListener
	 */
	public void removeStateListener(StateListener stateListener) {
		stateListeners.remove(stateListener);
	}

	/**
	 * @return The frame rate in frames per second
	 */
	public int getFrameRate() {
		return frameRate;
	}

	/**
	 * Set the frame rate in frames per second
	 * 
	 * @param frameRate
	 */
	public void setFrameRate(int frameRate) {
		this.frameRate = Math.max(frameRate, 1);
	}

	/**
	 * @return True if the animation is started or running, false if it is paused
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Use this method to start, pause and continue the animation. The registered
	 * listeners are informed about the state.
	 * 
	 * @param running Set to true for starting or continuation, false for pausing.
	 */
	public void setRunning(boolean running) {
		if (this.running == running)
			return;
		this.running = running;
		// We only need to do something here if the state is set
		// to running: Either start or release from wait state.
		if (running) {
			if (!animation.isAlive())
				animation.start();
			else
				notifySynch(this, this);
		}
		// Inform all interested listeners
		stateListeners.forEach(sl -> sl.stateChanged(running));
	}

	/**
	 * This implementation of the Runnable interface will continuously call
	 * {@link #repaintContainer()} according to the {@link #setFrameRate(int)
	 * frameRate} set. It also contains logic for the pause and continue as well as
	 * the terminate commands.
	 */
	@Override
	public void run() {
		try {
			while (!terminated) {
				// Check for state and sleep or wait accordingly.
				if (running)
					// Add minimal time for painting.
					Thread.sleep(999 / frameRate);
				else
					waitSynch(this, this);
				// Do the main thing: repaint and animate the container!
				// If the implementation is asynchronous it will
				// take place during the next sleep or wait.
				repaintContainer();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method closes the run task and thread nicely
	 */
	public void terminate() {
		terminated = true;
		if (!running)
			setRunning(true);
		try {
			animation.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
