package fr.sii.ogham.testing.assertion.internal.helper;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.sender.ConditionalSender;
import fr.sii.ogham.core.sender.ContentTranslatorSender;
import fr.sii.ogham.core.sender.MessageSender;
import fr.sii.ogham.core.sender.MultiImplementationSender;
import fr.sii.ogham.core.sender.MultiImplementationSender.Implementation;
import fr.sii.ogham.core.service.CleanableMessagingService;
import fr.sii.ogham.core.service.EverySupportingMessagingService;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.core.service.WrapExceptionMessagingService;
import fr.sii.ogham.core.template.parser.AutoDetectTemplateParser;
import fr.sii.ogham.core.template.parser.TemplateParser;
import fr.sii.ogham.core.template.parser.AutoDetectTemplateParser.TemplateImplementation;
import fr.sii.ogham.core.translator.content.ContentTranslator;
import fr.sii.ogham.core.translator.content.EveryContentTranslator;
import fr.sii.ogham.core.translator.content.MultiContentTranslator;
import fr.sii.ogham.core.translator.content.TemplateContentTranslator;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.email.sender.EmailSender;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.SmsSender;

/**
 * Utility class to find implementations used by Ogham service.
 * 
 * @author Aurélien Baudet
 *
 */
public final class ImplementationFinder {
	private static final String DELEGATE_FIELD = "delegate";

	/**
	 * Find recursively a finder of the provided class
	 * 
	 * @param <T>
	 *            the type of the found sender
	 * @param service
	 *            the messaging service
	 * @param clazz
	 *            the class of the sender to find
	 * @return the found sender
	 */
	public static <T extends MessageSender> T findSender(MessagingService service, Class<T> clazz) {
		Set<T> found = findSenders(service, clazz);
		if (found.isEmpty()) {
			throw new IllegalStateException("Failed to find MessageSender of " + clazz.getTypeName());
		}
		if (found.size() == 1) {
			return found.iterator().next();
		}
		throw new IllegalStateException("Several matching MessageSender for " + clazz.getTypeName() + " found");
	}

	/**
	 * Find all senders for the given type
	 * 
	 * @param <T>
	 *            the type of the senders to find
	 * @param service
	 *            the messaging service
	 * @param clazz
	 *            the class of the senders to find
	 * @return the found senders
	 */
	@SuppressWarnings("unchecked")
	public static <T extends MessageSender> Set<T> findSenders(MessagingService service, Class<T> clazz) {
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

	/**
	 * Find template parsers of the given type.
	 * 
	 * @param <T>
	 *            the type of the template parsers to find
	 * @param service
	 *            the messaging service
	 * @param clazz
	 *            the class of the template parsers to find
	 * @return the found parsers
	 */
	public static <T extends TemplateParser> Set<FoundParser<T>> findParsers(MessagingService service, Class<T> clazz) {
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

	private static MessagingService getRealService(MessagingService service) {
		try {
			if (service instanceof WrapExceptionMessagingService) {
				return getRealService((MessagingService) readField(service, DELEGATE_FIELD, true));
			}
			if (service instanceof CleanableMessagingService) {
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
		} catch (IllegalAccessException | IllegalArgumentException e) { // NOSONAR
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

	private ImplementationFinder() {
		super();
	}
}
