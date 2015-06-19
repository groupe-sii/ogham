package fr.sii.ogham.sms.message;


public class Recipient extends Contact {

	/**
	 * Initialize the contact with its name and its phone number.
	 * 
	 * @param name
	 *            the name of the contact
	 * @param phoneNumber
	 *            the phone number for the contact
	 */
	public Recipient(String name, PhoneNumber phoneNumber) {
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
	public Recipient(PhoneNumber phoneNumber) {
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
	public Recipient(String name, String phoneNumber) {
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
	public Recipient(String phoneNumber) {
		super(phoneNumber);
	}
}
