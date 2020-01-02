package fr.sii.ogham.testing.helper.sms.bean;

/**
 * This is enum of the alphabet type.
 *
 * Alphabet represents the lower 4 bits of the data_coding field in the PDU, as
 * specified in s5.2.19 of the SMPP v3.4 specification.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public enum Alphabet {
	/**
	 * SMSC alphabet default
	 */
	ALPHA_DEFAULT((byte) 0x00),

	/**
	 * IA5 (CCITT T.50)/ASCII (ANSI X3.4)
	 */
	ALPHA_IA5((byte) 0x01),

	/**
	 * 8-bit binary octet unspecified coding.
	 */
	ALPHA_UNSPECIFIED_2((byte) 0x02),

	/**
	 * Latin 1 (ISO-8859-1)
	 */
	ALPHA_LATIN1((byte) 0x03),

	/**
	 * 8-bit binary octet unspecified coding.
	 */
	ALPHA_8_BIT((byte) 0x04),

	/**
	 * JIS (X 0208-1990)
	 */
	ALPHA_JIS((byte) 0x05),

	/**
	 * Cyrllic (ISO-8859-5)
	 */
	ALPHA_CYRILLIC((byte) 0x06),

	/**
	 * Latin/Hebrew (ISO-8859-8)
	 */
	ALPHA_LATIN_HEBREW((byte) 0x07),

	/**
	 * UCS2 alphabet coding (16-bit)
	 */
	ALPHA_UCS2((byte) 0x08),

	/**
	 * Pictogram Encoding
	 */
	ALPHA_PICTOGRAM_ENCODING((byte) 0x09),

	/**
	 * ISO-2022-JP (Music Codes)
	 */
	ALPHA_ISO_2022_JP_MUSIC_CODES((byte) 0x0a),

	/**
	 * Unused.
	 */
	ALPHA_RESERVED_11((byte) 0x0b),

	/**
	 * Unused.
	 */
	ALPHA_RESERVED_12((byte) 0x0c),

	/**
	 * Extended Kanji JIS(X 0212-1990)
	 */
	ALPHA_JIS_X_0212_1990((byte) 0x0d),

	/**
	 * KS C 5601 (now known as KS X 1001 but referred to by the old name in the
	 * SMPP v3.4 spec)
	 */
	ALPHA_KS_C_5601((byte) 0x0e),

	/**
	 * Unused.
	 */
	ALPHA_RESERVED_15((byte) 0x0f);

	private final byte value;

	Alphabet(byte alphabetValue) {
		this.value = alphabetValue;
	}

	/**
	 * Initialize the Alphabet from the byte value
	 * 
	 * @param alphabetValue
	 *            the alphabet value
	 * @return the Alphabet enum
	 */
	public static Alphabet from(byte alphabetValue) {
		for (Alphabet alphabet : values()) {
			if (alphabet.value() == alphabetValue) {
				return alphabet;
			}
		}
		throw new IllegalArgumentException("No enum const Alphabet with value " + alphabetValue);
	}

	/**
	 * The alphabet value
	 * 
	 * @return the alphabet value
	 */
	public byte value() {
		return value;
	}
}
