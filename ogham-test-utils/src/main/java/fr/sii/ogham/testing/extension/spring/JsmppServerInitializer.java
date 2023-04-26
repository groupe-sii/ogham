package fr.sii.ogham.testing.extension.spring;

import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.util.RandomPortUtils;
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

/**
 * Initializer for Spring Boot tests that registers:
 * <ul>
 * <li>{@code "jsmpp.server.port"} property in Spring {@link Environment}</li>
 * <li>{@link JsmppServerExtension} bean in Spring {@link ApplicationContext} through
 * {@link JsmppServerTestConfiguration} in order to use the port defined by
 * {@code "jsmpp.server.port"} property</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class JsmppServerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	private static final Logger LOG = LoggerFactory.getLogger(JsmppServerInitializer.class);

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		if (applicationContext instanceof GenericApplicationContext) {
			BeanDefinition configBean = new AnnotatedGenericBeanDefinition(JsmppServerTestConfiguration.class);
			((GenericApplicationContext) applicationContext).registerBeanDefinition("jsmppServerTestConfiguration", configBean);
		}
		int port = RandomPortUtils.findAvailableTcpPort();
		LOG.debug("Registering {} port for JSMPP server", port);
		TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, "jsmpp.server.port=" + port);
	}
}