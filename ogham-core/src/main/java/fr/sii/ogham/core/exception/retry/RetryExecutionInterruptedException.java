package fr.sii.ogham.core.exception.retry;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class RetryExecutionInterruptedException extends RetryExecutionFailureException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;
	
	public RetryExecutionInterruptedException(String message, Throwable cause) {
		super(message, cause);
	}

	public RetryExecutionInterruptedException(String message) {
		super(message);
	}

	public RetryExecutionInterruptedException(Throwable cause) {
		super(cause);
	}
	
	
}
