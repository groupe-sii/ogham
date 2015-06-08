package fr.sii.notification.helper.sms.jsmpp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsmpp.bean.CancelSm;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.QuerySm;
import org.jsmpp.bean.ReplaceSm;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.SubmitMulti;
import org.jsmpp.bean.SubmitMultiResult;
import org.jsmpp.bean.SubmitSm;
import org.jsmpp.bean.UnsuccessDelivery;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.QuerySmResult;
import org.jsmpp.session.SMPPServerSession;
import org.jsmpp.session.SMPPServerSessionListener;
import org.jsmpp.session.ServerMessageReceiverListener;
import org.jsmpp.session.ServerResponseDeliveryAdapter;
import org.jsmpp.session.Session;
import org.jsmpp.util.MessageIDGenerator;
import org.jsmpp.util.MessageId;
import org.jsmpp.util.RandomMessageIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author uudashr
 *
 */
public class JSMPPServerSimulator extends ServerResponseDeliveryAdapter implements Runnable, ServerMessageReceiverListener {
	private static final Logger LOG = LoggerFactory.getLogger(JSMPPServerSimulator.class);
	
	private ExecutorService execService;// = Executors.newFixedThreadPool(5);
	private final ExecutorService execServiceDelReceipt = Executors.newFixedThreadPool(100);
	private final MessageIDGenerator messageIDGenerator = new RandomMessageIDGenerator();
	private int port;
	private boolean stopped;
	private List<SubmitSm> receivedMessages = new ArrayList<>();
	private SMPPServerSessionListener sessionListener;
	private SMPPServerSession serverSession;

	public JSMPPServerSimulator(int port) {
		this.port = port;
	}

	public void run() {
		try {
			if(!stopped) {
				sessionListener = new SMPPServerSessionListener(port);
				execService = Executors.newFixedThreadPool(5);
				LOG.info("Listening on port {}", port);
			}
			while (!stopped) {
				serverSession = sessionListener.accept();
				LOG.info("Accepting connection for session {}", serverSession.getSessionId());
				serverSession.setMessageReceiverListener(this);
				serverSession.setResponseDeliveryListener(this);
				execService.execute(new WaitBindTask(serverSession));
			}
		} catch (IOException e) {
			if(!stopped) {
				LOG.error("IO error occurred", e);
			}
		}
	}
	
	public synchronized void reset() {
		stopped = false;
		receivedMessages.clear();
	}

	public synchronized void stop() {
		LOG.info("Stopping SMPP simulator");
		stopped = true;
		if (execService != null) {
			execService.shutdownNow();
			execService = null;
		}
		if (serverSession != null) {
			serverSession.close();
			serverSession = null;
		}
		if (sessionListener != null) {
			try {
				sessionListener.close();
				sessionListener = null;
			} catch (IOException e) {
				// nothing to do
			}
		}
		LOG.info("SMPP simulator stopped");
	}

	public QuerySmResult onAcceptQuerySm(QuerySm querySm, SMPPServerSession source) throws ProcessRequestException {
		LOG.info("Accepting query sm, but not implemented");
		return null;
	}

	public MessageId onAcceptSubmitSm(SubmitSm submitSm, SMPPServerSession source) throws ProcessRequestException {
		MessageId messageId = messageIDGenerator.newMessageId();
		LOG.debug("Receiving submit_sm '{}', and return message id {}", new String(submitSm.getShortMessage()), messageId);
		receivedMessages.add(submitSm);
		if (SMSCDeliveryReceipt.SUCCESS.containedIn(submitSm.getRegisteredDelivery()) || SMSCDeliveryReceipt.SUCCESS_FAILURE.containedIn(submitSm.getRegisteredDelivery())) {
			execServiceDelReceipt.execute(new DeliveryReceiptTask(source, submitSm, messageId));
		}
		return messageId;
	}

	public void onSubmitSmRespSent(MessageId messageId, SMPPServerSession source) {
		LOG.debug("submit_sm_resp with message_id {} has been sent", messageId);
	}

	public SubmitMultiResult onAcceptSubmitMulti(SubmitMulti submitMulti, SMPPServerSession source) throws ProcessRequestException {
		MessageId messageId = messageIDGenerator.newMessageId();
		LOG.debug("Receiving submit_multi_sm '{}', and return message id {}", new String(submitMulti.getShortMessage()), messageId);
		if (SMSCDeliveryReceipt.SUCCESS.containedIn(submitMulti.getRegisteredDelivery()) || SMSCDeliveryReceipt.SUCCESS_FAILURE.containedIn(submitMulti.getRegisteredDelivery())) {
			execServiceDelReceipt.execute(new DeliveryReceiptTask(source, submitMulti, messageId));
		}

		return new SubmitMultiResult(messageId.getValue(), new UnsuccessDelivery[0]);
	}

	public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
		LOG.debug("onAcceptDataSm '{}'", dataSm);
		return null;
	}

	public void onAcceptCancelSm(CancelSm cancelSm, SMPPServerSession source) throws ProcessRequestException {
	}

	public void onAcceptReplaceSm(ReplaceSm replaceSm, SMPPServerSession source) throws ProcessRequestException {
	}

	public List<SubmitSm> getReceivedMessages() {
		return receivedMessages;
	}

	public int getPort() {
		return port;
	}
}