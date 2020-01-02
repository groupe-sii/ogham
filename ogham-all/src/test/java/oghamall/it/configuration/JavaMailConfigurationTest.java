package oghamall.it.configuration;

import static fr.sii.ogham.testing.assertion.OghamInternalAssertions.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.builder.javamail.JavaMailBuilder;

public class JavaMailConfigurationTest {
	@Test
	public void asDeveloperIDefineHostUsingProperties() {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("mail.smtp.host", "smtp.gmail.com");
		MessagingService service = builder.build();
		assertThat(service)
			.javaMail()
				.host(is("smtp.gmail.com"));
	}
	
	@Test
	public void asDeveloperIDefineHostInMyOwnCode() {
		MessagingBuilder builder = MessagingBuilder.standard();
		builder
			.environment()
				.properties()
					.set("mail.smtp.host", "localhost");
		builder
			.email()
				.sender(JavaMailBuilder.class)
					.host("smtp.gmail.com");
		MessagingService service = builder.build();
		assertThat(service)
			.javaMail()
				.host(is("smtp.gmail.com"));
	}
}
