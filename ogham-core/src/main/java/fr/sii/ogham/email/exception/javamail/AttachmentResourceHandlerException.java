package fr.sii.ogham.email.exception.javamail;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.email.attachment.Attachment;

public class AttachmentResourceHandlerException extends MessagingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3172860709067806202L;
	
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
