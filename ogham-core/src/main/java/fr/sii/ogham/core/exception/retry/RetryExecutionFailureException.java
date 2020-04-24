package fr.sii.ogham.core.exception.retry;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

/**
 * General exception that indicates that the action to retry has failed. It has
 * several subclasses to indicate the reason why it failed:
 * 
 * <ul>
 * <li>{@link ExecutionFailedNotRetriedException}: the action has failed but no
 * retry was requested</li>
 * <li>{@link ExecutionFailureWrapper}: wraps another exception and adds
 * additional information about the action execution that has failed</li>
 * <li>{@link MaximumAttemptsReachedException}: the action has been retried too
 * many times</li>
 * <li>{@link RetryExecutionInterruptedException}: the thread that runs the
 * action has been interrupted</li>
 * <li>{@link UnrecoverableException}: the action has thrown an exception that
 * should not be ignored so no more retry should be attempted</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class RetryExecutionFailureException extends RetryException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public RetryExecutionFailureException(String message, Throwable cause) {
		super(message, cause);
	}

	public RetryExecutionFailureException(String message) {
		super(message);
	}

	public RetryExecutionFailureException(Throwable cause) {
		super(cause);
	}

}
