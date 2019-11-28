package fr.sii.ogham.sms.sender.impl.cloudhopper.preparator;

import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;

import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.MessagePreparationException;
import fr.sii.ogham.sms.splitter.MessageSplitter;
import fr.sii.ogham.sms.splitter.Segment;

/**
 * This preparator creates {@link SubmitSm}s and places the message into the
 * {@code short_message} field.
 * 
 * <p>
 * This preparator detects which charset should be used to encode message string
 * and splits it if needed. It also converts {@link PhoneNumber} to
 * {@link AddressedPhoneNumber}.
 * 
 * @author Aurélien Baudet
 *
 */
public class ShortMessagePreparator extends BaseMessagePreparator {

	/**
	 * The preparator can split messages and provide data coding information.
	 * 
	 * <p>
	 * The phone number won't be translated meaning that if phone number is not
	 * already converted to {@link AddressedPhoneNumber} then an
	 * {@link IllegalStateException} is thrown.
	 * 
	 * @param messageSplitter
	 *            Split message in several parts if needed
	 * @param dataCodingProvider
	 *            Determines the data coding to use according to encoding
	 */
	public ShortMessagePreparator(MessageSplitter messageSplitter, DataCodingProvider dataCodingProvider) {
		super(messageSplitter, dataCodingProvider);
	}

	/**
	 * Initializes the preparator with message splitter, data coding provider
	 * and phone number translator.
	 * 
	 * @param messageSplitter
	 *            Split message in several parts if needed
	 * @param dataCodingProvider
	 *            Determines the data coding to use according to encoding
	 * @param phoneNumberTranslator
	 *            Fallback phone translator to handle addressing policy
	 */
	public ShortMessagePreparator(MessageSplitter messageSplitter, DataCodingProvider dataCodingProvider, PhoneNumberTranslator phoneNumberTranslator) {
		super(messageSplitter, dataCodingProvider, phoneNumberTranslator);
	}

	@Override
	protected void fill(Sms originalMessage, SubmitSm submit, Segment part) throws MessagePreparationException {
		try {
			submit.setShortMessage(part.getBytes());
		} catch (SmppInvalidArgumentException e) {
			throw new MessagePreparationException("Failed to set 'short_message' field", originalMessage, e);
		}
	}
	
}
