package fr.sii.ogham.core.retry;

import java.util.concurrent.Callable;

/**
 * Execute an action until it succeeds or the maximum retries are reached.
 * 
 * The retry management is handled by a {@link RetryStrategy} strategy. This strategy
 * indicates when the next try (next call of the action) should operate and when
 * the retries must stop.
 * 
 * For example, if the retry strategy is {@link FixedDelayRetry} with a 500ms
 * delay and 5 max retries, it means that a retry will be attempted every 500ms
 * until 5 attempts are reached (inclusive). For example, you want to connect to
 * an external system at t1=0 and the connection timeout (100ms) is triggered at
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
 * 
 * @author Aurélien Baudet
 *
 */
public interface RetryExecutor {
	/**
	 * Execute the action. If the action succeeds then return the result
	 * immediately. If the action fails (any exception) then retry it according
	 * to {@link RetryStrategy} strategy. The action will be executed until it succeeds
	 * or the {@link RetryStrategy} strategy is terminated. In this case, an exception
	 * is thrown (often the last one).
	 * 
	 * @param actionToRetry
	 *            the action to execute and retry if fails to execute
	 * @param <V>
	 *            the type of the object returned by the executed action
	 * @return the result of the executed action
	 * @throws Exception
	 *             when after maximum retries the action couldn't be executed
	 */
	<V> V execute(Callable<V> actionToRetry) throws Exception;
}
