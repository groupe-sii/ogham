package oghamall.it.email;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
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
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import mock.context.SimpleBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.util.Properties;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@LogTestInformation
public class EmailMultiTemplateTest {
	private MessagingService oghamService;
	
	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();
	
	@BeforeEach
	public void setUp() throws IOException {
		Properties additionalProps = new Properties();
		additionalProps.setProperty("mail.smtp.host", greenMail.getSmtp().getBindTo());
		additionalProps.setProperty("mail.smtp.port", String.valueOf(greenMail.getSmtp().getPort()));
		additionalProps.setProperty("ogham.email.template.path-prefix", "/template/");
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties(additionalProps)
					.and()
				.build();
	}
	
	@Test
	public void withThymeleafMulti() throws MessagingException, jakarta.mail.MessagingException, IOException {
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
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void withThymeleafMissingVariant() throws MessagingException, jakarta.mail.MessagingException, IOException {
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
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void withThymeleafInvalidPath() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			oghamService.send(new Email()
					.subject("Template")
					.content(new MultiTemplateContent("thymeleaf/source/unexisting", new SimpleBean("foo", 42)))
					.to("recipient@sii.fr"));
		}, "should throw");
		// @formatter:on					
		assertThat("should indicate cause", e.getCause(), instanceOf(NoContentException.class));
	}
	
	@Test
	public void withThymeleafOneVariantWithParsingError() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			oghamService.send(new Email()
							.content(new MultiTemplateContent("thymeleaf/source/parsing-error", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		}, "should throw");
		// @formatter:on					
		assertThat("should indicate cause", e.getCause(), instanceOf(TemplateParsingFailedException.class));
	}
	
	@Test
	public void withThymeleafOneVariantWithInvalidResourcePath() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			oghamService.send(new Email()
							.content(new MultiTemplateContent("thymeleaf/source/invalid-resources", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		}, "should throw");
		// @formatter:on					
		assertThat("should indicate cause", e.getCause(), instanceOf(ImageInliningException.class));
	}
	
	@Test
	public void withFreemarkerMulti() throws MessagingException, jakarta.mail.MessagingException, IOException {
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
					.contentAsString(isIdenticalHtml(resourceAsString("/template/freemarker/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/freemarker/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void withFreemarkerMissingVariant() throws MessagingException, jakarta.mail.MessagingException, IOException {
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
					.contentAsString(isIdenticalHtml(resourceAsString("/template/freemarker/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on					
	}
	
	@Test
	public void withFreemarkerInvalidPath() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			oghamService.send(new Email()
							.subject("Template")
							.content(new MultiTemplateContent("freemarker/source/unexisting", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		}, "should throw");
		// @formatter:on					
		assertThat("should indicate cause", e.getCause(), instanceOf(NoContentException.class));
	}
	
	@Test
	public void withFreemarkerOneVariantWithParsingError() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			oghamService.send(new Email()
							.content(new MultiTemplateContent("freemarker/source/parsing-error", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		}, "should throw");
		// @formatter:on					
		assertThat("should indicate cause", e.getCause(), instanceOf(TemplateParsingFailedException.class));
	}

	@Test
	public void withFreemarkerOneVariantWithInvalidResourcePath() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		MessageNotSentException e = assertThrows(MessageNotSentException.class, () -> {
			oghamService.send(new Email()
							.content(new MultiTemplateContent("freemarker/source/invalid-resources", new SimpleBean("foo", 42)))
							.to("recipient@sii.fr"));
		}, "should throw");
		// @formatter:on					
		assertThat("should indicate cause", e.getCause(), instanceOf(ImageInliningException.class));
	}
	
	@Test
	public void thymeleafHtmlFreemarkerText() throws MessagingException, jakarta.mail.MessagingException, IOException {
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
					.contentAsString(isIdenticalHtml(resourceAsString("/template/mixed/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/mixed/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.attachments(emptyIterable());
		// @formatter:on					
	}


	
	@Test
	public void thymeleafHtmlStringFreemarkerTextString() throws MessagingException, jakarta.mail.MessagingException, IOException {
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
					.contentAsString(isIdenticalHtml(resourceAsString("/template/mixed/expected/simple_foo_42.html")))
					.contentType(startsWith("text/html")).and()
				.alternative()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/mixed/expected/simple_foo_42.txt")))
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
