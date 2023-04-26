package oghamall.it.email;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
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
import static org.hamcrest.Matchers.*;

@LogTestInformation
public class EmailEmptyTemplateTest {
	private MessagingService oghamService;

	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();

	@BeforeEach
	public void setUp() throws IOException {
		Properties additionalProperties = new Properties();
		additionalProperties.setProperty("mail.smtp.host", greenMail.getSmtp().getBindTo());
		additionalProperties.setProperty("mail.smtp.port", String.valueOf(greenMail.getSmtp().getPort()));
		oghamService = MessagingBuilder.standard().environment().properties("/application.properties").properties(additionalProperties).and().build();
	}


	@Test
	public void emptyThymeleafTemplateShouldNotReportAnError() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// TODO: MimeType of empty content string is application/octet-stream
		// (default mimetype). Then assertions utilities consider any part with
		// a mimetype that is not text/* as an attachment.
		// We could:
		// - either use a particular filter in this test to remove the first
		// part for example because we consider the only and first part it as
		// the main body (in this test there is no alternative and no
		// attachments so there should be only one part)
		// - or update attachment filter to check mime message structure to
		// determine which parts are really body/alternative parts and which
		// ones are attachments (not only be based on mimetype). This is not
		// trivial because technically body, alternative and
		// attachments may be everywhere. In real life not so many possibilities
		// because email clients have to retrieve information.
		// - or modify MimeTypProvider configuration used by
		// JavaMailContentHandler to provide text/plain as default mimetype
		// - or just consider this case as an edge case that will only happen
		// when developer starts using Ogham and its template is empty. So
		// nothing more to check

		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new TemplateContent("/template/thymeleaf/source/empty.html", new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body(nullValue())
				.alternative(nullValue());
		// @formatter:on
	}

	@Test
	public void emptyFreemarkerTemplateShouldNotReportAnError() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// TODO: MimeType of empty content string is application/octet-stream
		// (default mimetype). Then assertions utilities consider any part with
		// a mimetype that is not text/* as an attachment.
		// We could:
		// - either use a particular filter in this test to remove the first
		// part for example because we consider the only and first part it as
		// the main body (in this test there is no alternative and no
		// attachments so there should be only one part)
		// - or update attachment filter to check mime message structure to
		// determine which parts are really body/alternative parts and which
		// ones are attachments (not only be based on mimetype). This is not
		// trivial because technically body, alternative and
		// attachments may be everywhere. In real life not so many possibilities
		// because email clients have to retrieve information.
		// - or modify MimeTypProvider configuration used by
		// JavaMailContentHandler to provide text/plain as default mimetype
		// - or just consider this case as an edge case that will only happen
		// when developer starts using Ogham and its template is empty. So
		// nothing more to check

		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new TemplateContent("/template/freemarker/source/empty.html.ftl", new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body(nullValue())
				.alternative(nullValue());
		// @formatter:on
	}

	@Test
	public void emptyThymeleafMultiTemplateShouldNotReportAnError() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new MultiTemplateContent("/template/thymeleaf/source/empty", new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body(nullValue())
				.alternative(nullValue());
		// @formatter:on
	}
	
	@Test
	public void emptyFreemarkerMultiTemplateShouldNotReportAnError() throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.subject("Template")
								.content(new MultiTemplateContent("/template/freemarker/source/empty", new SimpleBean("foo", 42)))
								.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Template"))
				.from().address(hasItems("test.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body(nullValue())
				.alternative(nullValue());
		// @formatter:on
	}
	
}
