package fr.sii.notification.mock.context;

public class NestedBean {
	private Object nested;

	public NestedBean(Object nested) {
		super();
		this.nested = nested;
	}

	public Object getNested() {
		return nested;
	}
}
