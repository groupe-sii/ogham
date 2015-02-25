package fr.sii.notification.core.service;

import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.exception.MessageNotSentException;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.sender.ConditionalSender;

public class ChainNotificationService implements NotificationService {

	private List<ConditionalSender> senders;
	
	public ChainNotificationService(ConditionalSender... senders) {
		this(Arrays.asList(senders));
	}
	
	public ChainNotificationService(List<ConditionalSender> senders) {
		super();
		this.senders = senders;
	}


	@Override
	public void send(Message message) throws NotificationException {
		boolean sent = false;
		for(ConditionalSender sender : senders) {
			if(sender.supports(message)) {
				sender.send(message);
				sent = true;
			}
		}
		if(!sent) {
			throw new MessageNotSentException("No sender available to send the message", message);
		}
	}

	public ChainNotificationService addSender(ConditionalSender sender) {
		senders.add(sender);
		return this;
	}
}
