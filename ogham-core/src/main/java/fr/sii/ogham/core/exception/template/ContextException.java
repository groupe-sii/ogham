package fr.sii.ogham.core.exception.template;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

/**
 * General exception to indicate that the context for template parsing couldn't
 * be created.
 * 
 * It may happen in several cases:
 * <ul>
 * <li>The bean can't be accessd</li>
 * <li>A property of a bean can't be accessed</li>
 * <li>...</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 */
public class ContextException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

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
