package fr.sii.ogham.testing.helper.sms.rule.config;

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

}
