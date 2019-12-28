package fr.sii.ogham.sms.sender.impl.ovh;

/**
 * See <a href=
 * "https://docs.ovh.com/fr/sms/envoyer_des_sms_depuis_une_url_-_http2sms/#annexe_2">https://docs.ovh.com/fr/sms/envoyer_des_sms_depuis_une_url_-_http2sms/#annexe_2</a>
 * 
 * @author Aurélien Baudet
 *
 */
public class DefaultSmsCodingDetector implements SmsCodingDetector {
	/**
	 * Basic characters
	 */
	private static final char[] CHAR_TABLE = {
		// @formatter:off
		'@', '\u00a3', '$', '\u00a5', '\u00e8', '\u00e9', '\u00f9', '\u00ec',
		'\u00f2', '\u00c7', '\n', '\u00d8', '\u00f8', '\r', '\u00c5', '\u00e5',
		'\u0394', '_', '\u03a6', '\u0393', '\u039b', '\u03a9', '\u03a0', '\u03a8',
		'\u03a3', '\u0398', '\u039e', ' ', '\u00c6', '\u00e6', '\u00df', '\u00c9',  // 0x1B is actually an escape which we'll encode to a space char
		' ', '!', '"', '#', '\u00a4', '%', '&', '\'',
		'(', ')', '*', '+', ',', '-', '.', '/',
		'0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', ':', ';', '<', '=', '>', '?',
		'\u00a1', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
		'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
		'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
		'X', 'Y', 'Z', '\u00c4', '\u00d6', '\u00d1', '\u00dc', '\u00a7',
		'\u00bf', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
		'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
		'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
		'x', 'y', 'z', '\u00e4', '\u00f6', '\u00f1', '\u00fc', '\u00e0',
		// @formatter:on
	};

	/**
	 * Extended character table.
	 */
	private static final char[] EXT_CHAR_TABLE = {
		// @formatter:off
		'\u20ac' /* € */, '\f', '[', '\\', ']',
		'^', '{', '|', '}', '~', 
		 // @formatter:on
	};

	@Override
	public SmsCoding detect(String message) {
		return canUseGsm7(message) ? SmsCoding.GSM7 : SmsCoding.UNICODE;
	}

	private static boolean canUseGsm7(String message) {
		for (int i=0 ; i<message.length() ; i++) {
			char c = message.charAt(i);
			if (!isInCharTable(c) && !isInExtensionCharTable(c)) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean isInCharTable(char c) {
		for (int i=0 ; i<CHAR_TABLE.length ; i++) {
			if (c == CHAR_TABLE[i]) {
				return true;
			}
		}
		return false;
	}

	private static boolean isInExtensionCharTable(char c) {
		for (int i=0 ; i<EXT_CHAR_TABLE.length ; i++) {
			if (c == EXT_CHAR_TABLE[i]) {
				return true;
			}
		}
		return false;
	}

}
