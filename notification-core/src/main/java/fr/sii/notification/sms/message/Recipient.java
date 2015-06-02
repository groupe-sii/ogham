package fr.sii.notification.sms.message;


public class Recipient extends Contact {

	public Recipient(PhoneNumber phoneNumber, String name) {
		super(phoneNumber, name);
	}

	public Recipient(PhoneNumber phoneNumber) {
		super(phoneNumber);
	}

	public Recipient(String phoneNumber, String name) {
		super(phoneNumber, name);
	}

	public Recipient(String phoneNumber) {
		super(phoneNumber);
	}
}
