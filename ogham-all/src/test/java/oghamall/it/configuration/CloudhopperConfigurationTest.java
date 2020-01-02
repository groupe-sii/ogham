package oghamall.it.configuration;

import static fr.sii.ogham.testing.assertion.OghamInternalAssertions.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.builder.cloudhopper.CloudhopperBuilder;

public class CloudhopperConfigurationTest {
	@Test
	public void asDeveloperIDefineHostUsingProperties() {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "smsprovider.com");
		MessagingService service = builder.build();
		assertThat(service)
			.cloudhopper()
				.host(is("smsprovider.com"));
	}
	
	@Test
	public void asDeveloperIDefineHostInMyOwnCode() {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("ogham.sms.smpp.host", "localhost");
		builder
			.sms()
				.sender(CloudhopperBuilder.class)
					.host("smsprovider.com");
		MessagingService service = builder.build();
		assertThat(service)
			.cloudhopper()
				.host(is("smsprovider.com"));
	}
}
