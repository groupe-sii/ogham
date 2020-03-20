package fr.sii.ogham.testing.sms.simulator.config;

import java.util.List;

/**
 * Configuration for local server used to simulate a SMPP server.
 * 
 * @author Aurélien Baudet
 *
 */
public class SimulatorConfiguration {
	private List<Credentials> credentials;
	private ServerDelays serverDelays;
	private boolean keepMessages;

	/**
	 * Get the allowed credentials
	 * 
	 * @return the allowed credentials
	 */
	public List<Credentials> getCredentials() {
		return credentials;
	}

	/**
	 * Set the allowed credentials
	 * 
	 * @param credentials
	 *            the allowed credentials
	 */
	public void setCredentials(List<Credentials> credentials) {
		this.credentials = credentials;
	}

	/**
	 * Control delays to simulate a slow server.
	 * 
	 * @return the delay configuration
	 */
	public ServerDelays getServerDelays() {
		return serverDelays;
	}

	/**
	 * Control delays to simulate a slow server.
	 * 
	 * @param serverDelays
	 *            the delay configuration
	 */
	public void setServerDelays(ServerDelays serverDelays) {
		this.serverDelays = serverDelays;
	}

	/**
	 * If the server is restarted, it indicates if received messages in the
	 * previous session should be kept (true) or dropped (false).
	 * 
	 * @return indicate if messages should be kept or not between sessions
	 */
	public boolean isKeepMessages() {
		return keepMessages;
	}

	/**
	 * If the server is restarted, it indicates if received messages in the
	 * previous session should be kept (true) or dropped (false).
	 * 
	 * @param keepMessages
	 *            indicate if messages should be kept or not between sessions
	 */
	public void setKeepMessages(boolean keepMessages) {
		this.keepMessages = keepMessages;
	}

}
