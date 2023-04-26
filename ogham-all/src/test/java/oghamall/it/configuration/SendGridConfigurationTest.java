package oghamall.it.configuration;

import static fr.sii.ogham.testing.assertion.OghamInternalAssertions.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.sendgrid.v4.builder.sendgrid.SendGridV4Builder;

public class SendGridConfigurationTest {
	@Test
	public void asDeveloperIDefineApiKeyUsingProperties() {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.email.sendgrid.api-key", "api-key");
		MessagingService service = builder.build();
		assertThat(service)
			.sendGrid()
				.apiKey(is("api-key"));
	}
	
	@Test
	public void asDeveloperIDefineApiKeyInMyOwnCode() {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.email.sendgrid.api-key", "foo");
		builder
			.email()
				.sender(SendGridV4Builder.class)
					.apiKey("api-key");
		MessagingService service = builder.build();
		assertThat(service)
			.sendGrid()
				.apiKey(is("api-key"));
	}
}
