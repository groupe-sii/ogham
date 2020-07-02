package fr.sii.packaged.runtime.testing;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static junit.filter.Assumptions.requires;

import java.util.Properties;

import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import fr.sii.ogham.runtime.runner.EmailRunner;
import fr.sii.ogham.runtime.checker.SendGridChecker;
import fr.sii.ogham.runtime.checker.SendGridCheckerProvider;
import fr.sii.ogham.runtime.checker.AutoSendGridCheckerProvider;

public class SendGridIT {
	public final @Rule WireMockRule wiremock = new WireMockRule(options().dynamicPort());
	SendGridChecker checker;
	Properties props;
	
	@Before
	public void setup() {
		requires("email-sendgrid");

		props = new Properties();
		props.setProperty("ogham.email.sendgrid.api-key", "foobar");
		props.setProperty("ogham.email.sendgrid.url", "http://localhost:"+wiremock.port());

		SendGridCheckerProvider provider = new AutoSendGridCheckerProvider();
		checker = provider.get(wiremock);
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
