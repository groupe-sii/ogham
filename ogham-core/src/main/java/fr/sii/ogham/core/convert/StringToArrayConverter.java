package fr.sii.ogham.core.convert;

import java.lang.reflect.Array;

import fr.sii.ogham.core.exception.convert.ConversionException;

/**
 * Converts a string to an array of anything. The string is split on
 * {@literal ,} by default. You can also provide your own separator.
 * 
 * Each split value is trimmed to remove any spaces before or after. For
 * example:
 * 
 * <pre>
 * String source = "  foo  ,    bar,   abc";
 * // calling the converter
 * String[] result = converter.convert(source, String[].class);
 * for (String value : result) {
 * 	System.out.println("'" + value + "'");
 * }
 * // prints:
 * // 'foo'
 * // 'bar'
 * // 'abc'
 * </pre>
 * 
 * This converter is also able to convert the string to an array of objects. For
 * example:
 * 
 * <pre>
 * String source = "1, 2, 3";
 * Integer[] numbers = converter.convert(source, Integer[].class);
 * </pre>
 * 
 * If you have registered a custom converter to handle elements, you can
 * directly convert string to your objects:
 * 
 * <pre>
 * String source = "bob, joe";
 * Person[] persons = converter.convert(source, Person[].class);
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class StringToArrayConverter implements SupportingConverter {
	private final Converter elementsConverter;
	private final String splitPattern;

	/**
	 * Initializes with the default separator {@literal ,} and another converter
	 * that is used to convert each split element.
	 * 
	 * @param elementsConverter
	 *            converts each element to the target type
	 */
	public StringToArrayConverter(Converter elementsConverter) {
		this(elementsConverter, ",\\s*");
	}

	/**
	 * Initializes with the provided separator and another converter that is
	 * used to convert each split element.
	 * 
	 * @param elementsConverter
	 *            converts each element to the target type
	 * @param splitPattern
	 *            the separator that is used to split the source string
	 */
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
