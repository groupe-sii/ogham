package fr.sii.ogham.core.exception.retry;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import fr.sii.ogham.core.retry.RetryExecutor;

/**
 * Retry strategies may fail for many possible reasons:
 * <ul>
 * <li>The action to retry has failed too many times (
 * {@link MaximumAttemptsReachedException})</li>
 * <li>The action to retry has failed with an error that should not be ignored
 * so the action should not be retried anymore</li>
 * <li>The retry strategy itself has failed</li>
 * <li>The thread has been interrupted</li>
 * <li>...</li>
 * </ul>
 * 
 * This is the general exception thrown by {@link RetryExecutor}s and it has
 * subclasses to indicate the detailed reason.
 * 
 * @author Aurélien Baudet
 *
 * @see MaximumAttemptsReachedException
 * @see RetryExecutionFailureException
 * @see RetryExecutionInterruptedException
 * @see ExecutionFailedNotRetriedException
 * @see UnrecoverableException
 * @see ExecutionFailureWrapper
 */
public class RetryException extends Exception {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public RetryException(String message, Throwable cause) {
		super(message, cause);
	}

	public RetryException(String message) {
		super(message);
	}

	public RetryException(Throwable cause) {
		super(cause);
	}
}
