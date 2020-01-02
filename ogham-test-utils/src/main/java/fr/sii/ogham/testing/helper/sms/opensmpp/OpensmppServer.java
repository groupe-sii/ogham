package fr.sii.ogham.testing.helper.sms.opensmpp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smpp.SmppObject;
import org.smpp.debug.Debug;
import org.smpp.debug.Event;
import org.smpp.debug.FileDebug;
import org.smpp.debug.FileEvent;
import org.smpp.pdu.SubmitSM;
import org.smpp.smscsim.DeliveryInfoSender;
import org.smpp.smscsim.PDUProcessorGroup;
import org.smpp.smscsim.SMSCListener;
import org.smpp.smscsim.SMSCListenerImpl;
import org.smpp.smscsim.SMSCSession;
import org.smpp.smscsim.ShortMessageStore;
import org.smpp.smscsim.SimulatorPDUProcessor;
import org.smpp.smscsim.SimulatorPDUProcessorFactory;
import org.smpp.smscsim.util.Table;

import fr.sii.ogham.testing.helper.sms.rule.SmppServerException;
import fr.sii.ogham.testing.helper.sms.rule.SmppServerSimulator;

/**
 * FIXME: doesn't work for now
 * 
 * @author Aurélien Baudet
 *
 */
public class OpensmppServer implements SmppServerSimulator<SubmitSM> {
	private static final Logger LOG = LoggerFactory.getLogger(OpensmppServer.class);

	private int port;

	private static final String DEBUG_DIR = "./";
	private static Debug debug = new FileDebug(DEBUG_DIR, "sim.dbg");
	private static Event event = new FileEvent(DEBUG_DIR, "sim.evt");

//	private static final int DSIM = 16;
//	private static final int DSIMD = 17;
	private static final int DSIMD2 = 18;

	private SMSCListener smscListener;
	private PDUProcessorGroup processors;
	private ReadableShortMessageStore messageStore;
	private DeliveryInfoSender deliveryInfoSender;

	public OpensmppServer(int port) {
		super();
		this.port = port;
		SmppObject.setDebug(debug);
		SmppObject.setEvent(event);
		debug.activate();
		event.activate();
		debug.deactivate(SmppObject.DRXTXD2);
		debug.deactivate(SmppObject.DPDUD);
		debug.deactivate(SmppObject.DCOMD);
		debug.deactivate(DSIMD2);
	}

	@Override
	public synchronized void start() throws SmppServerException {
		try {
			smscListener = new SMSCListenerImpl(port, true);
			processors = new PDUProcessorGroup();
			messageStore = new ReadableShortMessageStore();
			deliveryInfoSender = new DeliveryInfoSender();
			deliveryInfoSender.start();
			Table users = new Table();
			SimulatorPDUProcessorFactory factory = new SimulatorPDUProcessorFactory(processors, messageStore, deliveryInfoSender, users);
			factory.setDisplayInfo(false);
			smscListener.setPDUProcessorFactory(factory);
			smscListener.start();
			LOG.info("Opensmpp simulator started");
		} catch (IOException e) {
			throw new SmppServerException("Failed to start Opensmpp simulator", e);
		}
	}

	@Override
	public synchronized void stop() throws SmppServerException {
		try {
			if (smscListener != null) {
				LOG.debug("Stopping Opensmpp simulator listener...");
				synchronized (processors) {
					int procCount = processors.count();
					SimulatorPDUProcessor proc;
					SMSCSession session;
					for (int i = 0; i < procCount; i++) {
						proc = (SimulatorPDUProcessor) processors.get(i);
						session = proc.getSession();
						LOG.trace("Stopping session {}: {}...", i, proc.getSystemId());
						session.stop();
						LOG.trace(" stopped.");
					}
				}
				smscListener.stop();
				smscListener = null;
				if (deliveryInfoSender != null) {
					deliveryInfoSender.stop();
				}
				LOG.info("Opensmpp simulator stopped");
			}
		} catch (IOException e) {
			throw new SmppServerException("Failed to stop Opensmpp simulator", e);
		}
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public List<SubmitSM> getReceivedMessages() {
		return messageStore.getReceivedMessages();
	}

	private static class ReadableShortMessageStore extends ShortMessageStore {
		private final List<SubmitSM> receivedMessages;

		public ReadableShortMessageStore() {
			super();
			this.receivedMessages = new ArrayList<>();
		}

		@Override
		public synchronized void submit(SubmitSM message, String messageId, String systemId) throws UnsupportedEncodingException {
			super.submit(message, messageId, systemId);
			receivedMessages.add(message);
		}

		public List<SubmitSM> getReceivedMessages() {
			return receivedMessages;
		}
	}

}
