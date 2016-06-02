package fr.sii.ogham.it.html.translator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import fr.sii.ogham.core.builder.LookupMappingResourceResolverBuilder;
import fr.sii.ogham.core.exception.handler.ContentTranslatorException;
import fr.sii.ogham.core.id.generator.IdGenerator;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.mimetype.TikaProvider;
import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.core.resource.resolver.LookupMappingResolver;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.attachment.ContentDisposition;
import fr.sii.ogham.email.message.content.ContentWithAttachments;
import fr.sii.ogham.helper.html.AssertHtml;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.html.inliner.EveryImageInliner;
import fr.sii.ogham.html.inliner.ImageInliner;
import fr.sii.ogham.html.inliner.ImageResource;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupAttachImageInliner;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupBase64ImageInliner;
import fr.sii.ogham.html.translator.InlineImageTranslator;
import fr.sii.ogham.ut.html.inliner.impl.JsoupAttachImageInlinerTest;

@RunWith(MockitoJUnitRunner.class)
public class JsoupInlineImageTranslatorTest {
	private static String FOLDER = "/inliner/images/jsoup/";
	private static String SOURCE_FOLDER = FOLDER+"source/";
	private static String EXPECTED_FOLDER = FOLDER+"expected/";

	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
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
		LookupMappingResolver resourceResolver = new LookupMappingResourceResolverBuilder().useDefaults().withPrefix(SOURCE_FOLDER).build();
		MimeTypeProvider mimetypeProvider = new TikaProvider();
		ImageInliner inliner = new EveryImageInliner(new JsoupAttachImageInliner(generator), new JsoupBase64ImageInliner());
		translator = new InlineImageTranslator(inliner, resourceResolver, mimetypeProvider);
	}
	
	@Test
	public void attachImages() throws IOException, ContentTranslatorException {
		// prepare the html and associated images
		String source = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"withImages.html"));
		// do the job
		Content result = translator.translate(new StringContent(source));
		// prepare expected html
		String expected = getExpectedHtml("withImagesBoth.html");
		// prepare expected attachments
		List<Attachment> expectedAttachments = getAttachments(loadImages("fb.gif", "h1.gif", "left.gif", "right.gif", "tw.gif"));
		// assertions
		Assert.assertTrue("should be ContentWithAttachments", result instanceof ContentWithAttachments);
		ContentWithAttachments contentWithAttachments = (ContentWithAttachments) result;
		AssertHtml.assertSimilar(expected, contentWithAttachments.getContent().toString());
		Assert.assertEquals("should have 5 attachments", 5, contentWithAttachments.getAttachments().size());
		Assert.assertEquals("should have valid attachments", new HashSet<>(expectedAttachments), new HashSet<>(contentWithAttachments.getAttachments()));
	}
	
	
	@Test
	public void skipAttach() throws IOException, ContentTranslatorException {
		// prepare the html and associated images
		String source = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"skipInline.html"));
		// do the job
		Content result = translator.translate(new StringContent(source));
		// prepare expected html
		String expected = getExpectedHtml("skipInlineBoth.html");
		// prepare expected attachments
		List<Attachment> expectedAttachments = getAttachments(loadImages("fb.gif", "h1.gif"));
		// assertions
		Assert.assertTrue("should be ContentWithAttachments", result instanceof ContentWithAttachments);
		ContentWithAttachments contentWithAttachments = (ContentWithAttachments) result;
		AssertHtml.assertSimilar(expected, contentWithAttachments.getContent().toString());
		Assert.assertEquals("should have 2 attachments", 2, contentWithAttachments.getAttachments().size());
		Assert.assertEquals("should have valid attachments", new HashSet<>(expectedAttachments), new HashSet<>(contentWithAttachments.getAttachments()));
	}
	
	
	//---------------------------------------------------------------//
	//                           Utilities                           //
	//---------------------------------------------------------------//
	
	private static Attachment getAttachment(ImageResource image) {
		return new Attachment(new ByteResource(image.getName(), image.getContent()), null, ContentDisposition.INLINE, "<"+image.getName()+">");
	}
	
	private static List<Attachment> getAttachments(List<ImageResource> images) {
		List<Attachment> attachments = new ArrayList<>(images.size());
		for(ImageResource image : images) {
			attachments.add(getAttachment(image));
		}
		return attachments;
	}
	
	private static String getExpectedHtml(String fileName) throws IOException {
		return IOUtils.toString(JsoupAttachImageInlinerTest.class.getResourceAsStream(EXPECTED_FOLDER+fileName));
	}
	
	private static List<ImageResource> loadImages(String... imageNames) throws IOException {
		List<ImageResource> resources = new ArrayList<>(imageNames.length);
		for(String imageName : imageNames) {
			resources.add(new ImageResource(imageName, "images/"+imageName, IOUtils.toByteArray(JsoupAttachImageInlinerTest.class.getResourceAsStream(SOURCE_FOLDER+"images/"+imageName)), "images/gif"));
		}
		return resources;
	}
}
