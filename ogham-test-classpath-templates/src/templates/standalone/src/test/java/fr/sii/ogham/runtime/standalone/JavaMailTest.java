package fr.sii.ogham.runtime.standalone;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.runtime.checker.JavaMailChecker;
import fr.sii.ogham.runtime.runner.EmailRunner;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import junit.filter.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Properties;

public class JavaMailTest {
	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();
	
	EmailRunner runner;
	JavaMailChecker checker;
	
	@BeforeEach
	public void setup() {
		Assumptions.requires("email-javamail");

		Properties props = new Properties();
		props.setProperty("mail.smtp.host", greenMail.getSmtp().getBindTo());
		props.setProperty("mail.smtp.port", String.valueOf(greenMail.getSmtp().getPort()));
		
		MessagingService service = new StandaloneApp().init(props);
		runner = new EmailRunner(service);
		checker = new JavaMailChecker(greenMail);
	}

	@Test
	public void emailWithoutTemplate() throws Exception {
		runner.sendEmailWithoutTemplate();
		checker.assertEmailWithoutTemplate();
	}
	
	@Test
	public void emailWithThymeleaf() throws Exception {
		Assumptions.requires("template-thymeleaf");
		
		runner.sendEmailWithThymeleaf();
		checker.assertEmailWithThymeleaf();
	}
	
	@Test
	public void emailWithFreemarker() throws Exception {
		Assumptions.requires("template-freemarker");
		
		runner.sendEmailWithFreemarker();
		checker.assertEmailWithFreemarker();
	}
	
	@Test
	public void emailWithThymeleafAndFreemarker() throws Exception {
		Assumptions.requires("template-thymeleaf", "template-freemarker");
		
		runner.sendEmailWithThymeleafAndFreemarker();
		checker.assertEmailWithThymeleafAndFreemarker();
	}
}
