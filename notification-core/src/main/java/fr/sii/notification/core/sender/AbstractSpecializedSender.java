package fr.sii.notification.core.sender;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.message.Message;

public abstract class AbstractSpecializedSender<M> implements NotificationSender {

	@Override
	@SuppressWarnings("unchecked")
	public void send(Message message) throws MessageException {
		send((M) message);
	}

	public abstract void send(M message) throws MessageException;
}
