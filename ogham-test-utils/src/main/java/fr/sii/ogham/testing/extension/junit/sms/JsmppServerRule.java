package fr.sii.ogham.testing.extension.junit.sms;

import ogham.testing.org.jsmpp.bean.SubmitSm;

import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import fr.sii.ogham.testing.sms.simulator.SmppServerSimulator;
import fr.sii.ogham.testing.sms.simulator.config.SimulatorConfiguration;
import fr.sii.ogham.testing.sms.simulator.jsmpp.JSMPPServer;
import fr.sii.ogham.testing.sms.simulator.jsmpp.SubmitSmAdapter;

/**
 * JUnit rule that start a SMPP server based on JSMPP implementation.
 * 
 * @author Aurélien Baudet
 *
 */
public class JsmppServerRule extends SmppServerRule<SubmitSm> {
	/**
	 * Initialize the server with random port.
	 */
	public JsmppServerRule() {
		this(new ServerConfig());
	}

	/**
	 * Initialize the server with the provided port.
	 * 
	 * @param port
	 *            the port used by the server
	 */
	public JsmppServerRule(int port) {
		this(new ServerConfig().port(port));
	}

	/**
	 * Initialize the server with provided configuration.
	 * 
	 * @param config
	 *            the server configuration to simulate some behavior
	 */
	public JsmppServerRule(ServerConfig config) {
		super(config);
	}

	@Override
	protected SmppServerSimulator<SubmitSm> initServer(SimulatorConfiguration config) {
		return new JSMPPServer(config);
	}

	@Override
	protected fr.sii.ogham.testing.sms.simulator.bean.SubmitSm convert(SubmitSm raw) {
		return new SubmitSmAdapter(raw);
	}

}
