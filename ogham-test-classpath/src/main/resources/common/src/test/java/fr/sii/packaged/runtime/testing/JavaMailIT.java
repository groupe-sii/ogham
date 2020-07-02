package fr.sii.packaged.runtime.testing;

import static junit.filter.Assumptions.requires;

import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.service.MessagingService;

import fr.sii.ogham.runtime.runner.EmailRunner;
import fr.sii.ogham.runtime.checker.JavaMailChecker;

public class JavaMailIT {
	@Rule public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);
	
	JavaMailChecker checker;
	Properties props;
	
	@Before
	public void setup() {
		requires("email-javamail");

		props = new Properties();
		props.setProperty("mail.smtp.host", ServerSetupTest.SMTP.getBindAddress());
		props.setProperty("mail.smtp.port", String.valueOf(ServerSetupTest.SMTP.getPort()));

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
