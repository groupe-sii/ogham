package fr.sii.ogham.testing.assertion.filter;

import java.util.function.Predicate;

import ogham.testing.jakarta.mail.Message;
import ogham.testing.jakarta.mail.MessagingException;
import ogham.testing.jakarta.mail.Part;
import ogham.testing.jakarta.mail.internet.MimePart;

import fr.sii.ogham.testing.assertion.exception.FilterException;
import fr.sii.ogham.testing.assertion.util.EmailUtils;

/**
 * Attachments may be everywhere in the message hierarchy. The default
 * filter skips any body part that is either text/plain or text/html. All
 * other parts are considered attachments (even if related to HTML message
 * like images).
 * 
 * @author Aurélien Baudet
 */
public class DefaultAttachmentPredicate implements Predicate<Part> {

	@Override
	public boolean test(Part p) {
		if (p instanceof Message) {
			return false;
		}
		return !EmailUtils.isMultipart(p) && (isDownloadableAttachment(p) || isEmbeddableAttachment(p)); 
	}

	private boolean isDownloadableAttachment(Part p) {
		try {
			return Part.ATTACHMENT.equalsIgnoreCase(p.getDisposition()) || p.getFileName() != null;
		} catch(MessagingException e) {
			throw new FilterException("Failed to check if attachment is downloadable", e);
		}
	}

	private boolean isEmbeddableAttachment(Part p) {
		try {
			return Part.INLINE.equalsIgnoreCase(p.getDisposition()) || hasContentID(p);
		} catch(MessagingException e) {
			throw new FilterException("Failed to check if attachment is embeddable", e);
		}
	}

	private boolean hasContentID(Part p) throws MessagingException {
		if (p instanceof MimePart) {
			return ((MimePart) p).getContentID() != null;
		}
		return false;
	}

}