package fr.sii.notification.email.exception.javamail;

import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.email.attachment.Attachment;

public class AttachmentResourceHandlerException extends NotificationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3172860709067806202L;
	
	private Attachment attachment;

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
