package fr.sii.ogham.core.builder.condition;

import java.util.List;

import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.condition.NotCondition;
import fr.sii.ogham.core.condition.OrCondition;
import fr.sii.ogham.core.condition.RequiredClassCondition;
import fr.sii.ogham.core.condition.RequiredPropertyCondition;
import fr.sii.ogham.core.env.PropertyResolver;

public class Conditions {
	public static <T> FluentCondition<T> $(Condition<T> condition) {
		return new FluentCondition<>(condition);
	}
	
	@SafeVarargs
	public static <T> FluentCondition<T> and(Condition<T>... conditions) {
		return new FluentCondition<>(new AndCondition<>(conditions));
	}
	
	public static <T> FluentCondition<T> and(List<Condition<T>> conditions) {
		return new FluentCondition<>(new AndCondition<>(conditions));
	}
	
	@SafeVarargs
	public static <T> FluentCondition<T> or(Condition<T>... conditions) {
		return new FluentCondition<>(new OrCondition<>(conditions));
	}
	
	public static <T> FluentCondition<T> or(List<Condition<T>> conditions) {
		return new FluentCondition<>(new OrCondition<>(conditions));
	}
	
	public static <T> FluentCondition<T> not(Condition<T> condition) {
		return new FluentCondition<>(new NotCondition<>(condition));
	}
	
	public static <T> FluentCondition<T> requiredProperty(PropertyResolver propertyResolver, String property) {
		return new FluentCondition<>(new RequiredPropertyCondition<T>(property, propertyResolver));
	}

	public static <T> FluentCondition<T> requiredClass(String className) {
		return new FluentCondition<>(new RequiredClassCondition<T>(className));
	}

	public static <T> FluentCondition<T> alwaysTrue() {
		return new FluentCondition<>(new FixedCondition<T>(true));
	}

	public static <T> FluentCondition<T> alwaysFalse() {
		return new FluentCondition<>(new FixedCondition<T>(false));
	}

	private Conditions() {
		super();
	}
}
