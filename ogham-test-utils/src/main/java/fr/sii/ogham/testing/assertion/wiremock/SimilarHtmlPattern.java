package fr.sii.ogham.testing.assertion.wiremock;

import org.custommonkey.xmlunit.DetailedDiff;
import org.w3c.dom.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;

import fr.sii.ogham.testing.assertion.util.HtmlUtils;

/**
 * Check if the HTML is similar to the expected. The HTML strings are parsed
 * into {@link Document}s. Two documents are considered to be "similar" if they
 * contain the same elements and attributes regardless of order.
 * 
 * <p>
 * See {@link HtmlUtils} for more information about "similar" HTML.
 * 
 * <p>
 * NOTE: {@link WireMock#equalToXml(String)} does an identical check.
 * 
 * @author Aurélien Baudet
 *
 */
public class SimilarHtmlPattern extends StringValuePattern {
	/**
	 * Initialized with the expected HTML
	 * 
	 * @param expectedValue
	 *            the expected HTML
	 */
	public SimilarHtmlPattern(@JsonProperty("similarHtml") String expectedValue) {
		super(expectedValue);
	}

	@Override
	public MatchResult match(String value) {
		DetailedDiff diff = HtmlUtils.compare(expectedValue, value);
		return new MatchResult() {

			@Override
			public boolean isExactMatch() {
				return diff.similar();
			}

			@Override
			public double getDistance() {
				return diff.getAllDifferences().size();
			}
		};
	}
}
