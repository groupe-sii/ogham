package fr.sii.notification.sms.message;

import fr.sii.notification.core.util.EqualsBuilder;
import fr.sii.notification.core.util.HashCodeBuilder;

public class Contact {
	private String name;
	
	private PhoneNumber phoneNumber;

	public Contact(String phoneNumber) {
		this(new PhoneNumber(phoneNumber));
	}
	
	public Contact(String phoneNumber, String name) {
		this(new PhoneNumber(phoneNumber));
	}

	public Contact(PhoneNumber phoneNumber) {
		this(phoneNumber, null);
	}
	
	public Contact(PhoneNumber phoneNumber, String name) {
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
		if(name!=null && !name.isEmpty()) {
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
