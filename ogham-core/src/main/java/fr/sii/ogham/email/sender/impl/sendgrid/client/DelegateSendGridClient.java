package fr.sii.ogham.email.sender.impl.sendgrid.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGridException;

/**
 * Facade wrapping the {@link SendGrid} object.
 */
public final class DelegateSendGridClient implements SendGridClient {

	private static final Logger LOG = LoggerFactory.getLogger(DelegateSendGridClient.class);

	private SendGrid delegate;

	/**
	 * The account username
	 */
	private String username;

	/**
	 * The account password
	 */
	private String password;

	/**
	 * The API key
	 */
	private String apiKey;

	/**
	 * Constructor.
	 * 
	 * @param delegate
	 *            the entry point to the SendGrid library
	 * @throws NullPointerException
	 *             if provided delegate is null
	 */
	public DelegateSendGridClient(final SendGrid delegate) {
		if (delegate == null) {
			throw new NullPointerException("[delegate] cannot be null");
		}

		this.delegate = delegate;
	}

	public DelegateSendGridClient(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public DelegateSendGridClient(String apiKey) {
		super();
		this.apiKey = apiKey;
	}

	@Override
	public void send(final Email email) throws SendGridException {
		if (email == null) {
			throw new NullPointerException("[email] cannot be null");
		}

		LOG.debug("Sending to SendGrid client: FROM {}<{}>", email.getFromName(), email.getFrom());
		LOG.debug("Sending to SendGrid client: TO {} (as {})", email.getTos(), email.getToNames());
		LOG.debug("Sending to SendGrid client: SUBJECT {}", email.getSubject());
		LOG.debug("Sending to SendGrid client: TEXT CONTENT {}", email.getText());
		LOG.debug("Sending to SendGrid client: HTML CONTENT {}", email.getHtml());

		if (delegate == null) {
			if(username!=null && password!=null) {
				delegate = new SendGrid(username, password);
			} else if(apiKey!=null) {
				delegate = new SendGrid(apiKey);
			}
		}
		final SendGrid.Response response = delegate.send(email);

		if (response.getStatus()) {
			LOG.debug("Response from SendGrid client: ({}) {}", response.getCode(), response.getMessage());
		} else {
			throw new SendGridException(new IOException("Sending to SendGrid failed: (" + response.getCode() + ") " + response.getMessage()));
		}
	}

}
