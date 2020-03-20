package fr.sii.ogham.sms.sender.impl.cloudhopper;

import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.Unbind;

import fr.sii.ogham.core.retry.RetryExecutor;

/**
 * Additional options to configure how Cloudhopper should behave:
 * <ul>
 * <li>Additional timeouts (response, unbind)</li>
 * <li>Retry strategy for connection</li>
 * <li>Options for session management strategy</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class ExtendedSmppSessionConfiguration extends SmppSessionConfiguration {
	/**
	 * The maximum amount of time (in milliseconds) to wait for receiving the
	 * response from the server for a single segment ({@link SubmitSm}).
	 */
	private long responseTimeout;
	/**
	 * The maximum amount of time (in milliseconds) to wait for receiving the
	 * response to a disconnect request ({@link Unbind} command) from the
	 * server.
	 */
	private long unbindTimeout;
	/**
	 * The strategy to use for trying to connect to the server.
	 */
	private RetryExecutor connectRetry;
	/**
	 * Keep alive the opened session by sending messages ({@link EnquireLink})
	 * regularly. If the session is closed by the server automatic reconnection
	 * is done.
	 */
	private KeepAliveOptions keepAlive;
	/**
	 * If possible reuse the same session to send the messages instead of
	 * creating a new one for each message.
	 * 
	 * If keep alive options is also set, the keep alive sttrategy is used and
	 * this option has ne effect.
	 */
	private ReuseSessionOptions reuseSession;

	public long getResponseTimeout() {
		return responseTimeout;
	}

	public void setResponseTimeout(long responseTimeout) {
		this.responseTimeout = responseTimeout;
	}

	public long getUnbindTimeout() {
		return unbindTimeout;
	}

	public void setUnbindTimeout(long unbindTimeout) {
		this.unbindTimeout = unbindTimeout;
	}

	public RetryExecutor getConnectRetry() {
		return connectRetry;
	}

	public void setConnectRetry(RetryExecutor connectRetry) {
		this.connectRetry = connectRetry;
	}

	public KeepAliveOptions getKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(KeepAliveOptions keepAlive) {
		this.keepAlive = keepAlive;
	}

	public ReuseSessionOptions getReuseSession() {
		return reuseSession;
	}

	public void setReuseSession(ReuseSessionOptions reuseSession) {
		this.reuseSession = reuseSession;
	}

}
