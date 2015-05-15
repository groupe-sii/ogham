package fr.sii.notification.core.sender;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.filler.MessageFiller;
import fr.sii.notification.core.message.Message;

public class FillerSender implements ConditionalSender {

	private MessageFiller filler;
	
	private NotificationSender delegate;
	
	public FillerSender(MessageFiller filler, ConditionalSender delegate) {
		super();
		this.filler = filler;
		this.delegate = delegate;
	}

	@Override
	public void send(Message message) throws MessageException {
		// fill message with automatic values
		filler.fill(message);
		// send message
		delegate.send(message);
	}

	@Override
	public boolean supports(Message message) {
		return delegate instanceof ConditionalSender ? ((ConditionalSender) delegate).supports(message) : true;
	}

}
