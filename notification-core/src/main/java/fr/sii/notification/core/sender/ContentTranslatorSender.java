package fr.sii.notification.core.sender;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.exception.MessageNotSentException;
import fr.sii.notification.core.exception.handler.ContentTranslatorException;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.translator.ContentTranslator;

public class ContentTranslatorSender implements ConditionalSender {

	private ContentTranslator translator;
	
	private NotificationSender delegate;
	
	public ContentTranslatorSender(ContentTranslator translator, NotificationSender delegate) {
		super();
		this.translator = translator;
		this.delegate = delegate;
	}

	@Override
	public boolean supports(Message message) {
		return delegate instanceof ConditionalSender ? ((ConditionalSender) delegate).supports(message) : true;
	}
	
	@Override
	public void send(Message message) throws MessageException {
		try {
			message.setContent(translator.translate(message.getContent()));
			delegate.send(message);
		} catch (ContentTranslatorException e) {
			throw new MessageNotSentException("Failed to send message due to content handler", message, e);
		}
	}
}
