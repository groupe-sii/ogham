package fr.sii.ogham.sms.sender.impl.cloudhopper.preparator;

import static com.cloudhopper.smpp.SmppConstants.ESM_CLASS_UDHI_MASK;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;

import fr.sii.ogham.sms.encoder.Encoded;
import fr.sii.ogham.sms.exception.message.PhoneNumberTranslatorException;
import fr.sii.ogham.sms.exception.message.SplitMessageException;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.Recipient;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.DataCodingException;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.MessagePreparationException;
import fr.sii.ogham.sms.splitter.EncodedSegment;
import fr.sii.ogham.sms.splitter.MessageSplitter;
import fr.sii.ogham.sms.splitter.Segment;

/**
 * Base preparator that creates {@link SubmitSm}s.
 * 
 * <p>
 * The message content is not set. It lets implementations implement
 * {@code fill(SubmitSm, Segment)} method to set the content.
 * 
 * <p>
 * This preparator detects which charset should be used to encode message string
 * and splits it if needed. It also converts {@link PhoneNumber} to
 * {@link AddressedPhoneNumber}.
 * 
 * @author Aurélien Baudet
 *
 */
public abstract class BaseMessagePreparator implements MessagePreparator {
	private static final Logger LOG = LoggerFactory.getLogger(BaseMessagePreparator.class);

	/**
	 * Split message if needed into several parts
	 */
	private final MessageSplitter messageSplitter;

	/**
	 * Determines the data coding to use according to encoding
	 */
	private final DataCodingProvider dataCodingProvider;

	/**
	 * This phone number translator will handle the fallback addressing policy
	 * (TON / NPI).
	 */
	private final PhoneNumberTranslator phoneNumberTranslator;

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
	public BaseMessagePreparator(MessageSplitter messageSplitter, DataCodingProvider dataCodingProvider) {
		this(messageSplitter, dataCodingProvider, null);
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
	public BaseMessagePreparator(MessageSplitter messageSplitter, DataCodingProvider dataCodingProvider, PhoneNumberTranslator phoneNumberTranslator) {
		super();
		this.messageSplitter = messageSplitter;
		this.dataCodingProvider = dataCodingProvider;
		this.phoneNumberTranslator = phoneNumberTranslator;
	}

	@Override
	public List<SubmitSm> prepareMessages(Sms message) throws MessagePreparationException {
		try {
			return createMessages(message);
		} catch (PhoneNumberTranslatorException | SmppInvalidArgumentException | DataCodingException e) {
			LOG.error("Failed to prepare messages", e);
			throw new MessagePreparationException("Failed to prepare messages", message, e);
		} catch (SplitMessageException e) {
			LOG.error("Failed to split SMPP message before sending it", e);
			throw new MessagePreparationException("Failed to split SMPP message before sending it", message, e);
		}
	}

	/**
	 * Fill the {@link SubmitSm} with the message content.
	 * 
	 * @param originalMessage
	 *            the SMS that is about to be sent
	 * @param submit
	 *            the submit to fill
	 * @param part
	 *            the message content
	 * @throws MessagePreparationException
	 *             when message couldn't be prepared correctly
	 */
	protected abstract void fill(Sms originalMessage, SubmitSm submit, Segment part) throws MessagePreparationException;

	private List<SubmitSm> createMessages(Sms message) throws SmppInvalidArgumentException, PhoneNumberTranslatorException, SplitMessageException, DataCodingException, MessagePreparationException {
		List<SubmitSm> messages = new ArrayList<>();
		for (Recipient recipient : message.getRecipients()) {
			messages.addAll(createMessages(message, recipient));
		}
		return messages;
	}

	private List<SubmitSm> createMessages(Sms message, Recipient recipient)
			throws SmppInvalidArgumentException, PhoneNumberTranslatorException, SplitMessageException, DataCodingException, MessagePreparationException {
		List<SubmitSm> messages = new ArrayList<>();

		List<Segment> parts = messageSplitter.split(message.getContent().toString());
		if (parts.size() <= 1) {
			addSubmit(message, recipient, messages, parts.get(0));
			return messages;
		}

		LOG.debug("Content split into {} parts", parts.size());
		for (Segment part : parts) {
			addEsmClassSubmit(message, recipient, messages, part);
		}
		return messages;
	}

	private void addEsmClassSubmit(Sms message, Recipient recipient, List<SubmitSm> messages, Segment part)
			throws SmppInvalidArgumentException, PhoneNumberTranslatorException, DataCodingException, MessagePreparationException {
		SubmitSm submit = createMessage(message, recipient, part);
		submit.setEsmClass(ESM_CLASS_UDHI_MASK);
		messages.add(submit);
	}

	private void addSubmit(Sms message, Recipient recipient, List<SubmitSm> messages, Segment part)
			throws SmppInvalidArgumentException, PhoneNumberTranslatorException, DataCodingException, MessagePreparationException {
		SubmitSm submit = createMessage(message, recipient, part);
		messages.add(submit);
	}

	private SubmitSm createMessage(Sms message, Recipient recipient, Segment part)
			throws SmppInvalidArgumentException, PhoneNumberTranslatorException, DataCodingException, MessagePreparationException {
		SubmitSm submit = new SubmitSm();
		submit.setSourceAddress(toAddress(message.getFrom().getPhoneNumber()));
		submit.setDestAddress(toAddress(recipient.getPhoneNumber()));

		if (part instanceof EncodedSegment) {
			Encoded encoded = ((EncodedSegment) part).getEncoded();
			submit.setDataCoding(dataCodingProvider.provide(encoded).getByteValue());
		}

		fill(message, submit, part);

		return submit;
	}

	/**
	 * Transforms a {@link PhoneNumber} in a {@link Address} type.
	 * 
	 * @param phoneNumber
	 *            The given phone number
	 * @return corresponding address with number, TON and NPI
	 * @throws PhoneNumberTranslatorException
	 *             If an error occurs during fallback phone number translation
	 */
	private Address toAddress(PhoneNumber phoneNumber) throws PhoneNumberTranslatorException {
		AddressedPhoneNumber addressedPhoneNumber;

		if (phoneNumber instanceof AddressedPhoneNumber) {
			addressedPhoneNumber = (AddressedPhoneNumber) phoneNumber;
		} else if (phoneNumberTranslator != null) {
			LOG.warn("Fallback addressing policy used for PhoneNumber '{}'. You might decorate your sender with a PhoneNumberTranslatorSender.", phoneNumber);
			addressedPhoneNumber = phoneNumberTranslator.translate(phoneNumber);
		} else {
			throw new IllegalStateException("Must provide addressing policy with the phone number or with a fallback phone number translator.");
		}
		LOG.debug("Addressing policy applied on {} ", addressedPhoneNumber);
		return new Address(addressedPhoneNumber.getTon().value(), addressedPhoneNumber.getNpi().value(), addressedPhoneNumber.getNumber());
	}

}
