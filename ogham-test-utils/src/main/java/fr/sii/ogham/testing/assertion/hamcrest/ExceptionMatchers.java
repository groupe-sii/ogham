package fr.sii.ogham.testing.assertion.hamcrest;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.hamcrest.Matcher;

public final class ExceptionMatchers {

	/**
	 * Returns a matcher that verifies that any exception in the stack matches
	 * the finder exists.
	 *
	 * @param expectedClass
	 *            to find the cause in exception stack
	 * @param <T>
	 *            type of the outer exception
	 * @return the matcher
	 */
	public static <T extends Throwable> Matcher<T> hasAnyCause(final Class<? extends Throwable> expectedClass) {
		return hasAnyCause(instanceOf(expectedClass));
	}

	/**
	 * Returns a matcher that verifies that any exception in the stack matches
	 * the finder exists.
	 *
	 * @param finder
	 *            to find the cause in exception stack, the finder is also the matcher
	 * @param <T>
	 *            type of the outer exception
	 * @return the matcher
	 */
	public static <T extends Throwable> Matcher<T> hasAnyCause(final Matcher<? extends Throwable> finder) {
		return new ThrowableAnyCauseMatcher<>(finder, notNullValue(Throwable.class));
	}

	/**
	 * Returns a matcher that verifies that any exception in the stack matches
	 * the finder and also checks that the supplied matcher evaluates to true.
	 *
	 * @param finder
	 *            to find the cause in exception stack
	 * @param matcher
	 *            to apply to the cause of the outer exception
	 * @param <T>
	 *            type of the outer exception
	 * @return the matcher
	 */
	public static <T extends Throwable> Matcher<T> hasAnyCause(final Matcher<? extends Throwable> finder, final Matcher<? extends Throwable> matcher) {
		return new ThrowableAnyCauseMatcher<>(finder, matcher);
	}

	/**
	 * Returns a matcher that verifies that any exception in the stack matches
	 * the expected class and also checks that the supplied matcher evaluates to
	 * true.
	 *
	 * @param expectedClass
	 *            to find the cause in exception stack
	 * @param matcher
	 *            to apply to the cause of the outer exception
	 * @param <T>
	 *            type of the outer exception
	 * @return the matcher
	 */
	public static <T extends Throwable> Matcher<T> hasAnyCause(final Class<? extends Throwable> expectedClass, final Matcher<? extends Throwable> matcher) {
		return new ThrowableAnyCauseMatcher<>(instanceOf(expectedClass), matcher);
	}

	/**
	 * Returns a matcher that verifies that the exception has a message for
	 * which the supplied matcher evaluates to true.
	 *
	 * @param matcher
	 *            to apply to the cause of the exception
	 * @param <T>
	 *            type of the outer exception
	 * @return the matcher
	 */
	public static <T extends Throwable> Matcher<T> hasMessage(final Matcher<? extends String> matcher) {
		return new ThrowableMessageMatcher<>(matcher);
	}

	/**
	 * shortcut to:
	 * 
	 * <pre>
	 * {@code
	 * hasMessage(is("some message"))
	 * }
	 * </pre>
	 *
	 * @param expectedMessage
	 *            the expected message of the exception
	 * @param <T>
	 *            type of the outer exception
	 * @return the matcher
	 */
	public static <T extends Throwable> Matcher<T> hasMessage(String expectedMessage) {
		return new ThrowableMessageMatcher<>(is(expectedMessage));
	}

	private ExceptionMatchers() {
		super();
	}
}
