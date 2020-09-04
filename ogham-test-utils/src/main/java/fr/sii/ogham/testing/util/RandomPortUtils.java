package fr.sii.ogham.testing.util;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.SortedSet;

import javax.net.ServerSocketFactory;

import fr.sii.ogham.testing.util.port.DefaultPortFinder;
import fr.sii.ogham.testing.util.port.PortFinder;

/**
 * Simple utility methods for finding available ports on {@code localhost}.
 *
 * <p>
 * Within this class, a TCP port refers to a port for a {@link ServerSocket};
 * whereas, a UDP port refers to a port for a {@link DatagramSocket}.
 * 
 * <strong>NOTE:</strong> This code has been borrowed from Spring Framework.
 * 
 * @see "https://github.com/spring-projects/spring-framework/blob/master/spring-core/src/main/java/org/springframework/util/SocketUtils.java"
 */
public final class RandomPortUtils {

	/**
	 * The default minimum value for port ranges used when finding an available
	 * socket port.
	 */
	public static final int PORT_RANGE_MIN = 1024;

	/**
	 * The default maximum value for port ranges used when finding an available
	 * socket port.
	 */
	public static final int PORT_RANGE_MAX = 65535;

	/**
	 * Find an available TCP port randomly selected from the range
	 * [{@value #PORT_RANGE_MIN}, {@value #PORT_RANGE_MAX}].
	 * 
	 * @return an available TCP port number
	 * @throws IllegalStateException
	 *             if no available port could be found
	 */
	public static int findAvailableTcpPort() {
		return findAvailableTcpPort(PORT_RANGE_MIN);
	}

	/**
	 * Find an available TCP port randomly selected from the range
	 * [{@code minPort}, {@value #PORT_RANGE_MAX}].
	 * 
	 * @param minPort
	 *            the minimum port number
	 * @return an available TCP port number
	 * @throws IllegalStateException
	 *             if no available port could be found
	 */
	public static int findAvailableTcpPort(int minPort) {
		return findAvailableTcpPort(minPort, PORT_RANGE_MAX);
	}

	/**
	 * Find an available TCP port randomly selected from the range
	 * [{@code minPort}, {@code maxPort}].
	 * 
	 * @param minPort
	 *            the minimum port number
	 * @param maxPort
	 *            the maximum port number
	 * @return an available TCP port number
	 * @throws IllegalStateException
	 *             if no available port could be found
	 */
	public static int findAvailableTcpPort(int minPort, int maxPort) {
		return SocketType.TCP.findAvailablePort(minPort, maxPort);
	}

	/**
	 * Find the requested number of available TCP ports, each randomly selected
	 * from the range [{@value #PORT_RANGE_MIN}, {@value #PORT_RANGE_MAX}].
	 * 
	 * @param numRequested
	 *            the number of available ports to find
	 * @return a sorted set of available TCP port numbers
	 * @throws IllegalStateException
	 *             if the requested number of available ports could not be found
	 */
	public static SortedSet<Integer> findAvailableTcpPorts(int numRequested) {
		return findAvailableTcpPorts(numRequested, PORT_RANGE_MIN, PORT_RANGE_MAX);
	}

	/**
	 * Find the requested number of available TCP ports, each randomly selected
	 * from the range [{@code minPort}, {@code maxPort}].
	 * 
	 * @param numRequested
	 *            the number of available ports to find
	 * @param minPort
	 *            the minimum port number
	 * @param maxPort
	 *            the maximum port number
	 * @return a sorted set of available TCP port numbers
	 * @throws IllegalStateException
	 *             if the requested number of available ports could not be found
	 */
	public static SortedSet<Integer> findAvailableTcpPorts(int numRequested, int minPort, int maxPort) {
		return SocketType.TCP.findAvailablePorts(numRequested, minPort, maxPort);
	}

	/**
	 * Find an available UDP port randomly selected from the range
	 * [{@value #PORT_RANGE_MIN}, {@value #PORT_RANGE_MAX}].
	 * 
	 * @return an available UDP port number
	 * @throws IllegalStateException
	 *             if no available port could be found
	 */
	public static int findAvailableUdpPort() {
		return findAvailableUdpPort(PORT_RANGE_MIN);
	}

