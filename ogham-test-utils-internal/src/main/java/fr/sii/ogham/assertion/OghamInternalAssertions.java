package fr.sii.ogham.assertion;

import org.hamcrest.Matcher;
import org.springframework.context.ApplicationContext;

import fr.sii.ogham.assertion.internal.MessagingServiceAssertions;
import fr.sii.ogham.assertion.internal.matcher.IsSpringBeanInstance;
import fr.sii.ogham.core.service.MessagingService;

/**
 * Utility class used by Ogham for testing result of {@link MessagingService}
 * configuration.
 * 
 * @author Aurélien Baudet
 *
 */
public class OghamInternalAssertions {
	/**
	 * Entry point to write assertions on {@link MessagingService} instance.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * {@code
	 * OghamInternalAssertions
	 *   .assertThat(messagingService)
	 *     .sendGrid()
	 *       .apiKey(equalTo("bar"))
	 *       .client(allOf(isA(SendGrid.class), not(isSpringBeanInstance(context, SendGrid.class))))
	 *       .and()
	 *     .thymeleaf()
	 * 		 .all()
	 *         .engine(isA(TemplateEngine.class))
	 *         .and()
	 *       .and()
	 *     .freemarker()
	 *       .all()
	 *         .configuration()
	 *           .defaultEncoding(equalTo(StandardCharsets.US_ASCII.name()));
	 * }
	 * </pre>
	 * 
	 * @param service
	 *            the service to make assertions with
	 * @return builder for fluent assertions on messaging service
	 */
	public static MessagingServiceAssertions assertThat(MessagingService service) {
		return new MessagingServiceAssertions(service);
	}

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
		return new IsSpringBeanInstance<T>(context, beanClass);
	}

	private OghamInternalAssertions() {
		super();
	}
}
