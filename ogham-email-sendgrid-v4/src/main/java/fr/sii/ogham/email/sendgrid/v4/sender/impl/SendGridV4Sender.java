package fr.sii.ogham.email.sendgrid.v4.sender.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Personalization;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.sender.AbstractSpecializedSender;
import fr.sii.ogham.core.util.Base64Utils;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.sendgrid.ContentHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.email.message.Recipient;
import fr.sii.ogham.email.sendgrid.sender.SendGridSender;
import fr.sii.ogham.email.sendgrid.sender.exception.AttachmentReadException;
import fr.sii.ogham.email.sendgrid.sender.exception.SendGridException;
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
	private final MimeTypeProvider mimetypeProvider;
	private final SendGridInterceptor interceptor;

	/**
	 * Constructor.
	 * 
	 * @param service
	 *            the underlying SendGrid service
	 * @param handler
	 *            the content handler, in change of converting the email content
	 *            into something the {@link SendGridClient} can work with
	 * @param mimetypeProvider
	 *            determines mimetype for attachments
	 */
	public SendGridV4Sender(final SendGridClient service, final SendGridContentHandler handler, MimeTypeProvider mimetypeProvider) {
		this(service, handler, mimetypeProvider, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param service
	 *            the underlying SendGrid service
	 * @param handler
	 *            the content handler, in change of converting the email content
	 *            into something the {@link SendGridClient} can work with
	 * @param mimetypeProvider
	 *            determines mimetype for attachments
	 * @param interceptor
	 *            an extension point for customizing the email to send
	 */
	public SendGridV4Sender(final SendGridClient service, final SendGridContentHandler handler, MimeTypeProvider mimetypeProvider, SendGridInterceptor interceptor) {
		if (service == null) {
			throw new IllegalArgumentException("[service] cannot be null");
		}
		if (handler == null) {
			throw new IllegalArgumentException("[handler] cannot be null");
		}
		if (mimetypeProvider == null) {
			throw new IllegalArgumentException("[mimetypeProvider] cannot be null");
		}

		this.delegate = service;
		this.handler = handler;
		this.mimetypeProvider = mimetypeProvider;
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
		} catch (AttachmentReadException e) {
			throw new MessageException("Attaching file to email failed when trying to send an email", message, e);
		} catch (SendGridException e) {
			throw new MessageException("A SendGrid-related error occurred when trying to send an email", message, e);
		}
	}

	private Mail intercept(Mail sendGridEmail, Email source) {
		if (interceptor == null) {
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

	private Mail toSendGridEmail(final Email message) throws ContentHandlerException, AttachmentReadException {
		final Mail sendGridMail = new Mail();
		sendGridMail.setSubject(message.getSubject());

		sendGridMail.setFrom(new com.sendgrid.helpers.mail.objects.Email(message.getFrom().getAddress(), message.getFrom().getPersonal()));

		for (Recipient recipient : message.getRecipients()) {
			sendGridMail.addPersonalization(toPersonalization(recipient));
		}

		handler.setContent(sendGridMail, message.getContent());

		for (Attachment attachment : message.getAttachments()) {
			addAttachment(sendGridMail, attachment);
		}

		return sendGridMail;
	}

	private Personalization toPersonalization(Recipient recipient) {
		Personalization personalization = new Personalization();
		addRecipient(personalization, recipient);
		return personalization;
	}

	private void addRecipient(Personalization personalization, Recipient recipient) {
		final EmailAddress address = recipient.getAddress();
		switch (recipient.getType()) {
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

	private void addAttachment(final Mail sendGridMail, final Attachment attachment) throws AttachmentReadException {
		try {
			Attachments sendGridAttachment = new Attachments();
			byte[] bytes = IOUtils.toByteArray(attachment.getResource().getInputStream());
			sendGridAttachment.setContent(Base64Utils.encodeToString(bytes));
			sendGridAttachment.setContentId(attachment.getContentId());
			sendGridAttachment.setDisposition(attachment.getDisposition());
			sendGridAttachment.setFilename(attachment.getResource().getName());
			sendGridAttachment.setType(mimetypeProvider.detect(new ByteArrayInputStream(bytes)).toString());
			sendGridMail.addAttachments(sendGridAttachment);
		} catch (IOException e) {
			throw new AttachmentReadException("Failed to attach email attachment named " + attachment.getResource().getName(), attachment, e);
		} catch (MimeTypeDetectionException e) {
			throw new AttachmentReadException("Failed to determine mimetype for email attachment named " + attachment.getResource().getName(), attachment, e);
		}
	}

	public SendGridClient getDelegate() {
		return delegate;
	}
}
