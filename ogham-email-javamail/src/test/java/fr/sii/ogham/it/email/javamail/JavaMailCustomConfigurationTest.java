package fr.sii.ogham.it.email.javamail;

import static fr.sii.ogham.assertion.OghamAssertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.builder.javamail.JavaMailBuilder;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.helper.rule.LoggingTestRule;

public class JavaMailCustomConfigurationTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	
	@Test(expected=MessageNotSentException.class)
	public void noHostDefinedShouldFail() throws MessagingException {
		MessagingService service = MessagingBuilder.empty()
				.email()
					.sender(JavaMailBuilder.class)
					.mimetype()
						.tika()
							.failIfOctetStream(false)
							.and()
						.and()
					.and()
				.and()
				.build();
		// send the message
		service.send(new Email()
				.from("noreply@foo.bar")
				.to("recipient@foo.bar")
				.content("test")
				.subject("subject"));
	}

	@Test
	public void oghamPropertyShouldOverride() throws MessagingException {
		Properties additionalProps = new Properties();
		additionalProps.setProperty("ogham.email.javamail.host", ServerSetupTest.SMTP.getBindAddress());
		additionalProps.setProperty("ogham.email.javamail.port", String.valueOf(ServerSetupTest.SMTP.getPort()));
		additionalProps.setProperty("mail.smtp.host", "value of mail.smtp.host");
		additionalProps.setProperty("mail.smtp.port", "value of mail.smtp.port");
		additionalProps.setProperty("mail.host", "value of mail.host");
		additionalProps.setProperty("mail.port", "value of mail.port");
		MessagingService service = MessagingBuilder.empty()
				.email()
					.sender(JavaMailBuilder.class)
					.environment()
						.properties(additionalProps)
						.and()
					.mimetype()
						.tika()
							.failIfOctetStream(false)
							.and()
						.and()
					.host("${ogham.email.javamail.host}", "${mail.smtp.host}", "${mail.host}")
					.port("${ogham.email.javamail.port}", "${mail.smtp.port}", "${mail.port}")
					.and()
				.and()
				.build();
		// send the message
		service.send(new Email()
				.from("noreply@foo.bar")
				.to("recipient@foo.bar")
				.content("test")
				.subject("subject"));
		assertThat(greenMail).receivedMessages().count(is(1));
	}
	
	@Test
	public void directValueShouldOverride() throws MessagingException {
		Properties additionalProps = new Properties();
		additionalProps.setProperty("ogham.email.javamail.host", "value of ogham.email.javamail.host");
		additionalProps.setProperty("ogham.email.javamail.port", "value of ogham.email.javamail.port");
		additionalProps.setProperty("mail.smtp.host", "value of mail.smtp.host");
		additionalProps.setProperty("mail.smtp.port", "value of mail.smtp.port");
		additionalProps.setProperty("mail.host", "value of mail.host");
		additionalProps.setProperty("mail.port", "value of mail.port");
		MessagingService service = MessagingBuilder.empty()
				.email()
					.sender(JavaMailBuilder.class)
					.environment()
						.properties(additionalProps)
						.and()
					.mimetype()
						.tika()
							.failIfOctetStream(false)
							.and()
						.and()
					.host("${ogham.email.javamail.host}", "${mail.smtp.host}", "${mail.host}")
					.host(ServerSetupTest.SMTP.getBindAddress())
					.port("${ogham.email.javamail.port}", "${mail.smtp.port}", "${mail.port}")
					.port(ServerSetupTest.SMTP.getPort())
					.and()
				.and()
				.build();
		// send the message
		service.send(new Email()
				.from("noreply@foo.bar")
				.to("recipient@foo.bar")
				.content("test")
				.subject("subject"));
		assertThat(greenMail).receivedMessages().count(is(1));
	}
}
