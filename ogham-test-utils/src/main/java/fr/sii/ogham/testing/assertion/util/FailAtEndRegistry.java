package fr.sii.ogham.testing.assertion.util;

import java.util.ArrayList;
import java.util.List;

import org.opentest4j.AssertionFailedError;

/**
 * The aim of this registry is to report all errors at once.
 * 
 * It registers all functions but doesn't execute them.
 * 
 * When {@link #execute()} method is called, then all functions are executed.
 * All failures/failed assertions are collected and reported using a
 * {@link MultipleAssertionError}.
 * 
 * <p>
 * A {@link AssertionFailedError} exception is thrown. Therefore Eclipse can handle
 * this exception and provide a comparison view with all differences.
 * 
 * @author Aurélien Baudet
 *
 */
public class FailAtEndRegistry implements AssertionRegistry {
	private final List<Executable<?>> assertions;
	private final boolean convertToAssertionFailedError;

	/**
	 * Initializes an empty registry.
	 * 
	 * If the system property
	 * {@code "ogham.testing.assertions.fail-at-end.throw-comparison-failure"}
	 * is set to true (or not set at all), a {@link AssertionFailedError} exception
	 * is thrown. Using a {@link AssertionFailedError} lets Eclipse handle it
	 * specifically and provides a comparison view (Eclipse doesn't handle
	 * sub-classes of {@link AssertionFailedError}).
	 * 
	 * To throw {@link MultipleAssertionError} instead of
	 * {@link AssertionFailedError}, you can set the
	 * {@code "ogham.testing.assertions.fail-at-end.throw-comparison-failure"}
	 * system property to {@code "false"}. This can be useful if you need to
	 * manually handle every failure/failed assertions.
	 */
	public FailAtEndRegistry() {
		this(Boolean.valueOf(System.getProperty("ogham.testing.assertions.fail-at-end.throw-comparison-failure", "true")));
	}

	/**
	 * Initializes an empty registry.
	 * 
	 * If {@code convertToAssertionFailedError} parameter is set to true, a
	 * {@link AssertionFailedError} exception is thrown. Using a
	 * {@link AssertionFailedError} lets Eclipse handle it specifically and
	 * provides a comparison view (Eclipse doesn't handle sub-classes of
	 * {@link AssertionFailedError}).
	 * 
	 * @param convertToAssertionFailedError
	 *            true to generate a {@link AssertionFailedError} to let Eclipse
	 *            display a comparison view.
	 */
	public FailAtEndRegistry(boolean convertToAssertionFailedError) {
		super();
		assertions = new ArrayList<>();
		this.convertToAssertionFailedError = convertToAssertionFailedError;
	}

	public <E extends Exception> void register(Executable<E> executable) throws E {
		assertions.add(executable);
	}

	@SuppressWarnings("squid:S2221") // we can't know which exception may be
										// thrown so we have to catch all
										// exceptions
	public void execute() {
		List<Throwable> failures = new ArrayList<>();
		for (Executable<?> executable : assertions) {
			try {
				executable.run();
			} catch (Exception | AssertionError e) {
				failures.add(e);
			}
		}
		if (!failures.isEmpty()) {
			throwFailures(failures);
		}
	}

	private void throwFailures(List<Throwable> failures) {
		MultipleAssertionError e = new MultipleAssertionError(failures);
		if (convertToAssertionFailedError) {
			throw e.toAssertionFailedError();
		}
		throw e;
	}
}
