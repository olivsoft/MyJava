package ch.oliverfritz.util;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowDisposer extends WindowAdapter {

	private boolean doExit;
	private int exitCode;

	public WindowDisposer() {
		this(true, 0);
	}

	public WindowDisposer(int exitCode) {
		this(true, exitCode);
	}

	public WindowDisposer(boolean doExit) {
		this(doExit, 0);
	}

	public WindowDisposer(boolean doExit, int exitCode) {
		super();
		this.doExit = doExit;
		this.exitCode = exitCode;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		e.getWindow().dispose();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		if (doExit)
			System.exit(exitCode);
	}
}
