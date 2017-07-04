package fr.sii.ogham.sample.springboot.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

@SpringBootApplication
@PropertySource("application-email-template.properties")	// just needed to be able to run the sample
public class ThymeleafHtmlAndFreemarkerTextSample {

	public static void main(String[] args) throws MessagingException {
		SpringApplication.run(ThymeleafHtmlAndFreemarkerTextSample.class, args);
	}
	
	@RestController
	public static class EmailController {
		// Messaging service is automatically created using Spring Boot features
		// The configuration can be set into application-email-template.properties
		// The configuration files are stored into src/main/resources
		// The configuration file set the prefix for templates into email folder available in src/main/resources
		@Autowired
		MessagingService messagingService;															// <1>
		
		@RequestMapping(value="api/email/multitemplate/mixed", method=RequestMethod.POST)
		@ResponseStatus(HttpStatus.CREATED)
		public void sendEmail(@RequestParam("to") String to, @RequestParam("name") String name, @RequestParam("value") int value) throws MessagingException {
			// using fluent API
			messagingService.send(new Email()														// <2>
									.content(new MultiTemplateContent("mixed", 						// <3>
																	new SimpleBean(name, value)))	// <4>
									.to(to));														// <5>
		}
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
