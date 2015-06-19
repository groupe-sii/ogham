package fr.sii.ogham.email.sender.impl.javamail;

import java.util.HashMap;
import java.util.Map;

import javax.mail.Multipart;
import javax.mail.internet.MimePart;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.email.exception.javamail.ContentHandlerException;
import fr.sii.ogham.email.exception.javamail.NoContentHandlerException;
import fr.sii.ogham.email.message.Email;

/**
 * Provides a content handler based on the class of the content.
 * 
 * @author Aurélien Baudet
 *
 */
public class MapContentHandler implements JavaMailContentHandler {
	/**
	 * The map of content handlers indexed by the content class
	 */
	private Map<Class<? extends Content>, JavaMailContentHandler> map;

	/**
	 * Initialize with the map of content handlers indexed by the content class.
	 * 
	 * @param map
	 *            the map of content handlers indexed by the content class
	 */
	public MapContentHandler(Map<Class<? extends Content>, JavaMailContentHandler> map) {
		super();
		this.map = map;
	}

	/**
	 * Initialize an empty map
	 */
	public MapContentHandler() {
		this(new HashMap<Class<? extends Content>, JavaMailContentHandler>());
	}

	@Override
	public void setContent(MimePart message, Multipart multipart, Email email, Content content) throws ContentHandlerException {
		JavaMailContentHandler contentHandler = map.get(content.getClass());
		if (contentHandler == null) {
			throw new NoContentHandlerException("there is no content handler defined for managing " + content.getClass().getSimpleName() + " content class", content);
		}
		contentHandler.setContent(message, multipart, email, content);
	}

	/**
	 * Register a new content handler.
	 * 
	 * @param clazz
	 *            the class of the content
	 * @param handler
	 *            the content handler
	 */
	public void addContentHandler(Class<? extends Content> clazz, JavaMailContentHandler handler) {
		map.put(clazz, handler);
	}

}
