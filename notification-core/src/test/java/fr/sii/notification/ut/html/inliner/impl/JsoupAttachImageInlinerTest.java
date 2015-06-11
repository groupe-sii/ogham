package fr.sii.notification.ut.html.inliner.impl;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import fr.sii.notification.helper.html.AssertHtml;
import fr.sii.notification.helper.rule.LoggingTestRule;
import fr.sii.notification.html.inliner.ContentWithImages;
import fr.sii.notification.html.inliner.ImageResource;
import fr.sii.notification.html.inliner.impl.jsoup.JsoupAttachImageInliner;
import fr.sii.notification.mock.html.inliner.PassThroughGenerator;

public class JsoupAttachImageInlinerTest {
	private static String FOLDER = "/inliner/images/jsoup/";
	private static String SOURCE_FOLDER = FOLDER+"source/";
	private static String EXPECTED_FOLDER = FOLDER+"expected/";
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	private JsoupAttachImageInliner inliner;

	@Before
	public void setUp() {
		inliner = new JsoupAttachImageInliner(new PassThroughGenerator());
	}
	
	@Test
	public void withImages() throws IOException {
		String source = IOUtils.toString(getClass().getResourceAsStream(SOURCE_FOLDER+"withImages.html"));
		ImageResource image1 = new ImageResource("fb.gif", "images/fb.gif", IOUtils.toByteArray(getClass().getResourceAsStream(SOURCE_FOLDER+"images/fb.gif")), "images/gif");
		ImageResource image2 = new ImageResource("h1.gif", "images/h1.gif", IOUtils.toByteArray(getClass().getResourceAsStream(SOURCE_FOLDER+"images/h1.gif")), "images/gif");
		ImageResource image3 = new ImageResource("left.gif", "images/left.gif", IOUtils.toByteArray(getClass().getResourceAsStream(SOURCE_FOLDER+"images/left.gif")), "images/gif");
		ImageResource image4 = new ImageResource("right.gif", "images/right.gif", IOUtils.toByteArray(getClass().getResourceAsStream(SOURCE_FOLDER+"images/right.gif")), "images/gif");
		ImageResource image5 = new ImageResource("tw.gif", "images/tw.gif", IOUtils.toByteArray(getClass().getResourceAsStream(SOURCE_FOLDER+"images/tw.gif")), "images/gif");
		ContentWithImages inlined = inliner.inline(source, Arrays.asList(image1, image2, image3, image4, image5));
		String expected = IOUtils.toString(getClass().getResourceAsStream(EXPECTED_FOLDER+"withImages.html"));
		expected = expected.replaceAll("images/fb.gif", "cid:images/fb.gif");
		expected = expected.replaceAll("images/h1.gif", "cid:images/h1.gif");
		expected = expected.replaceAll("images/left.gif", "cid:images/left.gif");
		expected = expected.replaceAll("images/right.gif", "cid:images/right.gif");
		expected = expected.replaceAll("images/tw.gif", "cid:images/tw.gif");
		AssertHtml.assertSimilar(expected, inlined.getContent());
	}
}
