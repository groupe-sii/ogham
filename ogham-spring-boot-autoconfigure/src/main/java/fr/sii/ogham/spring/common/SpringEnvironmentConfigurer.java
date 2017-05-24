package fr.sii.ogham.spring.common;

import java.util.Properties;

import org.springframework.core.env.Environment;

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
	private final Environment environment;

	public SpringEnvironmentConfigurer(Environment environment) {
		super();
		this.environment = environment;
	}

	@Override
	public void configure(EnvironmentBuilder<?> environmentBuilder) {
		environmentBuilder.resolver(new SpringEnvironmentPropertyResolver(environment));
	}

	@Override
	public int getOrder() {
		return 99000;
	}

}
