package fr.sii.ogham.core.util.bean;

// TODO: handle other arguments (delegate, failOnMissingProperty) ?
public class MapRecursiveBeanReadWrapperFactory implements BeanReadWrapperFactory {

	@Override
	public BeanReadWrapper create(Object bean) {
		return new MapBeanReadWrapper(new RecursiveBeanReadWrapper(bean, this));
	}
	
}
