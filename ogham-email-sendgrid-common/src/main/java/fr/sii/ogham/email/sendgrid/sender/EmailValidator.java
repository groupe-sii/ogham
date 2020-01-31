package fr.sii.ogham.email.sendgrid.sender;

import java.util.HashSet;
import java.util.Set;

import fr.sii.ogham.core.exception.InvalidMessageException;
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
	 * @throws InvalidMessageException
	 *             when the email is not valid
	 */
	public static void validate(final Email message) throws InvalidMessageException {
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

		if (!violations.isEmpty()) {
			throw new InvalidMessageException("The provided email is invalid. (Violations: " + violations + ")", message, violations);
		}
	}

	private EmailValidator() {
		super();
	}

}
