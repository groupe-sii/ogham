package fr.sii.ogham.testing.extension.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.support.TestPropertySourceUtils;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import fr.sii.ogham.testing.util.RandomPortUtils;

/**
 * Initializer for Spring Boot tests that registers:
 * <ul>
 * <li>{@code "wiremock.server.port"} property in Spring {@link Environment}</li>
 * <li>{@link WireMockRule} bean in Spring {@link ApplicationContext} through
 * {@link WireMockTestConfiguration} in order to use the port defined by
 * {@code "wiremock.server.port"} property</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	private static final Logger LOG = LoggerFactory.getLogger(WireMockInitializer.class);

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		if (applicationContext instanceof GenericApplicationContext) {
			BeanDefinition configBean = new AnnotatedGenericBeanDefinition(WireMockTestConfiguration.class);
			((GenericApplicationContext) applicationContext).registerBeanDefinition("wiremockServerTestConfiguration", configBean);
		}
		int port = RandomPortUtils.findAvailableTcpPort();
		LOG.debug("Registering {} port for WireMock server", port);
		TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, "wiremock.server.port=" + port);
	}
}