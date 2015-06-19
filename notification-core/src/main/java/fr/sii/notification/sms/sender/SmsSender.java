package fr.sii.notification.sms.sender;

import java.util.Map;

import fr.sii.notification.core.condition.Condition;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.MultiImplementationSender;
import fr.sii.notification.core.sender.NotificationSender;
import fr.sii.notification.sms.message.Sms;

/**
 * Specialization of {@link MultiImplementationSender} for managing emails using
 * several implementations.
 * 
 * @author Aurélien Baudet
 *
 */
public class SmsSender extends MultiImplementationSender<Sms> {

	public SmsSender() {
		super();
	}

	public SmsSender(Condition<Message> condition, NotificationSender implementation) {
		super(condition, implementation);
	}

	public SmsSender(Map<Condition<Message>, NotificationSender> implementations) {
		super(implementations);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SmsSender ").append(getImplementations().values());
		return builder.toString();
	}
}
