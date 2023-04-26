package oghamtesting.it.sms.simulator

import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

import org.awaitility.Awaitility
import ogham.testing.org.jsmpp.bean.AlertNotification
import ogham.testing.org.jsmpp.bean.Alphabet
import ogham.testing.org.jsmpp.bean.BindType
import ogham.testing.org.jsmpp.bean.DataSm
import ogham.testing.org.jsmpp.bean.DeliverSm
import ogham.testing.org.jsmpp.bean.ESMClass
import ogham.testing.org.jsmpp.bean.GeneralDataCoding
import ogham.testing.org.jsmpp.bean.MessageClass
import ogham.testing.org.jsmpp.bean.NumberingPlanIndicator
import ogham.testing.org.jsmpp.bean.RegisteredDelivery
import ogham.testing.org.jsmpp.bean.SMSCDeliveryReceipt
import ogham.testing.org.jsmpp.bean.TypeOfNumber
import ogham.testing.org.jsmpp.extra.ProcessRequestException
import ogham.testing.org.jsmpp.extra.ResponseTimeoutException
import ogham.testing.org.jsmpp.session.BindParameter
import ogham.testing.org.jsmpp.session.DataSmResult
import ogham.testing.org.jsmpp.session.MessageReceiverListener
import ogham.testing.org.jsmpp.session.SMPPSession
import ogham.testing.org.jsmpp.session.Session
import ogham.testing.org.jsmpp.util.AbsoluteTimeFormatter

import ogham.testing.com.cloudhopper.commons.charset.CharsetUtil

import fr.sii.ogham.testing.extension.common.LogTestInformation
import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig
import fr.sii.ogham.testing.sms.simulator.jsmpp.JSMPPServer
import spock.lang.Specification
import spock.lang.Unroll

@LogTestInformation
@Unroll
class JsmppSimulatorSpec extends Specification {
	
	def "As developer I test sms sending"() {
		given:
			JSMPPServer server = new JSMPPServer(new ServerConfig().build())
			server.start()
			def session = connect(server.getPort(), null, null)

		when:
			sendSms(session, "test sms")
			
		then:
			with(server.getReceivedMessages()) {
				size() == 1
				
				with(get(0)) {
					getShortMessage() == CharsetUtil.CHARSET_GSM8.encode("test sms")
					getSourceAddr() == "1616"
					getSourceAddrTon() == TypeOfNumber.INTERNATIONAL.value()
					getSourceAddrNpi() == NumberingPlanIndicator.UNKNOWN.value()
					getDestAddress() == "628176504657"
					getDestAddrTon() == TypeOfNumber.INTERNATIONAL.value()
					getDestAddrNpi() == NumberingPlanIndicator.UNKNOWN.value()
					getDataCoding() == new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false).toByte()
				}
			}

