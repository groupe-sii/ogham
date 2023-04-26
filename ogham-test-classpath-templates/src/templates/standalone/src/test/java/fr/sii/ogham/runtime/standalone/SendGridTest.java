package fr.sii.ogham.runtime.standalone;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.runtime.checker.SendGridV4Checker;
import fr.sii.ogham.runtime.runner.EmailRunner;
import junit.filter.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Properties;

public class SendGridTest {
	public final @RegisterExtension WireMockExtension wiremock = new WireMockExtension();
	EmailRunner runner;
	SendGridV4Checker checker;
	
	@BeforeEach
	public void setup() {
		Assumptions.requires("email-sendgrid");

		wiremock.stubFor(WireMock.post("/v3/mail/send")
				.willReturn(WireMock.aResponse().withStatus(202)));

		Properties props = new Properties();
		props.setProperty("ogham.email.sendgrid.api-key", "foobar");
		props.setProperty("ogham.email.sendgrid.url", "http://localhost:"+wiremock.getPort());
		
		MessagingService service = new StandaloneApp().init(props);
		runner = new EmailRunner(service);
		checker = new SendGridV4Checker(wiremock.getRuntimeInfo().getWireMock());
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
