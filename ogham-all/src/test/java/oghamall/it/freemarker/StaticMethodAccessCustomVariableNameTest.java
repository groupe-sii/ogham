package oghamall.it.freemarker;

import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.Properties;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import mock.context.SimpleBean;

public class StaticMethodAccessCustomVariableNameTest {

	private MessagingService messagingService;

	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	@Before
	public void setUp() throws IOException {
		Properties additionalProps = new Properties();
		additionalProps.setProperty("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress());
		additionalProps.setProperty("mail.smtp.port", String.valueOf(ServerSetupTest.SMTP.getPort()));
		additionalProps.setProperty("ogham.sms.smpp.host", "127.0.0.1");
		additionalProps.setProperty("ogham.sms.smpp.port", String.valueOf(smppServer.getPort()));
		additionalProps.setProperty("ogham.freemarker.static-method-access-variable-name", "global");
		messagingService = MessagingBuilder.standard()
				.environment()
					.properties(additionalProps)
					.and()
				.build();
	}
	
	@Test
	public void emailUsingFreemarkerTemplateShouldBeAbleToCallStaticMethodsWithCustomVariableName() throws MessagingException, IOException {
		messagingService.send(new Email()
				.from("foo@yopmail.com")
				.to("bar@yopmail.com")
				.content(new TemplateContent("/template/freemarker/source/static-methods-different-name.html.ftl", new SimpleBean("world", 0))));
		OghamAssertions.assertThat(greenMail)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.body()
						.contentAsString(isIdenticalHtml(resourceAsString("/template/freemarker/expected/static-methods.html")));
	}

	@Test
	public void smsUsingFreemarkerTemplateShouldBeAbleToCallStaticMethodsWithCustomVariableName() throws MessagingException, IOException {
		messagingService.send(new Sms()
				.from("+33102030405")
				.to("+33123456789")
				.content(new TemplateContent("/template/freemarker/source/static-methods-different-name.txt.ftl", new SimpleBean("world", 0))));
		OghamAssertions.assertThat(smppServer)
		.receivedMessages()
			.count(is(1))
			.message(0)
				.content(is(resourceAsString("/template/freemarker/expected/static-methods.txt")));
	}
}
