package fr.sii.ogham.sms.sender.impl.cloudhopper;

import fr.sii.ogham.core.retry.RetryExecutor;

/**
 * Options to configure how Cloudhopper should behave:
 * <ul>
 * <li>Change timeouts (response, unbind)</li>
 * <li>Change retry strategy</li>
 * <li>Allow to keep session opened for all sent messages</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class CloudhopperOptions {
	private long responseTimeout;
	private long unbindTimeout;
	private RetryExecutor connectRetry;
	private boolean keepSession;

	/**
	 * Initialize with configured values
	 * 
	 * @param responseTimeout
	 *            the response timeout
	 * @param unbindTimeout
	 *            the unbind timeout
	 * @param connectRetry
	 *            the retry strategy for connecting
	 * @param keepSession
	 *            reuse or create a new session for sending messages
	 */
	public CloudhopperOptions(long responseTimeout, long unbindTimeout, RetryExecutor connectRetry, boolean keepSession) {
		super();
		this.responseTimeout = responseTimeout;
		this.unbindTimeout = unbindTimeout;
		this.connectRetry = connectRetry;
		this.keepSession = keepSession;
	}

	public long getResponseTimeout() {
		return responseTimeout;
	}

	public void setResponseTimeout(long responseTimeout) {
		this.responseTimeout = responseTimeout;
	}

	public long getUnbindTimeout() {
		return unbindTimeout;
	}

	public void setUnbindTimeout(long unbindTimeout) {
		this.unbindTimeout = unbindTimeout;
	}

	public RetryExecutor getConnectRetry() {
		return connectRetry;
	}

	public void setConnectRetry(RetryExecutor connectRetry) {
		this.connectRetry = connectRetry;
	}

	public boolean isKeepSession() {
		return keepSession;
	}

	public void setKeepSession(boolean keepSession) {
		this.keepSession = keepSession;
	}
}
