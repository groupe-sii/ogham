package fr.sii.ogham.runtime.common;

import fr.sii.ogham.runtime.checker.CloudhopperChecker;
import fr.sii.ogham.runtime.runner.SmsRunner;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.SmppServerExtension;
import ogham.testing.org.jsmpp.bean.SubmitSm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Properties;

import static junit.filter.Assumptions.requires;

public class SmsCloudhopperIT {
	@RegisterExtension
	public final SmppServerExtension<SubmitSm> smppServer = new JsmppServerExtension();
	
	CloudhopperChecker checker;
	Properties props;
	
	@BeforeEach
	public void setup() {
		requires("sms-cloudhopper");

		props = new Properties();
		props.setProperty("ogham.sms.smpp.host", "127.0.0.1");
		props.setProperty("ogham.sms.smpp.port", String.valueOf(smppServer.getPort()));

		checker = new CloudhopperChecker(smppServer);
	}

	@Test
	public void smsWithoutTemplate() throws Exception {
		CommandLineTestRunner.run(SmsRunner.class, "sendSmsWithoutTemplate", props);
		checker.assertSmsWithoutTemplate();
	}
	
	@Test
	public void smsWithThymeleaf() throws Exception {
		requires("template-thymeleaf");
		
		CommandLineTestRunner.run(SmsRunner.class, "sendSmsWithThymeleaf", props);
		checker.assertSmsWithThymeleaf();
	}
	
	@Test
	public void smsWithFreemarker() throws Exception {
		requires("template-freemarker");
		
		CommandLineTestRunner.run(SmsRunner.class, "sendSmsWithFreemarker", props);
		checker.assertSmsWithFreemarker();
	}
}
