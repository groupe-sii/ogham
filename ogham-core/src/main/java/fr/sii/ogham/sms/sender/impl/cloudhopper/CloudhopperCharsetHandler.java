package fr.sii.ogham.sms.sender.impl.cloudhopper;

import fr.sii.ogham.sms.exception.message.EncodingException;

/**
 * Handles charset detection for messages content.
 * 
 * @author cdejonghe
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
	 */
	byte[] encode(String messageStringContent) throws EncodingException;

}