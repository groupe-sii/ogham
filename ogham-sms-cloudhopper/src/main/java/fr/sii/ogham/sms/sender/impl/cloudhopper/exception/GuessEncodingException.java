package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.sms.exception.message.EncodingException;
import fr.sii.ogham.sms.message.Sms;

/**
 * Ogham tries to guess the encoding of the {@link Sms}. This exception is
 * thrown if the automatic guessing has failed.
 * 
 * @author Aurélien Baudet
 *
 */
public class GuessEncodingException extends EncodingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final String source;

	public GuessEncodingException(String message, String source) {
		super(message);
		this.source = source;
	}

	public String getSource() {
		return source;
	}
}
