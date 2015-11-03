package fr.sii.ogham.core.builder.env;

import java.util.Properties;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.Parent;
import fr.sii.ogham.core.env.PropertyResolver;

public interface EnvironmentBuilder<P> extends Parent<P>, Builder<PropertyResolver> {

	EnvironmentBuilder<P> properties(String path);

	EnvironmentBuilder<P> properties(String path, boolean merge);

	EnvironmentBuilder<P> properties(Properties properties);
	
	EnvironmentBuilder<P> properties(Properties properties, boolean merge);
	
	EnvironmentBuilder<P> systemProperties();
	
	ConverterBuilder<? extends EnvironmentBuilder<P>> converter();
	
	EnvironmentBuilder<P> resolver(PropertyResolver resolver);
}
