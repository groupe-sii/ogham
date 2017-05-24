package fr.sii.ogham.core.convert;

import fr.sii.ogham.core.exception.convert.ConversionException;

/**
 * A no-op converter that just casts the source to match the target type.
 * 
 * Cast is only applied if the source object is an instance of target type.
 * 
 * @author Aurélien Baudet
 *
 */
public class NoConversionNeededConverter implements SupportingConverter {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object source, Class<T> targetType) throws ConversionException {
		return (T) source;
	}

	@Override
	public boolean supports(Class<?> sourceType, Class<?> targetType) {
		return targetType.isAssignableFrom(sourceType);
	}

}
