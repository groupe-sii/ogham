package fr.sii.ogham.core.exception.retry;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import java.util.List;

public class MaximumAttemptsReachedException extends RetryExecutionFailureException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final List<Exception> executionFailures;

	public MaximumAttemptsReachedException(String message, List<Exception> executionFailures) {
		super(message);
		this.executionFailures = executionFailures;
	}

	public List<Exception> getExecutionFailures() {
		return executionFailures;
	}

}