package fr.sii.ogham.testing.assertion.hamcrest;

import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.assertion.util.HtmlUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.opentest4j.AssertionFailedError;
import org.w3c.dom.Document;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import java.util.function.Consumer;

/**
 * Check if the HTML is identical or similar to the expected. The HTML strings are parsed
 * into {@link Document}s. Two documents are considered to be "identical" if
 * they contain the same elements and attributes in the same order.Two documents are considered to be "similar" if they
 * contain the same elements and attributes regardless of order.
 * 
 * <p>
 * This matcher is a {@link ExpectedValueProvider} for knowing the original expected
 * value. Thanks to this information, {@link OghamAssertions} will generate a
 * {@link AssertionFailedError} with the expected string and actual string in order
 * to be able to visualize the differences on sources directly in the IDE.
 * 
 * <p>
 * See {@link HtmlUtils} for more information about "identical"/"similar" HTML.
 * 
 * @author Aurélien Baudet
 *
 */
public abstract class AbstractHtmlDiffMatcher extends BaseMatcher<String> implements ExpectedValueProvider<String>, ComparisonAwareMatcher {
	protected final String expected;
	protected final Consumer<String> printer;
	protected final String name;
	protected final boolean identical;
	protected Diff diff;

	public AbstractHtmlDiffMatcher(String expected, Consumer<String> printer, String name, boolean identical) {
		super();
		this.expected = expected;
		this.printer = printer;
		this.name = name;
		this.identical = identical;
	}

	@Override
	public boolean matches(Object item) {
		diff = HtmlUtils.compare(expected, (String) item, identical);
		boolean matches = matches(diff);
		if(!matches) {
			printer.accept(comparisonMessage());
		}
		return matches;
	}

	protected abstract boolean matches(Diff diff);

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
		sb.append("The two HTML documents are not ").append(name).append(".\n");
		sb.append("Here are the differences found:\n");
		for(Difference d : diff.getDifferences()) {
			sb.append("  - ").append(d.toString()).append("\n");
		}
		sb.append("\n");
		return sb.toString();
	}
}
