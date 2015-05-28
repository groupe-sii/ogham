package fr.sii.notification.core.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Helper class for I/O management:
 * <ul>
 * <li>Read a stream and provide its content as byte array</li>
 * </ul>
 * <p>
 * This work can be done by several libraries. The aim of this class is to be
 * able to change the implementation easily to use another library for example.
 * </p>
 * <p>
 * For example, we could find which library is available in the classpath and
 * use this library instead of forcing users to include Apache Commons IO
 * library.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public final class IOUtils {
	/**
	 * Get the contents of an InputStream as a byte[].
	 * 
	 * This method buffers the input internally, so there is no need to use a
	 * BufferedInputStream.
	 * 
	 * @param stream
	 *            the InputStream to read from
	 * @return the requested byte array
	 * @throws NullPointerException
	 *             if the input is null
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static byte[] toByteArray(InputStream stream) throws IOException {
		return org.apache.commons.io.IOUtils.toByteArray(stream);
	}

	private IOUtils() {
		super();
	}
}
