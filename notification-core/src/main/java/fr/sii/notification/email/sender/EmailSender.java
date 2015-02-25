package fr.sii.notification.email.sender;

import java.util.Map;

import fr.sii.notification.core.condition.Condition;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.ConditionalSender;
import fr.sii.notification.core.sender.MultiImplementationSender;
import fr.sii.notification.core.sender.NotificationSender;
import fr.sii.notification.email.message.Email;

public class EmailSender extends MultiImplementationSender<Email> implements ConditionalSender {

	public EmailSender() {
		super();
	}

	public EmailSender(Condition<Message> condition, NotificationSender implementation) {
		super(condition, implementation);
	}

	public EmailSender(Map<Condition<Message>, NotificationSender> implementations) {
		super(implementations);
	}
}
