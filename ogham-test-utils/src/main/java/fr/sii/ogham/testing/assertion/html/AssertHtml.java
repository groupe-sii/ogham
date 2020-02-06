package fr.sii.ogham.testing.assertion.html;

import java.util.List;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Difference;
import org.junit.ComparisonFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import fr.sii.ogham.testing.assertion.util.HtmlUtils;

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
	 * will generate a {@link ComparisonFailure} with the expected string and
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
		DetailedDiff diff = HtmlUtils.compare(expected, actual);
		if (!diff.identical()) {
			logDifferences(diff);
			throw new ComparisonFailure("HTML element different to expected one. See logs for details about found differences.\n", expected, actual);
		}
	}

	/**
	 * Check if the HTML is similar to the expected. The HTML strings are parsed
	 * into {@link Document}s. Two documents are considered to be "similar" if
	 * they contain the same elements and attributes regardless of order.
	 * <p>
	 * For each difference, the difference will be logged with error level. It
	 * will generate a {@link ComparisonFailure} with the expected string and
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
		DetailedDiff diff = HtmlUtils.compare(expected, actual);
		if (!diff.similar()) {
			logUnrecoverableDifferences(diff);
			throw new ComparisonFailure("HTML element different to expected one. See logs for details about found differences.\n", expected, actual);
		}
	}

	@SuppressWarnings("unchecked")
	private static void logDifferences(DetailedDiff diff) {
		for (Difference difference : (List<Difference>) diff.getAllDifferences()) {
			LOG.error(difference.toString()); // NOSONAR
		}
	}

	@SuppressWarnings("unchecked")
	private static void logUnrecoverableDifferences(DetailedDiff diff) {
		for (Difference difference : (List<Difference>) diff.getAllDifferences()) {
			if (!difference.isRecoverable()) {
				LOG.error(difference.toString()); // NOSONAR
			}
		}
	}

	private AssertHtml() {
		super();
	}
}
