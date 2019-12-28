package fr.sii.ogham.assertion.internal;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.ConditionalSender;
import fr.sii.ogham.core.sender.ContentTranslatorSender;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.sender.MultiImplementationSender;
import fr.sii.ogham.core.sender.MultiImplementationSender.Implementation;
import fr.sii.ogham.core.service.EverySupportingMessagingService;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.service.WrapExceptionMessagingService;
import fr.sii.ogham.core.template.parser.AutoDetectTemplateParser;
import fr.sii.ogham.core.template.parser.AutoDetectTemplateParser.TemplateImplementation;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.core.translator.content.EveryContentTranslator;
import fr.sii.ogham.core.translator.content.MultiContentTranslator;
import fr.sii.ogham.core.translator.content.TemplateContentTranslator;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sender.EmailSender;
import fr.sii.ogham.email.sender.impl.JavaMailSender;
import fr.sii.ogham.email.sendgrid.sender.SendGridSender;
import fr.sii.ogham.email.sendgrid.v2.sender.impl.SendGridV2Sender;
import fr.sii.ogham.email.sendgrid.v4.sender.impl.SendGridV4Sender;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.SmsSender;
import fr.sii.ogham.template.freemarker.FreeMarkerParser;
import fr.sii.ogham.template.thymeleaf.common.ThymeleafParser;

/**
 * Helper to make assertions on {@link MessagingService} instance created by a
 * {@link MessagingBuilder}.
 * 
 * @author Aurélien Baudet
 *
 */
