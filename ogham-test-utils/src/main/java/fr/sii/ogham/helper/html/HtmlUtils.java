package fr.sii.ogham.helper.html;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.HTMLDocumentBuilder;
import org.custommonkey.xmlunit.TolerantSaxDocumentBuilder;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import fr.sii.ogham.helper.exception.ComparisonException;

public final class HtmlUtils {
	/**
	 * Compare two HTML documents. The HTML strings are parsed into
	 * {@link Document}s. The HTML are compared by elements and attributes, not
	 * using directly using string.
	 * 
	 * <p>
	 * A {@link DetailedDiff} is provided to know if the documents are
	 * "identical" or "similar":
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
	 * @return a report that let you know differences between the two HTML
	 *         strings
	 */
	public static DetailedDiff compare(String expected, String actual) {
		try {
			HTMLDocumentBuilder builder = new HTMLDocumentBuilder(new TolerantSaxDocumentBuilder(XMLUnit.newTestParser()));
			Document expectedDoc = expected==null ? null : builder.parse(expected);
			Document actualDoc = actual==null ? null : builder.parse(actual);
			return new DetailedDiff(XMLUnit.compareXML(expectedDoc, actualDoc));
		} catch (SAXException | IOException | ConfigurationException | ParserConfigurationException e) {
			throw new ComparisonException("Failed to compare HTML", e);
		}
	}
	
	private HtmlUtils() {
		super();
	}
}
