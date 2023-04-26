package fr.sii.ogham.testing.assertion.filter;

import ogham.testing.jakarta.mail.MessagingException;
import ogham.testing.jakarta.mail.Part;

import java.util.function.Predicate;


/**
 * Predicate that matches the {@link Part} only if {@link Part#getFileName()}
 * exactly matches the provided filename.
 * 
 * @author Aurélien Baudet
 *
 */
public class FileNamePredicate implements Predicate<Part> {
	private final String filename;

	public FileNamePredicate(String filename) {
		super();
		this.filename = filename;
	}

	@Override
	public boolean test(Part input) {
		try {
			return filename.equals(input.getFileName());
		} catch (MessagingException e) {
			throw new AssertionError("Failed to access message", e);
		}
	}

	@Override
	public String toString() {
		return "named '" + filename + "'";
	}
}