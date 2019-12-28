package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridAPI;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;

import fr.sii.ogham.email.sendgrid.sender.exception.SendGridException;

/**
 * Facade wrapping the {@link SendGrid} object.
 */
public final class DelegateSendGridClient implements SendGridClient {

	private static final Logger LOG = LoggerFactory.getLogger(DelegateSendGridClient.class);

	private SendGridAPI delegate;

	/**
	 * Constructor.
	 * 
	 * @param delegate
	 *            the entry point to the SendGrid library
	 * @throws IllegalArgumentException
	 *             if provided delegate is null
	 */
	public DelegateSendGridClient(final SendGridAPI delegate) {
		if (delegate == null) {
			throw new IllegalArgumentException("[delegate] cannot be null");
		}

		this.delegate = delegate;
	}

	@Override
	public void send(final Mail email) throws SendGridException {
		if (email == null) {
			throw new IllegalArgumentException("[email] cannot be null");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Sending to SendGrid client: FROM {}", debug(email.getFrom()));
			LOG.debug("Sending to SendGrid client: TO {}", debug(email));
			LOG.debug("Sending to SendGrid client: SUBJECT {}", email.getSubject());
		}

		final Response response = callApi(email);

		if (isSuccess(response.getStatusCode())) {
			LOG.debug("Response from SendGrid client: ({}) {}", response.getStatusCode(), response.getBody());
		} else {
			throw new SendGridException(new IOException("Sending to SendGrid failed: (" + response.getStatusCode() + ") " + response.getBody()));
		}
	}

	private Response callApi(final Mail email) throws SendGridException {
		try {
			Request request = prepareRequest(email);
			return delegate.api(request);
		} catch (IOException e) {
			throw new SendGridException("Sending email to SendGrid failed", e);
		}
	}

	private static Request prepareRequest(final Mail email) throws SendGridException {
		Request request = new Request();
		request.setMethod(Method.POST);
		request.setEndpoint("mail/send");
		try {
			request.setBody(email.build());
		} catch (IOException e) {
			throw new SendGridException("Preparing email for SendGrid failed", e);
		}
		return request;
	}

	private static boolean isSuccess(int statusCode) {
		return statusCode >= 200 && statusCode < 300;
	}

	private static String debug(Email address) {
		if (address == null) {
			return null;
		}
		if (address.getName() != null) {
			return address.getName() + "<" + address.getEmail() + ">";
		}
		return address.getEmail();
	}

	private static List<String> debug(final Mail email) {
		if (email.getPersonalization() == null) {
			return null; // NOSONAR
		}
		// @formatter:off
		return email.getPersonalization()
				.stream()
				.flatMap(p -> p.getTos() == null ? Stream.empty() : p.getTos().stream())
				.map(DelegateSendGridClient::debug)
				.collect(toList());
		// @formatter:on
	}
}
