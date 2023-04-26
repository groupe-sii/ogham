package fr.sii.ogham.runtime.standalone;

import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.runtime.checker.CloudhopperChecker;
import fr.sii.ogham.runtime.runner.SmsRunner;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import junit.filter.Assumptions;
import ogham.testing.org.jsmpp.bean.SubmitSm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Properties;

public class SmsCloudhopperTest {
	@RegisterExtension
	public final SmppServerExtension<SubmitSm> smppServer = new JsmppServerExtension();
	
	SmsRunner runner;
	CloudhopperChecker checker;
	
	@BeforeEach
	public void setup() {
		Assumptions.requires("sms-cloudhopper");

		Properties props = new Properties();
		props.setProperty("ogham.sms.smpp.host", "127.0.0.1");
		props.setProperty("ogham.sms.smpp.port", String.valueOf(smppServer.getPort()));
		
		MessagingService service = new StandaloneApp().init(props);
		runner = new SmsRunner(service);
		checker = new CloudhopperChecker(smppServer);
	}

	@Test
	public void smsWithoutTemplate() throws Exception {
		runner.sendSmsWithoutTemplate();
		checker.assertSmsWithoutTemplate();
	}
	
	@Test
	public void smsWithThymeleaf() throws Exception {
		Assumptions.requires("template-thymeleaf");
		
		runner.sendSmsWithThymeleaf();
		checker.assertSmsWithThymeleaf();
	}
	
	@Test
	public void smsWithFreemarker() throws Exception {
		Assumptions.requires("template-freemarker");
		
		runner.sendSmsWithFreemarker();
		checker.assertSmsWithFreemarker();
	}
	
}
