package fr.sii.ogham.core.builder.env;

import static fr.sii.ogham.core.CoreConstants.DEFAULT_MANUAL_PROPERTY_PRIORITY;
import static fr.sii.ogham.core.CoreConstants.DEFAULT_PATH_PROPERTY_PRIORITY;
import static fr.sii.ogham.core.CoreConstants.DEFAULT_SYSTEM_PROPERTY_PRIORITY;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.env.FirstExistingPropertiesResolver;
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
	private int currentIndex;

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
		currentIndex = 0;
	}

	@Override
	public EnvironmentBuilder<P> override() {
		props.clear();
		return this;
	}

	@Override
	public EnvironmentBuilder<P> properties(String path) {
		return properties(path, DEFAULT_PATH_PROPERTY_PRIORITY);
	}

	@Override
	public EnvironmentBuilder<P> properties(String path, int priority) {
		props.add(new PropsPath(path, priority, currentIndex++));
		return this;
	}

	@Override
	public SimpleEnvironmentBuilder<P> properties(Properties properties) {
		return properties(properties, DEFAULT_MANUAL_PROPERTY_PRIORITY);
	}

	@Override
	public SimpleEnvironmentBuilder<P> properties(Properties properties, int priority) {
		props.add(new Props(properties, priority, currentIndex++));
		return this;
	}

	@Override
	public SimpleEnvironmentBuilder<P> systemProperties() {
		return systemProperties(DEFAULT_SYSTEM_PROPERTY_PRIORITY);
	}

	@Override
	public SimpleEnvironmentBuilder<P> systemProperties(int priority) {
		props.add(new Props(BuilderUtils.getDefaultProperties(), priority, currentIndex++));
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
		return properties(DEFAULT_MANUAL_PROPERTY_PRIORITY);
	}

	@Override
	public PropertiesBuilder<EnvironmentBuilder<P>> properties(int priority) {
		PropertiesBuilder<EnvironmentBuilder<P>> propsBuilder = new SimplePropertiesBuilder<EnvironmentBuilder<P>>(this);
		props.add(new PropsBuilder(propsBuilder, priority, currentIndex++));
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
		List<PropertyResolver> delegates = buildProperties(converter);
		return new FirstExistingPropertiesResolver(delegates);
	}

	private Converter buildConverter() {
		if (converterBuilder != null) {
			return converterBuilder.build();
		}
		return new DefaultConverter();
	}

	private List<PropertyResolver> buildProperties(Converter converter) {
		// sort by priority and then by registration order
		// highest priority comes first
		List<AbstractProps> orderedProps = new ArrayList<>(props);
		Collections.sort(orderedProps, new PriorityComparator());
		// build the resolvers separately
		List<PropertyResolver> builtProperties = new ArrayList<>();
		for (AbstractProps prop : orderedProps) {
			builtProperties.add(new JavaPropertiesResolver(prop.getProps(), converter));
		}
		return builtProperties;
	}

	private static class PriorityComparator implements Comparator<AbstractProps> {

		@Override
		public int compare(AbstractProps o1, AbstractProps o2) {
			if (o1.priority == o2.priority) {
				return o1.index <= o2.index ? -1 : 1;
			}
			return o1.priority <= o2.priority ? 1 : -1;
		}

	}

	private static abstract class AbstractProps {
		protected final int priority;
		protected final int index;

		protected AbstractProps(int priority, int index) {
			this.priority = priority;
			this.index = index;
		}

		public abstract Properties getProps();
	}

	private static class Props extends AbstractProps {
		private final Properties properties;

		public Props(Properties properties, int priority, int index) {
			super(priority, index);
			this.properties = properties;
		}

		@Override
		public Properties getProps() {
			return properties;
		}
	}

	private static class PropsPath extends AbstractProps {
		private final String path;

		public PropsPath(String path, int priority, int index) {
			super(priority, index);
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

		public PropsBuilder(PropertiesBuilder<?> propertiesBuilder, int priority, int index) {
			super(priority, index);
			this.propertiesBuilder = propertiesBuilder;
		}

		@Override
		public Properties getProps() {
			return propertiesBuilder.build();
		}
	}
}
