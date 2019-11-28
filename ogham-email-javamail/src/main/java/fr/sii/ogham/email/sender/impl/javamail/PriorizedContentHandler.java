package fr.sii.ogham.email.sender.impl.javamail;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.mail.Multipart;
import javax.mail.internet.MimePart;

import fr.sii.ogham.core.message.content.Content;
import fr.sii.ogham.core.util.PriorityComparator;
import fr.sii.ogham.core.util.PriorizedMatchingHandler;
import fr.sii.ogham.email.exception.javamail.ContentHandlerException;
import fr.sii.ogham.email.exception.javamail.NoContentHandlerException;
import fr.sii.ogham.email.message.Email;

/**
 * Provides a content handler based on the class of the content.
 * 
 * @author Aurélien Baudet
 *
 */
public class PriorizedContentHandler implements JavaMailContentHandler {

	/**
	 * The list of content handlers with corresponding matcher and priority
	 */
	private List<PriorizedMatchingHandler<JavaMailContentHandler>> matchingHandlers;

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
	public PriorizedContentHandler(List<PriorizedMatchingHandler<JavaMailContentHandler>> matchingHandlers) {
		super();
		this.matchingHandlers = matchingHandlers;
	}

	/**
	 * Initialize with an empty list
	 */
	public PriorizedContentHandler() {
		this(new ArrayList<>());
	}

	@Override
	public void setContent(MimePart message, Multipart multipart, Email email, Content content) throws ContentHandlerException {
		JavaMailContentHandler contentHandler = getContentHandler(content);
		if (contentHandler == null) {
			throw new NoContentHandlerException("there is no content handler defined for managing " + content.getClass().getSimpleName() + " content class", content);
		}
		contentHandler.setContent(message, multipart, email, content);
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
	public void register(Predicate<Content> contentMatcher, JavaMailContentHandler handler, int priority) {
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
	public void register(Predicate<Content> contentMatcher, JavaMailContentHandler handler) {
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
	public void register(Class<? extends Content> clazz, JavaMailContentHandler handler, int priority) {
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
	public void register(Class<? extends Content> clazz, JavaMailContentHandler handler) {
		register(clazz, handler, -matchingHandlers.size());
	}

	private JavaMailContentHandler getContentHandler(Content content) {
		matchingHandlers.sort(new PriorityComparator<>(PriorizedMatchingHandler::getPriority));
		for (PriorizedMatchingHandler<JavaMailContentHandler> entry : matchingHandlers) {
			if (entry.matches(content)) {
				return entry.getHandler();
			}
		}
		return null;
	}
}
