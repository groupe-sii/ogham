package fr.sii.ogham.core.builder;

import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.message.Message;

public interface ActivableAtRuntime {
	Condition<Message> getCondition();
}
