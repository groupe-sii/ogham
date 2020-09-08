package fr.sii.ogham.testing.util.port;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.IntPredicate;

import fr.sii.ogham.testing.util.RandomPortUtils;

/**
 * Default implementation that search for ports by delegating port availability
 * check to a function.
 * 
 * @author Aurélien Baudet
 *
 */
public class DefaultPortFinder implements PortFinder {
	protected final Random random;
	protected final String protocol;
	protected final IntPredicate isPortAvailable;

	/**
	 * Initialize with a function to check if a port is available or not.
	 * 
	 * @param protocol
	 *            the protocol
	 * @param isPortAvailable
	 *            Determine if the specified port for this {@code SocketType} is
	 *            currently available on {@code localhost}.
	 */
	@SuppressWarnings("java:S2245")
	public DefaultPortFinder(String protocol, IntPredicate isPortAvailable) {
		this(protocol, isPortAvailable, new Random(System.nanoTime()));
	}

	/**
	 * Initialize with a function to check if a port is available or not.
	 * 
	 * @param protocol
	 *            the protocol
	 * @param isPortAvailable
	 *            Determine if the specified port for this {@code SocketType} is
	 *            currently available on {@code localhost}.
	 * @param random
	 *            the random implementation to use
	 */
	public DefaultPortFinder(String protocol, IntPredicate isPortAvailable, Random random) {
		super();
		this.protocol = protocol;
		this.isPortAvailable = isPortAvailable;
		this.random = random;
	}

	/**
	 * Find an available port for this {@code SocketType}, randomly selected
	 * from the range [{@code minPort}, {@code maxPort}].
	 * 
	 * @param minPort
	 *            the minimum port number
	 * @param maxPort
	 *            the maximum port number
	 * @return an available port number for this socket type
	 * @throws IllegalStateException
	 *             if no available port could be found
	 */
	public int findAvailablePort(int minPort, int maxPort) {
		assertIsTrue(minPort > 0, "'minPort' must be greater than 0");
		assertIsTrue(maxPort >= minPort, "'maxPort' must be greater than or equal to 'minPort'");
		assertIsTrue(maxPort <= RandomPortUtils.PORT_RANGE_MAX, "'maxPort' must be less than or equal to " + RandomPortUtils.PORT_RANGE_MAX);

		int portRange = maxPort - minPort;
		int candidatePort;
		int searchCounter = 0;
		do {
			if (searchCounter > portRange) {
				throw new IllegalStateException(String.format("Could not find an available %s port in the range [%d, %d] after %d attempts", protocol, minPort, maxPort, searchCounter));
			}
			candidatePort = findRandomPort(minPort, maxPort);
			searchCounter++;
		} while (!isPortAvailable.test(candidatePort));

		return candidatePort;
	}

	/**
	 * Find the requested number of available ports for this {@code SocketType},
	 * each randomly selected from the range [{@code minPort}, {@code maxPort}].
	 * 
	 * @param numRequested
	 *            the number of available ports to find
	 * @param minPort
	 *            the minimum port number
	 * @param maxPort
	 *            the maximum port number
	 * @return a sorted set of available port numbers for this socket type
	 * @throws IllegalStateException
	 *             if the requested number of available ports could not be found
	 */
	public SortedSet<Integer> findAvailablePorts(int numRequested, int minPort, int maxPort) {
		assertIsTrue(minPort > 0, "'minPort' must be greater than 0");
		assertIsTrue(maxPort > minPort, "'maxPort' must be greater than 'minPort'");
		assertIsTrue(maxPort <= RandomPortUtils.PORT_RANGE_MAX, "'maxPort' must be less than or equal to " + RandomPortUtils.PORT_RANGE_MAX);
		assertIsTrue(numRequested > 0, "'numRequested' must be greater than 0");
		assertIsTrue((maxPort - minPort) >= numRequested, "'numRequested' must not be greater than 'maxPort' - 'minPort'");

		SortedSet<Integer> availablePorts = new TreeSet<>();
		int attemptCount = 0;
		while ((++attemptCount <= numRequested + 100) && availablePorts.size() < numRequested) {
			availablePorts.add(findAvailablePort(minPort, maxPort));
		}

		if (availablePorts.size() != numRequested) {
			throw new IllegalStateException(String.format("Could not find %d available %s ports in the range [%d, %d]", numRequested, protocol, minPort, maxPort));
		}

		return availablePorts;
	}
	
	/**
	 * Find a pseudo-random port number within the range [{@code minPort},
	 * {@code maxPort}].
	 * 
	 * @param minPort
	 *            the minimum port number
	 * @param maxPort
	 *            the maximum port number
	 * @return a random port number within the specified range
	 */
	private int findRandomPort(int minPort, int maxPort) {
		int portRange = maxPort - minPort;
		return minPort + random.nextInt(portRange + 1);
	}

	private static void assertIsTrue(boolean condition, String message) {
		if (!condition) {
			throw new IllegalArgumentException(message);
		}
	}
}