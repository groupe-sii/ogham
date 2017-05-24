package fr.sii.ogham.core.convert;

import fr.sii.ogham.core.exception.convert.ConversionException;

/**
 * Converts a source object into another type.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Converter {
	/**
	 * Convert the given {@code source} to the specified {@code targetType}.
	 * 
	 * @param source
	 *            the source object to convert (may be {@code null})
	 * @param targetType
	 *            the target type to convert to (required)
	 * @param <T>
	 *            the type of the result
	 * @return the converted object, an instance of targetType
	 * @throws ConversionException
	 *             if a conversion exception occurred
	 * @throws IllegalArgumentException
	 *             if targetType is {@code null}
	 */
	<T> T convert(Object source, Class<T> targetType) throws ConversionException;
}
