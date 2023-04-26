package fr.sii.ogham.runtime.common;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.runtime.checker.JavaMailChecker;
import fr.sii.ogham.runtime.runner.EmailRunner;
import fr.sii.ogham.testing.extension.junit.email.RandomPortGreenMailExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Properties;

import static junit.filter.Assumptions.requires;

public class JavaMailIT {
	@RegisterExtension
	public final GreenMailExtension greenMail = new RandomPortGreenMailExtension();
	
	JavaMailChecker checker;
	Properties props;
	
	@BeforeEach
	public void setup() {
		requires("email-javamail");

		props = new Properties();
		props.setProperty("mail.smtp.host", greenMail.getSmtp().getBindTo());
		props.setProperty("mail.smtp.port", String.valueOf(greenMail.getSmtp().getPort()));

		checker = new JavaMailChecker(greenMail);
	}

	@Test
	public void emailWithoutTemplate() throws Exception {
		CommandLineTestRunner.run(EmailRunner.class, "sendEmailWithoutTemplate", props);
		checker.assertEmailWithoutTemplate();
	}
	
	@Test
	public void emailWithThymeleaf() throws Exception {
		requires("template-thymeleaf");
		
		CommandLineTestRunner.run(EmailRunner.class, "sendEmailWithThymeleaf", props);
		checker.assertEmailWithThymeleaf();
	}
	
	@Test
	public void emailWithFreemarker() throws Exception {
		requires("template-freemarker");
		
		CommandLineTestRunner.run(EmailRunner.class, "sendEmailWithFreemarker", props);
		checker.assertEmailWithFreemarker();
	}
	
	@Test
	public void emailWithThymeleafAndFreemarker() throws Exception {
		requires("template-thymeleaf", "template-freemarker");
		
		CommandLineTestRunner.run(EmailRunner.class, "sendEmailWithThymeleafAndFreemarker", props);
		checker.assertEmailWithThymeleafAndFreemarker();
	}
}
