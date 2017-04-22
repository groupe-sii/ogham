package fr.sii.ogham.helper.email;

import javax.mail.BodyPart;
import javax.mail.MessagingException;

import com.google.common.base.Predicate;

public class FileNamePredicate implements Predicate<BodyPart> {
	private final String filename;
	
	public FileNamePredicate(String filename) {
		super();
		this.filename = filename;
	}

	@Override
	public boolean apply(BodyPart input) {
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