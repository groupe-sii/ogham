package fr.sii.ogham.runtime.common;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import fr.sii.ogham.runtime.checker.AutoSendGridCheckerProvider;
import fr.sii.ogham.runtime.checker.SendGridChecker;
import fr.sii.ogham.runtime.checker.SendGridCheckerProvider;
import fr.sii.ogham.runtime.runner.EmailRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Properties;

import static junit.filter.Assumptions.requires;

public class SendGridIT {
	public final @RegisterExtension WireMockExtension wiremock = new WireMockExtension();
	SendGridChecker checker;
	Properties props;
	
	@BeforeEach
	public void setup() {
		requires("email-sendgrid");

		props = new Properties();
		props.setProperty("ogham.email.sendgrid.api-key", "foobar");
		props.setProperty("ogham.email.sendgrid.url", "http://localhost:"+wiremock.getPort());

		SendGridCheckerProvider provider = new AutoSendGridCheckerProvider();
		checker = provider.get(wiremock.getRuntimeInfo().getWireMock());
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
