package fr.sii.notification.sms.sender.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.commons.charset.CharsetUtil;
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

import fr.sii.notification.core.exception.MessageException;
import fr.sii.notification.core.sender.AbstractSpecializedSender;
import fr.sii.notification.sms.message.Recipient;
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

	private static final int BODY_OFFSET = 6;
	
	/**
	 * Configuration to bind an SmppSession as an ESME to an SMSC.
	 */
	private SmppSessionConfiguration smppSessionConfiguration;
	
	/**
	 * Additional options
	 */
	private CloudhopperOptions options;

	public CloudhopperSMPPSender(SmppSessionConfiguration smppSessionConfiguration, CloudhopperOptions options) {
		super();
		this.smppSessionConfiguration = smppSessionConfiguration;
		this.options = options;
	}

	@Override
	public void send(Sms message) throws MessageException {
		DefaultSmppClient client = new DefaultSmppClient();
		SmppSession session = null;
		try {
			LOG.debug("Creating a new SMPP session...");
			session = client.bind(smppSessionConfiguration);
			LOG.info("SMPP session bounded");
			for (SubmitSm msg : createMessages(message)) {
				session.submit(msg, options.getResponseTimeout());
			}
		} catch (SmppInvalidArgumentException e) {
			throw new MessageException("Failed to create SMPP message", message, e);
		} catch (SmppTimeoutException | SmppChannelException | UnrecoverablePduException | InterruptedException | RecoverablePduException e) {
			throw new MessageException("Failed to initialize SMPP session", message, e);
		} finally {
			if (session != null) {
				session.unbind(options.getUnbindTimeout());
				session.close();
				session.destroy();
			}
			client.destroy();
		}
	}

	private List<SubmitSm> createMessages(Sms message) throws SmppInvalidArgumentException {
		List<SubmitSm> messages = new ArrayList<>();
		for (Recipient recipient : message.getRecipients()) {
			messages.addAll(createMessages(message, recipient));
		}
		return messages;
	}

	private List<SubmitSm> createMessages(Sms message, Recipient recipient) throws SmppInvalidArgumentException {
		List<SubmitSm> messages = new ArrayList<>();
		// TODO: use automatically the right charset ?
		byte[] textBytes = CharsetUtil.encode(message.getContent().toString(), CharsetUtil.CHARSET_ISO_8859_15);
		// generate new reference number
		byte[] referenceNumber = new byte[1];
		new Random().nextBytes(referenceNumber);
		// split message when too long
		byte[][] msgs = GsmUtil.createConcatenatedBinaryShortMessages(textBytes, referenceNumber[0]);
		if(msgs==null) {
			SubmitSm submit = createMessage(message, recipient, textBytes);
			LOG.debug("SubmitSm generated with content '{}'", new String(textBytes));
			messages.add(submit);
		} else {
			LOG.debug("Content split into {} parts", msgs.length);
			for (int i = 0; i < msgs.length; i++) {
				SubmitSm submit = createMessage(message, recipient, msgs[i]);
				LOG.debug("SubmitSm generated with content '{}'", new String(Arrays.copyOfRange(msgs[i], BODY_OFFSET, msgs[i].length)));
				submit.setEsmClass(SmppConstants.ESM_CLASS_UDHI_MASK);
				messages.add(submit);
			}
		}
		return messages;
	}

	private SubmitSm createMessage(Sms message, Recipient recipient, byte[] content) throws SmppInvalidArgumentException {
		SubmitSm submit = new SubmitSm();
		// TODO: set TON and NPI according to numbers
		submit.setSourceAddress(new Address(SmppConstants.TON_INTERNATIONAL, SmppConstants.NPI_E164, message.getFrom().getPhoneNumber().getNumber()));
		submit.setDestAddress(new Address(SmppConstants.TON_INTERNATIONAL, SmppConstants.NPI_E164, recipient.getPhoneNumber().getNumber()));
		// TODO: should be parameterized
		submit.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
		submit.setShortMessage(content);
		return submit;
	}

	@Override
	public String toString() {
		return "CloudhopperSMPPSender";
	}
}
