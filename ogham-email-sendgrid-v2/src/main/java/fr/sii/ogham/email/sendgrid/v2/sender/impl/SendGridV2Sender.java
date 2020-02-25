package fr.sii.ogham.email.sendgrid.v2.sender.impl;

import static fr.sii.ogham.core.util.LogUtils.summarize;
import static fr.sii.ogham.email.sendgrid.sender.EmailValidator.validate;

import java.io.IOException;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.sender.AbstractSpecializedSender;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.sendgrid.ContentHandlerException;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.email.message.Recipient;
import fr.sii.ogham.email.sendgrid.sender.SendGridSender;
import fr.sii.ogham.email.sendgrid.sender.exception.AttachmentReadException;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.SendGridInterceptor;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.handler.SendGridContentHandler;

/**
 * SendGrid-backed implementation of the email sender.
 */
public final class SendGridV2Sender extends AbstractSpecializedSender<Email> implements SendGridSender {
	private static final Logger LOG = LoggerFactory.getLogger(SendGridV2Sender.class);
	private static final Pattern CID = Pattern.compile("^<(.+)>$");

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
	public SendGridV2Sender(final SendGridClient service, final SendGridContentHandler handler) {
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
	public SendGridV2Sender(final SendGridClient service, final SendGridContentHandler handler, SendGridInterceptor interceptor) {
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
		validate(message);

		try {
			LOG.debug("Preparing to send email using SendGrid: {}", summarize(message));
			final SendGrid.Email sgEmail = intercept(toSendGridEmail(message), message);

			LOG.debug("Sending email...\n{}", summarize(message));
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

	private SendGrid.Email intercept(SendGrid.Email sendGridEmail, Email source) {
		if (interceptor == null) {
			return sendGridEmail;
		}
		return interceptor.intercept(sendGridEmail, source);
	}

	private SendGrid.Email toSendGridEmail(final Email message) throws ContentHandlerException, AttachmentReadException {
		final SendGrid.Email ret = new SendGrid.Email();
		ret.setSubject(message.getSubject());

		ret.setFrom(message.getFrom().getAddress());
		ret.setFromName(message.getFrom().getPersonal() == null ? "" : message.getFrom().getPersonal());

		final String[] tos = new String[message.getRecipients().size()];
		final String[] toNames = new String[message.getRecipients().size()];
		int i = 0;
		for (Recipient recipient : message.getRecipients()) {
			final EmailAddress address = recipient.getAddress();
			tos[i] = address.getAddress();
			toNames[i] = address.getPersonal() == null ? "" : address.getPersonal();
			i++;
		}
		ret.setTo(tos);
		ret.setToName(toNames);

		handler.setContent(message, ret, message.getContent());

		for (Attachment attachment : message.getAttachments()) {
			addAttachment(ret, attachment);
		}

		return ret;
	}

	private static void addAttachment(final SendGrid.Email ret, final Attachment attachment) throws AttachmentReadException {
		try {
			ret.addAttachment(attachment.getResource().getName(), attachment.getResource().getInputStream());
			// TODO: how to set Content-Type per attachment with SendGrid v2 API ?
			if (attachment.getContentId() != null) {
				String id = CID.matcher(attachment.getContentId()).replaceAll("$1");
				ret.addContentId(attachment.getResource().getName(), id);
			}
		} catch (IOException e) {
			throw new AttachmentReadException("Failed to attach email attachment named " + attachment.getResource().getName(), attachment, e);
		}
	}

	public SendGridClient getDelegate() {
		return delegate;
	}

}
