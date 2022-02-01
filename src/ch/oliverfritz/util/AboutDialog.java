package ch.oliverfritz.util;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

public class AboutDialog extends Dialog {

	public AboutDialog(Frame frame, String title, String message, String OK) {
		this(frame, title, Arrays.asList(message.split(System.lineSeparator())), OK);
	}

	public AboutDialog(Frame frame, String title, List<String> message, String OK) {
		super(frame, title, true);
		setLocationRelativeTo(frame);
		addWindowListener(new WindowDisposer(false));

		// Trim, center, and add an empty line before and after the message
		message.replaceAll(String::trim);
		message.add(0, MoreUtil.EMPTY_STRING);
		message.add(MoreUtil.EMPTY_STRING);
		Panel pMessage = new Panel(new GridLayout(0, 1));
		message.forEach(s -> pMessage.add(new Label(s, Label.CENTER)));

		// OK button in separate Panel. Listens to press action and to Enter key (in
		// addition to the default Space key!).
		Button bOK = new Button(MoreUtil.centerString(10, OK));
		bOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		bOK.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					dispose();
			}
		});
		Panel pOK = new Panel();
		pOK.add(bOK);

		// Layout (default for Dialog is BorderLayout)
		add(pMessage, BorderLayout.CENTER);
		add(pOK, BorderLayout.PAGE_END);

		// Size
		FontMetrics fm = getFontMetrics(pMessage.getFont());
		int w = message.stream().mapToInt(s -> fm.stringWidth(s)).max().getAsInt();
		int h = message.size() * fm.getHeight();
		setMinimumSize(new Dimension(w + 100, h + 100));
	}
}
