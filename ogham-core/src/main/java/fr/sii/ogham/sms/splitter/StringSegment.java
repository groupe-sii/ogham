package fr.sii.ogham.sms.splitter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * A segment initialized from a string. The charset used to encode the string to
 * a byte array is also tracked in order to indicate which encoding was used.
 * 
 * @author Aurélien Baudet
 *
 */
public class StringSegment implements Segment {
	private final String message;
	private final String charsetName;
	private final byte[] bytes;

	/**
	 * Initializes the segment with a message. The default charset of the
	 * platform is used.
	 * 
	 * WARNING: As the default charset for the platform is used, it may lead to
	 * inconsistencies.
	 * 
	 * @param message
	 *            the text for the segment
	 */
	public StringSegment(String message) {
		super();
		this.message = message;
		charsetName = Charset.defaultCharset().name();
		bytes = message.getBytes(Charset.defaultCharset());
	}

	/**
	 * Initializes the segment with a message. The message string is encoded to
	 * a byte array using the provided charset.
	 * 
	 * @param message
	 *            the text for the segment
	 * @param charset
	 *            the charset to use for encoding the string to a byte array
	 */
	public StringSegment(String message, Charset charset) {
		super();
		this.message = message;
		charsetName = charset.name();
		bytes = message.getBytes(charset);
	}

	/**
	 * Initializes the segment with a message. The message string is encoded to
	 * a byte array using the provided charset name.
	 * 
	 * @param message
	 *            the text for the segment
	 * @param charsetName
	 *            the charset name to use for encoding the string to a byte
	 *            array
	 * @throws UnsupportedEncodingException
	 *             when the charset could not be found
	 */
	public StringSegment(String message, String charsetName) throws UnsupportedEncodingException {
		super();
		this.message = message;
		this.charsetName = charsetName;
		bytes = message.getBytes(charsetName);
	}

	@Override
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * @return the original message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the charset name used to encode the original message string to a
	 *         byte array
	 */
	public String getCharsetName() {
		return charsetName;
	}

}
