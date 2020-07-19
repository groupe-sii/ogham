package fr.sii.ogham.testing.sms.simulator.jsmpp;

import static fr.sii.ogham.testing.sms.simulator.decode.MessageDecoder.decode;
import static java.util.Collections.unmodifiableList;
import static org.jsmpp.bean.SMSCDeliveryReceipt.SUCCESS_FAILURE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsmpp.bean.CancelSm;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.QuerySm;
import org.jsmpp.bean.ReplaceSm;
import org.jsmpp.bean.SubmitMulti;
import org.jsmpp.bean.SubmitMultiResult;
import org.jsmpp.bean.SubmitSm;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.testing.sms.simulator.config.SimulatorConfiguration;

/**
 * @author uudashr
 * @author Aurélien Baudet
 *
 */
public class JSMPPServerSimulator extends ServerResponseDeliveryAdapter implements Runnable, ServerMessageReceiverListener {
	private static final int BIND_THREAD_POOL_SIZE = 5;
	private static final int RECEIPT_THREAD_POOL_SIZE = 100;

	private static final Logger LOG = LoggerFactory.getLogger(JSMPPServerSimulator.class);

	private ExecutorService execService;
	private final ExecutorService execServiceDelReceipt = Executors.newFixedThreadPool(RECEIPT_THREAD_POOL_SIZE);
	private final MessageIDGenerator messageIDGenerator = new UnsecureRandomMessageIDGenerator();
	private int port;
	private boolean stopped;
	private List<SubmitSm> receivedMessages = new ArrayList<>();
	private SMPPServerSessionListener sessionListener;
	private SMPPServerSession serverSession;
	private final Object startupMonitor = new Object();
	private volatile boolean running = false;
	private final SimulatorConfiguration config;
	private ServerStartupException startupFailure;

	public JSMPPServerSimulator(int port, SimulatorConfiguration config) {
		this.port = port;
		this.config = config;
	}

	public void run() {
		try {
			if (!stopped) {
				sessionListener = createServerSessionListener();
				execService = Executors.newFixedThreadPool(BIND_THREAD_POOL_SIZE);
				running = true;
				LOG.info("Listening on port {}", port);
				synchronized (startupMonitor) {
					startupMonitor.notifyAll();
				}
			}
			while (!stopped) {
				serverSession = sessionListener.accept();
				LOG.info("Accepting connection for session {}", serverSession.getSessionId());
				serverSession.setMessageReceiverListener(this);
				serverSession.setResponseDeliveryListener(this);
				execService.execute(new WaitBindTask(serverSession, config.getCredentials()));
			}
		} catch (IOException e) {
			if (!stopped) { // NOSONAR
				LOG.trace("Failed to initialize SMPP server simulator", e);
				startupFailure = new ServerStartupException("Server failed to start on port "+port, e);
				close();
			}
		} finally {
			// Notify everybody that we're ready to accept connections or failed
			// to start.
			// Otherwise will run into startup timeout, see
			// #waitTillRunning(long).
			synchronized (startupMonitor) {
				startupMonitor.notifyAll();
			}
		}
	}

	private SMPPServerSessionListener createServerSessionListener() throws IOException {
		return new ConfigurableSMPPServerSessionListener(port, config.getServerDelays());
	}

	public synchronized void reset() {
		stopped = false;
		if (!config.isKeepMessages()) {
			receivedMessages.clear();
		}
	}

	public synchronized void stop() {
		LOG.info("Stopping SMPP simulator");
		running = false;
		stopped = true;
		if (execService != null) {
			LOG.trace("Stopping executor service");
			execService.shutdownNow();
			execService = null;
		}
		close();
		LOG.info("SMPP simulator stopped");
	}

	private void close() {
		if (serverSession != null) {
			LOG.trace("Closing server session");
			serverSession.close();
			LOG.trace("Server session closed");
			serverSession = null;
		}
		if (sessionListener != null) {
			try {
				LOG.trace("Closing session listener");
				sessionListener.close();
				LOG.trace("Session listener closed");
				sessionListener = null;
			} catch (IOException e) {
				// nothing to do
				LOG.trace("Failed to close session listener", e);
			}
		}
	}

	public void waitTillRunning(long timeoutInMs) throws ServerStartupException {
		try {
			long t = System.currentTimeMillis();
			synchronized (startupMonitor) {
				// Loop to avoid spurious wake ups, see
				// https://www.securecoding.cert.org/confluence/display/java/THI03-J.+Always+invoke+wait%28%29+and+await%28%29+methods+inside+a+loop
				while (!running && startupFailure == null && System.currentTimeMillis() - t < timeoutInMs) {
					startupMonitor.wait(timeoutInMs);
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ServerStartupException("Server failed to start (interrupted)", e);
		}
		
		if (startupFailure != null) {
			throw startupFailure;
		}

		if (!running) {
			close();
			throw new ServerStartupException("Server bouldn't be started after "+timeoutInMs+"ms");
		}
	}

	public QuerySmResult onAcceptQuerySm(QuerySm querySm, SMPPServerSession source) throws ProcessRequestException {
		LOG.info("Accepting query sm, but not implemented");
		return null;
	}

	public MessageId onAcceptSubmitSm(SubmitSm submitSm, SMPPServerSession source) throws ProcessRequestException {
		MessageId messageId = messageIDGenerator.newMessageId();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Receiving submit_sm '{}', and return message id {}", decode(new SubmitSmAdapter(submitSm)), messageId);
		}
		receivedMessages.add(submitSm);
		if (SUCCESS_FAILURE.containedIn(submitSm.getRegisteredDelivery())) {
			execServiceDelReceipt.execute(new DeliveryReceiptTask(source, submitSm, messageId));
		}
		return messageId;
	}

	@Override
	public void onSubmitSmRespSent(MessageId messageId, SMPPServerSession source) {
		LOG.debug("submit_sm_resp with message_id {} has been sent", messageId);
	}

	public SubmitMultiResult onAcceptSubmitMulti(SubmitMulti submitMulti, SMPPServerSession source) throws ProcessRequestException {
		MessageId messageId = messageIDGenerator.newMessageId();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Receiving submit_multi_sm '{}', and return message id {}", submitMulti, messageId);
		}
		if (SUCCESS_FAILURE.containedIn(submitMulti.getRegisteredDelivery())) {
			execServiceDelReceipt.execute(new DeliveryReceiptTask(source, submitMulti, messageId));
		}

		return new SubmitMultiResult(messageId.getValue());
	}

	public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
		LOG.debug("onAcceptDataSm '{}'", dataSm);
		return null;
	}

	@Override
	public void onAcceptCancelSm(CancelSm cancelSm, SMPPServerSession source) throws ProcessRequestException {
		// nothing to do
	}

	@Override
	public void onAcceptReplaceSm(ReplaceSm replaceSm, SMPPServerSession source) throws ProcessRequestException {
		// nothing to do
	}

	public List<SubmitSm> getReceivedMessages() {
		return unmodifiableList(new ArrayList<>(receivedMessages));
	}

	public int getPort() {
		return port;
	}
}