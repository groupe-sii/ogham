package fr.sii.ogham.spring.v1.it;

import static org.hamcrest.Matchers.equalTo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import fr.sii.ogham.assertion.OghamInternalAssertions;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.junit.LoggingTestRule;
import fr.sii.ogham.spring.v1.autoconfigure.OghamSpringBoot1AutoConfiguration;

public class OghamSpringBoot1JavaMailAutoConfigurationTests {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	private AnnotationConfigApplicationContext context;

	@Before
	public void setUp() {
		context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, "mail.smtp.host=ogham", "spring.mail.host=spring");
	}

	@After
	public void tearDown() {
		if (context != null) {
			context.close();
		}
	}

	@Test
	public void oghamAloneShouldUseOghamProperties() throws Exception {
		context.register(OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		OghamInternalAssertions.assertThat(messagingService)
			.javaMail()
				.host(equalTo("ogham"));
	}

	@Test
	public void oghamWithJavaMailAutoConfigShouldUseSpringProperties() throws Exception {
		context.register(MailSenderAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		OghamInternalAssertions.assertThat(messagingService)
			.javaMail()
				.host(equalTo("spring"));
	}

	@Test
	public void oghamWithSpringPropsShouldUseSpringProperties() throws Exception {
		context.register(ManuallyEnableSpringPropertiesConfig.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		OghamInternalAssertions.assertThat(messagingService)
			.javaMail()
				.host(equalTo("spring"));
	}

	@Configuration
	@EnableConfigurationProperties(MailProperties.class)
	protected static class ManuallyEnableSpringPropertiesConfig {

	}
}
