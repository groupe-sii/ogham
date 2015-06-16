package fr.sii.notification.helper.sms;

import fr.sii.notification.sms.message.addressing.AddressedPhoneNumber;

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
	private final AddressedPhoneNumber senderNumber;

	/**
	 * The expected phone number of the receiver
	 */
	private final AddressedPhoneNumber receiverNumber;

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
	public ExpectedSms(String message, AddressedPhoneNumber senderNumber, AddressedPhoneNumber receiverNumber) {
		super();
		this.message = message;
		this.senderNumber = senderNumber;
		this.receiverNumber = receiverNumber;
	}

	public String getMessage() {
		return message;
	}

	public AddressedPhoneNumber getSenderNumber() {
		return senderNumber;
	}

	public AddressedPhoneNumber getReceiverNumber() {
		return receiverNumber;
	}
}
