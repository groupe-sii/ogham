package fr.sii.ogham.testing.assertion.util;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.StringDescription;
import org.junit.ComparisonFailure;

import fr.sii.ogham.testing.assertion.context.Context;
import fr.sii.ogham.testing.assertion.hamcrest.ComparisonAwareMatcher;
import fr.sii.ogham.testing.assertion.hamcrest.CustomDescriptionProvider;
import fr.sii.ogham.testing.assertion.hamcrest.CustomReason;
import fr.sii.ogham.testing.assertion.hamcrest.DecoratorMatcher;
import fr.sii.ogham.testing.assertion.hamcrest.ExpectedValueProvider;
import fr.sii.ogham.testing.assertion.hamcrest.OverrideDescription;

/**
 * Utility class for Ogham assertions.
 * 
 * @author Aurélien Baudet
 *
 */
public final class AssertionHelper {

	/**
	 * Copy of {@link MatcherAssert#assertThat(Object, Matcher)} with the
	 * following additions:
	 * <ul>
	 * <li>If the matcher can provide expected value, a
	 * {@link ComparisonFailure} exception is thrown instead of
	 * {@link AssertionError} in order to display differences between expected
	 * string and actual string in the IDE.</li>
	 * <li>If the matcher is a {@link CustomReason} matcher and no reason is
	 * provided, the reason of the matcher is used to provide more information
	 * about the context (which message has failed for example)</li>
	 * </ul>
	 * 
	 * @param actual
	 *            the actual value
	 * @param matcher
	 *            the matcher to apply
	 * @param <T>
	 *            the type used for the matcher
	 */
	public static <T> void assertThat(T actual, Matcher<? super T> matcher) {
		assertThat("", actual, matcher);
	}

	/**
	 * Copy of {@link MatcherAssert#assertThat(String, Object, Matcher)} with
	 * the following additions:
	 * <ul>
	 * <li>If the matcher can provide expected value, a
	 * {@link ComparisonFailure} exception is thrown instead of
	 * {@link AssertionError} in order to display differences between expected
	 * string and actual string in the IDE.</li>
	 * <li>If the matcher is a {@link CustomReason} matcher and no reason is
	 * provided, the reason of the matcher is used to provide more information
	 * about the context (which message has failed for example)</li>
	 * </ul>
	 * 
	 * @param reason
	 *            the reason
	 * @param actual
	 *            the actual value
	 * @param matcher
	 *            the matcher to apply
	 * @param <T>
	 *            the type used for the matcher
	 */
	public static <T> void assertThat(String reason, T actual, Matcher<? super T> matcher) {
		if (!matcher.matches(actual)) {
			Description description = getDescription(reason, actual, matcher);

			if (hasExpectedValue(matcher)) {
				ExpectedValueProvider<T> comparable = getComparable(matcher);
				throw new ComparisonFailure(description.toString(), String.valueOf(comparable == null ? null : comparable.getExpectedValue()), String.valueOf(actual));
			} else {
				throw new AssertionError(description.toString());
			}
		}
	}

	/**
	 * Ogham helper for keeping context information when using fluent
	 * assertions.
	 * 
	 * @param reasonTemplate
	 *            the template for the reason
	 * @param context
	 *            the evaluation context
	 * @param delegate
	 *            the matcher to decorate
	 * @param <T>
	 *            the type used for the matcher
	 * @return the matcher
	 */
	public static <T> Matcher<T> usingContext(String reasonTemplate, Context context, Matcher<T> delegate) {
		return new CustomReason<>(context.evaluate(reasonTemplate), delegate);
	}

	/**
	 * Ogham helper for overriding default description.
	 * 
	 * @param description
	 *            the description to display
	 * @param delegate
	 *            the matcher to decorate
	 * @param <T>
	 *            the type used for the matcher
	 * @return the matcher
	 */
	public static <T> Matcher<T> overrideDescription(String description, Matcher<T> delegate) {
		return new OverrideDescription<>(description, delegate);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> boolean hasExpectedValue(Matcher<? super T> matcher) {
		if (matcher instanceof ExpectedValueProvider) {
			return true;
		}
		if (matcher instanceof DecoratorMatcher) {
			return hasExpectedValue(((DecoratorMatcher<T>) matcher).getDecoree());
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static <T> ExpectedValueProvider<T> getComparable(Matcher<? super T> matcher) {
		if (matcher instanceof ExpectedValueProvider) {
			return (ExpectedValueProvider<T>) matcher;
		}
		if (matcher instanceof DecoratorMatcher) {
			return getComparable(((DecoratorMatcher<T>) matcher).getDecoree());
		}
		return null;
	}

	private static <T> Description getDescription(String reason, T actual, Matcher<? super T> matcher) {
		String additionalText = null;
		ComparisonAwareMatcher cam = getComparisonAwareMatcher(matcher);
		if (cam != null) {
			additionalText = cam.comparisonMessage();
		}
		return getDescription(reason, actual, matcher, additionalText);
	}

	@SuppressWarnings("unchecked")
	private static <T> ComparisonAwareMatcher getComparisonAwareMatcher(Matcher<? super T> matcher) {
		if (matcher instanceof ComparisonAwareMatcher) {
			return (ComparisonAwareMatcher) matcher;
		}
		if (matcher instanceof DecoratorMatcher) {
			return getComparisonAwareMatcher(((DecoratorMatcher<T>) matcher).getDecoree());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T> Description getDescription(String reason, T actual, Matcher<? super T> matcher, String additionalText) {
		if (matcher instanceof CustomDescriptionProvider) {
			return ((CustomDescriptionProvider<T>) matcher).describe(reason, actual, additionalText);
		}
		// @formatter:off
		Description description = new StringDescription();
		description.appendText(getReason(reason, matcher))
					.appendText(additionalText==null ? "" : ("\n"+additionalText))
					.appendText("\nExpected: ")
					.appendDescriptionOf(matcher)
					.appendText("\n     but: ");
		matcher.describeMismatch(actual, description);
		// @formatter:on
		return description;
	}

	private static <T> String getReason(String reason, Matcher<? super T> matcher) {
		if (reason != null && !reason.isEmpty()) {
			return reason;
		}
		if (matcher instanceof CustomReason) {
			return ((CustomReason<?>) matcher).getReason();
		}
		return "";
	}

	private AssertionHelper() {
		super();
	}

}
