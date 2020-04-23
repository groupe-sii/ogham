package utils.properties;

import java.util.List;
import java.util.Optional;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;
import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper;
import fr.sii.ogham.core.builder.configuration.MayOverride;

public class TrackValueBuilder<P, V> extends ConfigurationValueBuilderHelper<P, V> {
	private final PropertiesAndValue track;
	private final ConfigurationValueBuilder<P, V> delegate;
	
	public TrackValueBuilder(ConfigurationValueBuilder<P, V> delegate, List<PropertiesAndValue> all) {
		super(null, null, null);
		this.delegate = delegate;
		this.track = new PropertiesAndValue(delegate);
		all.add(track);
	}

	@Override
	public ConfigurationValueBuilderHelper<P, V> defaultValue(V value) {
		delegate.defaultValue(value);
		return this;
	}

	@Override
	public ConfigurationValueBuilderHelper<P, V> defaultValue(MayOverride<V> possibleNewValue) {
		delegate.defaultValue(possibleNewValue);
		return this;
	}

	@Override
	public void setValue(V value) {
		((ConfigurationValueBuilderHelper<P, V>) delegate).setValue(value);
	}

	@Override
	public V getValue() {
		return ((ConfigurationValueBuilderHelper<P, V>) delegate).getValue();
	}

	@Override
	public V getValue(V defaultValue) {
		return ((ConfigurationValueBuilderHelper<P, V>) delegate).getValue(defaultValue);
	}

	@Override
	public boolean hasValueOrProperties() {
		return ((ConfigurationValueBuilderHelper<P, V>) delegate).hasValueOrProperties();
	}

	@Override
	public P and() {
		return delegate.and();
	}

	@Override
	public TrackValueBuilder<P, V> value(Optional<V> optionalValue) {
		track.addValues(optionalValue);
		super.value(optionalValue);
		return this;
	}

	@Override
	public TrackValueBuilder<P, V> properties(String... properties) {
		track.addProperties(properties);
		super.properties(properties);
		return this;
	}

}