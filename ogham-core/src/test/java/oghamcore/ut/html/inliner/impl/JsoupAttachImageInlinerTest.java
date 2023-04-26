package oghamcore.ut.html.inliner.impl;

import fr.sii.ogham.core.id.generator.IdGenerator;
import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.attachment.ContentDisposition;
import fr.sii.ogham.html.inliner.ContentWithImages;
import fr.sii.ogham.html.inliner.ImageResource;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupAttachImageInliner;
import fr.sii.ogham.testing.assertion.html.AssertHtml;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fr.sii.ogham.html.inliner.impl.jsoup.ImageInlineUtils.removeOghamAttributes;
import static fr.sii.ogham.testing.util.ResourceUtils.resource;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.mockito.quality.Strictness.LENIENT;

@LogTestInformation
@MockitoSettings(strictness = LENIENT)
public class JsoupAttachImageInlinerTest {
	private static String FOLDER = "/inliner/images/jsoup/";
	private static String SOURCE_FOLDER = FOLDER+"source/";
	private static String EXPECTED_FOLDER = FOLDER+"expected/";

	private JsoupAttachImageInliner inliner;
	
	@Mock
	private IdGenerator generator;

	@BeforeEach
	public void setUp() {
		Mockito.when(generator.generate("fb.gif")).thenReturn("fb.gif");
		Mockito.when(generator.generate("h1.gif")).thenReturn("h1.gif");
		Mockito.when(generator.generate("left.gif")).thenReturn("left.gif");
		Mockito.when(generator.generate("right.gif")).thenReturn("right.gif");
		Mockito.when(generator.generate("tw.gif")).thenReturn("tw.gif");
		inliner = new JsoupAttachImageInliner(generator);
	}
	
	@Test
	public void withImages() throws IOException {
		// prepare html and associated images
		String source = resourceAsString(SOURCE_FOLDER+"withImages.html");
		List<ImageResource> images = loadImages("fb.gif", "h1.gif", "left.gif", "right.gif", "tw.gif");
		// do the job
		ContentWithImages inlined = inliner.inline(source, images);
		// remove ogham attributes (internal use only)
		String inlinedHtml = removeOghamAttributes(inlined.getContent());
		// prepare expected result for the html
		String expected = getExpectedHtml("withImagesAttach.html");
		// prepare expected attachments
		List<Attachment> expectedAttachments = getAttachments(images);
		// assertions
		AssertHtml.assertEquals(expected, inlinedHtml);
		Assertions.assertEquals(5, inlined.getAttachments().size(), "should have 5 attachments");
		Assertions.assertEquals(expectedAttachments, inlined.getAttachments(), "should have valid attachments");
	}
	
	@Test
	public void skipInline() throws IOException {
		// prepare html and associated images
		String source = resourceAsString(SOURCE_FOLDER+"skipInline.html");
		List<ImageResource> images = loadImages("fb.gif", "h1.gif", "left.gif", "right.gif", "tw.gif");
		// do the job
		ContentWithImages inlined = inliner.inline(source, images);
		// remove ogham attributes (internal use only)
		String inlinedHtml = removeOghamAttributes(inlined.getContent());
		// prepare expected result for the html
		String expected = getExpectedHtml("skipInlineAttach.html");
		// prepare expected attachments
		List<Attachment> expectedAttachments = getAttachments(loadImages("fb.gif", "h1.gif"));
		// assertions
		AssertHtml.assertEquals(expected, inlinedHtml);
		Assertions.assertEquals(2, inlined.getAttachments().size(), "should have 2 attachments");
		Assertions.assertEquals(expectedAttachments, inlined.getAttachments(), "should have valid attachments");
	}
	
	@Test
	@Disabled("Not yet implemented")
	public void duplicatedImage() {
		// TODO: when the html contains the same image several times, it should generate only one attachment for it
		Assertions.fail("Not implemented");
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
		return resourceAsString(EXPECTED_FOLDER+fileName);
	}
	
	private static List<ImageResource> loadImages(String... imageNames) throws IOException {
		List<ImageResource> resources = new ArrayList<>(imageNames.length);
		for(String imageName : imageNames) {
			resources.add(new ImageResource(imageName, "images/"+imageName, new UnresolvedPath("images/"+imageName), resource(SOURCE_FOLDER+"images/"+imageName), "image/gif"));
		}
		return resources;
	}
}
