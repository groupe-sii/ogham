package fr.sii.ogham.core.exception.convert;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.MessagingRuntimeException;

/**
 * Ogham provides {@link PropertyResolver} implementations to load configuration
 * values from any source. The configuration values are always strings.
 * Therefore, if the value to use in Ogham code is an Integer, a conversion
 * needs to be performed. This is done using a {@link Converter}.
 * 
 * This exception is thrown when the conversion fails for any reason (such as
 * wrong format, wrong value, unknown type, ...).
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class ConversionException extends MessagingRuntimeException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

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
