package fr.sii.notification.core.sender;

import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.message.Message;

public class FallbackSender implements NotificationSender {

	private List<NotificationSender> senders;
	
	public FallbackSender(NotificationSender... senders) {
		this(Arrays.asList(senders));
	}

	public FallbackSender(List<NotificationSender> senders) {
		super();
		this.senders = senders;
	}

	@Override
	public void send(Message message) throws MessageException {
		for(NotificationSender sender : senders) {
			try {
				sender.send(message);
				return;
			} catch(Throwable e) {
				// TODO: log
			}
		}
		throw new MessageException("No sender could handle the message", message);
	}

}
