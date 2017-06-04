package fr.sii.ogham.sample.standard.email;

import java.util.Properties;

import fr.sii.ogham.context.SimpleBean;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class HtmlTemplateSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "<your server host>");
		properties.setProperty("mail.smtp.port", "<your server port>");
		properties.setProperty("ogham.email.from", "<email address to display for the sender user>");
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
						.content(new TemplateContent("classpath:/template/thymeleaf/simple.html", new SimpleBean("foo", 42)))
						.to("ogham-test@yopmail.com"));
	}

}
