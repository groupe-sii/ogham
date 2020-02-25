package fr.sii.ogham.sample.standard.template.freemarker;

import java.util.Properties;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
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
		// Note that the extension of the template is not given. This version
		// automatically takes the provided path and adds the '.html.ftl' extension
		// for the HTML template and '.txt.ftl' for text template
		// send the email using fluent API
		service.send(new Email()
						.subject("subject")
						.body().template("classpath:/template/freemarker/simple", new SimpleBean("foo", 42))
						.to("<recipient address>"));
	}

	public static class SimpleBean {
		private String name;
		private int value;
		public SimpleBean(String name, int value) {
			super();
			this.name = name;
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public int getValue() {
			return value;
		}
	}
}
