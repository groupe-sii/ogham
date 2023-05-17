package fr.sii.ogham.testing.extension.junit.sms;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import fr.sii.ogham.testing.extension.junit.sms.config.SmppServerConfig;
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
public abstract class SmppServerRule<M> extends AbstractJUnitSmppServerExt<M> implements TestRule {
	private static final Logger LOG = LoggerFactory.getLogger(SmppServerRule.class);

	/**
	 * Initializes with the default configuration to use for the SMPP server:
	 * <ul>
	 * <li>Starts on random port</li>
	 * <li>No delay</li>
	 * <li>No credentials</li>
	 * <li>Do not keep messages between tests</li>
	 * </ul>
	 */
	public SmppServerRule() {
		super();
	}

	/**
	 * Initializes with the configuration to use for the SMPP server
	 * 
	 * @param builder
	 *            the configuration for the server
	 */
	public SmppServerRule(ServerConfig builder) {
		super(builder);
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		SmppServerConfig config = description.getAnnotation(SmppServerConfig.class);
		SimulatorConfiguration serverConfig = builder.annotationConfig(config).build();
		server = initServer(serverConfig);
		return new StartServerStatement(base);
	}

	private final class StartServerStatement extends Statement {
		private final Statement base;

		private StartServerStatement(Statement base) {
			this.base = base;
		}

		@Override
		public void evaluate() throws Throwable {
			LOG.info("starting SMPP server on port {}...", getPort());
			before();
			server.start();
			LOG.info("SMPP server started on port {}", getPort());
			try {
				base.evaluate();
			} finally {
				LOG.info("stopping SMPP server...");
				server.stop();
				after();
				LOG.info("SMPP server stopped");
			}
		}
	}

	protected void before() {
		// extension point
	}

	protected void after() {
		// extension point
	}

}
