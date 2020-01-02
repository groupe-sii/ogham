package mock.context;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;



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
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		NestedBean rhs = (NestedBean) obj;
		return new EqualsBuilder().append(nested, rhs.nested).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(nested).hashCode();
	}
}
