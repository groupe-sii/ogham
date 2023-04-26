package oghamspringbootv1autoconfigure.it;

import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.spring.v1.autoconfigure.OghamSpringBoot1AutoConfiguration;
import fr.sii.ogham.testing.assertion.OghamInternalAssertions;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.Matchers.equalTo;

@LogTestInformation
@ExtendWith(SpringExtension.class)
public class OghamSpringBoot1JavaMailAutoConfigurationTests {
	private AnnotationConfigApplicationContext context;

	@BeforeEach
	public void setUp() {
		context = new AnnotationConfigApplicationContext();
	}

	@AfterEach
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
