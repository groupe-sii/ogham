package fr.sii.ogham.testing.sms.simulator.jsmpp;

import java.io.IOException;

import org.jsmpp.DefaultPDUReader;
import org.jsmpp.DefaultPDUSender;
import org.jsmpp.PDUReader;
import org.jsmpp.PDUSender;
import org.jsmpp.SynchronizedPDUSender;
import org.jsmpp.session.SMPPServerSession;
import org.jsmpp.session.SMPPServerSessionListener;
import org.jsmpp.session.ServerResponseDeliveryListener;
import org.jsmpp.session.connection.Connection;
import org.jsmpp.session.connection.ServerConnection;
import org.jsmpp.session.connection.ServerConnectionFactory;
import org.jsmpp.session.connection.socket.ServerSocketConnectionFactory;

import fr.sii.ogham.testing.sms.simulator.config.ServerDelays;

/**
 * Override default {@link SMPPServerSessionListener}.
 * 
 * 
 * Need to copy code because of visibility of fields.
 * 
 * @author Aurélien Baudet
 *
 */
public class ConfigurableSMPPServerSessionListener extends SMPPServerSessionListener {
	protected final ServerDelays delays;
	protected ServerResponseDeliveryListener responseDeliveryListener;
	protected final ServerConnection serverConn;

	public ConfigurableSMPPServerSessionListener(int port, ServerDelays delays, int timeout, int backlog, ServerConnectionFactory serverConnFactory) throws IOException {
		super(port, timeout, backlog, new NullServerConnectionFactory());
		this.delays = delays;
		serverConn = serverConnFactory.listen(port, timeout, backlog);
	}

	public ConfigurableSMPPServerSessionListener(int port, ServerDelays delays, int timeout, ServerConnectionFactory serverConnFactory) throws IOException {
		super(port, timeout, new NullServerConnectionFactory());
		this.delays = delays;
		serverConn = serverConnFactory.listen(port, timeout);
	}

	public ConfigurableSMPPServerSessionListener(int port, ServerDelays delays, ServerConnectionFactory serverConnFactory) throws IOException {
		super(port, new NullServerConnectionFactory());
		this.delays = delays;
		serverConn = serverConnFactory.listen(port);
	}

	public ConfigurableSMPPServerSessionListener(int port, ServerDelays delays) throws IOException {
		this(port, delays, new ServerSocketConnectionFactory());
	}

	@Override
	public SMPPServerSession accept() throws IOException {
		if (delays == null) {
			Connection conn = serverConn.accept();
			conn.setSoTimeout(getInitiationTimer());
			return new SMPPServerSession(conn, getSessionStateListener(), getMessageReceiverListener(), responseDeliveryListener, getPduProcessorDegree(), getQueueCapacity());
		}
		Connection conn = serverConn.accept();
		conn.setSoTimeout(getInitiationTimer());
		return new SMPPServerSession(conn, getSessionStateListener(), getMessageReceiverListener(), responseDeliveryListener, getPduProcessorDegree(), getQueueCapacity(), createPduSender(), createPduReader());
	}

	@Override
	public void setResponseDeliveryListener(ServerResponseDeliveryListener responseDeliveryListener) {
		super.setResponseDeliveryListener(responseDeliveryListener);
		this.responseDeliveryListener = responseDeliveryListener;
	}

	protected PDUSender createPduSender() {
		if (delays == null) {
			return new SynchronizedPDUSender(new DefaultPDUSender());
		}
		return new SlowPduSender(new SynchronizedPDUSender(new DefaultPDUSender()), delays);
	}

	protected PDUReader createPduReader() {
		return new DefaultPDUReader();
	}

	@Override
	public int getTimeout() throws IOException {
		return serverConn.getSoTimeout();
	}

	@Override
	public void setTimeout(int timeout) throws IOException {
		serverConn.setSoTimeout(timeout);
	}

	@Override
	public void close() throws IOException {
		serverConn.close();
	}

	private static class NullServerConnectionFactory implements ServerConnectionFactory {
		@Override
		public ServerConnection listen(int port) throws IOException {
			return null;
		}

		@Override
		public ServerConnection listen(int port, int timeout) throws IOException {
			return null;
		}

		@Override
		public ServerConnection listen(int port, int timeout, int backlog) throws IOException {
			return null;
		}
	}
}
