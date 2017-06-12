package fr.sii.ogham.sample.springboot.email.gmail;

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
@PropertySource("application-gmail-ssl.properties")				// <1>
public class BasicGmailSSLSample {

	public static void main(String[] args) throws MessagingException {
		SpringApplication.run(BasicGmailSSLSample.class, args);
	}
	
	@RestController
	public static class BasicController {
		// Messaging service is automatically created using Spring Boot features
		// The configuration can be set into application-gmail-ssl.properties
		// The configuration files are stored into src/main/resources
		@Autowired
		MessagingService messagingService;						// <2>
		
		@RequestMapping(value="api/email/gmail", method=RequestMethod.POST)
		@ResponseStatus(HttpStatus.CREATED)
		public void sendMail(@RequestParam("subject") String subject, @RequestParam("content") String content, @RequestParam("to") String to) throws MessagingException {
			// send the email using fluent API
			messagingService.send(new Email().					// <3>
									subject(subject).
									content(content).
									to(to));
		}
	}

}
