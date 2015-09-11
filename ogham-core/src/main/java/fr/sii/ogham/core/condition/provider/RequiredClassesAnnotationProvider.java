package fr.sii.ogham.core.condition.provider;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.builder.annotation.RequiredClass;
import fr.sii.ogham.core.builder.annotation.RequiredClasses;
import fr.sii.ogham.core.condition.AndCondition;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.condition.NotCondition;
import fr.sii.ogham.core.condition.OrCondition;
import fr.sii.ogham.core.condition.RequiredClassCondition;

public class RequiredClassesAnnotationProvider implements ConditionProvider<Class<?>> {

	@Override
	public Condition<Class<?>> provide(Class<?> source) {
		RequiredClasses annotation = source.getAnnotation(RequiredClasses.class);
		if(annotation==null) {
			return new FixedCondition<>(true);
		} else {
			// generate the conditions for the list of required classes provided by
			// value attribute
			List<Condition<Class<?>>> required = generateSeveral(annotation.value());
			// generate the conditions for the list of complex cases (excluded
			// classes, alternatives...)
			List<Condition<Class<?>>> altAndExcludes = generate(annotation.classes());
			// the final condition is a and between all required class names and
			// altAndExcludes class names
			return new AndCondition<>(required).addConditions(altAndExcludes);
		}
	}

	private List<Condition<Class<?>>> generate(RequiredClass[] annotations) {
		List<Condition<Class<?>>> conditions = new ArrayList<>(annotations.length);
		for (RequiredClass annotation : annotations) {
			conditions.add(generate(annotation));
		}
		return conditions;
	}

	private Condition<Class<?>> generate(RequiredClass annotation) {
		// @formatter:off
		return new AndCondition<>(
						new OrCondition<>(generateSeveral(annotation.alternatives())).addCondition(generateOne(annotation.value())),
						new NotCondition<>(new OrCondition<>(generateSeveral(annotation.excludes()))));
		// @formatter:on
	}

	private Condition<Class<?>> generateOne(String className) {
		return new RequiredClassCondition<>(className);
	}

	private List<Condition<Class<?>>> generateSeveral(String[] classNames) {
		List<Condition<Class<?>>> conditions = new ArrayList<>();
		for (String className : classNames) {
			conditions.add(generateOne(className));
		}
		return conditions;
	}

}
