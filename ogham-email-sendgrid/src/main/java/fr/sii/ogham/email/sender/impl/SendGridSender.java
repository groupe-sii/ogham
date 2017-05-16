package fr.sii.ogham.email.sender.impl;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.sender.AbstractSpecializedSender;
import fr.sii.ogham.email.exception.sendgrid.ContentHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.email.message.Recipient;
import fr.sii.ogham.email.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sender.impl.sendgrid.handler.SendGridContentHandler;

/**
 * SendGrid-backed implementation of the email sender.
 */
public final class SendGridSender extends AbstractSpecializedSender<Email> {

	private static final Logger LOG = LoggerFactory.getLogger(SendGridSender.class);

	private final SendGridClient service;
	private final SendGridContentHandler handler;

	/**
	 * Constructor.
	 * 
	 * @param service
	 *            the underlying SendGrid service
	 * @param handler
	 *            the content handler, in change of converting the email content
	 *            into something the {@link SendGridClient} can work with
	 */
	public SendGridSender(final SendGridClient service, final SendGridContentHandler handler) {
		if (service == null) {
			throw new IllegalArgumentException("[service] cannot be null");
		}
		if (handler == null) {
			throw new IllegalArgumentException("[handler] cannot be null");
		}

		this.service = service;
		this.handler = handler;
	}

	@Override
	public void send(final Email message) throws MessageException {
		if (message == null) {
			throw new IllegalArgumentException("[message] cannot be null");
		}
		final Set<String> violations = validate(message);
		if (!violations.isEmpty()) {
			throw new MessageException("The provided email is invalid. (Violations: " + violations + ")", message);
		}

		try {
			LOG.debug("Preparing to send email using SendGrid: {}", message);
			final SendGrid.Email sgEmail = toSendGridEmail(message);

			LOG.debug("Sending email {}", sgEmail);
			service.send(sgEmail);
			LOG.debug("Email has been successfully sent");
		} catch (ContentHandlerException e) {
			throw new MessageException("A content-related error occurred when trying to build an email", message, e);
		} catch (SendGridException e) {
			throw new MessageException("A SendGrid-related error occurred when trying to send an email", message, e);
		}
	}

	private Set<String> validate(final Email message) {
		final Set<String> violations = new HashSet<String>();

		if (message.getContent() == null) {
			violations.add("Missing content");
		}
		if (message.getSubject() == null) {
			violations.add("Missing subject");
		}

		if (message.getFrom() == null) {
			violations.add("Missing sender email address");
		} else if (message.getFrom().getPersonal() == null) {
			violations.add("Missing sender name");
		}

		if (message.getRecipients().isEmpty()) {
			violations.add("Missing recipients");
		}

		for (Recipient recipient : message.getRecipients()) {
			if (recipient.getAddress().getPersonal() == null) {
				violations.add("Missing recipient name for address " + recipient.getAddress().getAddress());
			}
		}

		return violations;
	}

	private SendGrid.Email toSendGridEmail(final Email message) throws ContentHandlerException {
		final SendGrid.Email ret = new SendGrid.Email();
		ret.setSubject(message.getSubject());

		ret.setFrom(message.getFrom().getAddress());
		ret.setFromName(message.getFrom().getPersonal());

		final String[] tos = new String[message.getRecipients().size()];
		final String[] toNames = new String[message.getRecipients().size()];
		int i = 0;
		for (Recipient recipient : message.getRecipients()) {
			final EmailAddress address = recipient.getAddress();
			tos[i] = address.getAddress();
			toNames[i] = address.getPersonal();
			i++;
		}
		ret.setTo(tos);
		ret.setToName(toNames);

		handler.setContent(ret, message.getContent());

		return ret;
	}

}
