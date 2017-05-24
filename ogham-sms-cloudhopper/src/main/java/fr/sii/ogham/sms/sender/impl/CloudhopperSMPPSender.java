package fr.sii.ogham.sms.sender.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.commons.gsm.GsmUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.retry.RetryExecutor;
import fr.sii.ogham.core.sender.AbstractSpecializedSender;
import fr.sii.ogham.sms.exception.message.EncodingException;
import fr.sii.ogham.sms.exception.message.PhoneNumberTranslatorException;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.Recipient;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.message.addressing.AddressedPhoneNumber;
import fr.sii.ogham.sms.message.addressing.translator.PhoneNumberTranslator;
import fr.sii.ogham.sms.sender.impl.cloudhopper.CloudhopperCharsetHandler;
import fr.sii.ogham.sms.sender.impl.cloudhopper.CloudhopperOptions;


/**
 * Implementation based on <a
 * href="https://github.com/twitter/cloudhopper-smpp">cloudhopper-smpp</a>
 * library.
 * 
 * @author Aurélien Baudet
 */
public class CloudhopperSMPPSender extends AbstractSpecializedSender<Sms> {
	private static final Logger LOG = LoggerFactory.getLogger(CloudhopperSMPPSender.class);

	private static final int BODY_OFFSET = 6;

	/** Configuration to bind an SmppSession as an ESME to an SMSC. */
	private final SmppSessionConfiguration smppSessionConfiguration;

	/** Additional options. */
	private final CloudhopperOptions options;

	/** Random seed to generate reference number in case of split messages. */
	private final Random splitMessagesReferenceGenerator = new Random();

	/**
	 * This phone number translator will handle the fallback addressing policy
	 * (TON / NPI).
	 */
	private PhoneNumberTranslator fallBackPhoneNumberTranslator;

	/**
	 * Handle sms charset detection.
	 */
	private final CloudhopperCharsetHandler charsetHandler;

	/**
	 * Initializes a CloudhopperSMPPSender with SMPP session configuration, some
	 * options and a default phone translator to handle addressing policy.
	 * 
	 * @param smppSessionConfiguration
	 *            SMPP session configuration
	 * @param options
	 *            Dedicated CloudHopper options
	 * @param charsetHandler
	 *            Handles charset detection for messages content
	 */
	public CloudhopperSMPPSender(SmppSessionConfiguration smppSessionConfiguration, CloudhopperOptions options, CloudhopperCharsetHandler charsetHandler) {
		super();
		this.smppSessionConfiguration = smppSessionConfiguration;
		this.options = options;
		this.charsetHandler = charsetHandler;
	}

	/**
	 * Initializes a CloudhopperSMPPSender with SMPP session configuration, some
	 * options and a default phone translator to handle addressing policy.
	 * 
	 * @param smppSessionConfiguration
	 *            SMPP session configuration
	 * @param options
	 *            Dedicated CloudHopper options
	 * @param charsetHandler
	 *            Handler that is able to provide a charset for the provided
	 *            message
	 * @param phoneNumberTranslator
	 *            Fallback phone translator to handle addressing policy
	 */
	public CloudhopperSMPPSender(SmppSessionConfiguration smppSessionConfiguration, CloudhopperOptions options, CloudhopperCharsetHandler charsetHandler, PhoneNumberTranslator phoneNumberTranslator) {
		this(smppSessionConfiguration, options, charsetHandler);

		this.fallBackPhoneNumberTranslator = phoneNumberTranslator;
	}

	@Override
	public void send(Sms message) throws MessageException {
		DefaultSmppClient client = new DefaultSmppClient();
		SmppSession session = null;
		try {
			LOG.debug("Creating a new SMPP session...");
			session = connect(client);
			LOG.info("SMPP session bounded");
			send(message, session);
		} catch (PhoneNumberTranslatorException | EncodingException e) {
			throw new MessageException("Failed to create SMPP message", message, e);
		} catch (SmppTimeoutException | SmppChannelException | UnrecoverablePduException | InterruptedException | RecoverablePduException e) {
			throw new MessageException("Failed to initialize SMPP session", message, e);
		} catch (Exception e) {
			throw new MessageException("Failed to initialize SMPP session after maximum retries reached", message, e);
		} finally {
			if (session != null) {
				session.unbind(options.getUnbindTimeout());
				session.close();
				session.destroy();
			}
			client.destroy();
		}
	}

