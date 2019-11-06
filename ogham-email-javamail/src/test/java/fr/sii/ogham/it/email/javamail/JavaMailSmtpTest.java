package fr.sii.ogham.it.email.javamail;

import static fr.sii.ogham.assertion.OghamAssertions.assertThat;
import static fr.sii.ogham.assertion.OghamAssertions.resource;
import static fr.sii.ogham.helper.email.EmailUtils.ATTACHMENT_DISPOSITION;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.builder.javamail.JavaMailBuilder;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.message.EmailAddress;
import fr.sii.ogham.email.sender.impl.JavaMailSender;
import fr.sii.ogham.junit.LoggingTestRule;

public class JavaMailSmtpTest {
	private JavaMailSender sender;
	
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	
	@Before
	public void setUp() throws IOException {
		Properties additionalProps = new Properties();
		additionalProps.setProperty("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress());
		additionalProps.setProperty("mail.smtp.port", String.valueOf(ServerSetupTest.SMTP.getPort()));
		sender = new JavaMailBuilder()
				.environment()
					.systemProperties()
					.properties(additionalProps)
					.and()
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
				.attachment("04-Java-OOP-Basics.pdf")
					.content(is(resource("/attachment/04-Java-OOP-Basics.pdf")))
					.contentType(startsWith("application/pdf"))
					.filename(is("04-Java-OOP-Basics.pdf"))
					.disposition(is(ATTACHMENT_DISPOSITION));
		// @formatter:on
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void missingRecipientIsInvalid() throws MessageException {
		// @formatter:off
		sender.send(new Email()
							.subject("subject")
							.content("content"));
		// @formatter:on
	}
}
