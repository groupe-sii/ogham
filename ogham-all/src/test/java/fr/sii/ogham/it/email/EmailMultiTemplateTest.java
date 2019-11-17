package fr.sii.ogham.it.email;

import static fr.sii.ogham.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.assertion.OghamAssertions.isSimilarHtml;
import static fr.sii.ogham.assertion.OghamAssertions.resourceAsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.exception.handler.ImageInliningException;
import fr.sii.ogham.core.exception.handler.NoContentException;
import fr.sii.ogham.core.exception.handler.TemplateParsingFailedException;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.junit.LoggingTestRule;
import fr.sii.ogham.mock.context.SimpleBean;

public class EmailMultiTemplateTest {
	private MessagingService oghamService;
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp() throws IOException {
		Properties additionalProps = new Properties();
		additionalProps.setProperty("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress());
		additionalProps.setProperty("mail.smtp.port", String.valueOf(ServerSetupTest.SMTP.getPort()));
		additionalProps.setProperty("ogham.email.template.path-prefix", "/template/");
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties(additionalProps)
					.and()
				.build();
	}
	
	@Test
	public void withThymeleafMulti() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
							.subject("Template")
							.content(new MultiTemplateContent("thymeleaf/source/simple", new SimpleBean("foo", 42)))
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
				.alternative()
					.contentAsString(isSimilarHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void withThymeleafMissingVariant() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
							.subject("Template")
							.content(new MultiTemplateContent("thymeleaf/source/multi_missing_variant", new SimpleBean("foo", 42)))
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
	public void withThymeleafInvalidPath() throws MessagingException, javax.mail.MessagingException, IOException {
		thrown.expect(instanceOf(MessageNotSentException.class));
		thrown.expectCause(instanceOf(NoContentException.class));
		// @formatter:off
		oghamService.send(new Email()
							.subject("Template")
							.content(new MultiTemplateContent("thymeleaf/source/unexisting", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		// @formatter:on					
	}
	
	@Test
	public void withThymeleafOneVariantWithParsingError() throws MessagingException, javax.mail.MessagingException, IOException {
		thrown.expect(instanceOf(MessageNotSentException.class));
		thrown.expectCause(instanceOf(TemplateParsingFailedException.class));
		// @formatter:off
		oghamService.send(new Email()
							.content(new MultiTemplateContent("thymeleaf/source/parsing-error", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		// @formatter:on					
	}
	
	@Test
	public void withThymeleafOneVariantWithInvalidResourcePath() throws MessagingException, javax.mail.MessagingException, IOException {
		thrown.expect(instanceOf(MessageNotSentException.class));
		thrown.expectCause(instanceOf(ImageInliningException.class));
		// @formatter:off
		oghamService.send(new Email()
							.content(new MultiTemplateContent("thymeleaf/source/invalid-resources", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		// @formatter:on					
	}
	
	@Test
	public void withFreemarkerMulti() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
							.subject("Template")
							.content(new MultiTemplateContent("freemarker/source/simple", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isSimilarHtml(resourceAsString("/template/freemarker/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isSimilarHtml(resourceAsString("/template/freemarker/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void withFreemarkerMissingVariant() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
							.subject("Template")
							.content(new MultiTemplateContent("freemarker/source/multi_missing_variant", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isSimilarHtml(resourceAsString("/template/freemarker/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void withFreemarkerInvalidPath() throws MessagingException, javax.mail.MessagingException, IOException {
		thrown.expect(instanceOf(MessageNotSentException.class));
		thrown.expectCause(instanceOf(NoContentException.class));
		// @formatter:off
		oghamService.send(new Email()
							.subject("Template")
							.content(new MultiTemplateContent("freemarker/source/unexisting", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		// @formatter:on					
	}
	
	@Test
	public void withFreemarkerOneVariantWithParsingError() throws MessagingException, javax.mail.MessagingException, IOException {
		thrown.expect(instanceOf(MessageNotSentException.class));
		thrown.expectCause(instanceOf(TemplateParsingFailedException.class));
		// @formatter:off
		oghamService.send(new Email()
							.content(new MultiTemplateContent("freemarker/source/parsing-error", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		// @formatter:on					
	}

	@Test
	public void withFreemarkerOneVariantWithInvalidResourcePath() throws MessagingException, javax.mail.MessagingException, IOException {
		thrown.expect(instanceOf(MessageNotSentException.class));
		thrown.expectCause(instanceOf(ImageInliningException.class));
		// @formatter:off
		oghamService.send(new Email()
							.content(new MultiTemplateContent("freemarker/source/invalid-resources", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		// @formatter:on					
	}
	
	@Test
	public void thymeleafHtmlFreemarkerText() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
							.subject("Template")
							.content(new MultiTemplateContent("mixed/source/simple", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isSimilarHtml(resourceAsString("/template/mixed/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isSimilarHtml(resourceAsString("/template/mixed/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}


	
	@Test
	public void thymeleafHtmlStringFreemarkerTextString() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
							.subject("Template")
							.content(new MultiContent(
										new TemplateContent("s:${name} ${value}", new SimpleBean("foo", 42)),
										new TemplateContent("s:"+thymeleafTemplateString, new SimpleBean("foo", 42))))
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isSimilarHtml(resourceAsString("/template/mixed/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isSimilarHtml(resourceAsString("/template/mixed/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}

	private String thymeleafTemplateString = "<!DOCTYPE html>"
												+ "<html xmlns:th=\"http://www.thymeleaf.org\">"
												+ 	 "<head>"
												+        "<title>Thymeleaf simple</title>"
												+        "<meta charset=\"utf-8\" />"
												+    "</head>"
												+    "<body>"
												+        "<h1 class=\"title\" th:text=\"${name}\"></h1>"
												+        "<p class=\"text\" th:text=\"${value}\"></p>"
												+    "</body>"
												+ "</html>";

}
