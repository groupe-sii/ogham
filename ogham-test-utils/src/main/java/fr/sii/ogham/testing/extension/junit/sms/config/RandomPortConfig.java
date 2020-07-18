package fr.sii.ogham.testing.extension.junit.sms.config;

import fr.sii.ogham.testing.sms.simulator.config.RandomServerPortProvider;
import fr.sii.ogham.testing.sms.simulator.config.ServerPortProvider;

/**
 * Configuration that builds a {@link RandomServerPortProvider} with the provided range
 * of ports. {@link RandomServerPortProvider} will in turn provide a random port that is
 * in the provided range.
 * 
 * @author Aurélien Baudet
 *
 */
public class RandomPortConfig implements PortConfig {
	private final int minPort;
	private final int maxPort;

	/**
	 * Initialize with the port range.
	 * 
	 * @param minPort
	 *            the minimum port value
	 * @param maxPort
	 *            the maximum port value
	 */
	public RandomPortConfig(int minPort, int maxPort) {
		super();
		this.minPort = minPort;
		this.maxPort = maxPort;
	}

	@Override
	public ServerPortProvider build() {
		return new RandomServerPortProvider(minPort, maxPort);
	}

}
