package fr.sii.ogham.testing.extension.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Test configuration that registers {@link WireMockRule} bean with port defined
 * by {@code wiremock.server.port} property.
 * 
 * @author Aurélien Baudet
 *
 */
@TestConfiguration
public class WireMockRuleTestConfiguration {
	@Bean
	@ConditionalOnMissingBean(WireMockRule.class)
	@ConditionalOnProperty("wiremock.server.port")
	public WireMockRule randomPortWireMockRule(@Value("${wiremock.server.port}") int port) {
		return new WireMockRule(port);
	}
}
