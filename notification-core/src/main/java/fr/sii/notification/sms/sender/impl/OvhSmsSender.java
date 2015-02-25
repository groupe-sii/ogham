package fr.sii.notification.sms.sender.impl;

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.sender.AbstractSpecializedSender;
import fr.sii.notification.sms.message.Sms;

public class OvhSmsSender extends AbstractSpecializedSender<Sms> {

	@Override
	public void send(Sms message) throws MessageException {
		// TODO Auto-generated method stub
		System.out.println("TODO implement ovh "+message);
	}

}
