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
	 * @return
	 * @throws EncodingException
	 */
	byte[] encode(String messageStringContent)
			throws EncodingException;

}