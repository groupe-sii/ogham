package oghamall.it.html.translator;

import static fr.sii.ogham.testing.assertion.OghamAssertions.resourceAsString;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurationPhase;
import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.handler.CssInliningException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.resource.path.LookupAwareRelativePathResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupCssInliner;
import fr.sii.ogham.html.translator.InlineCssTranslator;
import fr.sii.ogham.template.thymeleaf.v3.buider.ThymeleafV3EmailBuilder;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.helper.html.AssertHtml;

public class JsoupInlineCssTranslatorTest {
	private static String FOLDER = "/inliner/css/jsoup/";
	private static String SOURCE_FOLDER = FOLDER + "source/";
	private static String EXPECTED_FOLDER = FOLDER + "expected/";

	ExpectedException thrown = ExpectedException.none();
	@Rule public final RuleChain ruleChain = RuleChain
			.outerRule(new LoggingTestRule())
			.around(thrown);

	private InlineCssTranslator translator;

	@Before
	public void setUp() {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder.configure(ConfigurationPhase.BEFORE_BUILD);
		ResourceResolver resourceResolver = builder
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

	@Test
	public void unreadableCss() throws ContentTranslatorException {
		thrown.expect(CssInliningException.class);
		thrown.expectMessage(containsString("INVALID_FILE"));
		StringContent sourceContent = new StringContent("<html><head><link href=\"INVALID_FILE\" rel=\"stylesheet\" /></head><body></body></html>");
		translator.translate(sourceContent);
	}
}
