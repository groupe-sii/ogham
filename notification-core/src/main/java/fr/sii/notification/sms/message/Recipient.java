package fr.sii.notification.sms.message;

public class Recipient {
	private String name;
	
	private String phoneNumber;

	public Recipient(String phoneNumber) {
		this(phoneNumber, null);
	}
	
	public Recipient(String phoneNumber, String name) {
		super();
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Recipient [name=").append(name).append(", phoneNumber=").append(phoneNumber).append("]");
		return builder.toString();
	}
}
