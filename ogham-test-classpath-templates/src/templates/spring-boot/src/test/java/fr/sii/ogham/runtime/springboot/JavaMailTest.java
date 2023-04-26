package fr.sii.ogham.runtime.springboot;

import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import fr.sii.ogham.runtime.checker.JavaMailChecker;
import fr.sii.ogham.runtime.runner.EmailRunner;
import fr.sii.ogham.testing.extension.spring.GreenMailInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static junit.filter.Assumptions.requires;

@ExtendWith(SpringExtension.class) // required for old Spring Boot versions
@SpringBootTest({
	"mail.smtp.host=127.0.0.1",
	"mail.smtp.port=${greenmail.smtp.port}"
})
@ContextConfiguration(initializers = GreenMailInitializer.class)
public class JavaMailTest {
	@RegisterExtension
	@Autowired public GreenMailExtension greenMail;
	
	@Autowired EmailRunner runner;
	JavaMailChecker checker;
	
	@BeforeEach
	public void setup() {
		requires("email-javamail");

		checker = new JavaMailChecker(greenMail);
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