package fr.sii.ogham.testing.util.port;

import java.util.SortedSet;

/**
 * Abstraction for searching available ports
 * 
 * @author Aurélien Baudet
 *
 */
public interface PortFinder {
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
	int findAvailablePort(int minPort, int maxPort);

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
	SortedSet<Integer> findAvailablePorts(int numRequested, int minPort, int maxPort);
}