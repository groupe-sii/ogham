package fr.sii.notification.ut.html.inliner.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.notification.helper.html.AssertHtml;
import fr.sii.notification.helper.rule.LoggingTestRule;
import fr.sii.notification.html.inliner.ContentWithImages;
import fr.sii.notification.html.inliner.ImageResource;
import fr.sii.notification.html.inliner.impl.jsoup.JsoupBase64ImageInliner;

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
		String source = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"withImages.html"));
		List<ImageResource> images = loadImages("fb.gif", "h1.gif", "left.gif", "right.gif", "tw.gif");
		// do the job
		ContentWithImages inlined = inliner.inline(source, images);
		// prepare expected result for the html
		String expected = generateExpectedHtml("withImages.html", "fb.gif", "h1.gif", "left.gif", "right.gif", "tw.gif");
		AssertHtml.assertSimilar(expected, inlined.getContent());
	}
	
	
	
	//---------------------------------------------------------------//
	//                           Utilities                           //
	//---------------------------------------------------------------//
	
	private static String generateExpectedHtml(String fileName, String... imageNames) throws IOException {
		String expected = IOUtils.toString(JsoupAttachImageInlinerTest.class.getResourceAsStream(EXPECTED_FOLDER+fileName));
		for(String imageName : imageNames) {
			expected = expected.replaceAll("images/"+imageName, "data:images/gif;base64,"+new Base64().encodeToString(IOUtils.toByteArray(JsoupAttachImageInlinerTest.class.getResourceAsStream(SOURCE_FOLDER+"images/"+imageName))));
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
