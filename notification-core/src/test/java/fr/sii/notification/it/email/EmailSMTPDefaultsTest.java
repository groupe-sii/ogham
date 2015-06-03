package fr.sii.notification.it.email;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.MessageNotSentException;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.content.MultiContent;
import fr.sii.notification.core.message.content.MultiTemplateContent;
import fr.sii.notification.core.message.content.TemplateContent;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.message.Email;
import fr.sii.notification.helper.email.AssertAttachment;
import fr.sii.notification.helper.email.AssertEmail;
import fr.sii.notification.helper.email.ExpectedAttachment;
import fr.sii.notification.helper.email.ExpectedContent;
import fr.sii.notification.helper.email.ExpectedEmail;
import fr.sii.notification.helper.email.ExpectedMultiPartEmail;
import fr.sii.notification.helper.rule.LoggingTestRule;
import fr.sii.notification.mock.context.SimpleBean;

public class EmailSMTPDefaultsTest {

	private NotificationService notificationService;
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
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
	public void multiContent() throws NotificationException, MessagingException, IOException {
		notificationService.send(new Email("Multi", new MultiContent(
										new TemplateContent("classpath:/template/thymeleaf/source/simple.html", new SimpleBean("bar", 12)),
										new TemplateContent("classpath:/template/thymeleaf/source/simple.txt", new SimpleBean("bar", 12))), "recipient@sii.fr"));
		AssertEmail.assertEquals(new ExpectedMultiPartEmail("Multi", new ExpectedContent[] {
				new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_bar_12.html"), "text/html.*"),
				new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_bar_12.txt"), "text/plain.*")
		}, "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
	}


	@Test
	public void multiContentShortcut() throws NotificationException, MessagingException, IOException {
		notificationService.send(new Email("Multi", new MultiTemplateContent("classpath:/template/thymeleaf/source/simple", new SimpleBean("bar", 12)), "recipient@sii.fr"));
		AssertEmail.assertEquals(new ExpectedMultiPartEmail("Multi", new ExpectedContent[] {
				new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_bar_12.html"), "text/html.*"),
				new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_bar_12.txt"), "text/plain.*")
		}, "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
	}

	@Test(expected=MessageNotSentException.class)
	public void invalidTemplate() throws NotificationException, MessagingException, IOException {
		notificationService.send(new Email("Multi", new TemplateContent("classpath:/template/thymeleaf/source/invalid.html", new SimpleBean("bar", 12)), "recipient@sii.fr"));
	}

	@Test
	public void attachmentLookup() throws NotificationException, MessagingException, IOException {
		notificationService.send(new Email("Test", "body", "recipient@sii.fr", new Attachment("classpath:/attachment/04-Java-OOP-Basics.pdf")));
		AssertEmail.assertEquals(new ExpectedEmail("Test", "body", "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
		AssertAttachment.assertEquals(new ExpectedAttachment("/attachment/04-Java-OOP-Basics.pdf", "application/pdf.*"), greenMail.getReceivedMessages());
	}

	@Test
	public void attachmentFile() throws NotificationException, IOException, MessagingException {
		notificationService.send(new Email("Test", "body", "recipient@sii.fr", new Attachment(new File(getClass().getResource("/attachment/04-Java-OOP-Basics.pdf").getFile()))));
		AssertEmail.assertEquals(new ExpectedEmail("Test", "body", "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
		AssertAttachment.assertEquals(new ExpectedAttachment("/attachment/04-Java-OOP-Basics.pdf", "application/pdf.*"), greenMail.getReceivedMessages());
	}

	@Test
	public void attachmentStream() throws NotificationException, IOException, MessagingException {
		notificationService.send(new Email("Test", "body", "recipient@sii.fr", new Attachment("toto.pdf", getClass().getResourceAsStream("/attachment/04-Java-OOP-Basics.pdf"))));
		AssertEmail.assertEquals(new ExpectedEmail("Test", "body", "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
		AssertAttachment.assertEquals(new ExpectedAttachment("toto.pdf", "application/pdf.*", IOUtils.toByteArray(getClass().getResourceAsStream("/attachment/04-Java-OOP-Basics.pdf"))), greenMail.getReceivedMessages());
	}
}
