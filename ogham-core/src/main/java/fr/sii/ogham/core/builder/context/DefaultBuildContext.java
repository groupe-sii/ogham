package fr.sii.ogham.core.builder.context;

import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.env.JavaPropertiesResolver;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.util.BuilderUtils;

/**
 * Simple build context that uses a default {@link PropertyResolver} and a
 * default {@link Converter}.
 * 
 * <p>
 * <strong>WARNING: don't use it, this is for for advanced usage only
 * !!!</strong>
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class DefaultBuildContext implements BuildContext {
	private final PropertyResolver propertyResolver;
	private final Converter converter;

	public DefaultBuildContext() {
		this(new Properties());
	}

	public DefaultBuildContext(Properties props) {
		super();
		this.converter = new DefaultConverter();
		this.propertyResolver = new JavaPropertiesResolver(props, converter);
	}

	@Override
	public <T> T register(T instance) {
		return instance;
	}

	@Override
	public <T> T evaluate(List<String> properties, Class<T> resultClass) {
		return BuilderUtils.evaluate(properties, getPropertyResolver(), resultClass);
	}

	@Override
	public PropertyResolver getPropertyResolver() {
		return propertyResolver;
	}

	@Override
	public Converter getConverter() {
		return converter;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P, V, T extends ConfigurationValueBuilder<P, V>> T newConfigurationValueBuilder(P parent, Class<V> valueClass) {
		return newConfigurationValueBuilder(ctx -> (T) new ConfigurationValueBuilderHelper<>(parent, valueClass, ctx));
	}

	@Override
	public <P, V, T extends ConfigurationValueBuilder<P, V>> T newConfigurationValueBuilder(Function<BuildContext, T> factory) {
		return factory.apply(this);
	}

}
