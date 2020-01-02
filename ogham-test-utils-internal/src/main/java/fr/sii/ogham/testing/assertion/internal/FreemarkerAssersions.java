package fr.sii.ogham.testing.assertion.internal;

import static fr.sii.ogham.testing.assertion.internal.helper.ImplementationFinder.findParsers;
import static java.util.stream.Collectors.toSet;

import java.util.Set;

import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.template.freemarker.FreeMarkerParser;
import fr.sii.ogham.testing.assertion.HasParent;
import fr.sii.ogham.testing.assertion.internal.helper.FoundParser;
import freemarker.template.Configuration;

/**
 * Helper class to make assertions on FreeMarker instances created by Ogham.
 * 
 * For example, to ensure that particular configuration is used for emails:
 * 
 * <pre>
 * {@code
 * email()
 *   .configuration()
 *     .defaultEncoding(equalTo("UTF-8"))
 * }
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class FreemarkerAssersions extends HasParent<MessagingServiceAssertions> {
	private final Set<FoundParser<FreeMarkerParser>> freeMarkerParsers;

	public FreemarkerAssersions(MessagingServiceAssertions parent, Set<FoundParser<FreeMarkerParser>> freeMarkerParsers) {
		super(parent);
		this.freeMarkerParsers = freeMarkerParsers;
	}

	/**
	 * Make assertions on {@link FreeMarkerParser} for both email and sms.
	 * 
	 * For example, to ensure that all FreeMarker {@link Configuration} uses the
	 * right default encoding:
	 * 
	 * <pre>
	 * {@code
	 * all()
	 *   .configuration()
	 *     .defaultEncoding(equalTo("UTF-8"))
	 * }
	 * </pre>
	 * 
	 * @return builder for fluent chaining
	 */
	public FreemarkerParserAssertions all() {
		return new FreemarkerParserAssertions(this, freeMarkerParsers.stream()
				.map(FoundParser::getParser)
				.collect(toSet()));
	}

	/**
	 * Make assertions on {@link FreeMarkerParser} instantiated for sending
	 * emails only.
	 * 
	 * For example, to ensure that FreeMarker {@link Configuration} used by
	 * email uses the right default encoding:
	 * 
	 * <pre>
	 * {@code
	 * email()
	 *   .configuration()
	 *     .defaultEncoding(equalTo("UTF-8"))
	 * }
	 * </pre>
	 * 
	 * @return builder for fluent chaining
	 */
	public FreemarkerParserAssertions email() {
		// @formatter:off
		return new FreemarkerParserAssertions(this, freeMarkerParsers.stream()
				.filter(f -> f.getMessageType().equals(Email.class))
				.map(FoundParser::getParser)
				.collect(toSet()));
		// @formatter:on
	}

	/**
	 * Make assertions on {@link FreeMarkerParser} instantiated for sending sms
	 * only.
	 * 
	 * For example, to ensure that FreeMarker {@link Configuration} used by sms
	 * uses the right default encoding:
	 * 
	 * <pre>
	 * {@code
	 * sms()
	 *   .configuration()
	 *     .defaultEncoding(equalTo("UTF-8"))
	 * }
	 * </pre>
	 * 
	 * @return builder for fluent chaining
	 */
	public FreemarkerParserAssertions sms() {
		// @formatter:off
		return new FreemarkerParserAssertions(this, freeMarkerParsers.stream()
				.filter(f -> f.getMessageType().equals(Sms.class))
				.map(FoundParser::getParser)
				.collect(toSet()));
		// @formatter:on
	}

	/**
	 * Find instances of {@link FreeMarkerParser}
	 * 
	 * @param messagingService
	 *            the messaging service
	 * @return the found instances
	 */
	public static Set<FoundParser<FreeMarkerParser>> getFreemarkerParsers(MessagingService messagingService) {
		return findParsers(messagingService, FreeMarkerParser.class);
	}

}
