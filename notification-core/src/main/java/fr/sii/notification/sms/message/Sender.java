package fr.sii.notification.sms.message;


public class Sender extends Contact {

	public Sender(PhoneNumber phoneNumber, String name) {
		super(phoneNumber, name);
	}

	public Sender(PhoneNumber phoneNumber) {
		super(phoneNumber);
	}

	public Sender(String phoneNumber, String name) {
		super(phoneNumber, name);
	}

	public Sender(String phoneNumber) {
		super(phoneNumber);
	}

}
