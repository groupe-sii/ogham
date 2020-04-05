package fr.sii.ogham.core.retry;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.async.Awaiter;
import fr.sii.ogham.core.exception.async.WaitException;
import fr.sii.ogham.core.exception.retry.ExecutionFailedNotRetriedException;
import fr.sii.ogham.core.exception.retry.MaximumAttemptsReachedException;
import fr.sii.ogham.core.exception.retry.RetryException;
import fr.sii.ogham.core.exception.retry.RetryExecutionInterruptedException;
import fr.sii.ogham.core.exception.retry.UnrecoverableException;

/**
 * A simple implementation that tries to execute the action, if it fails (any
 * exception), it waits using {@link Thread#sleep(long)}. Once the sleep is
 * expired, the action is executed again.
 * 
 * This process is executed until the retry strategy tells that the retries
 * should stop. Once stopped, it means that no execution of the action succeeded
 * so the last exception is thrown.
 * 
 * @author Aurélien Baudet
 *
 */
public class SimpleRetryExecutor implements RetryExecutor {
	private static final Logger LOG = LoggerFactory.getLogger(SimpleRetryExecutor.class);

	/**
	 * Use a provider in order to use a fresh {@link RetryStrategy} strategy
	 * each time the execute method is called. This is mandatory to be able to
	 * use the {@link RetryExecutor} in a multi-threaded application. This
	 * avoids sharing same instance between several {@link #execute(Callable)}
	 * calls.
	 */
	private final RetryStrategyProvider retryProvider;

	/**
	 * Implementation that waits for some time between retries
	 */
	private final Awaiter awaiter;

	/**
	 * Use to check if the exception is recoverable (means that a retry can be
	 * attempted) or not (should fail immediately).
	 */
	private final Predicate<Exception> recoverable;

	/**
	 * Initializes with a provider in order to use a fresh {@link RetryStrategy}
	 * strategy each time the execute method is called. This is mandatory to be
	 * able to use the {@link RetryExecutor} in a multi-threaded application.
	 * This avoids sharing same instance between several
	 * {@link #execute(Callable)} calls.
	 * Every exception is considered as recoverable (means that retry is attempted).
	 * 
	 * @param retryProvider
	 *            the provider that will provide the retry strategy
	 * @param awaiter
	 *            the waiter that waits some time between retries
	 */
	public SimpleRetryExecutor(RetryStrategyProvider retryProvider, Awaiter awaiter) {
		this(retryProvider, awaiter, e -> true);
	}

	/**
	 * Initializes with a provider in order to use a fresh {@link RetryStrategy}
	 * strategy each time the execute method is called. This is mandatory to be
	 * able to use the {@link RetryExecutor} in a multi-threaded application.
	 * This avoids sharing same instance between several
	 * {@link #execute(Callable)} calls.
	 * 
	 * @param retryProvider
	 *            the provider that will provide the retry strategy
	 * @param awaiter
	 *            the waiter that waits some time between retries
	 * @param recoverable
	 *            check if the exception is recoverable (means that retry can be
	 *            attempted) or unrecoverable (means that it should fail
	 *            immediately)
	 */
	public SimpleRetryExecutor(RetryStrategyProvider retryProvider, Awaiter awaiter, Predicate<Exception> recoverable) {
		super();
		this.retryProvider = retryProvider;
		this.awaiter = awaiter;
		this.recoverable = recoverable;
	}

	@Override
	public <V> V execute(Callable<V> actionToRetry) throws RetryException {
		// new instance for each execution
		RetryStrategy retry = retryProvider.provide();
		if (retry == null) {
			return executeWithoutRetry(actionToRetry);
		}
		return executeWithRetry(actionToRetry, retry);
	}

	private <V> V executeWithRetry(Callable<V> actionToRetry, RetryStrategy retry) throws RetryExecutionInterruptedException, MaximumAttemptsReachedException, UnrecoverableException {
		List<Exception> failures = new ArrayList<>();
		do {
			Instant executionStartTime = Instant.now();
			try {
				return actionToRetry.call();
			} catch (Exception e) {
				handleFailure(actionToRetry, failures, e);
				pause(executionStartTime, Instant.now(), actionToRetry, retry, e);
			}
		} while (!retry.terminated());
		// action couldn't be executed
		throw new MaximumAttemptsReachedException("Maximum attempts to execute action " + getActionName(actionToRetry) + " is reached", failures);
	}

	private <V> void handleFailure(Callable<V> actionToRetry, List<Exception> failures, Exception e) throws UnrecoverableException {
		failures.add(e);
		if (!recoverable.test(e)) {
			throw new UnrecoverableException("Unrecoverable exception thrown while executing " + getActionName(actionToRetry), failures);
		}
	}

	private static <V> V executeWithoutRetry(Callable<V> actionToRetry) throws ExecutionFailedNotRetriedException {
		try {
			return actionToRetry.call();
		} catch (Exception e) {
			throw new ExecutionFailedNotRetriedException("Failed to execute action " + getActionName(actionToRetry) + " and no retry strategy configured", e);
		}
	}

	private <V> void pause(Instant executionStartTime, Instant executionFailureTime, Callable<V> actionToRetry, RetryStrategy retry, Exception e) throws RetryExecutionInterruptedException {
		Instant nextDate = retry.nextDate(executionStartTime, executionFailureTime);
		LOG.debug("{} failed ({}: {}). Retrying at {}...", getActionName(actionToRetry), e.getClass(), e.getMessage(), nextDate);
		LOG.trace("{}", e.getMessage(), e);
		pauseUntil(nextDate);
	}

	private void pauseUntil(Instant nextDate) throws RetryExecutionInterruptedException {
		try {
			awaiter.waitUntil(nextDate);
		} catch (WaitException e) {
			throw new RetryExecutionInterruptedException(e);
		}
	}

	private static <V> String getActionName(Callable<V> actionToRetry) {
		if (actionToRetry instanceof NamedCallable) {
			return ((NamedCallable<?>) actionToRetry).getName();
		}
		return "unnamed";
	}

}
