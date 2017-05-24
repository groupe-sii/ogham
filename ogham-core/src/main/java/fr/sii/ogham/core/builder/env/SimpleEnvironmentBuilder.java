package fr.sii.ogham.core.builder.env;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

/**
 * Builds the {@link PropertyResolver}:
 * <ul>
 * <li>If a custom resolver is defined, use it directly</li>
 * <li>If no custom resolver, use the {@link JavaPropertiesResolver} with merged
 * {@link Properties} and converter instance provided by
 * {@link ConverterBuilder#build()}</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class SimpleEnvironmentBuilder<P> extends AbstractParent<P> implements EnvironmentBuilder<P> {
	private PropertyResolver resolver;
	private ConverterBuilder<EnvironmentBuilder<P>> converterBuilder;
	private List<AbstractProps> props;

	/**
	 * Initializes the builder with the provided parent (parent is used when
	 * calling {@link #and()} method).
	 * 
	 * @param parent
	 *            the parent instance
	 */
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
		props.add(new PropsPath(path, override));
		return this;
	}

	@Override
	public SimpleEnvironmentBuilder<P> properties(Properties properties) {
		return properties(properties, false);
	}

	@Override
	public SimpleEnvironmentBuilder<P> properties(Properties properties, boolean override) {
		props.add(new Props(properties, override));
		return this;
	}

	@Override
	public SimpleEnvironmentBuilder<P> systemProperties() {
		return systemProperties(false);
	}

	@Override
	public SimpleEnvironmentBuilder<P> systemProperties(boolean override) {
		props.add(new Props(BuilderUtils.getDefaultProperties(), override));
		return this;
	}

	@Override
	public ConverterBuilder<EnvironmentBuilder<P>> converter() {
		if (converterBuilder == null) {
			converterBuilder = new SimpleConverterBuilder<EnvironmentBuilder<P>>(this);
		}
		return converterBuilder;
	}

	@Override
	public SimpleEnvironmentBuilder<P> resolver(PropertyResolver resolver) {
		this.resolver = resolver;
		return this;
	}

	@Override
	public PropertiesBuilder<EnvironmentBuilder<P>> properties() {
		PropertiesBuilder<EnvironmentBuilder<P>> propsBuilder = new SimplePropertiesBuilder<EnvironmentBuilder<P>>(this);
		props.add(new PropsBuilder(propsBuilder, false));
		return propsBuilder;
	}

	@Override
	public PropertiesBuilder<EnvironmentBuilder<P>> properties(boolean override) {
		PropertiesBuilder<EnvironmentBuilder<P>> propsBuilder = new SimplePropertiesBuilder<EnvironmentBuilder<P>>(this);
		props.add(new PropsBuilder(propsBuilder, true));
		return propsBuilder;
	}

	/**
	 * Build the {@link PropertyResolver}:
	 * <ul>
	 * <li>If a custom resolver is defined, use it directly</li>
	 * <li>If no custom resolver, use the {@link JavaPropertiesResolver} with
	 * merged {@link Properties} and converter instance provided by
	 * {@link ConverterBuilder#build()}</li>
	 * </ul>
	 */
	@Override
	public PropertyResolver build() throws BuildException {
		if (resolver != null) {
			return resolver;
		}
		Converter converter = buildConverter();
		Properties properties = buildProperties();
		return new JavaPropertiesResolver(properties, converter);
	}

	private Converter buildConverter() {
		if (converterBuilder != null) {
			return converterBuilder.build();
		}
		return new DefaultConverter();
	}

	private Properties buildProperties() {
		Properties properties = new Properties();
		for (AbstractProps prop : props) {
			properties = prop.buildProperties(properties);
		}
		if (properties == null) {
			properties = BuilderUtils.getDefaultProperties();
		}
		return properties;
	}

	private static abstract class AbstractProps {
		protected final boolean override;
		
		protected AbstractProps(boolean override) {
			this.override = override;
		}

		public Properties buildProperties(Properties properties) {
			if(override) {
				return getProps();
			}
			properties.putAll(getProps());
			return properties;
		}
		
		public abstract Properties getProps();
	}
	
	private static class Props extends AbstractProps {
		private final Properties properties;

		public Props(Properties properties, boolean override) {
			super(override);
			this.properties = properties;
		}

		@Override
		public Properties getProps() {
			return properties;
		}
	}
	
	private static class PropsPath extends AbstractProps {
		private final String path;

		public PropsPath(String path, boolean override) {
			super(override);
			this.path = path;
		}
		
		private String getClasspathPath(String path) {
			return path.startsWith("/") ? path.substring(1) : path;
		}

		private InputStream loadFromClasspath(String path) {
			return getClass().getClassLoader().getResourceAsStream(getClasspathPath(path));
		}

		@Override
		public Properties getProps() {
			try {
				Properties properties = new Properties();
				if (path.startsWith("classpath:")) {
					properties.load(loadFromClasspath(path.substring(10)));
				} else if (path.startsWith("file:")) {
					properties.load(new FileInputStream(new File(path.substring(5))));
				} else {
					properties.load(loadFromClasspath(path));
				}
				return properties;
			} catch (IOException e) {
				throw new BuildException("Failed to load properties file " + path, e);
			}
		}
	}
	
	private static class PropsBuilder extends AbstractProps {
		private final PropertiesBuilder<?> propertiesBuilder;

		public PropsBuilder(PropertiesBuilder<?> propertiesBuilder, boolean override) {
			super(override);
			this.propertiesBuilder = propertiesBuilder;
		}

		@Override
		public Properties getProps() {
			return propertiesBuilder.build();
		}
	}
}