	private void send(Sms message, SmppSession session) throws MessageException, PhoneNumberTranslatorException, EncodingException {
		try {
			for (SubmitSm msg : createMessages(message)) {
				session.submit(msg, options.getResponseTimeout());
			}
		} catch(RecoverablePduException | UnrecoverablePduException | SmppTimeoutException | SmppChannelException | InterruptedException e) {
			throw new MessageException("Failed to send SMPP message", message, e);
		}
	}

	private SmppSession connect(final DefaultSmppClient client) throws Exception {
		RetryExecutor retry = options.getConnectRetry();
		return retry.execute(new Callable<SmppSession>() {
			@Override
			public SmppSession call() throws Exception {
				return client.bind(smppSessionConfiguration);
			}
		});
	}

	private List<SubmitSm> createMessages(Sms message) throws SmppInvalidArgumentException, PhoneNumberTranslatorException, EncodingException {
		List<SubmitSm> messages = new ArrayList<>();
		for (Recipient recipient : message.getRecipients()) {
			messages.addAll(createMessages(message, recipient));
		}
		return messages;
	}

	private List<SubmitSm> createMessages(Sms message, Recipient recipient) throws SmppInvalidArgumentException, PhoneNumberTranslatorException, EncodingException {
		List<SubmitSm> messages = new ArrayList<>();
		byte[] textBytes = charsetHandler.encode(message.getContent().toString());

		// generate new reference number
		byte[] referenceNumber = new byte[1];
		splitMessagesReferenceGenerator.nextBytes(referenceNumber);

		// split message when too long
		byte[][] msgs = GsmUtil.createConcatenatedBinaryShortMessages(textBytes, referenceNumber[0]);
		if (msgs == null) {
			addSubmit(message, recipient, messages, textBytes);
		} else {
			LOG.debug("Content split into {} parts", msgs.length);
			for (int i = 0; i < msgs.length; i++) {
				addEsmClassSubmit(message, recipient, messages, msgs, i);
			}
		}
		return messages;
	}

	private void addEsmClassSubmit(Sms message, Recipient recipient, List<SubmitSm> messages, byte[][] msgs, int i) throws SmppInvalidArgumentException, PhoneNumberTranslatorException {
		SubmitSm submit = createMessage(message, recipient, msgs[i]);
		if(LOG.isDebugEnabled()) {
			LOG.debug("SubmitSm generated with content '{}'", new String(Arrays.copyOfRange(msgs[i], BODY_OFFSET, msgs[i].length)));
		}
		submit.setEsmClass(SmppConstants.ESM_CLASS_UDHI_MASK);
		messages.add(submit);
	}

	private void addSubmit(Sms message, Recipient recipient, List<SubmitSm> messages, byte[] textBytes) throws SmppInvalidArgumentException, PhoneNumberTranslatorException {
		SubmitSm submit = createMessage(message, recipient, textBytes);
		if(LOG.isDebugEnabled()) {
			LOG.debug("SubmitSm generated with content '{}'", new String(textBytes));
		}
		messages.add(submit);
	}

	private SubmitSm createMessage(Sms message, Recipient recipient, byte[] content) throws SmppInvalidArgumentException, PhoneNumberTranslatorException {
		SubmitSm submit = new SubmitSm();
		submit.setSourceAddress(toAddress(message.getFrom().getPhoneNumber()));
		submit.setDestAddress(toAddress(recipient.getPhoneNumber()));

		// TODO: should be configurable ?
		submit.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
		submit.setShortMessage(content);
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
		} else if (fallBackPhoneNumberTranslator != null) {
			LOG.warn("Fallback addressing policy used for PhoneNumber '{}'. You might decorate your sender with a PhoneNumberTranslatorSender.", phoneNumber);
			addressedPhoneNumber = fallBackPhoneNumberTranslator.translate(phoneNumber);

		} else {
			throw new IllegalStateException("Must provide addressing policy with the phone number or with a fallback phone number translator.");
		}
		LOG.debug("Addressing policy applied on {} ", addressedPhoneNumber);
		return new Address(addressedPhoneNumber.getTon().value(), addressedPhoneNumber.getNpi().value(), addressedPhoneNumber.getNumber());
	}

	@Override
	public String toString() {
		return "CloudhopperSMPPSender";
	}
}
