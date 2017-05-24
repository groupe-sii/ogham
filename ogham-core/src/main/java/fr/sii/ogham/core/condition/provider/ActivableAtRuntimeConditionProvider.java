package fr.sii.ogham.core.condition.provider;

import fr.sii.ogham.core.builder.ActivableAtRuntime;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.condition.FixedCondition;
import fr.sii.ogham.core.message.Message;

/**
 * If the source object implements {@link ActivableAtRuntime}, then this
 * provider returns directly the condition of
 * {@link ActivableAtRuntime#getCondition()}.
 * 
 * <p>
 * If the object doesn't implement {@link ActivableAtRuntime}, then returns a
 * condition that is always true.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class ActivableAtRuntimeConditionProvider implements ConditionProvider<Object, Message> {

	@Override
	public Condition<Message> provide(Object source) {
		if (source instanceof ActivableAtRuntime) {
			return ((ActivableAtRuntime) source).getCondition();
		}
		return new FixedCondition<>(true);
	}

}
