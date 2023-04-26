package oghamjavamail.it;

import fr.sii.ogham.core.exception.InvalidMessageException;
import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.builder.javamail.JavaMailBuilder;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.email.sender.impl.JavaMailSender;
import fr.sii.ogham.testing.extension.common.LogTestInformation;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import ogham.testing.jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.File;
import java.io.IOException;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.testing.assertion.util.EmailUtils.ATTACHMENT_DISPOSITION;
import static fr.sii.ogham.testing.util.ResourceUtils.resource;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@LogTestInformation
public class JavaMailSmtpTest {
	private JavaMailSender sender;
	
	@RegisterExtension public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();
	
	@BeforeEach
	public void setUp() throws IOException {
		sender = new JavaMailBuilder()
				.host(greenMail.getSmtp().getBindTo())
				.port(greenMail.getSmtp().getPort())
				.mimetype()
					.tika()
						.failIfOctetStream(false)
						.and()
					.and()
				.build();
	}
	
	@Test
	public void simple() throws MessageException, MessagingException {
		// @formatter:off
		sender.send(new Email()
							.subject("Subject")
							.content("Body")
							.from(new EmailAddress("custom.sender@sii.fr"))
							.to("recipient@sii.fr"));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Subject"))
				.from().address(hasItems("custom.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(is("Body"))
					.contentType(startsWith("text/plain")).and()
				.alternative(nullValue())
				.attachments(emptyIterable());
		// @formatter:on
	}
	
	@Test
	public void attachment() throws MessageException, MessagingException, IOException {
		// @formatter:off
		sender.send(new Email()
							.subject("Subject")
							.content("Body")
							.from(new EmailAddress("custom.sender@sii.fr"))
							.to("recipient@sii.fr")
							.attach(new Attachment(new File(getClass().getResource("/attachment/04-Java-OOP-Basics.pdf").getFile()))));
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.subject(is("Subject"))
				.from().address(hasItems("custom.sender@sii.fr")).and()
				.to().address(hasItems("recipient@sii.fr")).and()
				.body()
					.contentAsString(is("Body"))
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
	public void missingRecipientIsInvalid() throws MessageException {
		// @formatter:off
		assertThrows(InvalidMessageException.class, () -> {
			sender.send(new Email()
					.subject("subject")
					.content("content"));
		});
		// @formatter:on
	}
}
