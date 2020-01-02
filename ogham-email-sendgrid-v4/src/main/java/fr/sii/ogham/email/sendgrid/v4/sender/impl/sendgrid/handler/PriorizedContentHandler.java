package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sendgrid.helpers.mail.Mail;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.util.PriorityComparator;
import fr.sii.ogham.core.util.PriorizedMatchingHandler;
import fr.sii.ogham.email.exception.sendgrid.ContentHandlerException;
import fr.sii.ogham.email.message.Email;

/**
 * Implementation of {@link SendGridContentHandler} that delegates content
 * handling to specialized instances, if one matching the actual content type
 * has been declared.
 */
public final class PriorizedContentHandler implements SendGridContentHandler {

	private static final Logger LOG = LoggerFactory.getLogger(PriorizedContentHandler.class);

	/**
	 * The list of content handlers with corresponding matcher and priority
	 */
	private List<PriorizedMatchingHandler<SendGridContentHandler>> matchingHandlers;

	/**
	 * Initialize with the list of content handlers with matching predicate and
	 * priority.
	 * 
	 * Higher priority means that handler will be used first.
	 * 
	 * @param matchingHandlers
	 *            the list of content handlers with the associated content
	 *            matcher and priority
	 */
	public PriorizedContentHandler(List<PriorizedMatchingHandler<SendGridContentHandler>> matchingHandlers) {
		super();
		this.matchingHandlers = matchingHandlers;
	}

	/**
	 * Initialize with an empty list
	 */
	public PriorizedContentHandler() {
		this(new ArrayList<>());
	}


	/**
	 * Register a new content handler using the provided content matcher.
	 * 
	 * @param contentMatcher
	 *            the class of the content to match
	 * @param handler
	 *            the content handler to use if the class matches
	 * @param priority
	 *            the priority order (means that matching handler with higher
	 *            priority is used)
	 */
	public void register(Predicate<Content> contentMatcher, SendGridContentHandler handler, int priority) {
		matchingHandlers.add(new PriorizedMatchingHandler<>(contentMatcher, handler, priority));
	}

	/**
	 * Register a new content handler using the provided content matcher.
	 * 
	 * The priority is the registration order (first registered has higher
	 * priority).
	 * 
	 * @param contentMatcher
	 *            the class of the content
	 * @param handler
	 *            the content handler
	 */
	public void register(Predicate<Content> contentMatcher, SendGridContentHandler handler) {
		register(contentMatcher, handler, -matchingHandlers.size());
	}

	/**
	 * Register a new content handler. The matching predicate is only based on
	 * the class. It means that if the class of the content is a sub-class of
	 * clazz parameter, then the associated handler is used.
	 * 
	 * @param clazz
	 *            the class of the content to match
	 * @param handler
	 *            the content handler to use if the class matches
	 * @param priority
	 *            the priority order (means that matching handler with higher
	 *            priority is used)
	 */
	public void register(Class<? extends Content> clazz, SendGridContentHandler handler, int priority) {
		if (handler == null) {
			throw new IllegalArgumentException("[handler] cannot be null");
		}
		if (clazz == null) {
			throw new IllegalArgumentException("[clazz] cannot be null");
		}
		
		register(c -> clazz.isAssignableFrom(c.getClass()), handler, priority);
	}

	/**
	 * Register a new content handler. The matching predicate is only based on
	 * the class. It means that if the class of the content is a sub-class of
	 * clazz parameter, then the associated handler is used.
	 * 
	 * The priority is the registration order (first registered has higher
	 * priority).
	 * 
	 * @param clazz
	 *            the class of the content
	 * @param handler
	 *            the content handler
	 */
	public void register(Class<? extends Content> clazz, SendGridContentHandler handler) {
		register(clazz, handler, -matchingHandlers.size());
	}

	@Override
	public void setContent(final Email original, final Mail email, final Content content) throws ContentHandlerException {
		if (email == null) {
			throw new IllegalArgumentException("[email] cannot be null");
		}
		if (content == null) {
			throw new IllegalArgumentException("[content] cannot be null");
		}

		final Class<?> clazz = content.getClass();
		LOG.debug("Getting content handler for type {}", clazz);
		final SendGridContentHandler handler = findHandler(content);
		if (handler == null) {
			LOG.warn("No content handler found for requested type {}", clazz);
			throw new ContentHandlerException("No content handler found for content type " + clazz.getSimpleName());
		} else {
			handler.setContent(original, email, content);
		}
	}

	private SendGridContentHandler findHandler(final Content content) {
		matchingHandlers.sort(new PriorityComparator<>(PriorizedMatchingHandler::getPriority));
		for (PriorizedMatchingHandler<SendGridContentHandler> entry : matchingHandlers) {
			if (entry.matches(content)) {
				return entry.getHandler();
			}
		}
		return null;
	}

}
