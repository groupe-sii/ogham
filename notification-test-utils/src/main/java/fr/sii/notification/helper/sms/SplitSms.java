package fr.sii.notification.helper.sms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a SMS that has been split into several parts in order to be sent.
 * 
 * @author Aurélien Baudet
 *
 */
public class SplitSms {
	/**
	 * Parts of the message when the message is too long and has to be split
	 */
	private List<ExpectedSms> parts;

	/**
	 * Initialize with the provided parts. Each part MUST contain:
	 * <ul>
	 * <li>The expected phone number of the sender</li>
	 * <li>The expected phone number of the receiver</li>
	 * <li>The expected message part</li>
	 * </ul>
	 * <p>
	 * The receiver and sender numbers have to be duplicated. See
	 * {@link #SplitSms(String, String, String...)} for reusing the receiver and
	 * sender numbers.
	 * </p>
	 * 
	 * @param parts
	 *            the parts
	 */
	public SplitSms(List<ExpectedSms> parts) {
		super();
		this.parts = parts;
	}

	/**
	 * Initialize with the provided parts. Each part MUST contain:
	 * <ul>
	 * <li>The expected phone number of the sender</li>
	 * <li>The expected phone number of the receiver</li>
	 * <li>The expected message part</li>
	 * </ul>
	 * <p>
	 * The receiver and sender numbers have to be duplicated. See
	 * {@link #SplitSms(String, String, String...)} for reusing the receiver and
	 * sender numbers.
	 * </p>
	 * 
	 * @param parts
	 *            the parts
	 */
	public SplitSms(ExpectedSms... parts) {
		this(Arrays.asList(parts));
	}

	/**
	 * Initialize several parts by reusing the sender and receiver numbers. Each
	 * message part is directly provided as string.
	 * 
	 * @param senderNumber
	 *            the number of the sender to use in each part
	 * @param receiverNumber
	 *            the number of the receiver to use in each part
	 * @param messages
	 *            the array of message parts
	 */
	public SplitSms(String senderNumber, String receiverNumber, String... messages) {
		this(toExpectedSms(senderNumber, receiverNumber, messages));
	}

	private static List<ExpectedSms> toExpectedSms(String senderNumber, String receiverNumber, String[] messages) {
		List<ExpectedSms> parts = new ArrayList<>(messages.length);
		for (String message : messages) {
			parts.add(new ExpectedSms(message, senderNumber, receiverNumber));
		}
		return parts;
	}

	/**
	 * Get the parts of the message.
	 * 
	 * @return the parts of the message
	 */
	public List<ExpectedSms> getParts() {
		return parts;
	}
}
