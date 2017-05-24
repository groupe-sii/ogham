package fr.sii.ogham.email.exception.javamail;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.email.attachment.Attachment;

public class NoAttachmentResourceHandlerException extends AttachmentResourceHandlerException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public NoAttachmentResourceHandlerException(String message, Attachment attachment) {
		super(message, attachment);
	}

}
