package fr.sii.ogham.runtime.springboot;

import fr.sii.ogham.runtime.checker.CloudhopperChecker;
import fr.sii.ogham.runtime.runner.SmsRunner;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import fr.sii.ogham.testing.extension.spring.JsmppServerInitializer;
import ogham.testing.org.jsmpp.bean.SubmitSm;
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
@SpringBootTest(classes = Application.class, properties = {
	"ogham.sms.smpp.host=127.0.0.1",
	"ogham.sms.smpp.port=${jsmpp.server.port}"
})
@ContextConfiguration(initializers = JsmppServerInitializer.class)
public class SmsCloudhopperTest {
	@RegisterExtension @Autowired public SmppServerExtension<SubmitSm> smppServer;
	
	@Autowired SmsRunner runner;
	CloudhopperChecker checker;
	
	@BeforeEach
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
