package fr.sii.ogham.sms.sender.impl.cloudhopper.preparator;

import java.util.List;

import com.cloudhopper.smpp.pdu.SubmitSm;

import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.MessagePreparationException;

/**
 * Prepare the messages and generates a list of {@link SubmitSm} to send.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public interface MessagePreparator {
	/**
	 * Converts Ogham {@link Sms} message to {@link SubmitSm} messages.
	 * 
	 * The original Ogham message may be split into several segments.
	 * 
	 * @param message
	 *            the original message
	 * @return the list of generated messages
	 * @throws MessagePreparationException
	 *             when message could not be converted to list of
	 *             {@link SubmitSm}
	 */
	List<SubmitSm> prepareMessages(Sms message) throws MessagePreparationException;
}
