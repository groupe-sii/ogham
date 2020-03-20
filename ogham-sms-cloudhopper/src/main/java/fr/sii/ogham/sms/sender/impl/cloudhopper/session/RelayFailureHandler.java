package fr.sii.ogham.sms.sender.impl.cloudhopper.session;

import com.cloudhopper.smpp.PduAsyncResponse;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

/**
 * A Cloudhopper session handler that relay failures to an {@link ErrorHandler}.
 * Original failures are still fired to delegate instance.
 * 
 * @author Aurélien Baudet
 *
 */
public class RelayFailureHandler implements SmppSessionHandler {
	private final ErrorHandler errorHandler;
	private final SmppSessionHandler delegate;

	public RelayFailureHandler(ErrorHandler errorHandler, SmppSessionHandler delegate) {
		super();
		this.errorHandler = errorHandler;
		this.delegate = delegate;
	}

	@Override
	public String lookupResultMessage(int commandStatus) {
		return delegate.lookupResultMessage(commandStatus);
	}

	@Override
	public String lookupTlvTagName(short tag) {
		return delegate.lookupTlvTagName(tag);
	}

	@Override
	public void fireChannelUnexpectedlyClosed() {
		delegate.fireChannelUnexpectedlyClosed();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public PduResponse firePduRequestReceived(PduRequest pduRequest) {
		return delegate.firePduRequestReceived(pduRequest);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void firePduRequestExpired(PduRequest pduRequest) {
		delegate.firePduRequestExpired(pduRequest);
	}

	@Override
	public void fireExpectedPduResponseReceived(PduAsyncResponse pduAsyncResponse) {
		delegate.fireExpectedPduResponseReceived(pduAsyncResponse);
	}

	@Override
	public void fireUnexpectedPduResponseReceived(PduResponse pduResponse) {
		delegate.fireUnexpectedPduResponseReceived(pduResponse);
	}

	@Override
	public void fireUnrecoverablePduException(UnrecoverablePduException e) {
		errorHandler.handleFailure(e);
		delegate.fireUnrecoverablePduException(e);
	}

	@Override
	public void fireRecoverablePduException(RecoverablePduException e) {
		errorHandler.handleFailure(e);
		delegate.fireRecoverablePduException(e);
	}

	@Override
	public void fireUnknownThrowable(Throwable t) {
		errorHandler.handleFailure(t);
		delegate.fireUnknownThrowable(t);
	}

}
