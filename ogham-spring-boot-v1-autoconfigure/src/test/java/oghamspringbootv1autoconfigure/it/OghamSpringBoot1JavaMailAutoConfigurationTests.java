package oghamspringbootv1autoconfigure.it;

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

import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.spring.v1.autoconfigure.OghamSpringBoot1AutoConfiguration;
import fr.sii.ogham.testing.assertion.OghamInternalAssertions;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class OghamSpringBoot1JavaMailAutoConfigurationTests {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	private AnnotationConfigApplicationContext context;

	@Before
	public void setUp() {
		context = new AnnotationConfigApplicationContext();
	}

	@After
	public void tearDown() {
		if (context != null) {
			context.close();
		}
	}

	@Test
	public void oghamAloneShouldUseOghamProperties() throws Exception {
		EnvironmentTestUtils.addEnvironment(context, "ogham.email.javamail.host=ogham", "spring.mail.host=spring");
		context.register(OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		OghamInternalAssertions.assertThat(messagingService)
			.javaMail()
				.host(equalTo("ogham"));
	}

	@Test
	public void oghamWithJavaMailAutoConfigShouldUseSpringProperties() throws Exception {
		EnvironmentTestUtils.addEnvironment(context, "spring.mail.host=spring");
		context.register(MailSenderAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		OghamInternalAssertions.assertThat(messagingService)
			.javaMail()
				.host(equalTo("spring"));
	}

	@Test
	public void oghamWithSpringPropsShouldUseSpringProperties() throws Exception {
		EnvironmentTestUtils.addEnvironment(context, "spring.mail.host=spring");
		context.register(ManuallyEnableSpringPropertiesConfig.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		OghamInternalAssertions.assertThat(messagingService)
			.javaMail()
				.host(equalTo("spring"));
	}

	@Test
	public void oghamPropertiesWithJavaMailAutoConfigShouldUseOghamPropertiesPrecedence() throws Exception {
		EnvironmentTestUtils.addEnvironment(context, "ogham.email.javamail.host=ogham", "spring.mail.host=spring");
		context.register(MailSenderAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		OghamInternalAssertions.assertThat(messagingService)
			.javaMail()
				.host(equalTo("ogham"));
	}

	@Test
	public void oghamPropertiesWithSpringPropsShouldUseOghamPropertiesPrecedence() throws Exception {
		EnvironmentTestUtils.addEnvironment(context, "ogham.email.javamail.host=ogham", "spring.mail.host=spring");
		context.register(ManuallyEnableSpringPropertiesConfig.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		OghamInternalAssertions.assertThat(messagingService)
			.javaMail()
				.host(equalTo("ogham"));
	}

	@Configuration
	@EnableConfigurationProperties(MailProperties.class)
	protected static class ManuallyEnableSpringPropertiesConfig {

	}
}
