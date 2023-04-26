package oghamall.it.email;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.StringTemplateContent;
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
import static org.hamcrest.Matchers.*;


@LogTestInformation
public class EmailTemplateStringTest {
	private MessagingService oghamService;
	private final String thymeleafHtmlTemplate = "<!DOCTYPE html>"
			+ "<html xmlns:th=\"http://www.thymeleaf.org\">"
			+ "    <head>"
			+ "        <title>Thymeleaf simple</title>"
			+ "        <meta charset=\"utf-8\" />"
			+ "    </head>"
			+ "    <body>"
			+ "        <h1 class=\"title\" th:text=\"${name}\"></h1>"
			+ "        <p class=\"text\" th:text=\"${value}\"></p>"
			+ "    </body>"
			+ "</html>";
	private final String thymeleafTextTemplate = "[[${name}]] [[${value}]]";
	private final String freemarkerHtmlTemplate = "<!DOCTYPE html>\n" + 
			"<html>\n" + 
			"    <head>\n" + 
			"        <title>FreeMarker simple</title>\n" + 
			"        <meta charset=\"utf-8\" />\n" + 
			"    </head>\n" + 
			"    <body>\n" + 
			"        <h1 class=\"title\">${name}</h1>\n" + 
			"        <p class=\"text\">${value}</p>\n" + 
			"    </body>\n" + 
			"</html>";
	private final String freemarkerTextTemplate = "${name} ${value}";
	

	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();
	
	@BeforeEach
	public void setUp() throws IOException {
		Properties additionalProperties = new Properties();
		additionalProperties.setProperty("mail.smtp.host", greenMail.getSmtp().getBindTo());
		additionalProperties.setProperty("mail.smtp.port", String.valueOf(greenMail.getSmtp().getPort()));
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties(additionalProperties)
					.and()
				.build();
	}
	
	@Test
	public void htmlTemplateStringWithThymeleaf() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new StringTemplateContent(thymeleafHtmlTemplate, new SimpleBean("foo", 42)))
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
	public void textTemplateStringWithThymeleaf() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new StringTemplateContent(thymeleafTextTemplate, new SimpleBean("foo", 42)))
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
	public void htmlTemplateStringWithFreemarker() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new StringTemplateContent(freemarkerHtmlTemplate, new SimpleBean("foo", 42)))
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
	public void textTemplateStringWithFreemarker() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new StringTemplateContent(freemarkerTextTemplate, new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/freemarker/expected/simple_foo_42.txt")))
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}}
