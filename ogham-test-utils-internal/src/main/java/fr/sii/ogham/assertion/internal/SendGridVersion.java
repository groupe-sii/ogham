package fr.sii.ogham.assertion.internal;

import fr.sii.ogham.email.sendgrid.sender.SendGridSender;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.SendGridV2Sender;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.SendGridV4Sender;

public enum SendGridVersion {
	V2(SendGridV2Sender.class),
	V4(SendGridV4Sender.class);

	private final Class<? extends SendGridSender> senderClass;

	private SendGridVersion(Class<? extends SendGridSender> senderClass) {
		this.senderClass = senderClass;
	}

	public Class<? extends SendGridSender> getSenderClass() {
		return senderClass;
	}
}