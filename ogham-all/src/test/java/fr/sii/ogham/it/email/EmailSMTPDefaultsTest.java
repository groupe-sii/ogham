package fr.sii.ogham.it.email;

import static fr.sii.ogham.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.assertion.OghamAssertions.isSimilarHtml;
import static fr.sii.ogham.assertion.OghamAssertions.resource;
import static fr.sii.ogham.assertion.OghamAssertions.resourceAsString;
import static fr.sii.ogham.helper.email.EmailUtils.ATTACHMENT_DISPOSITION;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

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
		Properties additionalProps = new Properties();
		additionalProps.setProperty("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress());
		additionalProps.setProperty("mail.smtp.port", String.valueOf(ServerSetupTest.SMTP.getPort()));
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties(additionalProps)
					.and()
				.build();
	}
	
	@Test
	public void simple() throws MessagingException, javax.mail.MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Simple")
								.content("string body")
								.to("Recipient Name <recipient@sii.fr>"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Simple"))
				.from()
					.address(hasItems("test.sender@sii.fr"))
					.personal(hasItems("Sender Name")).and()
				.to()
					.address(hasItems("recipient@sii.fr"))
					.personal(hasItems("Recipient Name")).and()
				.body()
					.contentAsString(is("string body"))
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test
	public void withThymeleaf() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new TemplateContent("classpath:/template/thymeleaf/source/simple.html", new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isSimilarHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test
	public void withThymeleafResources() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new TemplateContent("classpath:/template/thymeleaf/source/resources.html", new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isSimilarHtml(resourceAsString("/template/thymeleaf/expected/resources_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test
	public void withThymeleafResourcesXhtml() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new TemplateContent("classpath:/template/thymeleaf/source/resources.xhtml", new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isSimilarHtml(resourceAsString("/template/thymeleaf/expected/resources_foo_42.xhtml")))
					.contentType(startsWith("application/xhtml")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test
	public void withThymeleafSubject() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.content(new TemplateContent("classpath:/template/thymeleaf/source/simple.html", new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Thymeleaf simple"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isSimilarHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test
	public void subjectInTextTemplate() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.content(new TemplateContent("classpath:/template/thymeleaf/source/withSubject.txt", new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Subject on first line"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isSimilarHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test
	public void multiContent() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Multi")
								.content(new MultiContent(
										new TemplateContent("classpath:/template/thymeleaf/source/simple.html", new SimpleBean("bar", 12)),
										new TemplateContent("classpath:/template/thymeleaf/source/simple.txt", new SimpleBean("bar", 12))))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Multi"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.alternative()
					.contentAsString(isSimilarHtml(resourceAsString("/template/thymeleaf/expected/simple_bar_12.html")))
					.contentType(startsWith("text/html")).and()
				.body()
					.contentAsString(isSimilarHtml(resourceAsString("/template/thymeleaf/expected/simple_bar_12.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test
	public void multiContentShortcut() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Multi")
								.content(new MultiTemplateContent("classpath:/template/thymeleaf/source/simple", new SimpleBean("bar", 12)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Multi"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isSimilarHtml(resourceAsString("/template/thymeleaf/expected/simple_bar_12.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isSimilarHtml(resourceAsString("/template/thymeleaf/expected/simple_bar_12.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test(expected = MessageNotSentException.class)
	public void invalidTemplate() throws MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Multi")
								.content(new TemplateContent("classpath:/template/thymeleaf/source/invalid.html", new SimpleBean("bar", 12)))
								.to("recipient@sii.fr"));
		// @formatter:on
	}

	@Test
	public void attachmentLookup() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Test")
								.content("body")
								.to("recipient@sii.fr")
								.attach(new Attachment("classpath:/attachment/04-Java-OOP-Basics.pdf")));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Test"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(is("body"))
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(hasSize(1))
				.attachment("04-Java-OOP-Basics.pdf")
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/pdf"))
					.filename(is("04-Java-OOP-Basics.pdf"))
					.disposition(is(ATTACHMENT_DISPOSITION));
		// @formatter:on
	}

	@Test
	public void attachmentFile() throws MessagingException, IOException, javax.mail.MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Test")
								.content("body")
								.to("recipient@sii.fr")
								.attach(new Attachment(new File(getClass().getResource("/attachment/04-Java-OOP-Basics.pdf").getFile()))));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Test"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(is("body"))
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(hasSize(1))
				.attachment("04-Java-OOP-Basics.pdf")
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/pdf"))
					.filename(is("04-Java-OOP-Basics.pdf"))
					.disposition(is(ATTACHMENT_DISPOSITION));
		// @formatter:on
	}

	@Test
	public void attachmentStream() throws MessagingException, IOException, javax.mail.MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Test")
								.content("body")
								.to("recipient@sii.fr")
								.attach(new Attachment("toto.pdf", getClass().getResourceAsStream("/attachment/04-Java-OOP-Basics.pdf"))));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Test"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(is("body"))
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(hasSize(1))
				.attachment("toto.pdf")
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/pdf"))
					.filename(is("toto.pdf"))
					.disposition(is(ATTACHMENT_DISPOSITION));
		// @formatter:on
	}
}
