package fr.sii.ogham.email.sender.impl.javaxmail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.BodyPart;

import fr.sii.ogham.core.resource.NamedResource;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.handler.AttachmentResourceHandlerException;
import fr.sii.ogham.email.exception.handler.NoAttachmentResourceHandlerException;

/**
 * Provides a handler for the attachment resource based on the class of the
 * attachment resource. The registration order is important.
 * 
 * @author Aurélien Baudet
 *
 */
public class MapAttachmentResourceHandler implements JavaMailAttachmentResourceHandler {
	/**
	 * The mapping of attachment resource handlers indexed by the attachment
	 * resource class
	 */
	private final List<Mapping> mappings;

	/**
	 * Initialize an empty mapping
	 */
	public MapAttachmentResourceHandler() {
		super();
		this.mappings = new ArrayList<>();
	}

	@Override
	public void setData(BodyPart part, NamedResource resource, Attachment attachment) throws AttachmentResourceHandlerException {
		JavaMailAttachmentResourceHandler attachmentHandler = find(resource.getClass());
		if (attachmentHandler == null) {
			throw new NoAttachmentResourceHandlerException("there is no attachment resource handler defined for managing " + resource.getClass().getSimpleName() + " attachment resource class",
					attachment);
		}
		attachmentHandler.setData(part, resource, attachment);
	}

	/**
	 * Register a new attachment resource handler. The registration order is
	 * important.
	 * 
	 * @param clazz
	 *            the class of the attachment resource
	 * @param handler
	 *            the attachment resource handler
	 */
	public void registerResourceHandler(Class<? extends NamedResource> clazz, JavaMailAttachmentResourceHandler handler) {
		mappings.add(new Mapping(clazz, handler));
	}

	private JavaMailAttachmentResourceHandler find(Class<? extends NamedResource> clazz) {
		for (Mapping mapping : mappings) {
			if (mapping.getClazz().isAssignableFrom(clazz)) {
				return mapping.getHandler();
			}
		}
		return null;
	}

	private static class Mapping {
		private final Class<? extends NamedResource> clazz;
		private final JavaMailAttachmentResourceHandler handler;

		public Mapping(Class<? extends NamedResource> clazz, JavaMailAttachmentResourceHandler handler) {
			super();
			this.clazz = clazz;
			this.handler = handler;
		}

		public Class<? extends NamedResource> getClazz() {
			return clazz;
		}

		public JavaMailAttachmentResourceHandler getHandler() {
			return handler;
		}
	}
}
