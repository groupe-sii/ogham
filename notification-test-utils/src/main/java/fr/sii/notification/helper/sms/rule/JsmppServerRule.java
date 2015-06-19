package fr.sii.notification.helper.sms.rule;

import org.jsmpp.bean.SubmitSm;

import fr.sii.notification.helper.sms.jsmpp.JSMPPServer;

public class JsmppServerRule extends SmppServerRule<SubmitSm> {

	/**
	 * Initialize the server with the provided port.
	 * 
	 * @param port
	 *            the port used by the server
	 */
	public JsmppServerRule(int port) {
		super(new JSMPPServer(port));
	}

	/**
	 * Initialize the server with the default port.
	 */
	public JsmppServerRule() {
		this(SmppServerRule.DEFAULT_PORT);
	}


}
