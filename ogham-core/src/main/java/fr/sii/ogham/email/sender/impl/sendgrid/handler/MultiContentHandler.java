package fr.sii.ogham.email.sender.impl.sendgrid.handler;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.email.exception.sendgrid.ContentHandlerException;

/**
 * Content handler for {@link MultiContent} instances. All it does is, for each
 * content contained within the instance, it delegates processing to an injected
 * content handler.
 *
 * @see fr.sii.ogham.email.sender.impl.javamail.MultiContentHandler
 */
public final class MultiContentHandler implements SendGridContentHandler {

	private final SendGridContentHandler delegate;

	/**
	 * Constructor.
	 * 
	 * @param delegate
	 *            the underlying content handler to delegate processing of each
	 *            of the contents contained within a {@link MultiContent}
	 *            instance
	 */
	public MultiContentHandler(final SendGridContentHandler delegate) {
		if (delegate == null) {
			throw new NullPointerException("[delegate] cannot be null");
		}

		this.delegate = delegate;
	}

	/**
	 * Reads the content and adds it into the email. This method is expected to
	 * update the content of the {@code email} parameter.
	 * 
	 * While the method signature accepts any {@link Content} instance as
	 * parameter, the method will fail if anything other than a
	 * {@link MultiContent} is provided.
	 * 
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
	public void setContent(final SendGrid.Email email, final Content content) throws ContentHandlerException {
		if (email == null) {
			throw new NullPointerException("[email] cannot be null");
		}
		if (content == null) {
			throw new NullPointerException("[content] cannot be null");
		}

		if (content instanceof MultiContent) {
			for (Content subContent : ((MultiContent) content).getContents()) {
				delegate.setContent(email, subContent);
			}
		} else {
			throw new IllegalArgumentException("This instance can only work with MultiContent instances, but was passed " + content.getClass().getSimpleName());
		}
	}

}
