package fr.sii.ogham.ut.email.sendgrid.builder;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import fr.sii.ogham.email.sendgrid.v2.builder.sendgrid.SendGridV2Builder;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.SendGridV2Sender;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.SendGridClient;

/**
 * Test campaign for the {@link SendGridV2Builder} class.
 */
public final class SendGridBuilderTest {

	private SendGridV2Builder instance;

	@Before
	public void setUp() {
		instance = new SendGridV2Builder()
				.mimetype()
					.tika()
						.failIfOctetStream(false)
						.and()
					.and();
	}

	@Test
	public void build_withCredentials() {
		instance.username("username");
		instance.password("password");

		final SendGridV2Sender val = instance.build();

		assertNotNull("The builder returned no sender", val);
	}

	@Test
	public void build_withApiKey() {
		instance.apiKey("apiKey");

		final SendGridV2Sender val = instance.build();

		assertNotNull("The builder returned no sender", val);
	}

	@Test
	public void build_withClient() {
		instance.client(mock(SendGridClient.class));

		final SendGridV2Sender val = instance.build();

		assertNotNull("The builder returned no sender", val);
	}

}
