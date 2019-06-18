package fr.sii.ogham.spring.v2.it;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.boot.autoconfigure.AutoConfigurations.of;

import java.nio.charset.StandardCharsets;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

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

public class OghamSpringBoot2FreeMarkerAutoConfigurationTests {
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
				.withPropertyValues("mail.smtp.host="+ServerSetupTest.SMTP.getBindAddress(), 
						"mail.smtp.port="+ServerSetupTest.SMTP.getPort(),
						"ogham.sms.smpp.host=127.0.0.1",
						"ogham.sms.smpp.port="+smppServer.getPort(),
						"ogham.email.sendgrid.api-key=bar",
						"spring.sendgrid.api-key=foo",
						"ogham.freemarker.default-encoding="+StandardCharsets.US_ASCII.name(),
						"spring.freemarker.charset="+StandardCharsets.UTF_16BE.name());
	}
	
	@Test
	public void oghamWithFreemarkerAutoConfigWithoutWebContextShouldUseSpringFreemarkerConfiguration() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(FreeMarkerAutoConfiguration.class, OghamSpringBoot2AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			checkEmail(messagingService);
			checkSms(messagingService);
			OghamInternalAssertions.assertThat(messagingService)
				.freemarker()
					.all()
						.configuration()
							.defaultEncoding(equalTo(StandardCharsets.UTF_16BE.name()));
		});
	}

	@Test
	public void oghamWithFreemarkerAutoConfigInWebContextShouldUseSpringFreemarkerConfiguration() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(WebMvcAutoConfiguration.class, FreeMarkerAutoConfiguration.class, OghamSpringBoot2AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			checkEmail(messagingService);
			checkSms(messagingService);
			OghamInternalAssertions.assertThat(messagingService)
				.freemarker()
					.all()
						.configuration()
							.defaultEncoding(equalTo(StandardCharsets.UTF_16BE.name()));
		});
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
