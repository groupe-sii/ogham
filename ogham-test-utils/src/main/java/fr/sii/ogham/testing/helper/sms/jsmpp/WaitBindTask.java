package fr.sii.ogham.testing.helper.sms.jsmpp;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import org.jsmpp.PDUStringException;
import org.jsmpp.SMPPConstant;
import org.jsmpp.bean.InterfaceVersion;
import org.jsmpp.session.BindRequest;
import org.jsmpp.session.SMPPServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.testing.helper.sms.rule.config.Credentials;

class WaitBindTask implements Runnable {
	private static final int WAIT_DURATION = 1000;

	private static final Logger LOG = LoggerFactory.getLogger(WaitBindTask.class);

	private final SMPPServerSession serverSession;
	private final List<Credentials> serverCredentials;

	public WaitBindTask(SMPPServerSession serverSession, List<Credentials> serverCredentials) {
		this.serverSession = serverSession;
		this.serverCredentials = serverCredentials;
	}

	public void run() {
		try {
			BindRequest bindRequest = serverSession.waitForBind(WAIT_DURATION);
			LOG.info("Accepting bind for session {}, interface version {}", serverSession.getSessionId(), InterfaceVersion.IF_34);
			bind(bindRequest);
		} catch (IllegalStateException e) {
			LOG.error("System error", e);
		} catch (TimeoutException e) {
			LOG.warn("Wait for bind has reach timeout", e);
		} catch (IOException e) {
			LOG.error("Failed accepting bind request for session {}", serverSession.getSessionId(), e);
		}
	}

	private void bind(BindRequest bindRequest) throws IOException {
		try {
			if (checkPassword(bindRequest)) {
				bindRequest.accept(bindRequest.getSystemId(), InterfaceVersion.IF_34);
			} else {
				bindRequest.reject(SMPPConstant.STAT_ESME_RINVPASWD);
			}
		} catch (PDUStringException e) {
			LOG.error("Invalid system id", e);
			bindRequest.reject(SMPPConstant.STAT_ESME_RSYSERR);
		}
	}

	private boolean checkPassword(BindRequest bindRequest) {
		// no credentials registered => allow requests
		if (serverCredentials.isEmpty()) {
			return true;
		}
		for (Credentials creds : serverCredentials) {
			if (Objects.equals(creds.getSystemId(), bindRequest.getSystemId()) && Objects.equals(creds.getPassword(), bindRequest.getPassword())) {
				return true;
			}
		}
		return false;
	}
}