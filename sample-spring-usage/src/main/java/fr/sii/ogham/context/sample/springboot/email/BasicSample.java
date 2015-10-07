package fr.sii.ogham.context.sample.springboot.email;

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
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

@SpringBootApplication
@PropertySource("application-basic.properties")
public class BasicSample {

	public static void main(String[] args) throws MessagingException {
		SpringApplication.run(BasicSample.class, args);
	}
	
	@RestController
	public static class BasicController {
		// Messaging service is automatically created using Spring Boot features
		// The configuration can be set into application-basic.properties
		// The configuration files are stored into src/main/resources
		@Autowired
		MessagingService messagingService;
		
		@RequestMapping(value="api/email", method=RequestMethod.POST)
		@ResponseStatus(HttpStatus.CREATED)
		public void sendMail(@RequestParam("subject") String subject, @RequestParam("content") String content, @RequestParam("to") String to) throws MessagingException {
			// send the email
			messagingService.send(new Email(subject, content, to));
			// or using fluent API
			messagingService.send(new Email().
									subject(subject).
									content(content).
									to(to));
		}
	}

}
