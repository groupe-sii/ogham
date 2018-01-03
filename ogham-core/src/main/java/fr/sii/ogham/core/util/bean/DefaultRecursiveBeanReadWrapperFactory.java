package fr.sii.ogham.core.util.bean;

/**
 * Implementation that is used by default by
 * {@link RecursiveBeanReadWrapper} in order to wrap nested beans.
 * 
 * @author Aurélien Baudet
 *
 */
public class DefaultRecursiveBeanReadWrapperFactory implements BeanReadWrapperFactory {

	@Override
	public BeanReadWrapper create(Object bean) {
		return new RecursiveBeanReadWrapper(new SimpleBeanReadWrapper(bean));
	}

}
