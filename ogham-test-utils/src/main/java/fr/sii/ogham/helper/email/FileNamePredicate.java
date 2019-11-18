package fr.sii.ogham.helper.email;

import javax.mail.MessagingException;
import javax.mail.Part;

import com.google.common.base.Predicate;

public class FileNamePredicate implements Predicate<Part> {
	private final String filename;
	
	public FileNamePredicate(String filename) {
		super();
		this.filename = filename;
	}

	@Override
	public boolean apply(Part input) {
		try {
			return filename.equals(input.getFileName());
		} catch (MessagingException e) {
			throw new AssertionError("Failed to access message", e);
		}
	}
	
	@Override
	public String toString() {
		return "named "+filename;
	}
}