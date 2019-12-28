package fr.sii.ogham.core.convert;

import static java.util.Locale.ENGLISH;

import java.util.HashSet;
import java.util.Set;

import fr.sii.ogham.core.exception.convert.ConversionException;

/**
 * Converts a string to a boolean value.
 * 
 * Strings that represent a {@code true} value are:
 * <ul>
 * <li>"true"</li>
 * <li>"on"</li>
 * <li>"yes"</li>
 * <li>"1"</li>
 * </ul>
 * 
 * Strings that represent a {@code false} value are:
 * <ul>
 * <li>"false"</li>
 * <li>"off"</li>
 * <li>"no"</li>
 * <li>"0"</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class StringToBooleanConverter implements SupportingConverter {
	private static final Set<String> trueValues = new HashSet<>(4);
	private static final Set<String> falseValues = new HashSet<>(4);

	static {
		trueValues.add("true");
		trueValues.add("on");
		trueValues.add("yes");
		trueValues.add("1");

		falseValues.add("false");
		falseValues.add("off");
		falseValues.add("no");
		falseValues.add("0");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object source, Class<T> targetType) {
		String value = ((String) source).trim();
		if ("".equals(value)) {
			return null;
		}
		value = value.toLowerCase(ENGLISH);
		if (trueValues.contains(value)) {
			return (T) Boolean.TRUE;
		} else if (falseValues.contains(value)) {
			return (T) Boolean.FALSE;
		} else {
			throw new ConversionException("Invalid boolean value '" + source + "'");
		}
	}

	@Override
	public boolean supports(Class<?> sourceType, Class<?> targetType) {
		return String.class.isAssignableFrom(sourceType) && Boolean.class.isAssignableFrom(targetType);
	}

}
