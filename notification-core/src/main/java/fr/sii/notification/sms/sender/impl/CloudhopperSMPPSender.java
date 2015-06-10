package fr.sii.notification.sms.sender.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.gsm.GsmUtil;
import com.cloudhopper.smpp.SmppBindType;
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
import fr.sii.notification.sms.SmsConstants;
import fr.sii.notification.sms.SmsConstants.SmppConstants.CloudhopperConstants;
import fr.sii.notification.sms.SmsConstants.SmppConstants.TimeoutConstants;
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
	 * Properties that is used to initialize the session
	 */
	private Properties properties;

	public CloudhopperSMPPSender(Properties properties) {
		super();
		this.properties = properties;
	}

	@Override
	public void send(Sms message) throws MessageException {
		DefaultSmppClient client = new DefaultSmppClient();
		SmppSession session = null;
		try {
			LOG.debug("Creating a new SMPP session...");
			session = client.bind(getSmppSessionConfiguration());
			LOG.info("SMPP session bounded");
			for (SubmitSm msg : createMessages(message)) {
				session.submit(msg, Integer.parseInt(properties.getProperty(CloudhopperConstants.RESPONSE_TIMEOUT_PROPERTY, String.valueOf(CloudhopperConstants.DEFAULT_RESPONSE_TIMEOUT))));
			}
		} catch (SmppInvalidArgumentException e) {
			throw new MessageException("Failed to create SMPP message", message, e);
		} catch (SmppTimeoutException | SmppChannelException | UnrecoverablePduException | InterruptedException | RecoverablePduException e) {
			throw new MessageException("Failed to initialize SMPP session", message, e);
		} finally {
			if (session != null) {
				session.unbind(Integer.parseInt(properties.getProperty(TimeoutConstants.UNBIND_PROPERTY, String.valueOf(CloudhopperConstants.DEFAULT_UNBIND_TIMEOUT))));
				session.close();
				session.destroy();
			}
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
		// TODO: split message when too long ?
		// generate new reference number
		byte[] referenceNumber = new byte[1];
		new Random().nextBytes(referenceNumber);
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

	private SmppSessionConfiguration getSmppSessionConfiguration() {
		SmppSessionConfiguration config = new SmppSessionConfiguration(SmppBindType.TRANSMITTER, properties.getProperty(SmsConstants.SmppConstants.SYSTEMID_PROPERTY), properties.getProperty(SmsConstants.SmppConstants.PASSWORD_PROPERTY));
		config.setHost(properties.getProperty(SmsConstants.SmppConstants.HOST_PROPERTY));
		config.setPort(Integer.parseInt(properties.getProperty(SmsConstants.SmppConstants.PORT_PROPERTY)));
		config.setBindTimeout(Integer.parseInt(properties.getProperty(TimeoutConstants.BIND_PROPERTY, String.valueOf(SmppConstants.DEFAULT_BIND_TIMEOUT))));
		config.setConnectTimeout(Integer.parseInt(properties.getProperty(TimeoutConstants.CONNECTION_PROPERTY, String.valueOf(SmppConstants.DEFAULT_CONNECT_TIMEOUT))));
		String version = properties.getProperty(SmsConstants.SmppConstants.INTERFACE_VERSION_PROPERTY, String.valueOf(SmppConstants.VERSION_3_4));
		switch(version) {
			case "3.3":
				config.setInterfaceVersion(SmppConstants.VERSION_3_3);
			break;
			case "3.4":
			default:
				config.setInterfaceVersion(SmppConstants.VERSION_3_4);
			break;
		}
		config.setName(properties.getProperty(CloudhopperConstants.SESSION_NAME_PROPERTY));
		config.setRequestExpiryTimeout(Integer.parseInt(properties.getProperty(TimeoutConstants.REQUEST_EXPIRY_PROPERTY, String.valueOf(SmppConstants.DEFAULT_REQUEST_EXPIRY_TIMEOUT))));
		// TODO: manage ssl properties
//		config.setSslConfiguration(value);
//		config.setUseSsl(value);
		// TODO: allow to configure system type and bind type ?
//		config.setSystemType(value);
//		config.setType(bindType);
		config.setWindowMonitorInterval(Integer.parseInt(properties.getProperty(SmsConstants.SmppConstants.WINDOW_MONITOR_INTERVAL_PROPERTY, String.valueOf(SmppConstants.DEFAULT_WINDOW_MONITOR_INTERVAL))));
		config.setWindowSize(Integer.parseInt(properties.getProperty(SmsConstants.SmppConstants.WINDOW_SIZE_PROPERTY, String.valueOf(SmppConstants.DEFAULT_WINDOW_SIZE))));
		config.setWindowWaitTimeout(Integer.parseInt(properties.getProperty(TimeoutConstants.WINDOW_WAIT_PROPERTY, String.valueOf(SmppConstants.DEFAULT_WINDOW_WAIT_TIMEOUT))));
		config.setWriteTimeout(Integer.parseInt(properties.getProperty(CloudhopperConstants.WRITE_TIMEOUT_PROPERTY, String.valueOf(SmppConstants.DEFAULT_WRITE_TIMEOUT))));
		
		// TODO: externalize logs options
//		config.getLoggingOptions().setLogBytes(false);
//		config.setCountersEnabled(false);
		return config;
	}

	@Override
	public String toString() {
		return "CloudhopperSMPPSender";
	}
}
