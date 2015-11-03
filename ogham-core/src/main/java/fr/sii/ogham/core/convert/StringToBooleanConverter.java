package fr.sii.ogham.core.convert;

import java.util.HashSet;
import java.util.Set;

import fr.sii.ogham.core.exception.convert.ConversionException;

public class StringToBooleanConverter implements SupportingConverter {
	private static final Set<String> trueValues = new HashSet<String>(4);
	private static final Set<String> falseValues = new HashSet<String>(4);

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

	@Override
	public <T> T convert(Object source, Class<T> targetType) throws ConversionException {
		String value = ((String) source).trim();
		if ("".equals(value)) {
			return null;
		}
		value = value.toLowerCase();
		if (trueValues.contains(value)) {
			return (T) Boolean.TRUE;
		}
		else if (falseValues.contains(value)) {
			return (T) Boolean.FALSE;
		}
		else {
			throw new ConversionException("Invalid boolean value '" + source + "'");
		}
	}

	@Override
	public boolean supports(Class<?> sourceType, Class<?> targetType) {
		return String.class.isAssignableFrom(sourceType) && Boolean.class.isAssignableFrom(targetType);
	}

}
