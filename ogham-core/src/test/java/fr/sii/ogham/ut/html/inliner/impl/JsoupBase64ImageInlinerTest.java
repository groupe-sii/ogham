package fr.sii.ogham.ut.html.inliner.impl;

import static fr.sii.ogham.assertion.OghamAssertions.resource;
import static fr.sii.ogham.assertion.OghamAssertions.resourceAsString;
import static fr.sii.ogham.html.inliner.impl.jsoup.ImageInlineUtils.removeOghamAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.helper.html.AssertHtml;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.html.inliner.ContentWithImages;
import fr.sii.ogham.html.inliner.ImageResource;
import fr.sii.ogham.html.inliner.impl.jsoup.JsoupBase64ImageInliner;

public class JsoupBase64ImageInlinerTest {
	private static String FOLDER = "/inliner/images/jsoup/";
	private static String SOURCE_FOLDER = FOLDER+"source/";
	private static String EXPECTED_FOLDER = FOLDER+"expected/";
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	private JsoupBase64ImageInliner inliner;

	@Before
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
		AssertHtml.assertSimilar(expected, inlinedHtml);
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
		AssertHtml.assertSimilar(expected, inlinedHtml);
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
			resources.add(new ImageResource(imageName, "images/"+imageName, new UnresolvedPath("images/"+imageName), resource(SOURCE_FOLDER+"images/"+imageName), "image/gif"));
		}
		return resources;
	}
}
