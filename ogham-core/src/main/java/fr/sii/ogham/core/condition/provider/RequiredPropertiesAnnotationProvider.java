package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.builder.annotation.RequiredProperties;
import fr.sii.ogham.core.builder.annotation.RequiredProperty;
import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.condition.RequiredClassCondition;
import fr.sii.ogham.core.env.PropertyResolver;

public class RequiredPropertiesAnnotationProvider<T> implements ConditionProvider<RequiredProperties, T> {
	private final RequiredPropertyAnnotationProvider<T> delegate;

	public RequiredPropertiesAnnotationProvider(PropertyResolver propertyResolver) {
		super();
		this.delegate = new RequiredPropertyAnnotationProvider<>(propertyResolver);
	}

	@Override
	public Condition<T> provide(RequiredProperties annotation) {
		if(annotation==null) {
			return new FixedCondition<>(true);
		} else {
			AndCondition<T> mainCondition = new AndCondition<>();
			for(String requiredClassName : annotation.value()) {
				mainCondition.and(new RequiredClassCondition<T>(requiredClassName));
			}
			for(RequiredProperty subAnnotation : annotation.props()) {
				mainCondition.and(delegate.provide(subAnnotation));
			}
			return mainCondition;
		}
	}

}
