package fr.sii.ogham.testing.assertion.wiremock;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import fr.sii.ogham.testing.assertion.exception.ComparisonException;
import fr.sii.ogham.testing.assertion.util.HtmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xmlunit.diff.Diff;

import java.util.concurrent.atomic.AtomicInteger;

import static com.github.tomakehurst.wiremock.matching.MatchResult.*;

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
	private static final Logger LOG = LoggerFactory.getLogger(SimilarHtmlPattern.class);

	/**
	 * Initialized with the expected HTML
	 *
	 * @param expectedValue
	 *            the expected HTML
	 */
	public SimilarHtmlPattern(@wiremock.com.fasterxml.jackson.annotation.JsonProperty("similarHtml") @JsonProperty("similarHtml") String expectedValue) {
		super(expectedValue);
	}

	@Override
	public MatchResult match(String value) {
		try {
			Diff diff = HtmlUtils.compare(expectedValue, value, false);
			return diff.hasDifferences() ? partialMatch(computeDistance(diff)) : exactMatch();
		} catch(ComparisonException e) {
			// ignore because WireMock will try each value (body, multipart files, ...) so comparison may fail
			// due to not xml value
			LOG.debug("Can't match since ", e);
			return noMatch();
		}
	}

	private double computeDistance(Diff diff) {
		final AtomicInteger count = new AtomicInteger();
		diff.getDifferences().forEach((d) -> count.incrementAndGet());
		return count.doubleValue();
	}
}
