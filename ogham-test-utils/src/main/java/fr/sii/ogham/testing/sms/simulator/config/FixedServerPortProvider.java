package fr.sii.ogham.testing.sms.simulator.config;

/**
 * Provider that simply provides a fixed port.
 * 
 * @author Aurélien Baudet
 *
 */
public class FixedServerPortProvider implements ServerPortProvider {
	private final int port;

	/**
	 * Initialize with the provided port.
	 * 
	 * @param port
	 *            the port to use
	 */
	public FixedServerPortProvider(int port) {
		super();
		this.port = port;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void reset() {
		// nothing to do
	}

}
