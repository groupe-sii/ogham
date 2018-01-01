package fr.sii.ogham.core.util.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.sii.ogham.core.exception.util.BeanWrapperException;
import fr.sii.ogham.core.exception.util.InvalidPropertyException;

/**
 * Access object property using reflection
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            The type of the value
 */
public class ReadMethodAccessor<T> implements Accessor<T> {
	private static final Object[] NULL_ARGUMENTS = {};
	private final Object bean;
	private final String name;
	private final Method readMethod;
	private final Accessor<T> defaultAccessor;

	/**
	 * Initialize the accessor with the provided bean and property name.
	 * 
	 * <p>
	 * The read {@link Method} is searched by reflection and using the property
	 * name.
	 * 
	 * <p>
	 * If no read method defined for the property, the {@link FieldAccessor} is
	 * used.
	 * 
	 * @param bean
	 *            the bean that will be accessed
	 * @param name
	 *            the name of the property to access
	 */
	public ReadMethodAccessor(Object bean, String name) {
		this(bean, name, getReadMethod(bean, name), new FieldAccessor<T>(bean, name));
	}

	/**
	 * Initialize the accessor with the provided bean and property name.
	 * 
	 * <p>
	 * The read {@link Method} is directly provided in order to avoid reflection
	 * scanning.
	 * 
	 * <p>
	 * If no read method defined for the property, the {@link FieldAccessor} is
	 * used.
	 * 
	 * @param bean
	 *            the bean that will be accessed
	 * @param name
	 *            the name of the property to access
	 * @param readMethod
	 *            the getter method obtained through reflection that is used to
	 *            access the property
	 */
	public ReadMethodAccessor(Object bean, String name, Method readMethod) {
		this(bean, name, readMethod, new FieldAccessor<T>(bean, name));
	}

	/**
	 * Initialize the accessor with the provided bean and property name.
	 * 
	 * <p>
	 * The read {@link Method} is directly provided in order to avoid reflection
	 * scanning.
	 * 
	 * <p>
	 * If no read method defined for the property, the
	 * <code>defaultAccessor</code> parameter is used.
	 * 
	 * @param bean
	 *            the bean that will be accessed
	 * @param name
	 *            the name of the property to access
	 * @param readMethod
	 *            the getter method obtained through reflection that is used to
	 *            access the property. May be null if no read method (no getter)
	 *            exists in the bean class but the property exists
	 * @param defaultAccessor
	 *            the default accessor if read method is null. May be null if
	 *            only read method is used
	 */
	public ReadMethodAccessor(Object bean, String name, Method readMethod, Accessor<T> defaultAccessor) {
		super();
		this.bean = bean;
		this.name = name;
		this.readMethod = readMethod;
		this.defaultAccessor = defaultAccessor;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getValue() {
		if (readMethod == null && defaultAccessor == null) {
			throw new InvalidPropertyException("Can't get value for property " + name + " on bean " + getClassName() + ": no getter and no default accessor provided.", bean, name);
		}
		if (readMethod == null) {
			return defaultAccessor.getValue();
		}

		try {
			return (T) readMethod.invoke(bean, NULL_ARGUMENTS);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
			throw new InvalidPropertyException("Failed to get value for property " + name + " on bean " + getClassName(), bean, name, e);
		}
	}

	private String getClassName() {
		return bean == null ? "null" : bean.getClass().getSimpleName();
	}

	private static Method getReadMethod(Object bean, String name) {
		Class<? extends Object> beanClass = bean.getClass();
		try {
			final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
			final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			if (propertyDescriptors != null) {
				for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
					if (propertyDescriptor != null) {
						final String n = propertyDescriptor.getName();
						final Method readMethod = propertyDescriptor.getReadMethod();

						if (n.equals(name) && readMethod != null) {
							return readMethod;
						}
					}
				}
			}
			return null;
		} catch (final IntrospectionException e) {
			throw new BeanWrapperException("Failed to initialize bean wrapper on " + beanClass, e);
		}
	}
}
