package oghamall.it.thymeleaf;

import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.extension.greenmail.RandomPortGreenMailRule;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import mock.context.SimpleBean;

public class StaticMethodAccessTest {

	private MessagingService messagingService;

	@Rule public final LoggingTestRule loggingRule = new LoggingTestRule();
	@Rule public final GreenMailRule greenMail = new RandomPortGreenMailRule();
	@Rule public final SmppServerRule<SubmitSm> smppServer = new JsmppServerRule();

	@Before
	public void setUp() throws IOException {
		messagingService = MessagingBuilder.standard()
				.environment()
					.properties()
						.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
						.set("mail.smtp.port", greenMail.getSmtp().getPort())
						.set("ogham.sms.smpp.host", "127.0.0.1")
						.set("ogham.sms.smpp.port", smppServer.getPort())
						.and()
					.and()
				.build();
	}
	
	@Test
	public void emailUsingThymeleafTemplateShouldBeAbleToCallStaticMethods() throws MessagingException, IOException {
		messagingService.send(new Email()
				.from("foo@yopmail.com")
				.to("bar@yopmail.com")
				.content(new TemplateContent("/template/thymeleaf/source/static-methods.html", new SimpleBean("world", 0))));
		OghamAssertions.assertThat(greenMail)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.body()
						.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/static-methods.html")));
	}

	@Test
	public void smsUsingThymeleafTemplateShouldBeAbleToCallStaticMethods() throws MessagingException, IOException {
		messagingService.send(new Sms()
				.from("+33102030405")
				.to("+33123456789")
				.content(new TemplateContent("/template/thymeleaf/source/static-methods.txt", new SimpleBean("world", 0))));
		OghamAssertions.assertThat(smppServer)
			.receivedMessages()
				.count(is(1))
				.message(0)
					.content(is(resourceAsString("/template/thymeleaf/expected/static-methods.txt")));
	}
}
