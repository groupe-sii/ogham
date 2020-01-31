package fr.sii.ogham.testing.assertion.filter;

import java.util.function.Predicate;

import javax.mail.Part;

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
		return !EmailUtils.isTextualContent(p) && !EmailUtils.isMultipart(p);
	}

}