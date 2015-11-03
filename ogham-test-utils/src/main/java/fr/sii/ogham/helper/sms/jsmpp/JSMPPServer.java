package fr.sii.ogham.helper.sms.jsmpp;

import java.util.List;

import org.jsmpp.bean.SubmitSm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.helper.sms.rule.SmppServerException;
import fr.sii.ogham.helper.sms.rule.SmppServerSimulator;

/**
 * The server simulator based on <a href="http://jsmpp.org/">jsmpp</a> samples.
 */
public class JSMPPServer implements SmppServerSimulator<SubmitSm> {
	private static final Logger LOG = LoggerFactory.getLogger(JSMPPServer.class);

	private Thread thread;

	private final JSMPPServerSimulator simulator;

	public JSMPPServer(int port) {
		super();
		simulator = new JSMPPServerSimulator(port);
	}

	@Override
	public synchronized void start() throws SmppServerException {
		try {
			LOG.debug("starting simulator thread...");
			simulator.reset();
			thread = new Thread(simulator);
			thread.start();
			simulator.waitTillRunning(5000L);
		} catch (InterruptedException e) {
			throw new SmppServerException("Failed to start JSMPPServer", e);
		}
		LOG.debug("simulator started");
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
