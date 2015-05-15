package fr.sii.notification.core.filler;

import fr.sii.notification.core.exception.filler.FillMessageException;
import fr.sii.notification.core.message.Message;

public interface MessageFiller {
	public void fill(Message message) throws FillMessageException;
}
