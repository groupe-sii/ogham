package fr.sii.ogham.testing.extension.junit.sms;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import fr.sii.ogham.testing.extension.junit.sms.config.SmppServerConfig;
import fr.sii.ogham.testing.sms.simulator.SmppServerSimulator;
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm;
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
public abstract class SmppServerExtension<M> implements BeforeEachCallback, AfterEachCallback {
	private static final Logger LOG = LoggerFactory.getLogger(SmppServerExtension.class);

	/**
	 * The configuration to control how server should behave
	 */
	private final ServerConfig builder;
	/**
	 * The server simulator
	 */
	private SmppServerSimulator<M> server;

	/**
	 * Initializes with the default configuration to use for the SMPP server
	 * 
	 */
	public SmppServerExtension() {
		this(new ServerConfig());
	}

	/**
	 * Initializes with the configuration to use for the SMPP server
	 * 
	 * @param builder
	 *            the configuration for the server
	 */
	public SmppServerExtension(ServerConfig builder) {
		super();
		this.builder = builder;
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
	public List<M> getRawMessages() {
		if (server == null) {
			return Collections.emptyList();
		}
		return server.getReceivedMessages();
	}

	/**
	 * Provide the list of received messages during the execution of the test.
	 * 
	 * The raw messages are converted to the common interface
	 * ({@link SubmitSm}).
	 * 
	 * @return the list of received messages
	 */
	public List<SubmitSm> getReceivedMessages() {
		return getRawMessages().stream().map(this::convert).collect(toList());
	}

	/**
	 * Initialize the server with the ready to use configuration.
	 * 
	 * @param simulatorConfiguration
	 *            the configuration to apply to the server
	 * @return the server instance
	 */
	protected abstract SmppServerSimulator<M> initServer(SimulatorConfiguration simulatorConfiguration);

	protected abstract SubmitSm convert(M raw);
}
