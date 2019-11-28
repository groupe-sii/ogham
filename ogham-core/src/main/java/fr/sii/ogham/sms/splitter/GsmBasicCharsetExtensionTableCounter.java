package fr.sii.ogham.sms.splitter;

/**
 * Every character present in base character table count as 1 octet.
 * 
 * Every character present in extended character table count as 2 octets (escape
 * character followed by extension character).
 * 
 * @author Aurélien Baudet
 */
public class GsmBasicCharsetExtensionTableCounter implements LengthCounter {
	private static final int MAXIMUM_SPLIT_SEGMENT_LENGTH = 153;
	private static final char[] EXT_CHARS = { '\f', '^', '{', '}', '\\', '[', '~', ']', '|', '\u20ac' };

	/**
	 * If extended characters are found in the string, each character must be
	 * count as 2 characters. As they are already present in the original
	 * string, add the number of extended characters to original string length.
	 */
	@Override
	public int count(String str) {
		int originalLength = str.length();
		int extendedChars = countExtendedChars(str);
		// There is a edge case where the whole message can't fit in a single
		// segment, contains extended characters only and the segments are full.
		//
		// As the message can't fit in one segment so it must be
		// split in segments of 153 characters.
		// So it could hypothetically fit in segments of 153 characters.
		// But it can't because the extended character can't be cut in the
		// middle.
		// We need to add 1 to message length to generate an additional segment
		// TODO: improve this
		if (originalLength == extendedChars && extendedChars % MAXIMUM_SPLIT_SEGMENT_LENGTH == 0) {
			return originalLength + extendedChars + 1;
		}
		return originalLength + extendedChars;
	}

	private static int countExtendedChars(String str) {
		int found = 0;
		int len = str.length();
		for (int i = 0; i < len; i++) {
			int search = 0;
			char c = str.charAt(i);
			for (; search < EXT_CHARS.length; search++) {
				if (c == EXT_CHARS[search]) {
					found++;
					break;
				}
			}
		}
		return found;
	}

}
