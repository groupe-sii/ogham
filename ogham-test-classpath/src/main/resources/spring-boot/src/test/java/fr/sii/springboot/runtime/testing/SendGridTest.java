package fr.sii.springboot.runtime.testing;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static junit.filter.Assumptions.requires;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;


import fr.sii.ogham.runtime.runner.EmailRunner;
import fr.sii.ogham.runtime.checker.SendGridChecker;
import fr.sii.ogham.runtime.checker.SendGridCheckerProvider;
import fr.sii.ogham.runtime.checker.AutoSendGridCheckerProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest({
	"ogham.email.sendgrid.api-key=foobar",
	"ogham.email.sendgrid.url=http://localhost:48089"
})
public class SendGridTest {
	@ClassRule public static WireMockClassRule wiremock = new WireMockClassRule(48089);
	@Autowired EmailRunner runner;
	SendGridChecker checker;
	
	@Before
	public void setup() {
		requires("email-sendgrid");

		SendGridCheckerProvider provider = new AutoSendGridCheckerProvider();
		checker = provider.get(wiremock);
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