public class MessagingServiceAssertions {
	private static final String DELEGATE_FIELD = "delegate";
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
	 * All the {@link ThymeleafParser} instances are automatically retrieved
	 * from {@link MessagingService}. You can then select which
	 * {@link ThymeleafParser} to check by using fluent API.
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
		return new ThymeleafAssertions(this, findParsers(messagingService, ThymeleafParser.class));
	}

	/**
	 * Use it to ensure that FreeMarker is configured as expected.
	 * 
	 * <p>
	 * All the {@link FreeMarkerParser} instances are automatically retrieved
	 * from {@link MessagingService}. You can then select which
	 * {@link FreeMarkerParser} to check by using fluent API.
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
		return new FreemarkerAssersions(this, findParsers(messagingService, FreeMarkerParser.class));
	}

	/**
	 * Use it to ensure that {@link JavaMailSender} is configured as expected.
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
		return new JavaMailAssertions(this, findSender(messagingService, JavaMailSender.class));
	}

	private static SendGridSender getSendGridSender(MessagingService messagingService) {
		try {
			return findSender(messagingService, SendGridV4Sender.class);
		} catch (IllegalStateException e) {		// NOSONAR
			// skip
		}
		try {
			return findSender(messagingService, SendGridV2Sender.class);
		} catch (IllegalStateException e) {		// NOSONAR
			// skip
		}
		throw new IllegalStateException("No SendGridSender available");
	}

	private static SendGridSender getSendGridSender(MessagingService messagingService, Class<? extends SendGridSender> senderClass) {
		return findSender(messagingService, senderClass);
	}

	private static MessagingService getRealService(MessagingService service) {
		try {
			if (service instanceof WrapExceptionMessagingService) {
				return getRealService((MessagingService) readField(service, DELEGATE_FIELD, true));
			}
			if (service instanceof EverySupportingMessagingService) {
				return service;
			}
			throw new IllegalStateException("Unknown MessagingService implementation, please add it here");
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to find real MessagingService", e);
		}
	}

	private static <T extends MessageSender> T findSender(MessagingService service, Class<T> clazz) {
		Set<T> found = findSenders(service, clazz);
		if (found.isEmpty()) {
			throw new IllegalStateException("Failed to find MessageSender of " + clazz.getTypeName());
		}
		if (found.size() == 1) {
			return found.iterator().next();
		}
		throw new IllegalStateException("Several matching MessageSender for " + clazz.getTypeName() + " found");
	}

	@SuppressWarnings("unchecked")
	private static <T extends MessageSender> Set<T> findSenders(MessagingService service, Class<T> clazz) {
		try {
			Set<T> found = new HashSet<>();
			List<ConditionalSender> senders = (List<ConditionalSender>) readField(getRealService(service), "senders", true);
			for (ConditionalSender sender : senders) {
				found.addAll(findSenders(sender, clazz));
			}
			return found;
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to find senders of type " + clazz.getTypeName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends MessageSender> Set<T> findSenders(MessageSender sender, Class<T> clazz) {
		try {
			Set<T> found = new HashSet<>();
			if (clazz.isAssignableFrom(sender.getClass())) {
				found.add((T) sender);
			}
			// Any sender that delegates in the chain (FillerSender,
			// AttachmentResourceTranslatorSender, ContentTranslatorSender,
			// PhoneNumberTranslatorSender)
			// TODO: FallbackSender
			if (delegates(sender)) {
				MessageSender delegate = (MessageSender) readField(sender, DELEGATE_FIELD, true);
				found.addAll(findSenders(delegate, clazz));
			}
			if (sender instanceof MultiImplementationSender<?>) {
				found.addAll(findSenders((MultiImplementationSender<?>) sender, clazz));
			}
			return found;
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to find senders of type " + clazz.getTypeName(), e);
		}
	}

	private static boolean delegates(MessageSender sender) {
		try {
			Object value = readField(sender, DELEGATE_FIELD, true);
			return value instanceof MessageSender;
		} catch (IllegalAccessException | IllegalArgumentException e) {		// NOSONAR
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends MessageSender> Set<T> findSenders(MultiImplementationSender<?> sender, Class<T> clazz) {
		Set<T> found = new HashSet<>();
		List<Implementation> implementations = sender.getImplementations();
		for (Implementation impl : implementations) {
			if (clazz.isAssignableFrom(impl.getSender().getClass())) {
				found.add((T) impl.getSender());
			}
		}
		return found;
	}

	public static class FoundParser<T extends TemplateParser> {
		private final T parser;
		private final Class<? extends Message> messageType;

		public FoundParser(T parser, Class<? extends Message> messageType) {
			super();
			this.parser = parser;
			this.messageType = messageType;
		}

		public T getParser() {
			return parser;
		}

		public Class<? extends Message> getMessageType() {
			return messageType;
		}
	}

	private static <T extends TemplateParser> Set<FoundParser<T>> findParsers(MessagingService service, Class<T> clazz) {
		try {
			Set<FoundParser<T>> found = new HashSet<>();
			Set<ContentTranslatorSender> translatorSenders = findSenders(service, ContentTranslatorSender.class);
			for (ContentTranslatorSender sender : translatorSenders) {
				Set<TemplateContentTranslator> translators = findTranslators(sender, TemplateContentTranslator.class);
				for (TemplateContentTranslator translator : translators) {
					found.addAll(findParsers(clazz, translator, (MessageSender) readField(sender, DELEGATE_FIELD, true)));
				}
			}
			return found;
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to find parser of type " + clazz.getTypeName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends TemplateParser> Set<FoundParser<T>> findParsers(Class<T> clazz, TemplateContentTranslator translator, MessageSender sender) throws IllegalAccessException {
		Set<FoundParser<T>> found = new HashSet<>();
		TemplateParser parser = (TemplateParser) readField(translator, "parser", true);
		if (clazz.isAssignableFrom(parser.getClass())) {
			found.add(new FoundParser<>((T) parser, getMessageType(sender)));
		}
		if (parser instanceof AutoDetectTemplateParser) {
			found.addAll(findParsers(clazz, (AutoDetectTemplateParser) parser, sender));
		}
		return found;
	}

	@SuppressWarnings("unchecked")
	private static <T extends TemplateParser> Set<FoundParser<T>> findParsers(Class<T> clazz, AutoDetectTemplateParser parser, MessageSender sender) throws IllegalAccessException {
		Set<FoundParser<T>> found = new HashSet<>();
		List<TemplateImplementation> implementations = (List<TemplateImplementation>) readField(parser, "implementations", true);
		for (TemplateImplementation impl : implementations) {
			if (clazz.isAssignableFrom(impl.getParser().getClass())) {
				found.add(new FoundParser<>((T) impl.getParser(), getMessageType(sender)));
			}
		}
		return found;
	}

	private static Class<? extends Message> getMessageType(MessageSender sender) {
		Set<EmailSender> emailSenders = findSenders(sender, EmailSender.class);
		if (!emailSenders.isEmpty()) {
			return Email.class;
		}
		Set<SmsSender> smsSenders = findSenders(sender, SmsSender.class);
		if (!smsSenders.isEmpty()) {
			return Sms.class;
		}
		throw new IllegalStateException("Failed to find message type");
	}

	private static <T extends ContentTranslator> Set<T> findTranslators(ContentTranslatorSender translatorSender, Class<T> clazz) {
		try {
			ContentTranslator translator = (ContentTranslator) readField(translatorSender, "translator", true);
			return findTranslators(translator, clazz);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to find translator of type " + clazz.getTypeName(), e);
		}
	}

	private static <T extends ContentTranslator> Set<T> findTranslators(ContentTranslator translator, Class<T> clazz) {
		try {
			Set<T> found = new HashSet<>();
			if (translator instanceof EveryContentTranslator) {
				found.addAll(findTranslators((EveryContentTranslator) translator, clazz));
			}
			if (translator instanceof MultiContentTranslator) {
				found.addAll(findTranslators((ContentTranslator) readField(translator, DELEGATE_FIELD, true), clazz));
			}
			return found;
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to read 'delegate' of MultiContentTranslator", e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends ContentTranslator> Set<T> findTranslators(EveryContentTranslator translator, Class<T> clazz) {
		Set<T> found = new HashSet<>();
		for (ContentTranslator t : translator.getTranslators()) {
			if (clazz.isAssignableFrom(t.getClass())) {
				found.add((T) t);
			}
		}
		return found;
	}
}
