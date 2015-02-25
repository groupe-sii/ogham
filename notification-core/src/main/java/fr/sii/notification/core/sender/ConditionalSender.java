package fr.sii.notification.core.sender;

import fr.sii.notification.core.message.Message;

public interface ConditionalSender extends NotificationSender {
	public boolean supports(Message message);
}
