package fr.sii.ogham.spring.common;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import fr.sii.ogham.core.CoreConstants;
import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.spring.env.SpringEnvironmentPropertyResolver;

/**
 * Configures general environment (and may be inherited) to use Spring
 * {@link Environment} instead of basic Java {@link Properties} object.
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringEnvironmentConfigurer extends MessagingConfigurerAdapter implements SpringMessagingConfigurer {
	private static final Logger LOG = LoggerFactory.getLogger(SpringEnvironmentConfigurer.class);
	
	private final Environment environment;

	public SpringEnvironmentConfigurer(Environment environment) {
		super();
		this.environment = environment;
	}

	@Override
	public void configure(EnvironmentBuilder<?> environmentBuilder) {
		LOG.debug("[{}] apply configuration", this);
		environmentBuilder.resolver(new SpringEnvironmentPropertyResolver(environment));
	}

	@Override
	public int getOrder() {
		return CoreConstants.DEFAULT_MESSAGING_CONFIGURER_PRIORITY - 1000;
	}

}
