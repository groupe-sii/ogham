package oghamspringbootv1autoconfigure.it;

import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.jsmpp.bean.SubmitSm;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.icegreen.greenmail.junit4.GreenMailRule;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.spring.v1.autoconfigure.OghamSpringBoot1AutoConfiguration;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailRule;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerRule;
import mock.context.SimpleBean;

public class StaticMethodsAccessTest {
	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule public final GreenMailRule greenMail = new RandomPortGreenMailRule();
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	private AnnotationConfigApplicationContext context;
	private MessagingService messagingService;
	
	@Before
	public void setUp() {
		context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, 
				"mail.smtp.host="+greenMail.getSmtp().getBindTo(), 
				"mail.smtp.port="+greenMail.getSmtp().getPort(),
				"ogham.sms.smpp.host=127.0.0.1",
				"ogham.sms.smpp.port="+smppServer.getPort(),
				"spring.freemarker.suffix=");
		context.register( FreeMarkerAutoConfiguration.class, OghamSpringBoot1AutoConfiguration.class);
		context.refresh();
		messagingService = context.getBean(MessagingService.class);
	}

	@After
	public void tearDown() {
		if (context != null) {
			context.close();
		}
	}
	
	@Test
	public void emailUsingFreemarkerTemplateShouldBeAbleToCallStaticMethods() throws MessagingException, IOException {
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
	}

	@Test
	public void smsUsingFreemarkerTemplateShouldBeAbleToCallStaticMethods() throws MessagingException, IOException {
		messagingService.send(new Sms()
				.from("+33102030405")
				.to("+33123456789")
				.content(new TemplateContent("/freemarker/source/static-methods.txt.ftl", new SimpleBean("world", 0))));
		OghamAssertions.assertThat(smppServer)
		.receivedMessages()
			.count(is(1))
			.message(0)
				.content(is(resourceAsString("/freemarker/expected/static-methods.txt")));
	}
	
	public static String hello(String name) {
		return "hello " + name;
	}

	public static class UtilityClass {
		public static String hello(String name) {
			return "Hello " + name;
		}
	}
}
