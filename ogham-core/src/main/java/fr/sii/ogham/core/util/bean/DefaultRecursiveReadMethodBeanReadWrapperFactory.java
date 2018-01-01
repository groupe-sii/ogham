package fr.sii.ogham.core.util.bean;

/**
 * Implementation that is used by default by
 * {@link RecursiveReadMethodBeanReadWrapper} in order to wrap nested beans.
 * 
 * @author Aurélien Baudet
 *
 */
public class DefaultRecursiveReadMethodBeanReadWrapperFactory implements BeanReadWrapperFactory {

	@Override
	public BeanReadWrapper create(Object bean) {
		return new RecursiveReadMethodBeanReadWrapper(new SimpleReadMethodBeanReadWrapper(bean));
	}

}
