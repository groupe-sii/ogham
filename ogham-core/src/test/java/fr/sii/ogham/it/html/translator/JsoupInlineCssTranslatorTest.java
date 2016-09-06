package fr.sii.ogham.it.html.translator;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.builder.FirstSupportingResolverBuilder;
import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.resource.resolver.FirstSupportingResourceResolver;
import fr.sii.ogham.helper.html.AssertHtml;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupCssInliner;
import fr.sii.ogham.html.translator.InlineCssTranslator;

public class JsoupInlineCssTranslatorTest {
	private static String FOLDER = "/inliner/css/jsoup/";
	private static String SOURCE_FOLDER = FOLDER + "source/";
	private static String EXPECTED_FOLDER = FOLDER + "expected/";

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	private InlineCssTranslator translator;

	@Before
	public void setUp() {
		FirstSupportingResourceResolver resourceResolver = new FirstSupportingResolverBuilder().useDefaults().withParentPath(SOURCE_FOLDER).build();
		translator = new InlineCssTranslator(new JsoupCssInliner(), resourceResolver);
	}

	@Test
	public void externalStyles() throws IOException, ContentTranslatorException {
		String source = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER + "externalStyles.html"));
		String expected = IOUtils.toString(getClass().getResourceAsStream(EXPECTED_FOLDER + "externalStyles.html"));
		StringContent sourceContent = new StringContent(source);
		Content result = translator.translate(sourceContent);
		// StringContent is now updatable => now it remains the same instance
		Assert.assertSame("Content should be the same (updated)", sourceContent, result);
		AssertHtml.assertSimilar(expected, result.toString());
	}

	@Test
	public void notHtml() throws ContentTranslatorException {
		StringContent sourceContent = new StringContent("<link href=\"file.css\" rel=\"stylesheet\" />");
		Content result = translator.translate(sourceContent);
		Assert.assertSame("Content should be the same", sourceContent, result);
		Assert.assertEquals("Content should not be updated", sourceContent.getContent(), ((StringContent) result).getContent());
	}
}
