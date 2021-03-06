package fr.sii.ogham.sample.springboot.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

@SpringBootApplication
@PropertySource("application-email-basic.properties")	// just needed to be able to run the sample
public class BasicSample {

	public static void main(String[] args) throws MessagingException {
		SpringApplication.run(BasicSample.class, args);
	}
	
	@RestController
	public static class EmailController {
		// Messaging service is automatically created using Spring Boot features
		// The configuration can be set into application-email-basic.properties
		// The configuration files are stored into src/main/resources
		@Autowired
		MessagingService messagingService;
		
		@PostMapping(value="api/email/basic")
		@ResponseStatus(HttpStatus.CREATED)
		public void sendMail(@RequestParam("subject") String subject, @RequestParam("content") String content, @RequestParam("to") String to) throws MessagingException {
			// send the email using fluent API
			messagingService.send(new Email()
									.subject(subject)
									.body().string(content)
									.to(to));
		}
	}

}
