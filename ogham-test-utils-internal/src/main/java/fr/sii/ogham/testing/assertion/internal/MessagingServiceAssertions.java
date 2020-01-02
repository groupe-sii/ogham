package fr.sii.ogham.testing.assertion.internal;

import static fr.sii.ogham.testing.assertion.internal.CloudhopperAssertions.getCloudhopperSender;
import static fr.sii.ogham.testing.assertion.internal.FreemarkerAssersions.getFreemarkerParsers;
import static fr.sii.ogham.testing.assertion.internal.JavaMailAssertions.getJavaMailSender;
import static fr.sii.ogham.testing.assertion.internal.SendGridAssertions.getSendGridSender;
import static fr.sii.ogham.testing.assertion.internal.ThymeleafAssertions.getThymeleafParsers;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.service.MessagingService;

/**
 * Helper to make assertions on {@link MessagingService} instance created by a
 * {@link MessagingBuilder}.
 * 
 * @author Aurélien Baudet
 *
 */
public class MessagingServiceAssertions {
	private final MessagingService messagingService;

	public MessagingServiceAssertions(MessagingService messagingService) {
		this.messagingService = messagingService;
	}

	/**
	 * Use it to ensure that SendGrid is configured as expected.
	 * 
	 * <p>
	 * The version of SendGrid is automatically determined. If the automatic
	 * detection doesn't work, use {@link #sendGrid(SendGridVersion)}
	 * 
	 * For example:
	 * 
	 * <pre>
	 * {@code
	 * sendGrid()
	 *   .apiKey(equalTo("foo"))
	 * }
	 * </pre>
	 * 
	 * @return builder for fluent chaining
	 */
	public SendGridAssertions sendGrid() {
		return new SendGridAssertions(this, getSendGridSender(messagingService));
	}

	/**
	 * Use it to ensure that SendGrid (for a particular version) is configured
	 * as expected.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * {code
	 * sendGrid(SendGridVersion.V2)
	 *   .apiKey(equalTo("foo"))
	 * }
	 * </pre>
	 * 
	 * @param sendGridVersion
	 *            the SendGrid version to use
	 * @return builder for fluent chaining
	 */
	public SendGridAssertions sendGrid(SendGridVersion sendGridVersion) {
		return new SendGridAssertions(this, getSendGridSender(messagingService, sendGridVersion.getSenderClass()));
	}

	/**
	 * Use it to ensure that Thymeleaf is configured as expected.
	 * 
	 * <p>
	 * All the ThymeleafParser instances are automatically retrieved
	 * from {@link MessagingService}. You can then select which
	 * ThymeleafParser to check by using fluent API.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * {@code
	 * thymeleaf()
	 *   .email()
	 *     .engine(isA(SpringTemplateEngine.class))
	 * }
	 * </pre>
	 * 
	 * @return the builder for fluent chaining
	 */
	public ThymeleafAssertions thymeleaf() {
		return new ThymeleafAssertions(this, getThymeleafParsers(messagingService));
	}

	/**
	 * Use it to ensure that FreeMarker is configured as expected.
	 * 
	 * <p>
	 * All the FreeMarkerParser instances are automatically retrieved
	 * from {@link MessagingService}. You can then select which
	 * FreeMarkerParser to check by using fluent API.
	 * 
	 * For example, to ensure that UTF-8 is used as default encoding for email
	 * parser:
	 * 
	 * <pre>
	 * {@code
	 * freemarker()
	 *   .email()
	 *     .configuration()
	 *       .defaultEncoding(equalTo("UTF-8"))
	 * }
	 * </pre>
	 * 
	 * @return the builder for fluent chaining
	 */
	public FreemarkerAssersions freemarker() {
		return new FreemarkerAssersions(this, getFreemarkerParsers(messagingService));
	}

	/**
	 * Use it to ensure that JavaMailSender is configured as expected.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * {@code
	 * javaMail()
	 *   .host(equalTo("foo"))
	 * }
	 * </pre>
	 * 
	 * @return the builder for fluent chaining
	 */
	public JavaMailAssertions javaMail() {
		return new JavaMailAssertions(this, getJavaMailSender(messagingService));
	}

	/**
	 * Use it to ensure that CloudhopperSMPPSender is configured as expected.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * {@code
	 * cloudhopper()
	 *   .host(equalTo("foo"))
	 * }
	 * </pre>
	 * 
	 * @return the builder for fluent chaining
	 */
	public CloudhopperAssertions cloudhopper() {
		return new CloudhopperAssertions(this, getCloudhopperSender(messagingService));
	}

}
