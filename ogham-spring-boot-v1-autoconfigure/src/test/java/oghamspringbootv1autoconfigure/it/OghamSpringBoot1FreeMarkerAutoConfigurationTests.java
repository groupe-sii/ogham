package oghamspringbootv1autoconfigure.it;

import static org.hamcrest.Matchers.equalTo;

import java.nio.charset.StandardCharsets;

import org.jsmpp.bean.SubmitSm;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.spring.v1.autoconfigure.OghamSpringBoot1AutoConfiguration;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.assertion.OghamInternalAssertions;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.helper.sms.rule.JsmppServerRule;
import fr.sii.ogham.testing.helper.sms.rule.SmppServerRule;

public class OghamSpringBoot1FreeMarkerAutoConfigurationTests {
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
				"ogham.email.sendgrid.api-key=ogham",
				"spring.sendgrid.api-key=spring",
				"spring.freemarker.charset="+StandardCharsets.UTF_16BE.name());
	}

	@After
	public void tearDown() {
		if (context != null) {
			context.close();
		}
	}
	
	@Test
	public void oghamWithFreemarkerAutoConfigWithoutWebContextShouldUseSpringFreemarkerConfiguration() throws Exception {
		context.register(FreeMarkerAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		checkEmail(messagingService);
		checkSms(messagingService);
		OghamInternalAssertions.assertThat(messagingService)
			.freemarker()
				.all()
					.configuration()
						.defaultEncoding(equalTo(StandardCharsets.UTF_16BE.name()));
	}

	@Test
	public void oghamWithFreemarkerAutoConfigInWebContextShouldUseSpringFreemarkerConfiguration() throws Exception {
		context.register(WebMvcAutoConfiguration.class, FreeMarkerAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		checkEmail(messagingService);
		checkSms(messagingService);
		OghamInternalAssertions.assertThat(messagingService)
			.freemarker()
				.all()
					.configuration()
						.defaultEncoding(equalTo(StandardCharsets.UTF_16BE.name()));
	}
	

	@Test
	public void oghamWithFreemarkerAutoConfigWithoutWebContextAndOghamPropertiesShouldUseSpringFreemarkerConfigurationAndOghamProperties() throws Exception {
		EnvironmentTestUtils.addEnvironment(context, "ogham.freemarker.default-encoding="+StandardCharsets.US_ASCII.name());
		context.register(FreeMarkerAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		checkEmail(messagingService);
		checkSms(messagingService);
		OghamInternalAssertions.assertThat(messagingService)
			.freemarker()
				.all()
					.configuration()
						.defaultEncoding(equalTo(StandardCharsets.US_ASCII.name()));
	}

	@Test
	public void oghamWithFreemarkerAutoConfigInWebContextAndOghamPropertiesShouldUseSpringFreemarkerConfigurationAndOghamProperties() throws Exception {
		EnvironmentTestUtils.addEnvironment(context, "ogham.freemarker.default-encoding="+StandardCharsets.US_ASCII.name());
		context.register(WebMvcAutoConfiguration.class, FreeMarkerAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		checkEmail(messagingService);
		checkSms(messagingService);
		OghamInternalAssertions.assertThat(messagingService)
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
