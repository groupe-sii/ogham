package fr.sii.ogham.sms;

public final class SmsConstants {
	/**
	 * Default constant values for splitting messages.
	 * 
	 * 
	 * @author Aurélien Baudet
	 *
	 */
	public static final class SmppSplitConstants {
		/**
		 * A single SMS GSM message has a maximum size of 140 octets.
		 */
		public static final int MAXIMUM_BYTES_PER_MESSAGE = 140;

		/**
		 * Smpp message size is 140 octets. UCS-2 encoding uses 2 octets per
		 * character. Therefore the original Java {@link String} can completely
		 * fit in one segment if its length is lower or equal to 70 characters
		 * (140 octets / 2). If the original Java {@link String} can't fit, then
		 * each part must have a maximum length of 67 characters ((140 octetes -
		 * 6 octets for header) / 2).
		 * 
		 * <hr>
		 * <strong>Explanation:</strong>
		 * <p>
		 * UCS-2 Encoding This encoding allows use of a greater range of
		 * characters and languages. UCS-2 can represent the most commonly used
		 * Latin and eastern characters at the cost of a greater space expense.
		 * Strictly speaking, UCS-2 is limited to characters in the Basic
		 * Multilingual Plane. However, since modern programming environments do
		 * not provide encoders or decoders for UCS-2, some cell phones (e.g.
		 * iPhones) use UTF-16 instead of UCS-2.[4] This works, because for
		 * characters in the Basic Multilingual Plane (including full alphabets
		 * of most modern human languages) UCS-2 and UTF-16 encodings are
		 * identical. To encode characters outside of the BMP (unreachable in
		 * plain UCS-2), such as Emoji, UTF-16 uses surrogate pairs, which when
		 * decoded with UCS-2 would appear as two valid but unmapped code
		 * points.
		 * 
		 * <p>
		 * A single SMS GSM message using this encoding can have at most 70
		 * characters (140 octets).
		 * 
		 * <p>
		 * Note that on many GSM cell phones, there's no specific preselection
		 * of the UCS-2 encoding. The default is to use the 7-bit encoding
		 * described above, until one enters a character that is not present in
		 * the GSM 7-bit table (for example the lowercase 'a' with acute: 'á').
		 * In that case, the whole message gets reencoded using the UCS-2
		 * encoding, and the maximum length of the message sent in a single SMS
		 * is immediately reduced to 70 characters, instead of 160. Others vary
		 * based on the choice and configuration of SMS application, and the
		 * length of the message.
		 * 
		 */
		public static final SegmentSizes SEGMENT_SIZE_UCS2 = new SegmentSizes(70, 67);

		/**
		 * Smpp message size is 140 octets. GSM 8-bit encoding uses one octet
		 * per character. Therefore the original Java {@link String} can
		 * completely fit in one segment if its length is lower or equal to 140
		 * characters (140 octets = 140 characters). If the original Java
		 * {@link String} can't fit, then each part must have a maximum length
		 * of 134 characters (140 octets - 6 octets for header = 134 octets for
		 * message {@literal ->} 134 characters).
		 */
		public static final SegmentSizes SEGMENT_SIZE_GSM_8BIT = new SegmentSizes(140, 134);

