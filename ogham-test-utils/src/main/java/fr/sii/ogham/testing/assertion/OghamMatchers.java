package fr.sii.ogham.testing.assertion;

import org.hamcrest.Matcher;
import org.w3c.dom.Document;

import fr.sii.ogham.testing.assertion.hamcrest.IdenticalHtmlMatcher;
import fr.sii.ogham.testing.assertion.hamcrest.SimilarHtmlMatcher;

/**
 * Contains additional matchers used in tests:
 * <ul>
 * <li>HTML matchers to check if HTML contents are either identical or
 * semantically similar</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public final class OghamMatchers {

	/**
	 * Check if the HTML is similar to the expected. The HTML strings are parsed
	 * into {@link Document}s. Two documents are considered to be "similar" if
	 * they contain the same elements and attributes regardless of order.
	 * 
	 * @param expectedHtml
	 *            the expected HTML
	 * @return the matcher that will check if HTML is identical to expected HTML
	 */
	public static Matcher<String> isSimilarHtml(String expectedHtml) {
		return new SimilarHtmlMatcher(expectedHtml);
	}

	/**
	 * Check if the HTML is identical to the expected. The HTML strings are
	 * parsed into {@link Document}s. Two documents are considered to be
	 * "identical" if they contain the same elements and attributes in the same
	 * order.
	 * 
	 * @param expectedHtml
	 *            the expected HTML
	 * @return the matcher that will check if HTML is identical to expected HTML
	 */
	public static Matcher<String> isIdenticalHtml(String expectedHtml) {
		return new IdenticalHtmlMatcher(expectedHtml);
	}

	private OghamMatchers() {
		super();
	}
}
