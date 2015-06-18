package fr.sii.notification.sms.message.addressing;

/**
 * NPI as described in GSM 03.40.
 * 
 * @author cdejonghe
 * 
 */
public enum NumberingPlanIndicator {
	/** Unknown = 0. */
	UNKNOWN((byte) 0),
	/** ISDN/telephone numbering plan (E163/E164) = 1. */
	ISDN_TELEPHONE((byte) 1),
	/** Data numbering plan (X.121) = 3. */
	DATA((byte) 3),
	/** Telex numbering plan (F.69) = 4. */
	TELEX((byte) 4),
	/** Land Mobile (E.212) =6. */
	LAND_MOBILE((byte) 6),
	/** National numbering plan = 8. */
	NATIONAL((byte) 8),
	/** Private numbering plan = 9. */
	PRIVATE((byte) 9),
	/** ERMES numbering plan (ETSI DE/PS 3 01-3) = 10. */
	ERMES((byte) 10),
	/** Internet (IP) = 13. */
	IP((byte) 13),
	/** WAP Client Id (to be defined by WAP Forum) = 18. */
	WAP((byte) 13);

	private byte value;

	private NumberingPlanIndicator(byte value) {
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
	 * Get the <tt>NumberingPlanIdentification</tt> based on the specified byte
	 * value representation.
	 * 
	 * @param value
	 *            is the int / binary value representation.
	 * @return is the enum const related to the specified int value.
	 * @throws IllegalArgumentException
	 *             if there is no enum const associated with specified int
	 *             value.
	 */
	public static NumberingPlanIndicator valueOf(byte value) {
		for (NumberingPlanIndicator current : values()) {
			if (current.value == value) {
				return current;
			}
		}
		throw new IllegalArgumentException("Invalid value for NumberingPlanIdentification : " + value);
	}
}
