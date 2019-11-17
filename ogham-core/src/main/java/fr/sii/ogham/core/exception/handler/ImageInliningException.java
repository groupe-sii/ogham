package fr.sii.ogham.core.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class ImageInliningException extends ContentTranslatorException {
	/**
	 * 
	 */
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public ImageInliningException(String message, Throwable cause) {
		super(message, cause);
	}

	public ImageInliningException(String message) {
		super(message);
	}

	public ImageInliningException(Throwable cause) {
		super(cause);
	}

}
