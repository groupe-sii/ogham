package fr.sii.notification.email.message;

public class Recipient {
	private EmailAddress address;
	
	private RecipientType type;

	public Recipient(String address) {
		this(new EmailAddress(address));
	}
	
	public Recipient(EmailAddress address) {
		this(address, RecipientType.TO);
	}
	
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
		builder.append("Recipient [address=").append(address).append(", type=").append(type).append("]");
		return builder.toString();
	}
}
