package fr.sii.ogham.testing.assertion.util;


import fr.sii.ogham.testing.assertion.exception.ComparisonException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;
import wiremock.org.custommonkey.xmlunit.HTMLDocumentBuilder;
import wiremock.org.custommonkey.xmlunit.TolerantSaxDocumentBuilder;
import wiremock.org.custommonkey.xmlunit.XMLUnit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import java.io.IOException;

public final class HtmlUtils {
	/**
	 * Compare two HTML documents. The HTML strings are parsed into
	 * {@link Document}s. The HTML are compared by elements and attributes, not
	 * using directly using string.
	 * 
	 * <p>
	 * Documents can be compared using different modes (either "identical" or "similar"):
	 * <ul>
	 * <li>Two documents are considered to be "identical" if they contain the
	 * same elements and attributes in the same order.</li>
	 * <li>Two documents are considered to be "similar" if they contain the same
	 * elements and attributes regardless of order.</li>
	 * </ul>
	 * 
	 * 
	 * @param expected
	 *            the expected HTML
	 * @param actual
	 *            the HTML content to check
	 * @param identical
	 * 			  true to do an identical comparison, false for a similar comparison
	 * @return a report that let you know differences between the two HTML
	 *         strings
	 */
	public static Diff compare(String expected, String actual, boolean identical) {
		try {
			DiffBuilder builder = getComparatorBuilder(expected, actual);
			if (identical) {
				builder.checkForIdentical();
			} else {
				builder.checkForSimilar()
						.ignoreComments()
						.normalizeWhitespace()
						.withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName));
			}
			return builder.build();
		} catch (ComparisonException e) {
			throw e;
		} catch (RuntimeException e) {
			throw new ComparisonException("Failed to compare HTML", e);
		}
	}

	public static DiffBuilder getComparatorBuilder(String expected, String actual) {
		if (expected == null) {
			throw new IllegalArgumentException("expected html can't be null");
		}
		try {
			HTMLDocumentBuilder documentBuilder = new HTMLDocumentBuilder(new TolerantSaxDocumentBuilder(XMLUnit.newTestParser()));
			Source expectedDoc = Input.fromDocument(documentBuilder.parse(expected)).build();
			Source actualDoc = Input.fromDocument(documentBuilder.parse(actual == null ? "" : actual)).build();
			return DiffBuilder.compare(expectedDoc)
					.withTest(actualDoc);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new ComparisonException("Failed to compare HTML", e);
		}
	}

	private HtmlUtils() {
		super();
	}
}
