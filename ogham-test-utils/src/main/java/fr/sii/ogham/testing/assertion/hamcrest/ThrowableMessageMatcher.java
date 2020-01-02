package fr.sii.ogham.testing.assertion.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * A matcher that applies a delegate matcher to the message of the current
 * Throwable, returning the result of that match.
 *
 * @param <T>
 *            the type of the throwable being matched
 */
public class ThrowableMessageMatcher<T extends Throwable> extends TypeSafeMatcher<T> {
	private final Matcher<? extends String> messageMatcher;

	public ThrowableMessageMatcher(Matcher<? extends String> messageMatcher) {
		super();
		this.messageMatcher = messageMatcher;
	}

	public void describeTo(Description description) {
		description.appendText("exception with message ");
		description.appendDescriptionOf(messageMatcher);
	}

	@Override
	protected boolean matchesSafely(T item) {
		return messageMatcher.matches(item.getMessage());
	}

	@Override
	protected void describeMismatchSafely(T item, Description description) {
		description.appendText("message ");
		messageMatcher.describeMismatch(item.getMessage(), description);
	}
	
	@Override
	public String toString() {
		return "hasMessage('"+messageMatcher+"')";
	}
}