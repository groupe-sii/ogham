package fr.sii.ogham.testing.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

/**
 * Contains utility methods to load resources in tests.
 * 
 * @author Aurélien Baudet
 *
 */
public final class ResourceUtils {

	/**
	 * Utility method that loads a file content from the classpath. UTF-8
	 * charset is used.
	 * 
	 * @param path
	 *            the path to the classpath resource
	 * @return the content of the file
	 * @throws IOException
	 *             when resource can't be read or doesn't exist
	 */
	public static String resourceAsString(String path) throws IOException {
		return ResourceUtils.resourceAsString(path, StandardCharsets.UTF_8);
	}

	/**
	 * Utility method that loads a file content from the classpath.
	 * 
	 * @param path
	 *            the path to the classpath resource
	 * @param charset
	 *            the charset used for reading the file
	 * @return the content of the file
	 * @throws IOException
	 *             when resource can't be read or doesn't exist
	 */
	public static String resourceAsString(String path, Charset charset) throws IOException {
		return IOUtils.toString(resource(path), charset.name());
	}

	/**
	 * Utility method that loads a file content from the classpath.
	 * 
	 * @param path
	 *            the path to the classpath resource
	 * @return the content of the file as byte array
	 * @throws IOException
	 *             when resource can't be read or doesn't exist
	 */
	public static byte[] resource(String path) throws IOException {
		InputStream resource = ResourceUtils.class.getClassLoader().getResourceAsStream(path.startsWith("/") ? path.substring(1) : path);
		if (resource == null) {
			throw new FileNotFoundException("No resource found for path '" + path + "'");
		}
		return IOUtils.toByteArray(resource);
	}

	private ResourceUtils() {
		super();
	}

}
