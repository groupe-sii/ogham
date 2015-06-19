package fr.sii.ogham.ut.email.builder;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import fr.sii.ogham.email.builder.SendGridBuilder;
import fr.sii.ogham.email.sender.impl.SendGridSender;
import fr.sii.ogham.email.sender.impl.sendgrid.client.SendGridClient;

/**
 * Test campaign for the {@link SendGridBuilder} class.
 */
public final class SendGridBuilderTest {

	private SendGridBuilder instance;

	@Before
	public void setUp() {
		instance = new SendGridBuilder();
	}

	@Test
	public void build_withCredentials() {
		instance.withCredentials("username", "password");

		final SendGridSender val = instance.build();

		assertNotNull("The builder returned no sender", val);
	}

	@Test
	public void build_withApiKey() {
		instance.withApiKey("apiKey");

		final SendGridSender val = instance.build();

		assertNotNull("The builder returned no sender", val);
	}

	@Test
	public void build_withClient() {
		instance.withClient(mock(SendGridClient.class));

		final SendGridSender val = instance.build();

		assertNotNull("The builder returned no sender", val);
	}

}
