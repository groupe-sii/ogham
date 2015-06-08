package fr.sii.notification.sms.sender.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

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
		try {
			DefaultSmppClient client = new DefaultSmppClient();
			SmppSession session = client.bind(getSmppSessionConfiguration());
			SubmitSm submit = createMessage(message);
			final SubmitSmResp submit1 = session.submit(submit, 10000);
			session.unbind(10000);
			session.close();
			session.destroy();
		} catch (SmppInvalidArgumentException e) {
			throw new MessageException("Failed to create SMPP message", message, e);
		} catch (SmppTimeoutException | SmppChannelException | UnrecoverablePduException | InterruptedException | RecoverablePduException e) {
			throw new MessageException("Failed to initialize SMPP session", message, e);
		}
	}

	private SubmitSm createMessage(Sms message) throws SmppInvalidArgumentException {
		SubmitSm submit = new SubmitSm();
		// TODO: set TON and NPI according to numbers
		// TODO: externalize TON and NPI values to enum (or use existing one if any) ?
		submit.setSourceAddress(new Address((byte) 0x01, (byte) 0x01, message.getFrom().getPhoneNumber().getNumber()));
		submit.setDestAddress(new Address((byte) 0x01, (byte) 0x01, message.getRecipients().get(0).getPhoneNumber().getNumber()));
		// TODO: should be parameterized
		submit.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
		// TODO: split message when too long ?
		// TODO: use automatically the right charset ?
		submit.setShortMessage(CharsetUtil.encode(message.getContent().toString(), CharsetUtil.CHARSET_GSM));
		return submit;
	}

	private static SmppSessionConfiguration getSmppSessionConfiguration() {
		// TODO: externalize configuration (properties)
		SmppSessionConfiguration config = new SmppSessionConfiguration();
		config.setWindowSize(5);
		config.setName("Tester.Session.");
		config.setType(SmppBindType.TRANSCEIVER);
		config.setHost("127.0.0.1");
		config.setPort(8056);
		config.setConnectTimeout(10000);
		config.setSystemId("systemId");
		config.setPassword("password");
		config.getLoggingOptions().setLogBytes(false);
		// to enable monitoring (request expiration)

		config.setRequestExpiryTimeout(30000);
		config.setWindowMonitorInterval(15000);

		config.setCountersEnabled(false);
		return config;
	}

	@Override
	public String toString() {
		return "CloudhopperSMPPSender";
	}
}
