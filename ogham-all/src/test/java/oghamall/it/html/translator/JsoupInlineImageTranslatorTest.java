package oghamall.it.html.translator;

import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.configurer.ConfigurationPhase;
import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.exception.handler.ImageInliningException;
import fr.sii.ogham.core.id.generator.IdGenerator;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.mimetype.TikaProvider;
import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.core.resource.path.LookupAwareRelativePathResolver;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.resource.resolver.ResourceResolver;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.attachment.ContentDisposition;
import fr.sii.ogham.email.message.content.ContentWithAttachments;
import fr.sii.ogham.html.inliner.EveryImageInliner;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.ImageResource;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupAttachImageInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupBase64ImageInliner;
import fr.sii.ogham.html.translator.InlineImageTranslator;
import fr.sii.ogham.template.thymeleaf.v3.buider.ThymeleafV3EmailBuilder;
import fr.sii.ogham.testing.assertion.html.AssertHtml;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

@RunWith(MockitoJUnitRunner.class)
public class JsoupInlineImageTranslatorTest {
	private static String FOLDER = "/inliner/images/jsoup/";
	private static String SOURCE_FOLDER = FOLDER + "source/";
	private static String EXPECTED_FOLDER = FOLDER + "expected/";

	ExpectedException thrown = ExpectedException.none();
	@Rule public final RuleChain ruleChain = RuleChain
			.outerRule(new LoggingTestRule())
			.around(thrown);

	private InlineImageTranslator translator;

	@Mock
	private IdGenerator generator;

	@Before
	public void setUp() {
		Mockito.when(generator.generate("fb.gif")).thenReturn("fb.gif");
		Mockito.when(generator.generate("h1.gif")).thenReturn("h1.gif");
		Mockito.when(generator.generate("left.gif")).thenReturn("left.gif");
		Mockito.when(generator.generate("right.gif")).thenReturn("right.gif");
		Mockito.when(generator.generate("tw.gif")).thenReturn("tw.gif");
		MessagingBuilder builder = MessagingBuilder.standard();
		builder.configure(ConfigurationPhase.BEFORE_BUILD);
		ResourceResolver resourceResolver = builder
				.email()
					.template(ThymeleafV3EmailBuilder.class)
						.classpath()
							.pathPrefix(SOURCE_FOLDER)
							.and()
						.buildResolver();
		MimeTypeProvider mimetypeProvider = new TikaProvider();
		ImageInliner inliner = new EveryImageInliner(new JsoupAttachImageInliner(generator), new JsoupBase64ImageInliner());
		Map<String, List<String>> lookups = new HashMap<>();
		lookups.put("string", asList("string:", "s:"));
		lookups.put("file", asList("file:"));
		lookups.put("classpath", asList("classpath:", ""));
		translator = new InlineImageTranslator(inliner, resourceResolver, mimetypeProvider, new LookupAwareRelativePathResolver(lookups));
	}

	@Test
	public void attachImages() throws IOException, ContentTranslatorException {
		// prepare the html and associated images
		String source = resourceAsString(SOURCE_FOLDER + "withImages.html");
		// do the job
		Content result = translator.translate(new StringContent(source));
		// prepare expected html
		String expected = getExpectedHtml("withImagesBoth.html");
		// prepare expected attachments
		List<Attachment> expectedAttachments = getAttachments(loadImages("fb.gif", "h1.gif", "left.gif", "right.gif", "tw.gif"));
		// assertions
		Assert.assertTrue("should be ContentWithAttachments", result instanceof ContentWithAttachments);
		ContentWithAttachments contentWithAttachments = (ContentWithAttachments) result;
		AssertHtml.assertEquals(expected, contentWithAttachments.getContent().toString());
		Assert.assertEquals("should have 5 attachments", 5, contentWithAttachments.getAttachments().size());
		Assert.assertEquals("should have valid attachments", new HashSet<>(expectedAttachments), new HashSet<>(contentWithAttachments.getAttachments()));
	}

	@Test
	public void skipExternalImages() throws IOException, ContentTranslatorException {
		// prepare the html and associated images
		String source = resourceAsString(SOURCE_FOLDER + "withExternalImages.html");
		// do the job
		Content result = translator.translate(new StringContent(source));
		// prepare expected html
		String expected = getExpectedHtml("withExternalImages.html");
		// prepare expected attachments
		List<Attachment> expectedAttachments = getAttachments(loadImages("h1.gif", "left.gif", "right.gif", "tw.gif"));
		// assertions
		Assert.assertTrue("should be ContentWithAttachments", result instanceof ContentWithAttachments);
		ContentWithAttachments contentWithAttachments = (ContentWithAttachments) result;
		AssertHtml.assertEquals(expected, contentWithAttachments.getContent().toString());
		Assert.assertEquals("should have 4 attachments", 4, contentWithAttachments.getAttachments().size());
		Assert.assertEquals("should have valid attachments", new HashSet<>(expectedAttachments), new HashSet<>(contentWithAttachments.getAttachments()));
	}

	@Test
	public void skipAttach() throws IOException, ContentTranslatorException {
		// prepare the html and associated images
		String source = resourceAsString(SOURCE_FOLDER + "skipInline.html");
		// do the job
		Content result = translator.translate(new StringContent(source));
		// prepare expected html
		String expected = getExpectedHtml("skipInlineBoth.html");
		// prepare expected attachments
		List<Attachment> expectedAttachments = getAttachments(loadImages("fb.gif", "h1.gif"));
		// assertions
		Assert.assertTrue("should be ContentWithAttachments", result instanceof ContentWithAttachments);
		ContentWithAttachments contentWithAttachments = (ContentWithAttachments) result;
		AssertHtml.assertEquals(expected, contentWithAttachments.getContent().toString());
		Assert.assertEquals("should have 2 attachments", 2, contentWithAttachments.getAttachments().size());
		Assert.assertEquals("should have valid attachments", new HashSet<>(expectedAttachments), new HashSet<>(contentWithAttachments.getAttachments()));
	}

	@Test
	public void unreadableImage() throws ContentTranslatorException {
		thrown.expect(ImageInliningException.class);
		thrown.expectMessage(containsString("INVALID_FILE"));
		StringContent sourceContent = new StringContent("<html><head></head><body><img src='INVALID_FILE' /></body></html>");
		translator.translate(sourceContent);
	}

	// ---------------------------------------------------------------//
	// Utilities //
	// ---------------------------------------------------------------//

	private static Attachment getAttachment(ImageResource image) {
		return new Attachment(new ByteResource(image.getName(), image.getContent()), null, ContentDisposition.INLINE, "<" + image.getName() + ">");
	}

	private static List<Attachment> getAttachments(List<ImageResource> images) {
		List<Attachment> attachments = new ArrayList<>(images.size());
		for (ImageResource image : images) {
			attachments.add(getAttachment(image));
		}
		return attachments;
	}

	private static String getExpectedHtml(String fileName) throws IOException {
		return resourceAsString(EXPECTED_FOLDER + fileName);
	}

	private static List<ImageResource> loadImages(String... imageNames) throws IOException {
		List<ImageResource> resources = new ArrayList<>(imageNames.length);
		for (String imageName : imageNames) {
			resources.add(new ImageResource(imageName, "images/" + imageName, new UnresolvedPath("images/" + imageName),
					IOUtils.toByteArray(JsoupInlineImageTranslatorTest.class.getResourceAsStream(SOURCE_FOLDER + "images/" + imageName)), "images/gif"));
		}
		return resources;
	}
}
