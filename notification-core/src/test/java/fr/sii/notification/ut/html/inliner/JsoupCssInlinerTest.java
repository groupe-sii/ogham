package fr.sii.notification.ut.html.inliner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.notification.helper.html.AssertHtml;
import fr.sii.notification.helper.rule.LoggingTestRule;
import fr.sii.notification.html.inliner.ExternalCss;
import fr.sii.notification.html.inliner.JsoupCssInliner;

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
		String source = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"noStyles.html"));
		String expected = IOUtils.toString(getClass().getResourceAsStream(EXPECTED_FOLDER+"noStyles.html"));
		AssertHtml.assertSimilar(expected, inliner.inline(source, new ArrayList<ExternalCss>()));
	}
	
	@Test
	public void internalStyles() throws IOException {
		String source = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"internalStyles.html"));
		String expected = IOUtils.toString(getClass().getResourceAsStream(EXPECTED_FOLDER+"internalStyles.html"));
		AssertHtml.assertSimilar(expected, inliner.inline(source, new ArrayList<ExternalCss>()));
	}
	
	@Test
	public void mixedStyles() throws IOException {
		String source = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"mixedStyles.html"));
		String css1 = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"css/external1.css"));
		String expected = IOUtils.toString(getClass().getResourceAsStream(EXPECTED_FOLDER+"mixedStyles.html"));
		AssertHtml.assertSimilar(expected, inliner.inline(source, Arrays.asList(new ExternalCss("css/external1.css", css1))));
	}
	
	@Test
	public void externalStyles() throws IOException {
		String source = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"externalStyles.html"));
		String css1 = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"css/external1.css"));
		String css2 = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"css/external2.css"));
		String expected = IOUtils.toString(getClass().getResourceAsStream(EXPECTED_FOLDER+"externalStyles.html"));
		AssertHtml.assertSimilar(expected, inliner.inline(source, Arrays.asList(new ExternalCss("css/external1.css", css1), new ExternalCss("css/external2.css", css2))));
	}
	
	@Test
	public void overrideStyles() throws IOException {
		String source = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"overrideStyles.html"));
		String css1 = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"css/external1.css"));
		String css2 = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"css/external2.css"));
		String css3 = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"css/override1.css"));
		String expected = IOUtils.toString(getClass().getResourceAsStream(EXPECTED_FOLDER+"overrideStyles.html"));
		AssertHtml.assertSimilar(expected, inliner.inline(source, Arrays.asList(new ExternalCss("css/external1.css", css1), new ExternalCss("css/external2.css", css2), new ExternalCss("css/override1.css", css3))));
	}
	
	@Test
	public void cssPriority() throws IOException {
		// TODO: test css with rules with higher priority before rules with lower priority
		Assert.fail("Not implemented");
	}
}
