package fr.sii.ogham.spring.v2.it;

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

import fr.sii.ogham.assertion.OghamInternalAssertions;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.spring.v2.autoconfigure.OghamSpringBoot2AutoConfiguration;

public class OghamSpringBoot2JavaMailAutoConfigurationTests {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	private ApplicationContextRunner contextRunner;

	@Before
	public void setUp() {
		contextRunner = new ApplicationContextRunner()
				.withPropertyValues("mail.smtp.host=ogham", "spring.mail.host=spring");
	}

	@Test
	public void oghamAloneShouldUseOghamProperties() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(OghamSpringBoot2AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			OghamInternalAssertions.assertThat(messagingService)
				.javaMail()
					.host(equalTo("ogham"));
		});
	}

	@Test
	public void oghamWithJavaMailAutoConfigShouldUseSpringProperties() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(MailSenderAutoConfiguration.class, OghamSpringBoot2AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			OghamInternalAssertions.assertThat(messagingService)
				.javaMail()
					.host(equalTo("spring"));
		});
	}

	@Test
	public void oghamWithSpringPropsShouldUseSpringProperties() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(ManuallyEnableSpringPropertiesConfig.class, OghamSpringBoot2AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			OghamInternalAssertions.assertThat(messagingService)
				.javaMail()
					.host(equalTo("spring"));
		});
	}

	@Configuration
	@EnableConfigurationProperties(MailProperties.class)
	protected static class ManuallyEnableSpringPropertiesConfig {

	}
}
