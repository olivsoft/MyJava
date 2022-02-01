package ch.oliverfritz.util;

import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

/**
 * Various useful constants and static functions
 * 
 * @author Oliver Fritz
 */
public final class MoreUtil {

	public static final String EMPTY_STRING = "";

	public static final String PNG_TYPE = "png";
	public static final String PNG_EXT = "." + PNG_TYPE;
	public static final String PNG_FILTER = "*" + PNG_EXT;

	public static Logger getLogger(Class<?> c) {
		return getLogger(c.getSimpleName());
	}

	public static Logger getLogger(String name) {
		final String KEY_LOGFORMAT = "java.util.logging.SimpleFormatter.format";
		final String LOGFORMAT = "%1$tF %1$tT | %4$-12s | %2$-55s | %5$s%6$s%n";
		System.setProperty(KEY_LOGFORMAT, LOGFORMAT);
		return Logger.getLogger(name);
	}

	// Little helper for centering
	public static String centerString(int width, String s) {
		return String.format("%-" + width + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
	}

	// Preferences are actually stored in the registry under
	// HKEY_CURRENT_USER\Software\JavaSoft\Prefs
	public static Preferences getClassPreferences(Class<?> c) {
		return Preferences.userNodeForPackage(c).node(c.getSimpleName().toLowerCase());
	}

	// Loading icons and text from resources is a lengthy matter
	public static Image getImageResource(Object o, String resource) {
		try {
			return ImageIO.read(o.getClass().getClassLoader().getResourceAsStream(resource));
		} catch (IOException e) {
			return null;
		}
	}

	public static Stream<String> readAllStream(InputStream is, Charset cs, boolean trim) {
		Stream<String> ss = new BufferedReader(new InputStreamReader(is, cs)).lines();
		return trim ? ss.map(s -> s.trim()) : ss;
	}

	public static String readAllText(InputStream is, Charset cs, boolean trim) {
		return readAllStream(is, cs, trim).collect(Collectors.joining(System.lineSeparator()));
	}

	public static List<String> readAllLines(InputStream is, Charset cs, boolean trim) {
		return readAllStream(is, cs, trim).collect(Collectors.toList());
	}

	// Allows for lambda expression in mouse handler
	public static MouseListener mousePressedAdapter(Consumer<MouseEvent> c) {
		return new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				c.accept(e);
			}
		};
	}
}