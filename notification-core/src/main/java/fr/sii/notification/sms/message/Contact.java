package fr.sii.notification.sms.message;

import fr.sii.notification.core.util.EqualsBuilder;
import fr.sii.notification.core.util.HashCodeBuilder;

/**
 * A SMS contact taht contains the following information:
 * <ul>
 * <li>The name of the contact (optional)</li>
 * <li>The phone number of the contact</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class Contact {
	/**
	 * The name of the contact
	 */
	private String name;

	/**
	 * The phone number for the contact
	 */
	private PhoneNumber phoneNumber;

	/**
	 * Initialize the contact with its phone number as string.
	 * <p>
	 * No name is specified.
	 * </p>
	 * 
	 * @param phoneNumber
	 *            the phone number for the contact
	 */
	public Contact(String phoneNumber) {
		this(new PhoneNumber(phoneNumber));
	}

	/**
	 * Initialize the contact with its name and its phone number as string.
	 * 
	 * @param name
	 *            the name of the contact
	 * @param phoneNumber
	 *            the phone number for the contact as string
	 */
	public Contact(String name, String phoneNumber) {
		this(new PhoneNumber(phoneNumber));
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
	public Contact(PhoneNumber phoneNumber) {
		this(null, phoneNumber);
	}

	/**
	 * Initialize the contact with its name and its phone number.
	 * 
	 * @param name
	 *            the name of the contact
	 * @param phoneNumber
	 *            the phone number for the contact
	 */
	public Contact(String name, PhoneNumber phoneNumber) {
		super();
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	public String getName() {
		return name;
	}

	public PhoneNumber getPhoneNumber() {
		return phoneNumber;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (name != null && !name.isEmpty()) {
			builder.append(name).append(" ");
		}
		builder.append("<").append(phoneNumber).append(">");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).append(phoneNumber).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("name", "phoneNumber").isEqual();
	}
}
