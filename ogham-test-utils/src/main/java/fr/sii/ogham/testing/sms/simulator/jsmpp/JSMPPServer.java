package fr.sii.ogham.testing.sms.simulator.jsmpp;

import java.util.List;

import org.jsmpp.bean.SubmitSm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.testing.sms.simulator.SmppServerException;
import fr.sii.ogham.testing.sms.simulator.SmppServerSimulator;
import fr.sii.ogham.testing.sms.simulator.config.SimulatorConfiguration;

/**
 * The server simulator based on <a href="http://jsmpp.org/">jsmpp</a> samples.
 */
public class JSMPPServer implements SmppServerSimulator<SubmitSm> {
	private static final Logger LOG = LoggerFactory.getLogger(JSMPPServer.class);

	private Thread thread;
	private final JSMPPServerSimulator simulator;

	/**
	 * Initializes with provided configuration.
	 * 
	 * @param config
	 *            the server configuration
	 */
	public JSMPPServer(SimulatorConfiguration config) {
		super();
		if (config.getPortProvider() == null) {
			throw new IllegalArgumentException("Port configuration is mandatory");
		}
		simulator = new JSMPPServerSimulator(config.getPortProvider().getPort(), config);
	}

	@Override
	public synchronized void start() throws SmppServerException {
		try {
			LOG.debug("starting simulator thread...");
			simulator.reset();
			thread = new Thread(simulator);
			thread.start();
			simulator.waitTillRunning(5000L);
			LOG.debug("simulator started");
		} catch (ServerStartupException e) {
			throw new SmppServerException("Failed to start JSMPP server", e);
		}
	}

	@Override
	public synchronized void stop() throws SmppServerException {
		try {
			LOG.debug("stopping simulator thread...");
			simulator.stop();
			thread.interrupt();
			thread.join();
			LOG.debug("simulator stopped");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new SmppServerException("Failed to stop JSMPP server", e);
		}
	}

	@Override
	public int getPort() {
		return simulator.getPort();
	}

	@Override
	public List<SubmitSm> getReceivedMessages() {
		return simulator.getReceivedMessages();
	}

}
