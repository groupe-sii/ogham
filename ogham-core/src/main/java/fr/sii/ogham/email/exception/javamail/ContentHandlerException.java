package fr.sii.ogham.email.exception.javamail;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.Content;

public class ContentHandlerException extends MessagingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3172860709067806202L;
	
	private final Content content;

	public ContentHandlerException(String message, Content content, Throwable cause) {
		super(message, cause);
		this.content = content;
	}

	public ContentHandlerException(String message, Content content) {
		super(message);
		this.content = content;
	}

	public ContentHandlerException(Throwable cause, Content content) {
		super(cause);
		this.content = content;
	}

	public Content getContent() {
		return content;
	}
}
