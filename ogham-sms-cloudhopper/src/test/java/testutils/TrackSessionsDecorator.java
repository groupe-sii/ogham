package testutils;

import static org.mockito.Mockito.spy;
import static testutils.SessionStrategyTestHelper.track;

import java.util.List;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.type.SmppBindException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

public class TrackSessionsDecorator implements SmppClient {
	private final SmppClient delegate;
	private final List<SmppSession> sessions;

	public TrackSessionsDecorator(SmppClient delegate, List<SmppSession> sessions) {
		super();
		this.delegate = delegate;
		this.sessions = sessions;
	}

	@Override
	public SmppSession bind(SmppSessionConfiguration config, SmppSessionHandler sessionHandler)
			throws SmppTimeoutException, SmppChannelException, SmppBindException, UnrecoverablePduException, InterruptedException {
		SmppSession session = spy(track(delegate.bind(config, sessionHandler), sessions.size()));
		sessions.add(session);
		System.out.println(session+" tracked");
		return session;
	}

	@Override
	public void destroy() {
		delegate.destroy();
	}
}
