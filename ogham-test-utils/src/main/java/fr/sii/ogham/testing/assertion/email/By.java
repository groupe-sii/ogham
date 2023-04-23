package fr.sii.ogham.testing.assertion.email;

import java.util.function.Predicate;

import jakarta.mail.Part;

import fr.sii.ogham.testing.assertion.filter.ContentIdPredicate;
import fr.sii.ogham.testing.assertion.filter.FileNamePredicate;

/**
 * Helper class to provide well-known predicates.
 * 
 * @author Aurélien Baudet
 *
 */
public final class By {
	private final Predicate<Part> predicate;

	private By(Predicate<Part> predicate) {
		super();
		this.predicate = predicate;
	}

	/**
	 * Find an attachment using its name (exact match).
	 * 
	 * @param name
	 *            the name of the attachment to find
	 * @return the finder method
	 */
	public static By filename(String name) {
		return new By(new FileNamePredicate(name));
	}

	/**
	 * Find an attachment by the Content-ID header value (exact match).
	 * 
	 * @param contentId
	 *            the value of the Content-ID header of the attachment to find
	 * @return the finder method
	 */
	public static By contentId(String contentId) {
		return new By(new ContentIdPredicate(contentId));
	}

	Predicate<Part> toPredicate() {
		return predicate;
	}
}
