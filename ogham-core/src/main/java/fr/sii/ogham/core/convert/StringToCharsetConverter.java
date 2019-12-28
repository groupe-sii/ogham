package fr.sii.ogham.core.convert;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import fr.sii.ogham.core.exception.convert.ConversionException;

/**
 * Converts a string to a {@link Charset} instance. It uses
 * {@link Charset#forName(String)} to instantiate the charset.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class StringToCharsetConverter implements SupportingConverter {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object source, Class<T> targetType) throws ConversionException {
		String charsetName = (String) source;
		if (charsetName == null || charsetName.isEmpty()) {
			return null;
		}
		try {
			return (T) Charset.forName(charsetName);
		} catch(UnsupportedCharsetException e) {
			throw new ConversionException("Failed to convert "+charsetName+" into Charset", e);
		}
	}

	@Override
	public boolean supports(Class<?> sourceType, Class<?> targetType) {
		return String.class.isAssignableFrom(sourceType) && Charset.class.isAssignableFrom(targetType);
	}

}
