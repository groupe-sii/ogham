package fr.sii.ogham.testing.extension.greenmail;

import com.icegreen.greenmail.util.ServerSetup;

import fr.sii.ogham.testing.util.RandomPortUtils;

/**
 * Extension of {@link ServerSetup} to handle random port.
 * 
 * Range (min and max port numbers) must be specified.
 * 
 * @author Aurélien Baudet
 *
 */
public class RandomPortServerSetup extends ServerSetup {
	private final int minPort;
	private final int maxPort;
	private int currentPort;

	/**
	 * Initialize the range of ports for a particular protocol. It uses the
	 * default bind address ({@code "127.0.0.1"} defined by
	 * {@link ServerSetup}).
	 * 
	 * @param minPort
	 *            the minimum port value
	 * @param maxPort
	 *            the maximum port value
	 * @param protocol
	 *            the protocol
	 */
	public RandomPortServerSetup(int minPort, int maxPort, String protocol) {
		this(minPort, maxPort, null, protocol);
	}

	/**
	 * Initialize the range of ports for a particular protocol. The bind address
	 * is manually specified ({@code null} can be specified to use default bind
	 * address).
	 * 
	 * @param minPort
	 *            the minimum port value
	 * @param maxPort
	 *            the maximum port value
	 * @param bindAddress
	 *            the bind address
	 * @param protocol
	 *            the protocol
	 */
	public RandomPortServerSetup(int minPort, int maxPort, String bindAddress, String protocol) {
		super(0, bindAddress, protocol);
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

	/**
	 * Add possibility to reset port in order to reuse the same server instance
	 * and configuration on different port when restarted.
	 */
	public void resetPort() {
		currentPort = 0;
	}
}
