package fr.sii.ogham.testing.extension.junit.sms;

import org.jsmpp.bean.SubmitSm;

import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import fr.sii.ogham.testing.sms.simulator.SmppServerSimulator;
import fr.sii.ogham.testing.sms.simulator.config.SimulatorConfiguration;
import fr.sii.ogham.testing.sms.simulator.jsmpp.JSMPPServer;
import fr.sii.ogham.testing.sms.simulator.jsmpp.SubmitSmAdapter;

/**
 * JUnit extension that start a SMPP server based on JSMPP implementation.
 * 
 * @author Aurélien Baudet
 *
 */
public class JsmppServerExtension extends SmppServerExtension<SubmitSm> {

	/**
	 * Initialize the server with default configuration (random port).
	 * 
	 */
	public JsmppServerExtension() {
		super();
	}

	/**
	 * Initialize the server with provided configuration.
	 * 
	 * @param config
	 *            the server configuration to simulate some behavior
	 */
	public JsmppServerExtension(ServerConfig config) {
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
