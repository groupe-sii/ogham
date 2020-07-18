package fr.sii.ogham.testing.extension.junit.sms.config;

import fr.sii.ogham.testing.sms.simulator.config.FixedServerPortProvider;
import fr.sii.ogham.testing.sms.simulator.config.ServerPortProvider;

/**
 * Configuration that simply builds a {@link FixedServerPortProvider} that will in turn
 * provide a fixed port.
 * 
 * @author Aurélien Baudet
 *
 */
public class FixedPortConfig implements PortConfig {
	private final int port;

	/**
	 * Initialize with the fixed port.
	 * 
	 * @param port
	 *            the port for the server
	 * @throws IllegalArgumentException
	 *             when the port is 0
	 */
	public FixedPortConfig(int port) {
		super();
		if (port <= 0) {
			throw new IllegalArgumentException("Fixed port can't be 0 or negative. If you want random port, please use RandomPortConfig instead");
		}
		this.port = port;
	}

	@Override
	public ServerPortProvider build() {
		return new FixedServerPortProvider(port);
	}

}
