package fr.sii.ogham.email.exception.sendgrid;

import fr.sii.ogham.core.exception.MessagingException;

/**
 * Exceptions related to errors happening at the content handler level.
 * 
 * @see ContentHandlerException
 */
public final class ContentHandlerException extends MessagingException {

	private static final long serialVersionUID = -557251869013180354L;

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
