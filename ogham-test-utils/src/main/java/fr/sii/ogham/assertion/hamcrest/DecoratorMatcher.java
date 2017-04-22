package fr.sii.ogham.assertion.hamcrest;

import org.hamcrest.Matcher;

/**
 * Mark a matcher as a decorator of another
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of expected value
 */
public interface DecoratorMatcher<T> {
	/**
	 * Access the decorated matcher
	 * 
	 * @return the matcher instance
	 */
	Matcher<T> getDecoree();
}