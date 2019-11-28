package fr.sii.ogham.sms.encoder;

/**
 * Extends {@link Encoder} to indicate if the encoder is able to encode the
 * message.
 * 
 * @author Aurélien Baudet
 *
 */
public interface SupportingEncoder extends Encoder {
	/**
	 * Indicates if the message can be encoded by this this encoder.
	 * 
	 * @param message
	 *            the message that is about to be encoded
	 * @return true if this encoder is able to encode the message, false
	 *         otherwise
	 */
	boolean canEncode(String message);
}
