package fr.sii.ogham.helper.sms.jsmpp;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.jsmpp.PDUStringException;
import org.jsmpp.SMPPConstant;
import org.jsmpp.bean.InterfaceVersion;
import org.jsmpp.session.BindRequest;
import org.jsmpp.session.SMPPServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WaitBindTask implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(WaitBindTask.class);

	private final SMPPServerSession serverSession;

	public WaitBindTask(SMPPServerSession serverSession) {
		this.serverSession = serverSession;
	}

	public void run() {
		try {
			BindRequest bindRequest = serverSession.waitForBind(1000);
			LOG.info("Accepting bind for session {}, interface version {}", serverSession.getSessionId());
			try {
				bindRequest.accept("sys", InterfaceVersion.IF_34);
			} catch (PDUStringException e) {
				LOG.error("Invalid system id", e);
				bindRequest.reject(SMPPConstant.STAT_ESME_RSYSERR);
			}

		} catch (IllegalStateException e) {
			LOG.error("System error", e);
		} catch (TimeoutException e) {
			LOG.warn("Wait for bind has reach timeout", e);
		} catch (IOException e) {
			LOG.error("Failed accepting bind request for session {}", serverSession.getSessionId());
		}
	}
}