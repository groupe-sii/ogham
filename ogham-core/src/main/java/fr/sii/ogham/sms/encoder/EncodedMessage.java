package fr.sii.ogham.sms.encoder;

/**
 * Keeps the unencoded original message in addition to the encoded message.
 * 
 * @author Aurélien Baudet
 *
 */
public class EncodedMessage implements Encoded {
	private final String originalMessage;
	private final byte[] encodedBytes;
	private final String encodedCharsetName;

	/**
	 * Initializes with the unencoded message, the result of encoding and the
	 * charset used to encode.
	 * 
	 * @param originalMessage
	 *            the unencoded message
	 * @param encodedBytes
	 *            the result of encoding as byte array
	 * @param encodedCharsetName
	 *            the charset used to encode
	 */
	public EncodedMessage(String originalMessage, byte[] encodedBytes, String encodedCharsetName) {
		super();
		this.originalMessage = originalMessage;
		this.encodedBytes = encodedBytes;
		this.encodedCharsetName = encodedCharsetName;
	}

	/**
	 * Initializes with the unencoded message end the encoded message.
	 * 
	 * @param originalMessage
	 *            the unencoded message
	 * @param encodedMessage
	 *            the encoded message
	 */
	public EncodedMessage(String originalMessage, Encoded encodedMessage) {
		this(originalMessage, encodedMessage.getBytes(), encodedMessage.getCharsetName());
	}

	@Override
	public byte[] getBytes() {
		return encodedBytes;
	}

	@Override
	public String getCharsetName() {
		return encodedCharsetName;
	}

	/**
	 * @return the unencode message
	 */
	public String getOriginalMessage() {
		return originalMessage;
	}

}
