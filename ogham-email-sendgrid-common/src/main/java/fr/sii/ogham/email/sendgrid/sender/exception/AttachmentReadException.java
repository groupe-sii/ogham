package fr.sii.ogham.email.sendgrid.sender.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.email.attachment.Attachment;

public class AttachmentReadException extends SendGridException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final transient Attachment attachment;

	public AttachmentReadException(String message, Attachment attachment, Throwable cause) {
		super(message, cause);
		this.attachment = attachment;
	}

	public AttachmentReadException(String message, Attachment attachment) {
		super(message);
		this.attachment = attachment;
	}

	public AttachmentReadException(Throwable cause, Attachment attachment) {
		super(cause);
		this.attachment = attachment;
	}

	public Attachment getAttachment() {
		return attachment;
	}
}
