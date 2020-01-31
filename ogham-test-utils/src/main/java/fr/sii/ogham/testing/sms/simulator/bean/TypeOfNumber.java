package fr.sii.ogham.testing.sms.simulator.bean;

/**
 * Type of Number based on SMPP version 3.4.
 * 
 * @author Aurélien Baudet
 *
 */
public enum TypeOfNumber {
	UNKNOWN((byte) 0x00), 
	INTERNATIONAL((byte) 0x01), 
	NATIONAL((byte) 0x02), 
	NETWORK_SPECIFIC((byte) 0x03), 
	SUBSCRIBER_NUMBER((byte) 0x04), 
	ALPHANUMERIC((byte) 0x05), 
	ABBREVIATED((byte) 0x06);

	private final byte value;

	TypeOfNumber(byte value) {
		this.value = value;
	}

	/**
	 * Get the byte value of the enum constant.
	 * 
	 * @return the byte value.
	 */
	public byte value() {
		return value;
	}

	/**
	 * Get the {@link TypeOfNumber} based on the specified byte value
	 * representation.
	 * 
	 * @param value
	 *            is the byte value representation.
	 * @return is the enum const related to the specified byte value.
	 * @throws IllegalArgumentException
	 *             if there is no enum const associated with specified byte
	 *             value.
	 */
	public static TypeOfNumber valueOf(byte value) {
		for (TypeOfNumber val : values()) {
			if (val.value == value) {
				return val;
			}
		}
		throw new IllegalArgumentException("No enum const TypeOfNumber with value " + value);
	}
}