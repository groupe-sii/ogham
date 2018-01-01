package fr.sii.ogham.core.util.bean;

import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;

import java.util.List;

import fr.sii.ogham.core.exception.util.InvalidPropertyException;

/**
 * A wrapper that wraps original bean and all nested property values (only
 * objects, not primitives).
 * 
 * The aim is to abstract access to the bean and also to nested properties that
 * are beans.
 * 
 * @author Aurélien Baudet
 *
 */
public class RecursiveReadMethodBeanReadWrapper implements BeanReadWrapper {
	private final BeanReadWrapper delegate;
	private final BeanReadWrapperFactory recursiveFactory;

	public RecursiveReadMethodBeanReadWrapper(BeanReadWrapper delegate) {
		this(delegate, new DefaultRecursiveReadMethodBeanReadWrapperFactory());
	}

	public RecursiveReadMethodBeanReadWrapper(BeanReadWrapper delegate, BeanReadWrapperFactory recursiveFactory) {
		super();
		this.delegate = delegate;
		this.recursiveFactory = recursiveFactory;
	}

	@Override
	public Object getPropertyValue(String name) throws InvalidPropertyException {
		Object value = delegate.getPropertyValue(name);
		if (value == null) {
			return null;
		}
		if (isPrimitiveOrWrapper(value.getClass())) {
			return value;
		}
		return recursiveFactory.create(value);
	}

	@Override
	public List<String> getProperties() {
		return delegate.getProperties();
	}

	@Override
	public Object getWrappedBean() {
		return delegate.getWrappedBean();
	}
}
