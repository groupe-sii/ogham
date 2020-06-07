package oghamcore.it.html.translator;

import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.resource.path.LookupAwareRelativePathResolver;
import fr.sii.ogham.core.resource.path.RelativePathResolver;
import fr.sii.ogham.core.resource.resolver.ClassPathResolver;
import fr.sii.ogham.core.resource.resolver.RelativeResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.html.inliner.CssInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupCssInliner;
import fr.sii.ogham.html.translator.InlineCssTranslator;
import fr.sii.ogham.testing.assertion.html.AssertHtml;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class InlineCssTranslatorTest {
	private static String FOLDER = "/inliner/css/translator/";
	private static String SOURCE_FOLDER = FOLDER+"source/";
	private static String EXPECTED_FOLDER = FOLDER+"expected/";

	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();

	InlineCssTranslator translator;
	
	@Before
	public void setup() {
		CssInliner inliner = new JsoupCssInliner();
		Map<String, List<String>> lookups = new HashMap<>();
		lookups.put("classpath", asList("classpath:", ""));
		RelativePathResolver relativePathResolver = new LookupAwareRelativePathResolver(lookups);
		ResourceResolver resourceResolver = new RelativeResolver(new ClassPathResolver("classpath", ""), SOURCE_FOLDER);
		translator = new InlineCssTranslator(inliner, resourceResolver, relativePathResolver);
	}
	
	@Test
	public void absoluteUrlsShouldBeAutomaticallySkipped() throws ContentTranslatorException, IOException, ResourceResolutionException {
		// GIVEN
		String source = resourceAsString(SOURCE_FOLDER+"absolute-urls.html");
		String expected = resourceAsString(EXPECTED_FOLDER+"absolute-urls.html");
		
		// WHEN
		Content translated = translator.translate(new StringContent(source));
		
		// THEN
		AssertHtml.assertEquals(expected, translated.toString());
	}
	
	@Test
	public void oghamAttributesShoulBeRemoved() throws ContentTranslatorException, IOException, ResourceResolutionException {
		// GIVEN
		String source = resourceAsString(SOURCE_FOLDER+"attributes.html");
		String expected = resourceAsString(EXPECTED_FOLDER+"attributes.html");
		
		// WHEN
		Content translated = translator.translate(new StringContent(source));
		
		// THEN
		AssertHtml.assertEquals(expected, translated.toString());
	}
}
