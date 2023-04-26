package oghamall.it.email;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static org.hamcrest.Matchers.*;

@LogTestInformation
public class EmailExtractSubjectTest {
	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();;

	private MessagingBuilder builder;
	
	@BeforeEach
	public void setUp() throws IOException {
		builder = MessagingBuilder.standard();
		builder.environment()
				.properties()
					.set("mail.smtp.host", greenMail.getSmtp().getBindTo())
					.set("mail.smtp.port", greenMail.getSmtp().getPort());
	}
	
	@Test
	public void noSubjectInContentsWithoutDefaultSubjectShouldSendWithoutSubject() throws MessagingException, jakarta.mail.MessagingException, IOException {
		MessagingService oghamService = builder.build();
		// @formatter:off
		oghamService.send(new Email()
							.content(new MultiContent("text", "<html><head></head><body>html</body></html>"))
							.from("test.sender@sii.fr")
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(nullValue())
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml("<html><head></head><body>html</body></html>"))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(is("text"))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void noSubjectInContentsWithDefaultSubjectShouldSendWithDefaultSubject() throws MessagingException, jakarta.mail.MessagingException, IOException {
		builder.environment().properties().set("ogham.email.subject.default-value", "foo");
		MessagingService oghamService = builder.build();
		// @formatter:off
		oghamService.send(new Email()
							.content(new MultiContent("text", "<html><head></head><body>html</body></html>"))
							.from("test.sender@sii.fr")
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("foo"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml("<html><head></head><body>html</body></html>"))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(is("text"))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void noSubjectInContentsWithDefaultSubjectAndCustomSubjectShouldSendWithCustomSubject() throws MessagingException, jakarta.mail.MessagingException, IOException {
		builder.environment().properties().set("ogham.email.subject.default-value", "foo");
		MessagingService oghamService = builder.build();
		// @formatter:off
		oghamService.send(new Email()
							.subject("bar")
							.content(new MultiContent("text", "<html><head></head><body>html</body></html>"))
							.from("test.sender@sii.fr")
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("bar"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml("<html><head></head><body>html</body></html>"))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(is("text"))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void subjectExtractedFromHtmlWithoutDefaultSubjectShouldSendWithHtmlTitle() throws MessagingException, jakarta.mail.MessagingException, IOException {
		MessagingService oghamService = builder.build();
		// @formatter:off
		oghamService.send(new Email()
							.content(new MultiContent("text", "<html><head><title>html title</title></head><body>html</body></html>"))
							.from("test.sender@sii.fr")
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("html title"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml("<html><head><title>html title</title></head><body>html</body></html>"))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(is("text"))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void subjectExtractedFromHtmlWithDefaultSubjectShouldSendWithHtmlTitle() throws MessagingException, jakarta.mail.MessagingException, IOException {
		builder.environment().properties().set("ogham.email.subject.default-value", "foo");
		MessagingService oghamService = builder.build();
		// @formatter:off
		oghamService.send(new Email()
							.content(new MultiContent("text", "<html><head><title>html title</title></head><body>html</body></html>"))
							.from("test.sender@sii.fr")
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("html title"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml("<html><head><title>html title</title></head><body>html</body></html>"))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(is("text"))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void subjectExtractedFromHtmlWithDefaultSubjectAndCustomSubjectShouldSendWithCustomSubject() throws MessagingException, jakarta.mail.MessagingException, IOException {
		builder.environment().properties().set("ogham.email.subject.default-value", "foo");
		MessagingService oghamService = builder.build();
		// @formatter:off
		oghamService.send(new Email()
							.subject("bar")
							.content(new MultiContent("text", "<html><head><title>html title</title></head><body>html</body></html>"))
							.from("test.sender@sii.fr")
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("bar"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml("<html><head><title>html title</title></head><body>html</body></html>"))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(is("text"))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void subjectExtractedFromTextWithoutDefaultSubjectShouldSendWithSubjectExtractedFromText() throws MessagingException, jakarta.mail.MessagingException, IOException {
		MessagingService oghamService = builder.build();
		// @formatter:off
		oghamService.send(new Email()
							.content(new MultiContent("Subject: from text content\ntext", "<html><head></head><body>html</body></html>"))
							.from("test.sender@sii.fr")
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("from text content"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml("<html><head></head><body>html</body></html>"))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(is("text"))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void subjectExtractedFromTextWithDefaultSubjectShouldSendWithSubjectExtractedFromText() throws MessagingException, jakarta.mail.MessagingException, IOException {
		builder.environment().properties().set("ogham.email.subject.default-value", "foo");
		MessagingService oghamService = builder.build();
		// @formatter:off
		oghamService.send(new Email()
							.content(new MultiContent("Subject: from text content\ntext", "<html><head></head><body>html</body></html>"))
							.from("test.sender@sii.fr")
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("from text content"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml("<html><head></head><body>html</body></html>"))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(is("text"))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void subjectExtractedFromTextWithDefaultSubjectAndCustomSubjectShouldSendWithCustomSubject() throws MessagingException, jakarta.mail.MessagingException, IOException {
		builder.environment().properties().set("ogham.email.subject.default-value", "foo");
		MessagingService oghamService = builder.build();
		// @formatter:off
		oghamService.send(new Email()
							.subject("bar")
							.content(new MultiContent("Subject: from text content\ntext", "<html><head></head><body>html</body></html>"))
							.from("test.sender@sii.fr")
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("bar"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml("<html><head></head><body>html</body></html>"))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(matchesRegex("Subject: from text content\r?\ntext"))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}
}
