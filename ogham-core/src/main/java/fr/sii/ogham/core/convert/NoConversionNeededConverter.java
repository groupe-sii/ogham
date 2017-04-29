package fr.sii.ogham.core.convert;

import fr.sii.ogham.core.exception.convert.ConversionException;

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
