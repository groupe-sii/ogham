package fr.sii.ogham.core.builder.retry;

import java.util.function.Predicate;

import fr.sii.ogham.core.async.Awaiter;
import fr.sii.ogham.core.async.ThreadSleepAwaiter;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.context.BuildContext;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.fluent.AbstractParent;
import fr.sii.ogham.core.retry.FixedDelayRetry;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.core.retry.RetryStrategy;
import fr.sii.ogham.core.retry.RetryStrategyProvider;
import fr.sii.ogham.core.retry.SimpleRetryExecutor;

/**
 * Configures retry handling.
 * 
 * For now, only a {@link FixedDelayRetry} is handled. The
 * {@link FixedDelayRetry} needs a delay between two tries and a maximum
 * attempts. In the future, we could handle different strategies if needed like
 * retrying with an exponential delay for example.
 * 
 * <p>
 * The {@link RetryExecutor} instance may be {@code null} if nothing has been
 * configured.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class RetryBuilder<P> extends AbstractParent<P> implements Builder<RetryExecutor> {
	private final BuildContext buildContext;
	private FixedDelayBuilder<RetryBuilder<P>> fixedDelay;
	private ExponentialDelayBuilder<RetryBuilder<P>> exponentialDelay;
	private PerExecutionDelayBuilder<RetryBuilder<P>> perExecutionDelay;
	private FixedIntervalBuilder<RetryBuilder<P>> fixedInterval;
	private Awaiter awaiter;
	private RetryExecutor executor;
	private RetryExecutorFactory executorFactory;
	private Predicate<Throwable> retryable;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param buildContext
	 *            for registering instances and property evaluation
	 */
	public RetryBuilder(P parent, BuildContext buildContext) {
		super(parent);
		this.buildContext = buildContext;
	}

	/**
	 * Retry several times with a fixed delay between each try until the maximum
	 * attempts is reached.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .fixedDelay()
	 *    .delay(500)
	 *    .maxRetries(5)
	 * </pre>
	 * 
	 * Means that a retry will be attempted every 500ms until 5 attempts are
	 * reached (inclusive). For example, you want to connect to an external
	 * system at t1=0 and the connection timeout (100ms) is triggered at
	 * t2=100ms. Using this retry will provide the following behavior:
	 * 
	 * <ul>
	 * <li>0: connect</li>
	 * <li>100: timeout</li>
	 * <li>600: connect</li>
	 * <li>700: timeout</li>
	 * <li>1200: connect</li>
	 * <li>1300: timeout</li>
	 * <li>1800: connect</li>
	 * <li>1900: timeout</li>
	 * <li>2400: connect</li>
	 * <li>2500: timeout</li>
	 * <li>3000: connect</li>
	 * <li>3100: timeout</li>
	 * <li>fail</li>
	 * </ul>
	 * 
	 * @return the builder to configure retry delay and maximum attempts
	 */
	public FixedDelayBuilder<RetryBuilder<P>> fixedDelay() {
		if (fixedDelay == null) {
			fixedDelay = new FixedDelayBuilder<>(this, buildContext);
		}
		return fixedDelay;
	}

	/**
	 * Retry several times with a delay that is doubled between each try until
	 * the maximum attempts is reached.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .exponentialDelay()
	 *    .initialDelay(500)
	 *    .maxRetries(5)
	 * </pre>
	 * 
	 * Means that a retry will be attempted every 500ms until 5 attempts are
	 * reached (inclusive). For example, you want to connect to an external
	 * system at t1=0 and the connection timeout (100ms) is triggered at
	 * t2=100ms. Using this retry will provide the following behavior:
	 * 
	 * <ul>
	 * <li>0: connect</li>
	 * <li>100: timeout</li>
	 * <li>600: connect</li>
	 * <li>700: timeout</li>
	 * <li>1700: connect</li>
	 * <li>1800: timeout</li>
	 * <li>3800: connect</li>
	 * <li>3900: timeout</li>
	 * <li>7900: connect</li>
	 * <li>8000: timeout</li>
	 * <li>16000: connect</li>
	 * <li>16100: timeout</li>
	 * <li>fail</li>
	 * </ul>
	 * 
	 * @return the builder to configure initial delay and maximum attempts
	 */
	public ExponentialDelayBuilder<RetryBuilder<P>> exponentialDelay() {
		if (exponentialDelay == null) {
			exponentialDelay = new ExponentialDelayBuilder<>(this, buildContext);
		}
		return exponentialDelay;
	}

	/**
	 * Retry several times with a fixed delay to wait after the last execution
	 * failure until the maximum attempts is reached. A specific delay is used
	 * for each execution. If there are more attempts than the configured
	 * delays, the last delays is used for remaining attempts.
	 * 
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .perExecutionDelay()
	 *    .delays(500, 750, 1800)
	 *    .maxRetries(5)
	 * </pre>
	 * 
	 * Means that a retry will be attempted with specified delays until 5
	 * attempts are reached (inclusive). For example, you want to connect to an
	 * external system at t1=0 and the connection timeout (100ms) is triggered
	 * at t2=100ms. Using this retry will provide the following behavior:
	 * 
	 * <ul>
	 * <li>0: connect</li>
	 * <li>100: timeout</li>
	 * <li>600: connect</li>
	 * <li>700: timeout</li>
	 * <li>1450: connect</li>
	 * <li>1550: timeout</li>
	 * <li>3350: connect</li>
	 * <li>3450: timeout</li>
	 * <li>5250: connect</li>
	 * <li>5350: timeout</li>
	 * <li>7150: connect</li>
	 * <li>7250: timeout</li>
	 * <li>fail</li>
	 * </ul>
	 * 
	 * @return the builder to configure retry delay and maximum attempts
	 */
	public PerExecutionDelayBuilder<RetryBuilder<P>> perExecutionDelay() {
		if (perExecutionDelay == null) {
			perExecutionDelay = new PerExecutionDelayBuilder<>(this, buildContext);
		}
		return perExecutionDelay;
	}

	/**
	 * Retry several times with a fixed delay between each try (no matter how
	 * long the execution of the action lasts) until the maximum attempts is
	 * reached. The next execution date is based on the execution start date of
	 * the first execution.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * .fixedInterval()
	 *    .interval(500)
	 *    .maxRetries(5)
	 * </pre>
	 * 
	 * Means that a retry will be attempted every 500ms until 5 attempts are
	 * reached (inclusive). For example, you want to connect to an external
	 * system at t1=0 and the connection timeout (100ms) is triggered at
	 * t2=100ms. Using this retry will provide the following behavior:
	 * 
	 * 
	 * <ul>
	 * <li>0: connect</li>
	 * <li>100: timeout</li>
	 * <li>500: connect</li>
	 * <li>600: timeout</li>
	 * <li>1000: connect</li>
	 * <li>1100: timeout</li>
	 * <li>1500: connect</li>
	 * <li>1600: timeout</li>
	 * <li>2000: connect</li>
	 * <li>2100: timeout</li>
	 * <li>2500: connect</li>
	 * <li>2600: timeout</li>
	 * <li>fail</li>
	 * </ul>
	 * 
	 * 
	 * <strong>NOTE:</strong> The provided date doesn't take the duration of the
	 * execution in account. If an execution takes 1s to execute while retry
	 * delay is set to 500ms, there may have several executions in parallel.
	 * However, this totally depends on the {@link RetryExecutor}
	 * implementation. For example {@link SimpleRetryExecutor} won't run several
	 * executions in parallel. In this case, it will execute the action as soon
	 * as the previous one has failed therefore the delay may not be complied.
	 * 
	 * @return the builder to configure retry delay and maximum attempts
	 */
	public FixedIntervalBuilder<RetryBuilder<P>> fixedInterval() {
		if (fixedInterval == null) {
			fixedInterval = new FixedIntervalBuilder<>(this, buildContext);
		}
		return fixedInterval;
	}

	/**
	 * Change implementation used to wait for some delay between retries.
	 * 
	 * By default, {@link ThreadSleepAwaiter} is used (internally uses
	 * {@link Thread#sleep(long)} to wait for some point in time.
	 * 
	 * @param impl
	 *            the custom implementation
	 * @return this instance for fluent chaining
	 */
	public RetryBuilder<P> awaiter(Awaiter impl) {
		this.awaiter = impl;
		return this;
	}

	/**
	 * Use custom executor instead of default one ({@link SimpleRetryExecutor}).
	 * 
	 * <strong>NOTE:</strong> Using custom executor doesn't take retry
	 * strategies into account. If you want to benefit from
	 * {@link RetryStrategy}s, use {@link #executor(RetryExecutorFactory)}
	 * instead.
	 * 
	 * <p>
	 * If {@link #executor(RetryExecutorFactory)} is also called, only the
	 * executor defined by this method is used and the factory is not used.
	 * 
	 * <p>
	 * If this method is called several times, only the last value is used.
	 * 
	 * <p>
	 * If you call with {@code null}, it removes any previous custom executor.
	 * 
	 * @param executor
	 *            the executor to use
	 * @return this builder for fluent chaining
	 */
	public RetryBuilder<P> executor(RetryExecutor executor) {
		this.executor = executor;
		return this;
	}

	/**
	 * Use a factory to create an instance of {@link RetryExecutor} while
	 * benefiting from configured {@link RetryStrategy}.
	 * 
	 * <p>
	 * The factory will receive the ready to use {@link RetryStrategyProvider}
	 * and the built {@link Awaiter}.
	 * 
	 * <p>
	 * If {@link #executor(RetryExecutor)} is also configured, the factory is
	 * not used.
	 * 
	 * <p>
	 * If this method is called several times, only the last factory is used.
	 * 
	 * <p>
	 * If you call with {@code null}, it removes any previous configured
	 * factory.
	 * 
	 * @param factory
	 *            the factory used to create the {@link RetryExecutor} instance
	 * @return this instance for fluent chaining
	 */
	public RetryBuilder<P> executor(RetryExecutorFactory factory) {
		this.executorFactory = factory;
		return this;
	}

	/**
	 * A predicate that checks if the raised error should allow another retry or
	 * not. This is useful when an error is raised and the error is severe so it
	 * should stop immediately.
	 * 
	 * <p>
	 * The predicate returns {@code true} if the exception is not fatal and a
	 * retry may be attempted. It returns {@code false} if the exception is
	 * fatal and no more attempt should be executed and retry must stop
	 * immediately.
	 * 
	 * <p>
	 * If {@code null} is passed to this method, it removes any previously
	 * defined predicate. Therefore the default predicate is used: all
	 * {@link Exception}s are considered retryable but not {@link Error}s.
	 * 
	 * @param retryable
	 *            the predicate that returns true if the exception is not fatal
	 *            and a retry can be attempted
	 * @return this instance for fluent chaining
	 */
	public RetryBuilder<P> retryable(Predicate<Throwable> retryable) {
		this.retryable = retryable;
		return this;
	}

	/**
	 * A condition that checks if the raised error should allow another retry or
	 * not. This is useful when an error is raised and the error is severe so it
	 * should stop immediately.
	 * 
	 * <p>
	 * The condition returns {@code true} if the exception is not fatal and a
	 * retry may be attempted. It returns {@code false} if the exception is
	 * fatal and no more attempt should be executed and retry must stop
	 * immediately.
	 * 
	 * <p>
	 * If {@code null} is passed to this method, it removes any previously
	 * defined condition. Therefore the default condition is used: all
	 * {@link Exception}s are considered retryable but not {@link Error}s.
	 * 
	 * <p>
	 * This method internally calls {@link #retryable(Predicate)}.
	 * 
	 * @param retryable
	 *            the condition that returns true if the exception is not fatal
	 *            and a retry can be attempted
	 * @return this instance for fluent chaining
	 */
	@SuppressWarnings("squid:S1905")
	public RetryBuilder<P> retryable(Condition<Throwable> retryable) {
		return retryable(retryable == null ? (Predicate<Throwable>) null : retryable::accept);
	}

	@Override
	public RetryExecutor build() {
		if (executor != null) {
			return executor;
		}
		Builder<RetryStrategy> retryStrategy = buildRetryStrategy();
		if (retryStrategy == null) {
			return null;
		}
		BuilderToRetryStrategyProviderBridge retryProvider = new BuilderToRetryStrategyProviderBridge(retryStrategy);
		Awaiter builtAwaiter = buildAwaiter();
		if (executorFactory != null) {
			return executorFactory.create(retryProvider, builtAwaiter);
		}
		return buildContext.register(new SimpleRetryExecutor(retryProvider, builtAwaiter, buildRetryable()));
	}

	private Builder<RetryStrategy> buildRetryStrategy() {
		if (isConfigured(perExecutionDelay)) {
			return perExecutionDelay;
		}
		if (isConfigured(exponentialDelay)) {
			return exponentialDelay;
		}
		if (isConfigured(fixedInterval)) {
			return fixedInterval;
		}
		if (isConfigured(fixedDelay)) {
			return fixedDelay;
		}
		return null;
	}

	private static boolean isConfigured(Builder<RetryStrategy> builder) {
		if (builder == null) {
			return false;
		}
		return builder.build() != null;
	}

	private Awaiter buildAwaiter() {
		if (awaiter == null) {
			return new ThreadSleepAwaiter();
		}
		return awaiter;
	}

	private Predicate<Throwable> buildRetryable() {
		if (retryable == null) {
			return e -> e instanceof Exception;
		}
		return retryable;
	}
}
