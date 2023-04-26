package oghamspringbootv1autoconfigure.it;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.spring.v1.autoconfigure.OghamSpringBoot1AutoConfiguration;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.assertion.OghamInternalAssertions;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import ogham.testing.org.jsmpp.bean.SubmitSm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

@LogTestInformation
@ExtendWith(SpringExtension.class)
public class OghamSpringBoot1ThymeleafAutoConfigurationTests {
	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();

	@RegisterExtension
	public final SmppServerExtension<SubmitSm> smppServer = new JsmppServerExtension();

	private AnnotationConfigApplicationContext context;
	
	@BeforeEach
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

	@AfterEach
	public void tearDown() {
		if (context != null) {
			context.close();
		}
	}
	
	@Test
	public void oghamWithThymeleafAutoConfigShouldUseSpringTemplateEngine() throws Exception {
		context.register(ThymeleafAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		checkEmail(messagingService);
		checkSms(messagingService);
		OghamInternalAssertions.assertThat(messagingService)
			.thymeleaf()
				.all()
					.engine(isA(SpringTemplateEngine.class));
	}
	

	@Test
	public void useCustomThymeleafBean() throws Exception {
		context.register(CustomThymeleafEngineConfig.class, ThymeleafAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		MessagingService messagingService = context.getBean(MessagingService.class);
		checkEmail(messagingService);
		checkSms(messagingService);
		OghamInternalAssertions.assertThat(messagingService)
			.thymeleaf()
				.all()
					.engine(isA(CustomSpringTemplateEngine.class));
	}
	

	@Configuration
	protected static class CustomThymeleafEngineConfig {
		
		@Bean
		public SpringTemplateEngine templateEngine() {
			return new CustomSpringTemplateEngine();
		}
	}

	private static class CustomSpringTemplateEngine extends SpringTemplateEngine {
		
	}


	private void checkSms(MessagingService messagingService) throws MessagingException {
		messagingService.send(new Sms()
				.from("+33102030405")
				.to("+33123456789")
				.content("hello"));
		OghamAssertions.assertThat(smppServer).receivedMessages().count(is(1));
	}

	private void checkEmail(MessagingService messagingService) throws MessagingException {
		messagingService.send(new Email()
				.from("foo@yopmail.com")
				.to("bar@yopmail.com")
				.subject("test")
				.content("hello"));
		OghamAssertions.assertThat(greenMail).receivedMessages().count(is(1));
	}
}
