package fr.sii.ogham.core.exception.retry;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

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
