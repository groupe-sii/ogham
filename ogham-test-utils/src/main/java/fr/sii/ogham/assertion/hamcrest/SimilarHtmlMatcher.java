package fr.sii.ogham.assertion.hamcrest;

import org.custommonkey.xmlunit.DetailedDiff;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.ComparisonFailure;
import org.w3c.dom.Document;

import fr.sii.ogham.assertion.OghamAssertions;
import fr.sii.ogham.helper.html.HtmlUtils;

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
public class SimilarHtmlMatcher extends BaseMatcher<String> implements ExpectedValueProvider<String> {
	private final String expected;

	public SimilarHtmlMatcher(String expected) {
		super();
		this.expected = expected;
	}

	@Override
	public boolean matches(Object item) {
		DetailedDiff diff = HtmlUtils.compare(expected, (String) item);
		return diff.similar();
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(expected);
	}

	@Override
	public String getExpectedValue() {
		return expected;
	}

}
