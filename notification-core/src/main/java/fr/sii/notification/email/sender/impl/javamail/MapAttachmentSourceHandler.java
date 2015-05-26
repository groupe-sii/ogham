package fr.sii.notification.email.sender.impl.javamail;

import java.util.HashMap;
import java.util.Map;

import javax.mail.BodyPart;

import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.attachment.Source;
import fr.sii.notification.email.exception.javamail.AttachmentSourceHandlerException;
import fr.sii.notification.email.exception.javamail.NoAttachmentSourceHandlerException;

/**
 * Provides a handler for the attachment source based on the class of the
 * attachment source.
 * 
 * @author Aurélien Baudet
 *
 */
public class MapAttachmentSourceHandler implements JavaMailAttachmentSourceHandler {
	/**
	 * The map of attachment source handlers indexed by the attachment source
	 * class
	 */
	private Map<Class<? extends Source>, JavaMailAttachmentSourceHandler> map;

	/**
	 * Initialize with the map of attachment source handlers indexed by the
	 * attachment source class.
	 * 
	 * @param map
	 *            the map of attachment source handlers indexed by the
	 *            attachment source class
	 */
	public MapAttachmentSourceHandler(Map<Class<? extends Source>, JavaMailAttachmentSourceHandler> map) {
		super();
		this.map = map;
	}

	/**
	 * Initialize an empty map
	 */
	public MapAttachmentSourceHandler() {
		this(new HashMap<Class<? extends Source>, JavaMailAttachmentSourceHandler>());
	}

	@Override
	public void setData(BodyPart part, Source source, Attachment attachment) throws AttachmentSourceHandlerException {
		JavaMailAttachmentSourceHandler attachmentHandler = map.get(source.getClass());
		if (attachmentHandler == null) {
			throw new NoAttachmentSourceHandlerException("there is no attachment source handler defined for managing " + source.getClass().getSimpleName() + " attachment source class", attachment);
		}
		attachmentHandler.setData(part, source, attachment);
	}

	/**
	 * Register a new attachment source handler.
	 * 
	 * @param clazz
	 *            the class of the attachment source
	 * @param handler
	 *            the attachment source handler
	 */
	public void addSourceHandler(Class<? extends Source> clazz, JavaMailAttachmentSourceHandler handler) {
		map.put(clazz, handler);
	}

}
