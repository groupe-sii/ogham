package fr.sii.notification.helper;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import fr.sii.notification.core.message.content.Content;

/**
 * Assertion class that simplifies checking template content.
 * 
 * @author Aurélien Baudet
 *
 */
public class AssertTemplate {
	/**
	 * Assert that the received content is same as the expected content. The
	 * check can be either strict (totally identical even new line characters)
	 * or not (new line characters are ignored). The expected content is loaded
	 * from the classpath.
	 * 
	 * @param expectedContentPath
	 *            the path to the expected content available in the classpath
	 * @param content
	 *            the received content to check
	 * @param strict
	 *            true to enable strict checking or false to allow comparison by
	 *            ignoring new lines
	 * @throws IOException
	 *             when the expected content couldn't be read
	 */
	public static void assertEquals(String expectedContentPath, Content content, boolean strict) throws IOException {
		assertEquals(IOUtils.toString(AssertTemplate.class.getResourceAsStream(expectedContentPath)), content.toString(), strict);
	}

	/**
	 * Assert that the received content is same as the expected content. The
	 * check is permissive (new line characters are ignored). The expected
	 * content is loaded from the classpath.
	 * 
	 * @param expectedContentPath
	 *            the path to the expected content available in the classpath
	 * @param content
	 *            the received content to check
	 * @throws IOException
	 *             when the expected content couldn't be read
	 */
	public static void assertEquals(String expectedContentPath, Content content) throws IOException {
		assertEquals(expectedContentPath, content, false);
	}

	/**
	 * Assert that the received content is same as the expected content. The
	 * check can be either strict (totally identical even new line characters)
	 * or not (new line characters are ignored).
	 * 
	 * @param expectedContent
	 *            the expected content available
	 * @param content
	 *            the received content to check
	 * @param strict
	 *            true to enable strict checking or false to allow comparison by
	 *            ignoring new lines
	 * @throws IOException
	 *             when the expected content couldn't be read
	 */
	public static void assertEquals(String expectedContent, String content, boolean strict) throws IOException {
		Assert.assertEquals("parsed template is different to expected content", strict ? expectedContent : sanitize(expectedContent), strict ? content : sanitize(content));
	}

	private static String sanitize(String str) {
		return str.replaceAll("\r|\n", "");
	}
}
