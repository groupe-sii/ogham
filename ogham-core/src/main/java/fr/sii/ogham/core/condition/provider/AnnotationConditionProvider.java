package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.builder.annotation.RequiredClasses;
import fr.sii.ogham.core.builder.annotation.RequiredProperties;
import fr.sii.ogham.core.builder.annotation.RequiredProperty;
import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.env.PropertyResolver;

/**
 * Implementation that handle conditions defined through annotations.
 * 
 * See {@link RequiredProperties}, {@link RequiredProperty},
 * {@link RequiredClasses} and {@link RequiredClass} for more information about
 * the annotations.
 * 
 * <p>
 * If no condition annotation is present on the source object, then a condition
 * that always evaluates to true is returned.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the object to analyze that may be annotated
 */
public class AnnotationConditionProvider<T> implements ConditionProvider<Class<?>, T> {
	private final RequiredPropertyAnnotationProvider<T> propertyConditionProvider;
	private final RequiredPropertiesAnnotationProvider<T> propertiesConditionProvider;
	private final RequiredClassAnnotationProvider<T> classConditionProvider;
	private final RequiredClassesAnnotationProvider<T> classesConditionProvider;

	/**
	 * Initializes with a {@link PropertyResolver} instance. The property
	 * resolver is used by {@link RequiredPropertiesAnnotationProvider} and
	 * {@link RequiredPropertyAnnotationProvider} in order to check if property
	 * exists in the provided property resolver.
	 * 
	 * @param propertyResolver
	 *            the property resolver
	 */
	public AnnotationConditionProvider(PropertyResolver propertyResolver) {
		super();
		propertyConditionProvider = new RequiredPropertyAnnotationProvider<>(propertyResolver);
		propertiesConditionProvider = new RequiredPropertiesAnnotationProvider<>(propertyResolver);
		classConditionProvider = new RequiredClassAnnotationProvider<>();
		classesConditionProvider = new RequiredClassesAnnotationProvider<>();
	}

	@Override
	public Condition<T> provide(Class<?> source) {
		AndCondition<T> mainCondition = new AndCondition<>();
		mainCondition.and(propertyConditionProvider.provide(source.getAnnotation(RequiredProperty.class)));
		mainCondition.and(propertiesConditionProvider.provide(source.getAnnotation(RequiredProperties.class)));
		mainCondition.and(classConditionProvider.provide(source.getAnnotation(RequiredClass.class)));
		mainCondition.and(classesConditionProvider.provide(source.getAnnotation(RequiredClasses.class)));
		return mainCondition;
	}

}
