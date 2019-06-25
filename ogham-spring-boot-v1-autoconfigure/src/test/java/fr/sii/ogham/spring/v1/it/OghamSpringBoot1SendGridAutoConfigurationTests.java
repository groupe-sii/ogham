package fr.sii.ogham.spring.v1.it;

import static fr.sii.ogham.assertion.OghamInternalAssertions.isSpringBeanInstance;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.nio.charset.StandardCharsets;

import org.jsmpp.bean.SubmitSm;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration;
import org.springframework.boot.autoconfigure.sendgrid.SendGridProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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
import fr.sii.ogham.spring.v1.autoconfigure.OghamSpringBoot1AutoConfiguration;

public class OghamSpringBoot1SendGridAutoConfigurationTests {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	private AnnotationConfigApplicationContext context;
	
	@Before
	public void setUp() {
		context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, 
				"mail.smtp.host="+ServerSetupTest.SMTP.getBindAddress(), 
				"mail.smtp.port="+ServerSetupTest.SMTP.getPort(),
				"ogham.sms.smpp.host=127.0.0.1",
				"ogham.sms.smpp.port="+smppServer.getPort(),
				"ogham.email.sendgrid.api-key=bar",
				"spring.sendgrid.api-key=foo",
				"ogham.freemarker.default-encoding="+StandardCharsets.US_ASCII.name(),
				"spring.freemarker.charset="+StandardCharsets.UTF_16BE.name());
	}

	@After
	public void tearDown() {
		if (context != null) {
			context.close();
		}
	}

	@Test
	public void oghamWithSendGridAutoConfigShouldUseSpringSendGridClient() throws Exception {
		context.register(SendGridAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		checkEmail(messagingService);
		checkSms(messagingService);
		OghamInternalAssertions.assertThat(messagingService)
			.sendGrid()
				.apiKey(equalTo("foo"))
				.client(isSpringBeanInstance(context, SendGrid.class));
	}

	@Test
	public void oghamWithoutSendGridAutoConfigShouldUseOghamSendGridClientWithSpringProperties() throws Exception {
		context.register(ManuallyEnableSendGridPropertiesConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		checkEmail(messagingService);
		checkSms(messagingService);
		OghamInternalAssertions.assertThat(messagingService)
			.sendGrid()
				.apiKey(equalTo("foo"))
				.client(not(isSpringBeanInstance(context, SendGrid.class)));
	}
	
	@Test
	public void useCustomSendGridBean() throws Exception {
		context.register(CustomSendGridConfig.class, SendGridAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		checkEmail(messagingService);
		checkSms(messagingService);
		OghamInternalAssertions.assertThat(messagingService)
			.sendGrid()
				.apiKey(nullValue(String.class))
				.client(allOf(isA(CustomSendGrid.class), isSpringBeanInstance(context, SendGrid.class)));
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
