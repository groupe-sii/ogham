package fr.sii.ogham.core.builder.env;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.exception.builder.BuildException;

/**
 * A {@link PropertiesBuilder} that registers properties (key/value pairs) that
 * is used by an {@link EnvironmentBuilder#properties()}.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class SimplePropertiesBuilder<P> extends AbstractParent<P> implements PropertiesBuilder<P> {
	private List<Property> properties;

	/**
	 * Initializes the builder with the provided parent. The list of properties
	 * is initialized with an empty list.
	 * 
	 * @param parent
	 *            the parent builder
	 */
	public SimplePropertiesBuilder(P parent) {
		super(parent);
		properties = new ArrayList<>();
	}

	@Override
	public PropertiesBuilder<P> set(String key, String value) {
		properties.add(new Property(key, value));
		return this;
	}

	@Override
	public Properties build() throws BuildException {
		Properties props = new Properties();
		for (Property prop : properties) {
			props.put(prop.getKey(), prop.getValue());
		}
		return props;
	}

	private static class Property {
		private final String key;
		private final String value;

		public Property(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}
}
