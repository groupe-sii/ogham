package fr.sii.ogham.core.util.bean;

import static fr.sii.ogham.core.util.bean.BeanWrapperUtils.getClassName;
import static fr.sii.ogham.core.util.bean.BeanWrapperUtils.getReadMethods;
import static fr.sii.ogham.core.util.bean.BeanWrapperUtils.isInvalid;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public class SimpleBeanReadWrapper implements BeanReadWrapper {
	private final Object bean;
	private final Map<String, Accessor<Object>> accessors;
	private final boolean failOnMissingProperty;

	/**
	 * Builds the map of accessors for each bean property.
	 * 
	 * If a property doesn't exist, an {@link InvalidPropertyException} is
	 * thrown.
	 * 
	 * @param bean
	 *            the bean that may have properties to access later
	 */
	public SimpleBeanReadWrapper(Object bean) {
		this(bean, true);
	}

	/**
	 * Builds the map of accessors for each bean property.
	 * 
	 * @param bean
	 *            the bean that may have properties to access later
	 * @param failOnMissingProperty
	 *            if false null is returned if the property doesn't exist, if
	 *            true an {@link InvalidPropertyException} is thrown if the
	 *            property doesn't exist
	 */
	public SimpleBeanReadWrapper(Object bean, boolean failOnMissingProperty) {
		super();
		this.bean = bean;
		this.accessors = new HashMap<>();
		this.failOnMissingProperty = failOnMissingProperty;
		initialize(bean);
	}

	@Override
	public Object getPropertyValue(String name) throws InvalidPropertyException {
		if (getWrappedBean() == null) {
			return null;
		}

		Accessor<Object> accessor = accessors.get(name);
		if (failOnMissingProperty && accessor == null) {
			throw new InvalidPropertyException("No accessor for property '" + name + "' on bean '" + getClassName(bean) + "'", bean, name);
		}

		return accessor == null ? null : accessor.getValue();
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
		if (bean == null) {
			return;
		}

		if (isInvalid(bean)) {
			throw new IllegalArgumentException(getClassName(bean) + " values can't be used as bean");
		} else if (bean instanceof Collection) {
			initializeCollection((Collection<Object>) bean, accessors);
		} else if (bean instanceof Map) {
			initializeMap((Map<Object, Object>) bean, accessors);
		} else {
			initializeBean(bean, accessors);
		}
	}

	private static void initializeCollection(Collection<Object> collection, Map<String, Accessor<Object>> accessors) {
		int i = 0;
		for (Iterator<Object> it = collection.iterator(); it.hasNext(); i++) {
			accessors.put(Integer.toString(i), new DirectAccessor<Object>(it.next()));
		}
	}

	private static void initializeMap(Map<Object, Object> map, Map<String, Accessor<Object>> accessors) {
		for (Entry<Object, Object> entry : map.entrySet()) {
			accessors.put(entry.getKey().toString(), new DirectAccessor<Object>(entry.getValue()));
		}
	}

	private static void initializeBean(Object bean, Map<String, Accessor<Object>> accessors) {
		Map<String, Method> readMethods = getReadMethods(bean);
		for(Entry<String, Method> entry : readMethods.entrySet()) {
			String name = entry.getKey();
			Method readMethod = entry.getValue();
			if (readMethod != null) {
				accessors.put(name, new ReadMethodAccessor<Object>(bean, name, readMethod));
			}
		}
	}
}
