package fr.sii.ogham.core.exception.filler;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.message.Message;

public class FillMessageException extends MessageException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3928118965036401787L;

	public FillMessageException(String message, Message msg, Throwable cause) {
		super(message, msg, cause);
	}

	public FillMessageException(String message, Message msg) {
		super(message, msg);
	}

	public FillMessageException(Throwable cause, Message msg) {
		super(cause, msg);
	}
}
