package fr.sii.ogham.email.sender;

import java.util.List;

import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.MessageSender;
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

	public EmailSender(Condition<Message> condition, MessageSender implementation) {
		super(condition, implementation);
	}

	public EmailSender(List<Implementation> implementations) {
		super(implementations);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmailSender ").append(getImplementations());
		return builder.toString();
	}
}
