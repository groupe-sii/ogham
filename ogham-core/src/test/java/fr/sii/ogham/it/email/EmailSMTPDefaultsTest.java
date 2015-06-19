package fr.sii.ogham.it.email;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.helper.email.AssertAttachment;
import fr.sii.ogham.helper.email.AssertEmail;
import fr.sii.ogham.helper.email.ExpectedAttachment;
import fr.sii.ogham.helper.email.ExpectedContent;
import fr.sii.ogham.helper.email.ExpectedEmail;
import fr.sii.ogham.helper.email.ExpectedMultiPartEmail;
import fr.sii.ogham.helper.rule.LoggingTestRule;
import fr.sii.ogham.mock.context.SimpleBean;

public class EmailSMTPDefaultsTest {

	private MessagingService oghamService;
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	
	@Before
	public void setUp() throws IOException {
		Properties props = new Properties(System.getProperties());
		props.load(getClass().getResourceAsStream("/application.properties"));
		props.setProperty("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress());
		props.setProperty("mail.smtp.port", String.valueOf(ServerSetupTest.SMTP.getPort()));
		oghamService = new MessagingBuilder().useAllDefaults(props).build();
	}
	
	@Test
	public void simple() throws MessagingException, javax.mail.MessagingException {
		oghamService.send(new Email("Simple", "string body", "recipient@sii.fr"));
		AssertEmail.assertEquals(new ExpectedEmail("Simple", "string body", "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
	}

	@Test
	public void withThymeleaf() throws MessagingException, javax.mail.MessagingException, IOException {
		oghamService.send(new Email("Template", new TemplateContent("classpath:/template/thymeleaf/source/simple.html", new SimpleBean("foo", 42)), "recipient@sii.fr"));
		AssertEmail.assertSimilar(new ExpectedEmail("Template", new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_foo_42.html"), "text/html.*"), "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
	}

	@Test
	public void withThymeleafResources() throws MessagingException, javax.mail.MessagingException, IOException {
		oghamService.send(new Email("Template", new TemplateContent("classpath:/template/thymeleaf/source/resources.html", new SimpleBean("foo", 42)), "recipient@sii.fr"));
		AssertEmail.assertSimilar(new ExpectedEmail("Template", new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/resources_foo_42.html"), "text/html.*"), "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
	}

	@Test
	public void withThymeleafSubject() throws MessagingException, javax.mail.MessagingException, IOException {
		oghamService.send(new Email(null, new TemplateContent("classpath:/template/thymeleaf/source/simple.html", new SimpleBean("foo", 42)), "recipient@sii.fr"));
		AssertEmail.assertSimilar(new ExpectedEmail("Thymeleaf simple", new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_foo_42.html"), "text/html.*"), "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
	}
	
	@Test
	public void subjectInTextTemplate() throws MessagingException, javax.mail.MessagingException, IOException {
		oghamService.send(new Email(null, new TemplateContent("classpath:/template/thymeleaf/source/withSubject.txt", new SimpleBean("foo", 42)), "recipient@sii.fr"));
		AssertEmail.assertSimilar(new ExpectedEmail("Subject on first line", new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_foo_42.txt"), "text/plain.*"), "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
	}
	
	@Test
	public void multiContent() throws MessagingException, javax.mail.MessagingException, IOException {
		oghamService.send(new Email("Multi", new MultiContent(
										new TemplateContent("classpath:/template/thymeleaf/source/simple.html", new SimpleBean("bar", 12)),
										new TemplateContent("classpath:/template/thymeleaf/source/simple.txt", new SimpleBean("bar", 12))), "recipient@sii.fr"));
		AssertEmail.assertSimilar(new ExpectedMultiPartEmail("Multi", new ExpectedContent[] {
				new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_bar_12.html"), "text/html.*"),
				new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_bar_12.txt"), "text/plain.*")
		}, "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
	}

	@Test
	public void multiContentShortcut() throws MessagingException, javax.mail.MessagingException, IOException {
		oghamService.send(new Email("Multi", new MultiTemplateContent("classpath:/template/thymeleaf/source/simple", new SimpleBean("bar", 12)), "recipient@sii.fr"));
		AssertEmail.assertSimilar(new ExpectedMultiPartEmail("Multi", new ExpectedContent[] {
				new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_bar_12.html"), "text/html.*"),
				new ExpectedContent(getClass().getResourceAsStream("/template/thymeleaf/expected/simple_bar_12.txt"), "text/plain.*")
		}, "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
	}

	@Test(expected=MessageNotSentException.class)
	public void invalidTemplate() throws MessagingException, IOException {
		oghamService.send(new Email("Multi", new TemplateContent("classpath:/template/thymeleaf/source/invalid.html", new SimpleBean("bar", 12)), "recipient@sii.fr"));
	}

	@Test
	public void attachmentLookup() throws MessagingException, javax.mail.MessagingException, IOException {
		oghamService.send(new Email("Test", "body", "recipient@sii.fr", new Attachment("classpath:/attachment/04-Java-OOP-Basics.pdf")));
		AssertEmail.assertEquals(new ExpectedEmail("Test", "body", "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
		AssertAttachment.assertEquals(new ExpectedAttachment("/attachment/04-Java-OOP-Basics.pdf", "application/pdf.*"), greenMail.getReceivedMessages());
	}

	@Test
	public void attachmentFile() throws MessagingException, IOException, javax.mail.MessagingException {
		oghamService.send(new Email("Test", "body", "recipient@sii.fr", new Attachment(new File(getClass().getResource("/attachment/04-Java-OOP-Basics.pdf").getFile()))));
		AssertEmail.assertEquals(new ExpectedEmail("Test", "body", "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
		AssertAttachment.assertEquals(new ExpectedAttachment("/attachment/04-Java-OOP-Basics.pdf", "application/pdf.*"), greenMail.getReceivedMessages());
	}

	@Test
	public void attachmentStream() throws MessagingException, IOException, javax.mail.MessagingException {
		oghamService.send(new Email("Test", "body", "recipient@sii.fr", new Attachment("toto.pdf", getClass().getResourceAsStream("/attachment/04-Java-OOP-Basics.pdf"))));
		AssertEmail.assertEquals(new ExpectedEmail("Test", "body", "test.sender@sii.fr", "recipient@sii.fr"), greenMail.getReceivedMessages());
		AssertAttachment.assertEquals(new ExpectedAttachment("toto.pdf", "application/pdf.*", IOUtils.toByteArray(getClass().getResourceAsStream("/attachment/04-Java-OOP-Basics.pdf"))), greenMail.getReceivedMessages());
	}
}
