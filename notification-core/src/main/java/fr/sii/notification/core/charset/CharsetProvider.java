package fr.sii.notification.core.charset;

import java.nio.charset.Charset;

/**
 * Interface for all charset providers. A charset provider generate a charset
 * for the provided string.
 * 
 * @author Aurélien Baudet
 *
 */
public interface CharsetProvider {
	/**
	 * Provide the charset for the input string. If no charset could be
	 * determined, then null is returned.
	 * 
	 * @param str
	 *            the string to analyze
	 * @return the charset to use
	 */
	public Charset getCharset(String str);
}
