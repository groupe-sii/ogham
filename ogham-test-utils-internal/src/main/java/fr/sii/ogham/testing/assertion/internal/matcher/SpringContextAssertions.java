package fr.sii.ogham.testing.assertion.internal.matcher;

import org.hamcrest.Matcher;
import org.springframework.context.ApplicationContext;

/**
 * Make assertions on Spring
 * 
 * @author Aurélien Baudet
 *
 */
public class SpringContextAssertions {
	/**
	 * Ensure that provided object is the same instance as the bean available in
	 * Spring context.
	 * 
	 * <p>
	 * If no bean is declared, then it will fail.
	 * 
	 * <p>
	 * If a bean is declared but not same instance, then it will fail.
	 * 
	 * 
	 * @param <T>
	 *            the type of the expected object
	 * @param context
	 *            the Spring context (used to retrieve bean instance)
	 * @param beanClass
	 *            the class of the bean to match
	 * @return the matcher
	 */
	public static <T> Matcher<T> isSpringBeanInstance(ApplicationContext context, Class<?> beanClass) {
		return new IsSpringBeanInstance<>(context, beanClass);
	}

	private SpringContextAssertions() {
		super();
	}
}
