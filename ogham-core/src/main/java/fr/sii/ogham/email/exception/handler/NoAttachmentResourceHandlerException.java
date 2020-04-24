package fr.sii.ogham.email.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.email.attachment.Attachment;

/**
 * Attachments are referenced using a resource path. Therefore the resource must
 * be resolved and it's content read. This is done using a resource handlers.
 * 
 * This is a specialized exception that indicates that there is no resource
 * handler that is able to handle the resource.
 * 
 * @author Aurélien Baudet
 */
public class NoAttachmentResourceHandlerException extends AttachmentResourceHandlerException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public NoAttachmentResourceHandlerException(String message, Attachment attachment) {
		super(message, attachment);
	}

}
