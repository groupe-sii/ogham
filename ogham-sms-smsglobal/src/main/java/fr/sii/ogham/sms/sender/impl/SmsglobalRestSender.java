package fr.sii.ogham.sms.sender.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.sender.AbstractSpecializedSender;
import fr.sii.ogham.sms.message.Sms;

/**
 * Implementation that sends a HTTP REST request on the <a
 * href="http://www.smsglobal.com/rest-api/">smsglobal REST API</a> .
 * 
 * @author Aurélien Baudet
 */
public class SmsglobalRestSender extends AbstractSpecializedSender<Sms> {
	private static final Logger LOG = LoggerFactory.getLogger(SmsglobalRestSender.class);

	@Override
	public void send(Sms message) throws MessageException {
		// TODO: implement Smsglobal using HTTP API
	}

	@Override
	public String toString() {
		return "SmsglobalRestSender";
	}
}
