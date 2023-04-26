package oghamall.it.html.translator;

import ogham.testing.com.icegreen.greenmail.util.GreenMail;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.GreenMailServer;
import mock.context.SimpleBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.OghamMatchers.isIdenticalHtml;
import static fr.sii.ogham.testing.util.ResourceUtils.resourceAsString;
import static org.hamcrest.Matchers.*;

@LogTestInformation
@GreenMailServer
public class EmailBase64ImageInliningTest {
	private MessagingService oghamService;
	

	@BeforeEach
	public void setUp(GreenMail greenMail) throws IOException {
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
	public void imageFormats(GreenMail greenMail) throws MessagingException, jakarta.mail.MessagingException, IOException {
		// @formatter:off
		oghamService.send(new Email()
								.content(new TemplateContent("/inliner/images/jsoup/source/differentImageFormats.html", new SimpleBean("foo", 42)))
								.to("aurelien.baudet@gmail.com"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.body()
					.contentAsString(isIdenticalHtml(resourceAsString("/inliner/images/jsoup/expected/differentImageFormatsBase64.html")))
					.contentType(startsWith("text/html")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}

}
