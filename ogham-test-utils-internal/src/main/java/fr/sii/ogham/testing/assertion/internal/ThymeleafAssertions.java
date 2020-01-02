package fr.sii.ogham.testing.assertion.internal;

import static fr.sii.ogham.testing.assertion.internal.helper.ImplementationFinder.findParsers;
import static java.util.stream.Collectors.toSet;

import java.util.Set;

import org.thymeleaf.TemplateEngine;

import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.template.thymeleaf.common.ThymeleafParser;
import fr.sii.ogham.testing.assertion.HasParent;
import fr.sii.ogham.testing.assertion.internal.helper.FoundParser;

/**
 * Helper class to make assertions on Thymealf instances created by Ogham.
 * 
 * For example, to ensure that particular engine is used for emails:
 * 
 * <pre>
* {@code
* email()
*   .engine(isA(SpringTemplateEngine.class))
* }
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class ThymeleafAssertions extends HasParent<MessagingServiceAssertions> {
	private final Set<FoundParser<ThymeleafParser>> thymeleafParsers;

	public ThymeleafAssertions(MessagingServiceAssertions parent, Set<FoundParser<ThymeleafParser>> thymeleafParsers) {
		super(parent);
		this.thymeleafParsers = thymeleafParsers;
	}

	/**
	 * Make assertions on {@link ThymeleafParser} instantiated for both emails
	 * and sms.
	 * 
	 * For example, to ensure that all {@link ThymeleafParser}s use the right
	 * {@link TemplateEngine} instance:
	 * 
	 * <pre>
	 * {@code
	 * all()
	 *   .engine(isA(SpringTemplateEngine.class))
	 * }
	 * </pre>
	 * 
	 * @return builder for fluent chaining
	 */
	public ThymeleafParserAssertions all() {
		return new ThymeleafParserAssertions(this, thymeleafParsers.stream().map(FoundParser::getParser).collect(toSet()));
	}

	/**
	 * Make assertions on {@link ThymeleafParser} instantiated for sending
	 * emails only.
	 * 
	 * For example, to ensure that {@link ThymeleafParser} used for sending
	 * emails uses the right {@link TemplateEngine} instance:
	 * 
	 * <pre>
	 * {@code
	 * email()
	 *   .engine(isA(SpringTemplateEngine.class))
	 * }
	 * </pre>
	 * 
	 * @return builder for fluent chaining
	 */
	public ThymeleafParserAssertions email() {
		return new ThymeleafParserAssertions(this, thymeleafParsers.stream().filter(f -> f.getMessageType().equals(Email.class)).map(FoundParser::getParser).collect(toSet()));
	}

	/**
	 * Make assertions on {@link ThymeleafParser} instantiated for sending sms
	 * only.
	 * 
	 * For example, to ensure that {@link ThymeleafParser} used for sending sms
	 * uses the right {@link TemplateEngine} instance:
	 * 
	 * <pre>
	 * {@code
	 * sms()
	 *   .engine(isA(SpringTemplateEngine.class))
	 * }
	 * </pre>
	 * 
	 * @return builder for fluent chaining
	 */
	public ThymeleafParserAssertions sms() {
		return new ThymeleafParserAssertions(this, thymeleafParsers.stream().filter(f -> f.getMessageType().equals(Sms.class)).map(FoundParser::getParser).collect(toSet()));
	}

	/**
	 * Find instances of {@link ThymeleafParser}
	 * 
	 * @param messagingService
	 *            the messaging service
	 * @return the found instances
	 */
	public static Set<FoundParser<ThymeleafParser>> getThymeleafParsers(MessagingService messagingService) {
		return findParsers(messagingService, ThymeleafParser.class);
	}

}
