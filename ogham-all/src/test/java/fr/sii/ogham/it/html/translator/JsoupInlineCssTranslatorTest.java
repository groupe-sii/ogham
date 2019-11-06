package fr.sii.ogham.it.html.translator;

import static fr.sii.ogham.assertion.OghamAssertions.resourceAsString;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.resource.path.LookupAwareRelativePathResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.helper.html.AssertHtml;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupCssInliner;
import fr.sii.ogham.html.translator.InlineCssTranslator;
import fr.sii.ogham.junit.LoggingTestRule;
import fr.sii.ogham.template.thymeleaf.v3.buider.ThymeleafV3EmailBuilder;

public class JsoupInlineCssTranslatorTest {
	private static String FOLDER = "/inliner/css/jsoup/";
	private static String SOURCE_FOLDER = FOLDER + "source/";
	private static String EXPECTED_FOLDER = FOLDER + "expected/";

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	private InlineCssTranslator translator;

	@Before
	public void setUp() {
		ResourceResolver resourceResolver = MessagingBuilder.standard()
				.email()
					.template(ThymeleafV3EmailBuilder.class)
						.classpath()
							.pathPrefix(SOURCE_FOLDER)
							.and()
						.buildResolver();
		Map<String, List<String>> lookups = new HashMap<>();
		lookups.put("string", asList("string:", "s:"));
		lookups.put("file", asList("file:"));
		lookups.put("classpath", asList("classpath:", ""));
		translator = new InlineCssTranslator(new JsoupCssInliner(), resourceResolver, new LookupAwareRelativePathResolver(lookups));
	}

	@Test
	public void externalStyles() throws IOException, ContentTranslatorException {
		String source = resourceAsString(SOURCE_FOLDER + "externalStyles.html");
		String expected = resourceAsString(EXPECTED_FOLDER + "externalStyles.html");
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
		Assert.assertEquals("Content should not be updated", sourceContent.asString(), ((StringContent) result).asString());
	}
}
