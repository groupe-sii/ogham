package fr.sii.ogham.email.sendgrid.v4.sender.impl;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Personalization;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.sender.AbstractSpecializedSender;
import fr.sii.ogham.email.exception.sendgrid.ContentHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.email.message.Recipient;
import fr.sii.ogham.email.sendgrid.sender.SendGridSender;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.SendGridException;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridInterceptor;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler.SendGridContentHandler;

/**
 * SendGrid-backed implementation of the email sender.
 */
public final class SendGridV4Sender extends AbstractSpecializedSender<Email> implements SendGridSender {

	private static final Logger LOG = LoggerFactory.getLogger(SendGridV4Sender.class);

	private final SendGridClient delegate;
	private final SendGridContentHandler handler;
	private final SendGridInterceptor interceptor;

	/**
	 * Constructor.
	 * 
	 * @param service
	 *            the underlying SendGrid service
	 * @param handler
	 *            the content handler, in change of converting the email content
	 *            into something the {@link SendGridClient} can work with
	 */
	public SendGridV4Sender(final SendGridClient service, final SendGridContentHandler handler) {
		this(service, handler, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param service
	 *            the underlying SendGrid service
	 * @param handler
	 *            the content handler, in change of converting the email content
	 *            into something the {@link SendGridClient} can work with
	 * @param interceptor
	 *            an extension point for customizing the email to send
	 */
	public SendGridV4Sender(final SendGridClient service, final SendGridContentHandler handler, SendGridInterceptor interceptor) {
		if (service == null) {
			throw new IllegalArgumentException("[service] cannot be null");
		}
		if (handler == null) {
			throw new IllegalArgumentException("[handler] cannot be null");
		}

		this.delegate = service;
		this.handler = handler;
		this.interceptor = interceptor;
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
			final Mail sgEmail = intercept(toSendGridEmail(message), message);

			LOG.debug("Sending email {}", sgEmail);
			delegate.send(sgEmail);
			LOG.debug("Email has been successfully sent");
		} catch (ContentHandlerException e) {
			throw new MessageException("A content-related error occurred when trying to build an email", message, e);
		} catch (SendGridException e) {
			throw new MessageException("A SendGrid-related error occurred when trying to send an email", message, e);
		}
	}

	private Mail intercept(Mail sendGridEmail, Email source) {
		if(interceptor==null) {
			return sendGridEmail;
		}
		return interceptor.intercept(sendGridEmail, source);
	}

	private Set<String> validate(final Email message) {
		final Set<String> violations = new HashSet<>();

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

	private Mail toSendGridEmail(final Email message) throws ContentHandlerException {
		final Mail ret = new Mail();
		ret.setSubject(message.getSubject());

		ret.setFrom(new com.sendgrid.helpers.mail.objects.Email(message.getFrom().getAddress(), message.getFrom().getPersonal()));

		for (Recipient recipient : message.getRecipients()) {
			ret.addPersonalization(toPersonalization(recipient));
		}

		handler.setContent(ret, message.getContent());
		
		// TODO: handle attachments

		return ret;
	}

	private Personalization toPersonalization(Recipient recipient) {
		Personalization personalization = new Personalization();
		addRecipient(personalization, recipient);
		return personalization;
	}
	
	private void addRecipient(Personalization personalization, Recipient recipient) {
		final EmailAddress address = recipient.getAddress();
		switch(recipient.getType()) {
			case TO:
				personalization.addTo(new com.sendgrid.helpers.mail.objects.Email(address.getAddress(), address.getPersonal()));
				break;
			case CC:
				personalization.addCc(new com.sendgrid.helpers.mail.objects.Email(address.getAddress(), address.getPersonal()));
				break;
			case BCC:
				personalization.addBcc(new com.sendgrid.helpers.mail.objects.Email(address.getAddress(), address.getPersonal()));
				break;
		}
	}

	public SendGridClient getDelegate() {
		return delegate;
	}
}
