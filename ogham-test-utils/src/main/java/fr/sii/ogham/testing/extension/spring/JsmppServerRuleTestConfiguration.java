package fr.sii.ogham.testing.extension.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;

/**
 * Test configuration that registers {@link JsmppServerRule} bean with port
 * defined by {@code jsmpp.server.port} property.
 * 
 * @author Aurélien Baudet
 *
 */
@TestConfiguration
public class JsmppServerRuleTestConfiguration {
	@Bean
	@ConditionalOnMissingBean(JsmppServerRule.class)
	@ConditionalOnProperty("jsmpp.server.port")
	public JsmppServerRule randomJsmppPortRule(@Value("${jsmpp.server.port}") int port, @Autowired(required=false) ServerConfig config) {
		if (config == null) {
			config = new ServerConfig();
		}
		config.port(port);
		return new JsmppServerRule(config);
	}
}
