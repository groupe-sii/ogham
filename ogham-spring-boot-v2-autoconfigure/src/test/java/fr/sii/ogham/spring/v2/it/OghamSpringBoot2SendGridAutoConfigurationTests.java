package fr.sii.ogham.spring.v2.it;

import static fr.sii.ogham.assertion.OghamInternalAssertions.isSpringBeanInstance;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.boot.autoconfigure.AutoConfigurations.of;

import java.nio.charset.StandardCharsets;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration;
import org.springframework.boot.autoconfigure.sendgrid.SendGridProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.sendgrid.SendGrid;

import fr.sii.ogham.assertion.OghamAssertions;
import fr.sii.ogham.assertion.OghamInternalAssertions;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.helper.sms.rule.JsmppServerRule;
import fr.sii.ogham.helper.sms.rule.SmppServerRule;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.spring.v2.autoconfigure.OghamSpringBoot2AutoConfiguration;

public class OghamSpringBoot2SendGridAutoConfigurationTests {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	private ApplicationContextRunner contextRunner;
	
	@Before
	public void setUp() {
		contextRunner = new ApplicationContextRunner()
				.withPropertyValues( 
						"mail.smtp.host="+ServerSetupTest.SMTP.getBindAddress(), 
						"mail.smtp.port="+ServerSetupTest.SMTP.getPort(),
						"ogham.sms.smpp.host=127.0.0.1",
						"ogham.sms.smpp.port="+smppServer.getPort(),
						"ogham.email.sendgrid.api-key=bar",
						"spring.sendgrid.api-key=foo",
						"ogham.freemarker.default-encoding="+StandardCharsets.US_ASCII.name(),
						"spring.freemarker.charset="+StandardCharsets.UTF_16BE.name());
	}


	@Test
	public void oghamWithSendGridAutoConfigShouldUseSpringSendGridClient() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(SendGridAutoConfiguration.class, OghamSpringBoot2AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			checkEmail(messagingService);
			checkSms(messagingService);
			OghamInternalAssertions.assertThat(messagingService)
				.sendGrid()
					.apiKey(equalTo("foo"))
					.client(isSpringBeanInstance(context, SendGrid.class));
		});
	}

	@Test
	public void oghamWithoutSendGridAutoConfigShouldUseOghamSendGridClientWithSpringProperties() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(ManuallyEnableSendGridPropertiesConfiguration.class, OghamSpringBoot2AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			checkEmail(messagingService);
			checkSms(messagingService);
			OghamInternalAssertions.assertThat(messagingService)
				.sendGrid()
					.apiKey(equalTo("foo"))
					.client(not(isSpringBeanInstance(context, SendGrid.class)));
		});
	}
	
	@Test
	public void useCustomSendGridBean() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(CustomSendGridConfig.class, SendGridAutoConfiguration.class, OghamSpringBoot2AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			checkEmail(messagingService);
			checkSms(messagingService);
			OghamInternalAssertions.assertThat(messagingService)
				.sendGrid()
					.apiKey(nullValue(String.class))
					.client(allOf(isA(CustomSendGrid.class), isSpringBeanInstance(context, SendGrid.class)));
		});
	}


	@Configuration
	@EnableConfigurationProperties(SendGridProperties.class)
	protected static class ManuallyEnableSendGridPropertiesConfiguration {
	}

	@Configuration
	protected static class CustomSendGridConfig {
		@Bean
		public SendGrid sendGrid() {
			return new CustomSendGrid();
		}
	}

	private static class CustomSendGrid extends SendGrid {
		public CustomSendGrid() {
			super(null);
		}
	}

	private void checkSms(MessagingService messagingService) throws MessagingException {
		messagingService.send(new Sms()
				.from("+33102030405")
				.to("+33123456789")
				.content("hello"));
		OghamAssertions.assertThat(smppServer).receivedMessages().count(equalTo(1));
	}

	private void checkEmail(MessagingService messagingService) throws MessagingException {
		messagingService.send(new Email()
				.from("foo@yopmail.com")
				.to("bar@yopmail.com")
				.subject("test")
				.content("hello"));
		OghamAssertions.assertThat(greenMail).receivedMessages().count(equalTo(1));
	}
}
