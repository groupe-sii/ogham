package fr.sii.notification.it.email;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.content.MultiContent;
import fr.sii.notification.core.message.content.TemplateContent;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;
import fr.sii.notification.helper.email.AssertEmail;
import fr.sii.notification.helper.email.ExpectedContent;
import fr.sii.notification.helper.email.ExpectedEmail;
import fr.sii.notification.helper.email.ExpectedMultiPartEmail;
import fr.sii.notification.mock.context.SimpleBean;

public class ManualEmailTest {

	private NotificationService notificationService;
	
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	
	@Before
	public void setUp() throws IOException {
		Properties props = new Properties(System.getProperties());
		props.load(getClass().getResourceAsStream("/application.properties"));
		props.put("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress());
		props.put("mail.smtp.port", ServerSetupTest.SMTP.getPort());
		notificationService = new NotificationBuilder().useAllDefaults(props).build();
	}
	
	@Test
	public void simple() throws NotificationException, MessagingException {
		notificationService.send(new Email("Simple", "string body", "recipient@sii.fr"));
		AssertEmail.assertEquals(new ExpectedEmail("Simple", "string body", "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
	}

	@Test
	public void withThymeleaf() throws NotificationException, MessagingException, IOException {
		notificationService.send(new Email("Template", new TemplateContent("classpath:/template/thymeleaf/source/simple.html", new SimpleBean("foo", 42)), "recipient@sii.fr"));
		AssertEmail.assertEquals(new ExpectedEmail("Template", new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_foo_42.html"), "text/html.*"), "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
	}

	@Test
	public void fallback() throws NotificationException, MessagingException, IOException {
		notificationService.send(new Email("Multi", new MultiContent(
										new TemplateContent("classpath:/template/thymeleaf/source/simple.html", new SimpleBean("bar", 12)),
										new TemplateContent("classpath:/template/thymeleaf/source/simple.txt", new SimpleBean("bar", 12))), "recipient@sii.fr"));
//		notificationService.send(new Email("Multi", new MultiTemplateContent("classpath:/email/test", new SimpleBean("bar", 12))), "recipient@sii.fr"));
		AssertEmail.assertEquals(new ExpectedMultiPartEmail("Multi", new ExpectedContent[] {
				new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_bar_12.html"), "text/html.*"),
				new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_bar_12.txt"), "text/plain.*")
		}, "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
	}

	@Test
	public void attachment() {
//		notificationService.send(new Email("aurelien.baudet@gmail.com", "recipient@sii.fr", "Test", "body", new FileAttachment(new File("toto.pdf"))));
//		notificationService.send(new Email("aurelien.baudet@gmail.com", "recipient@sii.fr", "Test", "body", new FileAttachment("classpath:/toto.pdf")));
//		notificationService.send(new Email("aurelien.baudet@gmail.com", "recipient@sii.fr", "Test", "body", new FileAttachment("toto.pdf", getClass().getResourceAsStream(""))));
	}
}
