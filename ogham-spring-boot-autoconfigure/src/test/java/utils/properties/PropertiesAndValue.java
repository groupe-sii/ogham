package utils.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilder;

public class PropertiesAndValue {
	final ConfigurationValueBuilder<?, ?> valueBuilder;
	final List<String> properties;
	final List<Optional<?>> values;

	public PropertiesAndValue(ConfigurationValueBuilder<?, ?> builder) {
		super();
		this.valueBuilder = builder;
		this.properties = new ArrayList<>();
		this.values = new ArrayList<>();
	}

	public void addProperties(String... props) {
		Collections.addAll(properties, props);
	}

	public void addValues(Optional<?>... values) {
		Collections.addAll(this.values, values);
	}

	public boolean isConsistent() {
		if (properties.isEmpty() && values.isEmpty()) {
			return true;
		}
		if (!properties.isEmpty() && !values.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public boolean containsPropertyKey(String key) {
		return properties.contains("${"+key+"}");
	}
	
	public boolean isOnlyDefinedInSpring() {
		return properties.isEmpty() && !values.isEmpty();
	}

	public boolean isForBuilder(String builderClass) {
		return valueBuilder.and().getClass().getSimpleName().equals(builderClass);
	}
	
	public ConfigurationValueBuilder<?, ?> getValueBuilder() {
		return valueBuilder;
	}

	public List<String> getProperties() {
		return properties;
	}

	public List<Optional<?>> getValues() {
		return values;
	}

}