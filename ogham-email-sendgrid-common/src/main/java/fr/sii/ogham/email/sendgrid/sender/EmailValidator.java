package fr.sii.ogham.email.sendgrid.sender;

import java.util.HashSet;
import java.util.Set;

import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.Recipient;

/**
 * Validate the email fields.
 * 
 * @author Aurélien Baudet
 *
 */
public final class EmailValidator {
	/**
	 * Ensure that email can be sent:
	 * <ul>
	 * <li>Content is required</li>
	 * <li>Subject is required</li>
	 * <li>Sender address is required</li>
	 * <li>At least one recipient is required</li>
	 * </ul>
	 * 
	 * @param message
	 *            the email to validate
	 * @return the violations if not valid or an empty list if valid
	 */
	public static Set<String> validate(final Email message) {
		final Set<String> violations = new HashSet<>();

		if (message.getContent() == null) {
			violations.add("Missing content");
		}
		if (message.getSubject() == null) {
			violations.add("Missing subject");
		}

		if (message.getFrom() == null) {
			violations.add("Missing sender email address");
		}

		if (message.getRecipients().isEmpty()) {
			violations.add("Missing recipients");
		}

		for (Recipient recipient : message.getRecipients()) {
			if (recipient.getAddress().getAddress() == null) {
				violations.add("Missing recipient address " + recipient);
			}
		}

		return violations;
	}

	private EmailValidator() {
		super();
	}

}
