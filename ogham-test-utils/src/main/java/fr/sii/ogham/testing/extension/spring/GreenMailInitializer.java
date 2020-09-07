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

import com.icegreen.greenmail.junit4.GreenMailRule;

import fr.sii.ogham.testing.util.RandomPortUtils;

/**
 * Initializer for Spring Boot tests that registers:
 * <ul>
 * <li>{@code "greenmail.smtp.port"} property in Spring {@link Environment}</li>
 * <li>{@link GreenMailRule} bean in Spring {@link ApplicationContext} through
 * {@link GreenMailTestConfiguration} in order to use the port defined by
 * {@code "greenmail.smtp.port"} property</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class GreenMailInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	private static final Logger LOG = LoggerFactory.getLogger(GreenMailInitializer.class);

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		if (applicationContext instanceof GenericApplicationContext) {
			BeanDefinition configBean = new AnnotatedGenericBeanDefinition(GreenMailTestConfiguration.class);
			((GenericApplicationContext) applicationContext).registerBeanDefinition("greenMailTestConfiguration", configBean);
		}
		int port = RandomPortUtils.findAvailableTcpPort();
		LOG.debug("Registering {} port for GreenMail SMTP", port);
		TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, "greenmail.smtp.port=" + port);
	}
}