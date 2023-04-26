package oghamspringbootv3autoconfigure.it;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.spring.v3.autoconfigure.OghamSpringBoot3AutoConfiguration;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import freemarker.core.InvalidReferenceException;
import mock.context.SimpleBean;
import ogham.testing.org.jsmpp.bean.SubmitSm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.io.IOException;

import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.boot.autoconfigure.AutoConfigurations.of;

@LogTestInformation
public class StaticMethodAccessTest {
	@RegisterExtension public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();
	@RegisterExtension public final SmppServerExtension<SubmitSm> smppServer = new JsmppServerExtension();

	private ApplicationContextRunner contextRunner;
	
	@BeforeEach
	public void setUp() {
		contextRunner = new ApplicationContextRunner()
				.withPropertyValues(
						"mail.smtp.host="+greenMail.getSmtp().getBindTo(), 
						"mail.smtp.port="+greenMail.getSmtp().getPort(),
						"ogham.sms.smpp.host=127.0.0.1",
						"ogham.sms.smpp.port="+smppServer.getPort(),
						"spring.freemarker.suffix=")
				.withConfiguration(of(FreeMarkerAutoConfiguration.class, OghamSpringBoot3AutoConfiguration.class));
	}

	@Test
	public void emailUsingFreemarkerTemplateShouldBeAbleToCallStaticMethods() throws MessagingException, IOException {
		contextRunner = contextRunner.withPropertyValues(
				"ogham.freemarker.static-method-access.enable=true");
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			messagingService.send(new Email()
					.from("foo@yopmail.com")
					.to("bar@yopmail.com")
					.content(new TemplateContent("/freemarker/source/static-methods.html.ftl", new SimpleBean("world", 0))));
			OghamAssertions.assertThat(greenMail)
				.receivedMessages()
					.count(is(1))
					.message(0)
						.body()
							.contentAsString(isIdenticalHtml(resourceAsString("/freemarker/expected/static-methods.html")));
		});
	}

	@Test
	public void smsUsingFreemarkerTemplateShouldBeAbleToCallStaticMethods() throws MessagingException, IOException {
		contextRunner = contextRunner.withPropertyValues(
				"ogham.freemarker.static-method-access.enable=true");
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			messagingService.send(new Sms()
					.from("+33102030405")
					.to("+33123456789")
					.content(new TemplateContent("/freemarker/source/static-methods.txt.ftl", new SimpleBean("world", 0))));
			OghamAssertions.assertThat(smppServer)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.content(is(resourceAsString("/freemarker/expected/static-methods.txt")));
		});
	}

	
	
	@Test
	public void emailUsingFreemarkerTemplateAndStaticMethodAccessDisabledShouldFail() throws MessagingException, IOException {
		contextRunner = contextRunner.withPropertyValues(
				"ogham.freemarker.static-method-access.enable=false");
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			// @formatter:off
			MessagingException e = assertThrows(MessagingException.class, () -> {
				messagingService.send(new Email()
						.from("foo@yopmail.com")
						.to("bar@yopmail.com")
						.content(new TemplateContent("/freemarker/source/static-methods.html.ftl", new SimpleBean("world", 0))));
			}, "should throw");
			assertThat("should report missing statics", e, allOf(
					instanceOf(MessagingException.class),
					hasAnyCause(InvalidReferenceException.class, hasMessage(containsString("The following has evaluated to null or missing:\n==> statics")))));
			// @formatter:on
		});
	}

	@Test
	public void smsUsingFreemarkerTemplateAndStaticMethodAccessDisabledShouldFail() throws MessagingException, IOException {
		contextRunner = contextRunner.withPropertyValues(
				"ogham.freemarker.static-method-access.enable=false");
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			// @formatter:off
			MessagingException e = assertThrows(MessagingException.class, () -> {
				messagingService.send(new Sms()
						.from("+33102030405")
						.to("+33123456789")
						.content(new TemplateContent("/freemarker/source/static-methods.txt.ftl", new SimpleBean("world", 0))));
			}, "should throw");
			// @formatter:on
			assertThat("should report missing statics", e, allOf(
					instanceOf(MessagingException.class),
					hasAnyCause(InvalidReferenceException.class, hasMessage(containsString("The following has evaluated to null or missing:\n==> statics")))));
		});
	}

	
	@Test
	public void emailUsingFreemarkerTemplateShouldBeAbleToCallStaticMethodsWithCustomVariableName() throws MessagingException, IOException {
		contextRunner = contextRunner.withPropertyValues(
				"ogham.freemarker.static-method-access.enable=true",
				"ogham.freemarker.static-method-access.variable-name=global");
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			messagingService.send(new Email()
					.from("foo@yopmail.com")
					.to("bar@yopmail.com")
					.content(new TemplateContent("/freemarker/source/static-methods-different-name.html.ftl", new SimpleBean("world", 0))));
			OghamAssertions.assertThat(greenMail)
				.receivedMessages()
					.count(is(1))
					.message(0)
						.body()
							.contentAsString(isIdenticalHtml(resourceAsString("/freemarker/expected/static-methods.html")));
		});
	}

	@Test
	public void smsUsingFreemarkerTemplateShouldBeAbleToCallStaticMethodsWithCustomVariableName() throws MessagingException, IOException {
		contextRunner = contextRunner.withPropertyValues(
				"ogham.freemarker.static-method-access.enable=true",
				"ogham.freemarker.static-method-access.variable-name=global");
		contextRunner.run((context) -> {
			MessagingService messagingService = context.getBean(MessagingService.class);
			messagingService.send(new Sms()
					.from("+33102030405")
					.to("+33123456789")
					.content(new TemplateContent("/freemarker/source/static-methods-different-name.txt.ftl", new SimpleBean("world", 0))));
			OghamAssertions.assertThat(smppServer)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.content(is(resourceAsString("/freemarker/expected/static-methods.txt")));
		});
	}

}
