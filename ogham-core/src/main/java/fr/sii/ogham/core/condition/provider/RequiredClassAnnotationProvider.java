package fr.sii.ogham.core.condition.provider;

import static fr.sii.ogham.core.condition.fluent.Conditions.alwaysTrue;
import static fr.sii.ogham.core.condition.fluent.Conditions.not;
import static fr.sii.ogham.core.condition.fluent.Conditions.requiredClass;

import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.fluent.Conditions;
import fr.sii.ogham.core.condition.fluent.FluentCondition;

/**
 * Provider that handle {@link RequiredClass} annotation to provide a condition.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the kind of the object under conditions
 */
public class RequiredClassAnnotationProvider<T> implements ConditionProvider<RequiredClass, T> {

	@Override
	public Condition<T> provide(RequiredClass annotation) {
		if (annotation == null) {
			return alwaysTrue();
		} else {
			FluentCondition<T> mainCondition = classNameOrAlternatives(annotation);
			for (String exclude : annotation.excludes()) {
				mainCondition = mainCondition.and(not(Conditions.<T> requiredClass(exclude)));
			}
			return mainCondition;
		}
	}

	private FluentCondition<T> classNameOrAlternatives(RequiredClass annotation) {
		FluentCondition<T> condition = requiredClass(annotation.value());
		for (String alternative : annotation.alternatives()) {
			condition = condition.or(Conditions.<T> requiredClass(alternative));
		}
		return condition;
	}

}
