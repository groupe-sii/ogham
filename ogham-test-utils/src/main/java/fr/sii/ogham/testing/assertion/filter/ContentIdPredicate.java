package fr.sii.ogham.testing.assertion.filter;

import static java.util.Collections.list;

import java.util.function.Predicate;

import jakarta.mail.Header;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;

/**
 * Predicate that matches the {@link Part} only if {@link Part#getAllHeaders()}
 * contains a {@code Content-ID} header that exactly matches the provided
 * Content-ID.
 * 
 * @author Aurélien Baudet
 *
 */
public class ContentIdPredicate implements Predicate<Part> {
	private final String contentId;

	public ContentIdPredicate(String contentId) {
		super();
		this.contentId = contentId;
	}

	@Override
	public boolean test(Part input) {
		try {
			// @formatter:off
			return list(input.getMatchingHeaders(new String[] { "Content-ID" }))
					.stream()
					.map(Header::getValue)
					.anyMatch(contentId::equals);
			// @formatter:on
		} catch (MessagingException e) {
			throw new AssertionError("Failed to access message", e);
		}
	}

	@Override
	public String toString() {
		return "having Content-ID header '" + contentId + "'";
	}

}
