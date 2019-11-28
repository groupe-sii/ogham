package fr.sii.ogham.sms.sender.impl.cloudhopper.charset;

import fr.sii.ogham.sms.encoder.Encoder;
import fr.sii.ogham.sms.exception.message.EncodingException;

/**
 * Handles charset detection for messages content.
 * 
 * @author cdejonghe
 * @deprecated charset handling and encoding is now handled by {@link Encoder}
 * 
 */
public interface CloudhopperCharsetHandler {

	/**
	 * Encodes the message string content after a charset detection.
	 * 
	 * @param messageStringContent
	 *            the message as string to encode
	 * @return the encoded string as byte array
	 * @throws EncodingException
	 *             when message can't be encoded
	 * @deprecated charset handling and encoding is now handled by
	 *             {@link Encoder}
	 */
	byte[] encode(String messageStringContent) throws EncodingException;

}