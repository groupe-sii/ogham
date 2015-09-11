package fr.sii.ogham.core.condition;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

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

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("accept").isEqual();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(accept).hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(accept);
		return builder.toString();
	}
}
