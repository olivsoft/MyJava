package ch.oliverfritz.util;

import java.awt.HeadlessException;
import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.IllegalFormatException;

public class DoubleField extends TextField implements TextListener {

	private String doubleFormat = "%.2f";
	private double lastValue = 0d;

	public DoubleField(String Format, double Value) throws HeadlessException {
		super(String.format(Format, Value));
		setFormat(Format);
		lastValue = Value;
		addTextListener(this);
	}

	public void setFormat(String Format) {
		try {
			// Make sure format is valid
			Double.valueOf(String.format(Format, lastValue));
			this.doubleFormat = Format;
		} catch (IllegalFormatException ife) {
			throw ife;
		}
	}

	public double getValue() {
		try {
			lastValue = Double.valueOf(getText());
			return lastValue;
		} catch (NumberFormatException nfe) {
			// This should not happen. The component checks in the textValueChanged method
			// below if the text represents a numeric value.
			throw nfe;
		}
	}

	public void setValue(double Value) {
		setText(String.format(doubleFormat, Value));
		lastValue = Value;
	}

	@Override
	public void textValueChanged(TextEvent e) {
		try {
			lastValue = Double.valueOf(getText());
		} catch (NumberFormatException nfe) {
			// Do not allow anything but double values in this TextField
			setValue(lastValue);
		}
	}
}
