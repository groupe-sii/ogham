package fr.sii.notification.sample.standard.email;

import java.util.Properties;

import fr.sii.notification.context.SimpleBean;
import fr.sii.notification.core.builder.NotificationBuilder;
import fr.sii.notification.core.exception.NotificationException;
import fr.sii.notification.core.message.content.StringTemplateContent;
import fr.sii.notification.core.service.NotificationService;
import fr.sii.notification.email.message.Email;

public class HtmlStringTemplateEmailSample {

	public static void main(String[] args) throws NotificationException {
		// configure properties (could be stored in a properties file or defined
		// in System properties)
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "<your server host>");
		properties.put("mail.smtp.port", "<your server port>");
		properties.put("notification.email.from", "<email address to display for the sender user>");
		// Instantiate the notification service using default behavior and
		// provided properties
		NotificationService service = new NotificationBuilder().useAllDefaults(properties).build();
		// send the email
		String template = "<!DOCTYPE html><html xmlns:th=\"http://www.thymeleaf.org\"><head><title>Thymeleaf simple</title><meta charset=\"utf-8\" /></head><body><h1 class=\"title\" th:text=\"${name}\"></h1><p class=\"text\" th:text=\"${value}\"></p></body></html>";
		service.send(new Email("subject", new StringTemplateContent(template, new SimpleBean("foo", 42)), "<recipient address>"));
	}

}
