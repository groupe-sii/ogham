package fr.sii.ogham.assertion.internal.matcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * Matcher that checks if the tested object is the same instance as the bean
 * registered in Spring context.
 * 
 * <p>
 * If no bean is declared, then it will fail.
 * 
 * <p>
 * If a bean is declared but not same instance, then it will fail.
 * 
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            Type of the tested object
 */
public class IsSpringBeanInstance<T> extends BaseMatcher<T> {
	private final ApplicationContext context;
	private final Class<?> beanClass;
	private boolean beanExists;

	/**
	 * This matcher requires the Spring context and which bean to retrieve. It
	 * is helpful to avoid handling {@link BeansException} when bean is not
	 * registered at all.
	 * 
	 * @param context
	 *            the Spring application context
	 * @param beanClass
	 *            the bean to retrieve
	 */
	public IsSpringBeanInstance(ApplicationContext context, Class<?> beanClass) {
		super();
		this.context = context;
		this.beanClass = beanClass;
	}

	@Override
	public boolean matches(Object item) {
		try {
			Object bean = context.getBean(beanClass);
			beanExists = true;
			return bean == item;
		} catch (BeansException e) {
			beanExists = false;
			return false;
		}
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(beanExists ? "bean not registered" : "bean registered but not same instance");
	}

}
