package fr.sii.ogham.common;

import static fr.sii.ogham.common.TestLogger.Characters.BOTTOM_LEFT;
import static fr.sii.ogham.common.TestLogger.Characters.BOTTOM_RIGHT;
import static fr.sii.ogham.common.TestLogger.Characters.HORIZONTAL;
import static fr.sii.ogham.common.TestLogger.Characters.TOP_LEFT;
import static fr.sii.ogham.common.TestLogger.Characters.TOP_RIGHT;
import static fr.sii.ogham.common.TestLogger.Characters.VERTICAL;
import static fr.sii.ogham.common.TestLogger.Characters.VERTICAL_LEFT;
import static fr.sii.ogham.common.TestLogger.Characters.VERTICAL_RIGHT;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class TestLogger {
	public static final int DEFAULT_MAX_LENGTH = 100;
	private static final Logger LOG = LoggerFactory.getLogger("");
	private static final String[] SINGLE = { "┌", "┐", "└", "┘", "─", "│", "├", "┤" };
	private static final String[] DOUBLE = { "╔", "╗", "╚", "╝", "═", "║", "╠", "╣" };

	private final int maxLength;

	public TestLogger() {
		this(DEFAULT_MAX_LENGTH);
	}

	public TestLogger(int maxLength) {
		super();
		this.maxLength = maxLength;
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
		LOG.info("\n{}\n{}\n{}", 
				borderTop(DOUBLE), 
				format(testName, DOUBLE), 
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
		LOG.info("\n{}\n{}\n{}\n{}\n{}\r\n\r\n", 
				borderTop(SINGLE), 
				format(testName, SINGLE), 
				borderMiddle(SINGLE), 
				format("SUCCESS", SINGLE), 
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
	public void writeFailure(String testName, Throwable e) {
		LOG.error("Test failure:\r\n", e);
		// @formatter:off
		LOG.error("\n{}\n{}\n{}\n{}\n{}\n{}\n{}\r\n\r\n", 
				borderTop(SINGLE), 
				format(testName, SINGLE), 
				borderMiddle(SINGLE), 
				format("FAILED", SINGLE), 
				borderMiddle(SINGLE),
				format(e.getMessage(), SINGLE), 
				borderBottom(SINGLE));
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
		return Arrays.asList(WordUtils.wrap(text, maxLength - 3, "\n", true).split("\n"));
	}

	enum Characters {
		TOP_LEFT(0), TOP_RIGHT(1), BOTTOM_LEFT(2), BOTTOM_RIGHT(3), HORIZONTAL(4), VERTICAL(5), VERTICAL_LEFT(6), VERTICAL_RIGHT(7);

		private final int pos;

		Characters(int pos) {
			this.pos = pos;
		}

		public String character(String[] characters) {
			return characters[pos];
		}
	}
}
