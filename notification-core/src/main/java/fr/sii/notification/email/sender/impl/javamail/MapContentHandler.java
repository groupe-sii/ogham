package fr.sii.notification.email.sender.impl.javamail;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimePart;

import fr.sii.notification.core.message.content.Content;
import fr.sii.notification.email.exception.javamail.ContentHandlerException;
import fr.sii.notification.email.exception.javamail.NoContentHandlerException;

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
	public void setContent(MimePart message, Content content) throws ContentHandlerException {
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
