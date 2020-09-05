package fr.sii.ogham.testing.assertion.hamcrest;

import java.util.function.Consumer;

import org.custommonkey.xmlunit.DetailedDiff;
import org.junit.ComparisonFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.assertion.util.HtmlUtils;

/**
 * Check if the HTML is similar to the expected. The HTML strings are parsed
 * into {@link Document}s. Two documents are considered to be "similar" if they
 * contain the same elements and attributes regardless of order.
 * 
 * <p>
 * This matcher is a {@link ExpectedValueProvider} for knowing the original expected
 * value. Thanks to this information, {@link OghamAssertions} will generate a
 * {@link ComparisonFailure} with the expected string and actual string in order
 * to be able to visualize the differences on sources directly in the IDE.
 * 
 * <p>
 * See {@link HtmlUtils} for more information about "similar" HTML.
 * 
 * @author Aurélien Baudet
 *
 */
public class SimilarHtmlMatcher extends AbstractHtmlDiffMatcher {
	private static final Logger LOG = LoggerFactory.getLogger(SimilarHtmlMatcher.class);

	public SimilarHtmlMatcher(String expected) {
		this(expected, LOG::warn);
	}

	public SimilarHtmlMatcher(String expected, Consumer<String> printer) {
		super(expected, printer, "similar");
	}

	@Override
	protected boolean matches(DetailedDiff diff) {
		return diff.similar();
	}

	@Override
	public String toString() {
		return "isSimilarHtml('"+expected+"')";
	}
}
