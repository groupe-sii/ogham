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
import java.util.Map.Entry;

import fr.sii.ogham.core.exception.util.BeanWrapperException;
import fr.sii.ogham.core.exception.util.InvalidPropertyException;

/**
 * Simple implementation that wraps a bean in order to access the properties of
 * the bean.
 * 
 * This implementation delegates the access to the properties to
 * {@link Accessor}s.
 * 
 * @author Aurélien Baudet
 *
 */
public class SimpleReadMethodBeanReadWrapper implements BeanReadWrapper {
	private final Object bean;
	private final Map<String, Accessor<Object>> accessors;

	/**
	 * Builds the map of accessors for each bean property.
	 * 
	 * @param bean
	 *            the bean that may have properties to access later
	 */
	public SimpleReadMethodBeanReadWrapper(Object bean) {
		super();
		this.bean = bean;
		this.accessors = new HashMap<>();
		initialize(bean);
	}

	@Override
	public Object getPropertyValue(String name) throws InvalidPropertyException {
		if (getWrappedBean() == null) {
			return null;
		}

		Accessor<Object> accessor = accessors.get(name);
		if (accessor == null) {
			throw new InvalidPropertyException("No accessor for property " + name + " on bean " + getClassName(), bean, name);
		}

		return accessor.getValue();
	}

	@Override
	public List<String> getProperties() {
		return new ArrayList<>(accessors.keySet());
	}

	@Override
	public Object getWrappedBean() {
		return bean;
	}

	@SuppressWarnings("unchecked")
	private void initialize(Object bean) {
		if(bean==null) {
			return;
		}
		
		if(isPrimitiveOrWrapper(bean.getClass())) {
			throw new IllegalArgumentException("Primitive values can't be used as bean");
		} else if (bean instanceof Map) {
			initializeMap((Map<Object, Object>) bean, accessors);
		} else {
			initializeBean(bean, accessors);
		}
	}

	private static void initializeMap(Map<Object, Object> map, Map<String, Accessor<Object>> accessors) {
		for (Entry<Object, Object> entry : map.entrySet()) {
			accessors.put(entry.getKey().toString(), new DirectAccessor<Object>(entry.getValue()));
		}
	}

	private static void initializeBean(Object bean, Map<String, Accessor<Object>> accessors) {
		Class<? extends Object> beanClass = bean.getClass();
		try {
			final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
			final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			if (propertyDescriptors != null) {
				for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
					if (propertyDescriptor != null) {
						final String name = propertyDescriptor.getName();
						final Method readMethod = propertyDescriptor.getReadMethod();

						if (readMethod != null) {
							accessors.put(name, new ReadMethodAccessor<Object>(bean, name, readMethod));
						}
					}
				}
			}
		} catch (final IntrospectionException e) {
			throw new BeanWrapperException("Failed to initialize bean wrapper on " + beanClass, e);
		}
	}


	private String getClassName() {
		return bean==null ? "null" : bean.getClass().getSimpleName();
	}
}