	/**
	 * Find an available UDP port randomly selected from the range
	 * [{@code minPort}, {@value #PORT_RANGE_MAX}].
	 * 
	 * @param minPort
	 *            the minimum port number
	 * @return an available UDP port number
	 * @throws IllegalStateException
	 *             if no available port could be found
	 */
	public static int findAvailableUdpPort(int minPort) {
		return findAvailableUdpPort(minPort, PORT_RANGE_MAX);
	}

	/**
	 * Find an available UDP port randomly selected from the range
	 * [{@code minPort}, {@code maxPort}].
	 * 
	 * @param minPort
	 *            the minimum port number
	 * @param maxPort
	 *            the maximum port number
	 * @return an available UDP port number
	 * @throws IllegalStateException
	 *             if no available port could be found
	 */
	public static int findAvailableUdpPort(int minPort, int maxPort) {
		return SocketType.UDP.findAvailablePort(minPort, maxPort);
	}

	/**
	 * Find the requested number of available UDP ports, each randomly selected
	 * from the range [{@value #PORT_RANGE_MIN}, {@value #PORT_RANGE_MAX}].
	 * 
	 * @param numRequested
	 *            the number of available ports to find
	 * @return a sorted set of available UDP port numbers
	 * @throws IllegalStateException
	 *             if the requested number of available ports could not be found
	 */
	public static SortedSet<Integer> findAvailableUdpPorts(int numRequested) {
		return findAvailableUdpPorts(numRequested, PORT_RANGE_MIN, PORT_RANGE_MAX);
	}

	/**
	 * Find the requested number of available UDP ports, each randomly selected
	 * from the range [{@code minPort}, {@code maxPort}].
	 * 
	 * @param numRequested
	 *            the number of available ports to find
	 * @param minPort
	 *            the minimum port number
	 * @param maxPort
	 *            the maximum port number
	 * @return a sorted set of available UDP port numbers
	 * @throws IllegalStateException
	 *             if the requested number of available ports could not be found
	 */
	public static SortedSet<Integer> findAvailableUdpPorts(int numRequested, int minPort, int maxPort) {
		return SocketType.UDP.findAvailablePorts(numRequested, minPort, maxPort);
	}

	@SuppressWarnings("squid:IndentationCheck")
	private enum SocketType implements PortFinder {

		TCP {
			@Override
			protected boolean isPortAvailable(int port) {
				try {
					ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, 1, InetAddress.getByName("localhost"));
					serverSocket.close();
					return true;
				} catch (Exception ex) {
					return false;
				}
			}
		},

		UDP {
			@Override
			protected boolean isPortAvailable(int port) {
				try {
					DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("localhost"));
					socket.close();
					return true;
				} catch (Exception ex) {
					return false;
				}
			}
		};
		
		private PortFinder delegate;
		
		SocketType() {
			this.delegate = new DefaultPortFinder(name(), this::isPortAvailable);
		}

		protected abstract boolean isPortAvailable(int port);
		
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
		@Override
		public int findAvailablePort(int minPort, int maxPort) {
			return delegate.findAvailablePort(minPort, maxPort);
		}
		
		/**
		 * Find the requested number of available ports for this
		 * {@code SocketType}, each randomly selected from the range
		 * [{@code minPort}, {@code maxPort}].
		 * 
		 * @param numRequested
		 *            the number of available ports to find
		 * @param minPort
		 *            the minimum port number
		 * @param maxPort
		 *            the maximum port number
		 * @return a sorted set of available port numbers for this socket type
		 * @throws IllegalStateException
		 *             if the requested number of available ports could not be
		 *             found
		 */
		@Override
		public SortedSet<Integer> findAvailablePorts(int numRequested, int minPort, int maxPort) {
			return delegate.findAvailablePorts(numRequested, minPort, maxPort);
		}
	}

	private RandomPortUtils() {
		super();
	}

}