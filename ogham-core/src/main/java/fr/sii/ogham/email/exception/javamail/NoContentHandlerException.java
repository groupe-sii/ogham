package fr.sii.ogham.email.exception.javamail;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.message.content.Content;

public class NoContentHandlerException extends ContentHandlerException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public NoContentHandlerException(String message, Content content) {
		super(message, content);
	}

}
