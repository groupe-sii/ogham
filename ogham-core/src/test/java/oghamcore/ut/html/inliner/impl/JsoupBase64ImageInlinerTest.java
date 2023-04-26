package oghamcore.ut.html.inliner.impl;

import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.html.inliner.ContentWithImages;
import fr.sii.ogham.html.inliner.ImageResource;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupBase64ImageInliner;
import fr.sii.ogham.testing.assertion.html.AssertHtml;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fr.sii.ogham.html.inliner.impl.jsoup.ImageInlineUtils.removeOghamAttributes;
import static fr.sii.ogham.testing.util.ResourceUtils.resource;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;

@LogTestInformation
public class JsoupBase64ImageInlinerTest {
	private static String FOLDER = "/inliner/images/jsoup/";
	private static String SOURCE_FOLDER = FOLDER+"source/";
	private static String EXPECTED_FOLDER = FOLDER+"expected/";

	private JsoupBase64ImageInliner inliner;

	@BeforeEach
	public void setUp() {
		inliner = new JsoupBase64ImageInliner();
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
		String expected = getExpectedHtml("withImagesBase64.html");
		AssertHtml.assertEquals(expected, inlinedHtml);
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
		String expected = getExpectedHtml("skipInlineBase64.html");
		AssertHtml.assertEquals(expected, inlinedHtml);
	}
	
	
	@Test
	public void differentImageFormats() throws IOException {
		// prepare html and associated images
		String source = resourceAsString(SOURCE_FOLDER+"differentImageFormats.html");
		List<ImageResource> images = loadImages("81w+rF3K1bL.png", "Blazar_12July2018_2.gif", "NINTCHDBPICT000454098440.jpg", "place-address.png");
		// do the job
		ContentWithImages inlined = inliner.inline(source, images);
		// remove ogham attributes (internal use only)
		String inlinedHtml = removeOghamAttributes(inlined.getContent());
		// prepare expected result for the html
		String expected = getExpectedHtml("differentImageFormatsBase64.html");
		AssertHtml.assertEquals(expected, inlinedHtml);
	}

	
	//---------------------------------------------------------------//
	//                           Utilities                           //
	//---------------------------------------------------------------//
	
	private static String getExpectedHtml(String fileName) throws IOException {
		return resourceAsString(EXPECTED_FOLDER+fileName);
	}
	
	private static List<ImageResource> loadImages(String... imageNames) throws IOException {
		List<ImageResource> resources = new ArrayList<>(imageNames.length);
		for(String imageName : imageNames) {
			resources.add(new ImageResource(imageName, "images/"+imageName, new UnresolvedPath("images/"+imageName), resource(SOURCE_FOLDER+"images/"+imageName), getMimetype(imageName)));
		}
		return resources;
	}

	private static String getMimetype(String imageName) {
		return "image/" + imageName.substring(imageName.lastIndexOf('.') + 1);
	}
}
