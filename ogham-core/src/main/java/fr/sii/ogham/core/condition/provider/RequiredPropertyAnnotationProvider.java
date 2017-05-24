package fr.sii.ogham.core.condition.provider;

import static fr.sii.ogham.core.condition.fluent.Conditions.not;
import static fr.sii.ogham.core.condition.fluent.Conditions.requiredProperty;

import java.util.regex.Pattern;

import fr.sii.ogham.core.builder.annotation.RequiredProperty;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.condition.fluent.Conditions;
import fr.sii.ogham.core.condition.fluent.FluentCondition;
import fr.sii.ogham.core.env.PropertyResolver;

/**
 * Provider that handle {@link RequiredProperty} annotation to provide a
 * condition.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the kind of the object under conditions
 */
public class RequiredPropertyAnnotationProvider<T> implements ConditionProvider<RequiredProperty, T> {
	private final PropertyResolver propertyResolver;

	public RequiredPropertyAnnotationProvider(PropertyResolver propertyResolver) {
		super();
		this.propertyResolver = propertyResolver;
	}

	@Override
	public Condition<T> provide(RequiredProperty annotation) {
		if (annotation == null) {
			return new FixedCondition<>(true);
		} else {
			FluentCondition<T> mainCondition = propertyOrAlternatives(annotation);
			if (!annotation.is().isEmpty()) {
				mainCondition = mainCondition.and(Conditions.<T> requiredPropertyValue(propertyResolver, annotation.value(), annotation.is()));
			}
			if (!annotation.pattern().isEmpty()) {
				mainCondition = mainCondition.and(Conditions.<T> requiredPropertyValue(propertyResolver, annotation.value(), Pattern.compile(annotation.pattern(), annotation.flags())));
			}
			for (String excludeValue : annotation.excludes()) {
				mainCondition = mainCondition.and(not(Conditions.<T> requiredPropertyValue(propertyResolver, annotation.value(), excludeValue)));
			}
			return mainCondition;
		}
	}

	private FluentCondition<T> propertyOrAlternatives(RequiredProperty annotation) {
		FluentCondition<T> condition = requiredProperty(propertyResolver, annotation.value());
		for (String alternative : annotation.alternatives()) {
			condition = condition.or(Conditions.<T> requiredProperty(propertyResolver, alternative));
		}
		return condition;
	}

}
