package fr.sii.ogham.sms.splitter;

import static fr.sii.ogham.sms.SmsConstants.SmppSplitConstants.MAXIMUM_BYTES_PER_MESSAGE;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.sms.SmsConstants.SmppSplitConstants.SegmentSizes;
import fr.sii.ogham.sms.encoder.EncodedWithHeader;
import fr.sii.ogham.sms.encoder.Encoder;
import fr.sii.ogham.sms.exception.message.EncodingException;
import fr.sii.ogham.sms.exception.message.InvalidReferenceNumberException;
import fr.sii.ogham.sms.exception.message.ReferenceNumberGenerationException;
import fr.sii.ogham.sms.exception.message.SplitMessageException;

/**
 * Split the message in segments if needed.
 * 
 * <p>
 * If the size of the unencoded Java {@link String} is less than the provided
 * maximum size then no split is done. The result is a list of only one segment
 * with the encoded message as byte array.
 * 
 * <p>
 * If the size of the unencoded Java {@link String} is greater than the provided
 * maximum size then the message is split. Encoded message byte array is cut to
 * fit the provided maximum segment size. The result is a list of segments that
 * contains the required headers and the partial byte array.
 * 
 * <p>
 * If the message is split, each segment contains a header and a payload. The
 * header follows <a href="https://en.wikipedia.org/wiki/User_Data_Header">User
 * Data Header</a> specification.
 * 
 * <p>
 * The specification also allows to use extended table. Even if the encoding
 * uses only one octet (GSM 7-bit encoding and GSM 8-bit encoding), the
 * characters that are present in the extended table must allocate two octets (1
 * for ESC character followed with extended character). That's why this splitter
 * may need to use a {@link LengthCounter}. If such a character is present then
 * the number of characters that could fit in a segment is decreased by one.
 * 
 * <p>
 * Each segment contains a reference number to identify
 * <a href="https://en.wikipedia.org/wiki/Concatenated_SMS">concatenated
 * messages</a>. The reference number can be encoded on one or two octets (see
 * {@link ReferenceNumberGenerator}). This algorithm supports both reference
 * numbers encoded on one or two octets.
 * 
 * 
 * 
 * <h1>Explanation</h1>
 * 
 * <h2>One-octet encoding</h2>
 * <p>
 * If every character of the original string is encoded on one octet and the
 * maximum size for segments is 12 octets. Then the maximum unencoded characters
 * that can fit in a single segment is also 12.
 * 
 * <pre>
 * {@code 
 * String originalMessage = "Hello World!"
 * // Not really encoded, just to explain. 
 * // Use back-tick to indicate that it is the octet value of the character
 * byte[] encoded = [`H`, `e`, `l`, `l`, `o`, ` ` , `W`, `o`, `r`, `l`, `d`, `!`]
 * // The message can fit entirely in a single segment
 * ┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┐
 * │`H`│`e`│`l`│`l`│`o`│` `│`W`│`o`│`r`│`l`│`d`│`!`│
 * └───┴───┴───┴───┴───┴───┴───┴───┴───┴───┴───┴───┘
 * 
 * String originalMessage = "Hello World !!"
 * // Not really encoded, just to explain. 
 * // Use back-tick to indicate that it is the octet value of the character
 * byte[] encoded = [`H`, `e`, `l`, `l`, `o`, ` ` , `W`, `o`, `r`, `l`, `d`, ` `, `!`, `!`]
 * // The message can't fit entirely in a single segment so it must be split
 * // Header (6 octets) is added on each segment
 * // So message is split in 3 segments like this
 * ┌───┬───┬───┬───┬───┬───╥───┬───┬───┬───┬───┬───┐
 * │#05│#00│#03│#??│ 3 │ 1 ║`H`│`e`│`l`│`l`│`o`│` `│
 * └───┴───┴───┴───┴───┴───╨───┴───┴───┴───┴───┴───┘
 * ┌───┬───┬───┬───┬───┬───╥───┬───┬───┬───┬───┬───┐
 * │#05│#00│#03│#??│ 3 │ 2 ║`W`│`o`│`r`│`l`│`d`│` `│
 * └───┴───┴───┴───┴───┴───╨───┴───┴───┴───┴───┴───┘
 * ┌───┬───┬───┬───┬───┬───╥───┬───┬───┬───┬───┬───┐
 * │#05│#00│#03│#??│ 3 │ 3 ║`!`│`!`│   │   │   │   │
 * └───┴───┴───┴───┴───┴───╨───┴───┴───┴───┴───┴───┘
 *   │   │   │   │   │   │ 
 *   │   │   │   │   │ This segment's number
 *   │   │   │   │   │ in the sequence
 *   │   │   │   │   │ 
 *   │   │   │   │ Total number of 
 *   │   │   │   │ segments
 *   │   │   │   │ 
 *   │   │   │ CSMS reference number
 *   │   │   │ Generated by
 *   │   │   │ ReferenceNumberGenerator
 *   │   │   │ 
 *   │   │  Length of the header,
 *   │   │  excluding the first two
 *   │   │  fields
 *   │   │
 *   │  Information Element Identifier
 *   │
 *  Length of User Data Header
 * }
 * </pre>
 * 
 * 
 * <h2>Two-octet encoding</h2>
 * <p>
 * If every character of the original string is encoded on two octets and the
 * maximum size for segments is 12 octets. Then the maximum unencoded characters
 * that can fit in a single segment is 6 (12 / 2).
 * 
 * <pre>
 * {@code 
 * String originalMessage = "Hello!"
 * // Each character is encoded on two octets
 * byte[] encoded = [0, 72, 0, 101, 0, 108, 0, 108, 0, 111, 0, 33]
 * // The message can fit entirely in a single segment
 * ┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┐
 * │ 0 │72 │ 0 │101│ 0 │108│ 0 │108│ 0 │111│ 0 │33 │
 * └───┴───┴───┴───┴───┴───┴───┴───┴───┴───┴───┴───┘
 * 
 * String originalMessage = "Hello !!"
 * // Each character is encoded on two octets
 * byte[] encoded = [0, 72, 0, 101, 0, 108, 0, 108, 0, 111, 0, 32, 0, 33, 0, 33]
 * // The message can't fit entirely in a single segment so it must be split
 * // Header (6 octets) is added on each segment
 * // So message is split in 3 segments like this
 * ┌───┬───┬───┬───┬───┬───╥───┬───┬───┬───┬───┬───┐
 * │#05│#00│#03│#??│ 3 │ 1 ║ 0 │72 │ 0 │101│ 0 │108│
 * └───┴───┴───┴───┴───┴───╨───┴───┴───┴───┴───┴───┘
 * ┌───┬───┬───┬───┬───┬───╥───┬───┬───┬───┬───┬───┐
 * │#05│#00│#03│#??│ 3 │ 2 ║ 0 │108│ 0 │111│ 0 │32 │
 * └───┴───┴───┴───┴───┴───╨───┴───┴───┴───┴───┴───┘
 * ┌───┬───┬───┬───┬───┬───╥───┬───┬───┬───┬───┬───┐
 * │#05│#00│#03│#??│ 3 │ 3 ║ 0 │33 │ 0 │33 │   │   │
 * └───┴───┴───┴───┴───┴───╨───┴───┴───┴───┴───┴───┘
 *   │   │   │   │   │   │ 
 *   │   │   │   │   │ This segment's number
 *   │   │   │   │   │ in the sequence
 *   │   │   │   │   │ 
 *   │   │   │   │ Total number of 
 *   │   │   │   │ segments
 *   │   │   │   │ 
 *   │   │   │ CSMS reference number
 *   │   │   │ Generated by
 *   │   │   │ ReferenceNumberGenerator
 *   │   │   │ 
 *   │   │  Length of the header,
 *   │   │  excluding the first two
 *   │   │  fields
 *   │   │
 *   │  Information Element Identifier
 *   │
 *  Length of User Data Header
 * }
 * </pre>
 * 
 * <h2>7-bits encoding</h2>
 * <p>
 * If every character of the original string is encoded on 7 bits and the
 * maximum size for segments is 14 octets. Then the maximum unencoded characters
 * that can fit in a single segment is 16.
 * 
 * <pre>
 * {@code 
 * String originalMessage = "aaaaaaaaaaaaaaaa"
 * The message can fit entirely in a single segment
 * encoded on 7 bits 'a' is: 1100001
 * originalMessage encoded on 7 bits is:
 *   1100001 1100001 1100001 1100001 1100001 1100001 1100001 1100001 1100001 1100001 1100001 1100001 1100001 1100001 1100001 1100001
 * originalMessage packed on 8 bits is (/!\ This is not the real packing algorithm but it is simpler to understand):
 *   11000011 10000111 00001110 00011100 00111000 01110000 11100001 11000011 10000111 00001110 00011100 00111000 01110000 11100001
 *     #c3      #87      #0e      #1c      #38      #70      #e1       #c3      #87      #0e      #1c      #38      #70      #e1  
 * byte[] encoded = [#c3, #87, #0e, #1c, #38, #70, #e1, #c3, #87, #0e, #1c, #38, #70, #e1]
 * ┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┐
 * │#c3│#87│#0e│#1c│#38│#70│#e1│#c3│#87│#0e│#1c│#38│#70│#e1│
 * └───┴───┴───┴───┴───┴───┴───┴───┴───┴───┴───┴───┴───┴───┘
 * 
 * String originalMessage = "aaaaaaaaaaaaaaaabbbb"
 * The message can't fit entirely in a single segment so it must be split
 * Header (6 octets) is added on each segment 
 * so each payload can contain 9 characters: (14 octets - 6 header octets) * 8 bits / 7 bits per char
 * originalMessage must be split like this (before encoding):
 * ┌─────────┬─────────┬─────────┐
 * │aaaaaaaaa│aaaaaaabb│bb       │
 * └─────────┴─────────┴─────────┘
 *   part1     part2     part3
 *   
 * encoded on 7 bits 'a' is: 1100001
 * encoded on 7 bits 'b' is: 1100010
 * part1 encoded on 7 bits is:
 *   1100001 1100001 1100001 1100001 1100001 1100001 1100001 1100001 1100001
 * part2 encoded on 7 bits is:
 *   1100001 1100001 1100001 1100001 1100001 1100001 1100001 1100010 1100010
 * part3 encoded on 7 bits is:
 *   1100010 1100010
 * part1 packed on 8 bits is (/!\ This is not the real packing algorithm but it is simpler to understand):
 *   11000011 10000111 00001110 00011100 00111000 01110000 11100001 1100001
 *     #c3      #87      #0e      #1c      #38      #70      #e1       #c2   
 * part2 packed on 8 bits is (/!\ This is not the real packing algorithm but it is simpler to understand):
 *   11000011 10000111 00001110 00011100 00111000 01110000 11100010 1100010
 *     #c3      #87      #0e      #1c      #38      #70      #e2       #c4   
 * part3 packed on 8 bits is (/!\ This is not the real packing algorithm but it is simpler to understand):
 *   11000101 100010
 *     #c5      #88
 * 
 * So message is split in 3 segments like this
 * ┌───┬───┬───┬───┬───┬───╥───┬───┬───┬───┬───┬───┬───┬───┐
 * │#05│#00│#03│#??│ 3 │ 1 ║#c3│#87│#0e│#1c│#38│#70│#e1│#c2│
 * └───┴───┴───┴───┴───┴───╨───┴───┴───┴───┴───┴───┴───┴───┘
 * ┌───┬───┬───┬───┬───┬───╥───┬───┬───┬───┬───┬───┬───┬───┐
 * │#05│#00│#03│#??│ 3 │ 2 ║#c3│#87│#0e│#1c│#38│#70│#e2│#c4│
 * └───┴───┴───┴───┴───┴───╨───┴───┴───┴───┴───┴───┴───┴───┘
 * ┌───┬───┬───┬───┬───┬───╥───┬───┬───┬───┬───┬───┬───┬───┐
 * │#05│#00│#03│#??│ 3 │ 3 ║#c5│#88│   │   │   │   │   │   │
 * └───┴───┴───┴───┴───┴───╨───┴───┴───┴───┴───┴───┴───┴───┘
 *   │   │   │   │   │   │ 
 *   │   │   │   │   │ This segment's number
 *   │   │   │   │   │ in the sequence
 *   │   │   │   │   │ 
 *   │   │   │   │ Total number of 
 *   │   │   │   │ segments
 *   │   │   │   │ 
 *   │   │   │ CSMS reference number
 *   │   │   │ Generated by
 *   │   │   │ ReferenceNumberGenerator
 *   │   │   │ 
 *   │   │  Length of the header,
 *   │   │  excluding the first two
 *   │   │  fields
 *   │   │
 *   │  Information Element Identifier
 *   │
 *  Length of User Data Header
 * }
 * </pre>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class GsmMessageSplitter implements MessageSplitter {
	private static final int MAXIMUM_SEGMENTS = 255;
	
	private static final int USER_DATA_HEADER_SIZE_ONE_BYTE_REFERENCE_NUMBER = 6;
	private static final byte UDHIE_HEADER_LENGTH_ONE_BYTE_REFERENCE_NUMBER = 0x05;
	private static final byte UDHIE_IDENTIFIER_SAR_ONE_BYTE_REFERENCE_NUMBER = 0x00;
	private static final byte UDHIE_SAR_LENGTH_ONE_BYTE_REFERENCE_NUMBER = 0x03;

	private static final int USER_DATA_HEADER_SIZE_TWO_BYTES_REFERENCE_NUMBER = 7;
	private static final byte UDHIE_HEADER_LENGTH_TWO_BYTES_REFERENCE_NUMBER = 0x06;
	private static final byte UDHIE_IDENTIFIER_SAR_TWO_BYTES_REFERENCE_NUMBER = 0x08;
	private static final byte UDHIE_SAR_LENGTH_TWO_BYTES_REFERENCE_NUMBER = 0x04;

	private final Encoder encoder;
	private final SegmentSizes segmentSizes;
	private final ReferenceNumberGenerator referenceNumberGenerator;
	private final LengthCounter lengthCounter;

	/**
	 * The splitter uses the {@link Encoder} to encode each segment.
	 * 
	 * <p>
	 * The algorithm compares the length of the Java String (using
	 * {@link String#length()} with
	 * {@link SegmentSizes#getMaximumStringLengthToFitInASingleSegment()} to
	 * check that the whole string can fit in a single segment. If it can't then
	 * split is applied. A reference number is generated (using
	 * {@link RandomReferenceNumberGenerator}). The algorithm uses
	 * {@link SegmentSizes#getMaximumStringLengthPerSegment()} to compute the
	 * remaining of characters that can fit in a segment with a header. The size
	 * of the header depends on the size of the reference number. Once the
	 * string is split in a segment, it is also encoded using {@link Encoder}.
	 * 
	 * @param encoder
	 *            the encoder to encode message
	 * @param segmentSizes
	 *            the information about size that can fit in one segment
	 *            (depends on encoder)
	 */
	public GsmMessageSplitter(Encoder encoder, SegmentSizes segmentSizes) {
		this(encoder, segmentSizes, new RandomReferenceNumberGenerator());
	}

	/**
	 * The splitter uses the {@link Encoder} to encode each segment.
	 * 
	 * <p>
	 * The algorithm compares the length of the Java String (using
	 * {@link String#length()} with
	 * {@link SegmentSizes#getMaximumStringLengthToFitInASingleSegment()} to
	 * check that the whole string can fit in a single segment. If it can't then
	 * split is applied. A reference number is generated (using
	 * {@link ReferenceNumberGenerator}). The algorithm uses
	 * {@link SegmentSizes#getMaximumStringLengthPerSegment()} to compute the
	 * remaining of characters that can fit in a segment with a header. The size
	 * of the header depends on the size of the reference number. Once the
	 * string is split in a segment, it is also encoded using {@link Encoder}.
	 * 
	 * @param encoder
	 *            the encoder to encode message
	 * @param segmentSizes
	 *            the information about size that can fit in one segment
	 *            (depends on encoder)
	 * @param referenceNumberGenerator
	 *            generates reference numbers
	 */
	public GsmMessageSplitter(Encoder encoder, SegmentSizes segmentSizes, ReferenceNumberGenerator referenceNumberGenerator) {
		this(encoder, segmentSizes, referenceNumberGenerator, String::length);
	}

	/**
	 * The splitter uses the {@link Encoder} to encode each segment.
	 * 
	 * <p>
	 * The algorithm compares {@link LengthCounter#count(String)} with
	 * {@link SegmentSizes#getMaximumStringLengthToFitInASingleSegment()} to
	 * check that the whole string can fit in a single segment. If it can't then
	 * split is applied. A reference number is generated (using
	 * {@link ReferenceNumberGenerator}). The algorithm uses
	 * {@link SegmentSizes#getMaximumStringLengthPerSegment()} to compute the
	 * remaining of characters that can fit in a segment with a header. The size
	 * of the header depends on the size of the reference number. Once the
	 * string is split in a segment, it is also encoded using {@link Encoder}.
	 * 
	 * @param encoder
	 *            the encoder to encode message
	 * @param segmentSizes
	 *            the information about size that can fit in one segment
	 *            (depends on encoder)
	 * @param referenceNumberGenerator
	 *            generates reference numbers
	 * @param lengthCounter
	 *            used to count the number of characters in the string (some
	 *            characters may not have the same size, using extended
	 *            character tables for example)
	 */
	public GsmMessageSplitter(Encoder encoder, SegmentSizes segmentSizes, ReferenceNumberGenerator referenceNumberGenerator, LengthCounter lengthCounter) {
		super();
		this.encoder = encoder;
		this.segmentSizes = segmentSizes;
		this.referenceNumberGenerator = referenceNumberGenerator;
		this.lengthCounter = lengthCounter;
	}

	@Override
	public List<Segment> split(String message) throws SplitMessageException {
		int messageLength = lengthCounter.count(message);
		if (messageLength <= segmentSizes.getMaximumStringLengthToFitInASingleSegment()) {
			return asList(singleSegment(message));
		}

		// generate new reference number
		byte[] referenceNumber = generateReferenceNumber(message);
		int maximumStringLengthPerSegment = computeMaximumStringLengthPerSegment(referenceNumber);

		// split into several messages
		int numberOfSegments = (int) Math.ceil(messageLength / (double) maximumStringLengthPerSegment);
		if (numberOfSegments > MAXIMUM_SEGMENTS) {
			throw new SplitMessageException("Can't split the message because the number of segments is greater than 255", message);
		}

		// prepare list for all of the msg segments
		List<Segment> segments = new ArrayList<>(numberOfSegments);

		int start = 0;
		for (int i = 0; i < numberOfSegments; i++) {
			String part = cutToFitInSegment(start, message, maximumStringLengthPerSegment);
			segments.add(segmentWithHeader(message, part, numberOfSegments, i + 1, referenceNumber));
			start += part.length();
		}
		return segments;
	}

	private byte[] generateReferenceNumber(String message) throws SplitMessageException {
		try {
			byte[] referenceNumber = referenceNumberGenerator.generateReferenceNumber();
			if (referenceNumber == null || referenceNumber.length == 0) {
				throw new InvalidReferenceNumberException("Generated reference number byte array can't be null or empty", referenceNumber);
			}
			if (referenceNumber.length > 2) {
				throw new InvalidReferenceNumberException(GsmMessageSplitter.class.getSimpleName() + " only support one byte or two byte reference number length", referenceNumber);
			}
			return referenceNumber;
		} catch (ReferenceNumberGenerationException e) {
			throw new SplitMessageException("Failed to split message due to reference number generation failure", message, e);
		}
	}

	private String cutToFitInSegment(int start, String message, int maximumStringLengthPerSegment) {
		int end = start + maximumStringLengthPerSegment;
		String part = message.substring(start, Math.min(message.length(), end));
		int lengthOfPart = lengthCounter.count(part);
		while (lengthOfPart > maximumStringLengthPerSegment && end > start) {
			end--;
			part = message.substring(start, Math.min(message.length(), end));
			lengthOfPart = lengthCounter.count(part);
		}
		return part;
	}

	private Segment segmentWithHeader(String wholeMessage, String part, int numberOfSegments, int segmentNumber, byte[] referenceNumber) throws SplitMessageException {
		try {
			int headerSize = headerSize(referenceNumber);
			byte[] header = new byte[headerSize];

			if (referenceNumber.length == 1) {
				// Field 1 (1 octet): Length of User Data Header, in this case
				// 05.
				header[0] = UDHIE_HEADER_LENGTH_ONE_BYTE_REFERENCE_NUMBER;
				// Field 2 (1 octet): Information Element Identifier, equal to
				// 00 (Concatenated short messages, 8-bit reference number)
				header[1] = UDHIE_IDENTIFIER_SAR_ONE_BYTE_REFERENCE_NUMBER;
				// Field 3 (1 octet): Length of the header, excluding the first
				// two fields; equal to 03 for one byte reference number
				header[2] = UDHIE_SAR_LENGTH_ONE_BYTE_REFERENCE_NUMBER;
				// Field 4 (1 octet): 00-FF, CSMS reference number, must be same
				// for all the SMS parts in the CSMS.
				header[3] = referenceNumber[0];
				// Field 5 (1 octet): 00-FF, total number of parts. The value
				// shall remain constant for every short message which makes up
				// the concatenated short message. If the value is zero then the
				// receiving entity shall ignore the whole information element
				header[4] = (byte) numberOfSegments;
				// Field 6 (1 octet): 00-FF, this part's number in the sequence.
				// The value shall start at 1 and increment for every short
				// message which makes up the concatenated short message. If the
				// value is zero or greater than the value in Field 5 then the
				// receiving entity shall ignore the whole information element.
				// [ETSI Specification: GSM 03.40 Version 5.3.0: July 1996]
				header[5] = (byte) segmentNumber;
			} else {
				// Field 1 (1 octet): Length of User Data Header, in this case
				// 06.
				header[0] = UDHIE_HEADER_LENGTH_TWO_BYTES_REFERENCE_NUMBER;
				// Field 2 (1 octet): Information Element Identifier, equal to
				// 08 (Concatenated short messages, 16-bit reference number)
				header[1] = UDHIE_IDENTIFIER_SAR_TWO_BYTES_REFERENCE_NUMBER;
				// Field 3 (1 octet): Length of the header, excluding the first
				// two fields; equal to 04 for one byte reference number
				header[2] = UDHIE_SAR_LENGTH_TWO_BYTES_REFERENCE_NUMBER;
				// Field 4 (2 octets): 0000-FFFF, CSMS reference number, must be
				// same for all the SMS parts in the CSMS.
				header[3] = referenceNumber[0];
				header[4] = referenceNumber[1];
				// Field 6 (1 octet): 00-FF, total number of parts. The value
				// shall remain constant for every short message which makes up
				// the concatenated short message. If the value is zero then the
				// receiving entity shall ignore the whole information element
				header[5] = (byte) numberOfSegments;
				// Field 7 (1 octet): 00-FF, this part's number in the sequence.
				// The value shall start at 1 and increment for every short
				// message which makes up the concatenated short message. If the
				// value is zero or greater than the value in Field 5 then the
				// receiving entity shall ignore the whole information element.
				// [ETSI Specification: GSM 03.40 Version 5.3.0: July 1996]
				header[6] = (byte) segmentNumber;
			}

			return new EncodedSegment(new EncodedWithHeader(header, encoder.encode(part)));
		} catch (EncodingException e) {
			throw new SplitMessageException("Failed to generate segment for " + part + " (segment " + segmentNumber + "/" + numberOfSegments + ") due to encoding error", wholeMessage, e);
		}
	}

	private int computeMaximumStringLengthPerSegment(byte[] referenceNumber) {
		return (int) Math.floor((MAXIMUM_BYTES_PER_MESSAGE - headerSize(referenceNumber)) * segmentSizes.getMaximumStringLengthToFitInASingleSegment() / (double) MAXIMUM_BYTES_PER_MESSAGE);
	}

	private static int headerSize(byte[] referenceNumber) {
		return referenceNumber.length == 1 ? USER_DATA_HEADER_SIZE_ONE_BYTE_REFERENCE_NUMBER : USER_DATA_HEADER_SIZE_TWO_BYTES_REFERENCE_NUMBER;
	}

	private Segment singleSegment(String message) throws SplitMessageException {
		try {
			return new EncodedSegment(encoder.encode(message));
		} catch (EncodingException e) {
			throw new SplitMessageException("Failed to generate single segment for " + message + " due to encoding error", message, e);
		}
	}

}