		cleanup:
			close(session)
			server.stop()
	}
	
	def "As developer I test successful client authentication"() {
		given:
			JSMPPServer server = new JSMPPServer(new ServerConfig().credentials("foo", "bar").build())
			server.start()
			def session = connect(server.getPort(), "foo", "bar")

		when:
			sendSms(session, "test sms")
			
		then:
			with(server.getReceivedMessages()) {
				size() == 1
				
				with(get(0)) {
					getShortMessage() == CharsetUtil.CHARSET_GSM8.encode("test sms")
					getSourceAddr() == "1616"
					getSourceAddrTon() == TypeOfNumber.INTERNATIONAL.value()
					getSourceAddrNpi() == NumberingPlanIndicator.UNKNOWN.value()
					getDestAddress() == "628176504657"
					getDestAddrTon() == TypeOfNumber.INTERNATIONAL.value()
					getDestAddrNpi() == NumberingPlanIndicator.UNKNOWN.value()
					getDataCoding() == new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false).toByte()
				}
			}
	
		cleanup:
			close(session)
			server.stop()
	}
	
	def "As developer I test unsuccessful client authentication"() {
		given:
			JSMPPServer server = new JSMPPServer(new ServerConfig().credentials("login", "password").build())
			server.start()

		when:
			def session = connect(server.getPort(), "foo", "bar")
			
		then:
			def e = thrown(IOException)
			e.message.toLowerCase().contains("invalid password")
	
		cleanup:
			close(session)
			server.stop()
	}

	def "As developer I test how client handles connection timeout"() {
		given:
			JSMPPServer server = new JSMPPServer(new ServerConfig().slow().sendBindRespDelay(10000L).and().build())
			server.start()
	
		when:
			def session = connect(server.getPort(), null, null, 200L, null)
			
		then:
			def e = thrown(IOException)
			e.message.contains("Time out waiting for bind response: No response after waiting for 200 millis when executing bind")
	
		cleanup:
			close(session)
			server.stop()
	}
	

	def "As developer I test how client handles send sms timeout"() {
		given:
			JSMPPServer server = new JSMPPServer(new ServerConfig().slow().sendSubmitSmRespDelay(10000L).and().build())
			server.start()
			def session = connect(server.getPort(), null, null, null, 200L)
	
		when:
			sendSms(session, "test sms")
		
		then:
			def e = thrown(ResponseTimeoutException)
			e.message.contains("No response after waiting for 200 millis when executing submit_sm")
		
		
		cleanup:
			close(session)
			server.stop()
	}
	
	def "As developer I test sms delivery receipt"() {
		given:
			JSMPPServer server = new JSMPPServer(new ServerConfig().build())
			server.start()
			def session = connect(server.getPort(), null, null, 120000L, 120000L)
			AtomicBoolean receiptReceived = new AtomicBoolean()
			MessageReceiverListener listener = Spy(new MessageReceiverListener() {
				@Override
				public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
					return null;
				}

				@Override
				public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
					receiptReceived.set(true)
				}

				@Override
				public void onAcceptAlertNotification(AlertNotification alertNotification) {
				}
			})
			session.setMessageReceiverListener(listener)

		when:
			sendSmsWithDeliveryReceipt(session, "test sms")
			Awaitility.await().atMost(Duration.ofSeconds(5)).untilTrue(receiptReceived)
			
		then:
			interaction {
				1 * listener.onAcceptDeliverSm(_)
			}
			with(server.getReceivedMessages()) {
				size() == 1
				
				with(get(0)) {
					getShortMessage() == CharsetUtil.CHARSET_GSM8.encode("test sms")
					getSourceAddr() == "1616"
					getSourceAddrTon() == TypeOfNumber.INTERNATIONAL.value()
					getSourceAddrNpi() == NumberingPlanIndicator.UNKNOWN.value()
					getDestAddress() == "628176504657"
					getDestAddrTon() == TypeOfNumber.INTERNATIONAL.value()
					getDestAddrNpi() == NumberingPlanIndicator.UNKNOWN.value()
					getDataCoding() == new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false).toByte()
				}
			}

		cleanup:
			close(session)
			server.stop()
	}
	
	private SMPPSession connect(int port, String systemId, String password) {
		SMPPSession session = new SMPPSession();
		session.connectAndBind("localhost", port, bindParam(systemId, password));
		return session;
	}
	
	private SMPPSession connect(int port, String systemId, String password, Long bindTimeout, Long transationTimeout) {
		SMPPSession session = new SMPPSession();
		if (bindTimeout != null) {
			session.connectAndBind("localhost", port, bindParam(systemId, password), bindTimeout);
		} else {
			session.connectAndBind("localhost", port, bindParam(systemId, password));
		}
		if (transationTimeout != null) {
			session.setTransactionTimer(transationTimeout);
		}
		return session;
	}

	private BindParameter bindParam(String systemId, String password) {
		return new BindParameter(BindType.BIND_TRX, systemId, password, "systemType", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null)
	}
	
	private String sendSms(SMPPSession session, String message) {
		return session.submitShortMessage("CMT",
			TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, "1616",
			TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, "628176504657",
			new ESMClass(), (byte)0, (byte)1,  new AbsoluteTimeFormatter().format(new Date()), null,
			new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), (byte)0,
			new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false), (byte)0,
			message.getBytes());
	}

	private String sendSmsWithDeliveryReceipt(SMPPSession session, String message) {
		return session.submitShortMessage("CMT",
			TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, "1616",
			TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, "628176504657",
			new ESMClass(), (byte)0, (byte)1,  new AbsoluteTimeFormatter().format(new Date()), null,
			new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE), (byte)0,
			new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false), (byte)0,
			message.getBytes());
	}

	private close(SMPPSession session) {
		session?.unbindAndClose()
	}
}
