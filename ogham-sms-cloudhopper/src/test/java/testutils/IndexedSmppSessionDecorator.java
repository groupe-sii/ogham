package testutils;

import com.cloudhopper.commons.util.windowing.Window;
import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.SmppSessionCounters;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.EnquireLinkResp;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

public class IndexedSmppSessionDecorator implements SmppSession {
	private final SmppSession delegate;
	private final int sessionIdx;

	public IndexedSmppSessionDecorator(SmppSession delegate, int sessionIdx) {
		super();
		this.delegate = delegate;
		this.sessionIdx = sessionIdx;
	}

	@Override
	public SmppBindType getBindType() {
		return delegate.getBindType();
	}

	@Override
	public Type getLocalType() {
		return delegate.getLocalType();
	}

	@Override
	public Type getRemoteType() {
		return delegate.getRemoteType();
	}

	@Override
	public SmppSessionConfiguration getConfiguration() {
		return delegate.getConfiguration();
	}

	@Override
	public String getStateName() {
		return delegate.getStateName();
	}

	@Override
	public byte getInterfaceVersion() {
		return delegate.getInterfaceVersion();
	}

	@Override
	public boolean areOptionalParametersSupported() {
		return delegate.areOptionalParametersSupported();
	}

	@Override
	public boolean isOpen() {
		return delegate.isOpen();
	}

	@Override
	public boolean isBinding() {
		return delegate.isBinding();
	}

	@Override
	public boolean isBound() {
		return delegate.isBound();
	}

	@Override
	public boolean isUnbinding() {
		return delegate.isUnbinding();
	}

	@Override
	public boolean isClosed() {
		return delegate.isClosed();
	}

	@Override
	public long getBoundTime() {
		return delegate.getBoundTime();
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	@Override
	public Window<Integer, PduRequest, PduResponse> getRequestWindow() {
		return delegate.getRequestWindow();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Window<Integer, PduRequest, PduResponse> getSendWindow() {
		return delegate.getSendWindow();
	}

	@Override
	public boolean hasCounters() {
		return delegate.hasCounters();
	}

	@Override
	public SmppSessionCounters getCounters() {
		return delegate.getCounters();
	}

	@Override
	public void close() {
		delegate.close();
	}

	@Override
	public void unbind(long timeoutMillis) {
		delegate.unbind(timeoutMillis);
	}

	@Override
	public void destroy() {
		delegate.destroy();
	}

	@Override
	public EnquireLinkResp enquireLink(EnquireLink request, long timeoutMillis)
			throws RecoverablePduException, UnrecoverablePduException, SmppTimeoutException, SmppChannelException, InterruptedException {
		return delegate.enquireLink(request, timeoutMillis);
	}

	@Override
	public SubmitSmResp submit(SubmitSm request, long timeoutMillis) throws RecoverablePduException, UnrecoverablePduException, SmppTimeoutException, SmppChannelException, InterruptedException {
		return delegate.submit(request, timeoutMillis);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public WindowFuture<Integer, PduRequest, PduResponse> sendRequestPdu(PduRequest request, long timeoutMillis, boolean synchronous)
			throws RecoverablePduException, UnrecoverablePduException, SmppTimeoutException, SmppChannelException, InterruptedException {
		return delegate.sendRequestPdu(request, timeoutMillis, synchronous);
	}

	@Override
	public void sendResponsePdu(PduResponse response) throws RecoverablePduException, UnrecoverablePduException, SmppChannelException, InterruptedException {
		delegate.sendResponsePdu(response);
	}

	public int getSessionIdx() {
		return sessionIdx;
	}

	@Override
	public String toString() {
		return "session[" + sessionIdx + "]";
	}

}