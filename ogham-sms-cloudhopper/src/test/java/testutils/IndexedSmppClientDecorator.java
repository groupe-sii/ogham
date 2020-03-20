package testutils;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.type.SmppBindException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

public class IndexedSmppClientDecorator implements SmppClient {
	private final SmppClient delegate;
	private final int clientIdx;

	public IndexedSmppClientDecorator(SmppClient delegate, int clientIdx) {
		super();
		this.delegate = delegate;
		this.clientIdx = clientIdx;
	}

	@Override
	public SmppSession bind(SmppSessionConfiguration config, SmppSessionHandler sessionHandler)
			throws SmppTimeoutException, SmppChannelException, SmppBindException, UnrecoverablePduException, InterruptedException {
		return delegate.bind(config, sessionHandler);
	}

	@Override
	public void destroy() {
		delegate.destroy();
	}

	public int getClientIdx() {
		return clientIdx;
	}

	@Override
	public String toString() {
		return "client[" + clientIdx + "]";
	}

}