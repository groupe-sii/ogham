package fr.sii.ogham.core.util.bean;

import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sii.ogham.core.exception.util.BeanWrapperException;

/**
 * Some utility methods for bean wrapper management
 * 
 * @author Aurélien Baudet
 *
 */
public final class BeanWrapperUtils {

	private static final List<Class<?>> INVALID_TYPES = new ArrayList<>();
	static {
		INVALID_TYPES.add(String.class);
		INVALID_TYPES.add(Number.class);
	}

	/**
	 * Check if the bean type can be wrapped or not.
	 * 
	 * @param bean
	 *            the bean instance
	 * @return false if null or valid type, true if primitive type or string
	 */
	@SuppressWarnings("squid:S2250")
	public static boolean isInvalid(Object bean) {
		if (bean == null) {
			return false;
		}
		return isPrimitiveOrWrapper(bean.getClass()) 
				|| INVALID_TYPES.contains(bean.getClass())
				|| isInstanceOfInvalid(bean.getClass());
	}

	/**
	 * Get the class name of the bean even if null.
	 * 
	 * @param bean
	 *            the bean instance
	 * @return the class of the bean
	 */
	public static String getClassName(Object bean) {
		return bean == null ? "null" : bean.getClass().getName();
	}

	/**
	 * Get the whole list of read {@link Method}s using introspection.
	 * 
	 * @param bean
	 *            the bean to introspect
	 * @return the map of bean property getters (indexed by property name)
	 */
	public static Map<String, Method> getReadMethods(Object bean) {
		Class<? extends Object> beanClass = bean.getClass();
		try {
			Map<String, Method> readMethods = new HashMap<>();
			final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
			final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			if (propertyDescriptors != null) {
				putReadMethods(readMethods, propertyDescriptors);
			}
			return readMethods;
		} catch (final IntrospectionException e) {
			throw new BeanWrapperException("Failed to initialize bean wrapper on " + beanClass, e);
		}
	}

	/**
	 * Get a read {@link Method} for a particular property.
	 * 
	 * @param bean
	 *            the bean instance
	 * @param name
	 *            the name of the property
	 * @return the getter method for the property
	 */
	public static Method getReadMethod(Object bean, String name) {
		return getReadMethods(bean).get(name);
	}

	private static void putReadMethods(Map<String, Method> readMethods, final PropertyDescriptor[] propertyDescriptors) {
		for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if (propertyDescriptor != null) {
				final String name = propertyDescriptor.getName();
				final Method readMethod = propertyDescriptor.getReadMethod();

				if (readMethod != null) {
					readMethods.put(name, readMethod);
				}
			}
		}
	}

	private static boolean isInstanceOfInvalid(Class<?> clazz) {
		for(Class<?> c : INVALID_TYPES) {
			if(c.isAssignableFrom(clazz)) {
				return true;
			}
		}
		return false;
	}

	private BeanWrapperUtils() {
		super();
	}

}
