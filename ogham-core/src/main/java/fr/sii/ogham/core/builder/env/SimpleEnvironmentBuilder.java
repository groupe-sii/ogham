package fr.sii.ogham.core.builder.env;

import static fr.sii.ogham.core.CoreConstants.DEFAULT_CLASSPATH_PROPERTY_PRIORITY;
import static fr.sii.ogham.core.CoreConstants.DEFAULT_FILE_PROPERTY_PRIORITY;
import static fr.sii.ogham.core.CoreConstants.DEFAULT_MANUAL_PROPERTY_PRIORITY;
import static fr.sii.ogham.core.CoreConstants.DEFAULT_SYSTEM_PROPERTY_PRIORITY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import fr.sii.ogham.core.builder.env.props.AbstractProps;
import fr.sii.ogham.core.builder.env.props.Props;
import fr.sii.ogham.core.builder.env.props.PropsBuilder;
import fr.sii.ogham.core.builder.env.props.PropsPath;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.env.FirstExistingPropertiesResolver;
import fr.sii.ogham.core.env.JavaPropertiesResolver;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.fluent.AbstractParent;
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
		int priority = DEFAULT_CLASSPATH_PROPERTY_PRIORITY;
		if(path.startsWith("file:")) {
			priority = DEFAULT_FILE_PROPERTY_PRIORITY;
		}
		return properties(path, priority);
	}

	@Override
	public EnvironmentBuilder<P> properties(String path, int priority) {
		props.add(new PropsPath(path, priority, currentIndex));
		currentIndex++;
		return this;
	}

	@Override
	public SimpleEnvironmentBuilder<P> properties(Properties properties) {
		return properties(properties, DEFAULT_MANUAL_PROPERTY_PRIORITY);
	}

	@Override
	public SimpleEnvironmentBuilder<P> properties(Properties properties, int priority) {
		props.add(new Props(properties, priority, currentIndex));
		currentIndex++;
		return this;
	}

	@Override
	public SimpleEnvironmentBuilder<P> systemProperties() {
		return systemProperties(DEFAULT_SYSTEM_PROPERTY_PRIORITY);
	}

	@Override
	public SimpleEnvironmentBuilder<P> systemProperties(int priority) {
		props.add(new Props(BuilderUtils.getDefaultProperties(), priority, currentIndex));
		currentIndex++;
		return this;
	}

	@Override
	public ConverterBuilder<EnvironmentBuilder<P>> converter() {
		if (converterBuilder == null) {
			converterBuilder = new SimpleConverterBuilder<>(this);
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
		PropertiesBuilder<EnvironmentBuilder<P>> propsBuilder = new SimplePropertiesBuilder<>(this);
		props.add(new PropsBuilder(propsBuilder, priority, currentIndex));
		currentIndex++;
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
	public PropertyResolver build() {
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
			if (o1.getPriority() == o2.getPriority()) {
				return o1.getIndex() <= o2.getIndex() ? -1 : 1;
			}
			return o1.getPriority() <= o2.getPriority() ? 1 : -1;
		}

	}
}
