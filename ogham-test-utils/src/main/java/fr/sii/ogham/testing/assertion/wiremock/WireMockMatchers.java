package fr.sii.ogham.testing.assertion.wiremock;

import org.w3c.dom.Document;

import com.github.tomakehurst.wiremock.client.WireMock;

import fr.sii.ogham.testing.assertion.util.HtmlUtils;

/**
 * Additional matchers for {@link WireMock}.
 * 
 * @author Aurélien Baudet
 *
 */
public final class WireMockMatchers {

	/**
	 * Check if the HTML is similar to the expected. The HTML strings are parsed
	 * into {@link Document}s. Two documents are considered to be "similar" if
	 * they contain the same elements and attributes regardless of order.
	 * 
	 * <p>
	 * See {@link HtmlUtils} for more information about "similar" HTML.
	 * 
	 * <p>
	 * NOTE: {@link WireMock#equalToXml(String)} does an identical check.
	 * 
	 * @param expected
	 *            the expected HTML
	 * @return the WireMock matcher
	 */
	public static SimilarHtmlPattern similarHtml(String expected) {
		return new SimilarHtmlPattern(expected);
	}

	private WireMockMatchers() {
		super();
	}
}
