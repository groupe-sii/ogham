package fr.sii.notification.mock.context;

import fr.sii.notification.core.util.EqualsBuilder;
import fr.sii.notification.core.util.HashCodeBuilder;


public class NestedBean {
	private Object nested;

	public NestedBean(Object nested) {
		super();
		this.nested = nested;
	}

	public Object getNested() {
		return nested;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"nested\": ").append(nested).append("}");
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("nested").isEqual();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(nested).hashCode();
	}
}
