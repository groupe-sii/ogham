package fr.sii.ogham.core.exception.template;

import fr.sii.ogham.core.exception.MessagingException;

public class ContextException extends MessagingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 692730330861798854L;

	public ContextException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContextException(String message) {
		super(message);
	}

	public ContextException(Throwable cause) {
		super(cause);
	}

}
