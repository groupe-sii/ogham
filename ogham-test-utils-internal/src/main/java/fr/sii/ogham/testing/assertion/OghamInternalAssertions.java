package fr.sii.ogham.testing.assertion;

import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.testing.assertion.internal.MessagingServiceAssertions;

/**
 * Utility class used by Ogham for testing result of {@link MessagingService}
 * configuration.
 * 
 * @author Aurélien Baudet
 *
 */
public final class OghamInternalAssertions {
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


	private OghamInternalAssertions() {
		super();
	}
}
