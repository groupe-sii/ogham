package fr.sii.ogham.testing.sms.simulator.config;

import fr.sii.ogham.testing.util.RandomPortUtils;

/**
 * Provider that provides a random port.
 * 
 * This provider can be reseted in order to provide a different random port each
 * time.
 * 
 * @author Aurélien Baudet
 *
 */
public class RandomServerPortProvider implements ServerPortProvider {
	private final int minPort;
	private final int maxPort;
	private int currentPort;

	/**
	 * Initialize with the port range.
	 * 
	 * @param minPort
	 *            the minimum port value
	 * @param maxPort
	 *            the maximum port value
	 */
	public RandomServerPortProvider(int minPort, int maxPort) {
		super();
		this.minPort = minPort;
		this.maxPort = maxPort;
	}

	@Override
	public int getPort() {
		if (currentPort == 0) {
			currentPort = RandomPortUtils.findAvailableTcpPort(minPort, maxPort);
		}
		return currentPort;
	}

	@Override
	public void reset() {
		currentPort = 0;
	}

}
