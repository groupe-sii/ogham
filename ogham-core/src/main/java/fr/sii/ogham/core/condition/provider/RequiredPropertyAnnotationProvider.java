package fr.sii.ogham.core.condition.provider;

import static fr.sii.ogham.core.condition.fluent.Conditions.alwaysTrue;
import static fr.sii.ogham.core.condition.fluent.Conditions.not;
import static fr.sii.ogham.core.condition.fluent.Conditions.requiredProperty;
import static fr.sii.ogham.core.condition.fluent.Conditions.requiredPropertyValue;

import java.util.regex.Pattern;

import fr.sii.ogham.core.builder.annotation.RequiredProperty;
import fr.sii.ogham.core.condition.Condition;
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
			return alwaysTrue();
		} else {
			FluentCondition<T> mainCondition = exists(annotation);
			if (!annotation.is().isEmpty()) {
				mainCondition = mainCondition.and(matchesValue(annotation));
			}
			if (!annotation.pattern().isEmpty()) {
				mainCondition = mainCondition.and(matchesPattern(annotation));
			}
			for (String excludeValue : annotation.excludes()) {
				mainCondition = mainCondition.and(not(matchesExcludes(annotation, excludeValue)));
			}
			return mainCondition;
		}
	}

	private FluentCondition<T> exists(RequiredProperty annotation) {
		FluentCondition<T> condition = requiredProperty(propertyResolver, annotation.value());
		for (String alternative : annotation.alternatives()) {
			condition = condition.or(Conditions.<T> requiredProperty(propertyResolver, alternative));
		}
		return condition;
	}

	private FluentCondition<T> matchesValue(RequiredProperty annotation) {
		FluentCondition<T> condition = requiredPropertyValue(propertyResolver, annotation.value(), annotation.is());
		for (String alternative : annotation.alternatives()) {
			condition = condition.or(Conditions.<T> requiredPropertyValue(propertyResolver, alternative, annotation.is()));
		}
		return condition;
	}

	private FluentCondition<T> matchesPattern(RequiredProperty annotation) {
		Pattern pattern = Pattern.compile(annotation.pattern(), annotation.flags());
		FluentCondition<T> condition = requiredPropertyValue(propertyResolver, annotation.value(), pattern);
		for (String alternative : annotation.alternatives()) {
			condition = condition.or(Conditions.<T> requiredPropertyValue(propertyResolver, alternative, pattern));
		}
		return condition;
	}

	private FluentCondition<T> matchesExcludes(RequiredProperty annotation, String excludeValue) {
		FluentCondition<T> condition = requiredPropertyValue(propertyResolver, annotation.value(), excludeValue);
		for (String alternative : annotation.alternatives()) {
			condition = condition.or(Conditions.<T> requiredPropertyValue(propertyResolver, alternative, excludeValue));
		}
		return condition;
	}

}
