package fr.sii.ogham.testing.extension.junit.sms.config;

import fr.sii.ogham.testing.sms.simulator.config.ServerPortProvider;

/**
 * Provide configuration to build a provider that will provide a port for the
 * server.
 * 
 * @author Aurélien Baudet
 *
 */
public interface PortConfig {

	/**
	 * Build the port provider.
	 * 
	 * @return the port provider
	 */
	ServerPortProvider build();

}
