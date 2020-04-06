package fr.sii.ogham.core.exception.retry;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import java.time.Instant;

/**
 * Simple wrapper that carries additional information about the execution
 * context.
 * 
 * @author Aurélien Baudet
 *
 */
public class ExecutionFailureWrapper extends RetryExecutionFailureException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	private final String actionName;
	private final Instant executionStart;
	private final Instant executionFailure;

	public ExecutionFailureWrapper(String action, Instant executionStart, Instant executionFailure, Throwable cause) {
		super(cause);
		this.actionName = action;
		this.executionStart = executionStart;
		this.executionFailure = executionFailure;
	}

	public String getActionName() {
		return actionName;
	}

	public Instant getExecutionStart() {
		return executionStart;
	}

	public Instant getExecutionFailure() {
		return executionFailure;
	}

}
