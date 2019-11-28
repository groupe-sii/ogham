package fr.sii.ogham.helper.sms.bean;

/**
 * This enum is defined Numbering Plan Indicator.
 * 
 * @author Aurélien Baudet
 */
public enum NumberingPlanIndicator {
	UNKNOWN((byte) 0x00), 
	ISDN((byte) 0x01), 
	DATA((byte) 0x03), 
	TELEX((byte) 0x04), 
	LAND_MOBILE((byte) 0x06), 
	NATIONAL((byte) 0x08), 
	PRIVATE((byte) 0x09), 
	ERMES((byte) 0x0a), 
	INTERNET((byte) 0x0e), 
	WAP((byte) 0x12);
	
	private final byte value;

	NumberingPlanIndicator(byte value) {
		this.value = value;
	}

	/**
	 * Return the value of NPI.
	 * 
	 * @return the value of NPI.
	 */
	public byte value() {
		return value;
	}

	/**
	 * Get the associated {@link NumberingPlanIndicator} by it's value.
	 * 
	 * @param value
	 *            is the value.
	 * @return the associated enum const for specified value.
	 * @throws IllegalArgumentException
	 *             if there is no enum const associated with specified
	 *             {@code value}.
	 */
	public static NumberingPlanIndicator valueOf(byte value) {
		for (NumberingPlanIndicator val : values()) {
			if (val.value == value) {
				return val;
			}
		}
		throw new IllegalArgumentException("No enum const NumberingPlanIndicator with value " + value);
	}
}