package fr.sii.notification.core.sender;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.message.Message;


public interface NotificationSender {
	public void send(Message message) throws MessageException;
}