package oghamsendgridv4.ut.builder;

import fr.sii.ogham.email.sendgrid.v4.builder.sendgrid.SendGridV4Builder;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.SendGridV4Sender;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client.SendGridClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

/**
 * Test campaign for the {@link SendGridV4Builder} class.
 */
public final class SendGridBuilderTest {

	private SendGridV4Builder instance;

	@BeforeEach
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

		assertNull(val, "The builder returned a sender");
	}

	@Test
	public void build_withApiKey() {
		instance.apiKey("apiKey");

		final SendGridV4Sender val = instance.build();

		assertNotNull(val, "The builder returned no sender");
	}

	@Test
	public void build_withClient() {
		instance.client(mock(SendGridClient.class));

		final SendGridV4Sender val = instance.build();

		assertNotNull(val, "The builder returned no sender");
	}

}
