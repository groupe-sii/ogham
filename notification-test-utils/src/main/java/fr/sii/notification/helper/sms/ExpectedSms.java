package fr.sii.notification.helper.sms;

/**
 * Class used in tests for ensuring that the SMS is respected. It provides the
 * following information:
 * <ul>
 * <li>The expected message</li>
 * <li>The expected sender phone number</li>
 * <li>The expected receiver phone number</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 */
public class ExpectedSms {
	/**
	 * The expected message
	 */
	private final String message;

	/**
	 * The expected phone number for the sender
	 */
	private final ExpectedAddressedPhoneNumber senderNumber;

	/**
	 * The expected phone number of the receiver
	 */
	private final ExpectedAddressedPhoneNumber receiverNumber;

	/**
	 * Initialize the expected SMS with the message, the sender phone number and
	 * the receiver phone number.
	 * 
	 * @param message
	 *            the expected message
	 * @param senderNumber
	 *            the expected phone number for the sender
	 * @param receiverNumber
	 *            the expected phone number of the receiver
	 */
	public ExpectedSms(String message, ExpectedAddressedPhoneNumber senderNumber, ExpectedAddressedPhoneNumber receiverNumber) {
		super();
		this.message = message;
		this.senderNumber = senderNumber;
		this.receiverNumber = receiverNumber;
	}

	public String getMessage() {
		return message;
	}

	public ExpectedAddressedPhoneNumber getSenderNumber() {
		return senderNumber;
	}

	public ExpectedAddressedPhoneNumber getReceiverNumber() {
		return receiverNumber;
	}
}
