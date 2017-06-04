package fr.sii.ogham.sample.standard.sms;

import java.util.Properties;

import fr.sii.ogham.context.SimpleBean;
import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.sms.message.Sms;

public class TemplateSample {

	public static void main(String[] args) throws MessagingException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.setProperty("ogham.sms.smpp.host", "<your server host>");
		properties.setProperty("ogham.sms.smpp.port", "<your server port>");
		properties.setProperty("ogham.sms.smpp.system-id", "<your server system ID>");
		properties.setProperty("ogham.sms.smpp.password", "<your server password>");
		properties.setProperty("ogham.sms.from", "<phone number to display for the sender>");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = MessagingBuilder.standard()
				.environment()
					.properties(properties)
					.and()
				.build();
		// send the sms using fluent API
		service.send(new Sms()
						.content(new TemplateContent("classpath:/template/thymeleaf/simple.txt", new SimpleBean("foo", 42)))
						.to("+33752962193"));
	}

}
