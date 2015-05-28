package fr.sii.notification.core.util;

import java.lang.reflect.Field;

import fr.sii.notification.core.exception.util.FieldAccessException;

public class EqualsBuilder {
	private Object object;

	private Object other;

	private boolean equals;

	private org.apache.commons.lang3.builder.EqualsBuilder delegate;

	public EqualsBuilder(Object object, Object other) {
		super();
		this.object = object;
		this.other = other;
		equals = other != null && (other == this || object.getClass() == other.getClass());
		delegate = new org.apache.commons.lang3.builder.EqualsBuilder();
	}

	public EqualsBuilder append(Object objectValue, Object otherValue) {
		delegate.append(objectValue, otherValue);
		return this;
	}

	public EqualsBuilder append(String... fields) {
		if (equals) {
			for (String field : fields) {
				try {
					delegate.append(getFieldValue(object, field), getFieldValue(other, field));
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					throw new FieldAccessException("Failed to access field " + field, e);
				}
			}
		}
		return this;
	}

	public EqualsBuilder appendSuper(boolean superEquals) {
		delegate.appendSuper(superEquals);
		return this;
	}

	private static Object getFieldValue(Object object, String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(object);
	}

	public boolean equals() {
		return equals && delegate.isEquals();
	}
}
