package fr.sii.notification.sms.sender.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.sender.AbstractSpecializedSender;
import fr.sii.notification.sms.SmsConstants;
import fr.sii.notification.sms.message.Sms;

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

	}

	@Override
	public String toString() {
		return "SmsglobalRestSender";
	}
}
