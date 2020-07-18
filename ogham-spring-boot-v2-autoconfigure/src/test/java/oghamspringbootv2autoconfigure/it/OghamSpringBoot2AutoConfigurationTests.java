package oghamspringbootv2autoconfigure.it;

import static fr.sii.ogham.testing.assertion.internal.matcher.SpringContextAssertions.isSpringBeanInstance;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.springframework.boot.autoconfigure.AutoConfigurations.of;

import java.nio.charset.StandardCharsets;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.thymeleaf.TemplateEngine;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.sendgrid.SendGrid;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.spring.v2.autoconfigure.OghamSpringBoot2AutoConfiguration;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.assertion.OghamInternalAssertions;
import fr.sii.ogham.testing.extension.greenmail.RandomPortGreenMailRule;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;

public class OghamSpringBoot2AutoConfigurationTests {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final GreenMailRule greenMail = new RandomPortGreenMailRule();

	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	private ApplicationContextRunner contextRunner;
	
	@Before
	public void setUp() {
		contextRunner = new ApplicationContextRunner()
				.withPropertyValues("mail.smtp.host="+greenMail.getSmtp().getBindTo(), 
						"mail.smtp.port="+greenMail.getSmtp().getPort(),
						"ogham.sms.smpp.host=127.0.0.1",
						"ogham.sms.smpp.port="+smppServer.getPort(),
						"ogham.email.sendgrid.api-key=ogham",
						"spring.sendgrid.api-key=spring",
						"ogham.freemarker.default-encoding="+StandardCharsets.US_ASCII.name(),
						"spring.freemarker.charset="+StandardCharsets.UTF_16BE.name());
	}

	@Test
	public void oghamAlone() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(OghamSpringBoot2AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			checkEmail(messagingService);
			checkSms(messagingService);
			OghamInternalAssertions.assertThat(messagingService)
				.sendGrid()
					.apiKey(equalTo("ogham"))
					.client(allOf(isA(SendGrid.class), not(isSpringBeanInstance(context, SendGrid.class))))
					.and()
				.thymeleaf()
					.all()
						.engine(isA(TemplateEngine.class))
						.and()
					.and()
				.freemarker()
					.all()
						.configuration()
							.defaultEncoding(equalTo(StandardCharsets.US_ASCII.name()));
		});
	}

	@Test
	public void oghamInWebContext() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(WebMvcAutoConfiguration.class, OghamSpringBoot2AutoConfiguration.class));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			checkEmail(messagingService);
			checkSms(messagingService);
			OghamInternalAssertions.assertThat(messagingService)
				.sendGrid()
					.apiKey(equalTo("ogham"))
					.client(allOf(isA(SendGrid.class), not(isSpringBeanInstance(context, SendGrid.class))))
					.and()
				.thymeleaf()
					.all()
						.engine(isA(TemplateEngine.class))
						.and()
					.and()
				.freemarker()
					.all()
						.configuration()
							.defaultEncoding(equalTo(StandardCharsets.US_ASCII.name()));
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
