package fr.sii.ogham.testing.extension.junit.sms;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import fr.sii.ogham.testing.extension.junit.sms.config.SmppServerConfig;
import fr.sii.ogham.testing.sms.simulator.config.SimulatorConfiguration;

/**
 * JUnit {@link Extension} for starting a local SMPP server for integration
 * tests.
 * 
 * <p>
 * The extension starts the server before every test, execute the test and stops
 * the server.
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
public abstract class SmppServerExtension<M> extends AbstractJUnitSmppServerExt<M> implements BeforeEachCallback, AfterEachCallback {
	private static final Logger LOG = LoggerFactory.getLogger(SmppServerExtension.class);

	/**
	 * Initializes with the default configuration to use for the SMPP server:
	 * <ul>
	 * <li>Starts on random port</li>
	 * <li>No delay</li>
	 * <li>No credentials</li>
	 * <li>Do not keep messages between tests</li>
	 * </ul>
	 */
	public SmppServerExtension() {
		super();
	}

	/**
	 * Initializes with the configuration to use for the SMPP server
	 * 
	 * @param builder
	 *            the configuration for the server
	 */
	public SmppServerExtension(ServerConfig builder) {
		super(builder);
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		SmppServerConfig config = AnnotationSupport.findAnnotation(context.getElement(), SmppServerConfig.class).orElse(null);
		SimulatorConfiguration serverConfig = builder.annotationConfig(config).build();
		server = initServer(serverConfig);
		LOG.info("starting SMPP server on port {}...", getPort());
		server.start();
		LOG.info("SMPP server started on port {}", getPort());
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		LOG.info("stopping SMPP server...");
		server.stop();
		LOG.info("SMPP server stopped");
	}
}
