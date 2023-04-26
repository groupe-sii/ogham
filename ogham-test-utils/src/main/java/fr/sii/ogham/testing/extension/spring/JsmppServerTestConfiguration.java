package fr.sii.ogham.testing.extension.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;

/**
 * Test configuration that registers:
 * <ul>
 * <li>{@link JsmppServerExtension} bean for JUnit 5</li>
 * <li>Configure port defined by {@code jsmpp.server.port} property.</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
@TestConfiguration
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
