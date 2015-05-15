package fr.sii.notification.email.sender.impl.javamail;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import fr.sii.notification.core.exception.sender.ContentHandlerException;
import fr.sii.notification.core.exception.sender.NoContentHandlerException;
import fr.sii.notification.core.message.content.Content;

public class MapContentHandler implements JavaMailContentHandler {
	private Map<Class<? extends Content>, JavaMailContentHandler> map;
	
	public MapContentHandler(Map<Class<? extends Content>, JavaMailContentHandler> map) {
		super();
		this.map = map;
	}

	public MapContentHandler() {
		this(new HashMap<Class<? extends Content>, JavaMailContentHandler>());
	}
	
	@Override
	public void setContent(MimeMessage message, Content content) throws ContentHandlerException {
		JavaMailContentHandler contentHandler = map.get(content.getClass());
		if(contentHandler==null) {
			throw new NoContentHandlerException("there is no content handler defined for managing "+content.getClass().getSimpleName()+" content class", content);
		}
		contentHandler.setContent(message, content);
	}
	
	public void addContentHandler(Class<? extends Content> clazz, JavaMailContentHandler handler) {
		map.put(clazz, handler);
	}

}
