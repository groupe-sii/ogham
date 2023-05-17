package fr.sii.ogham.testing.extension.spring;

import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test configuration that registers:
 * <ul>
 * <li>{@link JsmppServerRule} bean for JUnit 4</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@TestConfiguration
@ConditionalOnClass(org.junit.rules.TestRule.class)
public class JsmppServerRuleTestConfiguration {
	@Bean
	@ConditionalOnMissingBean(JsmppServerRule.class)
	@ConditionalOnProperty("jsmpp.server.port")
	public JsmppServerRule randomJsmppPortRule(@Value("${jsmpp.server.port}") int port, @Autowired(required = false) ServerConfig config) {
		return new JsmppServerRule(initConfig(port, config));
	}

	private static ServerConfig initConfig(int port, ServerConfig config) {
		if (config == null) {
			config = new ServerConfig();
		}
		config.port(port);
		return config;
	}
}
