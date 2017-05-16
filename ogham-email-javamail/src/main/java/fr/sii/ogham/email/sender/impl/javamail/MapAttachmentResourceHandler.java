package fr.sii.ogham.email.sender.impl.javamail;

import java.util.HashMap;
import java.util.Map;

import javax.mail.BodyPart;

import fr.sii.ogham.core.resource.NamedResource;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.javamail.AttachmentResourceHandlerException;
import fr.sii.ogham.email.exception.javamail.NoAttachmentResourceHandlerException;

/**
 * Provides a handler for the attachment resource based on the class of the
 * attachment resource.
 * 
 * @author Aurélien Baudet
 *
 */
public class MapAttachmentResourceHandler implements JavaMailAttachmentResourceHandler {
	/**
	 * The map of attachment resource handlers indexed by the attachment resource
	 * class
	 */
	private Map<Class<? extends NamedResource>, JavaMailAttachmentResourceHandler> map;

	/**
	 * Initialize with the map of attachment resource handlers indexed by the
	 * attachment resource class.
	 * 
	 * @param map
	 *            the map of attachment resource handlers indexed by the
	 *            attachment resource class
	 */
	public MapAttachmentResourceHandler(Map<Class<? extends NamedResource>, JavaMailAttachmentResourceHandler> map) {
		super();
		this.map = map;
	}

	/**
	 * Initialize an empty map
	 */
	public MapAttachmentResourceHandler() {
		this(new HashMap<Class<? extends NamedResource>, JavaMailAttachmentResourceHandler>());
	}

	@Override
	public void setData(BodyPart part, NamedResource resource, Attachment attachment) throws AttachmentResourceHandlerException {
		JavaMailAttachmentResourceHandler attachmentHandler = map.get(resource.getClass());
		if (attachmentHandler == null) {
			throw new NoAttachmentResourceHandlerException("there is no attachment resource handler defined for managing " + resource.getClass().getSimpleName() + " attachment resource class", attachment);
		}
		attachmentHandler.setData(part, resource, attachment);
	}

	/**
	 * Register a new attachment resource handler.
	 * 
	 * @param clazz
	 *            the class of the attachment resource
	 * @param handler
	 *            the attachment resource handler
	 */
	public void addResourceHandler(Class<? extends NamedResource> clazz, JavaMailAttachmentResourceHandler handler) {
		map.put(clazz, handler);
	}

}
