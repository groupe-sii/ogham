package fr.sii.notification.it.html.translator;

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

import fr.sii.notification.core.builder.LookupMappingResourceResolverBuilder;
import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.id.generator.IdGenerator;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.StringContent;
import fr.sii.notification.core.mimetype.JMimeMagicProvider;
import fr.sii.notification.core.resource.ByteResource;
import fr.sii.notification.core.resource.resolver.LookupMappingResolver;
import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.attachment.ContentDisposition;
import fr.sii.notification.email.message.content.ContentWithAttachments;
import fr.sii.notification.helper.html.AssertHtml;
import fr.sii.notification.helper.rule.LoggingTestRule;
import fr.sii.notification.html.inliner.ImageResource;
import fr.sii.notification.html.inliner.impl.jsoup.JsoupAttachImageInliner;
import fr.sii.notification.html.translator.InlineImageTranslator;
import fr.sii.notification.ut.html.inliner.impl.JsoupAttachImageInlinerTest;

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
		translator = new InlineImageTranslator(new JsoupAttachImageInliner(generator), resourceResolver, new JMimeMagicProvider());
	}
	
	@Test
	public void withImages() throws IOException, ContentTranslatorException {
		// prepare the html and associated images
		String source = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"withImages.html"));
		// do the job
		Content result = translator.translate(new StringContent(source));
		// prepare expected html
		String expected = generateExpectedHtml("withImages.html", "fb.gif", "h1.gif", "left.gif", "right.gif", "tw.gif");
		// prepare expected attachments
		List<Attachment> expectedAttachments = getAttachments(loadImages("fb.gif", "h1.gif", "left.gif", "right.gif", "tw.gif"));
		// assertions
		Assert.assertTrue("should be ContentWithAttachments", result instanceof ContentWithAttachments);
		ContentWithAttachments contentWithAttachments = (ContentWithAttachments) result;
		AssertHtml.assertSimilar(expected, contentWithAttachments.getContent().toString());
		Assert.assertEquals("should have 5 attachments", 5, contentWithAttachments.getAttachments().size());
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
	
	private static String generateExpectedHtml(String fileName, String... imageNames) throws IOException {
		String expected = IOUtils.toString(JsoupAttachImageInlinerTest.class.getResourceAsStream(EXPECTED_FOLDER+fileName));
		for(String imageName : imageNames) {
			expected = expected.replaceAll("images/"+imageName, "cid:"+imageName);
		}
		return expected;
	}
	
	private static List<ImageResource> loadImages(String... imageNames) throws IOException {
		List<ImageResource> resources = new ArrayList<>(imageNames.length);
		for(String imageName : imageNames) {
			resources.add(new ImageResource(imageName, "images/"+imageName, IOUtils.toByteArray(JsoupAttachImageInlinerTest.class.getResourceAsStream(SOURCE_FOLDER+"images/"+imageName)), "images/gif"));
		}
		return resources;
	}
}
