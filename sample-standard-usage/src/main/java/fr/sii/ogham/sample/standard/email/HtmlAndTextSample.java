package fr.sii.ogham.sample.standard.email;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class HtmlAndTextSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "<your server host>");
		properties.put("mail.smtp.port", "<your server port>");
		properties.put("notification.email.from", "<email address to display for the sender user>");
		// Instantiate the notification service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the email
		String html = "<!DOCTYPE html><html><head><meta charset=\"utf-8\" /></head><body><h1 class=\"title\">Hello World</h1><p class=\"text\">Foo bar</p></body></html>";
		String text = "Hello World !\r\nFoo bar";
		service.send(new Email("subject", new MultiContent(html, text), "<recipient address>"));
	}

}
