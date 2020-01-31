package fr.sii.ogham.testing.sms.simulator.cloudhopper;

import java.util.ArrayList;
import java.util.List;

import com.cloudhopper.smpp.SmppServerConfiguration;
import com.cloudhopper.smpp.SmppServerHandler;
import com.cloudhopper.smpp.impl.DefaultSmppServer;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.type.SmppChannelException;

import fr.sii.ogham.testing.sms.simulator.SmppServerException;
import fr.sii.ogham.testing.sms.simulator.SmppServerSimulator;

@SuppressWarnings("rawtypes")
public class CloudhopperSMPPServer implements SmppServerSimulator<PduRequest> {
	private DefaultSmppServer server;
	private TestSmppServerHandler serverHandler;
	
	protected CloudhopperSMPPServer(int port, TestSmppServerHandler serverHandler) {
		super();
		this.server = createSmppServer(port, serverHandler);
		this.serverHandler = serverHandler;
	}
	
	public CloudhopperSMPPServer(int port, String systemId, String password) {
		this(port, new TestSmppServerHandler(systemId, password));
	}

	public CloudhopperSMPPServer(int port) {
		this(port, "systemId", "password");
	}

	@Override
	public void start() throws SmppServerException {
		try {
			server.start();
		} catch (SmppChannelException e) {
			throw new SmppServerException("failed to start SMPP server", e);
		}
	}

	@Override
	public void stop() {
		server.destroy();
	}

	@Override
	public int getPort() {
		return server.getConfiguration().getPort();
	}

	@Override
	public List<PduRequest> getReceivedMessages() {
		return new ArrayList<>(serverHandler.getSessionHandler().getReceivedPduRequests());
	}
	
	private static SmppServerConfiguration createSmppServerConfiguration(int port) {
		SmppServerConfiguration configuration = new SmppServerConfiguration();
		configuration.setPort(port);
		configuration.setSystemId("cloudhopper");
		return configuration;
	}

	private static DefaultSmppServer createSmppServer(int port, SmppServerHandler serverHandler) {
		SmppServerConfiguration configuration = createSmppServerConfiguration(port);
		return new DefaultSmppServer(configuration, serverHandler);
	}

}
