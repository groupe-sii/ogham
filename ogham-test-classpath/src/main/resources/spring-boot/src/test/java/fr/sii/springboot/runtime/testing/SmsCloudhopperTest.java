package fr.sii.springboot.runtime.testing;

import static junit.filter.Assumptions.requires;

import java.util.Properties;

import org.jsmpp.bean.SubmitSm;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.sii.ogham.core.service.MessagingService;

import fr.sii.ogham.runtime.runner.SmsRunner;
import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.SmppServerRule;
import fr.sii.ogham.testing.extension.spring.JsmppServerRandomPortInitializer;
import fr.sii.ogham.runtime.checker.CloudhopperChecker;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest({
	"ogham.sms.smpp.host=127.0.0.1",
	"ogham.sms.smpp.port=${jsmpp.server.port}"
})
@ContextConfiguration(classes = Application.class, initializers = JsmppServerRandomPortInitializer.class)
public class SmsCloudhopperTest {
	@Rule @Autowired public SmppServerRule<SubmitSm> smppServer;
	
	@Autowired SmsRunner runner;
	CloudhopperChecker checker;
	
	@Before
	public void setup() {
		requires("sms-cloudhopper");

		checker = new CloudhopperChecker(smppServer);
	}

	@Test
	public void smsWithoutTemplate() throws Exception {
		runner.sendSmsWithoutTemplate();
		checker.assertSmsWithoutTemplate();
	}
	
	@Test
	public void smsWithThymeleaf() throws Exception {
		requires("template-thymeleaf");
		
		runner.sendSmsWithThymeleaf();
		checker.assertSmsWithThymeleaf();
	}
	
	@Test
	public void smsWithFreemarker() throws Exception {
		requires("template-freemarker");
		
		runner.sendSmsWithFreemarker();
		checker.assertSmsWithFreemarker();
	}
	
}
