package fr.sii.ogham.runtime.springboot;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import fr.sii.ogham.runtime.checker.AutoSendGridCheckerProvider;
import fr.sii.ogham.runtime.checker.SendGridChecker;
import fr.sii.ogham.runtime.checker.SendGridCheckerProvider;
import fr.sii.ogham.runtime.runner.EmailRunner;
import fr.sii.ogham.testing.extension.spring.WireMockInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static junit.filter.Assumptions.requires;

@ExtendWith(SpringExtension.class) // required for old Spring Boot versions
@SpringBootTest(classes = Application.class, properties = {
	"ogham.email.sendgrid.api-key=foobar",
	"ogham.email.sendgrid.url=http://localhost:${wiremock.server.port}"
})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(initializers = WireMockInitializer.class)
public class SendGridTest {
	@Autowired @RegisterExtension public WireMockExtension wiremock;
	@Autowired EmailRunner runner;
	SendGridChecker checker;
	
	@BeforeEach
	public void setup() {
		requires("email-sendgrid");

		SendGridCheckerProvider provider = new AutoSendGridCheckerProvider();
		checker = provider.get(wiremock.getRuntimeInfo().getWireMock());
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
