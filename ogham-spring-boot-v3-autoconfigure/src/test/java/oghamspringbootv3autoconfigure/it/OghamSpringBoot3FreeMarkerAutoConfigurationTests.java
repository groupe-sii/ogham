package oghamspringbootv3autoconfigure.it;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.spring.v3.autoconfigure.OghamSpringBoot3AutoConfiguration;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.assertion.OghamInternalAssertions;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import ogham.testing.org.jsmpp.bean.SubmitSm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.boot.autoconfigure.AutoConfigurations.of;

@LogTestInformation
public class OghamSpringBoot3FreeMarkerAutoConfigurationTests {

	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();

	@RegisterExtension
	public final SmppServerExtension<SubmitSm> smppServer = new JsmppServerExtension();

	private ApplicationContextRunner contextRunner;
	
	@BeforeEach
	public void setUp() {
		contextRunner = new ApplicationContextRunner()
				.withPropertyValues("mail.smtp.host="+greenMail.getSmtp().getBindTo(), 
						"mail.smtp.port="+greenMail.getSmtp().getPort(),
						"ogham.sms.smpp.host=127.0.0.1",
						"ogham.sms.smpp.port="+smppServer.getPort(),
						"ogham.email.sendgrid.api-key=ogham",
						"spring.sendgrid.api-key=spring",
						"spring.freemarker.charset="+StandardCharsets.UTF_16BE.name());
	}
	
	@Test
	public void oghamWithFreemarkerAutoConfigWithoutWebContextShouldUseSpringFreemarkerConfiguration() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(FreeMarkerAutoConfiguration.class, OghamSpringBoot3AutoConfiguration.class));
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
		contextRunner = contextRunner.withConfiguration(of(WebMvcAutoConfiguration.class, FreeMarkerAutoConfiguration.class, OghamSpringBoot3AutoConfiguration.class));
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
	public void oghamWithFreemarkerAutoConfigWithoutWebContextAndOghamPropertiesShouldUseSpringFreemarkerConfigurationAndOghamProperties() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(FreeMarkerAutoConfiguration.class, OghamSpringBoot3AutoConfiguration.class))
				.withPropertyValues("ogham.freemarker.default-encoding="+StandardCharsets.US_ASCII.name());
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			checkEmail(messagingService);
			checkSms(messagingService);
			OghamInternalAssertions.assertThat(messagingService)
				.freemarker()
					.all()
						.configuration()
							.defaultEncoding(equalTo(StandardCharsets.US_ASCII.name()));
		});
	}

	@Test
	public void oghamWithFreemarkerAutoConfigInWebContextAndOghamPropertiesShouldUseSpringFreemarkerConfigurationAndOghamProperties() throws Exception {
		contextRunner = contextRunner.withConfiguration(of(WebMvcAutoConfiguration.class, FreeMarkerAutoConfiguration.class, OghamSpringBoot3AutoConfiguration.class))
				.withPropertyValues("ogham.freemarker.default-encoding="+StandardCharsets.US_ASCII.name());
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			checkEmail(messagingService);
			checkSms(messagingService);
			OghamInternalAssertions.assertThat(messagingService)
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
