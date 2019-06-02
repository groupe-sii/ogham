package fr.sii.ogham.core.exception.retry;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class ExecutionFailedNotRetriedException extends RetryExecutionFailureException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;
	
	public ExecutionFailedNotRetriedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExecutionFailedNotRetriedException(String message) {
		super(message);
	}

	public ExecutionFailedNotRetriedException(Throwable cause) {
		super(cause);
	}

}
