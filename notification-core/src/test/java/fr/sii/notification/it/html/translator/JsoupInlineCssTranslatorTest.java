package fr.sii.notification.it.html.translator;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.notification.core.builder.LookupMappingResourceResolverBuilder;
import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.resource.resolver.LookupMappingResolver;
import fr.sii.notification.helper.html.AssertHtml;
import fr.sii.notification.helper.rule.LoggingTestRule;
import fr.sii.notification.html.inliner.impl.jsoup.JsoupCssInliner;
import fr.sii.notification.html.translator.InlineCssTranslator;

public class JsoupInlineCssTranslatorTest {
	private static String FOLDER = "/inliner/css/jsoup/";
	private static String SOURCE_FOLDER = FOLDER+"source/";
	private static String EXPECTED_FOLDER = FOLDER+"expected/";
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	private InlineCssTranslator translator;
	
	@Before
	public void setUp() {
		LookupMappingResolver resourceResolver = new LookupMappingResourceResolverBuilder().useDefaults().withPrefix(SOURCE_FOLDER).build();
		translator = new InlineCssTranslator(new JsoupCssInliner(), resourceResolver);
	}
	
	@Test
	public void externalStyles() throws IOException, ContentTranslatorException {
		String source = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"externalStyles.html"));
		String expected = IOUtils.toString(getClass().getResourceAsStream(EXPECTED_FOLDER+"externalStyles.html"));
		StringContent sourceContent = new StringContent(source);
		Content result = translator.translate(sourceContent);
		Assert.assertNotSame("Content should not be the same", sourceContent, result);
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
