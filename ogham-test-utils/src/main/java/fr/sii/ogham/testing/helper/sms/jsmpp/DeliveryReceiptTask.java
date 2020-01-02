package fr.sii.ogham.testing.helper.sms.jsmpp;

import java.util.Date;

import org.jsmpp.bean.DataCodings;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GSMSpecificFeature;
import org.jsmpp.bean.MessageMode;
import org.jsmpp.bean.MessageType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SubmitMulti;
import org.jsmpp.bean.SubmitSm;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.SMPPServerSession;
import org.jsmpp.util.DeliveryReceiptState;
import org.jsmpp.util.MessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DeliveryReceiptTask implements Runnable {
	private static final int TWO_BYTES = 16;
	private static final int WAIT_DURATION = 1000;

	private static final Logger LOG = LoggerFactory.getLogger(DeliveryReceiptTask.class);

	private final SMPPServerSession session;
	private final MessageId messageId;

	private final TypeOfNumber sourceAddrTon;
	private final NumberingPlanIndicator sourceAddrNpi;
	private final String sourceAddress;

	private final TypeOfNumber destAddrTon;
	private final NumberingPlanIndicator destAddrNpi;
	private final String destAddress;

	private final int totalSubmitted;
	private final int totalDelivered;

	private final byte[] shortMessage;

	public DeliveryReceiptTask(SMPPServerSession session, SubmitSm submitSm, MessageId messageId) {
		this.session = session;
		this.messageId = messageId;

		// reversing destination to source
		sourceAddrTon = TypeOfNumber.valueOf(submitSm.getDestAddrTon());
		sourceAddrNpi = NumberingPlanIndicator.valueOf(submitSm.getDestAddrNpi());
		sourceAddress = submitSm.getDestAddress();

		// reversing source to destination
		destAddrTon = TypeOfNumber.valueOf(submitSm.getSourceAddrTon());
		destAddrNpi = NumberingPlanIndicator.valueOf(submitSm.getSourceAddrNpi());
		destAddress = submitSm.getSourceAddr();

		totalSubmitted = totalDelivered = 1;

		shortMessage = submitSm.getShortMessage();
	}

	public DeliveryReceiptTask(SMPPServerSession session, SubmitMulti submitMulti, MessageId messageId) {
		this.session = session;
		this.messageId = messageId;

		// set to unknown and null, since it was submit_multi
		sourceAddrTon = TypeOfNumber.UNKNOWN;
		sourceAddrNpi = NumberingPlanIndicator.UNKNOWN;
		sourceAddress = null;

		// reversing source to destination
		destAddrTon = TypeOfNumber.valueOf(submitMulti.getSourceAddrTon());
		destAddrNpi = NumberingPlanIndicator.valueOf(submitMulti.getSourceAddrNpi());
		destAddress = submitMulti.getSourceAddr();

		// distribution list assumed only contains single address
		totalSubmitted = totalDelivered = submitMulti.getDestAddresses().length;

		shortMessage = submitMulti.getShortMessage();
	}

	public void run() {
		try {
			Thread.sleep(WAIT_DURATION);
		} catch (InterruptedException e1) {
			Thread.currentThread().interrupt();
			LOG.error("Sleep interrupted", e1);
		}
		SessionState state = session.getSessionState();
		if (!state.isReceivable()) {
			LOG.debug("Not sending delivery receipt for message id {} since session state is {}", messageId, state);
			return;
		}
		String stringValue = Integer.valueOf(messageId.getValue(), TWO_BYTES).toString();
		try {

			DeliveryReceipt delRec = new DeliveryReceipt(stringValue, totalSubmitted, totalDelivered, new Date(), new Date(), DeliveryReceiptState.DELIVRD, null, new String(shortMessage));
			session.deliverShortMessage("mc", sourceAddrTon, sourceAddrNpi, sourceAddress, destAddrTon, destAddrNpi, destAddress, new ESMClass(MessageMode.DEFAULT,
					MessageType.SMSC_DEL_RECEIPT, GSMSpecificFeature.DEFAULT), (byte) 0, (byte) 0, new RegisteredDelivery(0), DataCodings.ZERO, delRec.toString().getBytes());
			LOG.debug("Sending delivery receipt for message id {}: {}", messageId, stringValue);
		} catch (Exception e) {
			LOG.error("Failed sending delivery_receipt for message id {}: {}", messageId, stringValue, e);
		}
	}
}