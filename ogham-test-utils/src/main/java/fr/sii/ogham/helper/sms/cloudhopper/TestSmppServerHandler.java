package fr.sii.ogham.helper.sms.cloudhopper;

import java.util.HashSet;
import java.util.Set;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppServerHandler;
import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.PollableSmppSessionHandler;
import com.cloudhopper.smpp.pdu.BaseBind;
import com.cloudhopper.smpp.pdu.BaseBindResp;
import com.cloudhopper.smpp.type.SmppProcessingException;

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