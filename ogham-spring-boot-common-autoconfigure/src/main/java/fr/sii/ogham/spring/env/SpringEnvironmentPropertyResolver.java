package fr.sii.ogham.spring.env;

import org.springframework.core.env.Environment;

import fr.sii.ogham.core.env.PropertyResolver;

public class SpringEnvironmentPropertyResolver implements PropertyResolver {
	private final Environment environment;
	
	public SpringEnvironmentPropertyResolver(Environment environment) {
		super();
		this.environment = environment;
	}

	@Override
	public boolean containsProperty(String key) {
		return environment.containsProperty(key);
	}

	@Override
	public String getProperty(String key) {
		return environment.getProperty(key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return environment.getProperty(key, defaultValue);
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType) {
		return environment.getProperty(key, targetType);
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		return environment.getProperty(key, targetType, defaultValue);
	}

	@Override
	public String getRequiredProperty(String key) throws IllegalStateException {
		return environment.getRequiredProperty(key);
	}

	@Override
	public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
		return environment.getRequiredProperty(key, targetType);
	}

}
