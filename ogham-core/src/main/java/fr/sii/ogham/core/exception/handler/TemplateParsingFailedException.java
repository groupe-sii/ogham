package fr.sii.ogham.core.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class TemplateParsingFailedException extends ContentTranslatorException {
	/**
	 * 
	 */
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public TemplateParsingFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateParsingFailedException(String message) {
		super(message);
	}

	public TemplateParsingFailedException(Throwable cause) {
		super(cause);
	}

}
