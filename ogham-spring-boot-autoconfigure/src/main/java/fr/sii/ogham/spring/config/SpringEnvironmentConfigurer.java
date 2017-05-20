package fr.sii.ogham.spring.config;

import org.springframework.core.env.Environment;

import fr.sii.ogham.core.builder.configurer.MessagingConfigurerAdapter;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.spring.env.SpringEnvironmentPropertyResolver;

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
		return 900;
	}

}
