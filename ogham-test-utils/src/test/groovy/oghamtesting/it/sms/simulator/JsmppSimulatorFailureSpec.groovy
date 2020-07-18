package oghamtesting.it.sms.simulator

import org.jsmpp.bean.Alphabet
import org.jsmpp.bean.BindType
import org.jsmpp.bean.ESMClass
import org.jsmpp.bean.GeneralDataCoding
import org.jsmpp.bean.MessageClass
import org.jsmpp.bean.NumberingPlanIndicator
import org.jsmpp.bean.RegisteredDelivery
import org.jsmpp.bean.SMSCDeliveryReceipt
import org.jsmpp.bean.TypeOfNumber
import org.jsmpp.session.BindParameter
import org.jsmpp.session.SMPPSession
import org.jsmpp.util.AbsoluteTimeFormatter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import fr.sii.ogham.testing.extension.common.LogTestInformation
import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig
import fr.sii.ogham.testing.sms.simulator.SmppServerException
import fr.sii.ogham.testing.sms.simulator.jsmpp.JSMPPServer
import fr.sii.ogham.testing.sms.simulator.jsmpp.ServerStartupException
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class JsmppSimulatorFailureSpec extends Specification {
	private static final Logger LOG = LoggerFactory.getLogger(JsmppSimulatorFailureSpec.class);

	def ITERATIONS = 1000;
	
	def "Port already used should stop the simulator and throw an error"() {
		given:
			ServerSocket socket = new ServerSocket(65000);
			JSMPPServer server = new JSMPPServer(new ServerConfig().port(65000).build())

		when:
			server.start()
			
		then:
			def e = thrown(SmppServerException)
			e.getCause() instanceof ServerStartupException
			e.getCause().getCause() instanceof BindException

		cleanup:
			close(socket)
			server.stop()
	}
	
	@Requires({ sys.get("ide", false) })
	def "Start and stop server should be reliable"() {
		when:
			for (int i=0 ; i<ITERATIONS ; i++) {
				JSMPPServer server = new JSMPPServer(new ServerConfig().port(64000).build())
				server.start()
				def session = null
				if (i % 3 == 0) {
					session = connect(server.getPort(), null, null)
				}
				if (i % 5 == 0) {
					sendSms(session, ""+i);
				}
				if (i % 9 == 0) {
					sendSms(session, ""+i);
				}
				if (i % 11 == 0) {
					close(session)
				}
				if (i % 7 == 0) {
					unbindAndClose(session)
				}
				server.stop()
			}
			
		then:
			notThrown(SmppServerException)
	}
	
	@Requires({ sys.get("ide", false) })
	def "Random interactions with server should be reliable"() {
		when:
			for (int i=0 ; i<ITERATIONS ; i++) {
				JSMPPServer server = new JSMPPServer(new ServerConfig().port(64000).build())
				server.start()
				def session = null
				if (executeRandomly()) {
					session = connect(server.getPort(), null, null)
				}
				for (int j=0 ; j<Math.random()*10 ; j++) {
					sendSms(session, ""+i);
				}
				if (executeRandomly()) {
					close(session)
				}
				if (executeRandomly()) {
					unbindAndClose(session)
				}
				server.stop()
			}
			
		then:
			notThrown(SmppServerException)
	}

	private boolean executeRandomly() {
		return Math.random() > 0.5;
	}
	
	private void close(SMPPSession session) {
		if (session == null) {
			return;
		}
		LOG.debug("Closing session {}...", session.getSessionId());
		session?.close()
		LOG.debug("Session {} closed", session.getSessionId());
	}
	
	private void unbindAndClose(SMPPSession session) {
		if (session == null) {
			return;
		}
		LOG.debug("Unbinding and closing session {}...", session.getSessionId());
		session?.unbindAndClose()
		LOG.debug("Session {} unbound and closed", session.getSessionId());
	}
	
	private void close(ServerSocket socket) {
		try {
			socket?.close()
		} catch(Exception e) {
		}
	}
	
	private SMPPSession connect(int port, String systemId, String password) {
		LOG.debug("Connecting...");
		SMPPSession session = new SMPPSession();
		session.connectAndBind("localhost", port, bindParam(systemId, password));
		LOG.debug("Session {} connected", session.getSessionId());
		return session;
	}
	
	private String sendSms(SMPPSession session, String message) {
		if (session == null) {
			return null;
		}
		LOG.debug("Sending SMS '{}' using session {}...", message, session.getSessionId());
		String id = session.submitShortMessage("CMT",
			TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, "1616",
			TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, "628176504657",
			new ESMClass(), (byte)0, (byte)1,  new AbsoluteTimeFormatter().format(new Date()), null,
			new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), (byte)0,
			new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false), (byte)0,
			message.getBytes());
		LOG.debug("SMS '{}' sent using session {}", message, session.getSessionId());
	}
	
	private BindParameter bindParam(String systemId, String password) {
		return new BindParameter(BindType.BIND_TRX, systemId, password, "systemType", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null)
	}

}
