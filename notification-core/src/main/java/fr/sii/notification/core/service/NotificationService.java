package fr.sii.notification.core.service;

import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.Message;

public interface NotificationService {
	public void send(Message message) throws NotificationException;
}
