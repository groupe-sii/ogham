package fr.sii.ogham.context;

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
}
