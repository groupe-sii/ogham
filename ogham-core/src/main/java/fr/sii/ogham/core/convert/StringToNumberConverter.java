package fr.sii.ogham.core.convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import fr.sii.ogham.core.exception.convert.ConversionException;

/**
 * Converts a string to a number. It also handles {@link Byte}s. This class is
 * copied from Spring.
 * 
 * @author Aurélien Baudet
 *
 */
public class StringToNumberConverter implements SupportingConverter {

	@Override
	public <T> T convert(Object source, Class<T> targetType) {
		String text = (String) source;
		String trimmed = trimAllWhitespace(text);
		if(trimmed==null) {
			return null;
		}
		try {
			T value = convertNumber(trimmed, targetType);
			if(value==null) {
				throw new ConversionException("Cannot convert String [" + text + "] to target class [" + targetType.getName() + "]");
			}
			return value;
		} catch(NumberFormatException e) {
			throw new ConversionException("Failed to convert "+source+" to "+targetType.getSimpleName(), e);
		}
	}

	@Override
	public boolean supports(Class<?> sourceType, Class<?> targetType) {
		return String.class.isAssignableFrom(sourceType) && Number.class.isAssignableFrom(targetType);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T convertNumber(String trimmed, Class<T> targetType) {	// NOSONAR: code from Spring
		if (Byte.class == targetType) {
			return (T) (isHexOrOctalNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed));
		}
		if (Short.class == targetType) {
			return (T) (isHexOrOctalNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed));
		}
		if (Integer.class == targetType) {
			return (T) (isHexOrOctalNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed));
		}
		if (Long.class == targetType) {
			return (T) (isHexOrOctalNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed));
		}
		if (BigInteger.class == targetType) {
			return (T) (isHexOrOctalNumber(trimmed) ? decodeBigInteger(trimmed) : new BigInteger(trimmed));
		}
		if (Float.class == targetType) {
			return (T) Float.valueOf(trimmed);
		}
		if (Double.class == targetType) {
			return (T) Double.valueOf(trimmed);
		}
		if (BigDecimal.class == targetType || Number.class == targetType) {
			return (T) new BigDecimal(trimmed);
		}
		return null;
	}

	private static boolean isHexOrOctalNumber(String value) {
		int index = value.startsWith("-") ? 1 : 0;
		return value.startsWith("0x", index) 
				|| value.startsWith("0X", index) 
				|| value.startsWith("#", index) 
				|| value.startsWith("0", index);
	}

	private static String trimAllWhitespace(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		int len = str.length();
		StringBuilder sb = new StringBuilder(str.length());
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			if (!Character.isWhitespace(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private static BigInteger decodeBigInteger(String value) {
		int radix = 10;
		int index = 0;
		boolean negative = false;

		// Handle minus sign, if present.
		if (value.startsWith("-")) {
			negative = true;
			index++;
		}

		// Handle radix specifier, if present.
		if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
			index += 2;
			radix = 16;
		} else if (value.startsWith("#", index)) {
			index++;
			radix = 16;
		} else if (value.startsWith("0", index) && value.length() > 1 + index) {
			index++;
			radix = 8;
		}

		BigInteger result = new BigInteger(value.substring(index), radix);
		return (negative ? result.negate() : result);
	}
}
