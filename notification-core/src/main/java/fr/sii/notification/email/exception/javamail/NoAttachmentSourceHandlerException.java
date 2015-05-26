package fr.sii.notification.email.exception.javamail;

import fr.sii.notification.email.attachment.Attachment;

public class NoAttachmentSourceHandlerException extends AttachmentSourceHandlerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7371288847358143602L;

	public NoAttachmentSourceHandlerException(String message, Attachment attachment) {
		super(message, attachment);
	}

}
