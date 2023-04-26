package oghamall.it.email;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.id.generator.SequentialIdGenerator;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.assertion.email.By;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import mock.context.SimpleBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static fr.sii.ogham.email.attachment.ContentDisposition.INLINE;
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.assertion.util.EmailUtils.ATTACHMENT_DISPOSITION;
import static fr.sii.ogham.testing.assertion.util.EmailUtils.INLINE_DISPOSITION;
import static fr.sii.ogham.testing.util.ResourceUtils.resource;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@LogTestInformation
public class FluentEmailTest {

	private MessagingService oghamService;


	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();

	@BeforeEach
	public void setUp() throws IOException {
		Properties additionalProps = new Properties();
		additionalProps.setProperty("mail.smtp.host", greenMail.getSmtp().getBindTo());
		additionalProps.setProperty("mail.smtp.port", String.valueOf(greenMail.getSmtp().getPort()));
		oghamService = MessagingBuilder.standard()
				.email()
				.images()
					.inline()
						.attach()
							.cid()
								.generator(new SequentialIdGenerator(true))
								.and().and().and().and().and()
				.environment()
					.properties("/application.properties")
					.properties(additionalProps)
					.and()
				.build();
	}
	
	@Test
	public void bodyString() throws MessagingException, jakarta.mail.MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Simple")
								.body().string("string body")
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
	public void bodyTemplateString() throws MessagingException, jakarta.mail.MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Simple")
								.body().templateString("[[${name}]] [[${value}]]", new SimpleBean("foo", 42))
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
					.contentAsString(is("foo 42"))
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test
	public void bodyWithHtmlTemplateOnly() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.body().template("classpath:/template/thymeleaf/source/simple.html", new SimpleBean("foo", 42))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test
	public void htmlTemplateOnly() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.html().template("classpath:/template/thymeleaf/source/simple.html", new SimpleBean("foo", 42))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test
	public void textTemplateOnly() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.text().template("classpath:/template/thymeleaf/source/simple.txt", new SimpleBean("foo", 42))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}


	@Test
	public void bodyWithXhtmlTemplate() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.body().template("classpath:/template/thymeleaf/source/resources.xhtml", new SimpleBean("foo", 42))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/resources_foo_42.xhtml")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(hasSize(5))
				.attachment("h1.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/h1.gif")))
					.header("Content-ID", contains("<h1.gif0>"))
					.disposition(is(INLINE)).and()
				.attachment("fb.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/fb.gif")))
					.header("Content-ID", contains("<fb.gif4>"))
					.disposition(is(INLINE)).and()
				.attachment("left.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/left.gif")))
					.header("Content-ID", contains("<left.gif1>"))
					.disposition(is(INLINE)).and()
				.attachment("right1.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/right1.gif")))
					.header("Content-ID", contains("<right1.gif2>"))
					.disposition(is(INLINE)).and()
				.attachment("tw.gif")
					.contentType(startsWith("image/gif"))
					.content(is(resource("/template/freemarker/source/images/tw.gif")))
					.header("Content-ID", contains("<tw.gif3>"))
					.disposition(is(INLINE));
		// @formatter:on
	}


	@Test
	public void htmlAndText() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Multi")
								.html().template("classpath:/template/thymeleaf/source/simple.html", new SimpleBean("bar", 12))
								.text().template("classpath:/template/thymeleaf/source/simple.txt", new SimpleBean("bar", 12))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Multi"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.alternative()
					.contentAsString(is(resourceAsString("/template/thymeleaf/expected/simple_bar_12.txt")))
					.contentType(startsWith("text/plain")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/simple_bar_12.html")))
					.contentType(startsWith("text/html")).and()
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test
	public void bodyHtmlAndText() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Multi")
								.body().template("classpath:/template/thymeleaf/source/simple", new SimpleBean("bar", 12))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Multi"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/simple_bar_12.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/simple_bar_12.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test
	public void bodyHtmlAndMissingTextTemplate() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Multi")
								.body().template("classpath:/template/thymeleaf/source/multi_missing_variant", new SimpleBean("bar", 12))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Multi"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/simple_bar_12.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}

	@Test
	public void invalidTemplate() throws MessagingException, IOException {
		// @formatter:off
		assertThrows(MessageNotSentException.class, () -> {
			oghamService.send(new Email()
					.subject("Multi")
					.body().template("classpath:/template/thymeleaf/source/invalid.html", new SimpleBean("bar", 12))
					.to("recipient@sii.fr"));
		});
		// @formatter:on
	}

	@Test
	public void attachResource() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Test")
								.body().string("body")
								.to("recipient@sii.fr")
								.attach().resource("classpath:/attachment/04-Java-OOP-Basics.pdf")
								.attach().resource("custom-name.pdf", "classpath:/attachment/04-Java-OOP-Basics.pdf")
								.attach().resource("custom-name.doc", "classpath:/attachment/04-Java-OOP-Basics.pdf", "application/msword"));
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
				.attachments(hasSize(3))
				.attachment("04-Java-OOP-Basics.pdf")
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/pdf"))
					.filename(is("04-Java-OOP-Basics.pdf"))
					.disposition(is(ATTACHMENT_DISPOSITION))
					.and()
				.attachment("custom-name.pdf")
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/pdf"))
					.filename(is("custom-name.pdf"))
					.disposition(is(ATTACHMENT_DISPOSITION))
					.and()
				.attachment("custom-name.doc")
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/msword"))
					.filename(is("custom-name.doc"))
					.disposition(is(ATTACHMENT_DISPOSITION));
		// @formatter:on
	}

	@Test
	public void attachFile() throws MessagingException, IOException, jakarta.mail.MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Test")
								.text().string("body")
								.to("recipient@sii.fr")
								.attach().file(new File(getClass().getResource("/attachment/04-Java-OOP-Basics.pdf").getFile()))
								.attach().file("custom-name.doc", new File(getClass().getResource("/attachment/04-Java-OOP-Basics.pdf").getFile()), "application/msword"));
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
				.attachments(hasSize(2))
				.attachment("04-Java-OOP-Basics.pdf")
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/pdf"))
					.filename(is("04-Java-OOP-Basics.pdf"))
					.disposition(is(ATTACHMENT_DISPOSITION))
					.and()
				.attachment("custom-name.doc")
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/msword"))
					.filename(is("custom-name.doc"))
					.disposition(is(ATTACHMENT_DISPOSITION));
		// @formatter:on
	}

	@Test
	public void attachStream() throws MessagingException, IOException, jakarta.mail.MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Test")
								.text().string("body")
								.to("recipient@sii.fr")
								.attach().stream("foo.pdf", getClass().getResourceAsStream("/attachment/04-Java-OOP-Basics.pdf"))
								.attach().stream("bar.doc", getClass().getResourceAsStream("/attachment/04-Java-OOP-Basics.pdf"), "application/msword"));
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
				.attachments(hasSize(2))
				.attachment("foo.pdf")
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/pdf"))
					.filename(is("foo.pdf"))
					.disposition(is(ATTACHMENT_DISPOSITION))
					.and()
				.attachment("bar.doc")
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/msword"))
					.filename(is("bar.doc"))
					.disposition(is(ATTACHMENT_DISPOSITION));
		// @formatter:on
	}

	@Test
	public void embedResource() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Test")
								.body().string("body")
								.to("recipient@sii.fr")
								.embed().resource("cid1", "classpath:/attachment/04-Java-OOP-Basics.pdf")
								.embed().resource("cid2", "classpath:/attachment/04-Java-OOP-Basics.pdf", "application/msword"));
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
				.attachments(hasSize(2))
				.attachment(By.contentId("cid1"))
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/pdf"))
					.header("Content-ID", contains("cid1"))
					.disposition(is(INLINE_DISPOSITION))
					.and()
				.attachment(By.contentId("cid2"))
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/msword"))
					.header("Content-ID", contains("cid2"))
					.disposition(is(INLINE_DISPOSITION));
		// @formatter:on
	}

	@Test
	public void embedFile() throws MessagingException, IOException, jakarta.mail.MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Test")
								.text().string("body")
								.to("recipient@sii.fr")
								.embed().file("cid1", new File(getClass().getResource("/attachment/04-Java-OOP-Basics.pdf").getFile()))
								.embed().file("cid2", new File(getClass().getResource("/attachment/04-Java-OOP-Basics.pdf").getFile()), "application/msword"));
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
				.attachments(hasSize(2))
				.attachment(By.contentId("cid1"))
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/pdf"))
					.header("Content-ID", contains("cid1"))
					.disposition(is(INLINE_DISPOSITION))
					.and()
				.attachment(By.contentId("cid2"))
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/msword"))
					.header("Content-ID", contains("cid2"))
					.disposition(is(INLINE_DISPOSITION));
		// @formatter:on
	}

	@Test
	public void embedStream() throws MessagingException, IOException, jakarta.mail.MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Test")
								.text().string("body")
								.to("recipient@sii.fr")
								.embed().stream("cid1", getClass().getResourceAsStream("/attachment/04-Java-OOP-Basics.pdf"))
								.embed().stream("cid2", getClass().getResourceAsStream("/attachment/04-Java-OOP-Basics.pdf"), "application/msword"));
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
				.attachments(hasSize(2))
				.attachment(By.contentId("cid1"))
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/pdf"))
					.header("Content-ID", contains("cid1"))
					.disposition(is(INLINE_DISPOSITION))
					.and()
				.attachment(By.contentId("cid2"))
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/msword"))
					.header("Content-ID", contains("cid2"))
					.disposition(is(INLINE_DISPOSITION));
		// @formatter:on
	}

	@Test
	public void attachAndEmbed() throws MessagingException, IOException, jakarta.mail.MessagingException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Test")
								.text().string("body")
								.to("recipient@sii.fr")
								.attach().resource("/attachment/04-Java-OOP-Basics.pdf")
								.embed().bytes("cid1", resource("/template/thymeleaf/source/images/fb.gif"))
								.attach().stream("toto.pdf", getClass().getResourceAsStream("/attachment/04-Java-OOP-Basics.pdf"))
								.embed().file("cid2", new File(getClass().getResource("/template/thymeleaf/source/images/h1.gif").getFile())));
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
				.attachments(hasSize(4))
				.attachment("04-Java-OOP-Basics.pdf")
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/pdf"))
					.filename(is("04-Java-OOP-Basics.pdf"))
					.disposition(is(ATTACHMENT_DISPOSITION))
					.and()
				.attachment(By.contentId("cid1"))
					.content(is(resource("/template/thymeleaf/source/images/fb.gif")))
					.contentType(startsWith("image/gif"))
					.header("Content-ID", contains("cid1"))
					.disposition(is(INLINE_DISPOSITION))
					.and()
				.attachment("toto.pdf")
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/pdf"))
					.filename(is("toto.pdf"))
					.disposition(is(ATTACHMENT_DISPOSITION))
					.and()
				.attachment(By.contentId("cid2"))
					.content(is(resource("/template/thymeleaf/source/images/h1.gif")))
					.contentType(startsWith("image/gif"))
					.header("Content-ID", contains("cid2"))
					.disposition(is(INLINE_DISPOSITION));
		// @formatter:on
	}
}
