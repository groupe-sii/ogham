package fr.sii.ogham.sms.sender.impl.ovh;

import java.util.regex.Pattern;

import fr.sii.ogham.core.convert.StringToEnumConverter.FactoryMethod;

@FactoryMethod(name="from")
public enum SmsCoding {
	/**
	 * 7bit encoding
	 */
	GSM7(1),
	/**
	 * 16bit encoding
	 */
	UNICODE(2);

	private static final Pattern IS_NUMBER = Pattern.compile("1|2");
	private final int value;

	SmsCoding(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	/**
	 * Returns {@link SmsCoding} either from constant name ("GSM7" or "UNICODE")
	 * or from value ("1" or "2").
	 * 
	 * @param nameOrValue
	 *            the name or value as string
	 * @return the corresponding {@link SmsCoding}
	 */
	public static SmsCoding from(String nameOrValue) {
		if (IS_NUMBER.matcher(nameOrValue).matches()) {
			return from(Integer.parseInt(nameOrValue));
		}
		return valueOf(nameOrValue);
	}

	/**
	 * Returns {@link SmsCoding} according to the value:
	 * <ul>
	 * <li>1: GSM7</li>
	 * <li>2: UNICODE</li>
	 * </ul>
	 * 
	 * @param value
	 *            the value
	 * @return the corresponding {@link SmsCoding}
	 */
	public static SmsCoding from(int value) {
		for (SmsCoding coding : values()) {
			if (coding.getValue() == value) {
				return coding;
			}
		}
		throw new IllegalArgumentException("Invalid SmsCoding value: " + value);
	}
}