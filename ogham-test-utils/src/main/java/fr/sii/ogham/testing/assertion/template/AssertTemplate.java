package fr.sii.ogham.testing.assertion.template;

import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Assert;

import fr.sii.ogham.testing.assertion.util.AssertionRegistry;
import fr.sii.ogham.testing.assertion.util.Executable;
import fr.sii.ogham.testing.assertion.util.FailAtEndRegistry;

/**
 * Assertion class that simplifies checking template content.
 * 
 * @author Aurélien Baudet
 *
 */
public final class AssertTemplate {
	/**
	 * Assert that the received content is same as the expected content. The
	 * check is strict (totally identical even new line characters). The expected content is loaded
	 * from the classpath.
	 * 
	 * @param expectedContentPath
	 *            the path to the expected content available in the classpath
	 * @param content
	 *            the received content to check
	 * @throws IOException
	 *             when the expected content couldn't be read
	 */
	public static void assertEquals(String expectedContentPath, Object content) throws IOException {
		AssertionRegistry registry = new FailAtEndRegistry();
		assertEquals(loadOrNull(expectedContentPath, registry), content, true, registry);
		registry.execute();
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
	public static void assertSimilar(String expectedContentPath, Object content) throws IOException {
		AssertionRegistry registry = new FailAtEndRegistry();
		assertEquals(loadOrNull(expectedContentPath, registry), content, false, registry);
		registry.execute();
	}

	/**
	 * Assert that the received content is same as the expected content. The
	 * check is strict (totally identical even new line characters).
	 * 
	 * @param expectedContent
	 *            the expected content
	 * @param content
	 *            the received content to check
	 */
	public static void assertEquals(String expectedContent, String content) {
		AssertionRegistry registry = new FailAtEndRegistry();
		assertEquals(expectedContent, content, true, registry);
		registry.execute();
	}

	/**
	 * Assert that the received content is same as the expected content. The
	 * check is permissive (new line characters are ignored).
	 * 
	 * @param expectedContent
	 *            the expected content
	 * @param content
	 *            the received content to check
	 */
	public static void assertSimilar(String expectedContent, String content) {
		AssertionRegistry registry = new FailAtEndRegistry();
		assertEquals(expectedContent, content, false, registry);
		registry.execute();
	}
	
	private static void assertEquals(String expectedContent, Object content, boolean strict, AssertionRegistry registry) {
		String expected = strict ? expectedContent : sanitize(expectedContent);
		String contentAsString = content==null ? null : content.toString();
		String actual = strict ? contentAsString : sanitize(contentAsString);
		registry.register(() -> Assert.assertEquals("parsed template is different to expected content", expected == null ? null : expected.replace("\r", ""), actual == null ? null : actual.replace("\r", "")));
	}
	
	private static String loadOrNull(String path, AssertionRegistry registry) throws IOException {
		if (path == null) {
			return null;
		}
		try {
			return resourceAsString(path, Charset.defaultCharset());
		} catch (IOException e) {
			registry.register(failure(e));
			return null;
		}
	}
	
	private static <E extends Exception> Executable<E> failure(E exception) {
		return new Executable<E>() {
			@Override
			public void run() throws E {
				throw exception;
			}
		};
	}

	private static String sanitize(String str) {
		if (str == null) {
			return null;
		}
		return str.replaceAll("\r|\n", "");
	}
	
	private AssertTemplate() {
		super();
	}
}
