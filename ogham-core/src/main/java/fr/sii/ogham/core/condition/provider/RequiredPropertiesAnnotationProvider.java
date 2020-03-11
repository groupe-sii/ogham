package fr.sii.ogham.core.condition.provider;

import static fr.sii.ogham.core.condition.fluent.Conditions.alwaysTrue;

import fr.sii.ogham.core.builder.condition.RequiredProperties;
import fr.sii.ogham.core.builder.condition.RequiredProperty;
import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.fluent.Conditions;
import fr.sii.ogham.core.env.PropertyResolver;

/**
 * Provider that handle {@link RequiredProperties} annotation to provide a
 * condition.
 * 
 * It delegates handling of {@link RequiredProperty} to
 * {@link RequiredPropertyAnnotationProvider}.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the kind of the object under conditions
 */
public class RequiredPropertiesAnnotationProvider<T> implements ConditionProvider<RequiredProperties, T> {
	private final PropertyResolver propertyResolver;
	private final RequiredPropertyAnnotationProvider<T> delegate;

	public RequiredPropertiesAnnotationProvider(PropertyResolver propertyResolver) {
		super();
		this.propertyResolver = propertyResolver;
		this.delegate = new RequiredPropertyAnnotationProvider<>(propertyResolver);
	}

	@Override
	public Condition<T> provide(RequiredProperties annotation) {
		if (annotation == null) {
			return alwaysTrue();
		} else {
			AndCondition<T> mainCondition = new AndCondition<>();
			for (String requiredProperties : annotation.value()) {
				mainCondition = mainCondition.and(Conditions.<T> requiredProperty(propertyResolver, requiredProperties));
			}
			for (RequiredProperty subAnnotation : annotation.props()) {
				mainCondition = mainCondition.and(delegate.provide(subAnnotation));
			}
			return mainCondition;
		}
	}

}
