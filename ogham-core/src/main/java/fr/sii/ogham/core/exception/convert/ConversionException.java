package fr.sii.ogham.core.exception.convert;

import fr.sii.ogham.core.exception.MessagingRuntimeException;

public class ConversionException extends MessagingRuntimeException {
	private static final long serialVersionUID = 4793045577381729380L;

	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(Throwable cause) {
		super(cause);
	}

}
