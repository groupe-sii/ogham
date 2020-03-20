package fr.sii.ogham.testing.sms.simulator.jsmpp;

import fr.sii.ogham.testing.sms.simulator.SmppServerException;

public class ServerStartupException extends SmppServerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3260007404305593470L;

	public ServerStartupException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerStartupException(String message) {
		super(message);
	}

}
