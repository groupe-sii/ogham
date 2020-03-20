package fr.sii.ogham.email.sendgrid.v4.sender.impl;

import static fr.sii.ogham.core.util.LogUtils.logString;
import static fr.sii.ogham.email.sendgrid.SendGridConstants.DEFAULT_SENDGRID_IMPLEMENTATION_PRIORITY;
import static fr.sii.ogham.email.sendgrid.sender.EmailValidator.validate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Personalization;

import fr.sii.ogham.core.builder.priority.Priority;
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
@Priority(properties = "${ogham.email.implementation-priority.sendgrid}", defaultValue = DEFAULT_SENDGRID_IMPLEMENTATION_PRIORITY)
public final class SendGridV4Sender extends AbstractSpecializedSender<Email> implements SendGridSender {
	private static final Logger LOG = LoggerFactory.getLogger(SendGridV4Sender.class);
	private static final Pattern CID = Pattern.compile("^<(.+)>$");

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
		validate(message);

		try {
			LOG.debug("Preparing to send email using SendGrid: {}", message);
			final Mail sgEmail = intercept(toSendGridEmail(message), message);

			LOG.debug("Sending email {}", logString(message));
			LOG.trace("SendGrid email: {}", sgEmail);
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

	private Mail toSendGridEmail(final Email message) throws ContentHandlerException, AttachmentReadException {
		final Mail sendGridMail = new Mail();
		sendGridMail.setSubject(message.getSubject());

		sendGridMail.setFrom(new com.sendgrid.helpers.mail.objects.Email(message.getFrom().getAddress(), message.getFrom().getPersonal()));

		sendGridMail.addPersonalization(toPersonalization(message));

		handler.setContent(message, sendGridMail, message.getContent());

		for (Attachment attachment : message.getAttachments()) {
			addAttachment(sendGridMail, attachment);
		}

		return sendGridMail;
	}

	private static Personalization toPersonalization(final Email message) {
		Personalization personalization = new Personalization();
		for (Recipient recipient : message.getRecipients()) {
			addRecipient(personalization, recipient);
		}
		return personalization;
	}

	private static void addRecipient(Personalization personalization, Recipient recipient) {
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
			sendGridAttachment.setContentId(toCid(attachment.getContentId()));
			sendGridAttachment.setDisposition(attachment.getDisposition());
			sendGridAttachment.setFilename(attachment.getResource().getName());
			sendGridAttachment.setType(getMimetype(attachment, bytes));
			sendGridMail.addAttachments(sendGridAttachment);
		} catch (IOException e) {
			throw new AttachmentReadException("Failed to attach email attachment named " + attachment.getResource().getName(), attachment, e);
		} catch (MimeTypeDetectionException e) {
			throw new AttachmentReadException("Failed to determine mimetype for email attachment named " + attachment.getResource().getName(), attachment, e);
		}
	}

	private String getMimetype(Attachment attachment, byte[] bytes) throws MimeTypeDetectionException {
		if (attachment.getContentType() != null) {
			return attachment.getContentType();
		}
		return mimetypeProvider.detect(new ByteArrayInputStream(bytes)).toString();
	}

	private static String toCid(final String contentId) {
		if (contentId == null) {
			return null;
		}
		return CID.matcher(contentId).replaceAll("$1");
	}

	public SendGridClient getDelegate() {
		return delegate;
	}
}
