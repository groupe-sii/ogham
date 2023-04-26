package fr.sii.ogham.testing.sms.simulator.cloudhopper;

import java.util.HashSet;
import java.util.Set;

import ogham.testing.com.cloudhopper.smpp.SmppConstants;
import ogham.testing.com.cloudhopper.smpp.SmppServerHandler;
import ogham.testing.com.cloudhopper.smpp.SmppServerSession;
import ogham.testing.com.cloudhopper.smpp.SmppSessionConfiguration;
import ogham.testing.com.cloudhopper.smpp.impl.PollableSmppSessionHandler;
import ogham.testing.com.cloudhopper.smpp.pdu.BaseBind;
import ogham.testing.com.cloudhopper.smpp.pdu.BaseBindResp;
import ogham.testing.com.cloudhopper.smpp.type.SmppProcessingException;

public class TestSmppServerHandler implements SmppServerHandler {
	private Set<SmppServerSession> sessions = new HashSet<>();
	private PollableSmppSessionHandler sessionHandler = new PollableSmppSessionHandler();

	private String systemId;
	private String password;

	public TestSmppServerHandler(String systemId, String password) {
		super();
		this.systemId = systemId;
		this.password = password;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void sessionBindRequested(Long sessionId, SmppSessionConfiguration sessionConfiguration, final BaseBind bindRequest) throws SmppProcessingException {
		// test name change of sessions
		sessionConfiguration.setName("Test1");

		if (!systemId.equals(bindRequest.getSystemId())) {
			throw new SmppProcessingException(SmppConstants.STATUS_INVSYSID);
		}

		if (!password.equals(bindRequest.getPassword())) {
			throw new SmppProcessingException(SmppConstants.STATUS_INVPASWD);
		}
	}

	@Override
	public void sessionCreated(Long sessionId, SmppServerSession session, BaseBindResp preparedBindResponse) {
		sessions.add(session);
		// need to do something it now (flag we're ready)
		session.serverReady(sessionHandler);
	}

	@Override
	public void sessionDestroyed(Long sessionId, SmppServerSession session) {
		sessions.remove(session);
	}

	public Set<SmppServerSession> getSessions() {
		return sessions;
	}

	public PollableSmppSessionHandler getSessionHandler() {
		return sessionHandler;
	}

	public String getSystemId() {
		return systemId;
	}

	public String getPassword() {
		return password;
	}
}