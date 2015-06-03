package fr.sii.notification.helper.sms.rule;

import java.util.List;

import org.jsmpp.bean.SubmitSm;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.helper.sms.SMPPServerSimulator;

/**
 * JUnit {@link Rule} for starting a local SMPP server for integration tests.
 * The server used is based on <a href="http://jsmpp.org/">jsmpp</a>.
 * 
 * <p>
 * The rule starts the server before every test, execute the test and stops the
 * server.
 * </p>
 * <p>
 * The server stores the received messages (raw messages). These messages are
 * available in testing through {@link #getReceivedMessages()}.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public class SmppServerRule implements TestRule {
	private static final Logger LOG = LoggerFactory.getLogger(SmppServerRule.class);

	/**
	 * The default port used by the server if none is specified
	 */
	public static final int DEFAULT_PORT = 8056;

	/**
	 * The server simulator based on <a href="http://jsmpp.org/">jsmpp</a>
	 * samples.
	 */
	private final SMPPServerSimulator server;

	/**
	 * The port used by the server
	 */
	private final int port;

	/**
	 * Initialize the server with the provided port.
	 * 
	 * @param port
	 *            the port used by the server
	 */
	public SmppServerRule(int port) {
		super();
		this.port = port;
		server = new SMPPServerSimulator(port);
	}

	/**
	 * Initialize the server with the default port.
	 */
	public SmppServerRule() {
		this(DEFAULT_PORT);
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				LOG.info("starting SMPP server on port {}...", port);
				Thread t = new ServerThread(server);
				t.start();
				try {
					base.evaluate();
				} finally {
					LOG.info("stopping SMPP server...");
					t.interrupt();
				}
			}
		};
	}

	/**
	 * A thread for starting and stopping the server. Needs a dedicated
	 * implementation to be able to stop the simulator loop when calling
	 * interrupt method of the thread.
	 * 
	 * @author Aurélien Baudet
	 *
	 */
	private static class ServerThread extends Thread {

		private SMPPServerSimulator server;

		public ServerThread(SMPPServerSimulator target) {
			super(target);
			this.server = target;
		}

		@Override
		public void interrupt() {
			server.stop();
			super.interrupt();
		}
	}

	/**
	 * Get the port used by the server.
	 * 
	 * @return the port used by the server
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Provide the list of received messages during the execution of the test.
	 * 
	 * @return the list of received messages
	 */
	public List<SubmitSm> getReceivedMessages() {
		return server.getReceivedMessages();
	}

}
