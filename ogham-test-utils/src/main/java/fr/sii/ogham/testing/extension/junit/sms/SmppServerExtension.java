package fr.sii.ogham.testing.extension.junit.sms;

import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import fr.sii.ogham.testing.extension.junit.sms.config.SmppServerConfig;
import fr.sii.ogham.testing.sms.simulator.SmppServerException;
import fr.sii.ogham.testing.sms.simulator.SmppServerSimulator;
import fr.sii.ogham.testing.sms.simulator.SmppServerStartException;
import fr.sii.ogham.testing.sms.simulator.config.SimulatorConfiguration;
import ogham.testing.io.github.resilience4j.retry.Retry;
import ogham.testing.io.github.resilience4j.retry.RetryConfig;
import ogham.testing.io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public abstract class SmppServerExtension<M> extends AbstractJUnitSmppServerExt<M> implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
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
		RetryConfig retryConfig = builder.build().getStartRetryConfig();
		Retry retry = RetryRegistry.of(retryConfig).retry("SmppServer.start()");
		retry.getEventPublisher().onRetry((event) -> {
			LOG.info("SMPP server failed to start on port {}. Retrying...", getPort());
		});
		try {
			retry.<Void>executeCheckedSupplier(() -> {
				start(context);
				return null;
			});
		} catch (Throwable e) {
			throw new SmppServerStartException("Failed to start SMPP server", e);
		}
	}

	private void start(ExtensionContext context) throws SmppServerException {
		SmppServerConfig config = AnnotationSupport.findAnnotation(context.getElement(), SmppServerConfig.class).orElse(null);
		SimulatorConfiguration serverConfig = builder.annotationConfig(config).build();
		server = initServer(serverConfig);
		LOG.info("starting SMPP server on port {}...", getPort());
		server.start();
		LOG.info("SMPP server started on port {}", getPort());
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		stop();
	}

	private void stop() throws SmppServerException {
		LOG.info("stopping SMPP server...");
		server.stop();
		LOG.info("SMPP server stopped");
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return isSmppServerSimulatorParam(parameterContext) || isSmppServerExtensionParam(parameterContext);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		if (isSmppServerSimulatorParam(parameterContext)) {
			return server;
		}
		if (isSmppServerExtensionParam(parameterContext)) {
			return this;
		}
		return null;
	}


	private boolean isSmppServerSimulatorParam(ParameterContext parameterContext) {
		return SmppServerSimulator.class.isAssignableFrom(parameterContext.getParameter().getType());
	}

	private boolean isSmppServerExtensionParam(ParameterContext parameterContext) {
		return SmppServerExtension.class.isAssignableFrom(parameterContext.getParameter().getType());
	}
}
