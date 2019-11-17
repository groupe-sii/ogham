package fr.sii.ogham.core.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class CssInliningException extends ContentTranslatorException {
	/**
	 * 
	 */
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public CssInliningException(String message, Throwable cause) {
		super(message, cause);
	}

	public CssInliningException(String message) {
		super(message);
	}

	public CssInliningException(Throwable cause) {
		super(cause);
	}

}
