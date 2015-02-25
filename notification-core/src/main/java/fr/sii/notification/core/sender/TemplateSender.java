package fr.sii.notification.core.sender;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.exception.ParseException;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.message.TemplatedMessage;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.template.parser.TemplateParser;

public class TemplateSender extends AbstractSpecializedSender<TemplatedMessage> implements ConditionalSender {

	private NotificationSender delegate;
	
	private TemplateParser parser;
	
	public TemplateSender(TemplateParser parser, NotificationSender delegate) {
		super();
		this.parser = parser;
		this.delegate = delegate;
	}

	@Override
	public boolean supports(Message message) {
		return message instanceof TemplatedMessage;
	}

	@Override
	public void send(TemplatedMessage message) throws MessageException {
		try {
			// parse template
			Content content = parser.parse(message.getTemplateName(), message.getContext());
			// update message with parsed content
			message.setContent(content);
			// send message
			delegate.send(message.getMessage());
		} catch(MessageException | ParseException e) {
			throw new MessageException("Failed to send templated message", message, e);
		}
	}


}
