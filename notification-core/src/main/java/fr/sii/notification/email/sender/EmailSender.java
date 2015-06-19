package fr.sii.notification.email.sender;

import java.util.Map;

import fr.sii.notification.core.condition.Condition;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.MultiImplementationSender;
import fr.sii.notification.core.sender.NotificationSender;
import fr.sii.notification.email.message.Email;

/**
 * Specialization of {@link MultiImplementationSender} for managing emails using
 * several implementations.
 * 
 * @author Aurélien Baudet
 *
 */
public class EmailSender extends MultiImplementationSender<Email> {

	public EmailSender() {
		super();
	}

	public EmailSender(Condition<Message> condition, NotificationSender implementation) {
		super(condition, implementation);
	}

	public EmailSender(Map<Condition<Message>, NotificationSender> implementations) {
		super(implementations);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmailSender ").append(getImplementations().values());
		return builder.toString();
	}
}
