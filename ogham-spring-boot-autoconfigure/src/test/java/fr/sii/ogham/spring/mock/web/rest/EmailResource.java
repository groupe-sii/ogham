package fr.sii.ogham.spring.mock.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;

@RestController
@RequestMapping("api")
public class EmailResource {
	@Autowired
	MessagingService messagingService;
	
	@RequestMapping(value="email/simple", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public void simple(@RequestParam("subject") String subject, @RequestBody String content, @RequestParam("to") String to) throws MessagingException {
		messagingService.send(new Email(subject, content, to));
	}
	
	@RequestMapping(value="email/thymeleaf", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public void thymeleaf(@RequestParam("subject") String subject, @RequestParam("template") String template, @RequestBody Object context, @RequestParam("to") String to) throws MessagingException {
		messagingService.send(new Email(subject, new TemplateContent(template, context), to));
	}
}
