package fr.sii.ogham.testing.extension.junit.sms.config;

import fr.sii.ogham.testing.sms.simulator.config.ServerDelays;
import fr.sii.ogham.testing.util.HasParent;

public class SlowConfig extends HasParent<ServerConfig> {
	private final ServerDelays delays;

	public SlowConfig(ServerConfig parent) {
		super(parent);
		this.delays = new ServerDelays();
	}

	/**
	 * Simulate slow server by waiting {@code sendAlertNotificationDelay}
	 * milliseconds before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendAlertNotificationDelay(long delayMs) {
		delays.setSendAlertNotificationDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendBindDelay} milliseconds before
	 * sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendBindDelay(long delayMs) {
		delays.setSendBindDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendBindRespDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendBindRespDelay(long delayMs) {
		delays.setSendBindRespDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendCancelSmDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendCancelSmDelay(long delayMs) {
		delays.setSendCancelSmDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendCancelSmRespDelay}
	 * milliseconds before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendCancelSmRespDelay(long delayMs) {
		delays.setSendCancelSmRespDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendDataSmDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendDataSmDelay(long delayMs) {
		delays.setSendDataSmDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendDataSmRespDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendDataSmRespDelay(long delayMs) {
		delays.setSendDataSmRespDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendDeliverSmDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendDeliverSmDelay(long delayMs) {
		delays.setSendDeliverSmDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendDeliverSmRespDelay}
	 * milliseconds before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendDeliverSmRespDelay(long delayMs) {
		delays.setSendDeliverSmRespDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendEnquireLinkDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendEnquireLinkDelay(long delayMs) {
		delays.setSendEnquireLinkDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendEnquireLinkRespDelay}
	 * milliseconds before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendEnquireLinkRespDelay(long delayMs) {
		delays.setSendEnquireLinkRespDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendGenericNackDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendGenericNackDelay(long delayMs) {
		delays.setSendGenericNackDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendHeaderDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendHeaderDelay(long delayMs) {
		delays.setSendHeaderDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendOutbindDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendOutbindDelay(long delayMs) {
		delays.setSendOutbindDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendQuerySmDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendQuerySmDelay(long delayMs) {
		delays.setSendQuerySmDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendQuerySmRespDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendQuerySmRespDelay(long delayMs) {
		delays.setSendQuerySmRespDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendReplaceSmDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendReplaceSmDelay(long delayMs) {
		delays.setSendReplaceSmDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendReplaceSmRespDelay}
	 * milliseconds before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendReplaceSmRespDelay(long delayMs) {
		delays.setSendReplaceSmRespDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendSubmiMultiDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendSubmiMultiDelay(long delayMs) {
		delays.setSendSubmiMultiDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendSubmitMultiRespDelay}
	 * milliseconds before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendSubmitMultiRespDelay(long delayMs) {
		delays.setSendSubmitMultiRespDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendSubmitSmDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendSubmitSmDelay(long delayMs) {
		delays.setSendSubmitSmDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendSubmitSmRespDelay}
	 * milliseconds before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendSubmitSmRespDelay(long delayMs) {
		delays.setSendSubmitSmRespDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendUnbindDelay} milliseconds
	 * before sending data
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendUnbindDelay(long delayMs) {
		delays.setSendUnbindDelay(delayMs);
		return this;
	}

	public ServerDelays build() {
		return delays;
	}
}
