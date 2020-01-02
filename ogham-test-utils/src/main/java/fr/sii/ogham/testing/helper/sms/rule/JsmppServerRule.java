package fr.sii.ogham.testing.helper.sms.rule;

import org.jsmpp.bean.SubmitSm;

import fr.sii.ogham.testing.helper.sms.jsmpp.JSMPPServer;
import fr.sii.ogham.testing.helper.sms.rule.config.ServerConfig;
import fr.sii.ogham.testing.helper.sms.rule.config.SimulatorConfiguration;

/**
 * JUnit rule that start a SMPP server based on JSMPP implementation.
 * 
 * @author Aurélien Baudet
 *
 */
public class JsmppServerRule extends SmppServerRule<SubmitSm> {
	/**
	 * Initialize the server with the default port
	 * ({@link SmppServerRule#DEFAULT_PORT}).
	 */
	public JsmppServerRule() {
		this(SmppServerRule.DEFAULT_PORT);
	}

	/**
	 * Initialize the server with the provided port.
	 * 
	 * @param port
	 *            the port used by the server
	 */
	public JsmppServerRule(int port) {
		this(port, new ServerConfig());
	}

	/**
	 * Initialize the server with the default port
	 * ({@link SmppServerRule#DEFAULT_PORT}).
	 * 
	 * @param config
	 *            the server configuration to simulate some behavior
	 */
	public JsmppServerRule(ServerConfig config) {
		this(SmppServerRule.DEFAULT_PORT, config);
	}

	/**
	 * Initialize the server with the provided port.
	 * 
	 * @param port
	 *            the port used by the server
	 * @param config
	 *            the server configuration to simulate some behavior
	 */
	public JsmppServerRule(int port, ServerConfig config) {
		super(port, config);
	}

	@Override
	protected SmppServerSimulator<SubmitSm> initServer(SimulatorConfiguration config) {
		return new JSMPPServer(getPort(), config);
	}

}
