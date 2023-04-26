package oghamall.it.freemarker;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
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

import java.io.IOException;
import java.util.Properties;

import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasAnyCause;
import static fr.sii.ogham.testing.assertion.hamcrest.ExceptionMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@LogTestInformation
public class StaticMethodAccessDisabledTest {

	private MessagingService messagingService;

	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();
	@RegisterExtension public final SmppServerExtension<SubmitSm> smppServer = new JsmppServerExtension();

	@BeforeEach
	public void setUp() throws IOException {
		Properties additionalProps = new Properties();
		additionalProps.setProperty("mail.smtp.host", greenMail.getSmtp().getBindTo());
		additionalProps.setProperty("mail.smtp.port", String.valueOf(greenMail.getSmtp().getPort()));
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
		// @formatter:off
		MessagingException e = assertThrows(MessagingException.class, () -> {
			messagingService.send(new Email()
					.from("foo@yopmail.com")
					.to("bar@yopmail.com")
					.content(new TemplateContent("/template/freemarker/source/static-methods.html.ftl", new SimpleBean("world", 0))));
		}, "should throw");
		assertThat("should report missing statics", e, allOf(
				instanceOf(MessagingException.class),
				hasAnyCause(InvalidReferenceException.class, hasMessage(containsString("The following has evaluated to null or missing:\n==> statics")))));
		// @formatter:on
	}

	@Test
	public void smsUsingFreemarkerTemplateAndStaticMethodAccessDisabledShouldFail() throws MessagingException, IOException {
		// @formatter:off
		MessagingException e = assertThrows(MessagingException.class, () -> {
			messagingService.send(new Sms()
					.from("+33102030405")
					.to("+33123456789")
					.content(new TemplateContent("/template/freemarker/source/static-methods.txt.ftl", new SimpleBean("world", 0))));
		}, "should throw");
		// @formatter:on
		assertThat("should report missing statics", e, allOf(
				instanceOf(MessagingException.class),
				hasAnyCause(InvalidReferenceException.class, hasMessage(containsString("The following has evaluated to null or missing:\n==> statics")))));
	}
}
