package fr.sii.ogham.email.exception.sendgrid;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;

/**
 * Exceptions related to errors happening at the content handler level.
 * 
 * @see ContentHandlerException
 */
public final class ContentHandlerException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	/**
	 * Builds a content handler-related exception due to an underlying
	 * exception.
	 * 
	 * @param message
	 *            a description of the error
	 * @param cause
	 *            the underlying exception
	 */
	public ContentHandlerException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Builds a content handler-related exception with no underlying exception.
	 * 
	 * @param message
	 *            a description of the error
	 */
	public ContentHandlerException(final String message) {
		super(message);
	}

}
