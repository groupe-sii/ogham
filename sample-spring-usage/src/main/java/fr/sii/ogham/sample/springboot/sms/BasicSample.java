package fr.sii.ogham.sample.springboot.sms;

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
import fr.sii.ogham.sms.message.Sms;

@SpringBootApplication
@PropertySource("application-sms-basic.properties")	// just needed to be able to run the sample
public class BasicSample {

	public static void main(String[] args) throws MessagingException {
		SpringApplication.run(BasicSample.class, args);
	}
	
	@RestController
	public static class SmsController {
		// Messaging service is automatically created using Spring Boot features
		// The configuration can be set into application-sms-basic.properties
		// The configuration files are stored into src/main/resources
		@Autowired
		MessagingService messagingService;
		
		@RequestMapping(value="api/sms/basic", method=RequestMethod.POST)
		@ResponseStatus(HttpStatus.CREATED)
		public void sendSms(@RequestParam("content") String content, @RequestParam("to") String to) throws MessagingException {
			// send the SMS
			messagingService.send(new Sms(content, to));
			// or using fluent API
			messagingService.send(new Sms().
									content(content).
									to(to));
		}
	}

}
