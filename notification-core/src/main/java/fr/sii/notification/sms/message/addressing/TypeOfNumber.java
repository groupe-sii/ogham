package fr.sii.notification.sms.message.addressing;

/**
 * TON as described in GSM 03.40.
 * 
 * @author cdejonghe
 * 
 */
public enum TypeOfNumber {
	/** Unknown = 0. */
	UNKNOWN(0),
	/** International = 1. */
	INTERNATIONAL(1),
	/** National = 2. */
	NATIONAL(2),
	/** Network Specific = 3. */
	NETWORK_SPECIFIC(3),
	/** Subscriber Number = 4. */
	SUBSCRIBER(4),
	/** Alphanumeric = 5. */
	ALPHANUMERIC(5),
	/** Abbreviated = 6. */
	ABBREVIATED(6);
	
	private int value;
    
	private TypeOfNumber(int value) {
		this.value = value;
	}
	
	/**
     * Get the byte value of the enum constant.
     * 
	 * @return the byte value.
	 */
	public int value() {
		return value;
	}
	
	/**
     * Get the <tt>TypeOfNumber</tt> based on the specified byte value
     * representation.
     * 
     * @param value is the int / binary value representation.
     * @return is the enum const related to the specified int value.
     * @throws IllegalArgumentException if there is no enum const associated
     *         with specified int value.
     */
	public static TypeOfNumber valueOf(int value) {
		for (TypeOfNumber current : values()) {
			if (current.value == value)
				return current;
		}
		
		throw new IllegalArgumentException("Invalid value for TypeOfNumber : " + value);
	}
}

