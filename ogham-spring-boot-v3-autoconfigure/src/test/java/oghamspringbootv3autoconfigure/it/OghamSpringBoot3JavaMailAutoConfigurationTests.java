package oghamspringbootv3autoconfigure.it;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.boot.autoconfigure.AutoConfigurations.of;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.spring.v3.autoconfigure.OghamSpringBoot3AutoConfiguration;
import fr.sii.ogham.testing.assertion.OghamInternalAssertions;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class OghamSpringBoot3JavaMailAutoConfigurationTests {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	private ApplicationContextRunner contextRunner;

	@Before
	public void setUp() {
		contextRunner = new ApplicationContextRunner();
	}

	@Test
	public void oghamAloneShouldUseOghamProperties() throws Exception {
		contextRunner = contextRunner
				.withPropertyValues("ogham.email.javamail.host=ogham", "spring.mail.host=spring")
				.withConfiguration(of(OghamSpringBoot3AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			OghamInternalAssertions.assertThat(messagingService)
				.javaMail()
					.host(equalTo("ogham"));
		});
	}

	@Test
	public void oghamWithJavaMailAutoConfigShouldUseSpringProperties() throws Exception {
		contextRunner = contextRunner
				.withPropertyValues("spring.mail.host=spring")
				.withConfiguration(of(MailSenderAutoConfiguration.class, OghamSpringBoot3AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			OghamInternalAssertions.assertThat(messagingService)
				.javaMail()
					.host(equalTo("spring"));
		});
	}

	@Test
	public void oghamWithSpringPropsShouldUseSpringProperties() throws Exception {
		contextRunner = contextRunner
				.withPropertyValues("spring.mail.host=spring")
				.withConfiguration(of(ManuallyEnableSpringPropertiesConfig.class, OghamSpringBoot3AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			OghamInternalAssertions.assertThat(messagingService)
				.javaMail()
					.host(equalTo("spring"));
		});
	}

	@Test
	public void oghamWithJavaMailAutoConfigShouldUseOghamPropertiesPrecedence() throws Exception {
		contextRunner = contextRunner
				.withPropertyValues("ogham.email.javamail.host=ogham", "spring.mail.host=spring")
				.withConfiguration(of(MailSenderAutoConfiguration.class, OghamSpringBoot3AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			OghamInternalAssertions.assertThat(messagingService)
				.javaMail()
					.host(equalTo("ogham"));
		});
	}

	@Test
	public void oghamPropertiesWithSpringPropsShouldUseOghamPropertiesPrecedence() throws Exception {
		contextRunner = contextRunner
				.withPropertyValues("ogham.email.javamail.host=ogham", "spring.mail.host=spring")
				.withConfiguration(of(ManuallyEnableSpringPropertiesConfig.class, OghamSpringBoot3AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			OghamInternalAssertions.assertThat(messagingService)
				.javaMail()
					.host(equalTo("ogham"));
		});
	}

	@Configuration
	@EnableConfigurationProperties(MailProperties.class)
	protected static class ManuallyEnableSpringPropertiesConfig {

	}
}
