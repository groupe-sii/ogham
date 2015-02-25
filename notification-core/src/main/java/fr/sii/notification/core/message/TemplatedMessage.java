package fr.sii.notification.core.message;

import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.core.template.context.BeanContext;
import fr.sii.notification.core.template.context.Context;

public class TemplatedMessage implements Message {

	private String templateName;
	
	private Context context;
	
	private Message message;
	
	public TemplatedMessage(Message message, String templateName, Object context) {
		this(message, templateName, new BeanContext(context));
	}

	public TemplatedMessage(Message message, String templateName, Context context) {
		super();
		this.message = message;
		this.templateName = templateName;
		this.context = context;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public Content getContent() {
		return message.getContent();
	}

	@Override
	public void setContent(Content content) {
		message.setContent(content);
	}
}
