package fr.sii.ogham.testing.assertion.internal;

import static fr.sii.ogham.testing.assertion.AssertionHelper.assertThat;
import static fr.sii.ogham.testing.assertion.internal.helper.ImplementationFinder.findSender;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;

import org.hamcrest.Matcher;

import com.sendgrid.SendGrid;

import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.sendgrid.sender.SendGridSender;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.SendGridV2Sender;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.SendGridV4Sender;
import fr.sii.ogham.testing.assertion.HasParent;

/**
 * Helper to make assertions on SendGrid instance created by Ogham.
 * 
 * @author Aurélien Baudet
 *
 */
public class SendGridAssertions extends HasParent<MessagingServiceAssertions> {
	private static final String DELEGATE_FIELD = "delegate";
	private final SendGridSender sendGridSender;

	public SendGridAssertions(MessagingServiceAssertions parent, SendGridSender sendGridSender) {
		super(parent);
		this.sendGridSender = sendGridSender;
	}

	/**
	 * Ensures that SendGrid apiKey is correctly configured.
	 * 
	 * @param matcher
	 *            the matcher used to ensure that apiKey is correctly
	 *            configured.
	 * @return this instance for fluent chaining
	 */
	public SendGridAssertions apiKey(Matcher<String> matcher) {
		assertThat(getApiKey(sendGridSender), matcher);
		return this;
	}

	/**
	 * Ensures that SendGrid instance is correctly configured.
	 * 
	 * @param matcher
	 *            the matcher used to ensure that instance is correctly
	 *            configured.
	 * @param <T>
	 *            the assertion type for chaining
	 * @return this instance for fluent chaining
	 */
	public <T> SendGridAssertions client(Matcher<? super SendGrid> matcher) {
		assertThat(getClient(sendGridSender), matcher);
		return this;
	}

	/**
	 * Find the {@link SendGridSender} instance (one of {@link SendGridV2Sender}
	 * or {@link SendGridV4Sender}).
	 * 
	 * @param messagingService
	 *            the messaging service
	 * @param senderClass
	 *            which of {@link SendGridV2Sender} or {@link SendGridV4Sender}
	 * @return the found instance
	 */
	public static SendGridSender getSendGridSender(MessagingService messagingService, Class<? extends SendGridSender> senderClass) {
		return findSender(messagingService, senderClass);
	}

	/**
	 * Find the {@link SendGridSender} instance (one of {@link SendGridV2Sender}
	 * or {@link SendGridV4Sender}) based on the classpath. If
	 * {@link SendGridV4Sender} sender is found, return this instance. If not
	 * found, tried to get {@link SendGridV2Sender} instance.
	 * 
	 * @param messagingService the messaging service
	 * @return the found instance
	 */
	public static SendGridSender getSendGridSender(MessagingService messagingService) {
		try {
			return findSender(messagingService, SendGridV4Sender.class);
		} catch (IllegalStateException e) { // NOSONAR
			// skip
		}
		try {
			return findSender(messagingService, SendGridV2Sender.class);
		} catch (IllegalStateException e) { // NOSONAR
			// skip
		}
		throw new IllegalStateException("No SendGridSender available");
	}

	private static String getApiKey(SendGridSender sendGridSender) {
		SendGrid client = getClient(sendGridSender);
		try {
			if (sendGridSender instanceof SendGridV2Sender) {
				return (String) readField(client, "password", true);
			}
			return (String) readField(client, "apiKey", true);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to read 'apiKey' of SendGrid", e);
		}
	}

	private static SendGrid getClient(SendGridSender sendGridSender) {
		try {
			if (sendGridSender instanceof SendGridV2Sender) {
				SendGridClient wrapper = (SendGridClient) readField(sendGridSender, DELEGATE_FIELD, true);
				return (SendGrid) readField(wrapper, DELEGATE_FIELD, true);
			} else if (sendGridSender instanceof SendGridV4Sender) {
				fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridClient wrapper = (fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridClient) readField(
						sendGridSender, DELEGATE_FIELD, true);
				return (SendGrid) readField(wrapper, DELEGATE_FIELD, true);
			}
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to read 'delegate' of SendGridClient", e);
		}
		return null;
	}
}
