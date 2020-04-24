package fr.sii.ogham.email.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.email.attachment.Attachment;

/**
 * Attachments are referenced using a resource path. Therefore the resource must
 * be resolved and it's content read. This is done using a resource handlers.
 * 
 * This exception is thrown when no resource handler has been configured so the
 * attachment is unresolvable.
 * 
 * @author Aurélien Baudet
 *
 */
public class UnresolvableAttachmentResourceHandlerException extends AttachmentResourceHandlerException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public UnresolvableAttachmentResourceHandlerException(String message, Attachment attachment) {
		super(message, attachment);
	}

}
