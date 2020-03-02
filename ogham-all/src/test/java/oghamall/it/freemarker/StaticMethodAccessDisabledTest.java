package oghamall.it.freemarker;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;

import java.io.IOException;
import java.util.Properties;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import freemarker.core.InvalidReferenceException;
import mock.context.SimpleBean;

public class StaticMethodAccessDisabledTest {

	private MessagingService messagingService;

	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();
	@Rule public final ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws IOException {
		Properties additionalProps = new Properties();
		additionalProps.setProperty("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress());
		additionalProps.setProperty("mail.smtp.port", String.valueOf(ServerSetupTest.SMTP.getPort()));
		additionalProps.setProperty("ogham.sms.smpp.host", "127.0.0.1");
		additionalProps.setProperty("ogham.sms.smpp.port", String.valueOf(smppServer.getPort()));
		additionalProps.setProperty("ogham.freemarker.static-method-access.enable", "false");
		messagingService = MessagingBuilder.standard()
				.environment()
					.properties(additionalProps)
					.and()
				.build();
	}
	
	@Test
	public void emailUsingFreemarkerTemplateAndStaticMethodAccessDisabledShouldFail() throws MessagingException, IOException {
		thrown.expect(allOf(
				instanceOf(MessagingException.class),
				hasAnyCause(InvalidReferenceException.class, hasMessage(containsString("The following has evaluated to null or missing:\n==> statics")))));
		messagingService.send(new Email()
				.from("foo@yopmail.com")
				.to("bar@yopmail.com")
				.content(new TemplateContent("/template/freemarker/source/static-methods.html.ftl", new SimpleBean("world", 0))));
	}

	@Test
	public void smsUsingFreemarkerTemplateAndStaticMethodAccessDisabledShouldFail() throws MessagingException, IOException {
		thrown.expect(allOf(
				instanceOf(MessagingException.class),
				hasAnyCause(InvalidReferenceException.class, hasMessage(containsString("The following has evaluated to null or missing:\n==> statics")))));
		messagingService.send(new Sms()
				.from("+33102030405")
				.to("+33123456789")
				.content(new TemplateContent("/template/freemarker/source/static-methods.txt.ftl", new SimpleBean("world", 0))));
	}
}
