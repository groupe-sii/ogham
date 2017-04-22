package fr.sii.ogham.helper.sms;

import java.util.Arrays;

import org.jsmpp.bean.SubmitSm;

public class SmsUtils {
	private static final int UDHI_SIZE = 6;

	/**
	 * Get the text content of the SMS.
	 * 
	 * @param actual
	 *            the SMS
	 * @return the SMS content
	 */
	public static String getSmsContent(SubmitSm actual) {
		byte[] shortMessage = actual.getShortMessage();
		if (actual.isUdhi()) {
			shortMessage = Arrays.copyOfRange(shortMessage, UDHI_SIZE, shortMessage.length);
		}
		return new String(shortMessage);
	}

	private SmsUtils() {
		super();
	}
}
