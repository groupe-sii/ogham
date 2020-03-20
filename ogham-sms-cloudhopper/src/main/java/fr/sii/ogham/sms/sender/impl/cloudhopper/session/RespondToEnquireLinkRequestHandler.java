package fr.sii.ogham.sms.sender.impl.cloudhopper.session;

import com.cloudhopper.smpp.PduAsyncResponse;
import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

/**
 * Binding to SMSC can be either {@link SmppBindType#TRANSMITTER} or
 * {@link SmppBindType#TRANSCEIVER}. In the first case, only the client sends
 * PDUs to the SMSC. In the second case, the server may send PDUs to the client
 * for:
 * 
 * <ul>
 * <li>Acks</li>
 * <li>{@link EnquireLink} requests to ensure that the client is still
 * alive</li>
 * <li>...</li>
 * </ul>
 * 
 * <p>
 * This handler sends a response to the SMSC when a {@link EnquireLink} is
 * received. This is to send an ack that the {@link EnquireLink} has been
 * received correctly and it tells to the server that the client is still alive.
 * 
 * 
 * @author Aurélien Baudet
 *
 */
public class RespondToEnquireLinkRequestHandler implements SmppSessionHandler {
	private final SmppSessionHandler delegate;

	public RespondToEnquireLinkRequestHandler(SmppSessionHandler delegate) {
		super();
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
		if (pduRequest instanceof EnquireLink) {
			return pduRequest.createResponse();
		}
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
		delegate.fireUnrecoverablePduException(e);
	}

	@Override
	public void fireRecoverablePduException(RecoverablePduException e) {
		delegate.fireRecoverablePduException(e);
	}

	@Override
	public void fireUnknownThrowable(Throwable t) {
		delegate.fireUnknownThrowable(t);
	}

}
