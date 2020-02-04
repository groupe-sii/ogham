package fr.sii.ogham.testing.assertion.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Decorates a Hamcrest matcher in order to provide a different reason.
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the actual value
 */
public class CustomReason<T> extends BaseMatcher<T> implements DecoratorMatcher<T> {
	private final String reason;
	private final Matcher<T> matcher;

	public CustomReason(String reason, Matcher<T> matcher) {
		super();
		this.reason = reason;
		this.matcher = matcher;
	}

	@Override
	public boolean matches(Object item) {
		return matcher.matches(item);
	}

	@Override
	public void describeTo(Description description) {
		matcher.describeTo(description);
	}

	public String getReason() {
		return reason;
	}

	public Matcher<T> getDecoree() {
		return matcher;
	}

}