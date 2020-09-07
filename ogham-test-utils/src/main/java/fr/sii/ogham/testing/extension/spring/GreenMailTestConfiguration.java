package fr.sii.ogham.testing.extension.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.icegreen.greenmail.junit4.GreenMailRule;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;


/**
 * Test configuration that registers:
 * <ul>
 * <li>{@link GreenMailRule} bean for JUnit 4</li>
 * <li>{@link GreenMailExtension} bean for JUnit 5</li>
 * <li>Configure port defined by {@code greenmail.smtp.port} property.</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@TestConfiguration
public class GreenMailTestConfiguration {
	@Bean
	@ConditionalOnMissingBean(GreenMailRule.class)
	@ConditionalOnProperty("greenmail.smtp.port")
	public GreenMailRule randomSmtpPortGreenMailRule(@Value("${greenmail.smtp.port}") int port) {
		return new GreenMailRule(new ServerSetup(port, null, ServerSetup.PROTOCOL_SMTP));
	}
	
	@Bean
	@ConditionalOnMissingBean(GreenMailExtension.class)
	@ConditionalOnProperty("greenmail.smtp.port")
	public GreenMailExtension randomSmtpPortGreenMailExtension(@Value("${greenmail.smtp.port}") int port) {
		return new GreenMailExtension(new ServerSetup(port, null, ServerSetup.PROTOCOL_SMTP));
	}
}
