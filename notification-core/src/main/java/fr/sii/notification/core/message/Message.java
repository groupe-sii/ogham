package fr.sii.notification.core.message;

import fr.sii.notification.core.message.content.Content;

public interface Message {
	public Content getContent();
	
	public void setContent(Content content);
}
