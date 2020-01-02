package oghamsendgridv4.ut.builder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import fr.sii.ogham.email.sendgrid.v4.builder.sendgrid.SendGridV4Builder;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.SendGridV4Sender;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridClient;

/**
 * Test campaign for the {@link SendGridV4Builder} class.
 */
public final class SendGridBuilderTest {

	private SendGridV4Builder instance;

	@Before
	public void setUp() {
		instance = new SendGridV4Builder()
				.mimetype()
					.tika()
						.failIfOctetStream(false)
						.and()
					.and();
	}

	@Test
	@SuppressWarnings("deprecation")
	public void build_withCredentials() {
		instance.username("username");
		instance.password("password");

		final SendGridV4Sender val = instance.build();

		assertNull("The builder returned a sender", val);
	}

	@Test
	public void build_withApiKey() {
		instance.apiKey("apiKey");

		final SendGridV4Sender val = instance.build();

		assertNotNull("The builder returned no sender", val);
	}

	@Test
	public void build_withClient() {
		instance.client(mock(SendGridClient.class));

		final SendGridV4Sender val = instance.build();

		assertNotNull("The builder returned no sender", val);
	}

}
