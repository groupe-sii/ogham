package fr.sii.ogham.helper.html;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.HTMLDocumentBuilder;
import org.custommonkey.xmlunit.TolerantSaxDocumentBuilder;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.junit.ComparisonFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.sii.ogham.helper.exception.ComparisonException;

/**
 * Utility class for checking HTML content.
 * 
 * @author Aurélien Baudet
 *
 */
public class AssertHtml {
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
	public static void assertIdentical(String expected, String actual) {
		try {
			HTMLDocumentBuilder builder = new HTMLDocumentBuilder(new TolerantSaxDocumentBuilder(XMLUnit.newTestParser()));
			Document expectedDoc = builder.parse(expected);
			Document actualDoc = builder.parse(actual);
			DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(expectedDoc, actualDoc));
			if (!diff.identical()) {
				logDifferences(diff);
				throw new ComparisonFailure("HTML element different to expected one. See logs for details about found differences.\n", expected, actual);
			}
		} catch (SAXException | IOException | ConfigurationException | ParserConfigurationException e) {
			throw new ComparisonException("Failed to compare HTML", e);
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
		try {
			HTMLDocumentBuilder builder = new HTMLDocumentBuilder(new TolerantSaxDocumentBuilder(XMLUnit.newTestParser()));
			Document expectedDoc = builder.parse(expected);
			Document actualDoc = builder.parse(actual);
			DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(expectedDoc, actualDoc));
			if (!diff.similar()) {
				logUnrecoverableDifferences(diff);
				throw new ComparisonFailure("HTML element different to expected one. See logs for details about found differences.\n", expected, actual);
			}
		} catch (SAXException | IOException | ConfigurationException | ParserConfigurationException e) {
			throw new ComparisonException("Failed to compare HTML", e);
		}
	}

	
	@SuppressWarnings("unchecked")
	private static void logDifferences(DetailedDiff diff) {
		for (Difference difference : (List<Difference>) diff.getAllDifferences()) {
			LOG.error(difference.toString());	// NOSONAR
		}
	}

	@SuppressWarnings("unchecked")
	private static void logUnrecoverableDifferences(DetailedDiff diff) {
		for (Difference difference : (List<Difference>) diff.getAllDifferences()) {
			if (!difference.isRecoverable()) {
				LOG.error(difference.toString());	// NOSONAR
			}
		}
	}

	private AssertHtml() {
		super();
	}
}
