package fr.sii.ogham.core.util.bean;

/**
 * Factory to create a {@link BeanReadWrapper}.
 * 
 * @author Aurélien Baudet
 *
 */
public interface BeanReadWrapperFactory {
	/**
	 * Creates the instance of the {@link BeanReadWrapper} with the provided
	 * bean to must be wrapped.
	 * 
	 * @param bean
	 *            the bean to wrap
	 * @return the wrapper
	 */
	BeanReadWrapper create(Object bean);
}
