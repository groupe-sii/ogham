package fr.sii.ogham.core.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.translator.content.ContentTranslator;

/**
 * Ogham uses a chain to transform original content into final content. For
 * example, the original content may be a path to a template. Therefore, there
 * is a {@link ContentTranslator} that is in charge of loading and parse the
 * template.
 * 
 * This is a general exception that has subclasses to indicate the reason of the
 * failure of a {@link ContentTranslator}.
 * 
 * @author Aurélien Baudet
 *
 */
public class ContentTranslatorException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public ContentTranslatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContentTranslatorException(String message) {
		super(message);
	}

	public ContentTranslatorException(Throwable cause) {
		super(cause);
	}

}
