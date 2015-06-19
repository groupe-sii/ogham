package fr.sii.ogham.email.message;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Represents an email recipient. The recipient contains:
 * <ul>
 * <li>The email address (see {@link EmailAddress})</li>
 * <li>The recipient type (the field of the email: to, cc, bcc)</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class Recipient {
	/**
	 * The recipient address
	 */
	private EmailAddress address;

	/**
	 * The recipient type
	 */
	private RecipientType type;

	/**
	 * Initialize the recipient with the provided address. The address is of the
	 * form "user@host.domain" or "Personal Name &lt;user@host.domain&gt;". The
	 * recipient type is set to {@link RecipientType#TO}.
	 * 
	 * @param address
	 *            the email address
	 */
	public Recipient(String address) {
		this(new EmailAddress(address));
	}

	/**
	 * Initialize the recipient with the provided address. The recipient type is
	 * set to {@link RecipientType#TO}.
	 * 
	 * @param address
	 *            the email address
	 */
	public Recipient(EmailAddress address) {
		this(address, RecipientType.TO);
	}

	/**
	 * Initialize the recipient with the provided address and for the provided
	 * type.
	 * 
	 * @param address
	 *            the email address
	 * @param type
	 *            the recipient type
	 */
	public Recipient(EmailAddress address, RecipientType type) {
		super();
		this.address = address;
		this.type = type;
	}

	public EmailAddress getAddress() {
		return address;
	}

	public RecipientType getType() {
		return type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(address).append("(").append(type).append(")");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(address, type).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("address", "type").isEqual();
	}
}
