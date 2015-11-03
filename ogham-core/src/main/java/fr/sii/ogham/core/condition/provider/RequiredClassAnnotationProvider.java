package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.condition.NotCondition;
import fr.sii.ogham.core.condition.OrCondition;
import fr.sii.ogham.core.condition.RequiredClassCondition;

public class RequiredClassAnnotationProvider<T> implements ConditionProvider<RequiredClass, T> {

	@Override
	public Condition<T> provide(RequiredClass annotation) {
		if(annotation==null) {
			return new FixedCondition<>(true);
		} else {
			AndCondition<T> mainCondition = new AndCondition<>();
			mainCondition.and(classNameOrAlternatives(annotation));
			for(String exclude : annotation.excludes()) {
				mainCondition.and(new NotCondition<>(new RequiredClassCondition<T>(exclude)));
			}
			return mainCondition;
		}
	}

	private Condition<T> classNameOrAlternatives(RequiredClass annotation) {
		OrCondition<T> orCondition = new OrCondition<>(new RequiredClassCondition<T>(annotation.value()));
		for(String alternative : annotation.alternatives()) {
			orCondition.or(new RequiredClassCondition<T>(alternative));
		}
		return orCondition;
	}
	
}
