package oghamspringbootv1autoconfigure.it;

import static fr.sii.ogham.testing.assertion.internal.matcher.SpringContextAssertions.isSpringBeanInstance;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;

import java.nio.charset.StandardCharsets;

import org.jsmpp.bean.SubmitSm;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.thymeleaf.TemplateEngine;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.sendgrid.SendGrid;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.spring.v1.autoconfigure.OghamSpringBoot1AutoConfiguration;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.assertion.OghamInternalAssertions;
import fr.sii.ogham.testing.extension.greenmail.RandomPortGreenMailRule;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;

public class OghamSpringBoot1AutoConfigurationTests {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();

	@Rule
	public final GreenMailRule greenMail = new RandomPortGreenMailRule();

	@Rule
	public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	private AnnotationConfigApplicationContext context;
	
	@Before
	public void setUp() {
		context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, 
				"mail.smtp.host="+greenMail.getSmtp().getBindTo(), 
				"mail.smtp.port="+greenMail.getSmtp().getPort(),
				"ogham.sms.smpp.host=127.0.0.1",
				"ogham.sms.smpp.port="+smppServer.getPort(),
				"ogham.email.sendgrid.api-key=ogham",
				"spring.sendgrid.api-key=spring",
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
	public void oghamAlone() throws Exception {
		context.register(OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
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
	}

	@Test
	public void oghamInWebContext() throws Exception {
		context.register(WebMvcAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
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
