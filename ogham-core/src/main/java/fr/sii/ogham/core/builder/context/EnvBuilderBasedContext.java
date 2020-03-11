package fr.sii.ogham.core.builder.context;

import java.util.List;

import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.registry.Registry;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.util.BuilderUtils;

/**
 * Context that uses a shared {@link EnvironmentBuilder} to evaluate the
 * properties. The converter is the same as used by the
 * {@link EnvironmentBuilder}.
 * 
 * <p>
 * This implementation delegates registration of instances to the provided
 * registry. Any instance may be registered.
 * 
 * @author Aurélien Baudet
 *
 */
public class EnvBuilderBasedContext implements BuildContext {
	private final EnvironmentBuilder<?> environmentBuilder;
	private final Registry<Object> registry;

	public EnvBuilderBasedContext(EnvironmentBuilder<?> environmentBuilder, Registry<Object> registry) {
		super();
		this.environmentBuilder = environmentBuilder;
		this.registry = registry;
	}

	@Override
	public <T> T register(T instance) {
		registry.register(instance);
		return instance;
	}

	@Override
	public <T> T evaluate(List<String> properties, Class<T> resultClass) {
		return BuilderUtils.evaluate(properties, getPropertyResolver(), resultClass);
	}

	@Override
	public PropertyResolver getPropertyResolver() {
		return environmentBuilder.build();
	}

	@Override
	public Converter getConverter() {
		return environmentBuilder.converter().build();
	}

}
