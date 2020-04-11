package fr.sii.ogham.spring.common;

import java.util.List;

public class SendRetryProperties {
	/**
	 * Set the maximum number of attempts for sending a message.
	 */
	private Integer maxAttempts;
	/**
	 * Set the delay between two attempts for sending a message (in
	 * milliseconds).
	 */
	private Long delayBetweenAttempts;

	/**
	 * Set the initial delay between two executions (in milliseconds). This
	 * delay will be doubled for each try.
	 */
	private Long exponentialInitialDelay;

	/**
	 * Set specific delays (in milliseconds) used for each new execution. If
	 * there are more attempts than the configured delays, the last delay is
	 * used for remaining attempts.
	 */
	private List<Long> perExecutionDelays;

	/**
	 * Retry several times with a fixed delay between each try (no matter how
	 * long the execution of the action lasts) until the maximum attempts is
	 * reached. The next execution date is based on the execution start date of
	 * the first execution.<br />
	 * <br />
	 * Set the interval between two executions (in milliseconds).
	 */
	private Long executionInterval;

	public Integer getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(Integer maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	public Long getDelayBetweenAttempts() {
		return delayBetweenAttempts;
	}

	public void setDelayBetweenAttempts(Long delayBetweenAttempts) {
		this.delayBetweenAttempts = delayBetweenAttempts;
	}

	public Long getExponentialInitialDelay() {
		return exponentialInitialDelay;
	}

	public void setExponentialInitialDelay(Long exponentialInitialDelay) {
		this.exponentialInitialDelay = exponentialInitialDelay;
	}

	public List<Long> getPerExecutionDelays() {
		return perExecutionDelays;
	}

	public void setPerExecutionDelays(List<Long> perExecutionDelays) {
		this.perExecutionDelays = perExecutionDelays;
	}

	public Long getExecutionInterval() {
		return executionInterval;
	}

	public void setExecutionInterval(Long executionInterval) {
		this.executionInterval = executionInterval;
	}

}
