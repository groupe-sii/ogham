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

import fr.sii.ogham.testing.extension.junit.JsmppServerRule;
import fr.sii.ogham.testing.util.RandomPortUtils;

/**
 * Initializer for Spring Boot tests that registers:
 * <ul>
 * <li>{@code "jsmpp.server.port"} property in Spring {@link Environment}</li>
 * <li>{@link JsmppServerRule} bean in Spring {@link ApplicationContext} through
 * {@link JsmppServerRuleTestConfiguration} in order to use the port defined by
 * {@code "jsmpp.server.port"} property</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class JsmppServerRandomPortInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	private static final Logger LOG = LoggerFactory.getLogger(JsmppServerRandomPortInitializer.class);

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		if (applicationContext instanceof GenericApplicationContext) {
			BeanDefinition configBean = new AnnotatedGenericBeanDefinition(JsmppServerRuleTestConfiguration.class);
			((GenericApplicationContext) applicationContext).registerBeanDefinition("jsmppServerRuleTestConfiguration", configBean);
		}
		int port = RandomPortUtils.findAvailableTcpPort();
		LOG.debug("Registering {} port for JSMPP server", port);
		TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, "jsmpp.server.port=" + port);
	}
}