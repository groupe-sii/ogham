package fr.sii.ogham.helper.sms.opensmpp;

import org.smpp.pdu.SubmitSM;

import fr.sii.ogham.helper.sms.rule.SmppServerRule;

public class OpensmppSimulatorRule extends SmppServerRule<SubmitSM> {

	public OpensmppSimulatorRule(int port) {
		super(new OpensmppServer(port));
	}

	public OpensmppSimulatorRule() {
		this(SmppServerRule.DEFAULT_PORT);
	}
}
