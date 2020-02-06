package fr.sii.ogham.testing.assertion.hamcrest;

import java.util.function.Consumer;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Difference;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
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
public class SimilarHtmlMatcher extends BaseMatcher<String> implements ExpectedValueProvider<String>, ComparisonAwareMatcher {
	private static final Logger LOG = LoggerFactory.getLogger(SimilarHtmlMatcher.class);
	
	private final String expected;
	private final Consumer<String> printer;
	private DetailedDiff diff;

	public SimilarHtmlMatcher(String expected) {
		this(expected, LOG::warn);
	}

	public SimilarHtmlMatcher(String expected, Consumer<String> printer) {
		super();
		this.expected = expected;
		this.printer = printer;
	}

	@Override
	public boolean matches(Object item) {
		diff = HtmlUtils.compare(expected, (String) item);
		boolean similar = diff.similar();
		if(!similar) {
			printer.accept(comparisonMessage());
		}
		return similar;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(expected);
	}

	@Override
	public String getExpectedValue() {
		return expected;
	}

	@Override
	public String comparisonMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("The two HTML documents are not similar.\n");
		sb.append("Here are the differences found:\n");
		for(Object o : diff.getAllDifferences()) {
			Difference d = (Difference) o;
			sb.append("  - ").append(d.toString()).append("\n");
		}
		sb.append("\n");
		return sb.toString();
	}

	@Override
	public String toString() {
		return "isSimilarHtml('"+expected+"')";
	}
}
