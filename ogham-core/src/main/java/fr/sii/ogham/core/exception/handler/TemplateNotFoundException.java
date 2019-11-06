package fr.sii.ogham.core.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class TemplateNotFoundException extends ContentTranslatorException {
	/**
	 * 
	 */
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public TemplateNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateNotFoundException(String message) {
		super(message);
	}

	public TemplateNotFoundException(Throwable cause) {
		super(cause);
	}

}
