package fr.sii.standalone.runtime.testing;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static junit.filter.Assumptions.requires;

import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import fr.sii.ogham.core.service.MessagingService;

import fr.sii.ogham.runtime.runner.EmailRunner;
import fr.sii.ogham.runtime.checker.SendGridV4Checker;

public class SendGridTest {
	public final @Rule WireMockRule wiremock = new WireMockRule(options().dynamicPort());
	EmailRunner runner;
	SendGridV4Checker checker;
	
	@Before
	public void setup() {
		requires("email-sendgrid");

		wiremock.start();
		wiremock.stubFor(post("/v3/mail/send")
				.willReturn(aResponse().withStatus(202)));

		Properties props = new Properties();
		props.setProperty("ogham.email.sendgrid.api-key", "foobar");
		props.setProperty("ogham.email.sendgrid.url", "http://localhost:"+wiremock.port());
		
		MessagingService service = new StandaloneApp().init(props);
		runner = new EmailRunner(service);
		checker = new SendGridV4Checker(wiremock);
	}
	
	
	@Test
	public void emailWithoutTemplate() throws Exception {
		runner.sendEmailWithoutTemplate();
		checker.assertEmailWithoutTemplate();
	}
	
	@Test
	public void emailWithThymeleaf() throws Exception {
		requires("template-thymeleaf");
		
		runner.sendEmailWithThymeleaf();
		checker.assertEmailWithThymeleaf();
	}
	
	@Test
	public void emailWithFreemarker() throws Exception {
		requires("template-freemarker");
		
		runner.sendEmailWithFreemarker();
		checker.assertEmailWithFreemarker();
	}
	
	@Test
	public void emailWithThymeleafAndFreemarker() throws Exception {
		requires("template-thymeleaf", "template-freemarker");
		
		runner.sendEmailWithThymeleafAndFreemarker();
		checker.assertEmailWithThymeleafAndFreemarker();
	}
}
