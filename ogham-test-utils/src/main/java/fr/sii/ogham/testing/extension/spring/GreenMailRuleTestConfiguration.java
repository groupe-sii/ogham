package fr.sii.ogham.testing.extension.spring;

import ogham.testing.com.icegreen.greenmail.junit4.GreenMailRule;
import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import ogham.testing.com.icegreen.greenmail.util.ServerSetup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


/**
 * Test configuration that registers:
 * <ul>
 * <li>{@link GreenMailRule} bean for JUnit 4</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@TestConfiguration
@ConditionalOnClass(org.junit.rules.TestRule.class)
public class GreenMailRuleTestConfiguration {
	@Bean
	@ConditionalOnMissingBean(GreenMailRule.class)
	@ConditionalOnProperty("greenmail.smtp.port")
	public GreenMailRule randomSmtpPortGreenMailRule(@Value("${greenmail.smtp.port}") int port) {
		return new GreenMailRule(new ServerSetup(port, null, ServerSetup.PROTOCOL_SMTP));
	}
}
