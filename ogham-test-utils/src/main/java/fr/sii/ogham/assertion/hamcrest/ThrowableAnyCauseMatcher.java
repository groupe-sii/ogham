package fr.sii.ogham.assertion.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * A matcher that applies a delegate matcher to the cause of the current
 * Throwable, returning the result of that match.
 *
 * @param <T>
 *            the type of the throwable being matched
 */
public class ThrowableAnyCauseMatcher<T extends Throwable> extends TypeSafeMatcher<T> {
	private final Matcher<? extends Throwable> causeFinder;
	private final Matcher<? extends Throwable> causeMatcher;

	public ThrowableAnyCauseMatcher(Matcher<? extends Throwable> causeFinder, Matcher<? extends Throwable> causeMatcher) {
		this.causeFinder = causeFinder;
		this.causeMatcher = causeMatcher;
	}

	public void describeTo(Description description) {
		description.appendText("exception with cause matching ");
		description.appendDescriptionOf(causeFinder);
		description.appendText(" and matched cause ");
		description.appendDescriptionOf(causeMatcher);
	}

	@Override
	protected boolean matchesSafely(T item) {
		Throwable cause = item;
		while (cause != null) {
			if (causeFinder.matches(cause)) {
				return causeMatcher.matches(cause);
			}
			cause = cause.getCause();
		}
		return false;
	}

	@Override
	protected void describeMismatchSafely(T item, Description description) {
		Throwable cause = item;
		while (cause != null) {
			if (causeFinder.matches(cause)) {
				break;
			}
			cause = cause.getCause();
		}
		if (cause == null) {
			description.appendText("was not found in exception stack");
		} else {
			causeMatcher.describeMismatch(cause, description);
		}
	}

}