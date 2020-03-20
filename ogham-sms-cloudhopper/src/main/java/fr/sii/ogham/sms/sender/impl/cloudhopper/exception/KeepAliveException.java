package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

public class KeepAliveException extends SmppException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;
	
	private final int consecutiveFailures;

	public KeepAliveException(String message, Throwable cause, int consecutiveFailures) {
		super(message, cause);
		this.consecutiveFailures = consecutiveFailures;
	}

	public int getConsecutiveFailures() {
		return consecutiveFailures;
	}

}
