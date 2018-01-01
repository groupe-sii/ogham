package fr.sii.ogham.core.util.bean;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import fr.sii.ogham.core.exception.util.InvalidPropertyException;

/**
 * Wraps a bean and exposes as a Map.
 * 
 * The Map is indexed by the property names defined in the bean and allow to
 * access the value using the Map insterface.
 * 
 * This implementation doesn't support update methods (remove, clear, put...)
 * and doesn't support access to values directly (values, containsValue).
 * 
 * This implementation delegates access to real bean properties through a
 * {@link SimpleReadMethodBeanReadWrapper} instance (by default). You can
 * override this default wrapper using the constructor
 * {@link #MapBeanReadWrapper(BeanReadWrapper)}.
 * 
 * @author Aurélien Baudet
 *
 */
public class MapBeanReadWrapper implements Map<String, Object> {
	private final BeanReadWrapper delegate;

	/**
	 * Initializes the wrapper for the provided bean.
	 * 
	 * {@link SimpleReadMethodBeanReadWrapper} is used to access bean
	 * properties.
	 * 
	 * @param bean
	 *            the bean to wrap
	 */
	public MapBeanReadWrapper(Object bean) {
		this(new SimpleReadMethodBeanReadWrapper(bean));
	}

	/**
	 * Point of extension by providing custom bean wrapper.
	 * 
	 * @param delegate
	 *            the bean wrapper that access bean properties
	 */
	public MapBeanReadWrapper(BeanReadWrapper delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public int size() {
		return getProperties().size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		for (String prop : getProperties()) {
			if (prop.equals(key)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("containsValue not implemented");
	}

	@Override
	public Object get(Object key) {
		if (key == null) {
			throw new IllegalArgumentException("key must ot be null");
		}
		return getPropertyValue(key.toString());
	}

	@Override
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException("put not implemented");
	}

	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException("remove not implemented");
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException("putAll not implemented");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("clear not implemented");
	}

	@Override
	public Set<String> keySet() {
		return new HashSet<>(delegate.getProperties());
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException("values not implemented");
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		Set<Entry<String, Object>> entries = new TreeSet<>();
		for (String prop : getProperties()) {
			entries.add(new MapBeanReadWrapperEntry(delegate, prop));
		}
		return entries;
	}

	private Object getPropertyValue(String name) throws InvalidPropertyException {
		return delegate.getPropertyValue(name);
	}

	private List<String> getProperties() {
		return delegate.getProperties();
	}

	/**
	 * Specific Entry implementation that delegates to a real bean wrapper in order
	 * to access bean properties.
	 * 
	 * @author Aurélien Baudet
	 *
	 */
	public static class MapBeanReadWrapperEntry implements Entry<String, Object>, Comparable<MapBeanReadWrapperEntry> {
		private final BeanReadWrapper delegate;
		private final String key;

		public MapBeanReadWrapperEntry(BeanReadWrapper delegate, String key) {
			super();
			this.delegate = delegate;
			this.key = key;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public Object getValue() {
			return delegate.getPropertyValue(key);
		}

		@Override
		public Object setValue(Object value) {
			throw new UnsupportedOperationException("entry.setValue not implemented");
		}

		@Override
		public int compareTo(MapBeanReadWrapperEntry o) {
			return key.compareTo(o.getKey());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof MapBeanReadWrapperEntry)) {
				return false;
			}
			MapBeanReadWrapperEntry other = (MapBeanReadWrapperEntry) obj;
			if (key == null) {
				if (other.key != null) {
					return false;
				}
			} else if (!key.equals(other.key)) {
				return false;
			}
			return true;
		}
	}
}
