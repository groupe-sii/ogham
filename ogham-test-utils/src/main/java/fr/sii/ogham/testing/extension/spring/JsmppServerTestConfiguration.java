package fr.sii.ogham.testing.extension.spring;

import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerRule;
import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Test configuration that registers:
 * <ul>
 * <li>{@link JsmppServerRule} bean for JUnit 4</li>
 * <li>{@link JsmppServerExtension} bean for JUnit 5</li>
 * <li>Configure port defined by {@code jsmpp.server.port} property.</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@TestConfiguration
@Import(JsmppServerRuleTestConfiguration.class)
public class JsmppServerTestConfiguration {
	@Bean
	@ConditionalOnMissingBean(JsmppServerExtension.class)
	@ConditionalOnProperty("jsmpp.server.port")
	public JsmppServerExtension randomJsmppPortExtension(@Value("${jsmpp.server.port}") int port, @Autowired(required = false) ServerConfig config) {
		return new JsmppServerExtension(initConfig(port, config));
	}

	private static ServerConfig initConfig(int port, ServerConfig config) {
		if (config == null) {
			config = new ServerConfig();
		}
		config.port(port);
		return config;
	}
}
