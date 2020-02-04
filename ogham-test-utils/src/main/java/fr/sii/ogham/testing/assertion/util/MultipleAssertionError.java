package fr.sii.ogham.testing.assertion.util;

import static java.util.regex.Pattern.MULTILINE;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.junit.ComparisonFailure;

public class MultipleAssertionError extends AssertionError {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String FAILURE_SEPARATOR = "\n\t______________________________\n";
	private static final Pattern INDENT = Pattern.compile("^", MULTILINE);

	private final List<Throwable> failures;

	public MultipleAssertionError(List<Throwable> failures) {
		super(generateMessage(failures));
		this.failures = failures;
	}

	/**
	 * Get the whole list of failures/failed assertions.
	 * 
	 * @return list of failures/failed assertions
	 */
	public List<Throwable> getFailures() {
		return failures;
	}

	/**
	 * Eclipse can only handle {@link ComparisonFailure} instance (not
	 * sub-classes...)
	 * 
	 * @return the same exception but converted to {@link ComparisonFailure}
	 *         (list of failures is lost)
	 */
	public ComparisonFailure toComparisonFailure() {
		return new ComparisonFailure(generateMessage(failures), generateExpected(failures), generateActual(failures));
	}

	private static String generateMessage(List<Throwable> failures) {
		StringJoiner joiner = new StringJoiner(FAILURE_SEPARATOR, "Multiple assertions/failures (" + failures.size() + "):\n", FAILURE_SEPARATOR);
		int idx = 1;
		for (Throwable f : failures) {
			joiner.add(indent("Failure " + idx + ":\n" + getMessage(f)));
			idx++;
		}
		return joiner.toString();
	}

	private static String generateExpected(List<Throwable> failures) {
		return generateComparison(failures, "Expected", ComparisonFailure::getExpected);
	}

	private static String generateActual(List<Throwable> failures) {
		return generateComparison(failures, "Actual", ComparisonFailure::getActual);
	}

	private static String generateComparison(List<Throwable> failures, String name, Function<ComparisonFailure, String> getter) {
		StringJoiner joiner = new StringJoiner(FAILURE_SEPARATOR, name + " (" + failures.size() + "):\n", FAILURE_SEPARATOR);
		int idx = 1;
		for (Throwable f : failures) {
			String prefix = "Failure " + idx + ": " + getMessage(f) + "\n\n";
			if (f instanceof ComparisonFailure) {
				joiner.add(indent(prefix + getter.apply((ComparisonFailure) f)));
			} else {
				joiner.add(indent(prefix + "</!\\ no comparison available>"));
			}
			idx++;
		}
		return joiner.toString();
	}

	private static String indent(String message) {
		return INDENT.matcher(message).replaceAll("    ");
	}

	private static String getMessage(Throwable failure) {
		String message = failure.getMessage();
		if (message == null) {
			message = "";
		}
		return failure.getClass().getName() + ": " + message;
	}

}
