package fr.sii.ogham.sms.sender.impl.cloudhopper;

import fr.sii.ogham.core.retry.RetryExecutor;

public class CloudhopperOptions {
	private long responseTimeout;
	private long unbindTimeout;
	private RetryExecutor connectRetry;

	public CloudhopperOptions(long responseTimeout, long unbindTimeout, RetryExecutor connectRetry) {
		super();
		this.responseTimeout = responseTimeout;
		this.unbindTimeout = unbindTimeout;
		this.connectRetry = connectRetry;
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
}
