package fr.sii.ogham.sms.sender.impl.cloudhopper;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

import com.cloudhopper.smpp.pdu.EnquireLink;

public class KeepAliveOptions {
	/**
	 * Enable or disable sending of {@link EnquireLink} messages to keep the
	 * session alive.
	 */
	private Boolean enable;
	/**
	 * The delay (in milliseconds) between two {@link EnquireLink} messages.
	 */
	private Long enquireLinkInterval;
	/**
	 * The maximum amount of time (in milliseconds) to wait for receiving a
	 * response from the server to an {@link EnquireLink} request.
	 */
	private Long enquireLinkTimeout;
	/**
	 * Connect to the server directly when the client is ready (if true).
	 * Otherwise, the connection is done when the first message is sent.
	 * 
	 * This may be useful to avoid a latency for the first message.
	 * 
	 * If connection fails at startup, then a new attempt is done when first
	 * message is sent.
	 */
	private Boolean connectAtStartup;
	/**
	 * Provide a factory that creates a {@link ScheduledExecutorService}. The
	 * created executor is then used to schedule the task that sends regularly
	 * {@link EnquireLink} requests.
	 */
	private Supplier<ScheduledExecutorService> executor;
	/**
	 * The maximum number of consecutive {@link EnquireLink} requests that end
	 * in timeout to consider that a new session is required.
	 */
	private Integer maxConsecutiveTimeouts;

	public KeepAliveOptions() {
		super();
	}

	public KeepAliveOptions(boolean enable) {
		super();
		this.enable = enable;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public boolean isEnable(boolean defaultValue) {
		return enable == null ? defaultValue : enable;
	}

	public Long getEnquireLinkInterval() {
		return enquireLinkInterval;
	}

	public void setEnquireLinkInterval(Long enquireLinkInterval) {
		this.enquireLinkInterval = enquireLinkInterval;
	}

	public Long getEnquireLinkTimeout() {
		return enquireLinkTimeout;
	}

	public void setEnquireLinkTimeout(Long enquireLinkTimeout) {
		this.enquireLinkTimeout = enquireLinkTimeout;
	}

	public Boolean getConnectAtStartup() {
		return connectAtStartup;
	}

	public void setConnectAtStartup(Boolean connectAtStartup) {
		this.connectAtStartup = connectAtStartup;
	}

	public boolean isConnectAtStartup(boolean defaultValue) {
		return connectAtStartup == null ? defaultValue : connectAtStartup;
	}

	public Supplier<ScheduledExecutorService> getExecutor() {
		return executor;
	}

	public void setExecutor(Supplier<ScheduledExecutorService> executor) {
		this.executor = executor;
	}

	public Integer getMaxConsecutiveTimeouts() {
		return maxConsecutiveTimeouts;
	}

	public void setMaxConsecutiveTimeouts(Integer maximumConsecutiveTimeouts) {
		this.maxConsecutiveTimeouts = maximumConsecutiveTimeouts;
	}

}
