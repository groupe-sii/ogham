package fr.sii.ogham.testing.extension.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetup;

/**
 * Test configuration that registers {@link GreenMailRule} bean with port
 * defined by {@code greenmail.smtp.port} property.
 * 
 * @author Aurélien Baudet
 *
 */
@TestConfiguration
public class GreenMailRuleTestConfiguration {
	@Bean
	@ConditionalOnMissingBean(GreenMailRule.class)
	@ConditionalOnProperty("greenmail.smtp.port")
	public GreenMailRule randomSmtpPortGreenMailRule(@Value("${greenmail.smtp.port}") int port) {
		return new GreenMailRule(new ServerSetup(port, null, ServerSetup.PROTOCOL_SMTP));
	}
}
