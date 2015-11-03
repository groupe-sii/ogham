package fr.sii.ogham.core.condition.provider;

import java.util.regex.Pattern;

import fr.sii.ogham.core.builder.annotation.RequiredProperty;
import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.condition.NotCondition;
import fr.sii.ogham.core.condition.OrCondition;
import fr.sii.ogham.core.condition.PropertyPatternCondition;
import fr.sii.ogham.core.condition.PropertyValueCondition;
import fr.sii.ogham.core.condition.RequiredPropertyCondition;
import fr.sii.ogham.core.env.PropertyResolver;

public class RequiredPropertyAnnotationProvider<T> implements ConditionProvider<RequiredProperty, T> {
	private final PropertyResolver propertyResolver;
	
	public RequiredPropertyAnnotationProvider(PropertyResolver propertyResolver) {
		super();
		this.propertyResolver = propertyResolver;
	}

	@Override
	public Condition<T> provide(RequiredProperty annotation) {
		if(annotation==null) {
			return new FixedCondition<>(true);
		} else {
			AndCondition<T> mainCondition = new AndCondition<>();
			mainCondition.and(propertyOrAlternatives(annotation));
			if(!annotation.is().isEmpty()) {
				mainCondition.and(new PropertyValueCondition<T>(annotation.value(), annotation.is(), propertyResolver));
			}
			if(!annotation.pattern().isEmpty()) {
				mainCondition.and(new PropertyPatternCondition<T>(annotation.value(), Pattern.compile(annotation.pattern(), annotation.flags()), propertyResolver));
			}
			for(String excludeValue : annotation.excludes()) {
				mainCondition.and(new NotCondition<>(new PropertyValueCondition<T>(annotation.value(), excludeValue, propertyResolver)));
			}
			return mainCondition;
		}
	}

	private Condition<T> propertyOrAlternatives(RequiredProperty annotation) {
		OrCondition<T> orCondition = new OrCondition<>(new RequiredPropertyCondition<T>(annotation.value(), propertyResolver));
		for(String alternative : annotation.alternatives()) {
			orCondition.or(new RequiredPropertyCondition<T>(alternative, propertyResolver));
		}
		return orCondition;
	}


}
