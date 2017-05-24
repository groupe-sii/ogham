package fr.sii.ogham.email.exception.javamail;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.email.attachment.Attachment;

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
