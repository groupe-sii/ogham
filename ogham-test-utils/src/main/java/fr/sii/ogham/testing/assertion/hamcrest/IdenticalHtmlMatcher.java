package fr.sii.ogham.testing.assertion.hamcrest;

import java.util.function.Consumer;

import org.junit.ComparisonFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.assertion.util.HtmlUtils;
import org.xmlunit.diff.Diff;

/**
 * Check if the HTML is identical to the expected. The HTML strings are parsed
 * into {@link Document}s. Two documents are considered to be "identical" if
 * they contain the same elements and attributes in the same order.
 * 
 * <p>
 * This matcher is a {@link ExpectedValueProvider} for knowing the original expected
 * value. Thanks to this information, {@link OghamAssertions} will generate a
 * {@link ComparisonFailure} with the expected string and actual string in order
 * to be able to visualize the differences on sources directly in the IDE.
 * 
 * <p>
 * See {@link HtmlUtils} for more information about "identical" HTML.
 * 
 * @author Aurélien Baudet
 *
 */
public class IdenticalHtmlMatcher extends AbstractHtmlDiffMatcher {
	private static final Logger LOG = LoggerFactory.getLogger(IdenticalHtmlMatcher.class);
	
	public IdenticalHtmlMatcher(String expected) {
		this(expected, LOG::warn);
	}

	public IdenticalHtmlMatcher(String expected, Consumer<String> printer) {
		super(expected, printer, "identical", true);
	}

	@Override
	protected boolean matches(Diff diff) {
		return !diff.hasDifferences();
	}

	@Override
	public String toString() {
		return "isIdenticalHtml('"+expected+"')";
	}
}
