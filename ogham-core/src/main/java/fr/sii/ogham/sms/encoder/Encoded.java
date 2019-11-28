package fr.sii.ogham.sms.encoder;

/**
 * Represents an encoded message. The charset used to encode the string into a
 * byte array is kept to indicate how the string has been encoded.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Encoded {
	/**
	 * @return the string encoded into a byte array
	 */
	byte[] getBytes();

	/**
	 * @return the charset used to encode the string
	 */
	String getCharsetName();
}
