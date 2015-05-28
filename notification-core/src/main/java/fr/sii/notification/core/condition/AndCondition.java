package fr.sii.notification.core.condition;

import java.util.List;

/**
 * Condition that provides a logical AND operation on manipulated conditions.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the object to test
 */
public class AndCondition<T> extends CompositeCondition<T> {

	@SafeVarargs
	public AndCondition(Condition<T>... conditions) {
		super(conditions);
	}

	public AndCondition(List<Condition<T>> conditions) {
		super(conditions);
	}

	@Override
	public boolean accept(T obj) {
		for (Condition<T> condition : getConditions()) {
			// if the condition rejects the object => stop now
			if (!condition.accept(obj)) {
				return false;
			}
		}
		// none condition has rejected the object => it is accepted
		return true;
	}

}
