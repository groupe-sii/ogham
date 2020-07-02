package fr.sii.ogham.runtime.runner;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.util.ClasspathUtils;
import fr.sii.ogham.sms.message.Sms;
import mock.context.SimpleBean;

public class SmsRunner {
	private final MessagingService messagingService;
	
	public SmsRunner(MessagingService messagingService) {
		super();
		this.messagingService = messagingService;
	}

	public void sendSmsWithoutTemplate() throws MessagingException {
		messagingService.send(new Sms()
				.message().string("Hello world !!")
				.from("+33601020304")
				.to("0709080706"));
	}

	public void sendSmsWithThymeleaf() throws MessagingException {
		messagingService.send(new Sms()
				.message().template("classpath:/sms/thymeleaf/source/simple-"+detectThymeleafEngineVersion()+".txt", new SimpleBean("foo", 42))
				.from("+33601020304")
				.to("0709080706"));
	}

	public void sendSmsWithFreemarker() throws MessagingException {
		messagingService.send(new Sms()
				.message().template("classpath:/sms/freemarker/source/simple.txt.ftl", new SimpleBean("foo", 42))
				.from("+33601020304")
				.to("0709080706"));
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
