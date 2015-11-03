package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.builder.annotation.RequiredClasses;
import fr.sii.ogham.core.builder.annotation.RequiredProperties;
import fr.sii.ogham.core.builder.annotation.RequiredProperty;
import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.env.PropertyResolver;

public class AnnotationConditionProvider<T> implements ConditionProvider<Class<?>, T> {
	private final RequiredPropertyAnnotationProvider<T> propertyConditionProvider;
	private final RequiredPropertiesAnnotationProvider<T> propertiesConditionProvider;
	private final RequiredClassAnnotationProvider<T> classConditionProvider;
	private final RequiredClassesAnnotationProvider<T> classesConditionProvider;
	
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
