package fr.sii.ogham.sms.builder;

import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;

public class PhoneNumberTranslatorPair {
	private final PhoneNumberTranslator sender;
	private final PhoneNumberTranslator recipient;
	public PhoneNumberTranslatorPair(PhoneNumberTranslator sender, PhoneNumberTranslator recipient) {
		super();
		this.sender = sender;
		this.recipient = recipient;
	}
	public PhoneNumberTranslator getSender() {
		return sender;
	}
	public PhoneNumberTranslator getRecipient() {
		return recipient;
	}
}