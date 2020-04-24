package fr.sii.ogham.core.exception.retry;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import java.util.List;

import fr.sii.ogham.core.exception.MultipleCauseExceptionWrapper;

/**
 * Specialized exception that indicates that the action couldn't be executed due
 * to too many attempts.
 * 
 * This exception provides the list of original exceptions thrown while trying
 * to execute the action.
 * 
 * @author Aurélien Baudet
 *
 */
public class MaximumAttemptsReachedException extends RetryExecutionFailureException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final List<Exception> executionFailures;

	public MaximumAttemptsReachedException(String message, List<Exception> executionFailures) {
		super(message, new MultipleCauseExceptionWrapper(executionFailures));
		this.executionFailures = executionFailures;
	}

	public List<Exception> getExecutionFailures() {
		return executionFailures;
	}

}
