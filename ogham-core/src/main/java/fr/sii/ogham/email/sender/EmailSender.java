package fr.sii.ogham.email.sender;

import fr.sii.ogham.core.sender.MultiImplementationSender;
import fr.sii.ogham.email.message.Email;

/**
 * Specialization of {@link MultiImplementationSender} for managing emails using
 * several implementations.
 * 
 * @author Aurélien Baudet
 *
 */
public class EmailSender extends MultiImplementationSender<Email> {

	public EmailSender() {
		super();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmailSender ").append(getImplementations());
		return builder.toString();
	}
}
