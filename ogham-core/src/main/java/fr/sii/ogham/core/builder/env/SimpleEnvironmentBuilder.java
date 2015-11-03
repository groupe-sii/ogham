package fr.sii.ogham.core.builder.env;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.env.JavaPropertiesResolver;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;

public class SimpleEnvironmentBuilder<P> extends AbstractParent<P> implements EnvironmentBuilder<P> {
	private PropertyResolver resolver;
	private ConverterBuilder<SimpleEnvironmentBuilder<P>> converterBuilder;
	private List<PropsOrPath> props;
	
	public SimpleEnvironmentBuilder(P parent) {
		super(parent);
		props = new ArrayList<>();
	}

	@Override
	public EnvironmentBuilder<P> properties(String path) {
		return properties(path, false);
	}

	@Override
	public EnvironmentBuilder<P> properties(String path, boolean override) {
		props.add(new PropsOrPath(path, override));
		return this;
	}

	public SimpleEnvironmentBuilder<P> properties(Properties properties) {
		return properties(properties, false);
	}
	
	public SimpleEnvironmentBuilder<P> properties(Properties properties, boolean override) {
		props.add(new PropsOrPath(properties, override));
		return this;
	}
	
	public SimpleEnvironmentBuilder<P> systemProperties() {
		return systemProperties(false);
	}
	
	public SimpleEnvironmentBuilder<P> systemProperties(boolean override) {
		props.add(new PropsOrPath(BuilderUtils.getDefaultProperties(), override));
		return this;
	}
	
	public ConverterBuilder<? extends EnvironmentBuilder<P>> converter() {
		if(converterBuilder==null) {
			converterBuilder = new SimpleConverterBuilder<>(this);
		}
		return converterBuilder;
	}
	
	public SimpleEnvironmentBuilder<P> resolver(PropertyResolver resolver) {
		this.resolver = resolver;
		return this;
	}

	@Override
	public PropertyResolver build() throws BuildException {
		PropertyResolver resolver = this.resolver;
		if(resolver==null) {
			Converter converter = buildConverter();
			Properties properties = buildProperties();
			resolver = new JavaPropertiesResolver(properties, converter);
		}
		return resolver;
	}

	private Converter buildConverter() {
		if(converterBuilder!=null) {
			return converterBuilder.build();
		}
		return new DefaultConverter();
	}

	private Properties buildProperties() {
		Properties properties = new Properties();
		for(PropsOrPath prop : props) {
			if(prop.isOverride()) {
				properties = buildProps(prop);
			} else {
				properties.putAll(buildProps(prop));
			}
		}
		if(properties==null) {
			properties = BuilderUtils.getDefaultProperties();
		}
		return properties;
	}
	
	private Properties buildProps(PropsOrPath prop) {
		if(prop.getPath()!=null) {
			Properties properties = new Properties();
			try {
				properties.load(getClass().getResourceAsStream(prop.getPath()));
			} catch (IOException e) {
				throw new BuildException("Failed to load properties file "+prop.getPath(), e);
			}
			return properties;
		}
		return prop.getProperties();
	}

	private static class PropsOrPath {
		private final Properties properties;
		private final String path;
		private final boolean override;
		public PropsOrPath(Properties properties, boolean override) {
			this(properties, null, override);
		}
		public PropsOrPath(String path, boolean override) {
			this(null, path, override);
		}
		protected PropsOrPath(Properties properties, String path, boolean override) {
			super();
			this.properties = properties;
			this.path = path;
			this.override = override;
		}
		public Properties getProperties() {
			return properties;
		}
		public String getPath() {
			return path;
		}
		public boolean isOverride() {
			return override;
		}
	}
}
