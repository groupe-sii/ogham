package fr.sii.ogham.email.exception.javamail;

import fr.sii.ogham.core.message.content.Content;

public class NoContentHandlerException extends ContentHandlerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7169028390651116070L;

	public NoContentHandlerException(String message, Content content) {
		super(message, content);
	}

}
