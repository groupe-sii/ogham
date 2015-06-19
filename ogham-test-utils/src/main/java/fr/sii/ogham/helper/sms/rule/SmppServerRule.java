package fr.sii.ogham.helper.sms.rule;

import java.util.List;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit {@link Rule} for starting a local SMPP server for integration tests.
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
 * @param <M>
 *            The type of the received messages
 * 
 * @author Aurélien Baudet
 *
 */
public class SmppServerRule<M> implements TestRule {
	private static final Logger LOG = LoggerFactory.getLogger(SmppServerRule.class);

	/**
	 * The default port used by the server if none is specified
	 */
	public static final int DEFAULT_PORT = 8056;

	/**
	 * The server simulator based on <a href="http://jsmpp.org/">jsmpp</a>
	 * samples.
	 */
	private final SmppServerSimulator<M> server;

	/**
	 * Initialize the server with the provided port.
	 * 
	 * @param port
	 *            the port used by the server
	 */
	public SmppServerRule(SmppServerSimulator<M> server) {
		super();
		this.server = server;
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				LOG.info("starting SMPP server on port {}...", getPort());
				server.start();
				LOG.info("SMPP server started on port {}", getPort());
				try {
					base.evaluate();
				} finally {
					LOG.info("stopping SMPP server...");
					server.stop();
					LOG.info("SMPP server stopped");
				}
			}
		};
	}

	/**
	 * Get the port used by the server.
	 * 
	 * @return the port used by the server
	 */
	public int getPort() {
		return server.getPort();
	}

	/**
	 * Provide the list of received messages during the execution of the test.
	 * 
	 * @return the list of received messages
	 */
	public List<M> getReceivedMessages() {
		return server.getReceivedMessages();
	}
}
