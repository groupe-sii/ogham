package oghamcore.ut.html.inliner.impl;

import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.html.inliner.ExternalCss;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupCssInliner;
import fr.sii.ogham.testing.assertion.html.AssertHtml;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;

@LogTestInformation
public class JsoupCssInlinerTest {
	private static String FOLDER = "/inliner/css/jsoup/";
	private static String SOURCE_FOLDER = FOLDER+"source/";
	private static String EXPECTED_FOLDER = FOLDER+"expected/";
	
	private JsoupCssInliner inliner;

	@BeforeEach
	public void setUp() {
		inliner = new JsoupCssInliner();
	}
	
	@Test
	public void noStyles() throws IOException {
		String source = resourceAsString(SOURCE_FOLDER+"noStyles.html");
		String expected = resourceAsString(EXPECTED_FOLDER+"noStyles.html");
		AssertHtml.assertEquals(expected, inliner.inline(source, new ArrayList<ExternalCss>()));
	}
	
	@Test
	public void internalStyles() throws IOException {
		String source = resourceAsString(SOURCE_FOLDER+"internalStyles.html");
		String expected = resourceAsString(EXPECTED_FOLDER+"internalStyles.html");
		AssertHtml.assertEquals(expected, inliner.inline(source, new ArrayList<ExternalCss>()));
	}
	
	@Test
	public void mixedStyles() throws IOException {
		String source = resourceAsString(SOURCE_FOLDER+"mixedStyles.html");
		String css1 = resourceAsString(SOURCE_FOLDER+"css/external1.css");
		String expected = resourceAsString(EXPECTED_FOLDER+"mixedStyles.html");
		AssertHtml.assertEquals(expected, inliner.inline(source, Arrays.asList(new ExternalCss(new UnresolvedPath("css/external1.css"), css1))));
	}
	
	@Test
	public void externalStyles() throws IOException {
		String source = resourceAsString(SOURCE_FOLDER+"externalStyles.html");
		String css1 = resourceAsString(SOURCE_FOLDER+"css/external1.css");
		String css2 = resourceAsString(SOURCE_FOLDER+"css/external2.css");
		String expected = resourceAsString(EXPECTED_FOLDER+"externalStyles.html");
		AssertHtml.assertEquals(expected, inliner.inline(source, Arrays.asList(new ExternalCss(new UnresolvedPath("css/external1.css"), css1), new ExternalCss(new UnresolvedPath("css/external2.css"), css2))));
	}
	
	@Test
	public void skipSomeStyles() throws IOException {
		String source = resourceAsString(SOURCE_FOLDER+"skipSomeStyles.html");
		String css1 = resourceAsString(SOURCE_FOLDER+"css/external1.css");
		String css2 = resourceAsString(SOURCE_FOLDER+"css/external2.css");
		String expected = resourceAsString(EXPECTED_FOLDER+"skipSomeStyles.html");
		AssertHtml.assertEquals(expected, inliner.inline(source, Arrays.asList(new ExternalCss(new UnresolvedPath("css/external1.css"), css1), new ExternalCss(new UnresolvedPath("css/external2.css"), css2))));
	}
	
	@Test
	@Disabled("Not yet implemented")
	public void overrideStyles() throws IOException {
		String source = resourceAsString(SOURCE_FOLDER+"overrideStyles.html");
		String css1 = resourceAsString(SOURCE_FOLDER+"css/external1.css");
		String css2 = resourceAsString(SOURCE_FOLDER+"css/external2.css");
		String css3 = resourceAsString(SOURCE_FOLDER+"css/override1.css");
		String expected = resourceAsString(EXPECTED_FOLDER+"overrideStyles.html");
		AssertHtml.assertEquals(expected, inliner.inline(source, Arrays.asList(new ExternalCss(new UnresolvedPath("css/external1.css"), css1), new ExternalCss(new UnresolvedPath("css/external2.css"), css2), new ExternalCss(new UnresolvedPath("css/override1.css"), css3))));
	}
	
	@Test
	@Disabled("Not yet implemented")
	public void cssPriority() throws IOException {
		// TODO: test css with rules with higher priority before rules with lower priority
		Assertions.fail("Not implemented");
	}

	@Test
	public void ignoreAtRules() throws IOException {
		String source = resourceAsString(SOURCE_FOLDER+"at-rules.html");
		String css1 = resourceAsString(SOURCE_FOLDER+"css/at-rules.css");
		String expected = resourceAsString(EXPECTED_FOLDER+"at-rules.html");
		AssertHtml.assertEquals(expected, inliner.inline(source, Arrays.asList(new ExternalCss(new UnresolvedPath("css/at-rules.css"), css1))));
	}

	@Test
	public void updateRelativeUrls() throws IOException {
		String source = resourceAsString(SOURCE_FOLDER+"relative-urls.html");
		String css1 = resourceAsString(SOURCE_FOLDER+"css/relative-urls.css");
		String expected = resourceAsString(EXPECTED_FOLDER+"relative-urls.html");
		AssertHtml.assertEquals(expected, inliner.inline(source, Arrays.asList(new ExternalCss(new UnresolvedPath("css/relative-urls.css"), css1))));
	}
	
}
