package fr.sii.ogham.core.retry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.retry.ExecutionFailedNotRetriedException;
import fr.sii.ogham.core.exception.retry.MaximumAttemptsReachedException;
import fr.sii.ogham.core.exception.retry.RetryException;
import fr.sii.ogham.core.exception.retry.RetryExecutionInterruptedException;

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
	 * Initializes with a provider in order to use a fresh {@link RetryStrategy}
	 * strategy each time the execute method is called. This is mandatory to be
	 * able to use the {@link RetryExecutor} in a multi-threaded application.
	 * This avoids sharing same instance between several
	 * {@link #execute(Callable)} calls.
	 * 
	 * @param retryProvider
	 *            the provider that will provide the retry strategy
	 */
	public SimpleRetryExecutor(RetryStrategyProvider retryProvider) {
		super();
		this.retryProvider = retryProvider;
	}

	@Override
	public <V> V execute(Callable<V> actionToRetry) throws RetryException {
		// new instance for each execution
		RetryStrategy retry = retryProvider.provide();
		if (retry == null) {
			try {
				return actionToRetry.call();
			} catch (Exception e) {
				throw new ExecutionFailedNotRetriedException("Failed to execute action "+getActionName(actionToRetry)+" and no retry strategy configured", e);
			}
		}
		List<Exception> failures = new ArrayList<>();
		do {
			try {
				return actionToRetry.call();
			} catch (Exception e) {
				failures.add(e);
				long delay = Math.max(0, retry.nextDate() - System.currentTimeMillis());
				LOG.debug("{} failed. Cause: {}. Retrying in {}ms...", e.getMessage(), getActionName(actionToRetry), delay);
				LOG.trace("{}", e.getMessage(), e);
				pause(delay);
			}
		} while (!retry.terminated());
		// action couldn't be executed
		throw new MaximumAttemptsReachedException("Maximum attempts to execute action "+getActionName(actionToRetry)+" is reached", failures);
	}

	private static void pause(long delay) throws RetryExecutionInterruptedException {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RetryExecutionInterruptedException(e);
		}
	}

	private static <V> String getActionName(Callable<V> actionToRetry) {
		if(actionToRetry instanceof NamedCallable) {
			return ((NamedCallable<?>) actionToRetry).getName();
		}
		return "unnamed";
	}

}
