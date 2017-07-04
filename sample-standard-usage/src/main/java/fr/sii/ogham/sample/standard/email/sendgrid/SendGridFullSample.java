package fr.sii.ogham.sample.standard.email.sendgrid;

import java.io.IOException;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.message.Email;

/**
 * This sample shows how to send email with following characteristics:
 * <ul>
 * <li>Use templates</li>
 * <li>Use template prefix</li>
 * <li>The HTML template uses external CSS and images</li>
 * <li>The HTML template loads page fragments</li>
 * <li>The subject is extracted from templates</li>
 * <li>Send HTML email with text fallback</li>
 * <li>Add attachments to the email</li>
 * <li>Properties are loaded from external file and API key is set in code</li>
 * </ul>
 * 
 * <p>
 * The templates are available in src/main/resources/template/thymeleaf/email:
 * <ul>
 * <li>full.html</li>
 * <li>full.txt</li>
 * </ul>
 * 
 * <p>
 * The HTML template uses a page fragment that is available in
 * src/main/resources/template/thymeleaf/email/fragments/header.html.
 * 
 * <p>
 * The HTML template also references external CSS and images that are available
 * in src/main/resources/resources.
 * 
 * @author Aurélien Baudet
 *
 */
public class SendGridFullSample {
	public static void main(String[] args) throws MessagingException, IOException {
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = MessagingBuilder.standard()
				.environment()
					.properties("/sendgrid-template.properties")									// <1>
					.properties()
						.set("ogham.email.sengrid.api-key", "<your sendgrid API key>")				// <2>
						.and()
					.and()
				.build();
		// send the email using fluent API
		service.send(new Email()
						.content(new MultiTemplateContent("full", new SimpleBean("foo", 42)))		// <3>
						.to("ogham-test@yopmail.com")
						.attach(new Attachment("/attachment/test.pdf")));
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
