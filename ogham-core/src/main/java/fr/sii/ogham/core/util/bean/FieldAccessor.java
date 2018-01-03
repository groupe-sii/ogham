package fr.sii.ogham.core.util.bean;

import java.lang.reflect.Field;

import fr.sii.ogham.core.exception.util.InvalidPropertyException;

/**
 * Access the property of the bean using reflection. The property is accessed
 * through {@link Field}.
 * 
 * <p>
 * By default, if the field is not accessible, it makes it accessible and tries
 * to access it.
 * 
 * @author Aurélien Baudet
 * @param <T>
 *            The type of the value
 *
 */
public class FieldAccessor<T> implements Accessor<T> {
	private final Object bean;
	private final String name;
	private final boolean makeAccessible;
	private Field field;

	/**
	 * Use reflection to access the bean property named by <code>name</code>
	 * parameter.
	 * 
	 * <p>
	 * If the field is not accessible (private or protected), it automatically
	 * makes it accessible.
	 * 
	 * @param bean
	 *            the bean to get property value from
	 * @param name
	 *            the name of the property
	 */
	public FieldAccessor(Object bean, String name) {
		this(bean, name, true);
	}

	/**
	 * Use reflection to access the bean property named by <code>name</code>
	 * parameter.
	 * 
	 * <p>
	 * You can choose to force access to non accessible fields or not.
	 * 
	 * @param bean
	 *            the bean to get property value from
	 * @param name
	 *            the name of the property
	 * @param makeAccessible
	 *            make the field accessible if not
	 */
	public FieldAccessor(Object bean, String name, boolean makeAccessible) {
		super();
		this.bean = bean;
		this.name = name;
		this.makeAccessible = makeAccessible;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getValue() {
		try {
			if (field == null) {
				initField();
			}
			return (T) field.get(bean);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | ClassCastException e) {
			throw new InvalidPropertyException("Failed to get value for property '" + name + "' on bean '" + getClassName() + "'", bean, name, e);
		}
	}

	private void initField() throws NoSuchFieldException {
		field = bean.getClass().getDeclaredField(name);
		if (makeAccessible && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	private String getClassName() {
		return bean == null ? "null" : bean.getClass().getName();
	}
}
