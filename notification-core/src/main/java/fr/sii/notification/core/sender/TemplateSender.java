package fr.sii.notification.core.sender;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.exception.template.ParseException;
import fr.sii.notification.core.message.Message;
import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.message.content.TemplateContent;
import fr.sii.notification.core.template.parser.TemplateParser;

public class TemplateSender implements ConditionalSender {

	private NotificationSender delegate;
	
	private TemplateParser parser;
	
	public TemplateSender(TemplateParser parser, NotificationSender delegate) {
		super();
		this.parser = parser;
		this.delegate = delegate;
	}

	@Override
	public boolean supports(Message message) {
		return delegate instanceof ConditionalSender ? ((ConditionalSender) delegate).supports(message) : true;
	}

	@Override
	public void send(Message message) throws MessageException {
		try {
			if(message.getContent() instanceof TemplateContent) {
				TemplateContent template = (TemplateContent) message.getContent();
				// parse template
				Content content = parser.parse(template.getPath(), template.getContext());
				// update message with parsed content
				message.setContent(content);
			}
			// send message
			delegate.send(message);
		} catch(MessageException | ParseException e) {
			throw new MessageException("Failed to send templated message", message, e);
		}
	}

}
