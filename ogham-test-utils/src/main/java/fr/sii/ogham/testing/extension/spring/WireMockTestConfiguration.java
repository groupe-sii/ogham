package fr.sii.ogham.testing.extension.spring;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Test configuration that registers {@link WireMockExtension} bean with port defined
 * by {@code wiremock.server.port} property.
 * 
 * @author Aurélien Baudet
 *
 */
@TestConfiguration
public class WireMockTestConfiguration {
	@Bean
	@ConditionalOnMissingBean(WireMockExtension.class)
	@ConditionalOnProperty("wiremock.server.port")
	public WireMockExtension randomPortWireMockExtension(@Value("${wiremock.server.port}") int port) {
		return new WireMockExtension.Builder()
				.options(wireMockConfig().port(port))
				.build();
	}
}
