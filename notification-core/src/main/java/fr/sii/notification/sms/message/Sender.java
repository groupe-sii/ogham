package fr.sii.notification.sms.message;

/**
 * Specialization of {@link Contact} that represents the sender of the SMS.
 */
public class Sender extends Contact {

	/**
	 * Initialize the contact with its name and its phone number.
	 * 
	 * @param name
	 *            the name of the contact
	 * @param phoneNumber
	 *            the phone number for the contact
	 */
	public Sender(String name, PhoneNumber phoneNumber) {
		super(name, phoneNumber);
	}

	/**
	 * Initialize the contact with its name and its phone number.
	 * <p>
	 * No name is specified.
	 * </p>
	 * 
	 * @param phoneNumber
	 *            the phone number for the contact
	 */
	public Sender(PhoneNumber phoneNumber) {
		super(phoneNumber);
	}

	/**
	 * Initialize the contact with its name and its phone number as string.
	 * 
	 * @param name
	 *            the name of the contact
	 * @param phoneNumber
	 *            the phone number for the contact as string
	 */
	public Sender(String name, String phoneNumber) {
		super(name, phoneNumber);
	}

	/**
	 * Initialize the contact with its phone number as string.
	 * <p>
	 * No name is specified.
	 * </p>
	 * 
	 * @param phoneNumber
	 *            the phone number for the contact
	 */
	public Sender(String phoneNumber) {
		super(phoneNumber);
	}

}
