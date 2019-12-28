package fr.sii.ogham.assertion.internal;

import static fr.sii.ogham.assertion.AssertionHelper.assertThat;
import static org.apache.commons.lang3.reflect.FieldUtils.readField;

import org.hamcrest.Matcher;

import com.sendgrid.SendGrid;

import fr.sii.ogham.assertion.HasParent;
import fr.sii.ogham.email.sendgrid.sender.SendGridSender;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.SendGridV2Sender;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.SendGridClient;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.SendGridV4Sender;

/**
 * Helper to make assertions on SendGrid instance created by Ogham.
 * 
 * @author Aurélien Baudet
 *
 */
public class SendGridAssertions extends HasParent<MessagingServiceAssertions> {
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

	private String getApiKey(SendGridSender sendGridSender) {
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

	private SendGrid getClient(SendGridSender sendGridSender) {
		try {
			if (sendGridSender instanceof SendGridV2Sender) {
				SendGridClient wrapper = (SendGridClient) readField(sendGridSender, "delegate", true);
				return (SendGrid) readField(wrapper, "delegate", true);
			} else if (sendGridSender instanceof SendGridV4Sender) {
				fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridClient wrapper = (fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridClient) readField(
						sendGridSender, "delegate", true);
				return (SendGrid) readField(wrapper, "delegate", true);
			}
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to read 'delegate' of SendGridClient", e);
		}
		return null;
	}
}
