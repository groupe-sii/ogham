package fr.sii.ogham.template.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingRuntimeException;

/**
 * This exception is a {@link RuntimeException} and is useful when no checked
 * exception can be thrown. This is mainly used by template engine integration
 * classes.
 * 
 * This is a general exception and should use subclasses to indicate the reason.
 * 
 * @author Aurélien Baudet
 *
 */
public class TemplateRuntimeException extends MessagingRuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public TemplateRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateRuntimeException(String message) {
		super(message);
	}

	public TemplateRuntimeException(Throwable cause) {
		super(cause);
	}
}
