package fr.sii.ogham.testing.assertion.hamcrest;

/**
 * Mark a matcher as capable of providing expected value
 * 
 * @author Aurélien Baudet
 *
 * @param <T>
 *            the type of the expected value
 */
public interface ExpectedValueProvider<T> {
	/**
	 * Get the expected value
	 * 
	 * @return the expected value
	 */
	T getExpectedValue();
}
