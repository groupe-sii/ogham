package fr.sii.ogham.core.builder;

import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.message.Message;

public interface ImplementationBuilder<T> extends Builder<T> {
	public Condition<Message> getRuntimeCondition();
}