		/**
		 * Smpp message size is 140 octets. GSM 7-bit encoding uses 7 bits per
		 * character. Therefore the original Java {@link String} can completely
		 * fit in one segment if its length is lower or equal to 160 characters
		 * (140 octets * 8 bits / 7 bits). If the original Java {@link String}
		 * can't fit, then each part must have a maximum length of 153
		 * characters ((140 octets - 6 octets for header) * 8 / 7).
		 * 
		 * <hr>
		 * <strong>Explanation:</strong>
		 * <p>
		 * The standard encoding for GSM messages is the 7-bit default alphabet
		 * as defined in the 23.038 recommendation.
		 * 
		 * <p>
		 * Seven-bit characters must be encoded into octets following one of
		 * three packing modes:
		 * 
		 * <ul>
		 * <li>CBS: using this encoding, it is possible to send up to 93
		 * characters (packed in up to 82 octets) in one SMS message in a Cell
		 * Broadcast Service.</li>
		 * <li><strong>SMS</strong>: using this encoding, it is possible to send
		 * up to 160 characters (packed in up to 140 octets) in one SMS message
		 * in the GSM network.</li>
		 * <li>USSD: using this encoding, it is possible to send up to 182
		 * characters (packed in up to 160 octets) in one SMS message of
		 * Unstructured Supplementary Service Data.</li>
		 * </ul>
		 * 
		 * <p>
		 * In a standard GSM text message, all characters are encoded using
		 * 7-bit code units, packed together to fill all bits of octets. So, for
		 * example, the 140-octet envelope of an SMS, with no other language
		 * indicator but only the standard class prefix, can transport up to
		 * (140*8)/7=160, that is 160 GSM 7-bit characters (but note that the
		 * ESC code counts for one of them, if characters in the high part of
		 * the table are used).
		 * 
		 * <p>
		 * Longer messages may be sent, but will require a continuation prefix
		 * and a sequence number on subsequent SMS messages (these prefix octets
		 * and sequence number are counted within the maximum length of the
		 * 140-octet payload of the envelope format).
		 * 
		 * <p>
		 * When there are 1 to 6 spare bits in the last octet of a message,
		 * these bits are set to zero (these bits do not count as a character
		 * but only as a filler). When there are 7 spare bits in the last octet
		 * of a message, these bits are set to the 7-bit code of the CR control
		 * (also used as a padding filler) instead of being set to zero (where
		 * they would be confused with the 7-bit code of an '@' character).
		 */
		public static final SegmentSizes SEGMENT_SIZE_GSM_7BIT_SMS_PACKING_MODE = new SegmentSizes(160, 153);

		/**
		 * Represents maximum sizes for segments:
		 * <ul>
		 * <li>maximumStringLengthToFitInASingleSegment: This is the maximum
		 * length of the original string (Java {@link String}) that can
		 * completely fit in only one segment.</li>
		 * <li>maximumStringLengthPerSegment: If the original string (Java
		 * {@link String}) can't completely fit in only one segment, then the
		 * string is split in several segments with this value as maximum size.
		 * Each resulting segment may contain a header or some control
		 * characters. This size only represents the maximum length of the
		 * payload.</li>
		 * </ul>
		 * 
		 * @author Aurélien Baudet
		 *
		 */
		public static class SegmentSizes {
			/**
			 * This is the maximum length of the original string (Java
			 * {@link String}) that can completely fit in only one segment.
			 */
			private final int maximumStringLengthToFitInASingleSegment;
			/**
			 * If the original string (Java {@link String}) can't completely fit
			 * in only one segment, then the string is split in several segments
			 * with this value as maximum size. Each resulting segment may
			 * contain a header or some control characters. This size only
			 * represents the maximum length of the payload.
			 */
			private final int maximumStringLengthPerSegment;

			/**
			 * Initializes with maximum segment sizes
			 * 
			 * @param maximumStringLengthToFitInASingleSegment
			 *            This is the maximum length of the original string
			 *            (Java {@link String}) that can completely fit in only
			 *            one segment.
			 * @param maximumStringLengthPerSegment
			 *            If the original string (Java {@link String}) can't
			 *            completely fit in only one segment, then the string is
			 *            split in several segments with this value as maximum
			 *            size. Each resulting segment may contain a header or
			 *            some control characters. This size only represents the
			 *            maximum length of the payload.
			 */
			public SegmentSizes(int maximumStringLengthToFitInASingleSegment, int maximumStringLengthPerSegment) {
				super();
				this.maximumStringLengthToFitInASingleSegment = maximumStringLengthToFitInASingleSegment;
				this.maximumStringLengthPerSegment = maximumStringLengthPerSegment;
			}

			/**
			 * Get the maximum string length that can fit in only one segment.
			 * 
			 * @return the maximum length of the Java {@link String} that can
			 *         fit in only one segment
			 */
			public int getMaximumStringLengthToFitInASingleSegment() {
				return maximumStringLengthToFitInASingleSegment;
			}

			/**
			 * Get the maximum string length for each segment.
			 * 
			 * If the original string (Java {@link String}) can't completely fit
			 * in only one segment, then the string is split in several segments
			 * with this value as maximum size. Each resulting segment may
			 * contain a header or some control characters. This size only
			 * represents the maximum length of the payload.
			 * 
			 * @return the maximum string length of Java {@link String} for each
			 *         segment
			 */
			public int getMaximumStringLengthPerSegment() {
				return maximumStringLengthPerSegment;
			}
		}

		private SmppSplitConstants() {
			super();
		}
	}

	private SmsConstants() {
		super();
	}
}
