package fr.sii.ogham.testing.assertion.html;

import fr.sii.ogham.testing.assertion.util.HtmlUtils;
import org.junit.jupiter.api.AssertionFailureBuilder;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

/**
 * Utility class for checking HTML content.
 * 
 * @author Aurélien Baudet
 *
 */
public final class AssertHtml {
	private static final Logger LOG = LoggerFactory.getLogger(AssertHtml.class);

	/**
	 * Check if the HTML is identical to the expected. The HTML strings are
	 * parsed into {@link Document}s. Two documents are considered to be
	 * "identical" if they contain the same elements and attributes in the same
	 * order.
	 * <p>
	 * For each difference, the difference will be logged with error level. It
	 * will generate a {@link AssertionFailedError} with the expected string and
	 * actual string in order to be able to visualize the differences on sources
	 * directly in the IDE.
	 * </p>
	 * 
	 * @param expected
	 *            the expected HTML
	 * @param actual
	 *            the HTML content to check
	 */
	public static void assertEquals(String expected, String actual) {
		Diff diff = HtmlUtils.compare(expected, actual, true);
		if (diff.hasDifferences()) {
			logDifferences(diff);
			AssertionFailureBuilder.assertionFailure()
					.message("HTML element different to expected one. See logs for details about found differences.\n")
					.expected(expected)
					.actual(actual)
					.reason(diff.toString())
					.buildAndThrow();
		}
	}

	/**
	 * Check if the HTML is similar to the expected. The HTML strings are parsed
	 * into {@link Document}s. Two documents are considered to be "similar" if
	 * they contain the same elements and attributes regardless of order.
	 * <p>
	 * For each difference, the difference will be logged with error level. It
	 * will generate a {@link AssertionFailedError} with the expected string and
	 * actual string in order to be able to visualize the differences on sources
	 * directly in the IDE.
	 * </p>
	 * 
	 * @param expected
	 *            the expected HTML
	 * @param actual
	 *            the HTML content to check
	 */
	public static void assertSimilar(String expected, String actual) {
		Diff diff = HtmlUtils.compare(expected, actual, false);
		if (diff.hasDifferences()) {
			logDifferences(diff);
			AssertionFailureBuilder.assertionFailure()
					.message("HTML element different to expected one. See logs for details about found differences.\n")
					.expected(expected)
					.actual(actual)
					.reason(diff.toString())
					.buildAndThrow();
		}
	}

	private static void logDifferences(Diff diff) {
		for (Difference difference : diff.getDifferences()) {
			LOG.error(difference.toString()); // NOSONAR
		}
	}

	private AssertHtml() {
		super();
	}
}
