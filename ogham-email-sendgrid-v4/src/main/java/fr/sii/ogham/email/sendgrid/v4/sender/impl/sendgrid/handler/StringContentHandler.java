package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.helpers.mail.Mail;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MayHaveStringContent;
import fr.sii.ogham.core.message.content.StringContent;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.email.exception.handler.ContentHandlerException;
import fr.sii.ogham.email.message.Email;

/**
 * Content handler that puts plain text or HTML content into email to be sent
 * through SendGrid. MIME type detection is delegated to an instance of
 * {@link MimeTypeProvider}.
 */
public final class StringContentHandler implements SendGridContentHandler {

	private static final Logger LOG = LoggerFactory.getLogger(StringContentHandler.class);

	private final MimeTypeProvider mimeProvider;

	/**
	 * Constructor.
	 * 
	 * @param mimeProvider
	 *            an object in charge of determining the MIME type of the
	 *            messages to send
	 */
	public StringContentHandler(final MimeTypeProvider mimeProvider) {
		if (mimeProvider == null) {
			throw new IllegalArgumentException("[mimeProvider] cannot be null");
		}

		this.mimeProvider = mimeProvider;
	}

	/**
	 * Reads the content and adds it into the email. This method is expected to
	 * update the content of the {@code email} parameter.
	 * 
	 * While the method signature accepts any {@link Content} instance as
	 * parameter, the method will fail if anything other than a
	 * {@link StringContent} is provided.
	 * 
	 * @param original
	 *            the original Ogham email
	 * @param email
	 *            the email to put the content in
	 * @param content
	 *            the unprocessed content
	 * @throws ContentHandlerException
	 *             the handler is unable to add the content to the email
	 * @throws IllegalArgumentException
	 *             the content provided is not of the right type
	 */
	@Override
	public void setContent(final Email original, final Mail email, final Content content) throws ContentHandlerException {
		if (email == null) {
			throw new IllegalArgumentException("[email] cannot be null");
		}
		if (content == null) {
			throw new IllegalArgumentException("[content] cannot be null");
		}

		if (content instanceof MayHaveStringContent) {
			final String contentStr = ((MayHaveStringContent) content).asString();

			try {
				final String mime = mimeProvider.detect(contentStr).toString();
				LOG.debug("Email content has detected type {}", mime);
				LOG.trace("content: {}", content);
				setMimeContent(email, contentStr, mime);
			} catch (MimeTypeDetectionException e) {
				throw new ContentHandlerException("Unable to set the email content", content, e);
			}
		} else {
			throw new IllegalArgumentException("This instance can only work with MayHaveStringContent instances, but was passed " + content.getClass().getSimpleName());
		}

	}

	private static void setMimeContent(final Mail email, final String contentStr, final String mime) {
		com.sendgrid.helpers.mail.objects.Content content = new com.sendgrid.helpers.mail.objects.Content(mime, contentStr);
		email.addContent(content);
	}

}
