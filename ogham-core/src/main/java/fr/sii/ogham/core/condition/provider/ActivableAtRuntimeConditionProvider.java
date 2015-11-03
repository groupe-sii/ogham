package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.builder.ActivableAtRuntime;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.MessageSender;

public class ActivableAtRuntimeConditionProvider implements ConditionProvider<Builder<? extends MessageSender>, Message> {

	@Override
	public Condition<Message> provide(Builder<? extends MessageSender> source) {
		if(source instanceof ActivableAtRuntime) {
			return ((ActivableAtRuntime) source).getCondition();
		}
		return new FixedCondition<>(true);
	}


}
