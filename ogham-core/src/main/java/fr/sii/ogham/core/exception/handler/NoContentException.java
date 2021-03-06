package fr.sii.ogham.core.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import java.util.List;

import fr.sii.ogham.core.message.content.MultiContent;

/**
 * A specialized exception that is thrown when the content of the message is not
 * set or empty.
 * 
 * @author Aurélien Baudet
 *
 */
public class NoContentException extends ContentTranslatorException {
	/**
	 * 
	 */
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient MultiContent content;
	private final List<ContentTranslatorException> errors;

	public NoContentException(String message, MultiContent content, List<ContentTranslatorException> errors) {
		super(message);
		this.content = content;
		this.errors = errors;
	}

	public MultiContent getContent() {
		return content;
	}

	public List<ContentTranslatorException> getErrors() {
		return errors;
	}
}
