package fr.sii.notification.core.filler;

import fr.sii.notification.core.exception.filler.FillMessageException;
import fr.sii.notification.core.message.Message;

/**
 * Interface that declares a filler for messages. A filler will add some extra
 * information to the provided message.
 * 
 * @author Aurélien Baudet
 *
 */
public interface MessageFiller {
	/**
	 * Add extra information on the message.
	 * 
	 * @param message
	 *            The message that to fill
	 * @throws FillMessageException
	 *             when the message couldn't be filled
	 */
	public void fill(Message message) throws FillMessageException;
}
