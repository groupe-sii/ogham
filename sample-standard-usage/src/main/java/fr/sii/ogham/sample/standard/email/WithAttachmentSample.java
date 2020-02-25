package fr.sii.ogham.sample.standard.email;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class WithAttachmentSample {
	public static void main(String[] args) throws MessagingException, IOException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "<your server host>");
		properties.put("mail.smtp.port", "<your server port>");
		properties.put("ogham.email.from", "<email address to display for the sender user>");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = MessagingBuilder.standard()
				.environment()
					.properties(properties)
					.and()
				.build();
		// send the email using fluent API
		service.send(new Email()
						.subject("subject")
						.body().string("content of the email")
						.to("ogham-test@yopmail.com")
						.attach().resource("classpath:/attachment/test.pdf")			// <1>
						.attach().stream("from-stream.pdf", loadInputStream()));		// <2>
	}

	private static InputStream loadInputStream() {
		return WithAttachmentSample.class.getResourceAsStream("/attachment/test.pdf");
	}
}
