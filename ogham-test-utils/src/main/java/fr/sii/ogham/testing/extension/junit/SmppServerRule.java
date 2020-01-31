package fr.sii.ogham.testing.extension.junit;

import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import fr.sii.ogham.testing.extension.junit.sms.config.SmppServerConfig;
import fr.sii.ogham.testing.sms.simulator.SmppServerSimulator;
import fr.sii.ogham.testing.sms.simulator.config.SimulatorConfiguration;

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
public abstract class SmppServerRule<M> implements TestRule {
	private static final Logger LOG = LoggerFactory.getLogger(SmppServerRule.class);

	/**
	 * The default port used by the server if none is specified
	 */
	public static final int DEFAULT_PORT = 8056;

	/**
	 * The server port
	 */
	private int port;
	/**
	 * The configuration to control how server should behave
	 */
	private final ServerConfig builder;
	/**
	 * The server simulator
	 */
	private SmppServerSimulator<M> server;

	/**
	 * Initializes with the port to use for the SMPP server
	 * 
	 * @param port
	 *            the port for server
	 * @param builder
	 *            the configuration for the server
	 */
	public SmppServerRule(int port, ServerConfig builder) {
		super();
		this.port = port;
		this.builder = builder;
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		SmppServerConfig config = description.getAnnotation(SmppServerConfig.class);
		this.server = initServer(builder.annotationConfig(config).build());
		return new StartServerStatement(base);
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
	public List<M> getReceivedMessages() {
		if (server == null) {
			return Collections.emptyList();
		}
		return server.getReceivedMessages();
	}

	/**
	 * Initialize the server with the ready to use configuration.
	 * 
	 * @param simulatorConfiguration
	 *            the configuration to apply to the server
	 * @return the server instance
	 */
	protected abstract SmppServerSimulator<M> initServer(SimulatorConfiguration simulatorConfiguration);

	private final class StartServerStatement extends Statement {
		private final Statement base;

		private StartServerStatement(Statement base) {
			this.base = base;
		}

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
	}

}
