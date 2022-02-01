package ch.oliverfritz.util;

import java.awt.Component;
import java.awt.Label;
import java.awt.Panel;

public class LabeledComponentPanel extends Panel {

	public LabeledComponentPanel(String label, Component component) {
		super();
		add(new Label(label));
		add(component);
	}
}
