package fr.sii.notification.helper;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import fr.sii.notification.core.message.content.Content;

public class AssertTemplate {
	public static void assertEquals(String expectedContentPath, Content content, boolean strict) throws IOException {
		assertEquals(IOUtils.toString(AssertTemplate.class.getResourceAsStream(expectedContentPath)), content.toString(), strict);
	}
	
	public static void assertEquals(String expectedContentPath, Content content) throws IOException {
		assertEquals(expectedContentPath, content, false);
	}
	
	public static void assertEquals(String expectedContent, String content, boolean strict) throws IOException {
		Assert.assertEquals("parsed template is different to expected content", strict ? expectedContent : sanitize(expectedContent), strict ? content : sanitize(content));
	}
	
	private static String sanitize(String str) {
		return str.replaceAll("\r|\n", "");
	}
}
