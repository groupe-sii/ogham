package fr.sii.ogham.core.condition;

/**
 * Basic condition that always give the same result: the result you provided at
 * construction.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the object to test
 */
public class FixedCondition<T> implements Condition<T> {

	private boolean accept;

	public FixedCondition(boolean accept) {
		super();
		this.accept = accept;
	}

	@Override
	public boolean accept(T obj) {
		return accept;
	}

}
