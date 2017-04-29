package fr.sii.ogham.core.convert;

import java.lang.reflect.Array;

import fr.sii.ogham.core.exception.convert.ConversionException;

public class StringToArrayConverter implements SupportingConverter {
	private final Converter elementsConverter;
	private final String splitPattern;
	
	public StringToArrayConverter(Converter elementsConverter) {
		this(elementsConverter, ",\\s*");
	}
	public StringToArrayConverter(Converter elementsConverter, String splitPattern) {
		super();
		this.elementsConverter = elementsConverter;
		this.splitPattern = splitPattern;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object source, Class<T> targetType) throws ConversionException {
		String s = (String) source;
		String[] parts = s.split(splitPattern);
		Object target = Array.newInstance(targetType.getComponentType(), parts.length);
		for (int i = 0; i < parts.length; i++) {
			String sourceElement = parts[i];
			Object targetElement = elementsConverter.convert(sourceElement.trim(), targetType.getComponentType());
			Array.set(target, i, targetElement);
		}
		return (T) target;
	}

	@Override
	public boolean supports(Class<?> sourceType, Class<?> targetType) {
		return String.class.isAssignableFrom(sourceType) && String[].class.isAssignableFrom(targetType);
	}

}
