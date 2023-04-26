package oghamall.it.thymeleaf;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import mock.context.SimpleBean;
import ogham.testing.org.jsmpp.bean.SubmitSm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.Matchers.is;

@LogTestInformation
public class StaticMethodAccessTest {

	private MessagingService messagingService;

	@RegisterExtension public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();
	@RegisterExtension public final SmppServerExtension<SubmitSm> smppServer = new JsmppServerExtension();

	@BeforeEach
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
