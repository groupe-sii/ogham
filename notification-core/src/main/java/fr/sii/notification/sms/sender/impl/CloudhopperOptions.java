package fr.sii.notification.sms.sender.impl;

public class CloudhopperOptions {
	private long responseTimeout;
	
	private long unbindTimeout;

	public CloudhopperOptions(long responseTimeout, long unbindTimeout) {
		super();
		this.responseTimeout = responseTimeout;
		this.unbindTimeout = unbindTimeout;
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
}
