package oghamsendgridv2.ut.builder;

import fr.sii.ogham.email.sendgrid.v2.builder.sendgrid.SendGridV2Builder;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.SendGridV2Sender;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.sendgrid.client.SendGridClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Test campaign for the {@link SendGridV2Builder} class.
 */
public final class SendGridBuilderTest {

	private SendGridV2Builder instance;

	@BeforeEach
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

		assertNotNull(val, "The builder returned no sender");
	}

	@Test
	public void build_withApiKey() {
		instance.apiKey("apiKey");

		final SendGridV2Sender val = instance.build();

		assertNotNull(val, "The builder returned no sender");
	}

	@Test
	public void build_withClient() {
		instance.client(mock(SendGridClient.class));

		final SendGridV2Sender val = instance.build();

		assertNotNull(val, "The builder returned no sender");
	}

}
