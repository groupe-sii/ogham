package fr.sii.ogham.testing.extension.common;

import static fr.sii.ogham.testing.extension.common.TestInformationLogger.Characters.BOTTOM_LEFT;
import static fr.sii.ogham.testing.extension.common.TestInformationLogger.Characters.BOTTOM_RIGHT;
import static fr.sii.ogham.testing.extension.common.TestInformationLogger.Characters.HORIZONTAL;
import static fr.sii.ogham.testing.extension.common.TestInformationLogger.Characters.TOP_LEFT;
import static fr.sii.ogham.testing.extension.common.TestInformationLogger.Characters.TOP_RIGHT;
import static fr.sii.ogham.testing.extension.common.TestInformationLogger.Characters.VERTICAL;
import static fr.sii.ogham.testing.extension.common.TestInformationLogger.Characters.VERTICAL_LEFT;
import static fr.sii.ogham.testing.extension.common.TestInformationLogger.Characters.VERTICAL_RIGHT;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import com.github.jknack.handlebars.internal.text.WordUtils;

/**
 * Write information about test. This is useful when there are many tests:
 * <ul>
 * <li>To quickly find the logs for the test</li>
 * <li>To quickly know if the test has failed or succeeded</li>
 * <li>To quickly identify the test failure</li>
 * <li>To quickly find failed tests</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@SuppressWarnings("squid:S1312")
public class TestInformationLogger {
	public static final int DEFAULT_MAX_LENGTH = 100;
	public static final String DEFAULT_MARKER = "test-info";
	private static final String[] SINGLE = { "┌", "┐", "└", "┘", "─", "│", "├", "┤" };
	private static final String[] DOUBLE = { "╔", "╗", "╚", "╝", "═", "║", "╠", "╣" };

	private final int maxLength;
	private final Printer printer;
	private final String marker;

	/**
	 * Initializes with the default max line length (100), uses this logger as
	 * printer and mark logs with "test-info" marker
	 */
	public TestInformationLogger() {
		this(DEFAULT_MAX_LENGTH);
	}

	/**
	 * Initializes with the provided max line length.
	 * 
	 * Uses this logger as printer and default marker ("test-info").
	 * 
	 * @param maxLength
	 *            the length of each line
	 */
	public TestInformationLogger(int maxLength) {
		this(maxLength, DEFAULT_MARKER);
	}

	/**
	 * Initializes with the provided max line length and marker.
	 * 
	 * Uses this logger as printer.
	 * 
	 * @param maxLength
	 *            the length of each line
	 * @param marker
	 *            the marker for logs
	 */
	public TestInformationLogger(int maxLength, String marker) {
		this(maxLength, marker, new Slf4jPrinter());
	}

	/**
	 * 
	 * @param maxLength
	 *            the length of each line
	 * @param marker
	 *            the marker for logs
	 * @param printer
	 *            the printer
	 */
	public TestInformationLogger(int maxLength, String marker, Printer printer) {
		super();
		this.maxLength = maxLength;
		this.printer = printer;
		this.marker = marker;
	}

	/**
	 * Write the name of the test. The name is boxed to quickly see the test
	 * name in long logs.
	 * 
	 * @param testName
	 *            the name of the test
	 */
	public void writeStart(String testName) {
		// @formatter:off
		printer.printHeader(marker, 
				borderTop(DOUBLE)+"\n"+ 
				format(testName, DOUBLE)+"\n"+ 
				borderBottom(DOUBLE));
		// @formatter:on
	}

	/**
	 * Write the name of the test and "SUCCESS" message. The name is boxed to
	 * quickly see the test name in long logs.
	 * 
	 * @param testName
	 *            the name of the test
	 */
	public void writeSuccess(String testName) {
		// @formatter:off
		printer.printSucess(marker,
					borderTop(SINGLE)+"\n"+ 
					format(testName, SINGLE)+"\n"+ 
					borderMiddle(SINGLE)+"\n"+
					format("SUCCESS", SINGLE)+"\n"+ 
					borderBottom(SINGLE));
		// @formatter:on
	}

	/**
	 * Write the name of the test, "FAILED" message and failure information. The
	 * name is boxed to quickly see the test name in long logs.
	 * 
	 * @param testName
	 *            the name of the test
	 * @param e
	 *            the thrown exception
	 */
	@SuppressWarnings("squid:S4142")
	public void writeFailure(String testName, Throwable e) {
		// @formatter:off
		printer.printFailure(marker,
				borderTop(SINGLE)+"\n"+
				format(testName, SINGLE)+"\n"+ 
				borderMiddle(SINGLE)+"\n"+
				format("FAILED", SINGLE)+"\n"+
				borderMiddle(SINGLE)+"\n"+
				format(e.toString(), SINGLE)+"\n"+ 
				borderBottom(SINGLE), e);
		// @formatter:on
	}

	private String borderTop(String[] characters) {
		return TOP_LEFT.character(characters) + dashLine(HORIZONTAL.character(characters)) + TOP_RIGHT.character(characters);
	}

	private String borderMiddle(String[] characters) {
		return VERTICAL_LEFT.character(characters) + dashLine(HORIZONTAL.character(characters)) + VERTICAL_RIGHT.character(characters);
	}

	private String borderBottom(String[] characters) {
		return BOTTOM_LEFT.character(characters) + dashLine(HORIZONTAL.character(characters)) + BOTTOM_RIGHT.character(characters);
	}

	private String dashLine(String character) {
		return StringUtils.repeat(character, maxLength - 2);
	}

	private String format(String text, String[] characters) {
		String vertical = VERTICAL.character(characters);
		StringJoiner joiner = new StringJoiner(vertical + "\n" + vertical, vertical, vertical);
		for (String line : wrap(text)) {
			joiner.add(StringUtils.rightPad(line, maxLength - 2));
		}
		return joiner.toString();
	}

	private List<String> wrap(String text) {
		return Arrays.asList(WordUtils.wrap(text.replaceAll("\t", "  "), maxLength - 3, "\n", true).split("\n"));
	}

	enum Characters {
		TOP_LEFT(0),
		TOP_RIGHT(1),
		BOTTOM_LEFT(2),
		BOTTOM_RIGHT(3),
		HORIZONTAL(4),
		VERTICAL(5),
		VERTICAL_LEFT(6),
		VERTICAL_RIGHT(7);

		private final int pos;

		Characters(int pos) {
			this.pos = pos;
		}

		public String character(String[] characters) {
			return characters[pos];
		}
	}
}
