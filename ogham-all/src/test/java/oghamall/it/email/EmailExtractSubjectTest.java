package oghamall.it.email;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class EmailExtractSubjectTest {
	ExpectedException thrown = ExpectedException.none();
	@Rule public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	@Rule public final RuleChain chain = RuleChain
			.outerRule(new LoggingTestRule())
			.around(thrown);

	private MessagingBuilder builder;
	
	@Before
	public void setUp() throws IOException {
		builder = MessagingBuilder.standard();
		builder.environment()
				.properties()
					.set("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress())
					.set("mail.smtp.port", ServerSetupTest.SMTP.getPort());
	}
	
	@Test
	public void noSubjectInContentsWithoutDefaultSubjectShouldSendWithoutSubject() throws MessagingException, javax.mail.MessagingException, IOException {
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
	public void noSubjectInContentsWithDefaultSubjectShouldSendWithDefaultSubject() throws MessagingException, javax.mail.MessagingException, IOException {
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
	public void noSubjectInContentsWithDefaultSubjectAndCustomSubjectShouldSendWithCustomSubject() throws MessagingException, javax.mail.MessagingException, IOException {
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
	public void subjectExtractedFromHtmlWithoutDefaultSubjectShouldSendWithHtmlTitle() throws MessagingException, javax.mail.MessagingException, IOException {
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
	public void subjectExtractedFromHtmlWithDefaultSubjectShouldSendWithHtmlTitle() throws MessagingException, javax.mail.MessagingException, IOException {
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
	public void subjectExtractedFromHtmlWithDefaultSubjectAndCustomSubjectShouldSendWithCustomSubject() throws MessagingException, javax.mail.MessagingException, IOException {
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
	public void subjectExtractedFromTextWithoutDefaultSubjectShouldSendWithSubjectExtractedFromText() throws MessagingException, javax.mail.MessagingException, IOException {
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
	public void subjectExtractedFromTextWithDefaultSubjectShouldSendWithSubjectExtractedFromText() throws MessagingException, javax.mail.MessagingException, IOException {
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
	public void subjectExtractedFromTextWithDefaultSubjectAndCustomSubjectShouldSendWithCustomSubject() throws MessagingException, javax.mail.MessagingException, IOException {
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
