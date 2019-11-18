package fr.sii.ogham.core.util;

import java.util.function.Predicate;

import fr.sii.ogham.core.message.content.Content;

/**
 * Entry that declares a handler that is capable of handling a particular
 * content if the provided matcher matches the content.
 * 
 * The entry has also a priority order. Several handlers may be registered and
 * able to handle a particular content. Only the one with the highest priority
 * is used.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the handler type
 */
public class PriorizedMatchingHandler<T> {
	/**
	 * The matcher used to check if the content can be handled by the associated
	 * handler
	 */
	private final Predicate<Content> contentMatcher;
	/**
	 * The handler
	 */
	private final T handler;
	/**
	 * The priority of the handler
	 */
	private final int priority;

	public PriorizedMatchingHandler(Predicate<Content> contentMatcher, T handler, int priority) {
		super();
		this.contentMatcher = contentMatcher;
		this.handler = handler;
		this.priority = priority;
	}

	public boolean matches(Content content) {
		return contentMatcher.test(content);
	}

	public T getHandler() {
		return handler;
	}

	public int getPriority() {
		return priority;
	}
}