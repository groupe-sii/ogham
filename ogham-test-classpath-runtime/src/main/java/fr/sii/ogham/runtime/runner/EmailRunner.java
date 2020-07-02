package fr.sii.ogham.runtime.runner;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.email.message.Email;
import mock.context.SimpleBean;

public class EmailRunner {
	private final MessagingService messagingService;
	
	public EmailRunner(MessagingService messagingService) {
		super();
		this.messagingService = messagingService;
	}

	public void sendEmailWithoutTemplate() throws MessagingException {
		messagingService.send(new Email()
				.subject("Simple")
				.body().string("string body")
				.from("Sender Name <sender@sii.fr>")
				.to("Recipient Name <recipient@sii.fr>"));
	}

	public void sendEmailWithThymeleaf() throws MessagingException {
		messagingService.send(new Email()
				.subject("Thymeleaf")
				.body().template("classpath:/email/thymeleaf/source/simple-"+detectThymeleafEngineVersion(), new SimpleBean("foo", 42))
				.from("Sender Name <sender@sii.fr>")
				.to("Recipient Name <recipient@sii.fr>"));
	}

	public void sendEmailWithFreemarker() throws MessagingException {
		messagingService.send(new Email()
				.subject("Freemarker")
				.body().template("classpath:/email/freemarker/source/simple", new SimpleBean("foo", 42))
				.from("Sender Name <sender@sii.fr>")
				.to("Recipient Name <recipient@sii.fr>"));
	}

	public void sendEmailWithThymeleafAndFreemarker() throws MessagingException {
		messagingService.send(new Email()
				.subject("Thymeleaf+Freemarker")
				.body().template("classpath:/email/mixed/source/simple", new SimpleBean("foo", 42))
				.from("Sender Name <sender@sii.fr>")
				.to("Recipient Name <recipient@sii.fr>"));
	}


	private String detectThymeleafEngineVersion() {
		if (ClasspathUtils.exists("org.thymeleaf.TemplateEngine") && ClasspathUtils.exists("org.thymeleaf.IEngineConfiguration")) {
			return "v3";
		}
		if (ClasspathUtils.exists("org.thymeleaf.TemplateEngine") && !ClasspathUtils.exists("org.thymeleaf.IEngineConfiguration")) {
			return "v2";
		}
		throw new IllegalStateException("Unknown Thymeleaf engine version");
	}
}
