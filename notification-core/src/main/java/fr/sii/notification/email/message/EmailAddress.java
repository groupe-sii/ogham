package fr.sii.notification.email.message;

import javax.mail.internet.InternetAddress;

import fr.sii.notification.core.util.EqualsBuilder;
import fr.sii.notification.core.util.HashCodeBuilder;

/**
 * Represents an email address. {@link InternetAddress} also provides the same
 * feature but we don't want to be sticked to a particular implementation.
 * 
 * @author Aurélien Baudet
 *
 */
public class EmailAddress {
	/**
	 * The email address part (is of the form "user@domain.host")
	 */
	private String address;

	/**
	 * The user name part (can be anything)
	 */
	private String personal;

	/**
	 * Initialize the address with only the email address part (no personal). It
	 * is of the form "user@domain.host".
	 * 
	 * @param address
	 *            the email address part
	 */
	public EmailAddress(String address) {
		this(address, null);
	}

	/**
	 * Initialize the address with the email address and the personal parts.
	 * 
	 * @param address
	 *            the email address part, it is of the form "user@domain.host"
	 * @param personal
	 *            the personal part
	 */
	public EmailAddress(String address, String personal) {
		super();
		this.address = address;
		this.personal = personal;
	}

	public String getAddress() {
		return address;
	}

	public String getPersonal() {
		return personal;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (personal != null && !personal.isEmpty()) {
			builder.append(personal).append(" ");
		}
		builder.append("<").append(address).append(">");
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(address).append(personal).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("address", "personal").isEqual();
	}
}
