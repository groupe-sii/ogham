package fr.sii.ogham.core.exception.retry;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import java.util.List;

import fr.sii.ogham.core.exception.MultipleCauseExceptionWrapper;

/**
 * Specialized exception that is thrown when the action has thrown an error that
 * should not be ignored therefore the action should not be retried anymore.
 * 
 * This exception provides the list of exceptions raised by the previous
 * attempts.
 * 
 * @author Aurélien Baudet
 *
 */
public class UnrecoverableException extends RetryExecutionFailureException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final List<Exception> executionFailures;

	public UnrecoverableException(String message, List<Exception> executionFailures) {
		super(message, new MultipleCauseExceptionWrapper(executionFailures));
		this.executionFailures = executionFailures;
	}

	public List<Exception> getExecutionFailures() {
		return executionFailures;
	}

}
