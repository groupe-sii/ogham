package fr.sii.ogham.core.util;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for exceptions
 * 
 * @author Aurélien Baudet
 *
 */
public final class ExceptionUtils {
	private static final Logger LOG = LoggerFactory.getLogger(ExceptionUtils.class);
	private static final Pattern INDENT = Pattern.compile("^", Pattern.MULTILINE);

	/**
	 * Predicate that returns {@code true} if any cause in the exception stack
	 * matches the cause predicate.
	 * 
	 * <p>
	 * If a cause matches the predicate, then it return {@code true} immediately
	 * (skipping other checks).
	 * 
	 * @param error
	 *            the root error to analyze
	 * @param causePredicate
	 *            the predicate to apply to causes recursively
	 * @return true if the cause predicate returns true for at least one cause
	 *         in the exception stack
	 */
	public static boolean hasAnyCause(Throwable error, Predicate<Throwable> causePredicate) {
		Throwable cause = error;
		while (cause != null) {
			if (causePredicate.test(cause)) {
				return true;
			}
			cause = cause.getCause();
		}
		return false;
	}

	/**
	 * Checks whether the error has been raised due to a Java {@link Error}.
	 * {@link Error}s should not be ignored. For example, if there is a
	 * {@link OutOfMemoryError}, retrying may result in consuming more memory
	 * and totally crash the JVM or hang the system.
	 * 
	 * @param error
	 *            the raised error
	 * @return true if the error is fatal JVM error
	 */
	public static boolean fatalJvmError(Throwable error) {
		return error instanceof Error;
	}

	/**
	 * Generate a String based on the exception. Unlike default
	 * {@link Throwable#toString()} method, the {@link Throwable#getCause()} may
	 * also be included in the generated string (depending of logging
	 * configuration). If logger associated to this class is configured to
	 * {@code DEBUG} or {@code TRACE}, then the cause is included.
	 * 
	 * @param e
	 *            the error to convert to a string
	 * @return the error string
	 */
	public static String toString(Throwable e) {
		return toString(e, "");
	}

	/**
	 * Generate a String based on the exception. Unlike default
	 * {@link Throwable#toString()} method, the {@link Throwable#getCause()} may
	 * also be included in the generated string (depending of logging
	 * configuration). If logger associated to this class is configured to
	 * {@code DEBUG} or {@code TRACE}, then the cause is included.
	 * 
	 * @param e
	 *            the error to convert to a string
	 * @param indentation
	 *            the initial indentation
	 * @return the error string
	 */
	public static String toString(Throwable e, String indentation) {
		String str = toThrowableString(e, indentation);
		if (LOG.isDebugEnabled() || LOG.isTraceEnabled()) {
			str += toCauseString(e, indentation + " ");
		}
		return str;
	}

	private static String toThrowableString(Throwable e, String indentation) {
		if (e == null) {
			return "";
		}
		String s = e.getClass().getName();
		String message = e.getLocalizedMessage();
		return indent(indentation, (message != null) ? (s + ": " + message) : s);
	}

	private static String toCauseString(Throwable e, String indentation) {
		if (e == null) {
			return "";
		}
		Throwable cause = e.getCause();
		if (cause == null) {
			return "";
		}
		return "\n" + indent(indentation, toString(cause, indentation));
	}

	private static String indent(String indentation, String str) {
		return INDENT.matcher(str).replaceAll(indentation);
	}

	private ExceptionUtils() {
		super();
	}
}
