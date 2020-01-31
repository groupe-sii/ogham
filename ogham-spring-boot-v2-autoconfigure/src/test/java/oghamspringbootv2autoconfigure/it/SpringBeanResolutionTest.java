package oghamspringbootv2autoconfigure.it;

import static fr.sii.ogham.testing.assertion.OghamMatchers.isSimilarHtml;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.boot.autoconfigure.AutoConfigurations.of;

import java.io.IOException;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.spring.v2.autoconfigure.OghamSpringBoot2AutoConfiguration;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import freemarker.core.InvalidReferenceException;
import mock.context.SimpleBean;

public class SpringBeanResolutionTest {
	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();
	@Rule public final ExpectedException thrown = ExpectedException.none();

	private ApplicationContextRunner contextRunner;
	
	@Before
	public void setUp() {
		contextRunner = new ApplicationContextRunner()
				.withPropertyValues(
						"mail.smtp.host="+ServerSetupTest.SMTP.getBindAddress(), 
						"mail.smtp.port="+ServerSetupTest.SMTP.getPort(),
						"ogham.sms.smpp.host=127.0.0.1",
						"ogham.sms.smpp.port="+smppServer.getPort(),
						"spring.freemarker.suffix=")
				.withConfiguration(of(TestConfig.class, ThymeleafAutoConfiguration.class, FreeMarkerAutoConfiguration.class, OghamSpringBoot2AutoConfiguration.class));
	}

	@Test
	public void emailUsingThymeleafTemplateShouldResolveBeans() throws MessagingException, IOException {
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			messagingService.send(new Email()
					.from("foo@yopmail.com")
					.to("bar@yopmail.com")
					.content(new TemplateContent("/thymeleaf/source/bean-resolution.html", new SimpleBean("world", 0))));
			OghamAssertions.assertThat(greenMail)
				.receivedMessages()
					.count(is(1))
					.message(0)
						.body()
							.contentAsString(isSimilarHtml(resourceAsString("/thymeleaf/expected/bean-resolution.html")));
		});
	}

	@Test
	public void smsUsingThymeleafTemplateShouldResolveBeans() throws MessagingException, IOException {
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			messagingService.send(new Sms()
					.from("+33102030405")
					.to("+33123456789")
					.content(new TemplateContent("/thymeleaf/source/bean-resolution.txt", new SimpleBean("world", 0))));
			OghamAssertions.assertThat(smppServer)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.content(is(resourceAsString("/thymeleaf/expected/bean-resolution.txt")));
		});
	}

	@Test
	public void missingBeanErrorUsingThymeleaf() throws MessagingException, IOException {
		thrown.expect(allOf(
				instanceOf(MessagingException.class),
				hasAnyCause(NoSuchBeanDefinitionException.class, hasMessage("No bean named 'missingService' available"))));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			messagingService.send(new Email()
					.from("foo@yopmail.com")
					.to("bar@yopmail.com")
					.content(new TemplateContent("/thymeleaf/source/missing-bean.html", new SimpleBean("world", 0))));
		});
	}
	
	@Test
	public void emailUsingFreemarkerTemplateShouldResolveBeans() throws MessagingException, IOException {
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			messagingService.send(new Email()
					.from("foo@yopmail.com")
					.to("bar@yopmail.com")
					.content(new TemplateContent("/freemarker/source/bean-resolution.html.ftl", new SimpleBean("world", 0))));
			OghamAssertions.assertThat(greenMail)
				.receivedMessages()
					.count(is(1))
					.message(0)
						.body()
							.contentAsString(isSimilarHtml(resourceAsString("/freemarker/expected/bean-resolution.html")));
		});
	}

	@Test
	public void smsUsingFreemarkerTemplateShouldResolveBeans() throws MessagingException, IOException {
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			messagingService.send(new Sms()
					.from("+33102030405")
					.to("+33123456789")
					.content(new TemplateContent("/freemarker/source/bean-resolution.txt.ftl", new SimpleBean("world", 0))));
			OghamAssertions.assertThat(smppServer)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.content(is(resourceAsString("/freemarker/expected/bean-resolution.txt")));
		});
	}

	@Test
	public void missingBeanErrorUsingFreemarker() throws MessagingException, IOException {
		thrown.expect(allOf(
				instanceOf(MessagingException.class),
				hasAnyCause(InvalidReferenceException.class, hasMessage(containsString("The following has evaluated to null or missing:\n==> @missingService")))));
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			messagingService.send(new Email()
					.from("foo@yopmail.com")
					.to("bar@yopmail.com")
					.content(new TemplateContent("/freemarker/source/missing-bean.html.ftl", new SimpleBean("world", 0))));
		});
	}


	

	@Configuration
	protected static class TestConfig {
		
		@Bean
		public FakeService fakeService() {
			return new FakeService();
		}
	}
	
	public static class FakeService {
		public String hello(String name) {
			return "hello " + name;
		}
	}
}
