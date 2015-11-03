package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.builder.annotation.RequiredClasses;
import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.condition.RequiredClassCondition;

public class RequiredClassesAnnotationProvider<T> implements ConditionProvider<RequiredClasses, T> {
	private final RequiredClassAnnotationProvider<T> delegate;

	public RequiredClassesAnnotationProvider() {
		super();
		this.delegate = new RequiredClassAnnotationProvider<>();
	}

	@Override
	public Condition<T> provide(RequiredClasses annotation) {
		if(annotation==null) {
			return new FixedCondition<>(true);
		} else {
			AndCondition<T> mainCondition = new AndCondition<>();
			for(String requiredClassName : annotation.value()) {
				mainCondition.and(new RequiredClassCondition<T>(requiredClassName));
			}
			for(RequiredClass subAnnotation : annotation.classes()) {
				mainCondition.and(delegate.provide(subAnnotation));
			}
			return mainCondition;
		}
	}

}
