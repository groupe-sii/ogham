package fr.sii.ogham.testing.extension.junit.sms;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.extension.Extension;

import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import fr.sii.ogham.testing.sms.simulator.SmppServerSimulator;
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm;
import fr.sii.ogham.testing.sms.simulator.config.SimulatorConfiguration;

/**
 * Base class for JUnit Rule and JUnit {@link Extension}.
 * 
 * @author Aurélien Baudet
 *
 * @param <M>
 *            The type of the received messages
 */
public abstract class AbstractJUnitSmppServerExt<M> {

	/**
	 * The configuration to control how server should behave
	 */
	protected final ServerConfig builder;
	/**
	 * The server simulator
	 */
	protected SmppServerSimulator<M> server;

	/**
	 * Initializes with the default configuration to use for the SMPP server
	 * 
	 */
	public AbstractJUnitSmppServerExt() {
		this(new ServerConfig());
	}

	/**
	 * Initializes with the configuration to use for the SMPP server
	 * 
	 * @param builder
	 *            the configuration for the server
	 */
	public AbstractJUnitSmppServerExt(ServerConfig builder) {
		super();
		this.builder = builder;
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