package fr.sii.ogham.sms.encoder;

import fr.sii.ogham.sms.exception.message.EncodingException;

/**
 * An encoder transforms the string message into a byte array.
 * 
 * <p>
 * The result is an {@link Encoded} instance that provides the byte array but
 * also the charset used to convert the string into bytes.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Encoder {
	/**
	 * Encode the message into a byte array.
	 * 
	 * @param message
	 *            the message to encode
	 * @return the encoded message as byte array and the charset used to encode
	 *         it
	 * @throws EncodingException
	 *             when encoding has failed
	 */
	Encoded encode(String message) throws EncodingException;
}
