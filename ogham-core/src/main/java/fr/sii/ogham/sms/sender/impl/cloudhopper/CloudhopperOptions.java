package fr.sii.ogham.sms.sender.impl.cloudhopper;

import fr.sii.ogham.core.retry.Retry;

public class CloudhopperOptions {
	private long responseTimeout;
	private long unbindTimeout;
	private Retry connectRetry;

	public CloudhopperOptions(long responseTimeout, long unbindTimeout, Retry connectRetry) {
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

	public Retry getConnectRetry() {
		return connectRetry;
	}

	public void setConnectRetry(Retry connectRetry) {
		this.connectRetry = connectRetry;
	}
}
