package fr.sii.ogham.sms.sender.impl.cloudhopper.exception;

import static fr.sii.ogham.core.CoreConstants.SERIAL_VERSION_UID;

import com.cloudhopper.smpp.pdu.SubmitSm;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.MessagePreparator;

/**
 * The {@link Sms} message has to be converted to a {@link SubmitSm} PDU in
 * order to be sent by Cloudhopper. The conversion is done by a
 * {@link MessagePreparator}. The preparation of the message may fail for any
 * reason.
 * 
 * @author Aurélien Baudet
 *
 */
public class MessagePreparationException extends MessageException {
	private static final long serialVersionUID = SERIAL_VERSION_UID;

	public MessagePreparationException(String message, Message msg, Throwable cause) {
		super(message, msg, cause);
	}

	public MessagePreparationException(String message, Message msg) {
		super(message, msg);
	}

	public MessagePreparationException(Throwable cause, Message msg) {
		super(cause, msg);
	}

}
