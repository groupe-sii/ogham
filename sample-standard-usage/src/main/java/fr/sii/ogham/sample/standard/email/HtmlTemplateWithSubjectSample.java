package fr.sii.ogham.sample.standard.email;

import java.util.Properties;

import fr.sii.ogham.context.SimpleBean;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class HtmlTemplateWithSubjectSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "<your server host>");
		properties.setProperty("mail.smtp.port", "<your server port>");
		properties.setProperty("ogham.email.from", "<email address to display for the sender user>");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the email
		// subject is set to null to let automatic mechanism to read the title
		// of the HTML and use it as subject of your email
		service.send(new Email(null, new TemplateContent("classpath:/template/thymeleaf/simpleWithSubject.html", new SimpleBean("foo", 42)), "<recipient address>"));
	}

}
