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
import fr.sii.ogham.core.message.Message;

public class MessageConditions {
	public static FluentCondition<Message> $(Condition<Message> condition) {
		return new FluentCondition<>(condition);
	}
	
	@SafeVarargs
	public static FluentCondition<Message> and(Condition<Message>... conditions) {
		return new FluentCondition<>(new AndCondition<>(conditions));
	}
	
	public static FluentCondition<Message> and(List<Condition<Message>> conditions) {
		return new FluentCondition<>(new AndCondition<>(conditions));
	}
	
	@SafeVarargs
	public static FluentCondition<Message> or(Condition<Message>... conditions) {
		return new FluentCondition<>(new OrCondition<>(conditions));
	}
	
	public static FluentCondition<Message> or(List<Condition<Message>> conditions) {
		return new FluentCondition<>(new OrCondition<>(conditions));
	}
	
	public static FluentCondition<Message> not(Condition<Message> condition) {
		return new FluentCondition<>(new NotCondition<>(condition));
	}
	
	public static FluentCondition<Message> requiredProperty(PropertyResolver propertyResolver, String property) {
		return new FluentCondition<>(new RequiredPropertyCondition<Message>(property, propertyResolver));
	}

	public static FluentCondition<Message> requiredClass(String className) {
		return new FluentCondition<>(new RequiredClassCondition<Message>(className));
	}

	public static FluentCondition<Message> alwaysTrue() {
		return new FluentCondition<>(new FixedCondition<Message>(true));
	}

	public static FluentCondition<Message> alwaysFalse() {
		return new FluentCondition<>(new FixedCondition<Message>(false));
	}
	
	private MessageConditions() {
		super();
	}
}
