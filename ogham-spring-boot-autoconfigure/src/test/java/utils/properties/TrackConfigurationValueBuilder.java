package utils.properties;

import java.util.List;
import java.util.function.Function;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.sms.builder.cloudhopper.StandardEncodingHelper;

public class TrackConfigurationValueBuilder implements BuildContext {
	private final BuildContext delegate;
	private final List<PropertiesAndValue> configured;

	public TrackConfigurationValueBuilder(BuildContext delegate, List<PropertiesAndValue> configured) {
		super();
		this.delegate = delegate;
		this.configured = configured;
	}

	@Override
	public <T> T register(T instance) {
		return delegate.register(instance);
	}

	@Override
	public <T> T evaluate(List<String> properties, Class<T> resultClass) {
		return delegate.evaluate(properties, resultClass);
	}

	@Override
	public PropertyResolver getPropertyResolver() {
		return delegate.getPropertyResolver();
	}

	@Override
	public Converter getConverter() {
		return delegate.getConverter();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, V, T extends ConfigurationValueBuilder<P, V>> T newConfigurationValueBuilder(P parent, Class<V> valueClass) {
		T original = delegate.newConfigurationValueBuilder(parent, valueClass);
		T builder = (T) new TrackValueBuilder<P, V>(original, configured);
		return builder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, V, T extends ConfigurationValueBuilder<P, V>> T newConfigurationValueBuilder(Function<BuildContext, T> factory) {
		T original = delegate.newConfigurationValueBuilder(factory);
		T builder = (T) new TrackEncodingBuilder((StandardEncodingHelper) original, configured);
		return builder;
	}
}