package oghamall.it.email;

import static fr.sii.ogham.email.attachment.ContentDisposition.INLINE;
import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resource;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.greenmail.RandomPortGreenMailRule;
import fr.sii.ogham.testing.extension.junit.LoggingTestRule;

public class EmailResourceInliningTest {
	private MessagingService oghamService;
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final GreenMailRule greenMail = new RandomPortGreenMailRule();
	
	@Before
	public void setUp() throws IOException {
		Properties additionalProperties = new Properties();
		additionalProperties.setProperty("mail.smtp.host", greenMail.getSmtp().getBindTo());
		additionalProperties.setProperty("mail.smtp.port", String.valueOf(greenMail.getSmtp().getPort()));
		additionalProperties.setProperty("ogham.email.template.path-prefix", "/template/thymeleaf/source/");
		additionalProperties.setProperty("ogham.email.template.path-suffix", ".html");
		oghamService = MessagingBuilder.standard()
				.environment()
					.properties("/application.properties")
					.properties(additionalProperties)
					.and()
				.build();
	}
	
	@Test
	public void inlineResources() throws MessagingException, javax.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.body().template("inline-resources", null)
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/template/thymeleaf/expected/inline-resources.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(hasSize(2))
				.attachment(0)
					.content(is(resource("/template/thymeleaf/source/images/h1.gif")))
					.contentType(startsWith("image/gif"))
					.disposition(is(INLINE))
					.header("Content-ID", contains("<0>"))
					.and()
				.attachment(1)
					.content(is(resource("/template/thymeleaf/source/images/header-bande-verticale-1px300px-degrade.jpg")))
					.contentType(startsWith("image/jpeg"))
					.disposition(is(INLINE))
					.header("Content-ID", contains("<1>"));
		// @formatter:on
	}

}
