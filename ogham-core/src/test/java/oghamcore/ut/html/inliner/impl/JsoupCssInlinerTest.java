package oghamcore.ut.html.inliner.impl;

import static fr.sii.ogham.testing.assertion.OghamAssertions.resourceAsString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.html.inliner.ExternalCss;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupCssInliner;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.helper.html.AssertHtml;

public class JsoupCssInlinerTest {
	private static String FOLDER = "/inliner/css/jsoup/";
	private static String SOURCE_FOLDER = FOLDER+"source/";
	private static String EXPECTED_FOLDER = FOLDER+"expected/";
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	private JsoupCssInliner inliner;

	@Before
	public void setUp() {
		inliner = new JsoupCssInliner();
	}
	
	@Test
	public void noStyles() throws IOException {
		String source = resourceAsString(SOURCE_FOLDER+"noStyles.html");
		String expected = resourceAsString(EXPECTED_FOLDER+"noStyles.html");
		AssertHtml.assertSimilar(expected, inliner.inline(source, new ArrayList<ExternalCss>()));
	}
	
	@Test
	public void internalStyles() throws IOException {
		String source = resourceAsString(SOURCE_FOLDER+"internalStyles.html");
		String expected = resourceAsString(EXPECTED_FOLDER+"internalStyles.html");
		AssertHtml.assertSimilar(expected, inliner.inline(source, new ArrayList<ExternalCss>()));
	}
	
	@Test
	public void mixedStyles() throws IOException {
		String source = resourceAsString(SOURCE_FOLDER+"mixedStyles.html");
		String css1 = resourceAsString(SOURCE_FOLDER+"css/external1.css");
		String expected = resourceAsString(EXPECTED_FOLDER+"mixedStyles.html");
		AssertHtml.assertSimilar(expected, inliner.inline(source, Arrays.asList(new ExternalCss(new UnresolvedPath("css/external1.css"), css1))));
	}
	
	@Test
	public void externalStyles() throws IOException {
		String source = resourceAsString(SOURCE_FOLDER+"externalStyles.html");
		String css1 = resourceAsString(SOURCE_FOLDER+"css/external1.css");
		String css2 = resourceAsString(SOURCE_FOLDER+"css/external2.css");
		String expected = resourceAsString(EXPECTED_FOLDER+"externalStyles.html");
		AssertHtml.assertSimilar(expected, inliner.inline(source, Arrays.asList(new ExternalCss(new UnresolvedPath("css/external1.css"), css1), new ExternalCss(new UnresolvedPath("css/external2.css"), css2))));
	}
	
	@Test
	@Ignore("Not yet implemented")
	public void overrideStyles() throws IOException {
		String source = resourceAsString(SOURCE_FOLDER+"overrideStyles.html");
		String css1 = resourceAsString(SOURCE_FOLDER+"css/external1.css");
		String css2 = resourceAsString(SOURCE_FOLDER+"css/external2.css");
		String css3 = resourceAsString(SOURCE_FOLDER+"css/override1.css");
		String expected = resourceAsString(EXPECTED_FOLDER+"overrideStyles.html");
		AssertHtml.assertSimilar(expected, inliner.inline(source, Arrays.asList(new ExternalCss(new UnresolvedPath("css/external1.css"), css1), new ExternalCss(new UnresolvedPath("css/external2.css"), css2), new ExternalCss(new UnresolvedPath("css/override1.css"), css3))));
	}
	
	@Test
	@Ignore("Not yet implemented")
	public void cssPriority() throws IOException {
		// TODO: test css with rules with higher priority before rules with lower priority
		Assert.fail("Not implemented");
	}
}
