package fr.sii.notification.helper.html;

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

public class AssertHtml {
	private static final Logger LOG = LoggerFactory.getLogger(AssertHtml.class);
	
	public static void assertIdentical(String expected, String actual) {
		try {
			HTMLDocumentBuilder builder = new HTMLDocumentBuilder(new TolerantSaxDocumentBuilder(XMLUnit.newTestParser()));
			Document expectedDoc = builder.parse(expected);
			Document actualDoc = builder.parse(actual);
			DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(expectedDoc, actualDoc));
			if(!diff.identical()) {
				for(Difference difference : (List<Difference>) diff.getAllDifferences()) {
					LOG.error(difference.toString());
				}
				throw new ComparisonFailure("HTML element different to expected one. See logs for details about found differences.\n", expected, actual);
			}
		} catch (SAXException | IOException | ConfigurationException | ParserConfigurationException e) {
			throw new RuntimeException("Failed to compare HTML", e);
		}
	}

	public static void assertSimilar(String expected, String actual) {
		try {
			HTMLDocumentBuilder builder = new HTMLDocumentBuilder(new TolerantSaxDocumentBuilder(XMLUnit.newTestParser()));
			Document expectedDoc = builder.parse(expected);
			Document actualDoc = builder.parse(actual);
			DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(expectedDoc, actualDoc));
			if(!diff.similar()) {
				for(Difference difference : (List<Difference>) diff.getAllDifferences()) {
					if(!difference.isRecoverable()) {
						LOG.error(difference.toString());
					}
				}
				throw new ComparisonFailure("HTML element different to expected one. See logs for details about found differences.\n", expected, actual);
			}
		} catch (SAXException | IOException | ConfigurationException | ParserConfigurationException e) {
			throw new RuntimeException("Failed to compare HTML", e);
		}
	}

	private AssertHtml() {
		super();
	}
}
