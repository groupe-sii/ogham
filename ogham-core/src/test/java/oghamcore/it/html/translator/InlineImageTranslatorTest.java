package oghamcore.it.html.translator;

import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.exception.resource.ResourceResolutionException;
import fr.sii.ogham.core.id.generator.SequentialIdGenerator;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.resource.path.LookupAwareRelativePathResolver;
import fr.sii.ogham.core.resource.path.RelativePathResolver;
import fr.sii.ogham.core.resource.resolver.ClassPathResolver;
import fr.sii.ogham.core.resource.resolver.RelativeResolver;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.email.message.content.ContentWithAttachments;
import fr.sii.ogham.html.inliner.EveryImageInliner;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupAttachImageInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupBase64ImageInliner;
import fr.sii.ogham.html.inliner.impl.regexp.RegexAttachBackgroudImageInliner;
import fr.sii.ogham.html.inliner.impl.regexp.RegexBase64BackgroundImageInliner;
import fr.sii.ogham.html.translator.InlineImageTranslator;
import fr.sii.ogham.testing.assertion.html.AssertHtml;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class InlineImageTranslatorTest {
	private static String FOLDER = "/inliner/images/translator/";
	private static String SOURCE_FOLDER = FOLDER+"source/";
	private static String EXPECTED_FOLDER = FOLDER+"expected/";

	@Rule public final MockitoRule mockito = MockitoJUnit.rule();
	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Mock MimeTypeProvider mimeTypeProvider;
	
	InlineImageTranslator translator;
	
	@Before
	public void setup() throws MimeTypeDetectionException, MimeTypeParseException {
		when(mimeTypeProvider.detect(any(InputStream.class))).thenReturn(new MimeType("image/gif"));
		SequentialIdGenerator idGenerator = new SequentialIdGenerator();
		ImageInliner inliner = new EveryImageInliner(
				new JsoupAttachImageInliner(idGenerator),
				new JsoupBase64ImageInliner(), 
				new RegexAttachBackgroudImageInliner(idGenerator),
				new RegexBase64BackgroundImageInliner());
		Map<String, List<String>> lookups = new HashMap<>();
		lookups.put("classpath", asList("classpath:", ""));
		RelativePathResolver relativePathResolver = new LookupAwareRelativePathResolver(lookups);
		ResourceResolver resourceResolver = new RelativeResolver(new ClassPathResolver("classpath:", ""), SOURCE_FOLDER);
		translator = new InlineImageTranslator(inliner, resourceResolver, mimeTypeProvider, relativePathResolver);
	}
	
	@Test
	public void absoluteUrlsShouldBeAutomaticallySkipped() throws ContentTranslatorException, IOException, ResourceResolutionException {
		// GIVEN
		String source = resourceAsString(SOURCE_FOLDER+"absolute-urls.html");
		String expected = resourceAsString(EXPECTED_FOLDER+"absolute-urls.html");
		
		// WHEN
		Content translated = translator.translate(new StringContent(source));
		
		// THEN
		AssertHtml.assertEquals(expected, ((ContentWithAttachments) translated).asString());
	}
	
	@Test
	public void oghamAttributesShoulBeRemoved() throws ContentTranslatorException, IOException, ResourceResolutionException {
		// GIVEN
		String source = resourceAsString(SOURCE_FOLDER+"attributes.html");
		String expected = resourceAsString(EXPECTED_FOLDER+"attributes.html");
		
		// WHEN
		Content translated = translator.translate(new StringContent(source));
		
		// THEN
		AssertHtml.assertEquals(expected, ((ContentWithAttachments) translated).asString());
	}
	
	@Test
	public void backgroudImagesShouldBeInlined() throws ContentTranslatorException, IOException, ResourceResolutionException {
		// GIVEN
		String source = resourceAsString(SOURCE_FOLDER+"background-images.html");
		String expected = resourceAsString(EXPECTED_FOLDER+"background-images.html");
		
		// WHEN
		Content translated = translator.translate(new StringContent(source));
		
		// THEN
		AssertHtml.assertEquals(expected, ((ContentWithAttachments) translated).asString());
	}
}
