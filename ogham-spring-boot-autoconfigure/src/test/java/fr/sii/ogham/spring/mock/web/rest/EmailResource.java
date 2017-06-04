package fr.sii.ogham.spring.mock.web.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
	
	@RequestMapping(value="email/simple", method=POST)
	@ResponseStatus(CREATED)
	public void simple(@RequestParam("subject") String subject, @RequestBody String content, @RequestParam("to") String to) throws MessagingException {
		messagingService.send(new Email()
								.subject(subject)
								.content(content)
								.to(to));
	}
	
	@RequestMapping(value="email/thymeleaf", method=POST)
	@ResponseStatus(CREATED)
	public void thymeleaf(@RequestParam("subject") String subject, @RequestParam("template") String template, @RequestBody Object context, @RequestParam("to") String to) throws MessagingException {
		messagingService.send(new Email()
								.subject(subject)
								.content(new TemplateContent(template, context))
								.to(to));
	}
	
	@RequestMapping(value="email/freemarker", method=POST)
	@ResponseStatus(CREATED)
	public void freemarker(@RequestParam("subject") String subject, @RequestParam("template") String template, @RequestBody Object context, @RequestParam("to") String to) throws MessagingException {
		messagingService.send(new Email()
								.subject(subject)
								.content(new TemplateContent(template, context))
								.to(to));
	}
}
