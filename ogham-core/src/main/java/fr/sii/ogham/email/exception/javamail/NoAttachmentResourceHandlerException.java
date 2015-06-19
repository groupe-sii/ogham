package fr.sii.ogham.email.exception.javamail;

import fr.sii.ogham.email.attachment.Attachment;

public class NoAttachmentResourceHandlerException extends AttachmentResourceHandlerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7371288847358143602L;

	public NoAttachmentResourceHandlerException(String message, Attachment attachment) {
		super(message, attachment);
	}

}
