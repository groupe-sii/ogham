package fr.sii.ogham.sample.standard.email;

import java.io.IOException;
import java.util.Properties;

import fr.sii.ogham.context.SimpleBean;
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
 * <li>Properties are loaded from external file</li>
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
 * The HTML template also references external CSS and images that are avilable
 * in src/main/resources/resources.
 * 
 * @author Aurélien Baudet
 *
 */
public class FullSample {

	public static void main(String[] args) throws MessagingException, IOException {
		// configure properties from file
		Properties properties = new Properties();
		properties.load(FullSample.class.getResourceAsStream("/email-template.properties"));
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
		// send the email using fluent API
		// @formatter:off
		service.send(new Email().
						content(new MultiTemplateContent("full", new SimpleBean("foo", 42))).
						to("<recipient address>").
						attach(new Attachment("/attachment/test.pdf")));
		// @formatter:on
	}

}
