package fr.sii.ogham.testing.sms.simulator.decode;

import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm;

/**
 * Some utility functions for SMS.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public final class SmsUtils {

	/**
	 * Get the text content of the SMS. The alphabet/encoding is determined
	 * directly from the SMS bytes.
	 * 
	 * @param actual
	 *            the SMS
	 * @return the SMS content
	 */
	public static String getSmsContent(SubmitSm actual) {
		return MessageDecoder.decode(actual);
	}

	/**
	 * Get the text content of the SMS using a particular alphabet/encoding.
	 * 
	 * @param actual
	 *            the SMS
	 * @param charset
	 *            the charset used to decode the SMS message
	 * @return the SMS content
	 */
	public static String getSmsContent(SubmitSm actual, Charset charset) {
		return MessageDecoder.decode(actual, charset);
	}

	private SmsUtils() {
		super();
	}
}
