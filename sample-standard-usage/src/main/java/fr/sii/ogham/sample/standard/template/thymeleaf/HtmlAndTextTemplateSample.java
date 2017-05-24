package fr.sii.ogham.sample.standard.template.thymeleaf;

import java.util.Properties;

import fr.sii.ogham.context.SimpleBean;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

public class HtmlAndTextTemplateSample {

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
		// Note that the extension of the template is not given. This version
		// automatically takes the provided path and adds the '.html' extension
		// for the HTML template and '.txt' for text template
		service.send(new Email().
						subject("subject").
						content(new MultiTemplateContent("classpath:/template/thymeleaf/simple", new SimpleBean("foo", 42))).
						to("<recipient address>"));
	}

}
