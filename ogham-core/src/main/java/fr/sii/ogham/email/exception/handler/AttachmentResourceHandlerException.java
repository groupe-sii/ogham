package fr.sii.ogham.email.exception.handler;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.email.attachment.Attachment;

/**
 * Attachments are referenced using a resource path. Therefore the resource must
 * be resolved and it's content read. This is done using a resource handlers.
 * 
 * This exception is general and has subclasses to identify the reason why the
 * attachment resource handling has failed.
 * 
 * @author Aurélien Baudet
 *
 * @see NoAttachmentResourceHandlerException
 * @see UnresolvableAttachmentResourceHandlerException
 */
public class AttachmentResourceHandlerException extends MessagingException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient Attachment attachment;

	public AttachmentResourceHandlerException(String message, Attachment attachment, Throwable cause) {
		super(message, cause);
		this.attachment = attachment;
	}

	public AttachmentResourceHandlerException(String message, Attachment attachment) {
		super(message);
		this.attachment = attachment;
	}

	public AttachmentResourceHandlerException(Throwable cause, Attachment attachment) {
		super(cause);
		this.attachment = attachment;
	}

	public Attachment getAttachment() {
		return attachment;
	}
}
