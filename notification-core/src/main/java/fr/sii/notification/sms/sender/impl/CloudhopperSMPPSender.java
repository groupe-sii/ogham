package fr.sii.notification.sms.sender.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.sender.AbstractSpecializedSender;
import fr.sii.notification.sms.message.Sms;

/**
 * Implementation based on <a
 * href="https://github.com/twitter/cloudhopper-smpp">cloudhopper-smpp</a>
 * library.
 * 
 * @author Aurélien Baudet
 */
public class CloudhopperSMPPSender extends AbstractSpecializedSender<Sms> {
	private static final Logger LOG = LoggerFactory.getLogger(CloudhopperSMPPSender.class);

	@Override
	public void send(Sms message) throws MessageException {
		
	}

	@Override
	public String toString() {
		return "CloudhopperSMPPSender";
	}
}
