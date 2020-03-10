package fr.sii.ogham.core.builder.priority;

import static java.util.Arrays.asList;

import fr.sii.ogham.core.builder.BuildContext;

/**
 * Provide the priority of an implementation based on {@link Priority}
 * annotation that is present on the class (if any).
 * 
 * <p>
 * If no annotation or no priority is configured, then a default priority is
 * provided (delegating to another {@link PriorityProvider} implementation).
 * 
 * @author Aurélien Baudet
 *
 */
public class AnnotationPriorityProvider implements PriorityProvider<Class<?>> {
	private final BuildContext buildContext;
	private final PriorityProvider<Class<?>> defaultProvider;

	/**
	 * Initializes with the build context and a default provider if the priority
	 * is not provided through the annotation.
	 * 
	 * @param buildContext
	 *            for property evaluation
	 * @param defaultProvider
	 *            in case that there is no priority provided using the
	 *            annotation
	 */
	public AnnotationPriorityProvider(BuildContext buildContext, PriorityProvider<Class<?>> defaultProvider) {
		super();
		this.buildContext = buildContext;
		this.defaultProvider = defaultProvider;
	}

	@Override
	public int provide(Class<?> source) {
		Priority priority = source.getAnnotation(Priority.class);
		if (priority == null) {
			return defaultProvider.provide(source);
		}
		int value = computePriority(priority.properties(), priority.defaultValue());
		if (value > 0) {
			return value;
		}
		return defaultProvider.provide(source);
	}

	private int computePriority(String[] properties, int defaultValue) {
		Integer value = buildContext.evaluate(asList(properties), Integer.class);
		if (value != null && value > 0) {
			return value;
		}
		return defaultValue;
	}

}
